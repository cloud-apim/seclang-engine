package com.cloud.apim.seclang.test

import com.cloud.apim.seclang.javadsl._

import java.util.{Arrays, HashMap}

class SecLangJavaApiTest extends munit.FunSuite {

  test("simple rules via Java API") {

    val rules =
      """
        |SecRule REQUEST_HEADERS:User-Agent "@pm firefox" \
        |    "id:00001,\
        |    phase:1,\
        |    block,\
        |    t:none,t:lowercase,\
        |    msg:'someone used firefox to access',\
        |    logdata:'someone used firefox to access',\
        |    tag:'test',\
        |    ver:'0.0.0-dev',\
        |    status:403,\
        |    severity:'CRITICAL'"
        |
        |SecRule REQUEST_URI "@contains /health" \
        |    "id:00002,\
        |    phase:1,\
        |    pass,\
        |    t:none,t:lowercase,\
        |    msg:'someone called /health',\
        |    logdata:'someone called /health',\
        |    tag:'test',\
        |    ver:'0.0.0-dev'"
        |
        |SecRuleEngine On
        |""".stripMargin

    val parseResult = SecLang.parse(rules)
    assert(parseResult.isSuccess, "rules should be parsed successfully")

    val program = SecLang.compile(parseResult.getConfiguration)
    val engine = SecLang.engine(program)

    val failingCtx2 = JRequestContext.builder()
      .method("GET")
      .uri("/")
      .header("User-Agent", "Firefox/128.0")
      .queryParam("q", "test")
      .build()

    val passingCtx1 = JRequestContext.builder()
      .method("GET")
      .uri("/health")
      .header("User-Agent", "curl/8.0")
      .queryParam("q", "test")
      .build()

    val passingCtx2 = JRequestContext.builder()
      .method("GET")
      .uri("/admin")
      .header("User-Agent", "chrome/8.0")
      .queryParam("q", "test")
      .build()

    val failingRes2 = engine.evaluate(failingCtx2, Arrays.asList(1, 2))
    val passingRes1 = engine.evaluate(passingCtx1, Arrays.asList(1, 2))
    val passingRes2 = engine.evaluate(passingCtx2, Arrays.asList(1, 2))

    failingRes2.displayPrintln()
    passingRes1.displayPrintln()
    passingRes2.displayPrintln()

    assert(failingRes2.isBlocked, "failingRes2 should be blocked")
    assertEquals(failingRes2.getDisposition.getStatus, 403)
    assertEquals(failingRes2.getDisposition.getMessage.orElse(""), "someone used firefox to access")
    assertEquals(failingRes2.getDisposition.getRuleId.orElse(0), Integer.valueOf(1))

    assert(passingRes1.isContinue, "passingRes1 should continue")
    assert(passingRes2.isContinue, "passingRes2 should continue")
  }

  test("chain of rules via Java API") {
    val rules =
      """
        |SecRule REQUEST_HEADERS:User-Agent "@pm firefox" \
        |    "id:00001,\
        |    phase:1,\
        |    block,\
        |    t:none,t:lowercase,\
        |    msg:'someone used firefox to access',\
        |    logdata:'someone used firefox to access',\
        |    tag:'test',\
        |    ver:'0.0.0-dev',\
        |    severity:'CRITICAL'"
        |
        |SecRule REQUEST_URI "@contains /health" \
        |    "id:00002,\
        |    phase:1,\
        |    pass,\
        |    t:none,t:lowercase,\
        |    msg:'someone called /health',\
        |    logdata:'someone called /health',\
        |    tag:'test',\
        |    ver:'0.0.0-dev'"
        |
        |SecRule REQUEST_URI "@rx ^/admin" \
        |    "id:00003,\
        |    phase:1,\
        |    pass,\
        |    t:none,t:lowercase,\
        |    msg:'request on /admin',\
        |    nolog,\
        |    tag:'test',\
        |    ver:'0.0.0-dev',\
        |    chain"
        |    SecRule REQUEST_HEADERS:User-Agent "@rx curl" \
        |      "block,\
        |      t:none,t:lowercase,\
        |      msg:'someone used curl to access',\
        |      logdata:'someone used curl to access',\
        |      severity:'CRITICAL'"
        |
        |SecRuleEngine On
        |""".stripMargin

    val parseResult = SecLang.parse(rules)
    assert(parseResult.isSuccess, "rules should be parsed successfully")

    val program = SecLang.compile(parseResult.getConfiguration)
    val engine = SecLang.engine(program)

    val failingCtx1 = JRequestContext.builder()
      .method("GET")
      .uri("/admin")
      .header("User-Agent", "curl/8.0")
      .queryParam("q", "test")
      .build()

    val failingCtx2 = JRequestContext.builder()
      .method("GET")
      .uri("/")
      .header("User-Agent", "Firefox/128.0")
      .queryParam("q", "test")
      .build()

    val passingCtx1 = JRequestContext.builder()
      .method("GET")
      .uri("/health")
      .header("User-Agent", "curl/8.0")
      .queryParam("q", "test")
      .build()

    val passingCtx2 = JRequestContext.builder()
      .method("GET")
      .uri("/admin")
      .header("User-Agent", "Chrome/8.0")
      .queryParam("q", "test")
      .build()

    val failingRes1 = engine.evaluate(failingCtx1, Arrays.asList(1, 2))
    val failingRes2 = engine.evaluate(failingCtx2, Arrays.asList(1, 2))
    val passingRes1 = engine.evaluate(passingCtx1, Arrays.asList(1, 2))
    val passingRes2 = engine.evaluate(passingCtx2, Arrays.asList(1, 2))

    failingRes1.displayPrintln()
    failingRes2.displayPrintln()
    passingRes1.displayPrintln()
    passingRes2.displayPrintln()

    // failingRes1: blocked by chain rule (curl on /admin)
    assert(failingRes1.isBlocked, "failingRes1 should be blocked")
    assertEquals(failingRes1.getDisposition.getStatus, 400)
    assertEquals(failingRes1.getDisposition.getMessage.orElse(""), "someone used curl to access")
    assertEquals(failingRes1.getDisposition.getRuleId.orElse(0), Integer.valueOf(3))

    // failingRes2: blocked by firefox rule
    assert(failingRes2.isBlocked, "failingRes2 should be blocked")
    assertEquals(failingRes2.getDisposition.getStatus, 400)
    assertEquals(failingRes2.getDisposition.getMessage.orElse(""), "someone used firefox to access")
    assertEquals(failingRes2.getDisposition.getRuleId.orElse(0), Integer.valueOf(1))

    // passingRes1: curl but not on /admin, so chain doesn't match
    assert(passingRes1.isContinue, "passingRes1 should continue")

    // passingRes2: on /admin but not curl, so chain doesn't match
    assert(passingRes2.isContinue, "passingRes2 should continue")
  }

  test("parse error handling via Java API") {
    val invalidRules = "SecRule INVALID_SYNTAX"

    val parseResult = SecLang.parse(invalidRules)
    assert(parseResult.isError, "invalid rules should produce parse error")
    assert(parseResult.getError != null, "error message should not be null")
  }

  test("engine with custom config via Java API") {
    val rules =
      """
        |SecRule REQUEST_URI "@contains /test" \
        |    "id:12345,\
        |    phase:1,\
        |    block,\
        |    msg:'test rule'"
        |
        |SecRuleEngine On
        |""".stripMargin

    val parseResult = SecLang.parse(rules)
    assert(parseResult.isSuccess)

    val program = SecLang.compile(parseResult.getConfiguration)

    // Create engine with debug config
    val config = JSecLangEngineConfig.builder()
      .debugRule(12345)
      .build()

    val engine = SecLang.engine(
      program,
      config,
      new java.util.HashMap[String, String](),
      JSecLangIntegration.defaultIntegration()
    )

    val ctx = JRequestContext.builder()
      .method("GET")
      .uri("/test")
      .build()

    val result = engine.evaluate(ctx)
    result.displayPrintln()

    assert(result.isBlocked, "request should be blocked")
    assertEquals(result.getDisposition.getMessage.orElse(""), "test rule")
  }

  test("geoLookup via Java API") {
    val rules =
      """
        |SecRule REMOTE_ADDR "@geoLookup" \
        |    "id:12346,\
        |    phase:1,\
        |    deny,\
        |    status:403,\
        |    msg:'blocked from %{GEO.COUNTRY_CODE}',\
        |    chain"
        |    SecRule GEO:COUNTRY_CODE "@streq KP" "t:none"
        |
        |SecRuleEngine On
        |""".stripMargin

    val defaults = JSecLangIntegration.noLogIntegration()
    val integration = new JSecLangIntegration {
      override def logDebug(msg: String): Unit = ()
      override def logInfo(msg: String): Unit = ()
      override def logAudit(msg: String): Unit = ()
      override def logError(msg: String): Unit = ()
      override def getEnv: java.util.Map[String, String] = new java.util.HashMap[String, String]()
      override def getExternalPreset(name: String) = defaults.getExternalPreset(name)
      override def getCachedProgram(key: String) = defaults.getCachedProgram(key)
      override def putCachedProgram(key: String, program: com.cloud.apim.seclang.model.CompiledProgram, ttl: java.time.Duration): Unit = defaults.putCachedProgram(key, program, ttl)
      override def removeCachedProgram(key: String): Unit = defaults.removeCachedProgram(key)
      override def audit(ruleId: Int, context: JRequestContext, state: com.cloud.apim.seclang.model.RuntimeState, phase: Int, msg: String, logdata: java.util.List[String]): Unit = ()
      override def geoLookup(address: String): java.util.Optional[java.util.Map[String, String]] =
        if (address == "1.2.3.4") java.util.Optional.of(java.util.Map.of("COUNTRY_CODE", "KP")) else java.util.Optional.empty()
    }

    val parseResult = SecLang.parse(rules)
    assert(parseResult.isSuccess)
    val engine = SecLang.engine(SecLang.compile(parseResult.getConfiguration), JSecLangEngineConfig.defaultConfig(), new java.util.HashMap[String, String](), integration)

    val located = engine.evaluate(JRequestContext.builder().method("GET").uri("/").remoteAddr("1.2.3.4").build())
    val unknown = engine.evaluate(JRequestContext.builder().method("GET").uri("/").remoteAddr("5.6.7.8").build())

    assert(located.isBlocked, "an address located in KP should be blocked")
    assertEquals(located.getDisposition.getMessage.orElse(""), "blocked from KP")
    assert(unknown.isContinue, "an address the host cannot locate should continue")
  }

  test("factory with presets via Java API") {
    // Create presets
    val presets = new HashMap[String, JSecLangPreset]()
    presets.put("no_firefox", JSecLangPreset.withNoFiles("no_firefox",
      """
        |SecRule REQUEST_HEADERS:User-Agent "@pm firefox" \
        |    "id:00001,\
        |    phase:1,\
        |    block,\
        |    t:none,t:lowercase,\
        |    msg:'someone used firefox to access',\
        |    logdata:'someone used firefox to access',\
        |    tag:'test',\
        |    ver:'0.0.0-dev',\
        |    status:403,\
        |    severity:'CRITICAL'"
        |""".stripMargin))

    presets.put("health_check", JSecLangPreset.withNoFiles("health_check",
      """
        |SecRule REQUEST_URI "@contains /health" \
        |    "id:00002,\
        |    phase:1,\
        |    pass,\
        |    t:none,t:lowercase,\
        |    msg:'someone called /health',\
        |    logdata:'someone called /health',\
        |    tag:'test',\
        |    ver:'0.0.0-dev'"
        |""".stripMargin))

    // Create factory
    val factory = SecLang.factory(presets)

    // Define rule configuration
    val rulesConfig = Arrays.asList(
      "@import_preset no_firefox",
      "@import_preset health_check",
      "SecRuleEngine On"
    )

    // Test contexts
    val failingCtx = JRequestContext.builder()
      .method("GET")
      .uri("/")
      .header("User-Agent", "Firefox/128.0")
      .queryParam("q", "test")
      .build()

    val passingCtx1 = JRequestContext.builder()
      .method("GET")
      .uri("/health")
      .header("User-Agent", "curl/8.0")
      .queryParam("q", "test")
      .build()

    val passingCtx2 = JRequestContext.builder()
      .method("GET")
      .uri("/admin")
      .header("User-Agent", "chrome/8.0")
      .queryParam("q", "test")
      .build()

    // Evaluate
    val failingRes = factory.evaluate(rulesConfig, failingCtx, Arrays.asList(1, 2))
    val passingRes1 = factory.evaluate(rulesConfig, passingCtx1, Arrays.asList(1, 2))
    val passingRes2 = factory.evaluate(rulesConfig, passingCtx2, Arrays.asList(1, 2))

    failingRes.displayPrintln()
    passingRes1.displayPrintln()
    passingRes2.displayPrintln()

    // Assertions
    assert(failingRes.isBlocked, "failingRes should be blocked")
    assertEquals(failingRes.getDisposition.getStatus, 403)
    assertEquals(failingRes.getDisposition.getMessage.orElse(""), "someone used firefox to access")
    assertEquals(failingRes.getDisposition.getRuleId.orElse(0), Integer.valueOf(1))

    assert(passingRes1.isContinue, "passingRes1 should continue")
    assert(passingRes2.isContinue, "passingRes2 should continue")
  }
}
