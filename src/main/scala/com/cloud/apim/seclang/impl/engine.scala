
package com.cloud.apim.seclang.impl.engine

import com.cloud.apim.seclang.impl.compiler._
import com.cloud.apim.seclang.model.Action.{CtlAction, Msg}
import com.cloud.apim.seclang.model._
import play.api.libs.json.Json

import java.net.{URLDecoder, URLEncoder}
import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.collection.concurrent.TrieMap
import scala.util.matching.Regex

private final case class RuntimeState(disabledIds: Set[Int], events: List[MatchEvent])

case class SecRulesEngineConfig()

object SecRulesEngineConfig {
  val default = SecRulesEngineConfig()
}

final class SecRulesEngine(program: CompiledProgram, files: Map[String, String] = Map.empty, config: SecRulesEngineConfig = SecRulesEngineConfig.default) {

  private val txMap = new TrieMap[String, String]()

  // println("new engine config: " + program.itemsByPhase.toSeq.flatMap(_._2).size)

  // program.itemsByPhase.toSeq.foreach(_._2.foreach(ci => ci.asInstanceOf[RuleChain].rules.foreach(r => println(Json.prettyPrint(r.json)))))

  // runtime disables (ctl:ruleRemoveById)
  def evaluate(ctx: RequestContext, phases: List[Int] = List(1, 2)): EngineResult = {
    val init = RuntimeState(Set.empty, Nil)
    val (disp, st) = phases.foldLeft((Disposition.Continue: Disposition, init)) {
      case ((Disposition.Block(_,_,_), st), _) => (Disposition.Block(403, None, None), st) // already blocked, keep
      case ((Disposition.Continue, st), ph) =>
        val (d2, st2) = runPhase(ph, ctx, st)
        (d2, st2)
    }
    EngineResult(disp, st.events.reverse)
  }

  private def runPhase(phase: Int, ctx: RequestContext, st0: RuntimeState): (Disposition, RuntimeState) = {
    val items = program.itemsByPhase.getOrElse(phase, Vector.empty)

    // println(s"running phase ${phase} with ${items.size} items")
    // build marker index for this phase stream
    val markerIndex: Map[String, Int] = items.zipWithIndex.collect {
      case (MarkerItem(name), idx) => name -> idx
    }.toMap

    var i = 0
    var st = st0

    while (i < items.length) {
      items(i) match {
        // TODO: handle all needed statements
        case ActionItem(action) =>
          val (matched, stAfterMatch, skipToIdxOpt, dispOpt) = evalAction(action, phase, ctx, st, markerIndex)
          st = stAfterMatch
          dispOpt match {
            case Some(d) => return (d, st)
            case None =>
              skipToIdxOpt match {
                case Some(j) => i = j
                case None    => i += 1
              }
          }
        case MarkerItem(_) =>
          i += 1
        case RuleChain(rules) =>
          // if rule id disabled runtime, skip
          val chainId = rules.last.id.orElse(rules.head.id)
          val ridDisabled = chainId.exists(st.disabledIds.contains) || chainId.exists(program.removedRuleIds.contains)

          if (ridDisabled) {
            i += 1
          } else {
            val (matched, stAfterMatch, skipToIdxOpt, dispOpt) =
              evalChain(rules, phase, ctx, st, markerIndex)

            st = stAfterMatch

            dispOpt match {
              case Some(d) => return (d, st)
              case None =>
                skipToIdxOpt match {
                  case Some(j) => i = j
                  case None    => i += 1
                }
            }
          }
      }
    }

    (Disposition.Continue, st)
  }

  // (?i) => case-insensitive
  private val TxExpr: Regex = """(?i)%\{tx\.([a-z0-9_.-]+)\}""".r

  private def evalTxExpressions(input: String): String = {
    if (input.contains("%{")) {
      TxExpr.replaceAllIn(input, m => {
        val key = m.group(1).toLowerCase
        txMap.getOrElse(key, m.matched)
      })
    } else {
      input
    }
  }

  private def performActions(actions: List[Action], i: Int, context: RequestContext, state: RuntimeState): RuntimeState = {
    // TODO: implement it
    actions.foreach {
      case Action.SetVar(expr) => {
        evalTxExpressions(expr) match {
          case expr if expr.startsWith("!") => {
            val name = expr.substring(1).replace("tx.", "").replace("TX.", "").toLowerCase()
            txMap.remove(name)
          }
          case expr if expr.contains("+=") => {
            val ex = expr.replace("tx.", "").replace("TX.", "").toLowerCase()
            val parts = ex.split("=")
            val name = parts(0)
            val incr = parts(1).toInt
            val value = txMap.get(name).map(_.toInt).getOrElse(0)
            txMap.put(name, (value + incr).toString)
          }
          case expr if expr.contains("-=") => {
            val ex = expr.replace("tx.", "").replace("TX.", "").toLowerCase()
            val parts = ex.split("=")
            val name = parts(0)
            val decr = parts(1).toInt
            val value = txMap.get(name).map(_.toInt).getOrElse(0)
            txMap.put(name, (value - decr).toString)
          }
          case expr if expr.contains("=") => {
            val ex = expr.replace("tx.", "").replace("TX.", "").toLowerCase()
            val parts = ex.split("=")
            val name = parts(0)
            val value = parts(1)
            txMap.put(name, value)
          }
          case expr if !expr.contains("=") => {
            val name = expr.replace("tx.", "").replace("TX.", "").toLowerCase()
            txMap.put(name, "0")
          }
          case expr => println("invalid setvar expression: " + expr)
        }
      }
      case _ =>
    }
    state
  }

  private def evalAction(
    action: SecAction,
    phase: Int,
    ctx: RequestContext,
    st0: RuntimeState,
    markerIndex: Map[String, Int]
  ): (Boolean, RuntimeState, Option[Int], Option[Disposition]) = {
    var st = st0
    var collectedMsg: Option[String] = None
    var collectedStatus: Option[Int] = None
    var disruptive: Option[Action] = None
    var skipAfter: Option[String] = None
    var lastRuleId: Option[Int] = None
    val actionsList = action.actions.actions.toList

    val msg = actionsList.collectFirst {
      case Action.Msg(m) => m
    }
    if (msg.nonEmpty) collectedMsg = msg

    val status = actionsList.collectFirst {
      case Action.Status(s) => s
    }
    if (status.nonEmpty) collectedStatus = status

    // disruptive action can appear anywhere, but we keep last
    val dis = actionsList.collectFirst {
      case Action.Block() => Action.Block()
      case Action.Deny => Action.Deny
      case Action.Drop => Action.Drop
      case Action.Pass => Action.Pass
      case Action.Allow(m) => Action.Allow(m)
    }
    if (dis.nonEmpty) disruptive = dis

    // skipAfter
    val sk = actionsList.collectFirst { case Action.SkipAfter(m) => m }
    if (sk.nonEmpty) skipAfter = sk

    // runtime ctl disable
    actionsList.collect { case CtlAction.RuleRemoveById(id) => id }.foreach { id =>
      st = st.copy(disabledIds = st.disabledIds + id)
    }

    st = st.copy(events = MatchEvent(action.id, msg, phase, Json.stringify(action.json)) :: st.events)
    st = performActions(actionsList, phase, ctx, st)
    val disp =
      disruptive match {
        case Some(Action.Deny) | Some(Action.Drop) | Some(Action.Block()) =>
          Some(Disposition.Block(
            status = collectedStatus.getOrElse(403),
            msg = collectedMsg,
            ruleId = lastRuleId
          ))
        case _ => None
      }

    val skipIdx = skipAfter.flatMap(markerIndex.get).map(_ + 1)

    (true, st, skipIdx, disp)
  }

  private def evalChain(
      rules: List[SecRule],
      phase: Int,
      ctx: RequestContext,
      st0: RuntimeState,
      markerIndex: Map[String, Int]
  ): (Boolean, RuntimeState, Option[Int], Option[Disposition]) = {

    var st = st0
    var allMatched = true
    var collectedMsg: Option[String] = None
    var collectedStatus: Option[Int] = None
    var disruptive: Option[Action] = None
    var skipAfter: Option[String] = None
    var lastRuleId: Option[Int] = None

    // Evaluate sequentially; if one fails -> chain fails
    rules.foreach { r =>
      lastRuleId = r.id.orElse(lastRuleId)
      if (allMatched) {
        val matched = evalRule(r, ctx, debug = false)
        // TODO: implement actions: https://github.com/owasp-modsecurity/ModSecurity/wiki/Reference-Manual-%28v3.x%29#actions
        if (matched) {
          val actionsList = r.actions.toList.flatMap(_.actions)
          val msg = actionsList.collectFirst {
            case Action.Msg(m) => m
          }
          if (msg.nonEmpty) collectedMsg = msg

          val status = actionsList.collectFirst {
            case Action.Status(s) => s
          }
          if (status.nonEmpty) collectedStatus = status

          // disruptive action can appear anywhere, but we keep last
          val dis = actionsList.collectFirst {
            case Action.Block() => Action.Block()
            case Action.Deny => Action.Deny
            case Action.Drop => Action.Drop
            case Action.Pass => Action.Pass
            case Action.Allow(m) => Action.Allow(m)
          }
          if (dis.nonEmpty) disruptive = dis

          // skipAfter
          val sk = actionsList.collectFirst { case Action.SkipAfter(m) => m }
          if (sk.nonEmpty) skipAfter = sk

          // runtime ctl disable
          actionsList.collect { case CtlAction.RuleRemoveById(id) => id }.foreach { id =>
            st = st.copy(disabledIds = st.disabledIds + id)
          }

          st = st.copy(events = MatchEvent(r.id, msg, phase, Json.stringify(r.json)) :: st.events)
          st = performActions(actionsList, phase, ctx, st)
        } else {
          allMatched = false
        }
      }
    }

    if (!allMatched) {
      (false, st0.copy(disabledIds = st.disabledIds, events = st.events), None, None)
    } else {
      val disp =
        disruptive match {
          case Some(Action.Deny) | Some(Action.Drop) | Some(Action.Block()) =>
            Some(Disposition.Block(
              status = collectedStatus.getOrElse(403),
              msg = collectedMsg,
              ruleId = lastRuleId
            ))
          case _ => None
        }

      val skipIdx = skipAfter.flatMap(markerIndex.get).map(_ + 1)

      (true, st, skipIdx, disp)
    }
  }

  private def evalRule(rule: SecRule, ctx: RequestContext, debug: Boolean): Boolean = {
    // 1) extract values from variables
    val extracted: List[String] = {
      val vrbls = rule.variables.variables.flatMap(v => resolveVariable(v, rule.variables.count, rule.variables.negated, ctx))
      if (rule.variables.count) {
        vrbls.size.toString :: Nil
      } else {
        vrbls
      }
    }
    // 2) apply transformations
    val transforms = rule.actions.toList.flatMap(_.actions).toList.collect { case Action.Transform(name) => name }.filterNot(_ == "none")
    val transformed = extracted.map(v => applyTransforms(v, transforms))
    // 3) operator match on ANY extracted value
    val matched = transformed.exists(v => evalOperator(rule.operator, v))
    matched
  }

  private def unimplementedVariable(name: String): List[String] = {
    println("unimplemented variable: " + name)
    Nil
  }

  private def unsupportedVariable(name: String): List[String] = {
    println("unsupported variable: " + name)
    Nil
  }

  private def unsupportedV3Variable(name: String): List[String] = {
    println("unsupported variable in V3: " + name)
    Nil
  }

  private def resolveVariable(sel: Variable, count: Boolean, negated: Boolean, ctx: RequestContext): List[String] = {
    // https://github.com/owasp-modsecurity/ModSecurity/wiki/Reference-Manual-%28v3.x%29#user-content-Variables
    val (col, key) = sel match {
      case Variable.Simple(name) => (name, None)
      case Variable.Collection(collection, key) => (collection, key.map(_.toLowerCase()))
    }

    col match {
      case "REQUEST_URI" => List(ctx.uri)
      case "REQUEST_METHOD" => List(ctx.method)
      case "REQUEST_HEADERS" => {
        key match {
          case None =>
            ctx.headers.toList.flatMap { case (k, vs) => vs.map(v => s"$k: $v") }
          case Some(h) =>
            ctx.headers.collect {
              case (k, vs) if k.toLowerCase == h => vs
            }.flatten.toList
        }
      }
      case "ARGS" => { // TODO: fix it, should also contains body values if form submitted
        key match {
          case None =>
            ctx.query.values.flatten.toList
          case Some(k) =>
            ctx.query.collect { case (kk, vs) if kk.toLowerCase == k => vs }.flatten.toList
        }
      }
      case "TX" => {
        key match {
          case None => List.empty
          case Some(key) => txMap.get(key.toLowerCase()).toList
        }
      }
      case "REQUEST_BODY" => ctx.body.toList
      case "REQUEST_HEADERS_NAMES" => ctx.headers.keySet.toList
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      case "ARGS_COMBINED_SIZE" => unimplementedVariable("ARGS_COMBINED_SIZE") // TODO: implement it
      case "ARGS_GET" => unimplementedVariable("ARGS_GET") // TODO: implement it
      case "ARGS_GET_NAMES" => unimplementedVariable("ARGS_GET_NAMES") // TODO: implement it
      case "ARGS_NAMES" => unimplementedVariable("ARGS_NAMES") // TODO: implement it
      case "ARGS_POST" => unimplementedVariable("ARGS_POST") // TODO: implement it
      case "ARGS_POST_NAMES" => unimplementedVariable("ARGS_POST_NAMES") // TODO: implement it
      case "AUTH_TYPE" => unimplementedVariable("AUTH_TYPE") // TODO: implement it
      case "DURATION" => unimplementedVariable("DURATION") // TODO: implement it
      case "ENV" => unimplementedVariable("ENV") // TODO: implement it
      case "FILES" => unimplementedVariable("FILES") // TODO: implement it
      case "FILES_COMBINED_SIZE" => unimplementedVariable("FILES_COMBINED_SIZE") // TODO: implement it
      case "FILES_NAMES" => unimplementedVariable("FILES_NAMES") // TODO: implement it
      case "FULL_REQUEST" => unimplementedVariable("FULL_REQUEST") // TODO: implement it
      case "FULL_REQUEST_LENGTH" => unimplementedVariable("FULL_REQUEST_LENGTH") // TODO: implement it
      case "FILES_SIZES" => unimplementedVariable("FILES_SIZES") // TODO: implement it
      case "FILES_TMPNAMES" => unimplementedVariable("FILES_TMPNAMES") // TODO: implement it
      case "FILES_TMP_CONTENT" => unimplementedVariable("FILES_TMP_CONTENT") // TODO: implement it
      case "GEO" => unimplementedVariable("GEO") // TODO: implement it
      case "HIGHEST_SEVERITY" => unimplementedVariable("HIGHEST_SEVERITY") // TODO: implement it
      case "INBOUND_DATA_ERROR" => unimplementedVariable("INBOUND_DATA_ERROR") // TODO: implement it
      case "MATCHED_VAR" => unimplementedVariable("MATCHED_VAR") // TODO: implement it
      case "MATCHED_VARS" => unimplementedVariable("MATCHED_VARS") // TODO: implement it
      case "MATCHED_VAR_NAME" => unimplementedVariable("MATCHED_VAR_NAME") // TODO: implement it
      case "MATCHED_VARS_NAMES" => unimplementedVariable("MATCHED_VARS_NAMES") // TODO: implement it
      case "MODSEC_BUILD" => unimplementedVariable("MODSEC_BUILD") // TODO: implement it
      case "MSC_PCRE_LIMITS_EXCEEDED" => unimplementedVariable("MSC_PCRE_LIMITS_EXCEEDED") // TODO: implement it
      case "MULTIPART_CRLF_LF_LINES" => unimplementedVariable("MULTIPART_CRLF_LF_LINES") // TODO: implement it
      case "MULTIPART_FILENAME" => unimplementedVariable("MULTIPART_FILENAME") // TODO: implement it
      case "MULTIPART_NAME" => unimplementedVariable("MULTIPART_NAME") // TODO: implement it
      case "MULTIPART_PART_HEADERS" => unimplementedVariable("MULTIPART_PART_HEADERS") // TODO: implement it
      case "MULTIPART_STRICT_ERROR" => unimplementedVariable("MULTIPART_STRICT_ERROR") // TODO: implement it
      case "MULTIPART_UNMATCHED_BOUNDARY" => unimplementedVariable("MULTIPART_UNMATCHED_BOUNDARY") // TODO: implement it
      case "OUTBOUND_DATA_ERROR" => unimplementedVariable("OUTBOUND_DATA_ERROR") // TODO: implement it
      case "PATH_INFO" => unimplementedVariable("PATH_INFO") // TODO: implement it
      case "QUERY_STRING" => unimplementedVariable("QUERY_STRING") // TODO: implement it
      case "REMOTE_ADDR" => unimplementedVariable("REMOTE_ADDR") // TODO: implement it
      case "REMOTE_HOST" => unimplementedVariable("REMOTE_HOST") // TODO: implement it
      case "REMOTE_PORT" => unimplementedVariable("REMOTE_PORT") // TODO: implement it
      case "REMOTE_USER" => unimplementedVariable("REMOTE_USER") // TODO: implement it
      case "REQBODY_ERROR" => unimplementedVariable("REQBODY_ERROR") // TODO: implement it
      case "REQBODY_ERROR_MSG" => unimplementedVariable("REQBODY_ERROR_MSG") // TODO: implement it
      case "REQBODY_PROCESSOR" => unimplementedVariable("REQBODY_PROCESSOR") // TODO: implement it
      case "REQUEST_BASENAME" => unimplementedVariable("REQUEST_BASENAME") // TODO: implement it
      case "REQUEST_BODY_LENGTH" => unimplementedVariable("REQUEST_BODY_LENGTH") // TODO: implement it
      case "REQUEST_COOKIES" => unimplementedVariable("REQUEST_COOKIES") // TODO: implement it
      case "REQUEST_COOKIES_NAMES" => unimplementedVariable("REQUEST_COOKIES_NAMES") // TODO: implement it
      case "REQUEST_FILENAME" => unimplementedVariable("REQUEST_FILENAME") // TODO: implement it
      case "REQUEST_LINE" => unimplementedVariable("REQUEST_LINE") // TODO: implement it
      case "REQUEST_PROTOCOL" => unimplementedVariable("REQUEST_PROTOCOL") // TODO: implement it
      case "REQUEST_URI_RAW" => unimplementedVariable("REQUEST_URI_RAW") // TODO: implement it
      case "RESPONSE_CONTENT_LENGTH" => unimplementedVariable("RESPONSE_CONTENT_LENGTH") // TODO: implement it
      case "RESPONSE_CONTENT_TYPE" => unimplementedVariable("RESPONSE_CONTENT_TYPE") // TODO: implement it
      case "RESPONSE_HEADERS" => unimplementedVariable("RESPONSE_HEADERS") // TODO: implement it
      case "RESPONSE_HEADERS_NAMES" => unimplementedVariable("RESPONSE_HEADERS_NAMES") // TODO: implement it
      case "RESPONSE_PROTOCOL" => unimplementedVariable("RESPONSE_PROTOCOL") // TODO: implement it
      case "RESPONSE_STATUS" => unimplementedVariable("RESPONSE_STATUS") // TODO: implement it
      case "RULE" => unimplementedVariable("RULE") // TODO: implement it
      case "SDBM_DELETE_ERROR" => unimplementedVariable("SDBM_DELETE_ERROR") // TODO: implement it
      case "SERVER_ADDR" => unimplementedVariable("SERVER_ADDR") // TODO: implement it
      case "SERVER_NAME" => unimplementedVariable("SERVER_NAME") // TODO: implement it
      case "SERVER_PORT" => unimplementedVariable("SERVER_PORT") // TODO: implement it
      case "SESSION" => unimplementedVariable("SESSION") // TODO: implement it
      case "SESSIONID" => unimplementedVariable("SESSIONID") // TODO: implement it
      case "STATUS_LINE" => unimplementedVariable("STATUS_LINE") // TODO: implement it
      case "TIME" => unimplementedVariable("TIME") // TODO: implement it
      case "TIME_DAY" => unimplementedVariable("TIME_DAY") // TODO: implement it
      case "TIME_EPOCH" => unimplementedVariable("TIME_EPOCH") // TODO: implement it
      case "TIME_HOUR" => unimplementedVariable("TIME_HOUR") // TODO: implement it
      case "TIME_MIN" => unimplementedVariable("TIME_MIN") // TODO: implement it
      case "TIME_MON" => unimplementedVariable("TIME_MON") // TODO: implement it
      case "TIME_SEC" => unimplementedVariable("TIME_SEC") // TODO: implement it
      case "TIME_WDAY" => unimplementedVariable("TIME_WDAY") // TODO: implement it
      case "TIME_YEAR" => unimplementedVariable("TIME_YEAR") // TODO: implement it
      case "UNIQUE_ID" => unimplementedVariable("UNIQUE_ID") // TODO: implement it
      case "URLENCODED_ERROR" => unimplementedVariable("URLENCODED_ERROR") // TODO: implement it
      case "USERID" => unimplementedVariable("USERID") // TODO: implement it
      case "WEBAPPID" => unimplementedVariable("WEBAPPID") // TODO: implement it
      case "XML" => unimplementedVariable("XML") // TODO: implement it
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      case "PERF_ALL" => unsupportedV3Variable("PERF_ALL")
      case "PERF_COMBINED" => unsupportedV3Variable("PERF_COMBINED")
      case "PERF_GC" => unsupportedV3Variable("PERF_GC")
      case "PERF_LOGGING" => unsupportedV3Variable("PERF_LOGGING")
      case "PERF_PHASE1" => unsupportedV3Variable("PERF_PHASE1")
      case "PERF_PHASE2" => unsupportedV3Variable("PERF_PHASE2")
      case "PERF_PHASE3" => unsupportedV3Variable("PERF_PHASE3")
      case "PERF_PHASE4" => unsupportedV3Variable("PERF_PHASE4")
      case "PERF_PHASE5" => unsupportedV3Variable("PERF_PHASE5")
      case "PERF_RULES" => unsupportedV3Variable("PERF_RULES")
      case "PERF_SREAD" => unsupportedV3Variable("PERF_SREAD")
      case "PERF_SWRITE" => unsupportedV3Variable("PERF_SWRITE")
      case "SCRIPT_BASENAME" => unsupportedV3Variable("SCRIPT_BASENAME")
      case "SCRIPT_FILENAME" => unsupportedV3Variable("SCRIPT_FILENAME")
      case "SCRIPT_GID" => unsupportedV3Variable("SCRIPT_GID")
      case "SCRIPT_GROUPNAME" => unsupportedV3Variable("SCRIPT_GROUPNAME")
      case "SCRIPT_MODE" => unsupportedV3Variable("SCRIPT_MODE")
      case "SCRIPT_UID" => unsupportedV3Variable("SCRIPT_UID")
      case "SCRIPT_USERNAME" => unsupportedV3Variable("SCRIPT_USERNAME")
      case "STREAM_INPUT_BODY" => unsupportedV3Variable("STREAM_INPUT_BODY")
      case "STREAM_OUTPUT_BODY" => unsupportedV3Variable("STREAM_OUTPUT_BODY")
      case "WEBSERVER_ERROR_LOG" => unsupportedV3Variable("WEBSERVER_ERROR_LOG")
      case "USERAGENT_IP" => unsupportedV3Variable("USERAGENT_IP")
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      case other =>
        // fallback: unsupported collection
        println("unknown variable: " + other)
        Nil
    }
  }

  private def unimplementedTransform(name: String, v: String): String = {
    println("unimplemented transform " + name)
    v
  }

  private def unsupportedTransform(name: String, v: String): String = {
    println("unsupported transform " + name)
    v
  }

  private def applyTransforms(value: String, transforms: List[String]): String = {
    // https://github.com/owasp-modsecurity/ModSecurity/wiki/Reference-Manual-%28v3.x%29#transformation-functions
    transforms.foldLeft(value) {
      case (v, "lowercase") => v.toLowerCase
      case (v, "uppercase") => v.toUpperCase
      case (v, "trim")      => v.trim
      case (v, "urlDecodeUni") => try URLDecoder.decode(v, StandardCharsets.UTF_8.name()) catch { case _: Throwable => v }
      case (v, "base64Decode") => new String(Base64.getDecoder.decode(v))
      case (v, "base64DecodeExt") => new String(Base64.getDecoder.decode(v))
      case (v, "base64Encode") => Base64.getEncoder.encodeToString(v.getBytes(StandardCharsets.UTF_8))
      case (v, "compressWhitespace") => v.replaceAll("\\s+", " ")
      case (v, "hexDecode") => try java.util.HexFormat.of().parseHex(v).mkString catch { case _: Throwable => v }
      case (v, "hexEncode") => try java.util.HexFormat.of().formatHex(v.getBytes(StandardCharsets.UTF_8)).toLowerCase catch { case _: Throwable => v }
      case (v, "length") => v.length.toString
      case (v, "md5") => try java.security.MessageDigest.getInstance("MD5").digest(v.getBytes(StandardCharsets.UTF_8)).map("%02x".format(_)).mkString catch { case _: Throwable => v }
      case (v, "none") => v
      case (v, "normalisePath") => v.split("/").filterNot(_.isEmpty).mkString("/")
      case (v, "normalizePath") => v.split("/").filterNot(_.isEmpty).mkString("/")
      case (v, "normalisePathWin") => v.split("[/\\\\]+").filterNot(_.isEmpty).mkString("/")
      case (v, "normalizePathWin") => v.split("[/\\\\]+").filterNot(_.isEmpty).mkString("/")
      case (v, "removeNulls") => v.replaceAll("\u0000", "")
      case (v, "removeWhitespace") => v.replaceAll("\\s+", "")
      case (v, "removeCommentsChar") => v.replaceAll("--[^\r\n]*", "").replaceAll("/\\*", "").replaceAll("\\*/", "").replaceAll("#", "")
      case (v, "urlDecode") => try URLDecoder.decode(v, StandardCharsets.UTF_8.name()) catch { case _: Throwable => v }
      case (v, "urlEncode") => try URLEncoder.encode(v, StandardCharsets.UTF_8.name()) catch { case _: Throwable => v }
      case (v, "sha1") => try java.security.MessageDigest.getInstance("SHA-1").digest(v.getBytes(StandardCharsets.UTF_8)).map("%02x".format(_)).mkString catch { case _: Throwable => v }
      case (v, "trimLeft") => v.dropWhile(_ == ' ')
      case (v, "trimRight") => v.reverse.dropWhile(_ == ' ').reverse
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      case (v, "utf8toUnicode") => unimplementedTransform("utf8toUnicode", v) // TODO: implement it
      case (v, "replaceNulls") => unimplementedTransform("replaceNulls", v) // TODO: implement it
      case (v, "replaceComments") => unimplementedTransform("replaceComments", v) // TODO: implement it
      case (v, "parityEven7bit") => unimplementedTransform("parityEven7bit", v) // TODO: implement it
      case (v, "parityOdd7bit") => unimplementedTransform("parityOdd7bit", v) // TODO: implement it
      case (v, "parityZero7bit") => unimplementedTransform("parityZero7bit", v) // TODO: implement it
      case (v, "jsDecode") => unimplementedTransform("jsDecode", v) // TODO: implement it
      case (v, "htmlEntityDecode") => unimplementedTransform("htmlEntityDecode", v) // TODO: implement it
      case (v, "escapeSeqDecode") => unimplementedTransform("escapeSeqDecode", v) // TODO: implement it
      case (v, "cssDecode") => unimplementedTransform("cssDecode", v) // TODO: implement it
      case (v, "cmdLine") => unimplementedTransform("cmdLine", v) // TODO: implement it
      case (v, "sqlHexDecode") => unimplementedTransform("sqlHexDecode", v) // TODO: implement it
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      case (v, _) => v
    }
  }

  private def unimplementedOperator(op: String): Boolean = {
    println("unimplemented operator: " + op)
    false
  }

  private def unsupportedOperator(op: String): Boolean = {
    println("unsupported operator: " + op)
    false
  }

  private def unsupportedV3Operator(op: String): Boolean = {
    println("unsupported operator in v3: " + op)
    false
  }

  private def evalOperator(op: Operator, value: String): Boolean = op match {
    // https://github.com/owasp-modsecurity/ModSecurity/wiki/Reference-Manual-%28v3.x%29#user-content-Operators
    case Operator.Negated(oop)         => !evalOperator(oop, value)
    case Operator.UnconditionalMatch() => true
    case Operator.Contains(x)          => value.contains(evalTxExpressions(x))
    case Operator.Streq(x)             => value == evalTxExpressions(x)
    case Operator.Pm(xs)               => evalTxExpressions(xs).split(" ").exists(it => value.toLowerCase().contains(it.toLowerCase))
    case Operator.Rx(pattern) => {
      try {
        val r: Regex = evalTxExpressions(pattern).r
        r.findFirstIn(value).nonEmpty
      } catch {
        case _: Throwable => false
      }
    }
    case Operator.BeginsWith(x) => value.startsWith(evalTxExpressions(x))
    case Operator.ContainsWord(x) => value.contains(evalTxExpressions(x).split(" ").filterNot(_.isEmpty).headOption.getOrElse(""))
    case Operator.EndsWith(x) => value.endsWith(evalTxExpressions(x))
    case Operator.Eq(x) => scala.util.Try(value.toInt).getOrElse(0) == scala.util.Try(evalTxExpressions(x).toInt).getOrElse(0)
    case Operator.Ge(x) => scala.util.Try(value.toInt).getOrElse(0) >= scala.util.Try(evalTxExpressions(x).toInt).getOrElse(0)
    case Operator.Gt(x) => scala.util.Try(value.toInt).getOrElse(0) > scala.util.Try(evalTxExpressions(x).toInt).getOrElse(0)
    case Operator.Le(x) => scala.util.Try(value.toInt).getOrElse(0) <= scala.util.Try(evalTxExpressions(x).toInt).getOrElse(0)
    case Operator.Lt(x) => scala.util.Try(value.toInt).getOrElse(0) < scala.util.Try(evalTxExpressions(x).toInt).getOrElse(0)
    case Operator.StrMatch(x) => value.toLowerCase.contains(evalTxExpressions(x).toLowerCase)
    case Operator.Within(x) => evalTxExpressions(x).toLowerCase().contains(value.toLowerCase())
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    case Operator.ValidateByteRange(x) => unimplementedOperator("validateByteRange") // TODO: implement it
    case Operator.ValidateUrlEncoding(x) => unimplementedOperator("validateUrlEncoding") // TODO: implement it
    case Operator.ValidateUtf8Encoding(x) => unimplementedOperator("validateUtf8Encoding") // TODO: implement it
    case Operator.VerifyCC(x) => unimplementedOperator("verifyCC") // TODO: implement it
    case Operator.VerifyCPF(x) => unimplementedOperator("verifyCPF") // TODO: implement it
    case Operator.VerifySSN(x) => unimplementedOperator("verifySSN") // TODO: implement it
    // case Operator.NoMatch(x) => unimplementedOperator("noMatch")
    case Operator.PmFromFile(x) => unimplementedOperator("pmFromFile") // TODO: implement it
    case Operator.Rbl(x) => unimplementedOperator("rbl") // TODO: implement it
    case Operator.RxGlobal(x) => unimplementedOperator("rxGlobal") // TODO: implement it
    case Operator.IpMatch(x) => unimplementedOperator("ipMatch") // TODO: implement it
    case Operator.IpMatchFromFile(x) => unimplementedOperator("ipMatchFromFile") // TODO: implement it
    case Operator.FuzzyHash(x) => unimplementedOperator("fuzzyHash") // TODO: implement it
    case Operator.DetectXSS(x) => unimplementedOperator("detectXSS") // TODO: implement it
    case Operator.DetectSQLi(x) => unimplementedOperator("detectSQLi") // TODO: implement it
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    case Operator.ValidateDTD(x) => unsupportedOperator("validateDTD")
    case Operator.ValidateSchema(x) => unsupportedOperator("validateSchema")
    case Operator.GeoLookup(x) => unsupportedOperator("geoLookup")
    case Operator.InspectFile(x) => unsupportedOperator("inspectFile")
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    case Operator.ValidateHash(x) => unsupportedV3Operator("validateHash")
    case Operator.Rsub(x) => unsupportedV3Operator("rsub")
    case Operator.GsbLookup(x) => unsupportedV3Operator("gsbLookup")
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    case _ =>
      // unsupported operator => "safe false"
      println("unknown operator: " + op)
      false
  }
}
