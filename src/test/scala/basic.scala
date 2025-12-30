package com.cloud.apim.seclang.test

import com.cloud.apim.seclang.model.Disposition._
import com.cloud.apim.seclang.model._
import com.cloud.apim.seclang.model.Configuration
import com.cloud.apim.seclang.scaladsl.SecLang
import play.api.libs.json.Json

import java.io.File
import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.nio.file.Files

class SecLangBasicTest extends munit.FunSuite {

  test("simple rule") {

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
      |    msg:'someone called health',\
      |    logdata:'someone called health',\
      |    tag:'test',\
      |    ver:'0.0.0-dev'"
      |
      |SecRule REQUEST_URI \
      |    "@rx ^/admin" \
      |    "id:00003,\
      |    phase:1,\
      |    pass,\
      |    t:none,t:lowercase,\
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
      |""".stripMargin

    val loaded = SecLang.parse(rules).fold(err => sys.error(err), identity)
    val program = SecLang.compile(loaded)
    val engine = SecLang.engine(program)

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
      headers = Map("User-Agent" -> List("chrome/8.0")),
      query = Map("q" -> List("test")),
      body = None
    )

    println("rung failing test")
    val failing_res_1 = engine.evaluate(failing_ctx_1, phases = List(1, 2))
    println(failing_res_1.disposition)
    failing_res_1.events.foreach(e => println(s"match phase=${e.phase} id=${e.ruleId} msg=${e.msg.getOrElse("")}"))
    val failing_res_2 = engine.evaluate(failing_ctx_2, phases = List(1, 2))
    println(failing_res_2.disposition)
    failing_res_2.events.foreach(e => println(s"match phase=${e.phase} id=${e.ruleId} msg=${e.msg.getOrElse("")}"))

    println("run passing test")
    val passing_res_1 = engine.evaluate(passing_ctx_1, phases = List(1, 2))
    println(passing_res_1.disposition)
    passing_res_1.events.foreach(e => println(s"match phase=${e.phase} id=${e.ruleId} msg=${e.msg.getOrElse("")}"))
    val passing_res_2 = engine.evaluate(passing_ctx_2, phases = List(1, 2))
    println(passing_res_2.disposition)
    passing_res_2.events.foreach(e => println(s"match phase=${e.phase} id=${e.ruleId} msg=${e.msg.getOrElse("")}"))
    
    assertEquals(failing_res_1.disposition, Block(403, None, None))
    assertEquals(failing_res_2.disposition, Block(403, None, None))
    assertEquals(passing_res_1.disposition, Continue)
    assertEquals(passing_res_2.disposition, Continue)
  }

  test("antlr".ignore) {
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
    SecLang.parse(rules) match {
      case Left(err) => println("parse error: " + err)
      case Right(config) => println("parse success: " + Json.prettyPrint(config.json))
    }
  }

  test("antlr_crs".ignore) {
    val client = HttpClient.newHttpClient()
    /*val data: Map[String, String] = List(
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
          val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build()
          val response = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
          )
          (key, response.body())
      }.toMap*/

    println("fetching CRS ...")
    val rules = List(
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
      .map(v => s"https://raw.githubusercontent.com/coreruleset/coreruleset/refs/heads/main/rules/${v}")
      .map { url =>
        println(s"fetching: ${url} ...")
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
    SecLang.parse(rules) match {
      case Left(err) => println("parse error: " + err)
      case Right(config) => {
        val out1 = Json.prettyPrint(config.json)
        Files.writeString(new File("./crs.json").toPath, out1)
        val config2 = Configuration.format.reads(Json.parse(out1)).get
        val out2 = Json.prettyPrint(config2.json)
        assertEquals(out1, out2)
      }
    }
  }
}