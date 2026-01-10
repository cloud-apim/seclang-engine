package com.cloud.apim.seclang.impl.engine

import com.cloud.apim.seclang.impl.utils.{FormUrlEncoded, MultipartVars, SimpleXmlSelector, XmlXPathParser}
import com.cloud.apim.seclang.model.{RequestContext, RuntimeState, SecLangIntegration, Variable}
import play.api.libs.json.{JsArray, JsBoolean, JsNull, JsNumber, JsObject, JsString, JsValue, Json}

import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64
import scala.util.{Failure, Success, Try}

object EngineVariables {
  // will be implemented one day
  private def unimplementedVariable(name: String, integration: SecLangIntegration): List[String] = {
    integration.logDebug("unimplemented variable: " + name)
    Nil
  }

  // unsupported because we don't want to support it
  private def unsupportedVariable(name: String, integration: SecLangIntegration): List[String] = {
    integration.logDebug("unsupported variable: " + name)
    Nil
  }

  // unsupported because seclang 3 does not support it anymore
  private def unsupportedV3Variable(name: String, integration: SecLangIntegration): List[String] = {
    integration.logDebug("unsupported variable in V3: " + name)
    Nil
  }

  private def deepMerge(m1: Map[String, List[String]], m2: Map[String, List[String]]): Map[String, List[String]] = {
    (m1.keySet ++ m2.keySet).iterator.map { k =>
      k -> (m1.getOrElse(k, Nil) ++ m2.getOrElse(k, Nil))
    }.toMap
  }

  private def jsToStr(js: JsValue): List[String] = js match {
    case JsString(s) => List(s)
    case JsNull => List("null")
    case JsNumber(s) => List(s.toString())
    case JsBoolean(s) => List(s.toString)
    case o @ JsObject(_) => List(Json.stringify(o))
    case a @ JsArray(_) => List(Json.stringify(a))
  }

  def resolveVariable(sel: Variable, count: Boolean, negated: Boolean, ctx: RequestContext, debug: Boolean, state: RuntimeState, integration: SecLangIntegration): List[String] = {
    // https://github.com/owasp-modsecurity/ModSecurity/wiki/Reference-Manual-%28v3.x%29#user-content-Variables
    val (col, key) = sel match {
      case Variable.Simple(name) => (name, None)
      case Variable.Collection(collection, key) => (collection, key.map {
        case k if k.startsWith("/") && k.endsWith("/") => k
        case k => k.toLowerCase()
      })
    }
    val rawPath = ctx.rawPath
    val path = ctx.path
    val rawQuery = ctx.rawQuery
    val datetime = LocalDateTime.now()
    val res: List[String] = col match {
      case "REQUEST_URI" => List(ctx.uri)
      case "REQUEST_METHOD" => List(ctx.method)
      case "REQUEST_HEADERS" | "RESPONSE_HEADERS" => {
        key match {
          case None =>
            //ctx.headers.toList.flatMap { case (k, vs) => vs.map(v => s"$k: $v") }
            ctx.headers.toList.flatMap(_._2)
          case Some(h) if h.startsWith("/") && h.endsWith("/") =>
            val r = h.substring(1, h.length - 1).r
            ctx.headers.collect {
              case (k, vs) if r.findFirstIn(k).isDefined => vs
            }.flatten.toList
          case Some(h) =>
            ctx.headers.collect {
              case (k, vs) if k.toLowerCase == h => vs
            }.flatten.toList
        }
      }
      case "ARGS" => {
        val args: Map[String, List[String]] = ctx.args
        key match {
          case None => ctx.flatArgs
          case Some(h) if h.startsWith("/") && h.endsWith("/") =>
            val r = h.substring(1, h.length - 1).r
            args.collect {
              case (k, vs) if r.findFirstIn(k).isDefined => vs
            }.flatten.toList
          case Some(h) =>
            args.collect {
              case (k, vs) if k.toLowerCase == h => vs
            }.flatten.toList
        }
      }
      case "ARGS_NAMES" => ctx.args.keySet.toList
      case "ARGS_COMBINED_SIZE" => List(ctx.flatArgs.map(_.length).sum.toString)
      case "TX" => {
        key match {
          case None => List.empty
          case Some(key) if key.startsWith("/") && key.endsWith("/") => {
            val r = key.substring(1, key.length - 1).r
            state.txMap.collect {
              case (k, vs) if r.findFirstIn(k).isDefined => vs
            }.toList
          }
          case Some(key) => state.txMap.get(key.toLowerCase()).toList
        }
      }
      case "ENV" => key match {
        case None => List.empty
        case Some(h) if h.startsWith("/") && h.endsWith("/") =>
          val r = h.substring(1, h.length - 1).r
          state.envMap.collect {
            case (k, vs) if r.findFirstIn(k).isDefined => vs
          }.toList
        case Some(key) => state.envMap.get(key).toList
      }
      case "REQUEST_BODY" => {
        if (ctx.isXml) List.empty
        else ctx.body.toList.map(_.utf8String)
      }
      case "RESPONSE_BODY" => ctx.body.toList.map(_.utf8String)
      case "REQUEST_HEADERS_NAMES" | "RESPONSE_HEADERS_NAMES" => ctx.headers.keySet.toList
      case "DURATION" => List((System.currentTimeMillis() - ctx.startTime).toString)
      case "PATH_INFO" => List(path)
      case "QUERY_STRING" => List(rawQuery)
      case "REMOTE_ADDR" => List(ctx.remoteAddr)
      case "REMOTE_HOST" => List(ctx.remoteAddr)
      case "REMOTE_PORT" => List(ctx.remotePort.toString)
      case "REMOTE_USER" => ctx.user.toList
      case "REQUEST_BASENAME" => path.split("/").lastOption.orElse(Some("")).toList
      case "REQUEST_COOKIES" => key match {
        case None =>
          ctx.cookies.toList.flatMap { case (k, vs) => vs }//.map(v => s"$k: $v") }
        case Some(h) if h.startsWith("/") && h.endsWith("/") =>
          if (debug) {
            println(s"REQUEST_COOKIES - key: ${key}")
          }
          val r = h.substring(1, h.length - 1).r
          ctx.cookies.collect {
            case (k, vs) if r.findFirstIn(k).isDefined => vs
          }.flatten.toList
        case Some(h) =>
          ctx.cookies.collect {
            case (k, vs) if k.toLowerCase == h => vs
          }.flatten.toList
      }
      case "REQUEST_COOKIES_NAMES" => ctx.cookies.keySet.toList
      case "REQUEST_FILENAME" => List(path)
      case "REQUEST_LINE" => List(s"${ctx.method.toUpperCase()} ${rawPath} ${ctx.protocol}")
      case "REQUEST_PROTOCOL" | "RESPONSE_PROTOCOL" => List(ctx.protocol)
      case "REQUEST_URI_RAW" => List(ctx.uriRaw)
      case "REQUEST_CONTENT_TYPE" | "RESPONSE_CONTENT_TYPE" => ctx.contentType.toList
      case "RESPONSE_STATUS" => ctx.status.map(_.toString).toList
      case "REQUEST_BODY_LENGTH" | "RESPONSE_CONTENT_LENGTH" => ctx.contentLength.toList
      // case "SERVER_ADDR" => unimplementedVariable("SERVER_ADDR") // from ctx.variables
      // case "SERVER_NAME" => unimplementedVariable("SERVER_NAME") // from ctx.variables
      // case "SERVER_PORT" => unimplementedVariable("SERVER_PORT") // from ctx.variables
      case "STATUS_LINE" => List(ctx.statusLine)
      case "TIME" => List(datetime.format(DateTimeFormatter.ofPattern("HH:mm:ss")))
      case "TIME_DAY" => List(datetime.getDayOfMonth.toString)
      case "TIME_EPOCH" => List((System.currentTimeMillis / 1000).toString)
      case "TIME_HOUR" => List(datetime.getHour.toString)
      case "TIME_MIN" => List(datetime.getMinute.toString)
      case "TIME_MON" => List(datetime.getMonthValue.toString)
      case "TIME_SEC" => List(datetime.getSecond.toString)
      case "TIME_WDAY" => List(datetime.getDayOfWeek.getValue.toString)
      case "TIME_YEAR" => List(datetime.getYear.toString)
      case "UNIQUE_ID" => List(ctx.requestId)
      case "USERID" => Option(state.uidRef.get).toList
      case "FILES" => {
        ctx.body match {
          case Some(_) if ctx.isMultipartFormData => ctx.files
          case _ => List.empty
        }
      }
      case "FILES_COMBINED_SIZE" => {
        if (ctx.isMultipartFormData && ctx.files.nonEmpty) {
          ctx.contentType.toList
        } else {
          List.empty
        }
      }
      case "FILES_NAMES" => ctx.filesNames
      case "FILES_SIZES" => List(ctx.filesNames.size.toString)
      case "FILES_TMPNAMES" => List.empty
      case "FILES_TMP_CONTENT" => List.empty
      case "REQBODY_PROCESSOR" => ctx.requestBodyProcessor
      case "MULTIPART_PART_HEADERS" => {
        ctx.body match {
          case Some(_) if ctx.isMultipartFormData => {
            val headers = ctx.multipartFormDataBody.getOrElse(Map.empty)
            key match {
              case None =>
                //headers.toList.flatMap { case (k, vs) => vs.map(v => s"$k: $v") }
                headers.toList.flatMap(_._2)
              case Some(h) if h.startsWith("/") && h.endsWith("/") =>
                val r = h.substring(1, h.length - 1).r
                headers.collect {
                  case (k, vs) if r.findFirstIn(k).isDefined => vs
                }.flatten.toList
              case Some(h) =>
                headers.collect {
                  case (k, vs) if k.toLowerCase == h => vs
                }.flatten.toList
            }
          }
          case _ => List.empty
        }
      }
      case "ARGS_GET" => {
        val headers = ctx.query
        key match {
          case None =>
            headers.toList.flatMap(_._2)
          case Some(h) if h.startsWith("/") && h.endsWith("/") =>
            val r = h.substring(1, h.length - 1).r
            headers.collect {
              case (k, vs) if r.findFirstIn(k).isDefined => vs
            }.flatten.toList
          case Some(h) =>
            headers.collect {
              case (k, vs) if k.toLowerCase == h => vs
            }.flatten.toList
        }
      }
      case "ARGS_GET_NAMES" =>  ctx.query.keySet.toList
      case "ARGS_POST" => {
        ctx.body match {
          case Some(_) if ctx.isXwwwFormUrlEncoded => {
            val headers = ctx.wwwFormEncodedBody.getOrElse(Map.empty)
            key match {
              case None =>
                headers.toList.flatMap(_._2)
              case Some(h) if h.startsWith("/") && h.endsWith("/") =>
                val r = h.substring(1, h.length - 1).r
                headers.collect {
                  case (k, vs) if r.findFirstIn(k).isDefined => vs
                }.flatten.toList
              case Some(h) =>
                headers.collect {
                  case (k, vs) if k.toLowerCase == h => vs
                }.flatten.toList
            }
          }
          case _ => List.empty
        }
      }
      case "ARGS_POST_NAMES" => {
        ctx.body match {
          case Some(_) if ctx.isXwwwFormUrlEncoded => ctx.wwwFormEncodedBody.map(_.keySet.toList).getOrElse(List.empty)
          case _ => List.empty
        }
      }
      case "XML" => {
        ctx.body match {
          case Some(_) if key.isDefined && ctx.isXml => ctx.xmlBody.flatMap(_.get(key.get)).getOrElse(List.empty)
          case _ => List.empty
        }
      }
      case "MATCHED_VAR" => state.txMap.get("matched_var").toList
      case "MATCHED_VARS" =>  state.txMap.get("matched_vars").flatMap(v => Json.parse(v).asOpt[List[String]]).getOrElse(List.empty)
      case "MATCHED_VAR_NAME" => state.txMap.get("matched_var_name").toList
      case "MATCHED_VARS_NAMES" => state.txMap.get("matched_var_names").flatMap(v => Json.parse(v).asOpt[List[String]]).getOrElse(List.empty)
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      case "AUTH_TYPE" => unimplementedVariable("AUTH_TYPE", integration) // TODO: implement it
      case "FULL_REQUEST" => unimplementedVariable("FULL_REQUEST", integration) // TODO: implement it
      case "FULL_REQUEST_LENGTH" => unimplementedVariable("FULL_REQUEST_LENGTH", integration) // TODO: implement it
      case "HIGHEST_SEVERITY" => unimplementedVariable("HIGHEST_SEVERITY", integration) // TODO: implement it
      case "INBOUND_DATA_ERROR" => unimplementedVariable("INBOUND_DATA_ERROR", integration) // TODO: implement it
      case "MODSEC_BUILD" => unimplementedVariable("MODSEC_BUILD", integration) // TODO: implement it
      case "MSC_PCRE_LIMITS_EXCEEDED" => unimplementedVariable("MSC_PCRE_LIMITS_EXCEEDED", integration) // TODO: implement it
      case "MULTIPART_CRLF_LF_LINES" => unimplementedVariable("MULTIPART_CRLF_LF_LINES", integration) // TODO: implement it
      case "MULTIPART_FILENAME" => unimplementedVariable("MULTIPART_FILENAME", integration) // TODO: implement it
      case "MULTIPART_NAME" => unimplementedVariable("MULTIPART_NAME", integration) // TODO: implement it
      case "MULTIPART_STRICT_ERROR" => unimplementedVariable("MULTIPART_STRICT_ERROR", integration) // TODO: implement it
      case "MULTIPART_UNMATCHED_BOUNDARY" => unimplementedVariable("MULTIPART_UNMATCHED_BOUNDARY", integration) // TODO: implement it
      case "OUTBOUND_DATA_ERROR" => unimplementedVariable("OUTBOUND_DATA_ERROR", integration) // TODO: implement it
      case "REQBODY_ERROR" => unimplementedVariable("REQBODY_ERROR", integration) // TODO: implement it
      case "REQBODY_ERROR_MSG" => unimplementedVariable("REQBODY_ERROR_MSG", integration) // TODO: implement it
      case "RULE" => unimplementedVariable("RULE", integration) // TODO: implement it
      case "SDBM_DELETE_ERROR" => unimplementedVariable("SDBM_DELETE_ERROR", integration) // TODO: implement it
      case "SESSION" => unimplementedVariable("SESSION", integration) // TODO: implement it
      case "SESSIONID" => unimplementedVariable("SESSIONID", integration) // TODO: implement it
      case "URLENCODED_ERROR" => unimplementedVariable("URLENCODED_ERROR", integration) // TODO: implement it
      case "WEBAPPID" => unimplementedVariable("WEBAPPID", integration) // TODO: implement it
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      case "GEO" => unsupportedVariable("GEO", integration)
      case "PERF_ALL" => unsupportedV3Variable("PERF_ALL", integration)
      case "PERF_COMBINED" => unsupportedV3Variable("PERF_COMBINED", integration)
      case "PERF_GC" => unsupportedV3Variable("PERF_GC", integration)
      case "PERF_LOGGING" => unsupportedV3Variable("PERF_LOGGING", integration)
      case "PERF_PHASE1" => unsupportedV3Variable("PERF_PHASE1", integration)
      case "PERF_PHASE2" => unsupportedV3Variable("PERF_PHASE2", integration)
      case "PERF_PHASE3" => unsupportedV3Variable("PERF_PHASE3", integration)
      case "PERF_PHASE4" => unsupportedV3Variable("PERF_PHASE4", integration)
      case "PERF_PHASE5" => unsupportedV3Variable("PERF_PHASE5", integration)
      case "PERF_RULES" => unsupportedV3Variable("PERF_RULES", integration)
      case "PERF_SREAD" => unsupportedV3Variable("PERF_SREAD", integration)
      case "PERF_SWRITE" => unsupportedV3Variable("PERF_SWRITE", integration)
      case "SCRIPT_BASENAME" => unsupportedV3Variable("SCRIPT_BASENAME", integration)
      case "SCRIPT_FILENAME" => unsupportedV3Variable("SCRIPT_FILENAME", integration)
      case "SCRIPT_GID" => unsupportedV3Variable("SCRIPT_GID", integration)
      case "SCRIPT_GROUPNAME" => unsupportedV3Variable("SCRIPT_GROUPNAME", integration)
      case "SCRIPT_MODE" => unsupportedV3Variable("SCRIPT_MODE", integration)
      case "SCRIPT_UID" => unsupportedV3Variable("SCRIPT_UID", integration)
      case "SCRIPT_USERNAME" => unsupportedV3Variable("SCRIPT_USERNAME", integration)
      case "STREAM_INPUT_BODY" => unsupportedV3Variable("STREAM_INPUT_BODY", integration)
      case "STREAM_OUTPUT_BODY" => unsupportedV3Variable("STREAM_OUTPUT_BODY", integration)
      case "WEBSERVER_ERROR_LOG" => unsupportedV3Variable("WEBSERVER_ERROR_LOG", integration)
      case "USERAGENT_IP" => unsupportedV3Variable("USERAGENT_IP", integration)
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      case other =>
        ctx.variables.get(other)
          .orElse(ctx.variables.get(other.toLowerCase))
          .orElse(ctx.variables.get(other.toUpperCase)) match {
          case Some(v) => List(v)
          case None =>
            // fallback: unsupported collection
            integration.logDebug("unknown variable: " + other)
            Nil
        }
    }
    res
  }
}
