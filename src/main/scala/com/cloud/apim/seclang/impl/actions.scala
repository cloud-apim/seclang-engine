package com.cloud.apim.seclang.impl.engine

import com.cloud.apim.seclang.model.{Action, EngineMode, NeedRunAction, RequestContext, RuntimeState, SecLangIntegration}
import play.api.libs.json.Json

object EngineActions {
  def performActions(ruleId: Int, actions: List[Action], phase: Int, context: RequestContext, state: RuntimeState, integration: SecLangIntegration, msg: Option[String], rawLogdata: List[String], isLast: Boolean): RuntimeState = {
    var localState = state
    val events = state.events.filter(_.msg.isDefined).map { e =>
      s"phase=${e.phase} rule_id=${e.ruleId.getOrElse(0)} - ${e.msg.getOrElse("no msg")}"
    }.mkString(". ")
    var hasLog = false
    val executableActions: List[NeedRunAction] = actions.collect {
      case Action.Log =>
        hasLog = true
        Action.Log
      case a: NeedRunAction => a
    } ++ (if (!hasLog && rawLogdata.nonEmpty) List[NeedRunAction](Action.Log) else Nil)

    // First pass: run SetVar and other non-log actions to populate TX variables
    executableActions.foreach {
      case Action.Log => () // Skip log actions in first pass
      case Action.AuditLog() => () // Skip audit log in first pass
      case Action.Capture() => {
        state.txMap.get("MATCHED_VAR").foreach(v => state.txMap.put("0", v))
        state.txMap.get("MATCHED_LIST").foreach { list =>
          val liststr = Json.parse(list).asOpt[Seq[String]].getOrElse(Seq.empty)
          liststr.zipWithIndex.foreach {
            case (v, idx) => state.txMap.put(s"${idx + 1}", v)
          }
        }
      }
      case Action.SetEnv(expr) => {
        val parts = expr.split("=")
        val name = parts(0)
        val value = state.evalTxExpressions(parts(1))
        state.envMap.put(name, value)
      }
      case Action.SetRsc(_) => ()
      case Action.SetSid(_) => ()
      case Action.SetUid(expr) => {
        state.uidRef.set(expr)
      }
      case Action.SetVar(rawExpr) => {
        val expr = state.evalTxExpressions(rawExpr)
        val isDelete = expr.startsWith("!")
        val baseExpr = if (isDelete) expr.substring(1) else expr
        // Normalize once: remove tx./TX. prefix and lowercase — but only over the variable name.
        // Lowercasing the whole assignment also lowercased the value, so a rule storing
        // `%{MATCHED_VAR_NAME}` to read back in its logdata — which is how CRS reports what it
        // matched on — got `request_headers:referer` where it wrote `REQUEST_HEADERS:Referer`.
        val normalized = baseExpr.indexOf('=') match {
          case -1  => baseExpr.replace("tx.", "").replace("TX.", "").toLowerCase()
          case idx =>
            baseExpr.substring(0, idx).replace("tx.", "").replace("TX.", "").toLowerCase() + baseExpr.substring(idx)
        }

        if (isDelete) {
          state.txMap.remove(normalized)
        } else if (expr.contains("+=")) {
          val parts = normalized.split("=")
          val name = parts(0)
          try {
            val incr = parts(1).toInt
            val value = state.txMap.get(name).map(_.toInt).getOrElse(0)
            state.txMap.put(name, (value + incr).toString)
          } catch {
            case _: Throwable => ()
          }
        } else if (expr.contains("-=")) {
          val parts = normalized.split("=")
          val name = parts(0)
          try {
            val decr = parts(1).toInt
            val value = state.txMap.get(name).map(_.toInt).getOrElse(0)
            state.txMap.put(name, (value - decr).toString)
          } catch {
            case _: Throwable => ()
          }
        } else if (expr.contains("=+")) {
          // ModSecurity syntax: setvar:'TX.var=+1' means add 1 to the variable
          val parts = normalized.split("=\\+")
          val name = parts(0)
          try {
            val incr = parts(1).toInt
            val value = state.txMap.get(name).map(v => state.evalTxExpressions(v)).map(_.toInt).getOrElse(0)
            state.txMap.put(name, (value + incr).toString)
          } catch {
            case _: Throwable => ()
          }
        } else if (expr.contains("=-")) {
          // ModSecurity syntax: setvar:'TX.var=-1' means subtract 1 from the variable
          val parts = normalized.split("=-")
          val name = parts(0)
          try {
            val decr = parts(1).toInt
            val value = state.txMap.get(name).map(_.toInt).getOrElse(0)
            state.txMap.put(name, (value - decr).toString)
          } catch {
            case _: Throwable => ()
          }
        } else if (expr.contains("=")) {
          val parts = normalized.split("=", 2) // Only split on first '=' to preserve '=' in value
          val name = parts(0)
          val value = parts(1)
          state.txMap.put(name, value)
        } else {
          state.txMap.put(normalized, "0")
        }
      }
      // the host owns audit logging: these run on real traffic (CRS 905100 and 905110), so they are
      // said at debug level rather than printed for every request
      case Action.CtlAction.AuditEngine(value) => integration.logDebug(s"ctl:auditEngine=$value is not supported, ignored")
      case Action.CtlAction.AuditLogParts(value) => integration.logDebug(s"ctl:auditLogParts=$value is not supported, ignored")
      case Action.CtlAction.RequestBodyAccess(id) => ()
      // read by the rules that follow, which see the body through this processor (CRS 901350 to 901370)
      case Action.CtlAction.RequestBodyProcessor(value) => {
        value.trim.toUpperCase match {
          case processor @ ("JSON" | "XML" | "URLENCODED" | "MULTIPART") => localState = localState.copy(bodyProcessor = Some(processor))
          case other => integration.logDebug(s"ctl:requestBodyProcessor=$other is not a supported processor, ignored")
        }
      }
      case Action.CtlAction.RuleEngine(value) => {
        localState = localState.copy(mode = EngineMode(value))
      }
      case Action.CtlAction.ForceRequestBodyVariable(id) =>()
      case Action.CtlAction.RuleRemoveByTag(tag) => {
        localState = localState.copy(disabledTags = localState.disabledTags + tag)
      }
      case Action.CtlAction.RuleRemoveTargetById(id, target) => {
        val existing = localState.removedTargetsById.getOrElse(id, Set.empty)
        localState = localState.copy(removedTargetsById = localState.removedTargetsById + (id -> (existing + target.toUpperCase)))
      }
      case Action.CtlAction.RuleRemoveTargetByTag(tag, target) => {
        val existing = localState.removedTargetsByTag.getOrElse(tag, Set.empty)
        localState = localState.copy(removedTargetsByTag = localState.removedTargetsByTag + (tag -> (existing + target.toUpperCase)))
      }
      case Action.CtlAction.RuleRemoveById(id) => {
        localState = localState.copy(disabledIds = localState.disabledIds + id)
      }
      case act => integration.logError("unimplemented action: " + act.getClass.getSimpleName)
    }

    // Now evaluate logdata expressions after all SetVar actions have run
    val evaluatedLogdata = rawLogdata.map(state.evalTxExpressions)

    // Second pass: run Log and AuditLog actions with evaluated logdata
    executableActions.foreach {
      case Action.AuditLog() => {
        if (isLast) {
          msg.foreach { msg =>
            integration.audit(ruleId, context, state, phase, msg, evaluatedLogdata)
          }
        }
      }
      case Action.Log => {
        if (isLast) {
          msg.foreach { msg =>
            // Include ModSecurity-style format with [id "..."][msg "..."] for compatibility with CRS tests
            val logdataStr = if (evaluatedLogdata.nonEmpty) s" ${evaluatedLogdata.mkString(". ")}" else ""
            val q = '"'
            val m = s"${context.requestId} - ${context.method} ${context.uri} [id $q$ruleId$q][msg $q$msg$q]$logdataStr"
            localState = localState.copy(logs = localState.logs :+ m)
            integration.logInfo(m)
          }
        }
      }
      case _ => () // Other actions already handled in first pass
    }
    localState
  }

}
