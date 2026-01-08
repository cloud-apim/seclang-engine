package com.cloud.apim.seclang.test

import com.cloud.apim.seclang.model.Disposition._
import com.cloud.apim.seclang.model._
import com.cloud.apim.seclang.scaladsl.SecLang

class SecLangFactoryTest extends munit.FunSuite {

  test("simple factory test") {
    val presets = Map(
      "no_firefox" -> SecLangPreset.withNoFiles("no_firefox", """
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
      "health_check" -> SecLangPreset.withNoFiles("health_check", """
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
      headers = Map("User-Agent" -> List("Firefox/128.0")),
      query = Map("q" -> List("test")),
      body = None
    )
    val passing_ctx_1 = RequestContext(
      method = "GET",
      uri = "/health",
      headers = Map("User-Agent" -> List("curl/8.0")),
      query = Map("q" -> List("test")),
      body = None
    )
    val passing_ctx_2 = RequestContext(
      method = "GET",
      uri = "/admin",
      headers = Map("User-Agent" -> List("chrome/8.0")),
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
}
