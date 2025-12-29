
package com.cloud.apim.seclang.impl.engine

import com.cloud.apim.seclang.impl.model._
import com.cloud.apim.seclang.impl.model.Operator._
import com.cloud.apim.seclang.impl.model.Action._
import com.cloud.apim.seclang.impl.compiler._

import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import scala.util.matching.Regex

private final case class RuntimeState(disabledIds: Set[Int], events: List[MatchEvent])

final class SecRulesEngine(program: CompiledProgram) {

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

    println(s"running phase ${phase} with ${items.size} items")
    // build marker index for this phase stream
    val markerIndex: Map[String, Int] = items.zipWithIndex.collect {
      case (MarkerItem(name), idx) => name -> idx
    }.toMap

    var i = 0
    var st = st0

    while (i < items.length) {
      items(i) match {
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

  private def evalChain(
      rules: List[Rule],
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
        val matched = evalRule(r, ctx)
        if (matched) {
          val msg = r.actions.collectFirst { case Msg(m) => m }
          if (msg.nonEmpty) collectedMsg = msg

          val status = r.actions.collectFirst { case Status(s) => s }
          if (status.nonEmpty) collectedStatus = status

          // disruptive action can appear anywhere, but we keep last
          val dis = r.actions.collectFirst {
            case Deny => Deny
            case Drop => Drop
            case Pass => Pass
          }
          if (dis.nonEmpty) disruptive = dis

          // skipAfter
          val sk = r.actions.collectFirst { case SkipAfter(m) => m }
          if (sk.nonEmpty) skipAfter = sk

          // runtime ctl disable
          r.actions.collect { case CtlRuleRemoveById(id) => id }.foreach { id =>
            st = st.copy(disabledIds = st.disabledIds + id)
          }

          st = st.copy(events = MatchEvent(r.id, msg, phase, r.raw) :: st.events)
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
          case Some(Deny) | Some(Drop) =>
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

  private def evalRule(rule: Rule, ctx: RequestContext): Boolean = {
    // 1) extract values from variables
    val extracted: List[String] = rule.variables.flatMap(v => resolveVariable(v, ctx))

    // 2) apply transformations
    val transforms = rule.actions.collect { case Transform(name) => name }.filterNot(_ == "none")
    val transformed = extracted.map(v => applyTransforms(v, transforms))

    // 3) operator match on ANY extracted value
    transformed.exists(v => evalOperator(rule.operator, v))
  }

  private def resolveVariable(sel: VariableSelector, ctx: RequestContext): List[String] = {
    val col = sel.collection
    val key = sel.key.map(_.toLowerCase)

    col match {
      case "REQUEST_URI" => List(ctx.uri)
      case "REQUEST_METHOD" => List(ctx.method)
      case "REQUEST_HEADERS" =>
        key match {
          case None =>
            ctx.headers.toList.flatMap { case (k, vs) => vs.map(v => s"$k: $v") }
          case Some(h) =>
            ctx.headers.collect {
              case (k, vs) if k.toLowerCase == h => vs
            }.flatten.toList
        }
      case "ARGS" =>
        key match {
          case None =>
            ctx.query.values.flatten.toList
          case Some(k) =>
            ctx.query.collect { case (kk, vs) if kk.toLowerCase == k => vs }.flatten.toList
        }
      case "REQUEST_BODY" =>
        ctx.body.toList
      case other =>
        // fallback: unsupported collection
        Nil
    }
  }

  private def applyTransforms(value: String, transforms: List[String]): String = {
    transforms.foldLeft(value) {
      case (v, "lowercase") => v.toLowerCase
      case (v, "trim")      => v.trim
      case (v, "urlDecodeUni") =>
        try URLDecoder.decode(v, StandardCharsets.UTF_8.name())
        catch { case _: Throwable => v }
      case (v, _) => v
    }
  }

  private def evalOperator(op: Operator, value: String): Boolean = op match {
    case UnconditionalTrue() => true
    case Contains(x)         => value.contains(x)
    case Streq(x)            => value == x
    case Pm(xs)              => xs.exists(value.contains)
    case Rx(pattern) =>
      try {
        val r: Regex = pattern.r
        r.findFirstIn(value).nonEmpty
      } catch {
        case _: Throwable => false
      }
    case Operator.Raw(_, _) =>
      // unsupported operator => "safe false"
      false
  }
}
