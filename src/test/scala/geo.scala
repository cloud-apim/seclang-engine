package com.cloud.apim.seclang.test

import com.cloud.apim.seclang.model.Disposition._
import com.cloud.apim.seclang.model._
import com.cloud.apim.seclang.scaladsl.SecLang

class SecLangGeoLookupTest extends munit.FunSuite {

  private val locations = Map(
    "1.2.3.4" -> Map("COUNTRY_CODE" -> "KP", "COUNTRY_NAME" -> "North Korea", "COUNTRY_CONTINENT" -> "AS", "CITY" -> "Pyongyang"),
    "5.6.7.8" -> Map("COUNTRY_CODE" -> "FR", "COUNTRY_NAME" -> "France", "COUNTRY_CONTINENT" -> "EU", "CITY" -> "Paris")
  )

  private class GeoIntegration(lookup: String => Option[Map[String, String]]) extends NoLogSecLangIntegration() {
    @volatile var calls: List[String] = Nil
    override def geoLookup(address: String): Option[Map[String, String]] = {
      calls = calls :+ address
      lookup(address)
    }
  }

  private def engine(rules: String, integration: SecLangIntegration) = {
    val loaded = SecLang.parse(rules).fold(err => throw err.throwable, identity)
    SecLang.engine(SecLang.compile(loaded), integration = integration)
  }

  private def from(ip: String) = RequestContext(method = "GET", uri = "/", headers = Headers(Map("Host" -> List("www.example.com"))), remoteAddr = ip)

  // the shape of CRS 3's rule 910100, the reason @geoLookup exists
  private val countryBlocking =
    """
      |SecAction "id:1,phase:1,pass,nolog,setvar:'tx.high_risk_country_codes=KP IR'"
      |
      |SecRule REMOTE_ADDR "@geoLookup" \
      |    "id:2,\
      |    phase:1,\
      |    deny,\
      |    status:403,\
      |    msg:'client from a high risk country: %{GEO.COUNTRY_CODE}',\
      |    chain"
      |    SecRule GEO:COUNTRY_CODE "@within %{tx.high_risk_country_codes}" "t:none"
      |
      |SecRuleEngine On
      |""".stripMargin

  test("@geoLookup fills GEO for the rest of the chain") {
    val integration = new GeoIntegration(locations.get)
    val waf         = engine(countryBlocking, integration)

    assertEquals(waf.evaluate(from("1.2.3.4")).disposition, Block(403, Some("client from a high risk country: KP"), Some(2)))
    assertEquals(waf.evaluate(from("5.6.7.8")).disposition, Continue)
    assertEquals(integration.calls, List("1.2.3.4", "5.6.7.8"))
  }

  test("an address the host cannot locate is not a match") {
    val waf = engine(countryBlocking, new GeoIntegration(_ => None))
    assertEquals(waf.evaluate(from("1.2.3.4")).disposition, Continue)
  }

  test("a host that throws is not a match, and does not break evaluation") {
    val waf = engine(countryBlocking, new GeoIntegration(_ => throw new IllegalStateException("database not loaded")))
    assertEquals(waf.evaluate(from("1.2.3.4")).disposition, Continue)
  }

  test("without a host database @geoLookup never matches, so its negation always does") {
    val rules =
      """
        |SecRule REMOTE_ADDR "!@geoLookup" "id:3,phase:1,deny,status:403,msg:'unlocated'"
        |SecRuleEngine On
        |""".stripMargin
    assertEquals(engine(rules, new NoLogSecLangIntegration()).evaluate(from("1.2.3.4")).disposition, Block(403, Some("unlocated"), Some(3)))
  }

  test("GEO members are read case-insensitively, by name, by regex and as a whole") {
    def blocks(target: String, operator: String): Boolean = {
      val rules =
        s"""
          |SecRule REMOTE_ADDR "@geoLookup" "id:4,phase:1,pass,nolog"
          |SecRule $target "$operator" "id:5,phase:1,deny,status:403,msg:'matched'"
          |SecRuleEngine On
          |""".stripMargin
      engine(rules, new GeoIntegration(locations.get)).evaluate(from("5.6.7.8")).disposition.isInstanceOf[Block]
    }
    assert(blocks("GEO:country_code", "@streq FR"))
    assert(blocks("GEO:COUNTRY_CODE", "@streq FR"))
    assert(blocks("GEO:/^country_c/", "@streq FR"))
    assert(blocks("GEO", "@streq Paris"))
    assert(blocks("&GEO", "@eq 4"))
    assert(!blocks("GEO:COUNTRY_CODE", "@streq KP"))
  }

  test("GEO is empty until @geoLookup runs") {
    val rules =
      """
        |SecRule &GEO "@eq 0" "id:6,phase:1,deny,status:403,msg:'no geo yet'"
        |SecRuleEngine On
        |""".stripMargin
    assertEquals(engine(rules, new GeoIntegration(locations.get)).evaluate(from("5.6.7.8")).disposition, Block(403, Some("no geo yet"), Some(6)))
  }

  test("SecGeoLookupDb is accepted and left to the host") {
    val rules =
      """
        |SecGeoLookupDb /usr/share/GeoIP/GeoLite2-Country.mmdb
        |SecRule REMOTE_ADDR "@geoLookup" "id:7,phase:1,deny,status:403,msg:'located in %{geo.country_name}'"
        |SecRuleEngine On
        |""".stripMargin
    val loaded = SecLang.parse(rules).fold(err => throw err.throwable, identity)
    assert(loaded.statements.exists {
      case EngineConfigDirective(_, ConfigDirective.GeoLookupDb(path)) => path == "/usr/share/GeoIP/GeoLite2-Country.mmdb"
      case _                                                          => false
    })
    assertEquals(engine(rules, new GeoIntegration(locations.get)).evaluate(from("5.6.7.8")).disposition, Block(403, Some("located in France"), Some(7)))
  }
}
