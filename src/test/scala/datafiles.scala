package com.cloud.apim.seclang.test

import com.cloud.apim.seclang.model.Disposition._
import com.cloud.apim.seclang.model._
import com.cloud.apim.seclang.scaladsl.SecLang

/**
 * A preset scanned from a directory or the classpath keys its data files by their path under its
 * root, while its rules name them bare: every `*FromFile` operator has to find them anyway.
 */
class SecLangDataFilesTest extends munit.FunSuite {

  private def engine(rules: String, files: Map[String, String]) = {
    val loaded = SecLang.parse(rules).fold(err => throw err.throwable, identity)
    SecLang.engine(SecLang.compile(loaded), files = files, integration = new NoLogSecLangIntegration())
  }

  private def get(q: String, ip: String = "1.2.3.4") =
    RequestContext(method = "GET", uri = s"/?q=$q", query = Map("q" -> List(q)), remoteAddr = ip)

  private val words = "# a comment\nattack\nexploit\n"

  test("a bare name finds a file keyed by its path, as the embedded CRS keys them") {
    val waf = engine(
      """
        |SecRule ARGS "@pmFromFile words.data" "id:1,phase:1,deny,status:403,msg:'listed word'"
        |SecRuleEngine On
        |""".stripMargin,
      Map("/rules/words.data" -> words)
    )
    assertEquals(waf.evaluate(get("an-attack")).disposition, Block(403, Some("listed word"), Some(1)))
    assertEquals(waf.evaluate(get("hello")).disposition, Continue)
  }

  test("ipMatchFromFile resolves the same way") {
    val waf = engine(
      """
        |SecRule REMOTE_ADDR "@ipMatchFromFile blocked.data" "id:2,phase:1,deny,status:403,msg:'blocked'"
        |SecRuleEngine On
        |""".stripMargin,
      Map("/rules/blocked.data" -> "10.0.0.0/8\n")
    )
    assertEquals(waf.evaluate(get("x", "10.1.2.3")).disposition, Block(403, Some("blocked"), Some(2)))
    assertEquals(waf.evaluate(get("x", "1.2.3.4")).disposition, Continue)
  }

  test("@ipMatch matches an address inside a CIDR, not only an exact one") {
    val waf = engine(
      """
        |SecRule REMOTE_ADDR "@ipMatch 10.0.0.0/8, 192.168.1.10" "id:6,phase:1,deny,status:403"
        |SecRuleEngine On
        |""".stripMargin,
      Map.empty
    )
    assertEquals(waf.evaluate(get("x", "10.1.2.3")).disposition, Block(403, None, Some(6)))
    assertEquals(waf.evaluate(get("x", "192.168.1.10")).disposition, Block(403, None, Some(6)))
    assertEquals(waf.evaluate(get("x", "192.168.1.11")).disposition, Continue)
  }

  test("a name two files share is not guessed, and a full path still works") {
    val files = Map("/a/words.data" -> words, "/b/words.data" -> "nothing\n")
    val bare  = engine("""SecRule ARGS "@pmFromFile words.data" "id:3,phase:1,deny,status:403"
                         |SecRuleEngine On""".stripMargin, files)
    assertEquals(bare.evaluate(get("attack")).disposition, Continue)
    val full  = engine("""SecRule ARGS "@pmFromFile /a/words.data" "id:4,phase:1,deny,status:403"
                         |SecRuleEngine On""".stripMargin, files)
    assertEquals(full.evaluate(get("attack")).disposition, Block(403, None, Some(4)))
  }

  test("a file keyed by its bare name is found as before") {
    val waf = engine("""SecRule ARGS "@pmFromFile words.data" "id:5,phase:1,deny,status:403"
                       |SecRuleEngine On""".stripMargin, Map("words.data" -> words))
    assertEquals(waf.evaluate(get("exploit")).disposition, Block(403, None, Some(5)))
  }
}
