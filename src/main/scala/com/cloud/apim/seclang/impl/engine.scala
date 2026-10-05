
package com.cloud.apim.seclang.impl.engine

import com.cloud.apim.seclang.model.Action.CtlAction
import com.cloud.apim.seclang.model._
import play.api.libs.json._

import java.util.concurrent.atomic.AtomicReference
import scala.collection.concurrent.TrieMap
import scala.collection.mutable.ArrayBuffer

// Helper case class to extract all action info in a single pass
private[engine] case class ExtractedActionInfo(
  msg: Option[String] = None,
  status: Option[Int] = None,
  disruptive: Option[Action] = None,
  skipAfter: Option[String] = None,
  logData: List[String] = Nil,
  idsToDisable: List[Int] = Nil
)

private[engine] object ExtractedActionInfo {
  def extract(actions: List[Action], evalExpr: String => String = identity): ExtractedActionInfo = {
    actions.foldLeft(ExtractedActionInfo()) { (acc, action) =>
      action match {
        case Action.Msg(m) if acc.msg.isEmpty => acc.copy(msg = Some(evalExpr(m)))
        case Action.Status(s) if acc.status.isEmpty => acc.copy(status = Some(s))
        case Action.LogData(m) => acc.copy(logData = m :: acc.logData)
        case Action.SkipAfter(m) if acc.skipAfter.isEmpty => acc.copy(skipAfter = Some(m))
        case Action.Block() if acc.disruptive.isEmpty => acc.copy(disruptive = Some(Action.Block()))
        case Action.Deny if acc.disruptive.isEmpty => acc.copy(disruptive = Some(Action.Deny))
        case Action.Drop if acc.disruptive.isEmpty => acc.copy(disruptive = Some(Action.Drop))
        case Action.Pass if acc.disruptive.isEmpty => acc.copy(disruptive = Some(Action.Pass))
        case Action.Allow(m) if acc.disruptive.isEmpty => acc.copy(disruptive = Some(Action.Allow(m)))
        case CtlAction.RuleRemoveById(id) => acc.copy(idsToDisable = id :: acc.idsToDisable)
        case _ => acc
      }
    }
  }
}

final class SecLangEngine(
  val program: CompiledProgram,
  config: SecLangEngineConfig = SecLangEngineConfig.default,
  files: Map[String, String] = Map.empty,
  engineTxMap: Option[TrieMap[String, String]] = None,
  integration: SecLangIntegration = DefaultSecLangIntegration.default
) {

  // resolved once for the life of the engine — a composed program folds its parts to answer it
  private val exclusions: RuleExclusions = program.exclusions

  def evaluate(ctx: RequestContext, phases: List[Int] = List(1, 2), evalTxMap: Option[TrieMap[String, String]] = None): EngineResult = {
    val pmode = program.mode.getOrElse(EngineMode.On)
    if (pmode.isOff) {
      EngineResult(Disposition.Continue, List.empty)
    } else {
      val txMap = evalTxMap.orElse(engineTxMap).getOrElse(new TrieMap[String, String]())
      // Initialize request_headers in txMap for use in operators like @endsWith %{request_headers.host}
      ctx.headers.toList.foreach { case (name, values) =>
        val lowerName = name.toLowerCase
        values.headOption.foreach { v =>
          if (ctx.isResponse) {
            txMap.put(s"response_headers.$lowerName", v)
          } else {
            txMap.put(s"request_headers.$lowerName", v)
          }
        }
      }
      // Initialize args in txMap for use in expressions like %{ARGS.xxx}
      if (ctx.isRequest) {
        ctx.args.foreach { case (name, values) =>
          val lowerName = name.toLowerCase
          values.headOption.foreach { v =>
            txMap.put(s"args.$lowerName", v)
          }
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
        webAppId = program.webAppId,
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
      // O(n) deduplication instead of O(n²) .distinct
      val seen = scala.collection.mutable.Set.empty[MatchEvent]
      val uniqueEvents = st.events.filter { e =>
        if (seen.contains(e)) false
        else { seen += e; true }
      }
      EngineResult(disp, uniqueEvents.reverse)
    }
  }
  // runtime disables (ctl:ruleRemoveById)
  def evaluateSafe(ctx: RequestContext, phases: List[Int] = List(1, 2), evalTxMap: Option[TrieMap[String, String]] = None): Either[SecLangError, EngineResult] = try {
    Right(evaluate(ctx, phases, evalTxMap))
  } catch {
    case t: Throwable => Left(EvaluationError(t))
  }

  private def runPhase(phase: Int, ctx: RequestContext, st0: RuntimeState): (Disposition, RuntimeState) = {
    val items = program.itemsForPhase(phase)

    // build marker index for this phase stream
    val markerIndex: Map[String, Int] = items.zipWithIndex.collect {
      case (MarkerItem(name), idx) => name -> idx
    }.toMap

    var i = 0
    var st = st0
    val disps = ArrayBuffer.empty[Disposition]
    // ctl:requestBodyProcessor changes how the rules after it read the body, in this phase and the next
    // ones. The context is rebuilt only when the processor changes, so the body is parsed once per change
    var cx = ctx.withBodyProcessor(st0.bodyProcessor.orElse(ctx.bodyProcessor))

    while (i < items.length) {
      items(i) match {
        // TODO: handle all needed statements
        case ActionItem(action) =>
          val (matched, stAfterMatch, skipToIdxOpt, dispOpt) = evalAction(action, phase, cx, st, markerIndex)
          st = stAfterMatch
          if (st.bodyProcessor.isDefined) cx = cx.withBodyProcessor(st.bodyProcessor)
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
          val ridDisabled = chainId.exists(st.disabledIds.contains) || chainId.exists(program.containsRemovedRuleId) ||
            // ctl:ruleRemoveByTag, the runtime sibling of SecRuleRemoveByTag
            (st.disabledTags.nonEmpty && rules.exists(_.tags.exists(st.disabledTags.contains))) ||
            // SecRuleRemoveByTag / ByMsg declared in another entry of the same configuration
            (exclusions.hasRemovals && exclusions.removes(chainId, rules.flatMap(_.tags), rules.flatMap(_.msgs)))

          if (ridDisabled) {
            i += 1
          } else {
            val (matched, stAfterMatch, skipToIdxOpt, dispOpt) =
              evalChain(rules, phase, cx, st, markerIndex)

            st = stAfterMatch
            if (st.bodyProcessor.isDefined) cx = cx.withBodyProcessor(st.bodyProcessor)

            dispOpt match {
              case Some(d) if !st.mode.isDetectionOnly => return (d, st)
              case Some(d) if st.mode.isDetectionOnly => {
                disps += d
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

    // Single pass extraction of all action info
    val extracted = ExtractedActionInfo.extract(actionsList)
    if (extracted.msg.nonEmpty) collectedMsg = extracted.msg
    if (extracted.status.nonEmpty) collectedStatus = extracted.status
    if (extracted.disruptive.nonEmpty) disruptive = extracted.disruptive
    if (extracted.skipAfter.nonEmpty) skipAfter = extracted.skipAfter
    val addLogData = extracted.logData.reverse
    if (extracted.idsToDisable.nonEmpty) {
      st = st.copy(disabledIds = st.disabledIds ++ extracted.idsToDisable)
    }

    st = EngineActions.performActions(action.id.getOrElse(0), actionsList, phase, ctx, st, integration, collectedMsg, addLogData, isLast = true)
    // Batch events and logs update in single copy
    st = st.copy(
      events = MatchEvent(action.id, extracted.msg, st.logs, phase, Json.stringify(action.json)) :: st.events,
      logs = List.empty
    )
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
    val events = ArrayBuffer.empty[MatchEvent]
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
          // Single pass extraction of all action info
          val extracted = ExtractedActionInfo.extract(actionsList, st.evalTxExpressions)
          if (extracted.msg.nonEmpty) collectedMsg = extracted.msg
          if (extracted.status.nonEmpty) collectedStatus = extracted.status
          if (extracted.disruptive.nonEmpty) disruptive = extracted.disruptive
          if (extracted.skipAfter.nonEmpty) skipAfter = extracted.skipAfter
          addLogData = addLogData ++ extracted.logData.reverse
          if (extracted.idsToDisable.nonEmpty) {
            st = st.copy(disabledIds = st.disabledIds ++ extracted.idsToDisable)
          }

          st = EngineActions.performActions(lastRuleId.getOrElse(0), actionsList, phase, ctx, st, integration, collectedMsg, addLogData, isLast)
          if (isLast) {
            // Batch events and logs update in single copy
            st = st.copy(
              events = MatchEvent(lastRuleId, extracted.msg, st.logs, phase, Json.stringify(r.json)) :: events.toList ++ st.events,
              logs = List.empty
            )
          } else {
            events += MatchEvent(lastRuleId, extracted.msg, st.logs, phase, Json.stringify(r.json))
            st = st.copy(logs = List.empty)
          }
        }
        if (!matched) {
          allMatched = false
        }
      }
    }

    if (!allMatched) {
      (false, st0.copy(disabledIds = st.disabledIds, disabledTags = st.disabledTags, events = st.events), None, None)
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

  private def evalRule(_rule: SecRule, lastRuleId: Option[Int], ctx: RequestContext, debug: Boolean, st: RuntimeState)(f: (String, String) => Unit): Boolean = {
    //println(s"eval rule ${rule.id} - ${lastRuleId} - ${st.mode}")
    // 0) targets added or excluded by a directive that lives in another compilation unit
    val rule = if (exclusions.hasTargetUpdates) {
      exclusions.targetUpdatesFor(_rule.id.orElse(lastRuleId), _rule.tags, _rule.msgs) match {
        case Nil     => _rule
        case updates =>
          updates.foldLeft(_rule) { case (r, u) =>
            r.copy(variables = r.variables.copy(
              variables = r.variables.variables ++ u.added,
              negatedVariables = r.variables.negatedVariables ++ u.excluded
            ))
          }
      }
    } else _rule
    // compute excluded targets for this rule, from its tags and from its id
    val excludedTargets: Set[String] =
      rule.tags.flatMap(tag => st.removedTargetsByTag.getOrElse(tag, Set.empty)) ++
        lastRuleId.flatMap(st.removedTargetsById.get).getOrElse(Set.empty)
    // a target naming a whole collection ("ARGS") drops the variable; one naming a member
    // ("ARGS:comment") only excludes that member, and rides the same negation the rule's own
    // `!ARGS:comment` uses. Treating both as "drop the collection" is how an exclusion meant for one
    // parameter used to stop the rule from looking at any of them.
    val (excludedCollections, excludedMembers) = excludedTargets.partition(!_.contains(":"))
    val excludedMemberVars: List[Variable] = excludedMembers.toList.map { t =>
      val parts = t.split(":")
      Variable.Collection(parts.head, Some(parts.tail.mkString(":").toLowerCase))
    }
    // 1) extract values from variables (filtering out excluded targets)
    val filteredVariables = rule.variables.variables.filterNot { v =>
      val name = v match {
        case Variable.Simple(n) => n.toUpperCase
        case Variable.Collection(n, _) => n.toUpperCase
      }
      excludedCollections.contains(name)
    }
    val filteredNegatedVariables = rule.variables.negatedVariables.filterNot { v =>
      val name = v match {
        case Variable.Simple(n) => n.toUpperCase
        case Variable.Collection(n, _) => n.toUpperCase
      }
      excludedCollections.contains(name)
    } ++ excludedMemberVars
    // each value now travels with the name it was resolved under, so `ARGS` yields
    // ("ARGS:comment" -> "…") rather than an anonymous list of strings
    def declaredName(v: Variable): String = v match {
      case Variable.Simple(name)              => name
      case Variable.Collection(name, None)    => name
      case Variable.Collection(name, Some(k)) => s"$name:$k"
    }
    val negatedVariables: List[(String, List[(String, String)])] = filteredNegatedVariables.map { v =>
      (declaredName(v), EngineVariables.resolveNamedVariable(v, false, true, ctx, debug, st, integration))
    }
    val extracted: List[(String, List[(String, String)])] = {
      val vrbls = filteredVariables.map { v =>
        (declaredName(v), EngineVariables.resolveNamedVariable(v, rule.variables.count, rule.variables.negated, ctx, debug, st, integration))
      }
      if (rule.variables.count) {
        val name = vrbls.headOption.map(v => s"&${v._1}").getOrElse("--")
        List((name, List((name, vrbls.flatMap(_._2).size.toString))))
      } else {
        vrbls
      }
    }
    // 2) apply transformations
    val actionsList = rule.actions.toList.flatMap(_.actions).toList
    val transforms = actionsList.collect { case Action.Transform(name) => name }.filterNot(_ == "none")
    val isMultiMatch = actionsList.contains(Action.MultiMatch)
    // TX.0 belongs to `capture`. Writing it on every match let a later link of a chain overwrite
    // what an earlier one captured, so `%{TX.0}` in the chain's logdata reported the wrong slice.
    val captures = actionsList.exists { case _: Action.Capture => true; case _ => false }

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
      case (name, members) => (name, members.flatMap { case (n, v) => applyTransformsWithMultiMatch(v, name, transforms).map(tv => (n, tv)) })
    }
    // Pre-index for O(1) lookups instead of O(n²)
    val negatedNamesSet = negatedVariables.map(_._1).toSet
    // the members a `!ARGS:comment` takes off the table, by name. matching them by value is what
    // made an exclusion for one parameter silently shield every other parameter that happened to
    // carry the same string
    val negatedMemberNames: Set[String] = negatedVariables.flatMap(_._2.map(_._1)).toSet

    // 3) operator match on ANY extracted value
    val matched_vars = ArrayBuffer.empty[String]
    val matched_var_names = ArrayBuffer.empty[String]
    val matched = transformed.map {
      case (name, members) => {
        if (negatedNamesSet.contains(name)) {
          (name, List.empty[(String, String)])
        } else if (negatedMemberNames.isEmpty) {
          (name, members)
        } else {
          (name, members.filterNot { case (n, _) => negatedMemberNames.contains(n) })
        }
      }
    }.filterNot(_._2.isEmpty).filter {
      case (_, members) =>
        members.filter { case (fullName, v) =>
          val m = EngineOperators.evalOperator(lastRuleId.getOrElse(-1), rule.operator, v, files, st, integration)
          if (m) {
            st.txMap.put("matched_var_name", fullName)
            st.txMap.put("matched_var", v)
            if (captures) st.txMap.put("0", v)
            matched_vars += v
            matched_var_names += fullName
            f(fullName, v)
          }
          m
        }.nonEmpty
    }.nonEmpty
    if (matched_vars.nonEmpty) {
      // Store directly in matchedVarsLists to avoid JSON serialization overhead
      st.matchedVarsLists.put("matched_vars", matched_vars.toSeq)
      st.matchedVarsLists.put("matched_var_names", matched_var_names.toSeq)
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