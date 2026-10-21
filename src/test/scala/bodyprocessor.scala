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

  private def multipart(parts: String*): RequestContext =
    post("multipart/form-data; boundary=XyZ", parts.map(p => s"--XyZ\r\n$p\r\n").mkString + "--XyZ--\r\n")

  // until 2.5.2 a part was only named when its Content-Disposition line ended right after
  // `name="…"`: what PHP and every other backend read as the field `q` was invisible to ARGS
  test("a multipart field is named however its Content-Disposition is written") {
    val waf = engine(argsRule)
    Seq(
      "Content-Disposition: form-data; name=\"q\"",
      "Content-Disposition: form-data; name=\"q\"; x=y",
      "Content-Disposition: form-data; name=\"q\" ",
      "Content-Disposition: form-data; name=q",
      "Content-Disposition: form-data; name='q'",
      "content-disposition:form-data;name=\"q\"",
      "  Content-Disposition : form-data; NAME=\"q\"",
      "Content-Disposition: name=\"q\""
    ).foreach { d =>
      assertEquals(waf.evaluate(multipart(s"$d\r\n\r\nattack")).disposition, blocked, d)
    }
  }

  test("a file part is named, and its content stays out of ARGS") {
    val waf = engine(
      """
        |SecRule MULTIPART_PART_HEADERS:avatar "@rx (?i)^content-type\s*:\s*image/png" "id:4,phase:2,deny,status:403,msg:'avatar headers'"
        |SecRuleEngine On
        |""".stripMargin
    )
    val avatar = "Content-Disposition: form-data; name=\"avatar\"; filename=\"me.png\"\r\nContent-Type: image/png\r\n\r\nattack"
    assertEquals(waf.evaluate(multipart(avatar)).disposition, Block(403, Some("avatar headers"), Some(4)))
    assertEquals(engine(argsRule.replace("ARGS:q", "ARGS")).evaluate(multipart(avatar)).disposition, Continue)
  }

  test("FILES and FILES_NAMES read the same parameters as the part names") {
    val waf = engine(
      """
        |SecRule FILES "@streq me.php" "id:5,phase:2,deny,status:403,msg:'file'"
        |SecRule FILES_NAMES "@streq avatar" "id:6,phase:2,deny,status:403,msg:'file name'"
        |SecRuleEngine On
        |""".stripMargin
    )
    Seq("filename=\"me.php\"", "filename='me.php'", "filename=me.php", "FILENAME=\"me.php\"; x=y").foreach { f =>
      assertEquals(waf.evaluate(multipart(s"Content-Disposition: form-data; $f\r\n\r\nz")).disposition, Block(403, Some("file"), Some(5)), f)
    }
    assertEquals(
      waf.evaluate(multipart("Content-Disposition: form-data; filename=\"\"; name=avatar\r\n\r\nz")).disposition,
      Block(403, Some("file name"), Some(6))
    )
  }

  test("whitespace after a delimiter is transport padding, not a reason to lose the parts") {
    val waf  = engine(argsRule)
    val body = "--XyZ  \r\nContent-Disposition: form-data; name=\"q\"\r\n\r\nattack\r\n--XyZ-- \t\r\n"
    assertEquals(waf.evaluate(post("multipart/form-data; boundary=XyZ", body)).disposition, blocked)
    // a line that only starts like the boundary is content
    val inside = "--XyZ\r\nContent-Disposition: form-data; name=\"q\"\r\n\r\n--XyZzz\r\nattack\r\n--XyZ--\r\n"
    assertEquals(waf.evaluate(post("multipart/form-data; boundary=XyZ", inside)).disposition, blocked)
  }

  test("Content-Disposition parameters: quotes, escapes, separators inside quotes, last one wins") {
    import com.cloud.apim.seclang.impl.utils.MultipartVars.dispositionParams
    assertEquals(dispositionParams("form-data; name=\"a;b\"; filename='it\"s.png'"), Map("name" -> "a;b", "filename" -> "it\"s.png"))
    assertEquals(dispositionParams("form-data; name=\"say \\\"hi\\\"\""), Map("name" -> "say \"hi\""))
    assertEquals(dispositionParams("form-data; name=a; name=b"), Map("name" -> "b"))
    assertEquals(dispositionParams("form-data; name = spaced ; flag; x="), Map("name" -> "spaced", "x" -> ""))
    assertEquals(dispositionParams("form-data"), Map.empty[String, String])
  }
}
