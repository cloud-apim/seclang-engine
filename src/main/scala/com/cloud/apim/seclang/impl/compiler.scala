package com.cloud.apim.seclang.impl.compiler

import com.cloud.apim.seclang.model.ConfigDirective.{ComponentSignature, DefaultAction}
import com.cloud.apim.seclang.model._

object Compiler {

  private def unimplementedStatement(name: String): Unit = {
    println("unimplemented statement " + name)
  }

  def compile(configuration: Configuration): CompiledProgram = {
    val statements = configuration.statements
    val removed = statements.collect { case SecRuleRemoveById(_, ids) => ids }.flatten.toSet
    val removedRuleTags: Set[String] = statements.collect { case SecRuleRemoveByTag(_, tag) => tag }.toSet
    val removedRuleMsgs: Set[String] = statements.collect { case SecRuleRemoveByMsg(_, msg) => msg }.toSet
    val defaultActions: Map[Int, List[Action]] = statements.collect {
      case EngineConfigDirective(_, DefaultAction(actions)) if actions.hasPhase => actions.actions
        .filterNot(_.isMetaData)
        .map(a => (actions.phase.get, a))
    }.flatten.groupBy(_._1).mapValues(_.map(_._2))

    // flatten into CompiledItem with chain logic
    val items = scala.collection.mutable.ArrayBuffer.empty[CompiledItem]
    val it = statements.iterator
    var mode: EngineMode = EngineMode.On

    while (it.hasNext) {
      it.next() match {
        case r: SecRule => {
          if (removed.contains(r.id.getOrElse(-1)) || r.tags.exists(t => removedRuleTags.contains(t)) || r.msgs.exists(t => removedRuleMsgs.contains(t))) {
            // skip removed (if no id, can't remove)
          } else if (r.isChain) {
            val chain = scala.collection.mutable.ListBuffer[SecRule](r)
            var done = false
            while (!done && it.hasNext) {
              it.next() match {
                case rr: SecRule =>
                  chain += rr.copy(commentBlock = None)
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
            items += ActionItem(s.copy(commentBlock = None, actions = s.actions.copy(actions = s.actions.actions ++ defaultActions.get(s.phase).toList.flatten)))
          }
        }
        case s: SecRuleScript => unimplementedStatement("SecRuleScript")
        case s: SecRuleRemoveById => () // already implemented in remove
        case s: SecRuleRemoveByMsg => () // already implemented in remove
        case s: SecRuleRemoveByTag => () // already implemented in remove
        case s: SecRuleUpdateTargetById => unimplementedStatement("SecRuleUpdateTargetById")
        case s: SecRuleUpdateTargetByMsg => unimplementedStatement("SecRuleUpdateTargetByMsg")
        case s: SecRuleUpdateTargetByTag => unimplementedStatement("SecRuleUpdateTargetByTag")
        case s: SecRuleUpdateActionById => unimplementedStatement("SecRuleUpdateActionById")
        case EngineConfigDirective(_, DefaultAction(_)) => () // already handled
        case EngineConfigDirective(_, ComponentSignature(_)) => () // already handled
        case EngineConfigDirective(_, ConfigDirective.RuleEngine(expr)) => {
          mode = EngineMode(expr)
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

    CompiledProgram(byPhase.toMap, removed, mode, configuration.hash)
  }
}
