package com.cloud.apim.seclang.test

import com.cloud.apim.seclang.model.Disposition._
import com.cloud.apim.seclang.model._
import com.cloud.apim.seclang.scaladsl.SecLang

/**
 * The five ways a rule can be told to stop matching something.
 *
 * They are the primitives a tuning assistant generates, which makes silent breakage in them worse
 * than in most of the engine: an exclusion that quietly does nothing leaves an operator convinced
 * they fixed a false positive, and an exclusion that quietly does too much leaves a hole nobody
 * asked for. Both directions are asserted here — what stops matching, and what must keep matching.
 */
class SecLangExclusionTest extends munit.FunSuite {

  private val rule =
    """SecRule ARGS "@rx select" "id:7001,phase:2,deny,status:403,log,msg:'own',tag:'attack-sqli'""""

  private def ctx(args: Map[String, List[String]]) = RequestContext(
    method = "GET",
    uri = "/search",
    headers = Headers(Map("Host" -> List("example.com"))),
    cookies = Map.empty,
    query = args,
    body = None,
    status = None,
    statusTxt = None,
    remoteAddr = "1.2.3.4",
    remotePort = 1234,
    protocol = "http/1.1"
  )

  private def blocks(rules: List[String], args: Map[String, List[String]]): Boolean = {
    val engine = SecLang.factory(Map.empty, SecLangEngineConfig.default, DefaultNoCacheSecLangIntegration.default)
      .engine("SecRuleEngine On" :: rules)
    engine.evaluate(ctx(args), List(1, 2, 5)).disposition match {
      case _: Block => true
      case Continue => false
    }
  }

  private val hit  = Map("comment" -> List("select 1"))
  private val other = Map("q" -> List("select 1"))

  test("the rule blocks with no exclusion at all") {
    assert(blocks(List(rule), hit))
    assert(blocks(List(rule), other))
  }

  // -----------------------------------------------------------------------------------------------
  // SecRuleUpdateTargetById — the surgical one, and the one a tuning assistant reaches for first
  // -----------------------------------------------------------------------------------------------

  test("SecRuleUpdateTargetById excludes the named parameter") {
    assert(!blocks(List(rule, """SecRuleUpdateTargetById 7001 "!ARGS:comment""""), hit))
  }

  test("SecRuleUpdateTargetById leaves every other parameter alone") {
    assert(blocks(List(rule, """SecRuleUpdateTargetById 7001 "!ARGS:comment""""), other))
  }

  test("SecRuleUpdateTargetByTag excludes the named parameter") {
    assert(!blocks(List(rule, """SecRuleUpdateTargetByTag "attack-sqli" "!ARGS:comment""""), hit))
  }

  test("an exclusion aimed at another rule id changes nothing") {
    assert(blocks(List(rule, """SecRuleUpdateTargetById 7999 "!ARGS:comment""""), hit))
  }

  // -----------------------------------------------------------------------------------------------
  // SecRuleRemoveById / ByTag — the blunt ones
  // -----------------------------------------------------------------------------------------------

  test("SecRuleRemoveById drops the rule everywhere") {
    assert(!blocks(List(rule, "SecRuleRemoveById 7001"), hit))
    assert(!blocks(List(rule, "SecRuleRemoveById 7001"), other))
  }

  test("SecRuleRemoveByTag drops every rule carrying the tag") {
    assert(!blocks(List(rule, """SecRuleRemoveByTag "attack-sqli""""), hit))
  }

  // -----------------------------------------------------------------------------------------------
  // ctl: — the runtime siblings, scoped by whatever condition carries them
  // -----------------------------------------------------------------------------------------------

  private def scoped(ctl: String) =
    s"""SecRule REQUEST_URI "@beginsWith /search" "id:50001,phase:1,pass,nolog,$ctl""""

  private def scopedElsewhere(ctl: String) =
    s"""SecRule REQUEST_URI "@beginsWith /nowhere" "id:50002,phase:1,pass,nolog,$ctl""""

  test("ctl:ruleRemoveTargetById excludes the named parameter") {
    assert(!blocks(List(rule, scoped("ctl:ruleRemoveTargetById=7001;ARGS:comment")), hit))
  }

  test("ctl:ruleRemoveTargetById leaves every other parameter alone") {
    assert(blocks(List(rule, scoped("ctl:ruleRemoveTargetById=7001;ARGS:comment")), other))
  }

  test("ctl:ruleRemoveTargetById does nothing when its condition does not hold") {
    assert(blocks(List(rule, scopedElsewhere("ctl:ruleRemoveTargetById=7001;ARGS:comment")), hit))
  }

  test("ctl:ruleRemoveTargetById on a whole collection drops the variable") {
    assert(!blocks(List(rule, scoped("ctl:ruleRemoveTargetById=7001;ARGS")), hit))
    assert(!blocks(List(rule, scoped("ctl:ruleRemoveTargetById=7001;ARGS")), other))
  }

  test("ctl:ruleRemoveByTag disables the rule for this request only") {
    assert(!blocks(List(rule, scoped("ctl:ruleRemoveByTag=attack-sqli")), hit))
    assert(blocks(List(rule, scopedElsewhere("ctl:ruleRemoveByTag=attack-sqli")), hit))
  }

  test("ctl:ruleRemoveById still works") {
    assert(!blocks(List(rule, scoped("ctl:ruleRemoveById=7001")), hit))
    assert(blocks(List(rule, scopedElsewhere("ctl:ruleRemoveById=7001")), hit))
  }

  // -----------------------------------------------------------------------------------------------
  // parsing
  // -----------------------------------------------------------------------------------------------

  test("a quoted exclusion does not carry its closing quote into the variable name") {
    SecLang.parse("""SecRuleUpdateTargetById 7001 "!ARGS:comment"""") match {
      case Left(err) => fail("did not parse: " + err.msg)
      case Right(conf) =>
        val stmt = conf.statements.collectFirst { case s: SecRuleUpdateTargetById => s }.get
        assertEquals(stmt.negatedVariables.variables, List(Variable.Collection("ARGS", Some("comment"))))
        assertEquals(stmt.variables.variables, List.empty[Variable])
    }
  }

  test("a mixed update adds one target and excludes another") {
    SecLang.parse("""SecRuleUpdateTargetById 7001 "ARGS:keep,!ARGS:drop"""") match {
      case Left(err) => fail("did not parse: " + err.msg)
      case Right(conf) =>
        val stmt = conf.statements.collectFirst { case s: SecRuleUpdateTargetById => s }.get
        assertEquals(stmt.variables.variables, List(Variable.Collection("ARGS", Some("keep"))))
        assertEquals(stmt.negatedVariables.variables, List(Variable.Collection("ARGS", Some("drop"))))
    }
  }

  // -----------------------------------------------------------------------------------------------
  // against the real CRS, composed the way a deployment composes it: the ruleset is one entry and
  // the tuning is another, so they are compiled apart and only meet at evaluation
  // -----------------------------------------------------------------------------------------------

  private lazy val crsRules: String = { CRSTestUtils.setupCRSEngine(List.empty); CRSTestUtils.crsRulesText }

  private def crsBlocksOn(arg: String, extra: List[String]): Boolean = {
    val engine = SecLang.factory(Map.empty, SecLangEngineConfig.test, DefaultNoCacheSecLangIntegration.default)
      .engine(crsRules :: extra ::: List("SecRuleEngine On"))
    val res = engine.evaluate(
      ctx(Map(arg -> List("1' or 1=1--"))).copy(uri = "/search"),
      List(1, 2, 5)
    )
    res.events.flatMap(_.ruleId).contains(942100)
  }

  test("CRS 942100 fires on the parameter with no exclusion") {
    assert(crsBlocksOn("comment", Nil))
  }

  test("SecRuleUpdateTargetById reaches a rule that lives in another compilation unit") {
    assert(!crsBlocksOn("comment", List("""SecRuleUpdateTargetById 942100 "!ARGS:comment"""")))
  }

  test("and leaves the same rule armed for every other parameter") {
    assert(crsBlocksOn("q", List("""SecRuleUpdateTargetById 942100 "!ARGS:comment"""")))
  }

  test("ctl:ruleRemoveTargetById reaches CRS too, scoped to its condition") {
    val scopedToSearch =
      """SecRule REQUEST_URI "@beginsWith /search" "id:50010,phase:1,pass,nolog,ctl:ruleRemoveTargetById=942100;ARGS:comment""""
    assert(!crsBlocksOn("comment", List(scopedToSearch)))
    assert(crsBlocksOn("q", List(scopedToSearch)))
  }

  test("MATCHED_VAR_NAME names the parameter that matched") {
    val engine = SecLang.factory(Map.empty, SecLangEngineConfig.test, DefaultNoCacheSecLangIntegration.default)
      .engine(List(crsRules, "SecRuleEngine On"))
    val res = engine.evaluate(ctx(Map("comment" -> List("1' or 1=1--"))).copy(uri = "/search"), List(1, 2, 5))
    val logs = res.events.filter(_.ruleId.contains(942100)).flatMap(_.logs).mkString(" ")
    assert(logs.contains("ARGS:comment"), s"expected the matched parameter to be named, got: $logs")
  }
}
