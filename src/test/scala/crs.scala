package com.cloud.apim.seclang.test

import akka.util.ByteString
import com.cloud.apim.seclang.impl.compiler.RuleChain
import com.cloud.apim.seclang.impl.engine.SecRulesEngine
import com.cloud.apim.seclang.impl.utils.StatusCodes
import com.cloud.apim.seclang.model.Disposition.{Block, Continue}
import com.cloud.apim.seclang.model.{RequestContext, SecRulesEngineConfig}
import com.cloud.apim.seclang.scaladsl.SecLang
import play.api.libs.json.{JsArray, JsNull, JsObject, JsString, JsValue, Json}

import java.io.File
import java.net.{URI, URLDecoder}
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.Base64
import java.util.concurrent.atomic.AtomicLong
import scala.collection.concurrent.TrieMap
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

object CRSTestUtils {

  val client = HttpClient.newHttpClient()

  def fetch(url: String): String = {
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

  def read(path: String): String = {
    val file = new File(path)
    // println(s"reading file file://${file.getAbsolutePath}")
    Files.readString(file.toPath)
  }

  def setupCRSEngine(): SecRulesEngine = {
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
      .map(v => (v, s"./test-data/coreruleset/rules/${v}"))
      .map {
        case (key, url) => (key, Files.readString(new File(url).toPath))
      }.toMap

    val rules = (Seq(
      "./test-data/coreruleset/crs-setup.conf.example"
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
      .map(v => s"./test-data/coreruleset/rules/${v}")))
      .map { url =>
        Files.readString(new File(url).toPath)
      }
      .mkString("\n")
    val finalRules =
      s"""# setup for test
        |
        |SecAction "id:900005,\\
        |  phase:1,\\
        |  nolog,\\
        |  pass,\\
        |  ctl:ruleEngine=DetectionOnly,\\
        |  ctl:ruleRemoveById=910000,\\
        |  setvar:tx.blocking_paranoia_level=4,\\
        |  setvar:tx.crs_validate_utf8_encoding=1,\\
        |  setvar:tx.arg_name_length=100,\\
        |  setvar:tx.arg_length=400,\\
        |  setvar:tx.total_arg_length=64000,\\
        |  setvar:tx.max_num_args=255,\\
        |  setvar:tx.max_file_size=64100,\\
        |  setvar:tx.combined_file_sizes=65535,\\
        |  setvar:tx.allowed_methods=GET HEAD POST OPTIONS CONNECT"
        |
        |# then include crs
        |${rules}
        |
        |# DetectionOnly mode
        |# SecRuleEngine DetectionOnly
        |""".stripMargin
    val config = SecLang.parse(finalRules).right.get
    val program = SecLang.compile(config)
    SecLang.engine(program, files = files)
  }

  private def parseQueryString(qs: String): Map[String, List[String]] = {
    val params = new TrieMap[String, List[String]]
    if (qs == null || qs.isEmpty) return Map.empty
    qs.split("&").foreach { pair =>
      val idx = pair.indexOf('=')
      val key =
        if (idx > 0)
          URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8)
        else
          URLDecoder.decode(pair, StandardCharsets.UTF_8)
      val value =
        if (idx > 0 && pair.length > idx + 1)
          URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8)
        else
          ""
      params.put(key, params.get(key).getOrElse(List.empty) :+ value)
    }

    params.toMap
  }

  private def parseCookies(rawCookieHeaders: List[String]): Map[String, List[String]] = {
    rawCookieHeaders
      .flatMap { header =>
        header
          .split(";")
          .toList
          .flatMap { part =>
            val idx = part.indexOf('=')
            if (idx > 0) {
              val name  = part.substring(0, idx).trim
              val value = part.substring(idx + 1).trim
              if (name.nonEmpty) Some(name -> value) else None
            } else {
              None
            }
          }
      }.groupBy(_._1).mapValues(_.map(_._2))
  }

  def parseRawHttpRequest(raw: String): JsObject = {
    val lines = raw.replace("\r\n", "\n").split("\n").toList

    // request line
    val requestLine :: rest = lines
    val Array(method, uri, protocol) = requestLine.split(" ", 3)

    // split headers / body
    val (headerLines, bodyLines) = rest.span(_.nonEmpty)

    val headers = headerLines.flatMap { line =>
      line.split(":", 2) match {
        case Array(k, v) => Some(k.trim -> v.trim)
        case _           => None
      }
    }

    Json.obj(
      "method"   -> method,
      "uri"      -> uri,
      "protocol" -> protocol,
      "headers"  -> JsObject(headers.map { case (k, v) => k -> JsString(v) }),
      "data"     -> bodyLines.dropWhile(_.isEmpty).mkString("\n")
    )
  }

  def requestContext(_json: JsValue): RequestContext = {
    val json = (_json \ "encoded_request").asOpt[String] match {
      case None => _json
      case Some(encodedRaw) => {
        val enc = Base64.getDecoder.decode(encodedRaw.replaceAll("\\\\n", "").getBytes(StandardCharsets.UTF_8))
        _json.as[JsObject].deepMerge(parseRawHttpRequest(new String(enc, StandardCharsets.UTF_8)))
      }
    }
    val uri = (json \ "uri").asOpt[String].getOrElse("/")
    val port = (json \ "port").asOpt[Int].getOrElse(80)
    val address = (json \ "dest_addr").asOpt[String].getOrElse("127.0.0.1")
    val body = (json \ "data").asOpt[String].map(s => ByteString(s))
    val isResponse = uri.startsWith("/reflect")
    val respStruct = if (isResponse) Try(Json.parse(body.map(_.utf8String).getOrElse("{}"))).getOrElse(Json.obj("body" -> body.getOrElse(ByteString.empty).utf8String)) else Json.obj()
    val respStatus = if (isResponse) Some((respStruct \ "status").asOpt[Int].getOrElse(200)) else None
    val respStatusTxt = if (isResponse) StatusCodes.get((respStruct \ "status").asOpt[Int].getOrElse(200)) else None
    val headers: Map[String, List[String]] = (json \ "headers").asOpt[Map[String, String]].map(_.mapValues(v => List(v))).getOrElse(Map.empty)
    val respHeaders: Map[String, List[String]] = (respStruct \ "headers").asOpt[Map[String, String]].map(_.mapValues(v => List(v))).getOrElse(Map.empty)
    val encBody = (respStruct \ "encodedBody").asOpt[String].map(s => ByteString(s).decodeBase64)
    val respBody = encBody.orElse((respStruct \ "body").asOpt[String].map(s => ByteString(s)))
    val finalBody = if (isResponse) respBody else body
    val pfheaders = if (isResponse) respHeaders else headers
    val autocomplete_headers = (json \ "autocomplete_headers").asOpt[Boolean].getOrElse(true)
    val finalHeaders: Map[String, List[String]] = {
      if (finalBody.isDefined && autocomplete_headers) {
        pfheaders + ("Content-Length" -> List(finalBody.get.length.toString))
      } else {
        pfheaders
      }
    }
    val query: Map[String, List[String]] = try {
      parseQueryString(new URI(uri).getQuery)
    } catch {
      case e: Throwable => Map.empty
    }
    val cookies: Map[String, List[String]] = try {
      val rawCookie: List[String] = finalHeaders.get("Cookie").orElse(finalHeaders.get("cookie")).getOrElse(List.empty)
      parseCookies(rawCookie)
    } catch {
      case e: Throwable => Map.empty
    }
    RequestContext(
      method = (json \ "method").asOpt[String].getOrElse("GET"),
      uri = uri,
      status = respStatus,
      statusTxt = respStatusTxt,
      query = query,
      cookies = cookies,
      headers = finalHeaders,
      body = finalBody,
      protocol = (json \ "version").asOpt[String].orElse((json \ "protocol").asOpt[String]).getOrElse("HTTP/1.1"),
    )
  }

  val testOverrides = Map(
    (920100, 2) -> Json.parse("""{
        |  "test_id" : 2,
        |  "desc" : "Request has tab (\\t) before request method - Apache complains\nAH00126: Invalid URI in request      GET / HTTP/1.1\n",
        |  "stages" : [ {
        |    "input" : {
        |      "dest_addr" : "127.0.0.1",
        |      "method" : "     GET",
        |      "port" : 80,
        |      "headers" : {
        |        "User-Agent" : "OWASP CRS test agent",
        |        "Host" : "localhost",
        |        "Accept" : "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"
        |      },
        |      "uri" : "/get",
        |      "version" : "HTTP/1.1"
        |    },
        |    "output" : {
        |      "log" : {
        |        "expect_ids": [911100]
        |      }
        |    }
        |  } ]
        |}""".stripMargin),
    (920100, 5) -> Json.parse("""{
        |  "test_id" : 5,
        |  "desc" : "invalid Connect request, domains require ports",
        |  "stages" : [ {
        |    "input" : {
        |      "dest_addr" : "127.0.0.1",
        |      "method" : "CONNECT",
        |      "port" : 80,
        |      "headers" : {
        |        "User-Agent" : "OWASP CRS test agent",
        |        "Host" : "localhost",
        |        "Accept" : "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"
        |      },
        |      "uri" : "www.coreruleset.org",
        |      "version" : "HTTP/1.1"
        |    },
        |    "output" : {
        |      "status" : 200
        |    }
        |  } ]
        |}""".stripMargin),
    (920100, 8) -> Json.parse("""{
        |  "test_id" : 8,
        |  "desc" : "The colon in the path is not allowed. Apache will block by default:\n(20024)The given path is misformatted or contained invalid characters: [client 127.0.0.1:4142] AH00127: Cannot map GET /index.html:80?I=Like&Apples=Today#tag HTTP/1.1 to file\n",
        |  "stages" : [ {
        |    "input" : {
        |      "dest_addr" : "127.0.0.1",
        |      "method" : "GET",
        |      "port" : 80,
        |      "headers" : {
        |        "User-Agent" : "OWASP CRS test agent",
        |        "Host" : "localhost",
        |        "Accept" : "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"
        |      },
        |      "uri" : "/get/index.html:80?I=Like&Apples=Today#tag",
        |      "version" : "HTTP/1.1"
        |    },
        |    "output" : {
        |      "log" : { "expect_ids": [920610] }
        |    }
        |  } ]
        |}""".stripMargin),
    (920100, 11) -> Json.parse("""{
        |  "test_id" : 11,
        |  "desc" : "An invalid request because a backslash is used in URI.\nApache will end up blocking this before it gets to CRS.\nWe will need to support OR output tests to fix this.\n",
        |  "stages" : [ {
        |    "input" : {
        |      "dest_addr" : "127.0.0.1",
        |      "port" : 80,
        |      "headers" : {
        |        "User-Agent" : "OWASP CRS test agent",
        |        "Host" : "localhost",
        |        "Accept" : "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"
        |      },
        |      "uri" : "\\",
        |      "version" : "HTTP/1.1"
        |    },
        |    "output" : {
        |      "status" : 200
        |    }
        |  } ]
        |}""".stripMargin),
    (920100, 12) -> Json.parse("""{
        |  "test_id" : 12,
        |  "desc" : "Invalid HTTP Request Line (920100) - Test 1 from old modsec regressions",
        |  "stages" : [ {
        |    "input" : {
        |      "dest_addr" : "127.0.0.1",
        |      "headers" : {
        |        "Accept" : "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5",
        |        "Host" : "localhost",
        |        "Keep-Alive" : "300",
        |        "Proxy-Connection" : "keep-alive",
        |        "User-Agent" : "OWASP CRS test agent"
        |      },
        |      "method" : "\tGET",
        |      "port" : 80,
        |      "uri" : "/get",
        |      "version" : "HTTP/1.1"
        |    },
        |    "output" : {
        |      "log" : { "expect_ids": [911100, 920100] }
        |    }
        |  } ]
        |}""".stripMargin),
    (920100, 13) -> Json.parse("""{
        |  "test_id" : 13,
        |  "desc" : "Invalid HTTP Request Line (920100) - Test 2 from old modsec regressions",
        |  "stages" : [ {
        |    "input" : {
        |      "dest_addr" : "127.0.0.1",
        |      "headers" : {
        |        "Accept" : "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5",
        |        "Host" : "localhost",
        |        "Keep-Alive" : "300",
        |        "Proxy-Connection" : "keep-alive",
        |        "User-Agent" : "OWASP CRS test agent"
        |      },
        |      "method" : "GET",
        |      "port" : 80,
        |      "uri" : "\\index.html",
        |      "version" : "HTTP\\1.0"
        |    },
        |    "output" : {
        |      "log" : { "expect_ids": [920100, 920460] }
        |    }
        |  } ]
        |}""".stripMargin),
    (920100, 15) -> Json.parse("""{
        |  "test_id" : 15,
        |  "desc" : "Test as described in http://www.client9.com/article/five-interesting-injection-attacks/",
        |  "stages" : [ {
        |    "input" : {
        |      "dest_addr" : "127.0.0.1",
        |      "method" : "GET",
        |      "port" : 80,
        |      "uri" : "/get/demo/xss/xml/vuln.xml.php?input=<script xmlns=\"http://www.w3.org/1999/xhtml\">setTimeout(\"top.frame2.location=\\\"javascript:(function () {var x = document.createElement(\\\\\\\"script\\\\\\\");x.src = \\\\\\\"//sdl.me/popup.js?//\\\\\\\";document.childNodes\\[0\\].appendChild(x);}());\\\"\",1000)</script>&//",
        |      "headers" : {
        |        "User-Agent" : "OWASP CRS test agent",
        |        "Host" : "localhost",
        |        "Accept" : "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"
        |      },
        |      "version" : "HTTP/1.1"
        |    },
        |    "output" : {
        |       "log" : { "expect_ids": [920100] }
        |    }
        |  } ]
        |}""".stripMargin),
    (920160, 1) -> Json.parse("""{
        |  "test_id" : 1,
        |  "desc" : "Non digit Content-Length without content-type",
        |  "stages" : [ {
        |    "input" : {
        |      "dest_addr" : "127.0.0.1",
        |      "method" : "GET",
        |      "port" : 80,
        |      "headers" : {
        |        "User-Agent" : "OWASP CRS test agent",
        |        "Host" : "localhost",
        |        "Content-Length" : "NotDigits",
        |        "Accept" : "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"
        |      },
        |      "uri" : "/",
        |      "version" : "HTTP/1.1"
        |    },
        |    "output" : {
        |      "log" : { "expect_ids": [920160] }
        |    }
        |  } ]
        |}""".stripMargin),
    (920160, 2) -> Json.parse("""{
        |  "test_id" : 2,
        |  "desc" : "Non digit content-length with content-type",
        |  "stages" : [ {
        |    "input" : {
        |      "dest_addr" : "127.0.0.1",
        |      "method" : "POST",
        |      "port" : 80,
        |      "headers" : {
        |        "User-Agent" : "OWASP CRS test agent",
        |        "Host" : "localhost",
        |        "Content-Type" : "application/x-www-form-urlencoded",
        |        "Content-Length" : "NotDigits",
        |        "Accept" : "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"
        |      },
        |      "uri" : "/",
        |      "version" : "HTTP/1.1"
        |    },
        |    "output" : {
        |      "log" : { "expect_ids": [920160] }
        |    }
        |  } ]
        |}""".stripMargin),
    (920160, 3) -> Json.parse("""{
        |  "test_id" : 3,
        |  "desc" : "Mixed digit and non digit content length",
        |  "stages" : [ {
        |    "input" : {
        |      "dest_addr" : "127.0.0.1",
        |      "method" : "POST",
        |      "port" : 80,
        |      "headers" : {
        |        "User-Agent" : "OWASP CRS test agent",
        |        "Host" : "localhost",
        |        "Content-Type" : "application/x-www-form-urlencoded",
        |        "Content-Length" : "123x",
        |        "Accept" : "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"
        |      },
        |      "uri" : "/",
        |      "version" : "HTTP/1.1"
        |    },
        |    "output" : {
        |      "log" : { "expect_ids": [920160] }
        |    }
        |  } ]
        |}""".stripMargin),
    (920160, 5) -> Json.parse("""{
        |  "test_id" : 5,
        |  "desc" : "Content-Length HTTP header is not numeric (920160)  from old modsec regressions",
        |  "stages" : [ {
        |    "input" : {
        |      "dest_addr" : "127.0.0.1",
        |      "headers" : {
        |        "Accept" : "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5",
        |        "Accept-Language" : "en-us,en;q=0.5",
        |        "Content-Length" : "3;",
        |        "Content-Type" : "application/x-www-form-urlencoded",
        |        "Host" : "localhost",
        |        "Keep-Alive" : "300",
        |        "Proxy-Connection" : "keep-alive",
        |        "User-Agent" : "OWASP CRS test agent"
        |      },
        |      "method" : "POST",
        |      "port" : 80,
        |      "uri" : "/",
        |      "version" : "HTTP/1.1",
        |      "data" : "abc"
        |    },
        |    "output" : {
        |      "log" : { "expect_ids": [920160] }
        |    }
        |  } ]
        |}""".stripMargin),
  )
}

class SecLangCRSTest extends munit.FunSuite {

  private val engine = CRSTestUtils.setupCRSEngine()
  private val counter = new AtomicLong(0L)
  private val failures = new AtomicLong(0L)
  private val dev = true
  private var failingTests = List.empty[JsObject]
  private var times = List.empty[Long]
  private val allRules = engine.program.itemsByPhase.toSeq.flatMap(_._2).collect {
    case RuleChain(rules) => rules
  }.flatten

  //private val testOnly: List[(String, Int)] = List(("920190", 2))
  private val testOnly: List[(String, Int)] = List.empty

  def writeStats(): Unit = {
    if (testOnly.isEmpty) {
      val document = Json.obj(
        "global_stats" -> Json.obj(
          "failure_percentage" -> ((failures.get() * 100.0) / counter.get()),
          "passing_percentage" -> (((counter.get() - failures.get()) * 100.0) / counter.get()),
          "total_tests" -> counter.get(),
          "success_tests" -> (counter.get() - failures.get()),
          "failure_tests" -> failures.get(),
        ),
        "test_failures" -> JsArray(failingTests)
      )
      println(Json.prettyPrint((document \ "global_stats").as[JsObject]))
      Files.writeString(new File("crs-tests-status.json").toPath, Json.prettyPrint(document))
    }
    def fmt(ns: Long): String =
      Duration.fromNanos(ns).toMillis + " ms"

    val rounds = counter.get()
    val total = times.sum
    val min = times.min
    val max = times.max
    val avg = total / rounds
    println(s"Runs        : $rounds")
    println(s"Total time  : ${fmt(total)}")
    println(s"Min         : ${fmt(min)}")
    println(s"Max         : ${fmt(max)}")
    println(s"Average     : ${fmt(avg)}")
  }

  def execTest(rule: String, path: String): Unit = {
    Try {
      // println(s"running tests for rule ${rule} ...")
      val testsFile = CRSTestUtils.read(s"./test-data/coreruleset/tests/regression/tests/${path}")
      val testsYaml = Yaml.parse(testsFile).get
      val tests = (testsYaml \ "tests").as[Seq[JsObject]]
      tests.foreach { _test =>
        val testId = (_test \ "test_id").as[Int]
        val test = CRSTestUtils.testOverrides.get((rule.toInt, testId)).getOrElse(_test)
        if (testOnly.isEmpty || (testOnly.nonEmpty && testOnly.contains((rule, testId)))) {
          val desc = (test \ "desc").asOpt[String]
          (test \ "stages").as[Seq[JsObject]].foreach { stage =>
            counter.incrementAndGet()
            val input = (stage \ "input").as[JsObject]
            val ctx = CRSTestUtils.requestContext(input)
            val start = System.nanoTime()
            val result = engine.evaluate(ctx, if(ctx.isResponse) List(3, 4) else List(1, 2))
            val dur = System.nanoTime() - start
            times = times :+ dur
            val output = (stage \ "output").as[JsObject]
            val status = (output \ "status").asOpt[Int]
            val log = (output \ "log").asOpt[JsObject].getOrElse(Json.obj())
            val expect_ids: List[Int] = (log \ "expect_ids").asOpt[List[Int]].getOrElse(List.empty)
            val no_expect_ids: List[Int] = (log \ "no_expect_ids").asOpt[List[Int]].getOrElse(List.empty)
            var checked = false
            var ok = true
            var cause = "--"
            if (status.isDefined) {
              checked = true
              val outStatus = result.disposition match {
                case Continue => 200
                case Block(s, _, _) => s
              }
              if (outStatus != status.get) {
                failures.incrementAndGet()
                ok = false
                println(s"[${rule} - ${testId}] ${desc.getOrElse("--")}")
                println(s"      failed status check: $outStatus != ${status.get} ")
              }
              if (!dev) assertEquals(outStatus, status.get, s"status mismatch for test ${testId}")
            }
            if (expect_ids.nonEmpty) {
              checked = true
              expect_ids.foreach { expect_id =>

                val passed = result.events.exists(evt => evt.ruleId.contains(expect_id))
                if (passed == false) {
                  ok = false
                  failures.incrementAndGet()
                  println(s"[${rule} - ${testId}] ${desc.getOrElse("--")}")
                  println(s"      failed expect_id check: $expect_id not found ")
                  cause = s"failed expect_id check: $expect_id not found"
                }
                if (!dev) assertEquals(passed, true, s"expect_id mismatch for test ${testId}")
              }
            }
            if (no_expect_ids.nonEmpty) {
              checked = true
              no_expect_ids.foreach { no_expect_id =>
                val notpassed = result.events.exists(evt => evt.ruleId.contains(no_expect_id))
                if (notpassed == true) {
                  ok = false
                  failures.incrementAndGet()
                  println(s"[${rule} - ${testId}] ${desc.getOrElse("--")}")
                  println(s"      failed no_expect_ids check: $no_expect_id was found ")
                  cause = s"failed no_expect_ids check: $no_expect_id was found"
                }
                if (!dev) assertEquals(notpassed, false, s"no_expect_ids mismatch for test ${testId}")
              }
            }
            if (checked && ok) {
              //  println(s"    - [${rule} - ${testId}] passed")
            } else if (!checked && ok) {
              ok = false
              failures.incrementAndGet()
              println(s"[${rule} - ${testId}] ${desc.getOrElse("--")}")
              println(s"      nothing checked for test ${testId} ")
              cause = s"nothing checked for test ${testId}"
            }
            if (!ok) {
              val descr = desc.getOrElse("--")
              failingTests = failingTests :+ Json.obj(
                "test_rule" -> rule,
                "test_id" -> testId,
                "test_path" -> path,
                "description" -> descr,
                "cause" -> cause,
                "result" -> result.json,
                "test" -> test,
                "tested_rule" -> allRules.find(_.id.contains(rule.toInt)).map(_.json).getOrElse(JsNull).as[JsValue]
              )
            }
            if (!ok && testOnly.nonEmpty && testOnly.contains((rule, testId))) {
              result.displayPrintln()
              println(Json.prettyPrint(test))
              println(Json.prettyPrint(Json.parse(result.events.filter(_.msg.isDefined).last.raw)))
            }
            if (!dev) assert(checked, s"nothing checked for test ${testId}")
          }
        }
      }
    } match {
      case Failure(e) =>
        val clazzName = e.getStackTrace.apply(0).getClassName
        if (!clazzName.startsWith("munit.Assertions")) {
          println(e.getClass.getName + ": " + e.getMessage)
        } else {
          // println(e.getMessage)
        }
      // throw e // TODO: uncomment for back to normal
      case Success(s) => s
    }
  }

  test("REQUEST-911-METHOD-ENFORCEMENT") {
    execTest("911100", "REQUEST-911-METHOD-ENFORCEMENT/911100.yaml")
  }
  test("REQUEST-913-SCANNER-DETECTION") {
    execTest("913100", "REQUEST-913-SCANNER-DETECTION/913100.yaml")
  }
  test("REQUEST-920-PROTOCOL-ENFORCEMENT") {
    execTest("920100", "REQUEST-920-PROTOCOL-ENFORCEMENT/920100.yaml")
    execTest("920120", "REQUEST-920-PROTOCOL-ENFORCEMENT/920120.yaml")
    execTest("920121", "REQUEST-920-PROTOCOL-ENFORCEMENT/920121.yaml")
    execTest("920160", "REQUEST-920-PROTOCOL-ENFORCEMENT/920160.yaml")
    execTest("920170", "REQUEST-920-PROTOCOL-ENFORCEMENT/920170.yaml")
    execTest("920171", "REQUEST-920-PROTOCOL-ENFORCEMENT/920171.yaml")
    execTest("920180", "REQUEST-920-PROTOCOL-ENFORCEMENT/920180.yaml")
    execTest("920181", "REQUEST-920-PROTOCOL-ENFORCEMENT/920181.yaml")
    execTest("920190", "REQUEST-920-PROTOCOL-ENFORCEMENT/920190.yaml")
    execTest("920200", "REQUEST-920-PROTOCOL-ENFORCEMENT/920200.yaml")
    execTest("920201", "REQUEST-920-PROTOCOL-ENFORCEMENT/920201.yaml")
    execTest("920202", "REQUEST-920-PROTOCOL-ENFORCEMENT/920202.yaml")
    execTest("920210", "REQUEST-920-PROTOCOL-ENFORCEMENT/920210.yaml")
    execTest("920230", "REQUEST-920-PROTOCOL-ENFORCEMENT/920230.yaml")
    execTest("920240", "REQUEST-920-PROTOCOL-ENFORCEMENT/920240.yaml")
    execTest("920250", "REQUEST-920-PROTOCOL-ENFORCEMENT/920250.yaml")
    execTest("920260", "REQUEST-920-PROTOCOL-ENFORCEMENT/920260.yaml")
    execTest("920270", "REQUEST-920-PROTOCOL-ENFORCEMENT/920270.yaml")
    execTest("920271", "REQUEST-920-PROTOCOL-ENFORCEMENT/920271.yaml")
    execTest("920272", "REQUEST-920-PROTOCOL-ENFORCEMENT/920272.yaml")
    execTest("920273", "REQUEST-920-PROTOCOL-ENFORCEMENT/920273.yaml")
    execTest("920274", "REQUEST-920-PROTOCOL-ENFORCEMENT/920274.yaml")
    execTest("920275", "REQUEST-920-PROTOCOL-ENFORCEMENT/920275.yaml")
    execTest("920280", "REQUEST-920-PROTOCOL-ENFORCEMENT/920280.yaml")
    execTest("920290", "REQUEST-920-PROTOCOL-ENFORCEMENT/920290.yaml")
    execTest("920300", "REQUEST-920-PROTOCOL-ENFORCEMENT/920300.yaml")
    execTest("920310", "REQUEST-920-PROTOCOL-ENFORCEMENT/920310.yaml")
    execTest("920311", "REQUEST-920-PROTOCOL-ENFORCEMENT/920311.yaml")
    execTest("920320", "REQUEST-920-PROTOCOL-ENFORCEMENT/920320.yaml")
    execTest("920330", "REQUEST-920-PROTOCOL-ENFORCEMENT/920330.yaml")
    execTest("920340", "REQUEST-920-PROTOCOL-ENFORCEMENT/920340.yaml")
    execTest("920350", "REQUEST-920-PROTOCOL-ENFORCEMENT/920350.yaml")
    execTest("920360", "REQUEST-920-PROTOCOL-ENFORCEMENT/920360.yaml")
    execTest("920370", "REQUEST-920-PROTOCOL-ENFORCEMENT/920370.yaml")
    execTest("920380", "REQUEST-920-PROTOCOL-ENFORCEMENT/920380.yaml")
    execTest("920390", "REQUEST-920-PROTOCOL-ENFORCEMENT/920390.yaml")
    execTest("920400", "REQUEST-920-PROTOCOL-ENFORCEMENT/920400.yaml")
    execTest("920410", "REQUEST-920-PROTOCOL-ENFORCEMENT/920410.yaml")
    execTest("920420", "REQUEST-920-PROTOCOL-ENFORCEMENT/920420.yaml")
    execTest("920430", "REQUEST-920-PROTOCOL-ENFORCEMENT/920430.yaml")
    execTest("920440", "REQUEST-920-PROTOCOL-ENFORCEMENT/920440.yaml")
    execTest("920450", "REQUEST-920-PROTOCOL-ENFORCEMENT/920450.yaml")
    execTest("920451", "REQUEST-920-PROTOCOL-ENFORCEMENT/920451.yaml")
    execTest("920460", "REQUEST-920-PROTOCOL-ENFORCEMENT/920460.yaml")
    execTest("920470", "REQUEST-920-PROTOCOL-ENFORCEMENT/920470.yaml")
    execTest("920480", "REQUEST-920-PROTOCOL-ENFORCEMENT/920480.yaml")
    execTest("920490", "REQUEST-920-PROTOCOL-ENFORCEMENT/920490.yaml")
    execTest("920500", "REQUEST-920-PROTOCOL-ENFORCEMENT/920500.yaml")
    execTest("920510", "REQUEST-920-PROTOCOL-ENFORCEMENT/920510.yaml")
    execTest("920520", "REQUEST-920-PROTOCOL-ENFORCEMENT/920520.yaml")
    execTest("920521", "REQUEST-920-PROTOCOL-ENFORCEMENT/920521.yaml")
    execTest("920530", "REQUEST-920-PROTOCOL-ENFORCEMENT/920530.yaml")
    execTest("920540", "REQUEST-920-PROTOCOL-ENFORCEMENT/920540.yaml")
    execTest("920600", "REQUEST-920-PROTOCOL-ENFORCEMENT/920600.yaml")
    execTest("920610", "REQUEST-920-PROTOCOL-ENFORCEMENT/920610.yaml")
    execTest("920620", "REQUEST-920-PROTOCOL-ENFORCEMENT/920620.yaml")
  }
  test("REQUEST-921-PROTOCOL-ATTACK") {
    execTest("921110", "REQUEST-921-PROTOCOL-ATTACK/921110.yaml")
    execTest("921120", "REQUEST-921-PROTOCOL-ATTACK/921120.yaml")
    execTest("921130", "REQUEST-921-PROTOCOL-ATTACK/921130.yaml")
    execTest("921140", "REQUEST-921-PROTOCOL-ATTACK/921140.yaml")
    execTest("921150", "REQUEST-921-PROTOCOL-ATTACK/921150.yaml")
    execTest("921151", "REQUEST-921-PROTOCOL-ATTACK/921151.yaml")
    execTest("921160", "REQUEST-921-PROTOCOL-ATTACK/921160.yaml")
    execTest("921180", "REQUEST-921-PROTOCOL-ATTACK/921180.yaml")
    execTest("921190", "REQUEST-921-PROTOCOL-ATTACK/921190.yaml")
    execTest("921200", "REQUEST-921-PROTOCOL-ATTACK/921200.yaml")
    execTest("921210", "REQUEST-921-PROTOCOL-ATTACK/921210.yaml")
    execTest("921220", "REQUEST-921-PROTOCOL-ATTACK/921220.yaml")
    execTest("921230", "REQUEST-921-PROTOCOL-ATTACK/921230.yaml")
    execTest("921240", "REQUEST-921-PROTOCOL-ATTACK/921240.yaml")
    execTest("921250", "REQUEST-921-PROTOCOL-ATTACK/921250.yaml")
    execTest("921421", "REQUEST-921-PROTOCOL-ATTACK/921421.yaml")
    execTest("921422", "REQUEST-921-PROTOCOL-ATTACK/921422.yaml")
  }
  test("REQUEST-922-MULTIPART-ATTACK") {
    execTest("922100", "REQUEST-922-MULTIPART-ATTACK/922100.yaml")
    execTest("922110", "REQUEST-922-MULTIPART-ATTACK/922110.yaml")
    execTest("922120", "REQUEST-922-MULTIPART-ATTACK/922120.yaml")
    execTest("922130", "REQUEST-922-MULTIPART-ATTACK/922130.yaml")
  }
  test("REQUEST-930-APPLICATION-ATTACK-LFI") {
    execTest("930100", "REQUEST-930-APPLICATION-ATTACK-LFI/930100.yaml")
    execTest("930110", "REQUEST-930-APPLICATION-ATTACK-LFI/930110.yaml")
    execTest("930120", "REQUEST-930-APPLICATION-ATTACK-LFI/930120.yaml")
    execTest("930121", "REQUEST-930-APPLICATION-ATTACK-LFI/930121.yaml")
    execTest("930130", "REQUEST-930-APPLICATION-ATTACK-LFI/930130.yaml")
  }
  test("REQUEST-931-APPLICATION-ATTACK-RFI") {
    execTest("931100", "REQUEST-931-APPLICATION-ATTACK-RFI/931100.yaml")
    execTest("931110", "REQUEST-931-APPLICATION-ATTACK-RFI/931110.yaml")
    execTest("931120", "REQUEST-931-APPLICATION-ATTACK-RFI/931120.yaml")
    execTest("931130", "REQUEST-931-APPLICATION-ATTACK-RFI/931130.yaml")
    execTest("931131", "REQUEST-931-APPLICATION-ATTACK-RFI/931131.yaml")
  }
  test("REQUEST-932-APPLICATION-ATTACK-RCE") {
    execTest("932120", "REQUEST-932-APPLICATION-ATTACK-RCE/932120.yaml")
    execTest("932125", "REQUEST-932-APPLICATION-ATTACK-RCE/932125.yaml")
    execTest("932130", "REQUEST-932-APPLICATION-ATTACK-RCE/932130.yaml")
    execTest("932131", "REQUEST-932-APPLICATION-ATTACK-RCE/932131.yaml")
    execTest("932140", "REQUEST-932-APPLICATION-ATTACK-RCE/932140.yaml")
    execTest("932160", "REQUEST-932-APPLICATION-ATTACK-RCE/932160.yaml")
    execTest("932161", "REQUEST-932-APPLICATION-ATTACK-RCE/932161.yaml")
    execTest("932170", "REQUEST-932-APPLICATION-ATTACK-RCE/932170.yaml")
    execTest("932171", "REQUEST-932-APPLICATION-ATTACK-RCE/932171.yaml")
    execTest("932175", "REQUEST-932-APPLICATION-ATTACK-RCE/932175.yaml")
    execTest("932180", "REQUEST-932-APPLICATION-ATTACK-RCE/932180.yaml")
    execTest("932190", "REQUEST-932-APPLICATION-ATTACK-RCE/932190.yaml")
    execTest("932200", "REQUEST-932-APPLICATION-ATTACK-RCE/932200.yaml")
    execTest("932205", "REQUEST-932-APPLICATION-ATTACK-RCE/932205.yaml")
    execTest("932206", "REQUEST-932-APPLICATION-ATTACK-RCE/932206.yaml")
    execTest("932207", "REQUEST-932-APPLICATION-ATTACK-RCE/932207.yaml")
    execTest("932210", "REQUEST-932-APPLICATION-ATTACK-RCE/932210.yaml")
    execTest("932220", "REQUEST-932-APPLICATION-ATTACK-RCE/932220.yaml")
    execTest("932230", "REQUEST-932-APPLICATION-ATTACK-RCE/932230.yaml")
    execTest("932231", "REQUEST-932-APPLICATION-ATTACK-RCE/932231.yaml")
    execTest("932232", "REQUEST-932-APPLICATION-ATTACK-RCE/932232.yaml")
    execTest("932235", "REQUEST-932-APPLICATION-ATTACK-RCE/932235.yaml")
    execTest("932236", "REQUEST-932-APPLICATION-ATTACK-RCE/932236.yaml")
    execTest("932237", "REQUEST-932-APPLICATION-ATTACK-RCE/932237.yaml")
    execTest("932238", "REQUEST-932-APPLICATION-ATTACK-RCE/932238.yaml")
    execTest("932239", "REQUEST-932-APPLICATION-ATTACK-RCE/932239.yaml")
    execTest("932240", "REQUEST-932-APPLICATION-ATTACK-RCE/932240.yaml")
    execTest("932250", "REQUEST-932-APPLICATION-ATTACK-RCE/932250.yaml")
    execTest("932260", "REQUEST-932-APPLICATION-ATTACK-RCE/932260.yaml")
    execTest("932270", "REQUEST-932-APPLICATION-ATTACK-RCE/932270.yaml")
    execTest("932271", "REQUEST-932-APPLICATION-ATTACK-RCE/932271.yaml")
    execTest("932280", "REQUEST-932-APPLICATION-ATTACK-RCE/932280.yaml")
    execTest("932281", "REQUEST-932-APPLICATION-ATTACK-RCE/932281.yaml")
    execTest("932300", "REQUEST-932-APPLICATION-ATTACK-RCE/932300.yaml")
    execTest("932301", "REQUEST-932-APPLICATION-ATTACK-RCE/932301.yaml")
    execTest("932310", "REQUEST-932-APPLICATION-ATTACK-RCE/932310.yaml")
    execTest("932311", "REQUEST-932-APPLICATION-ATTACK-RCE/932311.yaml")
    execTest("932320", "REQUEST-932-APPLICATION-ATTACK-RCE/932320.yaml")
    execTest("932321", "REQUEST-932-APPLICATION-ATTACK-RCE/932321.yaml")
    execTest("932330", "REQUEST-932-APPLICATION-ATTACK-RCE/932330.yaml")
    execTest("932331", "REQUEST-932-APPLICATION-ATTACK-RCE/932331.yaml")
    execTest("932370", "REQUEST-932-APPLICATION-ATTACK-RCE/932370.yaml")
    execTest("932371", "REQUEST-932-APPLICATION-ATTACK-RCE/932371.yaml")
    execTest("932380", "REQUEST-932-APPLICATION-ATTACK-RCE/932380.yaml")
  }
  test("REQUEST-933-APPLICATION-ATTACK-PHP") {
    execTest("933100", "REQUEST-933-APPLICATION-ATTACK-PHP/933100.yaml")
    execTest("933110", "REQUEST-933-APPLICATION-ATTACK-PHP/933110.yaml")
    execTest("933111", "REQUEST-933-APPLICATION-ATTACK-PHP/933111.yaml")
    execTest("933120", "REQUEST-933-APPLICATION-ATTACK-PHP/933120.yaml")
    execTest("933130", "REQUEST-933-APPLICATION-ATTACK-PHP/933130.yaml")
    execTest("933131", "REQUEST-933-APPLICATION-ATTACK-PHP/933131.yaml")
    execTest("933135", "REQUEST-933-APPLICATION-ATTACK-PHP/933135.yaml")
    execTest("933140", "REQUEST-933-APPLICATION-ATTACK-PHP/933140.yaml")
    execTest("933150", "REQUEST-933-APPLICATION-ATTACK-PHP/933150.yaml")
    execTest("933151", "REQUEST-933-APPLICATION-ATTACK-PHP/933151.yaml")
    execTest("933152", "REQUEST-933-APPLICATION-ATTACK-PHP/933152.yaml")
    execTest("933153", "REQUEST-933-APPLICATION-ATTACK-PHP/933153.yaml")
    execTest("933160", "REQUEST-933-APPLICATION-ATTACK-PHP/933160.yaml")
    execTest("933161", "REQUEST-933-APPLICATION-ATTACK-PHP/933161.yaml")
    execTest("933170", "REQUEST-933-APPLICATION-ATTACK-PHP/933170.yaml")
    execTest("933180", "REQUEST-933-APPLICATION-ATTACK-PHP/933180.yaml")
    execTest("933190", "REQUEST-933-APPLICATION-ATTACK-PHP/933190.yaml")
    execTest("933200", "REQUEST-933-APPLICATION-ATTACK-PHP/933200.yaml")
    execTest("933210", "REQUEST-933-APPLICATION-ATTACK-PHP/933210.yaml")
    execTest("933211", "REQUEST-933-APPLICATION-ATTACK-PHP/933211.yaml")
  }
  test("REQUEST-934-APPLICATION-ATTACK-GENERIC") {
    execTest("934100", "REQUEST-934-APPLICATION-ATTACK-GENERIC/934100.yaml")
    execTest("934101", "REQUEST-934-APPLICATION-ATTACK-GENERIC/934101.yaml")
    execTest("934110", "REQUEST-934-APPLICATION-ATTACK-GENERIC/934110.yaml")
    execTest("934120", "REQUEST-934-APPLICATION-ATTACK-GENERIC/934120.yaml")
    execTest("934130", "REQUEST-934-APPLICATION-ATTACK-GENERIC/934130.yaml")
    execTest("934140", "REQUEST-934-APPLICATION-ATTACK-GENERIC/934140.yaml")
    execTest("934150", "REQUEST-934-APPLICATION-ATTACK-GENERIC/934150.yaml")
    execTest("934160", "REQUEST-934-APPLICATION-ATTACK-GENERIC/934160.yaml")
    execTest("934170", "REQUEST-934-APPLICATION-ATTACK-GENERIC/934170.yaml")
    execTest("934180", "REQUEST-934-APPLICATION-ATTACK-GENERIC/934180.yaml")
  }
  test("REQUEST-941-APPLICATION-ATTACK-XSS") {
    execTest("941100", "REQUEST-941-APPLICATION-ATTACK-XSS/941100.yaml")
    execTest("941101", "REQUEST-941-APPLICATION-ATTACK-XSS/941101.yaml")
    execTest("941110", "REQUEST-941-APPLICATION-ATTACK-XSS/941110.yaml")
    execTest("941120", "REQUEST-941-APPLICATION-ATTACK-XSS/941120.yaml")
    execTest("941130", "REQUEST-941-APPLICATION-ATTACK-XSS/941130.yaml")
    execTest("941140", "REQUEST-941-APPLICATION-ATTACK-XSS/941140.yaml")
    execTest("941150", "REQUEST-941-APPLICATION-ATTACK-XSS/941150.yaml")
    execTest("941160", "REQUEST-941-APPLICATION-ATTACK-XSS/941160.yaml")
    execTest("941170", "REQUEST-941-APPLICATION-ATTACK-XSS/941170.yaml")
    execTest("941180", "REQUEST-941-APPLICATION-ATTACK-XSS/941180.yaml")
    execTest("941181", "REQUEST-941-APPLICATION-ATTACK-XSS/941181.yaml")
    execTest("941190", "REQUEST-941-APPLICATION-ATTACK-XSS/941190.yaml")
    execTest("941200", "REQUEST-941-APPLICATION-ATTACK-XSS/941200.yaml")
    execTest("941210", "REQUEST-941-APPLICATION-ATTACK-XSS/941210.yaml")
    execTest("941220", "REQUEST-941-APPLICATION-ATTACK-XSS/941220.yaml")
    execTest("941230", "REQUEST-941-APPLICATION-ATTACK-XSS/941230.yaml")
    execTest("941240", "REQUEST-941-APPLICATION-ATTACK-XSS/941240.yaml")
    execTest("941250", "REQUEST-941-APPLICATION-ATTACK-XSS/941250.yaml")
    execTest("941260", "REQUEST-941-APPLICATION-ATTACK-XSS/941260.yaml")
    execTest("941270", "REQUEST-941-APPLICATION-ATTACK-XSS/941270.yaml")
    execTest("941280", "REQUEST-941-APPLICATION-ATTACK-XSS/941280.yaml")
    execTest("941290", "REQUEST-941-APPLICATION-ATTACK-XSS/941290.yaml")
    execTest("941300", "REQUEST-941-APPLICATION-ATTACK-XSS/941300.yaml")
    execTest("941310", "REQUEST-941-APPLICATION-ATTACK-XSS/941310.yaml")
    execTest("941320", "REQUEST-941-APPLICATION-ATTACK-XSS/941320.yaml")
    execTest("941330", "REQUEST-941-APPLICATION-ATTACK-XSS/941330.yaml")
    execTest("941340", "REQUEST-941-APPLICATION-ATTACK-XSS/941340.yaml")
    execTest("941350", "REQUEST-941-APPLICATION-ATTACK-XSS/941350.yaml")
    execTest("941360", "REQUEST-941-APPLICATION-ATTACK-XSS/941360.yaml")
    execTest("941370", "REQUEST-941-APPLICATION-ATTACK-XSS/941370.yaml")
    execTest("941380", "REQUEST-941-APPLICATION-ATTACK-XSS/941380.yaml")
    execTest("941390", "REQUEST-941-APPLICATION-ATTACK-XSS/941390.yaml")
    execTest("941400", "REQUEST-941-APPLICATION-ATTACK-XSS/941400.yaml")
  }
  test("REQUEST-942-APPLICATION-ATTACK-SQLI") {
    execTest("942100", "REQUEST-942-APPLICATION-ATTACK-SQLI/942100.yaml")
    execTest("942101", "REQUEST-942-APPLICATION-ATTACK-SQLI/942101.yaml")
    execTest("942120", "REQUEST-942-APPLICATION-ATTACK-SQLI/942120.yaml")
    execTest("942130", "REQUEST-942-APPLICATION-ATTACK-SQLI/942130.yaml")
    execTest("942131", "REQUEST-942-APPLICATION-ATTACK-SQLI/942131.yaml")
    execTest("942140", "REQUEST-942-APPLICATION-ATTACK-SQLI/942140.yaml")
    execTest("942150", "REQUEST-942-APPLICATION-ATTACK-SQLI/942150.yaml")
    execTest("942151", "REQUEST-942-APPLICATION-ATTACK-SQLI/942151.yaml")
    execTest("942152", "REQUEST-942-APPLICATION-ATTACK-SQLI/942152.yaml")
    execTest("942160", "REQUEST-942-APPLICATION-ATTACK-SQLI/942160.yaml")
    execTest("942170", "REQUEST-942-APPLICATION-ATTACK-SQLI/942170.yaml")
    execTest("942180", "REQUEST-942-APPLICATION-ATTACK-SQLI/942180.yaml")
    execTest("942190", "REQUEST-942-APPLICATION-ATTACK-SQLI/942190.yaml")
    execTest("942200", "REQUEST-942-APPLICATION-ATTACK-SQLI/942200.yaml")
    execTest("942210", "REQUEST-942-APPLICATION-ATTACK-SQLI/942210.yaml")
    execTest("942220", "REQUEST-942-APPLICATION-ATTACK-SQLI/942220.yaml")
    execTest("942230", "REQUEST-942-APPLICATION-ATTACK-SQLI/942230.yaml")
    execTest("942240", "REQUEST-942-APPLICATION-ATTACK-SQLI/942240.yaml")
    execTest("942250", "REQUEST-942-APPLICATION-ATTACK-SQLI/942250.yaml")
    execTest("942251", "REQUEST-942-APPLICATION-ATTACK-SQLI/942251.yaml")
    execTest("942260", "REQUEST-942-APPLICATION-ATTACK-SQLI/942260.yaml")
    execTest("942270", "REQUEST-942-APPLICATION-ATTACK-SQLI/942270.yaml")
    execTest("942280", "REQUEST-942-APPLICATION-ATTACK-SQLI/942280.yaml")
    execTest("942290", "REQUEST-942-APPLICATION-ATTACK-SQLI/942290.yaml")
    execTest("942300", "REQUEST-942-APPLICATION-ATTACK-SQLI/942300.yaml")
    execTest("942310", "REQUEST-942-APPLICATION-ATTACK-SQLI/942310.yaml")
    execTest("942320", "REQUEST-942-APPLICATION-ATTACK-SQLI/942320.yaml")
    execTest("942321", "REQUEST-942-APPLICATION-ATTACK-SQLI/942321.yaml")
    execTest("942330", "REQUEST-942-APPLICATION-ATTACK-SQLI/942330.yaml")
    execTest("942340", "REQUEST-942-APPLICATION-ATTACK-SQLI/942340.yaml")
    execTest("942350", "REQUEST-942-APPLICATION-ATTACK-SQLI/942350.yaml")
    execTest("942360", "REQUEST-942-APPLICATION-ATTACK-SQLI/942360.yaml")
    execTest("942361", "REQUEST-942-APPLICATION-ATTACK-SQLI/942361.yaml")
    execTest("942362", "REQUEST-942-APPLICATION-ATTACK-SQLI/942362.yaml")
    execTest("942370", "REQUEST-942-APPLICATION-ATTACK-SQLI/942370.yaml")
    execTest("942380", "REQUEST-942-APPLICATION-ATTACK-SQLI/942380.yaml")
    execTest("942390", "REQUEST-942-APPLICATION-ATTACK-SQLI/942390.yaml")
    execTest("942400", "REQUEST-942-APPLICATION-ATTACK-SQLI/942400.yaml")
    execTest("942410", "REQUEST-942-APPLICATION-ATTACK-SQLI/942410.yaml")
    execTest("942420", "REQUEST-942-APPLICATION-ATTACK-SQLI/942420.yaml")
    execTest("942421", "REQUEST-942-APPLICATION-ATTACK-SQLI/942421.yaml")
    execTest("942430", "REQUEST-942-APPLICATION-ATTACK-SQLI/942430.yaml")
    execTest("942431", "REQUEST-942-APPLICATION-ATTACK-SQLI/942431.yaml")
    execTest("942432", "REQUEST-942-APPLICATION-ATTACK-SQLI/942432.yaml")
    execTest("942440", "REQUEST-942-APPLICATION-ATTACK-SQLI/942440.yaml")
    execTest("942450", "REQUEST-942-APPLICATION-ATTACK-SQLI/942450.yaml")
    execTest("942460", "REQUEST-942-APPLICATION-ATTACK-SQLI/942460.yaml")
    execTest("942470", "REQUEST-942-APPLICATION-ATTACK-SQLI/942470.yaml")
    execTest("942480", "REQUEST-942-APPLICATION-ATTACK-SQLI/942480.yaml")
    execTest("942490", "REQUEST-942-APPLICATION-ATTACK-SQLI/942490.yaml")
    execTest("942500", "REQUEST-942-APPLICATION-ATTACK-SQLI/942500.yaml")
    execTest("942510", "REQUEST-942-APPLICATION-ATTACK-SQLI/942510.yaml")
    execTest("942511", "REQUEST-942-APPLICATION-ATTACK-SQLI/942511.yaml")
    execTest("942520", "REQUEST-942-APPLICATION-ATTACK-SQLI/942520.yaml")
    execTest("942521", "REQUEST-942-APPLICATION-ATTACK-SQLI/942521.yaml")
    execTest("942522", "REQUEST-942-APPLICATION-ATTACK-SQLI/942522.yaml")
    execTest("942530", "REQUEST-942-APPLICATION-ATTACK-SQLI/942530.yaml")
    execTest("942540", "REQUEST-942-APPLICATION-ATTACK-SQLI/942540.yaml")
    execTest("942550", "REQUEST-942-APPLICATION-ATTACK-SQLI/942550.yaml")
    execTest("942560", "REQUEST-942-APPLICATION-ATTACK-SQLI/942560.yaml")
  }
  test("REQUEST-943-APPLICATION-ATTACK-SESSION-FIXATION") {
    execTest("943100", "REQUEST-943-APPLICATION-ATTACK-SESSION-FIXATION/943100.yaml")
    execTest("943110", "REQUEST-943-APPLICATION-ATTACK-SESSION-FIXATION/943110.yaml")
    execTest("943120", "REQUEST-943-APPLICATION-ATTACK-SESSION-FIXATION/943120.yaml")
  }
  test("REQUEST-944-APPLICATION-ATTACK-JAVA") {
    execTest("944000", "REQUEST-944-APPLICATION-ATTACK-JAVA/944000.yaml")
    execTest("944100", "REQUEST-944-APPLICATION-ATTACK-JAVA/944100.yaml")
    execTest("944110", "REQUEST-944-APPLICATION-ATTACK-JAVA/944110.yaml")
    execTest("944120", "REQUEST-944-APPLICATION-ATTACK-JAVA/944120.yaml")
    execTest("944130", "REQUEST-944-APPLICATION-ATTACK-JAVA/944130.yaml")
    execTest("944140", "REQUEST-944-APPLICATION-ATTACK-JAVA/944140.yaml")
    execTest("944150", "REQUEST-944-APPLICATION-ATTACK-JAVA/944150.yaml")
    execTest("944151", "REQUEST-944-APPLICATION-ATTACK-JAVA/944151.yaml")
    execTest("944152", "REQUEST-944-APPLICATION-ATTACK-JAVA/944152.yaml")
    execTest("944200", "REQUEST-944-APPLICATION-ATTACK-JAVA/944200.yaml")
    execTest("944210", "REQUEST-944-APPLICATION-ATTACK-JAVA/944210.yaml")
    execTest("944240", "REQUEST-944-APPLICATION-ATTACK-JAVA/944240.yaml")
    execTest("944250", "REQUEST-944-APPLICATION-ATTACK-JAVA/944250.yaml")
    execTest("944260", "REQUEST-944-APPLICATION-ATTACK-JAVA/944260.yaml")
    execTest("944300", "REQUEST-944-APPLICATION-ATTACK-JAVA/944300.yaml")
  }
  test("REQUEST-949-BLOCKING-EVALUATION") {
    execTest("949110", "REQUEST-949-BLOCKING-EVALUATION/949110.yaml")
  }
  test("RESPONSE-950-DATA-LEAKAGES") {
    execTest("950150", "RESPONSE-950-DATA-LEAKAGES/950150.yaml")
  }
  test("RESPONSE-951-DATA-LEAKAGES-SQL") {
    execTest("951110", "RESPONSE-951-DATA-LEAKAGES-SQL/951110.yaml")
    execTest("951120", "RESPONSE-951-DATA-LEAKAGES-SQL/951120.yaml")
    execTest("951130", "RESPONSE-951-DATA-LEAKAGES-SQL/951130.yaml")
    execTest("951140", "RESPONSE-951-DATA-LEAKAGES-SQL/951140.yaml")
    execTest("951150", "RESPONSE-951-DATA-LEAKAGES-SQL/951150.yaml")
    execTest("951160", "RESPONSE-951-DATA-LEAKAGES-SQL/951160.yaml")
    execTest("951170", "RESPONSE-951-DATA-LEAKAGES-SQL/951170.yaml")
    execTest("951180", "RESPONSE-951-DATA-LEAKAGES-SQL/951180.yaml")
    execTest("951190", "RESPONSE-951-DATA-LEAKAGES-SQL/951190.yaml")
    execTest("951200", "RESPONSE-951-DATA-LEAKAGES-SQL/951200.yaml")
    execTest("951210", "RESPONSE-951-DATA-LEAKAGES-SQL/951210.yaml")
    execTest("951220", "RESPONSE-951-DATA-LEAKAGES-SQL/951220.yaml")
    execTest("951230", "RESPONSE-951-DATA-LEAKAGES-SQL/951230.yaml")
    execTest("951240", "RESPONSE-951-DATA-LEAKAGES-SQL/951240.yaml")
    execTest("951250", "RESPONSE-951-DATA-LEAKAGES-SQL/951250.yaml")
    execTest("951260", "RESPONSE-951-DATA-LEAKAGES-SQL/951260.yaml")
  }
  test("RESPONSE-952-DATA-LEAKAGES-JAVA") {
    execTest("952110", "RESPONSE-952-DATA-LEAKAGES-JAVA/952110.yaml")
  }
  test("RESPONSE-953-DATA-LEAKAGES-PHP") {
    execTest("953100", "RESPONSE-953-DATA-LEAKAGES-PHP/953100.yaml")
    execTest("953101", "RESPONSE-953-DATA-LEAKAGES-PHP/953101.yaml")
    execTest("953120", "RESPONSE-953-DATA-LEAKAGES-PHP/953120.yaml")
  }
  test("RESPONSE-954-DATA-LEAKAGES-IIS") {
    execTest("954100", "RESPONSE-954-DATA-LEAKAGES-IIS/954100.yaml")
    execTest("954101", "RESPONSE-954-DATA-LEAKAGES-IIS/954101.yaml")
    execTest("954120", "RESPONSE-954-DATA-LEAKAGES-IIS/954120.yaml")
  }
  test("RESPONSE-955-WEB-SHELLS") {
    execTest("955100", "RESPONSE-955-WEB-SHELLS/955100.yaml")
    execTest("955110", "RESPONSE-955-WEB-SHELLS/955110.yaml")
    execTest("955120", "RESPONSE-955-WEB-SHELLS/955120.yaml")
    execTest("955260", "RESPONSE-955-WEB-SHELLS/955260.yaml")
    execTest("955400", "RESPONSE-955-WEB-SHELLS/955400.yaml")
  }
  test("RESPONSE-956-DATA-LEAKAGES-RUBY") {
    execTest("956100", "RESPONSE-956-DATA-LEAKAGES-RUBY/956100.yaml")
    execTest("956110", "RESPONSE-956-DATA-LEAKAGES-RUBY/956110.yaml")
  }
  test("RESPONSE-959-BLOCKING-EVALUATION") {
    execTest("959100", "RESPONSE-959-BLOCKING-EVALUATION/959100.yaml")
  }
  test("RESPONSE-980-CORRELATION") {
    execTest("980170", "RESPONSE-980-CORRELATION/980170.yaml")
  }
  test("display results") {
    writeStats()
  }
}