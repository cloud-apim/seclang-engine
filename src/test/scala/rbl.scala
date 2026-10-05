package com.cloud.apim.seclang.test

import com.cloud.apim.seclang.model.Disposition._
import com.cloud.apim.seclang.model._
import com.cloud.apim.seclang.scaladsl.SecLang

class SecLangRblTest extends munit.FunSuite {

  private val listed = Set(("1.2.3.4", "zen.spamhaus.org"), ("5.6.7.8", "bl.example.org"))

  private class RblIntegration(lookup: (String, String) => Boolean) extends NoLogSecLangIntegration() {
    @volatile var calls: List[(String, String)] = Nil
    override def rblLookup(address: String, zone: String): Boolean = {
      calls = calls :+ (address -> zone)
      lookup(address, zone)
    }
  }

  private def engine(rules: String, integration: SecLangIntegration) = {
    val loaded = SecLang.parse(rules).fold(err => throw err.throwable, identity)
    SecLang.engine(SecLang.compile(loaded), integration = integration)
  }

  private def from(ip: String) = RequestContext(method = "GET", uri = "/", headers = Headers(Map("Host" -> List("www.example.com"))), remoteAddr = ip)

  private val spamhaus =
    """
      |SecRule REMOTE_ADDR "@rbl zen.spamhaus.org" "id:1,phase:1,deny,status:403,msg:'listed by spamhaus'"
      |SecRuleEngine On
      |""".stripMargin

  test("an address the host says is listed matches, with the zone the rule names") {
    val integration = new RblIntegration((ip, zone) => listed.contains((ip, zone)))
    val waf         = engine(spamhaus, integration)
    assertEquals(waf.evaluate(from("1.2.3.4")).disposition, Block(403, Some("listed by spamhaus"), Some(1)))
    assertEquals(waf.evaluate(from("5.6.7.8")).disposition, Continue)
    assertEquals(integration.calls, List("1.2.3.4" -> "zen.spamhaus.org", "5.6.7.8" -> "zen.spamhaus.org"))
  }

  test("a host that throws is not a listing") {
    val waf = engine(spamhaus, new RblIntegration((_, _) => throw new IllegalStateException("resolver down")))
    assertEquals(waf.evaluate(from("1.2.3.4")).disposition, Continue)
  }

  test("without a host resolver @rbl never matches, so its negation always does") {
    val rules =
      """
        |SecRule REMOTE_ADDR "!@rbl zen.spamhaus.org" "id:2,phase:1,deny,status:403,msg:'unlisted'"
        |SecRuleEngine On
        |""".stripMargin
    assertEquals(engine(rules, new NoLogSecLangIntegration()).evaluate(from("1.2.3.4")).disposition, Block(403, Some("unlisted"), Some(2)))
  }

  test("the zone goes through macro expansion") {
    val rules =
      """
        |SecAction "id:3,phase:1,pass,nolog,setvar:tx.rbl_zone=bl.example.org"
        |SecRule REMOTE_ADDR "@rbl %{tx.rbl_zone}" "id:4,phase:1,deny,status:403,msg:'listed'"
        |SecRuleEngine On
        |""".stripMargin
    val integration = new RblIntegration((ip, zone) => listed.contains((ip, zone)))
    assertEquals(engine(rules, integration).evaluate(from("5.6.7.8")).disposition, Block(403, Some("listed"), Some(4)))
    assertEquals(integration.calls, List("5.6.7.8" -> "bl.example.org"))
  }

  test("capture puts the address in TX:0, as libmodsecurity does") {
    val rules =
      """
        |SecRule REMOTE_ADDR "@rbl zen.spamhaus.org" "id:5,phase:1,deny,status:403,capture,msg:'listed: %{tx.0}'"
        |SecRuleEngine On
        |""".stripMargin
    val integration = new RblIntegration((ip, zone) => listed.contains((ip, zone)))
    assertEquals(engine(rules, integration).evaluate(from("1.2.3.4")).disposition, Block(403, Some("listed: 1.2.3.4"), Some(5)))
  }

  test("SecHttpBlKey is accepted and left to the host") {
    val rules =
      """
        |SecHttpBlKey abcdefghijkl
        |SecRule REMOTE_ADDR "@rbl dnsbl.httpbl.org" "id:6,phase:1,deny,status:403,msg:'httpbl'"
        |SecRuleEngine On
        |""".stripMargin
    val loaded = SecLang.parse(rules).fold(err => throw err.throwable, identity)
    assert(loaded.statements.exists {
      case EngineConfigDirective(_, ConfigDirective.HttpBlKey(key)) => key == "abcdefghijkl"
      case _                                                       => false
    })
    val integration = new RblIntegration((_, zone) => zone == "dnsbl.httpbl.org")
    assertEquals(engine(rules, integration).evaluate(from("9.9.9.9")).disposition, Block(403, Some("httpbl"), Some(6)))
  }
}
