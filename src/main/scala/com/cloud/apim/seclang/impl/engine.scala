
package com.cloud.apim.seclang.impl.engine

import akka.util.ByteString
import com.cloud.apim.libinjection.LibInjection
import com.cloud.apim.seclang.impl.compiler._
import com.cloud.apim.seclang.impl.utils._
import com.cloud.apim.seclang.model.Action.CtlAction
import com.cloud.apim.seclang.model._
import play.api.libs.json.{JsArray, JsString, Json}

import java.net.{URLDecoder, URLEncoder}
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.concurrent.atomic.AtomicReference
import scala.collection.concurrent.TrieMap
import scala.util._
import scala.util.matching.Regex

final class SecRulesEngine(program: CompiledProgram, config: SecRulesEngineConfig, files: Map[String, String] = Map.empty, env: Map[String, String] = Map.empty) {

  // TODO: declare in evaluate, just to be sure !
  private val txMap = new TrieMap[String, String]()
  private val envMap = {
    val tm = new TrieMap[String, String]()
    env.foreach(tm += _)
    tm
  }
  private val uidRef = new AtomicReference[String](null)

  private def logDebug(msg: String): Unit = println(s"[Debug]: $msg")
  private def logInfo(msg: String): Unit = println(s"[Info]: $msg")
  private def logAudit(msg: String): Unit = println(s"[Audit]: $msg")
  private def logError(msg: String): Unit = println(s"[Error]: $msg")

  // runtime disables (ctl:ruleRemoveById)
  def evaluate(ctx: RequestContext, phases: List[Int] = List(1, 2)): EngineResult = {
    if (program.mode.isOff) {
      EngineResult(Disposition.Continue, List.empty)
    } else {
      val init = RuntimeState(program.mode, Set.empty, Nil)
      val (disp, st) = phases.foldLeft((Disposition.Continue: Disposition, init)) {
        case ((Disposition.Block(a, b, c), st), _) if st.mode.isBlocking => (Disposition.Block(a, b, c), st) // already blocked, keep
        case ((Disposition.Block(a, b, c), st), ph) if st.mode.isDetectionOnly => {
          val (d2, st2) = runPhase(ph, ctx, st)
          (d2, st2)
        }
        case ((Disposition.Continue, st), ph) => {
          val (d2, st2) = runPhase(ph, ctx, st)
          (d2, st2)
        }
      }
      EngineResult(disp, st.events.reverse)
    }
  }

  private def runPhase(phase: Int, ctx: RequestContext, st0: RuntimeState): (Disposition, RuntimeState) = {
    val items = program.itemsByPhase.getOrElse(phase, Vector.empty)

    // build marker index for this phase stream
    val markerIndex: Map[String, Int] = items.zipWithIndex.collect {
      case (MarkerItem(name), idx) => name -> idx
    }.toMap

    var i = 0
    var st = st0
    var disps = List.empty[Disposition]

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
              case Some(d) if !st.mode.isDetectionOnly => return (d, st)
              case Some(d) if st.mode.isDetectionOnly => {
                disps = disps :+ d
                skipToIdxOpt match {
                  case Some(j) => i = j
                  case None    => i += 1
                }
              }
              case None =>
                skipToIdxOpt match {
                  case Some(j) => i = j
                  case None    => i += 1
                }
            }
          }
      }
    }
    if (st.mode.isDetectionOnly) {
      (Disposition.Continue, st)
    } else {
      if (disps.nonEmpty) {
        (disps.head, st)
      } else {
        (Disposition.Continue, st)
      }
    }
  }

  // (?i) => case-insensitive
  private val TxExpr: Regex = """(?i)%\{tx\.([a-z0-9_.-]+)\}""".r

  private def evalTxExpressions(input: String): String = {
    if (input.contains("%{")) {
      val finalInput: String = if (input.contains("%{MATCHED_VAR}") && txMap.contains("MATCHED_VAR")) {
        input.replaceAll("%\\{MATCHED_VAR\\}", txMap("MATCHED_VAR"))
      } else {
        input
      }
      TxExpr.replaceAllIn(finalInput, m => {
        val key = m.group(1).toLowerCase
        txMap.getOrElse(key, m.matched)
      })
    } else {
      input
    }
  }

  private def performActions(ruleId: Int, actions: List[Action], phase: Int, context: RequestContext, state: RuntimeState): RuntimeState = {
    // TODO: implement it
    var localState = state
    val events = state.events.filter(_.msg.isDefined).map { e =>
      s"phase=${e.phase} rule_id=${e.ruleId.getOrElse(0)} - ${e.msg.getOrElse("no msg")}"
    }.mkString(". ")
    val executableActions: List[NeedRunAction] = actions.collect {
      case a: NeedRunAction => a
    }
    executableActions.foreach {
      case Action.AuditLog() => logAudit(s"${context.requestId} - ${context.method} ${context.uri} matched on: $events")
      case Action.Capture() => {
        txMap.get("MATCHED_VAR").foreach(v => txMap.put("0", v))
        txMap.get("MATCHED_LIST").foreach { list =>
          val liststr = Json.parse(list).asOpt[Seq[String]].getOrElse(Seq.empty)
          liststr.zipWithIndex.foreach {
            case (v, idx) => txMap.put(s"${idx + 1}", v)
          }
        }
      }
      case Action.Log => logInfo(s"${context.requestId} - ${context.method} ${context.uri} matched on: $events")
      case Action.SetEnv(expr) => {
        val parts = expr.split("=")
        val name = parts(0)
        val value = evalTxExpressions(parts(1))
        envMap.put(name, value)
      }
      case Action.SetRsc(_) => ()
      case Action.SetSid(_) => ()
      case Action.SetUid(expr) => {
        uidRef.set(expr)
      }
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
          case expr => logError("invalid setvar expression: " + expr)
        }
      }
      case Action.CtlAction.AuditEngine(id) => println("AuditEngine not implemented yet")
      case Action.CtlAction.AuditLogParts(id) => println("AuditLogParts not implemented yet")
      case Action.CtlAction.RequestBodyAccess(id) => println("RequestBodyAccess not implemented yet")
      case Action.CtlAction.RequestBodyProcessor(id) => println("RequestBodyProcessor not implemented yet")
      case Action.CtlAction.RuleEngine(value) => {
        localState = localState.copy(mode = EngineMode(value))
      }
      case Action.CtlAction.ForceRequestBodyVariable(id) => println("ForceRequestBodyVariable not implemented yet")
      case Action.CtlAction.RuleRemoveByTag(tag) => println("RuleRemoveByTag not implemented yet")
      case Action.CtlAction.RuleRemoveTargetById(id, target) => println("RuleRemoveTargetById not implemented yet")
      case Action.CtlAction.RuleRemoveTargetByTag(tag, target) => {
        // println(s"RuleRemoveTargetByTag('${tag}', '${target}')")
        // TODO: needs to be implemented
      }
      case Action.CtlAction.RuleRemoveById(id) => {
        localState = localState.copy(disabledIds = localState.disabledIds + id)
      }
      case act => logError("unimplemented action: " + act.getClass.getSimpleName)
    }
    localState
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
    st = performActions(action.id.getOrElse(0), actionsList, phase, ctx, st)
    val disp =
      disruptive match {
        case Some(Action.Deny) | Some(Action.Drop) | Some(Action.Block()) =>
          Some(Disposition.Block(
            status = collectedStatus.getOrElse(400),
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
    var events = List.empty[MatchEvent]

    val isChain = rules.size > 1
    val firstRule = rules.head

    // Evaluate sequentially; if one fails -> chain fails
    rules.foreach { r =>
      lastRuleId = r.id.orElse(lastRuleId)
      val isLast = rules.last == r
      if (allMatched) {
        val matched = evalRule(r, ctx, debug = false)
        // TODO: implement actions: https://github.com/owasp-modsecurity/ModSecurity/wiki/Reference-Manual-%28v3.x%29#actions
        if (matched) {
          val actionsList = r.actions.toList.flatMap(_.actions)
          val msg = actionsList.collectFirst {
            case Action.Msg(m) => evalTxExpressions(m)
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

          if (isLast) {
            st = st.copy(events = MatchEvent(r.id, msg, phase, Json.stringify(r.json)) :: events ++ st.events)
            st = performActions(r.id.getOrElse(0), actionsList, phase, ctx, st)
          } else {
            events = events :+ MatchEvent(r.id, msg, phase, Json.stringify(r.json))
          }
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
              status = collectedStatus.getOrElse(400),
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
      val vrbls = rule.variables.variables.flatMap { v =>
        resolveVariable(v, rule.variables.count, rule.variables.negated, ctx)
      }
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
    val matched = transformed.exists(v => evalOperator(rule.id.getOrElse(-1), rule.operator, v))
    matched
  }

  private def unimplementedVariable(name: String): List[String] = {
    logDebug("unimplemented variable: " + name)
    Nil
  }

  private def unsupportedVariable(name: String): List[String] = {
    logDebug("unsupported variable: " + name)
    Nil
  }

  private def unsupportedV3Variable(name: String): List[String] = {
    logDebug("unsupported variable in V3: " + name)
    Nil
  }

  private def deepMerge(m1: Map[String, List[String]], m2: Map[String, List[String]]): Map[String, List[String]] = {
    (m1.keySet ++ m2.keySet).iterator.map { k =>
      k -> (m1.getOrElse(k, Nil) ++ m2.getOrElse(k, Nil))
    }.toMap
  }

  private def resolveVariable(sel: Variable, count: Boolean, negated: Boolean, ctx: RequestContext): List[String] = {
    // https://github.com/owasp-modsecurity/ModSecurity/wiki/Reference-Manual-%28v3.x%29#user-content-Variables
    val (col, key) = sel match {
      case Variable.Simple(name) => (name, None)
      case Variable.Collection(collection, key) => (collection, key.map(_.toLowerCase()))
    }
    //ctx.cache.get(col) match {
    //  case Some(v) => v
    //  case None => {
        //val uri = new java.net.URI(ctx.uri)
        val (rawPath, path, rawQuery) = {
          if (ctx.method.toLowerCase == "connect") {
            (ctx.uri, ctx.uri, "")
          } else {
            Try(new java.net.URI(ctx.uri)) match {
              case Success(uri) => (uri.getRawPath, uri.getPath, uri.getRawQuery)
              case Failure(e) => {
                val uri = if (!ctx.uri.startsWith("/")) {
                  ctx.uri.replaceFirst("https://", "").replaceFirst("http://", "").split("/").tail.mkString("/")
                } else {
                  ctx.uri
                }
                val parts = uri.split("\\?")
                val p = parts.headOption.getOrElse("")
                val rq = parts.tail.mkString("?")
                (uri, p, rq)
              }
            }
          }
        }
        val datetime = LocalDateTime.now()
        val res: List[String] = col match {
          case "REQUEST_URI" => List(ctx.uri)
          case "REQUEST_METHOD" => List(ctx.method)
          case "REQUEST_HEADERS" | "RESPONSE_HEADERS" => {
            key match {
              case None =>
                ctx.headers.toList.flatMap { case (k, vs) => vs.map(v => s"$k: $v") }
              case Some(h) =>
                ctx.headers.collect {
                  case (k, vs) if k.toLowerCase == h => vs
                }.flatten.toList
            }
          }
          case "ARGS" => {
            val headers = ctx.body match {
              case Some(body) if ctx.contentType.contains("application/www-form-urlencoded") => {
                val bodyArgs = FormUrlEncoded.parse(body.utf8String)
                deepMerge(ctx.query, bodyArgs)
              }
              case _ => ctx.query
            }
            key match {
              case None =>
                headers.toList.flatMap { case (k, vs) => vs.map(v => s"$k: $v") }
              case Some(h) =>
                headers.collect {
                  case (k, vs) if k.toLowerCase == h => vs
                }.flatten.toList
            }
          }
          case "ARGS_NAMES" => {
            val headers = ctx.body match {
              case Some(body) if ctx.contentType.contains("application/www-form-urlencoded") => {
                val bodyArgs = FormUrlEncoded.parse(body.utf8String)
                deepMerge(ctx.query, bodyArgs)
              }
              case _ => ctx.query
            }
            headers.keySet.toList
          }
          case "ARGS_COMBINED_SIZE" => {
            val headers = ctx.body match {
              case Some(body) if ctx.contentType.contains("application/www-form-urlencoded") => {
                val bodyArgs = FormUrlEncoded.parse(body.utf8String)
                deepMerge(ctx.query, bodyArgs)
              }
              case _ => ctx.query
            }
            List(headers.size.toString)
          }
          case "TX" => {
            key match {
              case None => List.empty
              case Some(key) => txMap.get(key.toLowerCase()).toList
            }
          }
          case "ENV" => key match {
            case None => List.empty
            case Some(key) => envMap.get(key).toList
          }
          case "REQUEST_BODY" | "RESPONSE_BODY" => ctx.body.toList.map(_.utf8String)
          case "REQUEST_HEADERS_NAMES" | "RESPONSE_HEADERS_NAMES" => ctx.headers.keySet.toList
          case "DURATION" => List((System.currentTimeMillis() - ctx.startTime).toString)
          case "PATH_INFO" => List(path)
          case "QUERY_STRING" => List(rawQuery)
          case "REMOTE_ADDR" => List(ctx.remoteAddr)
          case "REMOTE_HOST" => List(ctx.remoteAddr)
          case "REMOTE_PORT" => List(ctx.remotePort.toString)
          case "REMOTE_USER" => ctx.headers.get("Authorization").orElse(ctx.headers.get("authorization")).flatMap(_.lastOption).collect {
            case auth if auth.startsWith("Basic ") => Try(new String(Base64.getDecoder.decode(auth.substring("Basic ".length).split(":")(0)), StandardCharsets.UTF_8)).getOrElse("")
          }.toList.filter(_.nonEmpty)
          case "REQUEST_BASENAME" => path.split('/').lastOption.toList
          case "REQUEST_COOKIES" => key match {
            case None =>
              ctx.cookies.toList.flatMap { case (k, vs) => vs }//.map(v => s"$k: $v") }
            case Some(h) =>
              ctx.cookies.collect {
                case (k, vs) if k.toLowerCase == h => vs
              }.flatten.toList
          }
          case "REQUEST_COOKIES_NAMES" => ctx.cookies.keySet.toList
          case "REQUEST_FILENAME" => List(path)
          case "REQUEST_LINE" => List(s"${ctx.method.toUpperCase()} ${rawPath} ${ctx.protocol}")
          case "REQUEST_PROTOCOL" | "RESPONSE_PROTOCOL" => List(ctx.protocol)
          case "REQUEST_URI_RAW" => List(ctx.uriRaw)
          case "REQUEST_CONTENT_TYPE" | "RESPONSE_CONTENT_TYPE" => ctx.contentType.toList
          case "RESPONSE_STATUS" => ctx.status.map(_.toString).toList
          case "REQUEST_BODY_LENGTH" | "RESPONSE_CONTENT_LENGTH" => ctx.contentLength.toList
          // case "SERVER_ADDR" => unimplementedVariable("SERVER_ADDR") // from ctx.variables
          // case "SERVER_NAME" => unimplementedVariable("SERVER_NAME") // from ctx.variables
          // case "SERVER_PORT" => unimplementedVariable("SERVER_PORT") // from ctx.variables
          case "STATUS_LINE" => List(ctx.statusLine)
          case "TIME" => List(datetime.format(DateTimeFormatter.ofPattern("HH:mm:ss")))
          case "TIME_DAY" => List(datetime.getDayOfMonth.toString)
          case "TIME_EPOCH" => List((System.currentTimeMillis / 1000).toString)
          case "TIME_HOUR" => List(datetime.getHour.toString)
          case "TIME_MIN" => List(datetime.getMinute.toString)
          case "TIME_MON" => List(datetime.getMonthValue.toString)
          case "TIME_SEC" => List(datetime.getSecond.toString)
          case "TIME_WDAY" => List(datetime.getDayOfWeek.getValue.toString)
          case "TIME_YEAR" => List(datetime.getYear.toString)
          case "UNIQUE_ID" => List(ctx.requestId)
          case "USERID" => Option(uidRef.get).toList
          case "FILES" => ctx.contentType match {
            case Some("multipart/form-data") if ctx.body.isDefined => {
              ctx.body.get.utf8String.linesIterator
                .collect {
                  case line if line.toLowerCase.startsWith("content-disposition:") =>
                    """filename="([^"]+)"""".r
                      .findFirstMatchIn(line)
                      .map(_.group(1))
                }.flatten.toList
            }
            case _ => List.empty
          }
          case "FILES_COMBINED_SIZE" => ctx.contentType match {
            case Some("multipart/form-data") => ctx.contentLength.toList
            case _ => List.empty
          }
          case "FILES_NAMES" => ctx.contentType match {
            case Some("multipart/form-data") if ctx.body.isDefined => {
              ctx.body.get.utf8String.linesIterator
                .collect {
                  case line if line.toLowerCase.startsWith("content-disposition:") =>
                    """filename="([^"]+)"""".r
                      .findFirstMatchIn(line)
                      .map(_.group(1))
                }.flatten.toList
            }
            case _ => List.empty
          }
          case "FILES_SIZES" => ctx.contentType match {
            case Some("multipart/form-data") => ctx.contentLength.toList
            case _ => List.empty
          }
          case "FILES_TMPNAMES" => List.empty
          case "FILES_TMP_CONTENT" => List.empty
          case "REQBODY_PROCESSOR" => {
            ctx.contentType
              .map(_.toLowerCase)
              .collect {
                case ct if ct.startsWith("application/x-www-form-urlencoded") => "URLENCODED"
                case ct if ct.startsWith("multipart/form-data")               => "MULTIPART"
                case ct if ct.startsWith("application/xml") ||
                  ct.startsWith("text/xml")                           => "XML"
                case ct if ct.startsWith("application/json")                   => "JSON"
              }.toList
          }
          case "MULTIPART_PART_HEADERS" => {
            ctx.body match {
              case Some(body) if ctx.contentType.contains("multipart/form-data")=> {
                val headers = MultipartVars.multipartPartHeadersFromCtx(body.utf8String, ctx.contentType).getOrElse(Map.empty)
                key match {
                  case None =>
                    headers.toList.flatMap { case (k, vs) => vs.map(v => s"$k: $v") }
                  case Some(h) =>
                    headers.collect {
                      case (k, vs) if k.toLowerCase == h => vs
                    }.flatten.toList
                }
              }
              case _ => List.empty
            }
          }
          case "ARGS_GET" => {
            val headers = ctx.query
            key match {
              case None =>
                headers.toList.flatMap { case (k, vs) => vs.map(v => s"$k: $v") }
              case Some(h) =>
                headers.collect {
                  case (k, vs) if k.toLowerCase == h => vs
                }.flatten.toList
            }
          }
          case "ARGS_GET_NAMES" =>  ctx.query.keySet.toList
          case "ARGS_POST" => {
            ctx.body match {
              case Some(body) if ctx.contentType.contains("application/www-form-urlencoded") => {
                val headers = FormUrlEncoded.parse(body.utf8String)
                key match {
                  case None =>
                    headers.toList.flatMap { case (k, vs) => vs.map(v => s"$k: $v") }
                  case Some(h) =>
                    headers.collect {
                      case (k, vs) if k.toLowerCase == h => vs
                    }.flatten.toList
                }
              }
              case _ => List.empty
            }
          }
          case "ARGS_POST_NAMES" => {
            ctx.body match {
              case Some(body) if ctx.contentType.contains("application/www-form-urlencoded") => {
                val headers = FormUrlEncoded.parse(body.utf8String)
                headers.keySet.toList
              }
              case _ => List.empty
            }
          }
          case "XML" => {
            ctx.body match {
              case Some(body) if key.isDefined && (ctx.contentType.exists(_.contains("application/xm")) || ctx.contentType.exists(_.contains("text/xm"))) => {
                XmlXPathParser.xpathValues(body.utf8String, key.get)
              }
              case _ => List.empty
            }
          }
          case "MATCHED_VAR" => txMap.get("MATCHED_VAR").orElse(txMap.get("MATCHED_VAR".toLowerCase())).toList
          case "MATCHED_VARS" =>  txMap.get("MATCHED_LIST").toList.flatMap { v =>
            Json.parse(v).asOpt[List[String]].getOrElse(List.empty)
          }
          //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
          case "AUTH_TYPE" => unimplementedVariable("AUTH_TYPE") // TODO: implement it
          case "FULL_REQUEST" => unimplementedVariable("FULL_REQUEST") // TODO: implement it
          case "FULL_REQUEST_LENGTH" => unimplementedVariable("FULL_REQUEST_LENGTH") // TODO: implement it
          case "HIGHEST_SEVERITY" => unimplementedVariable("HIGHEST_SEVERITY") // TODO: implement it
          case "INBOUND_DATA_ERROR" => unimplementedVariable("INBOUND_DATA_ERROR") // TODO: implement it
          case "MATCHED_VAR_NAME" => unimplementedVariable("MATCHED_VAR_NAME") // TODO: implement it
          case "MATCHED_VARS_NAMES" => unimplementedVariable("MATCHED_VARS_NAMES") // TODO: implement it
          case "MODSEC_BUILD" => unimplementedVariable("MODSEC_BUILD") // TODO: implement it
          case "MSC_PCRE_LIMITS_EXCEEDED" => unimplementedVariable("MSC_PCRE_LIMITS_EXCEEDED") // TODO: implement it
          case "MULTIPART_CRLF_LF_LINES" => unimplementedVariable("MULTIPART_CRLF_LF_LINES") // TODO: implement it
          case "MULTIPART_FILENAME" => unimplementedVariable("MULTIPART_FILENAME") // TODO: implement it
          case "MULTIPART_NAME" => unimplementedVariable("MULTIPART_NAME") // TODO: implement it
          case "MULTIPART_STRICT_ERROR" => unimplementedVariable("MULTIPART_STRICT_ERROR") // TODO: implement it
          case "MULTIPART_UNMATCHED_BOUNDARY" => unimplementedVariable("MULTIPART_UNMATCHED_BOUNDARY") // TODO: implement it
          case "OUTBOUND_DATA_ERROR" => unimplementedVariable("OUTBOUND_DATA_ERROR") // TODO: implement it
          case "REQBODY_ERROR" => unimplementedVariable("REQBODY_ERROR") // TODO: implement it
          case "REQBODY_ERROR_MSG" => unimplementedVariable("REQBODY_ERROR_MSG") // TODO: implement it
          case "RULE" => unimplementedVariable("RULE") // TODO: implement it
          case "SDBM_DELETE_ERROR" => unimplementedVariable("SDBM_DELETE_ERROR") // TODO: implement it
          case "SESSION" => unimplementedVariable("SESSION") // TODO: implement it
          case "SESSIONID" => unimplementedVariable("SESSIONID") // TODO: implement it
          case "URLENCODED_ERROR" => unimplementedVariable("URLENCODED_ERROR") // TODO: implement it
          case "WEBAPPID" => unimplementedVariable("WEBAPPID") // TODO: implement it
          //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
          case "GEO" => unsupportedVariable("GEO")
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
            ctx.variables.get(other)
              .orElse(ctx.variables.get(other.toLowerCase))
              .orElse(ctx.variables.get(other.toUpperCase)) match {
              case Some(v) => List(v)
              case None =>
                // fallback: unsupported collection
                logDebug("unknown variable: " + other)
                Nil
            }
        }
        //ctx.cache.put(col, res)
        res
      //}
    //}
  }

  private def unimplementedTransform(name: String, v: String): String = {
    logDebug("unimplemented transform " + name)
    v
  }

  private def unsupportedTransform(name: String, v: String): String = {
    logDebug("unsupported transform " + name)
    v
  }

  private def applyTransforms(value: String, transforms: List[String]): String = {
    // https://github.com/owasp-modsecurity/ModSecurity/wiki/Reference-Manual-%28v3.x%29#transformation-functions
    transforms.foldLeft(value) {
      case (v, "lowercase") => v.toLowerCase
      case (v, "uppercase") => v.toUpperCase
      case (v, "trim")      => v.trim
      case (v, "urlDecodeUni") => try URLDecoder.decode(v, StandardCharsets.UTF_8.name()) catch { case _: Throwable => v }
      case (v, "base64Decode") => Try(new String(Base64.getDecoder.decode(v))).getOrElse(v)
      case (v, "base64DecodeExt") => Try(new String(Base64.getDecoder.decode(v))).getOrElse(v)
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
      case (v, "utf8toUnicode") => Transformations.utf8toUnicode(v)
      case (v, "jsDecode") => Transformations.jsDecode(v)
      case (v, "htmlEntityDecode") => Transformations.htmlEntityDecode(v)
      case (v, "cssDecode") => Transformations.cssDecode(v)
      case (v, "replaceComments") => Transformations.replaceComments(v)
      case (v, "cmdLine") => Transformations.cmdLine(v)
      case (v, "escapeSeqDecode") => EscapeSeq.escapeSeqDecode(v)
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      case (v, "replaceNulls") => unimplementedTransform("replaceNulls", v) // TODO: implement it
      case (v, "parityEven7bit") => unimplementedTransform("parityEven7bit", v) // TODO: implement it
      case (v, "parityOdd7bit") => unimplementedTransform("parityOdd7bit", v) // TODO: implement it
      case (v, "parityZero7bit") => unimplementedTransform("parityZero7bit", v) // TODO: implement it
      case (v, "sqlHexDecode") => unimplementedTransform("sqlHexDecode", v) // TODO: implement it
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      case (v, _) => v
    }
  }

  private def unimplementedOperator(op: String): Boolean = {
    logDebug("unimplemented operator: " + op)
    false
  }

  private def unsupportedOperator(op: String): Boolean = {
    logDebug("unsupported operator: " + op)
    false
  }

  private def unsupportedV3Operator(op: String): Boolean = {
    logDebug("unsupported operator in v3: " + op)
    false
  }

  private def evalOperator(ruleId: Int, op: Operator, value: String): Boolean = op match {
    // https://github.com/owasp-modsecurity/ModSecurity/wiki/Reference-Manual-%28v3.x%29#user-content-Operators
    case Operator.Negated(oop)         => !evalOperator(ruleId, oop, value)
    case Operator.UnconditionalMatch() => true
    case Operator.Contains(x)          => value.contains(evalTxExpressions(x))
    case Operator.Streq(x)             => value == evalTxExpressions(x)
    case Operator.Pm(xs)               => evalTxExpressions(xs).split(" ").exists(it => value.toLowerCase().contains(it.toLowerCase))
    case Operator.Rx(pattern) => {
      //if (ruleId == 920470) {
      //  println(s"value: ${value}")
      //  println(s"pattern: ${pattern}")
      //}
      try {
        val r: Regex = evalTxExpressions(pattern).r
        val rs = r.findFirstMatchIn(value)
        rs.foreach { m =>
          val str = m.group(0)
          txMap.put("MATCHED_VAR".toLowerCase, str)
          txMap.put("MATCHED_VAR".toUpperCase, str)
          val list = JsArray((1 to m.groupCount).map { idx =>
            JsString(m.group(idx))
          })
          txMap.put("MATCHED_LIST", Json.stringify(list))
        }
        //if (ruleId == 920470) {
        //  println(s"matched ???: ${rs.nonEmpty}")
        //}
        rs.nonEmpty
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
    case Operator.PmFromFile(xs) => {
      val fileName = evalTxExpressions(xs)
      files.get(fileName) match {
        case None => false
        case Some(file) => {
          file
            .linesIterator
            .filter(_.trim.nonEmpty)
            .filterNot(_.startsWith("#"))
            .exists { ex =>
              //ex.split(" ").exists(it => value.toLowerCase().contains(it.toLowerCase))
              value.toLowerCase().contains(ex.toLowerCase)
            }
        }
      }
    }
    case Operator.ValidateByteRange(x) => ByteRangeValidator.validateByteRange(value, x)
    case Operator.IpMatch(x) => IpMatch.ipMatch(x, value)
    case Operator.IpMatchFromFile(xs) => {
      val fileName = evalTxExpressions(xs)
      files.get(fileName) match {
        case None => false
        case Some(file) => {
          file
            .linesIterator
            .filter(_.trim.nonEmpty)
            .filterNot(_.startsWith("#"))
            //.exists(ex => ex.split(" ").exists(it => IpMatch.ipMatch(it, value)))
            .exists(ex => IpMatch.ipMatch(ex, value))
        }
      }
    }
    case Operator.DetectXSS(x) => LibInjection.isXSS(evalTxExpressions(x))
    case Operator.DetectSQLi(x) => LibInjection.isSQLi(evalTxExpressions(x))
    case Operator.ValidateUrlEncoding(x) => EncodingHelper.validateUrlEncoding(x)
    case Operator.ValidateUtf8Encoding(x) => !EncodingHelper.validateUtf8Encoding(ByteString(x))
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    case Operator.VerifyCC(x) => unimplementedOperator("verifyCC") // TODO: implement it
    case Operator.VerifyCPF(x) => unimplementedOperator("verifyCPF") // TODO: implement it
    case Operator.VerifySSN(x) => unimplementedOperator("verifySSN") // TODO: implement it
    // case Operator.NoMatch(x) => unimplementedOperator("noMatch")
    case Operator.Rbl(x) => unimplementedOperator("rbl") // TODO: implement it
    case Operator.RxGlobal(x) => unimplementedOperator("rxGlobal") // TODO: implement it
    case Operator.FuzzyHash(x) => unimplementedOperator("fuzzyHash") // TODO: implement it
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
      logError("unknown operator: " + op)
      false
  }
}