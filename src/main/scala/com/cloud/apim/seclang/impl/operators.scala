package com.cloud.apim.seclang.impl.engine

import com.cloud.apim.libinjection.LibInjection
import com.cloud.apim.seclang.impl.utils.{ByteRangeValidator, EncodingHelper, IpMatch, RegexPool}
import com.cloud.apim.seclang.model.{Operator, RuntimeState, SecLangIntegration}
import play.api.libs.json.{JsArray, JsString, Json}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import scala.util.matching.Regex

object EngineOperators {

  private def unimplementedOperator(op: String, integration: SecLangIntegration): Boolean = {
    integration.logDebug("unimplemented operator: " + op)
    false
  }

  private def unsupportedOperator(op: String, integration: SecLangIntegration): Boolean = {
    integration.logDebug("unsupported operator: " + op)
    false
  }

  private def unsupportedV3Operator(op: String, integration: SecLangIntegration): Boolean = {
    integration.logDebug("unsupported operator in v3: " + op)
    false
  }

  def evalOperator(ruleId: Int, op: Operator, value: String, files: Map[String, String], state: RuntimeState, integration: SecLangIntegration): Boolean = op match {
    // https://github.com/owasp-modsecurity/ModSecurity/wiki/Reference-Manual-%28v3.x%29#user-content-Operators
    case Operator.Negated(oop)         => !evalOperator(ruleId, oop, value, files, state, integration)
    case Operator.UnconditionalMatch() => true
    case Operator.Contains(x)          => value.contains(state.evalTxExpressions(x, state))
    case Operator.Streq(x)             => value == state.evalTxExpressions(x, state)
    case Operator.Pm(xs)               => state.evalTxExpressions(xs, state).split(" ").exists(it => value.toLowerCase().contains(it.toLowerCase))
    case Operator.Rx(pattern) => {
      if ((ruleId == 932240 || ruleId == 941170) && value.length > (10*1024)) { // TODO: find a way to avoid that ?
        return false
      }
      try {
        val r: Regex = RegexPool.regex(state.evalTxExpressions(pattern, state))
        val s = System.nanoTime()
        val rs = r.findFirstMatchIn(value)
        val d = Duration(System.nanoTime() - s, TimeUnit.NANOSECONDS)
        if (d.toMillis > 10000) {
          integration.logError("------------------------------------------------------------------------------------>")
          integration.logError(s"rule ${ruleId} match took: ${d.toMillis} ms for value length of ${value.length}")
          integration.logError("------------------------------------------------------------------------------------>")
        }
        rs.foreach { m =>
          val str = m.group(0)
          state.txMap.put("MATCHED_VAR".toLowerCase, str)
          state.txMap.put("MATCHED_VAR".toUpperCase, str)
          val list = JsArray((1 to m.groupCount).map { idx =>
            JsString(m.group(idx))
          })
          state.txMap.put("MATCHED_LIST", Json.stringify(list))
        }
        rs.nonEmpty
      } catch {
        case _: Throwable => false
      }
    }
    case Operator.BeginsWith(x) => value.startsWith(state.evalTxExpressions(x, state))
    case Operator.ContainsWord(x) => value.contains(state.evalTxExpressions(x, state).split(" ").filterNot(_.isEmpty).headOption.getOrElse(""))
    case Operator.EndsWith(x) => value.endsWith(state.evalTxExpressions(x, state))
    case Operator.Eq(x) => scala.util.Try(value.toInt).getOrElse(0) == scala.util.Try(state.evalTxExpressions(x, state).toInt).getOrElse(0)
    case Operator.Ge(x) => scala.util.Try(value.toInt).getOrElse(0) >= scala.util.Try(state.evalTxExpressions(x, state).toInt).getOrElse(0)
    case Operator.Gt(x) => scala.util.Try(value.toInt).getOrElse(0) > scala.util.Try(state.evalTxExpressions(x, state).toInt).getOrElse(0)
    case Operator.Le(x) => scala.util.Try(value.toInt).getOrElse(0) <= scala.util.Try(state.evalTxExpressions(x, state).toInt).getOrElse(0)
    case Operator.Lt(x) => scala.util.Try(value.toInt).getOrElse(0) < scala.util.Try(state.evalTxExpressions(x, state).toInt).getOrElse(0)
    case Operator.StrMatch(x) => value.toLowerCase.contains(state.evalTxExpressions(x, state).toLowerCase)
    case Operator.Within(x) =>
      val expr = state.evalTxExpressions(x, state).toLowerCase().split(" ")
      val v = value.toLowerCase
      if (v.isEmpty) {
        return false
      }
      expr.contains(v)
    case Operator.PmFromFile(xs) => {
      val fileName = state.evalTxExpressions(xs, state)
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
      val fileName = state.evalTxExpressions(xs, state)
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
    case Operator.DetectXSS(x) => LibInjection.isXSS(state.evalTxExpressions(value, state))
    case Operator.DetectSQLi(x) => LibInjection.isSQLi(state.evalTxExpressions(value, state))
    case Operator.ValidateUrlEncoding(x) => !EncodingHelper.validateUrlEncoding(value)
    case Operator.ValidateUtf8Encoding(x) => !EncodingHelper.validateUtf8Encoding(value)

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    case Operator.VerifyCC(x) => unimplementedOperator("verifyCC", integration) // TODO: implement it
    case Operator.VerifyCPF(x) => unimplementedOperator("verifyCPF", integration) // TODO: implement it
    case Operator.VerifySSN(x) => unimplementedOperator("verifySSN", integration) // TODO: implement it
    // case Operator.NoMatch(x) => unimplementedOperator("noMatch")
    case Operator.Rbl(x) => unimplementedOperator("rbl", integration) // TODO: implement it
    case Operator.RxGlobal(x) => unimplementedOperator("rxGlobal", integration) // TODO: implement it
    case Operator.FuzzyHash(x) => unimplementedOperator("fuzzyHash", integration) // TODO: implement it
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    case Operator.ValidateDTD(x) => unsupportedOperator("validateDTD", integration)
    case Operator.ValidateSchema(x) => unsupportedOperator("validateSchema", integration)
    case Operator.GeoLookup(x) => unsupportedOperator("geoLookup", integration)
    case Operator.InspectFile(x) => unsupportedOperator("inspectFile", integration)
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    case Operator.ValidateHash(x) => unsupportedV3Operator("validateHash", integration)
    case Operator.Rsub(x) => unsupportedV3Operator("rsub", integration)
    case Operator.GsbLookup(x) => unsupportedV3Operator("gsbLookup", integration)
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    case _ =>
      // unsupported operator => "safe false"
      integration.logError("unknown operator: " + op)
      false
  }
}
