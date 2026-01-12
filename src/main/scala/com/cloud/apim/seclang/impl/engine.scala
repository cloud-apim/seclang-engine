
package com.cloud.apim.seclang.impl.engine

import com.cloud.apim.seclang.model.Action.CtlAction
import com.cloud.apim.seclang.model._
import play.api.libs.json._

import java.util.concurrent.atomic.AtomicReference
import scala.collection.concurrent.TrieMap

final class SecLangEngine(val program: CompiledProgram, config: SecLangEngineConfig, files: Map[String, String] = Map.empty, integration: SecLangIntegration) {

  // runtime disables (ctl:ruleRemoveById)
  def evaluate(ctx: RequestContext, phases: List[Int] = List(1, 2)): EngineResult = {
    val pmode = program.mode.getOrElse(EngineMode.On)
    if (pmode.isOff) {
      EngineResult(Disposition.Continue, List.empty)
    } else {
      val txMap = new TrieMap[String, String]()
      // Initialize request_headers in txMap for use in operators like @endsWith %{request_headers.host}
      ctx.headers.toList.foreach { case (name, values) =>
        val lowerName = name.toLowerCase
        values.headOption.foreach { v =>
          txMap.put(s"request_headers.$lowerName", v)
        }
      }
      val envMap = {
        val tm = new TrieMap[String, String]()
        integration.getEnv.foreach(tm += _)
        tm
      }
      val uidRef = new AtomicReference[String](null)
      val init = RuntimeState(
        mode = pmode,
        disabledIds = Set.empty,
        events = Nil,
        logs = Nil,
        txMap = txMap,
        envMap = envMap,
        uidRef = uidRef
      )
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
      EngineResult(disp, st.events.distinct.reverse)
    }
  }

  private def runPhase(phase: Int, ctx: RequestContext, st0: RuntimeState): (Disposition, RuntimeState) = {
    val items = program.itemsForPhase(phase)

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
          val ridDisabled = chainId.exists(st.disabledIds.contains) || chainId.exists(program.containsRemovedRuleId)

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
    var addLogData = List.empty[String]

    val msg = actionsList.collectFirst {
      case Action.Msg(m) => m
    }
    if (msg.nonEmpty) collectedMsg = msg
    addLogData = addLogData ++ actionsList.collect {
      case Action.LogData(m) => st.evalTxExpressions(m)
    }
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

    st = EngineActions.performActions(action.id.getOrElse(0), actionsList, phase, ctx, st, integration, collectedMsg, addLogData, isLast = true)
    st = st.copy(events = MatchEvent(action.id, msg, st.logs, phase, Json.stringify(action.json)) :: st.events)
    st = st.copy(logs = List.empty)
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
    var addLogData = List.empty[String]

    val isChain = rules.size > 1
    val firstRule = rules.head

    // Evaluate sequentially; if one fails -> chain fails
    rules.foreach { r =>
      lastRuleId = r.id.orElse(lastRuleId)
      val isLast = rules.last == r
      if (allMatched) {
        val matched = evalRule(r, lastRuleId, ctx, debug = lastRuleId.exists(id => config.debugRules.contains(id)), st) { (_, _) =>
          // TODO: implement actions: https://github.com/owasp-modsecurity/ModSecurity/wiki/Reference-Manual-%28v3.x%29#actions
          val actionsList = r.actions.toList.flatMap(_.actions)
          val msg = actionsList.collectFirst {
            case Action.Msg(m) => st.evalTxExpressions(m)
          }
          if (msg.nonEmpty) collectedMsg = msg
          addLogData = addLogData ++ actionsList.collect {
            case Action.LogData(m) => st.evalTxExpressions(m)
          }

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

          st = EngineActions.performActions(lastRuleId.getOrElse(0), actionsList, phase, ctx, st, integration, collectedMsg, addLogData, isLast)
          if (isLast) {
            st = st.copy(events = MatchEvent(lastRuleId, msg, st.logs, phase, Json.stringify(r.json)) :: events ++ st.events)
          } else {
            events = events :+ MatchEvent(lastRuleId, msg, st.logs, phase, Json.stringify(r.json))
          }
          st = st.copy(logs = List.empty)
        }
        if (!matched) {
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

  private def evalRule(rule: SecRule, lastRuleId: Option[Int], ctx: RequestContext, debug: Boolean, st: RuntimeState)(f: (String, String) => Unit): Boolean = {
    //println(s"eval rule ${rule.id} - ${lastRuleId} - ${st.mode}")
    // 0) compute excluded targets for this rule based on its tags
    val excludedTargets: Set[String] = rule.tags.flatMap(tag => st.removedTargetsByTag.getOrElse(tag, Set.empty))
    // 1) extract values from variables (filtering out excluded targets)
    val filteredVariables = rule.variables.variables.filterNot { v =>
      val name = v match {
        case Variable.Simple(n) => n.toUpperCase
        case Variable.Collection(n, _) => n.toUpperCase
      }
      excludedTargets.contains(name)
    }
    val filteredNegatedVariables = rule.variables.negatedVariables.filterNot { v =>
      val name = v match {
        case Variable.Simple(n) => n.toUpperCase
        case Variable.Collection(n, _) => n.toUpperCase
      }
      excludedTargets.contains(name)
    }
    val negatedVariables: List[(String, List[String])] = filteredNegatedVariables.map {
      case v @ Variable.Simple(name) => (name, EngineVariables.resolveVariable(v, false, true, ctx, debug, st, integration))
      case v @ Variable.Collection(name, key) => (s"$name:${key.getOrElse("")}", EngineVariables.resolveVariable(v, false, true, ctx, debug, st, integration))
    }
    val extracted: List[(String, List[String])] = {
      val vrbls = filteredVariables.map {
        case v @ Variable.Simple(name) => (name, EngineVariables.resolveVariable(v, rule.variables.count, rule.variables.negated, ctx, debug, st, integration))
        case v @ Variable.Collection(name, key) => (s"$name:${key.getOrElse("")}", EngineVariables.resolveVariable(v, rule.variables.count, rule.variables.negated, ctx, debug, st, integration))
      }
      if (rule.variables.count) {
        List((vrbls.headOption.map(v => s"&${v._1}").getOrElse("--"), vrbls.flatMap(_._2).size.toString :: Nil))
      } else {
        vrbls
      }
    }
    // 2) apply transformations
    val actionsList = rule.actions.toList.flatMap(_.actions).toList
    val transforms = actionsList.collect { case Action.Transform(name) => name }.filterNot(_ == "none")
    val isMultiMatch = actionsList.contains(Action.MultiMatch)

    // For multiMatch, we need to test after each transformation step
    def applyTransformsWithMultiMatch(value: String, name: String, transforms: List[String]): List[String] = {
      if (!isMultiMatch) {
        List(EngineTransformations.applyTransforms(value, name, transforms, integration))
      } else {
        // Return value after each transformation step for multiMatch
        transforms.scanLeft(value) { (v, t) =>
          EngineTransformations.applyTransforms(v, name, List(t), integration)
        }.distinct
      }
    }

    val transformed = extracted.map {
      case (name, values) => (name, values.flatMap(v => applyTransformsWithMultiMatch(v, name, transforms)))
    }
    val negatedTransformed = negatedVariables.map {
      case (name, values) => (name, values.flatMap(v => applyTransformsWithMultiMatch(v, name, transforms)))
    }
    // 3) operator match on ANY extracted value
    var matched_vars = List.empty[String]
    var matched_var_names = List.empty[String]
    val matched = transformed.map {
      case (name, values) => {
        val strict = negatedVariables.exists(_._1 == name)
        if (strict) {
          (name, List.empty[String])
        } else {
          var currentValues = values
          negatedVariables.filter(v => v._1.startsWith(name)).foreach { nameStartingWithNegatedVariableName =>
            val (nname, _) = nameStartingWithNegatedVariableName
            val nvalues = negatedTransformed.find(_._1 == nname).get._2
            currentValues = currentValues.filterNot(v => nvalues.contains(v))
          }
          (name, currentValues)
        }
      }
    }.filterNot(_._2.isEmpty).filter {
      case (name, values) =>
        values.filter { v =>
          val m = EngineOperators.evalOperator(lastRuleId.getOrElse(-1), rule.operator, v, files, st, integration)
          if (m) {
            // For variables without a key (like ARGS_NAMES), include the matched value in the name
            // This matches ModSecurity behavior where MATCHED_VAR_NAME includes the specific item
            val fullName = if (name.contains(":")) name else s"$name:$v"
            st.txMap.put("matched_var_name", fullName)
            st.txMap.put("matched_var", v)//values.mkString(" "))
            st.txMap.put("0", v)//values.mkString(" "))
            matched_vars = matched_vars :+ v//values.mkString(" ")
            matched_var_names = matched_var_names :+ fullName
            f(fullName, v)
          }
          m
        }.nonEmpty
    }.nonEmpty
    if (matched_vars.nonEmpty) {
      st.txMap.put("matched_vars", Json.stringify(JsArray(matched_vars.map(v => JsString(v)))))
      st.txMap.put("matched_var_names", Json.stringify(JsArray(matched_var_names.map(v => JsString(v)))))
    }
    if (debug) {
      println("---------------------------------------------------------")
      println(s"debug for rule: ${lastRuleId.getOrElse(0)}")
      println("---------------------------------------------------------")
      //println(s"ctx: \n${Json.prettyPrint(ctx.json)}\n")
      println(s"variables: \n${rule.variables.variables.map {
        case Variable.Simple(name) if rule.variables.count => s"&${name}"
        case Variable.Simple(name) => name
        case Variable.Collection(name, key) if rule.variables.count => s"&$name:$key"
        case Variable.Collection(name, key) => s"$name:$key"
      }.mkString("\n")}\n")
      // println(s"negated_variables: \n${rule.variables.negatedVariables.map {
      //   case Variable.Simple(name) if rule.variables.count => s"&${name}"
      //   case Variable.Simple(name) => name
      //   case Variable.Collection(name, key) if rule.variables.count => s"&$name:$key"
      //   case Variable.Collection(name, key) => s"$name:$key"
      // }.mkString("\n")}\n")
      println(s"extracted: \n${extracted.mkString("\n")}\n")
      println(s"variables_values: ${transformed.mkString("\n")}\n")
      println(s"matched_vars: \n${matched_vars.zipWithIndex.map { case (v, idx) => s"${matched_var_names(idx)}: ${v}" }.mkString("\n")}\n")
      println(s"matched: ${matched}")
      println("---------------------------------------------------------")
    }
    matched
  }
}