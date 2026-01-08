package com.cloud.apim.seclang.impl.engine

import com.cloud.apim.seclang.model.{Action, EngineMode, NeedRunAction, RequestContext, RuntimeState, SecLangIntegration}
import play.api.libs.json.Json

object EngineActions {
  def performActions(ruleId: Int, actions: List[Action], phase: Int, context: RequestContext, state: RuntimeState, integration: SecLangIntegration, msg: Option[String], logdata: List[String], isLast: Boolean): RuntimeState = {
    var localState = state
    val events = state.events.filter(_.msg.isDefined).map { e =>
      s"phase=${e.phase} rule_id=${e.ruleId.getOrElse(0)} - ${e.msg.getOrElse("no msg")}"
    }.mkString(". ")
    val executableActions: List[NeedRunAction] = actions.collect {
      case a: NeedRunAction => a
    }
    executableActions.foreach {
      case Action.AuditLog() => {
        if (isLast) {
          msg.foreach { msg =>
            integration.audit(ruleId, context, state, phase,  msg, logdata)
          }
        }
      }
      case Action.Capture() => {
        state.txMap.get("MATCHED_VAR").foreach(v => state.txMap.put("0", v))
        state.txMap.get("MATCHED_LIST").foreach { list =>
          val liststr = Json.parse(list).asOpt[Seq[String]].getOrElse(Seq.empty)
          liststr.zipWithIndex.foreach {
            case (v, idx) => state.txMap.put(s"${idx + 1}", v)
          }
        }
      }
      case Action.Log => {
        if (isLast) {
          msg.foreach { msg =>
            integration.logInfo(s"${context.requestId} - ${context.method} ${context.uri} matched on: $msg. ${logdata.mkString(". ")}")
          }
        }
      }
      case Action.SetEnv(expr) => {
        val parts = expr.split("=")
        val name = parts(0)
        val value = state.evalTxExpressions(parts(1), state)
        state.envMap.put(name, value)
      }
      case Action.SetRsc(_) => ()
      case Action.SetSid(_) => ()
      case Action.SetUid(expr) => {
        state.uidRef.set(expr)
      }
      case Action.SetVar(expr) => {
        state.evalTxExpressions(expr, state) match {
          case expr if expr.startsWith("!") => {
            val name = expr.substring(1).replace("tx.", "").replace("TX.", "").toLowerCase()
            state.txMap.remove(name)
          }
          case expr if expr.contains("+=") => {
            val ex = expr.replace("tx.", "").replace("TX.", "").toLowerCase()
            val parts = ex.split("=")
            val name = parts(0)
            val incr = parts(1).toInt
            val value = state.txMap.get(name).map(_.toInt).getOrElse(0)
            state.txMap.put(name, (value + incr).toString)
          }
          case expr if expr.contains("-=") => {
            val ex = expr.replace("tx.", "").replace("TX.", "").toLowerCase()
            val parts = ex.split("=")
            val name = parts(0)
            val decr = parts(1).toInt
            val value = state.txMap.get(name).map(_.toInt).getOrElse(0)
            state.txMap.put(name, (value - decr).toString)
          }
          case expr if expr.contains("=") => {
            val ex = expr.replace("tx.", "").replace("TX.", "").toLowerCase()
            val parts = ex.split("=")
            val name = parts(0)
            val value = parts(1)
            state.txMap.put(name, value)
          }
          case expr if !expr.contains("=") => {
            val name = expr.replace("tx.", "").replace("TX.", "").toLowerCase()
            state.txMap.put(name, "0")
          }
          case expr => integration.logError("invalid setvar expression: " + expr)
        }
      }
      case Action.CtlAction.AuditEngine(id) => println("AuditEngine not implemented yet")
      case Action.CtlAction.AuditLogParts(id) => println("AuditLogParts not implemented yet")
      case Action.CtlAction.RequestBodyAccess(id) => ()
      case Action.CtlAction.RequestBodyProcessor(id) => println("RequestBodyProcessor not implemented yet")
      case Action.CtlAction.RuleEngine(value) => {
        localState = localState.copy(mode = EngineMode(value))
      }
      case Action.CtlAction.ForceRequestBodyVariable(id) =>()
      case Action.CtlAction.RuleRemoveByTag(tag) => println("RuleRemoveByTag not implemented yet")
      case Action.CtlAction.RuleRemoveTargetById(id, target) => println("RuleRemoveTargetById not implemented yet")
      case Action.CtlAction.RuleRemoveTargetByTag(tag, target) => {
        // println(s"RuleRemoveTargetByTag('${tag}', '${target}')")
        // TODO: needs to be implemented
      }
      case Action.CtlAction.RuleRemoveById(id) => {
        localState = localState.copy(disabledIds = localState.disabledIds + id)
      }
      case act => integration.logError("unimplemented action: " + act.getClass.getSimpleName)
    }
    localState
  }

}
