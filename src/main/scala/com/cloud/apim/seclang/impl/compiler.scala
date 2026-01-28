package com.cloud.apim.seclang.impl.compiler

import com.cloud.apim.seclang.impl.utils.RegexPool
import com.cloud.apim.seclang.model.ConfigDirective.{ComponentSignature, DefaultAction}
import com.cloud.apim.seclang.model._

object Compiler {

  private def unimplementedStatement(name: String): Unit = {
    println("unimplemented statement " + name)
  }

  def compileUnsafe(configuration: Configuration): CompiledProgram = {
    val statements = configuration.statements

    // Single pass to collect all metadata
    val removedBuilder = Set.newBuilder[Int]
    val removedRuleTagsBuilder = Set.newBuilder[String]
    val removedRuleMsgsBuilder = Set.newBuilder[String]
    val updatedTargetsByIdBuilder = Map.newBuilder[Int, SecRuleUpdateTargetById]
    val updatedTargetsByMsgBuilder = Map.newBuilder[String, SecRuleUpdateTargetByMsg]
    val updatedTargetsByTagBuilder = Map.newBuilder[String, SecRuleUpdateTargetByTag]
    val defaultActionsBuilder = scala.collection.mutable.ListBuffer.empty[(Int, Action)]

    statements.foreach {
      case SecRuleRemoveById(_, ids) => removedBuilder ++= ids
      case SecRuleRemoveByTag(_, tag) => removedRuleTagsBuilder += tag
      case SecRuleRemoveByMsg(_, msg) => removedRuleMsgsBuilder += msg
      case u @ SecRuleUpdateTargetById(_, id, _, _) => updatedTargetsByIdBuilder += (id -> u)
      case u @ SecRuleUpdateTargetByMsg(_, msg, _, _) => updatedTargetsByMsgBuilder += (msg -> u)
      case u @ SecRuleUpdateTargetByTag(_, tag, _, _) => updatedTargetsByTagBuilder += (tag -> u)
      case EngineConfigDirective(_, DefaultAction(actions)) if actions.hasPhase =>
        actions.actions.filterNot(_.isMetaData).foreach(a => defaultActionsBuilder += ((actions.phase.get, a)))
      case SecRule(_, variables, op, _, _) =>
        op match {
          case Operator.Rx(pattern) => RegexPool.regex(pattern)
          case _ => ()
        }
        variables.variables.foreach {
          case Variable.Collection(_, Some(key)) if key.startsWith("/") && key.endsWith("/") =>
            RegexPool.regex(key.substring(1, key.length - 1))
          case _ => ()
        }
      case _ => ()
    }

    val removed = removedBuilder.result()
    val removedRuleTags = removedRuleTagsBuilder.result()
    val removedRuleMsgs = removedRuleMsgsBuilder.result()
    val updatedTargetsById = updatedTargetsByIdBuilder.result()
    val updatedTargetsByMsg = updatedTargetsByMsgBuilder.result()
    val updatedTargetsByTag = updatedTargetsByTagBuilder.result()
    val defaultActions: Map[Int, List[Action]] = defaultActionsBuilder.toList.groupBy(_._1).mapValues(_.map(_._2)).toMap

    // flatten into CompiledItem with chain logic
    val items = scala.collection.mutable.ArrayBuffer.empty[CompiledItem]
    val it = statements.iterator
    var mode: Option[EngineMode] = None
    var webAppId: Option[String] = None

    while (it.hasNext) {
      it.next() match {
        case _r: SecRule => {
          val rid = _r.id.getOrElse(-1)
          val r = _r match {
            case rule if updatedTargetsById.contains(rid) => {
              val t = updatedTargetsById(rid)
              rule.copy(variables = rule.variables.copy(
                variables = rule.variables.variables ++ t.variables.variables,
                negatedVariables = rule.variables.negatedVariables ++ t.negatedVariables.variables,
              ))
            }
            case rule if rule.msgs.exists(m => updatedTargetsByMsg.contains(m)) => {
              rule.msgs.flatMap(m => updatedTargetsByMsg.get(m)).foldLeft(rule) {
                case (rule, t) => rule.copy(variables = rule.variables.copy(
                  variables = rule.variables.variables ++ t.variables.variables,
                  negatedVariables = rule.variables.negatedVariables ++ t.negatedVariables.variables,
                ))
              }
            }
            case rule if rule.tags.exists(m => updatedTargetsByTag.contains(m)) => {
              rule.msgs.flatMap(m => updatedTargetsByTag.get(m)).foldLeft(rule) {
                case (rule, t) => rule.copy(variables = rule.variables.copy(
                  variables = rule.variables.variables ++ t.variables.variables,
                  negatedVariables = rule.variables.negatedVariables ++ t.negatedVariables.variables,
                ))
              }
            }
            case _ => _r
          }
          if (removed.contains(r.id.getOrElse(-1)) || r.tags.exists(t => removedRuleTags.contains(t)) || r.msgs.exists(t => removedRuleMsgs.contains(t))) {
            // skip removed (if no id, can't remove)
          } else if (r.isChain) {
            val chain = scala.collection.mutable.ListBuffer[SecRule](r)
            var done = false
            while (!done && it.hasNext) {
              it.next() match {
                case rr: SecRule =>
                  chain += rr
                  done = !rr.isChain
                case m: SecMarker =>
                  // chain interrupted by marker -> still close chain
                  items += RuleChain(chain.toList)
                  items += MarkerItem(m.name)
                  done = true
                case other =>
                  // chain interrupted by non rule -> close chain, then keep other
                  items += RuleChain(chain.toList)
                  other match {
                    case mm: SecMarker =>
                      items += MarkerItem(mm.name)
                    case _ =>
                    // ignore other directives for now
                  }
                  done = true
              }
            }
            items += RuleChain(chain.toList)
          } else {
            items += RuleChain(List(r))
          }
        }
        case m: SecMarker => {
          items += MarkerItem(m.name)
        }
        case s: SecAction => {
          if (removed.contains(s.id.getOrElse(-1)) || s.tags.exists(t => removedRuleTags.contains(t)) || s.msgs.exists(t => removedRuleMsgs.contains(t))) {
            // skip removed (if no id, can't remove)
          } else {
            items += ActionItem(s.copy(actions = s.actions.copy(actions = s.actions.actions ++ defaultActions.get(s.phase).toList.flatten)))
          }
        }
        case s: SecRuleScript => unimplementedStatement("SecRuleScript")
        case s: SecRuleRemoveById => () // already implemented in remove
        case s: SecRuleRemoveByMsg => () // already implemented in remove
        case s: SecRuleRemoveByTag => () // already implemented in remove
        case s: SecRuleUpdateTargetById => ()
        case s: SecRuleUpdateTargetByMsg => ()
        case s: SecRuleUpdateTargetByTag => ()
        case s: SecRuleUpdateActionById => unimplementedStatement("SecRuleUpdateActionById")
        case EngineConfigDirective(_, DefaultAction(_)) => () // already handled
        case EngineConfigDirective(_, ComponentSignature(_)) => () // already handled
        case EngineConfigDirective(_, ConfigDirective.RuleEngine(expr)) => {
          mode = Some(EngineMode(expr))
        }
        case EngineConfigDirective(_, ConfigDirective.DataDir(_)) => ()
        case EngineConfigDirective(_, ConfigDirective.TmpDir(_)) => ()
        case EngineConfigDirective(_, ConfigDirective.WebAppId(id)) => {
          webAppId = Some(id)
        }
        case s: EngineConfigDirective => {
          // println(s.directive.getClass.getSimpleName)
          unimplementedStatement("EngineConfigDirective")
        }
        case s => {
          println(s"unknown statement ${s.getClass.getSimpleName}")
          // ignore for now (SecAction etc.)
        }
      }
    }

    // group by phase; markers are kept in all phases list where they appear; simplest: keep in phase 2 by default?
    // Better: markers should live in the current phase stream. We'll do: markers belong to "phase 2" unless explicitly used;
    // but CRS usually uses markers in a specific phase file anyway.
    // We'll place markers into phase 2 (safe default), but also duplicate into phase 1 if you want.
    val byPhase = scala.collection.mutable.Map.empty[Int, Vector[CompiledItem]].withDefaultValue(Vector.empty)
    items.foreach {
      case rc: RuleChain =>
        val ph = rc.phase
        byPhase(ph) = byPhase(ph) :+ rc
      case ai: ActionItem =>
        val ph = ai.phase
        byPhase(ph) = byPhase(ph) :+ ai
      case m: MarkerItem =>
        byPhase(5) = byPhase(5) :+ m
        byPhase(4) = byPhase(4) :+ m
        byPhase(3) = byPhase(3) :+ m
        byPhase(2) = byPhase(2) :+ m
        byPhase(1) = byPhase(1) :+ m
      case _ =>
      // TODO: support all statements here
    }

    SimpleCompiledProgram(byPhase.toMap, removed, mode, webAppId, configuration.hash)
  }
  def compile(configuration: Configuration): Either[SecLangError, CompiledProgram] = try {
    Right(compileUnsafe(configuration))
  } catch {
    case t: Throwable => Left(CompileError(t))
  }
}
