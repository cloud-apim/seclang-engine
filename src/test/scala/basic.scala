package com.cloud.apim.seclang.test

import com.cloud.apim.seclang.impl.model.Disposition._
import com.cloud.apim.seclang.impl.model._
import com.cloud.apim.seclang.scaladsl.SecLang

class SecLangBasicTest extends munit.FunSuite {
  test("simple rule") {

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
    SecLang.parseAntlr(rules)
    /*
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
    */
  }
}