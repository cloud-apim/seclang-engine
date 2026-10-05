package com.cloud.apim.seclang.test

import com.cloud.apim.seclang.model.Disposition._
import com.cloud.apim.seclang.model._
import com.cloud.apim.seclang.scaladsl.SecLang

class SecLangBodyProcessorTest extends munit.FunSuite {

  private class RecordingIntegration extends NoLogSecLangIntegration() {
    @volatile var debug: List[String] = Nil
    override def logDebug(msg: String): Unit = debug = debug :+ msg
  }

  private def engine(rules: String, integration: SecLangIntegration = new NoLogSecLangIntegration()) = {
    val loaded = SecLang.parse(rules).fold(err => throw err.throwable, identity)
    SecLang.engine(SecLang.compile(loaded), integration = integration)
  }

  private def post(contentType: String, body: String) = RequestContext(
    method = "POST",
    uri = "/api",
    headers = Headers(Map("Host" -> List("www.example.com"), "Content-Type" -> List(contentType))),
    body = Some(ByteString(body))
  )

  private val argsRule =
    """
      |SecRule ARGS:q "@contains attack" "id:1,phase:2,deny,status:403,msg:'attack in args'"
      |SecRuleEngine On
      |""".stripMargin

  private val blocked = Block(403, Some("attack in args"), Some(1))

  test("a +json body is split into ARGS, like application/json") {
    val waf = engine(argsRule)
    Seq(
      "application/json",
      "application/json; charset=utf-8",
      "application/vnd.api+json",
      "application/problem+json",
      "application/merge-patch+json",
      "application/vc+ld+json",
      "APPLICATION/VND.API+JSON; charset=utf-8",
      "application/x-amz-json-1.0",
      "application/x-amz-json-1.1"
    ).foreach { ct =>
      assertEquals(waf.evaluate(post(ct, """{"q":"attack"}""")).disposition, blocked, ct)
    }
  }

  test("a type that only looks like JSON is not read as JSON") {
    val waf = engine(argsRule)
    val read = Seq("text/plain", "application/csp-report", "application/x-amz-json-2.0", "application/xml+json-ish", "text/x-json").filter { ct =>
      waf.evaluate(post(ct, """{"q":"attack"}""")).disposition != Continue
    }
    assertEquals(read, Seq.empty)
  }

  test("REQBODY_PROCESSOR names the processor of a +json body") {
    val waf = engine(
      """
        |SecRule REQBODY_PROCESSOR "@streq JSON" "id:2,phase:1,deny,status:403,msg:'json'"
        |SecRuleEngine On
        |""".stripMargin
    )
    assertEquals(waf.evaluate(post("application/vnd.api+json", "{}")).disposition, Block(403, Some("json"), Some(2)))
  }

  test("ctl:requestBodyProcessor=JSON makes the rules after it read the body as JSON") {
    val forcing =
      """
        |SecRule REQUEST_HEADERS:Content-Type "@streq application/x-custom" "id:10,phase:1,pass,nolog,ctl:requestBodyProcessor=JSON"
        |SecRule ARGS:q "@contains attack" "id:1,phase:2,deny,status:403,msg:'attack in args'"
        |SecRule REQBODY_PROCESSOR "@streq JSON" "id:3,phase:2,pass,log,msg:'forced'"
        |SecRuleEngine On
        |""".stripMargin
    assertEquals(engine(argsRule).evaluate(post("application/x-custom", """{"q":"attack"}""")).disposition, Continue)
    assertEquals(engine(forcing).evaluate(post("application/x-custom", """{"q":"attack"}""")).disposition, blocked)
  }

  test("ctl:requestBodyProcessor=URLENCODED reads a body of any type as a form") {
    val forcing =
      """
        |SecAction "id:10,phase:1,pass,nolog,ctl:requestBodyProcessor=URLENCODED"
        |SecRule ARGS:q "@contains attack" "id:1,phase:2,deny,status:403,msg:'attack in args'"
        |SecRuleEngine On
        |""".stripMargin
    assertEquals(engine(argsRule).evaluate(post("text/plain", "q=attack")).disposition, Continue)
    assertEquals(engine(forcing).evaluate(post("text/plain", "q=attack")).disposition, blocked)
  }

  test("ctl:auditEngine is ignored and said at debug level, on the request path") {
    val integration = new RecordingIntegration()
    val waf = engine(
      """
        |SecAction "id:10,phase:1,pass,nolog,ctl:auditEngine=Off"
        |SecRuleEngine On
        |""".stripMargin,
      integration
    )
    assertEquals(waf.evaluate(post("application/json", "{}")).disposition, Continue)
    assert(integration.debug.exists(_.contains("ctl:auditEngine=Off")), integration.debug.toString)
  }

  test("engineSafe reports a rule that does not parse instead of throwing") {
    val factory = SecLang.factory(Map.empty, integration = new NoLogSecLangIntegration())
    factory.engineSafe(List("SecRuleEngine On", """SecRule ARGS "@contains x" "id:10,phase:2,deny""")) match {
      case Left(errors) =>
        assertEquals(errors.size, 1)
        assert(errors.head.msg.startsWith("Parse error"), errors.head.msg)
      case Right(_) => fail("a rule that does not parse must not build an engine")
    }
  }

  test("engine throws the parse error itself, not a bare None.get") {
    val factory = SecLang.factory(Map.empty, integration = new NoLogSecLangIntegration())
    val err     = intercept[RuntimeException](factory.engine(List("""SecRule ARGS "@contains x" "id:10,phase:2,deny""")))
    assert(err.getMessage.startsWith("Parse error"), err.getMessage)
  }

  test("what the parser and the compiler ignore goes to the host's debug log, not to stdout") {
    val integration = new RecordingIntegration()
    val factory     = SecLang.factory(Map.empty, integration = integration)
    factory.engine(List("SecRuleEngine On\nSecUnicodeMapFile unicode.mapping 20127"))
    assert(integration.debug.exists(_.contains("ignored")), integration.debug.toString)
  }
}
