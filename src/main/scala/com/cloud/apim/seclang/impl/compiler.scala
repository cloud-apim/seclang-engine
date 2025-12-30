package com.cloud.apim.seclang.impl.compiler

import com.cloud.apim.seclang.model._

sealed trait CompiledItem

final case class RuleChain(rules: List[SecRule]) extends CompiledItem {
  require(rules.nonEmpty)
  lazy val phase: Int = rules.head.phase
  lazy val id: Option[Int] = rules.last.id.orElse(rules.head.id)
}

final case class MarkerItem(name: String) extends CompiledItem

final case class CompiledProgram(
  itemsByPhase: Map[Int, Vector[CompiledItem]],
  removedRuleIds: Set[Int]
)

object Compiler {

  private def unsupportedStatement(name: String): Unit = {
    println("Unsupported statement " + name)
  }

  def compile(configuration: Configuration): CompiledProgram = {
    val statements = configuration.statements
    val removed = statements.collect { case SecRuleRemoveById(_, ids) => ids }.flatten.toSet

    // flatten into CompiledItem with chain logic
    val items = scala.collection.mutable.ArrayBuffer.empty[CompiledItem]
    val it = statements.iterator

    while (it.hasNext) {
      it.next() match {
        case r: SecRule =>
          if (removed.contains(r.id.getOrElse(-1))) {
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
                    case _                =>
                      // ignore other directives for now
                  }
                  done = true
              }
            }
            items += RuleChain(chain.toList)
          } else {
            items += RuleChain(List(r))
          }
        case m: SecMarker =>
          items += MarkerItem(m.name)
        case s: SecRuleScript => unsupportedStatement("SecRuleScript")
        case s: SecAction => unsupportedStatement("SecAction")
        case s: SecRuleRemoveById => unsupportedStatement("SecRuleRemoveById")
        case s: SecRuleRemoveByMsg => unsupportedStatement("SecRuleRemoveByMsg")
        case s: SecRuleRemoveByTag => unsupportedStatement("SecRuleRemoveByTag")
        case s: SecRuleUpdateTargetById => unsupportedStatement("SecRuleUpdateTargetById")
        case s: SecRuleUpdateTargetByMsg => unsupportedStatement("SecRuleUpdateTargetByMsg")
        case s: SecRuleUpdateTargetByTag => unsupportedStatement("SecRuleUpdateTargetByTag")
        case s: SecRuleUpdateActionById => unsupportedStatement("SecRuleUpdateActionById")
        case s: EngineConfigDirective => unsupportedStatement("EngineConfigDirective")
        case s =>
          println(s"unknown statement ${s.getClass.getSimpleName}")
          // ignore for now (SecAction etc.)
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
      case m: MarkerItem =>
        byPhase(2) = byPhase(2) :+ m
        byPhase(1) = byPhase(1) :+ m
      case _ =>
        // TODO: support all statements here
    }

    CompiledProgram(byPhase.toMap, removed)
  }
}
