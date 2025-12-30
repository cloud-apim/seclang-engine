
package com.cloud.apim.seclang.impl.engine

import com.cloud.apim.seclang.impl.compiler._
import com.cloud.apim.seclang.model.Action.{CtlAction, Msg}
import com.cloud.apim.seclang.model._
import play.api.libs.json.Json

import java.net.{URLDecoder, URLEncoder}
import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.util.matching.Regex

private final case class RuntimeState(disabledIds: Set[Int], events: List[MatchEvent])

case class SecRulesEngineConfig()

object SecRulesEngineConfig {
  val default = SecRulesEngineConfig()
}

final class SecRulesEngine(program: CompiledProgram, files: Map[String, String] = Map.empty, config: SecRulesEngineConfig = SecRulesEngineConfig.default) {

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
        val matched = evalRule(r, ctx)
        if (matched) {
          val msg = r.actions.toList.flatMap(_.actions).collectFirst {
            case Action.Msg(m) => m
          }
          if (msg.nonEmpty) collectedMsg = msg

          val status = r.actions.toList.flatMap(_.actions).collectFirst {
            case Action.Status(s) => s
          }
          if (status.nonEmpty) collectedStatus = status

          // disruptive action can appear anywhere, but we keep last
          val dis = r.actions.toList.flatMap(_.actions).collectFirst {
            case Action.Block() => Action.Block()
            case Action.Deny => Action.Deny
            case Action.Drop => Action.Drop
            case Action.Pass => Action.Pass
            case Action.Allow(m) => Action.Allow(m)
          }
          if (dis.nonEmpty) disruptive = dis

          // skipAfter
          val sk = r.actions.toList.flatMap(_.actions).collectFirst { case Action.SkipAfter(m) => m }
          if (sk.nonEmpty) skipAfter = sk

          // runtime ctl disable
          r.actions.toList.flatMap(_.actions).collect { case CtlAction.RuleRemoveById(id) => id }.foreach { id =>
            st = st.copy(disabledIds = st.disabledIds + id)
          }

          st = st.copy(events = MatchEvent(r.id, msg, phase, Json.stringify(r.json)) :: st.events)
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

  private def evalRule(rule: SecRule, ctx: RequestContext): Boolean = {
    // 1) extract values from variables
    val extracted: List[String] = rule.variables.variables.flatMap(v => resolveVariable(v, ctx))

    // 2) apply transformations
    val transforms = rule.actions.toList.flatMap(_.actions).toList.collect { case Action.Transform(name) => name }.filterNot(_ == "none")
    val transformed = extracted.map(v => applyTransforms(v, transforms))

    // 3) operator match on ANY extracted value
    transformed.exists(v => evalOperator(rule.operator, v))
  }

  private def resolveVariable(sel: Variable, ctx: RequestContext): List[String] = {
    val (col, key) = sel match {
      case Variable.Simple(name) => (name, None)
      case Variable.Collection(collection, key) => (collection, key.map(_.toLowerCase()))
    }

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
    // https://github.com/owasp-modsecurity/ModSecurity/wiki/Reference-Manual-%28v3.x%29#transformation-functions
    transforms.foldLeft(value) {
      case (v, "lowercase") => v.toLowerCase
      case (v, "uppercase") => v.toUpperCase
      case (v, "trim")      => v.trim
      case (v, "urlDecodeUni") => try URLDecoder.decode(v, StandardCharsets.UTF_8.name()) catch { case _: Throwable => v }
      case (v, "base64Decode") => new String(Base64.getDecoder.decode(v))
      case (v, "base64DecodeExt") => new String(Base64.getDecoder.decode(v))
      case (v, "sqlHexDecode") => v // TODO: implement it
      case (v, "base64Encode") => Base64.getEncoder.encodeToString(v.getBytes(StandardCharsets.UTF_8))
      case (v, "cmdLine") => v // TODO: implement it
      case (v, "compressWhitespace") => v.replaceAll("\\s+", " ")
      case (v, "cssDecode") => v // TODO: implement it
      case (v, "escapeSeqDecode") => v // TODO: implement it
      case (v, "hexDecode") => try java.util.HexFormat.of().parseHex(v).mkString catch { case _: Throwable => v }
      case (v, "hexEncode") => try java.util.HexFormat.of().formatHex(v.getBytes(StandardCharsets.UTF_8)).toLowerCase catch { case _: Throwable => v }
      case (v, "htmlEntityDecode") => v // TODO: implement it
      case (v, "jsDecode") => v // TODO: implement it
      case (v, "length") => v.length.toString
      case (v, "md5") => try java.security.MessageDigest.getInstance("MD5").digest(v.getBytes(StandardCharsets.UTF_8)).map("%02x".format(_)).mkString catch { case _: Throwable => v }
      case (v, "none") => v
      case (v, "normalisePath") => v.split("/").filterNot(_.isEmpty).mkString("/")
      case (v, "normalizePath") => v.split("/").filterNot(_.isEmpty).mkString("/")
      case (v, "normalisePathWin") => v.split("[/\\\\]+").filterNot(_.isEmpty).mkString("/")
      case (v, "normalizePathWin") => v.split("[/\\\\]+").filterNot(_.isEmpty).mkString("/")
      case (v, "parityEven7bit") => v // TODO: implement it
      case (v, "parityOdd7bit") => v // TODO: implement it
      case (v, "parityZero7bit") => v // TODO: implement it
      case (v, "removeNulls") => v.replaceAll("\u0000", "")
      case (v, "removeWhitespace") => v.replaceAll("\\s+", "")
      case (v, "replaceComments") => v // TODO: implement it
      case (v, "removeCommentsChar") => v.replaceAll("--[^\r\n]*", "").replaceAll("/\\*", "").replaceAll("\\*/", "").replaceAll("#", "")
      case (v, "replaceNulls") => v // TODO: implement it
      case (v, "urlDecode") => try URLDecoder.decode(v, StandardCharsets.UTF_8.name()) catch { case _: Throwable => v }
      case (v, "urlEncode") => try URLEncoder.encode(v, StandardCharsets.UTF_8.name()) catch { case _: Throwable => v }
      case (v, "utf8toUnicode") => v // TODO: implement it
      case (v, "sha1") => try java.security.MessageDigest.getInstance("SHA-1").digest(v.getBytes(StandardCharsets.UTF_8)).map("%02x".format(_)).mkString catch { case _: Throwable => v }
      case (v, "trimLeft") => v.dropWhile(_ == ' ')
      case (v, "trimRight") => v.reverse.dropWhile(_ == ' ').reverse
      case (v, _) => v
    }
  }

  private def evalOperator(op: Operator, value: String): Boolean = op match {
    case Operator.UnconditionalMatch() => true
    case Operator.Contains(x)         => value.contains(x)
    case Operator.Streq(x)            => value == x
    case Operator.Pm(xs)              => xs.split(" ").exists(it => value.toLowerCase().contains(it.toLowerCase))
    case Operator.Rx(pattern) =>
      try {
        val r: Regex = pattern.r
        r.findFirstIn(value).nonEmpty
      } catch {
        case _: Throwable => false
      }
    case _ =>
      // unsupported operator => "safe false"
      println("unsupported operator: " + op)
      false
  }
}
