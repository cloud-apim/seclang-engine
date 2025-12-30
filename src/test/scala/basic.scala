package com.cloud.apim.seclang.test

import com.cloud.apim.seclang.impl.model.Disposition._
import com.cloud.apim.seclang.impl.model._
import com.cloud.apim.seclang.scaladsl.SecLang

import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}

class SecLangBasicTest extends munit.FunSuite {

  test("simple rule".ignore) {

    println("\n\n\nRunning tests ... \n\n")

    val rules = """
      |SecRule REMOTE_ADDR "127.0.0.1" "id:1, phase:1, pass, log, logdata:'Request from localhost'"
      |SecRule REQUEST_HEADERS:user-agent "@contains firefox" "id:1, pass, log, logdata:'someone used firefox to access'"
      |SecRule REQUEST_URI "@rx ^/admin" "id:1,phase:1,chain,msg:'admin area'"
      |  SecRule REQUEST_HEADERS:User-Agent "@rx curl" "deny,status:403,msg:'no curl',t:lowercase"
      |
      |SecRule REQUEST_URI "@contains /health" "id:10,phase:1,pass,log"
      |""".stripMargin

    println("parsing ...")
    val loaded = SecLang.parse(rules).fold(err => sys.error(err), identity)
    loaded.foreach(d => println(s"  - ${d}"))
    println("compiling ...")
    val program = SecLang.compile(loaded)
    program.itemsByPhase.foreach {
      case (phase, directives) => directives.foreach { directive =>
        println(s"    - ${phase} - ${directive}")
      }
    }
    println("loading ...")
    val engine = SecLang.engine(program)

    val failing_ctx = RequestContext(
      method = "GET",
      uri = "/admin",
      headers = Map("User-Agent" -> List("curl/8.0")),
      query = Map("q" -> List("test")),
      body = None
    )

    val passing_ctx = RequestContext(
      method = "GET",
      uri = "/health",
      headers = Map("User-Agent" -> List("curl/8.0")),
      query = Map("q" -> List("test")),
      body = None
    )

    println("rung failing test")
    val failing_res = engine.evaluate(failing_ctx, phases = List(1, 2))
    println(failing_res.disposition)
    failing_res.events.foreach(e => println(s"match phase=${e.phase} id=${e.ruleId} msg=${e.msg.getOrElse("")}"))

    println("run passing test")
    val passing_res = engine.evaluate(passing_ctx, phases = List(1, 2))
    println(passing_res.disposition)
    passing_res.events.foreach(e => println(s"match phase=${e.phase} id=${e.ruleId} msg=${e.msg.getOrElse("")}"))
    
    assertEquals(failing_res.disposition, Block(403, None, None))
    assertEquals(passing_res.disposition, Continue)
  }

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
    SecLang.parseAntlr(rules) match {
      case Left(err) => println("parse error: " + err)
      case Right(config) => println("parse success: " + config)
    }
  }

  test("antlr_crs") {
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
    SecLang.parseAntlr(rules) match {
      case Left(err) => println("parse error: " + err)
      case Right(config) => println("parse success: ")
    }
  }
}