package com.cloud.apim.seclang.impl.engine

import com.cloud.apim.seclang.impl.utils.{EscapeSeq, MsUrlDecode, Transformations}
import com.cloud.apim.seclang.model.SecLangIntegration

import java.net.{URLDecoder, URLEncoder}
import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.util.Try

object EngineTransformations {

  private def unimplementedTransform(name: String, v: String, integration: SecLangIntegration): String = {
    integration.logDebug("unimplemented transform " + name)
    v
  }

  private def unsupportedTransform(name: String, v: String, integration: SecLangIntegration): String = {
    integration.logDebug("unsupported transform " + name)
    v
  }

  def applyTransforms(value: String, transforms: List[String], integration: SecLangIntegration): String = {
    // https://github.com/owasp-modsecurity/ModSecurity/wiki/Reference-Manual-%28v3.x%29#transformation-functions
    transforms.foldLeft(value) {
      case (v, "lowercase") => v.toLowerCase
      case (v, "uppercase") => v.toUpperCase
      case (v, "trim")      => v.trim
      case (v, "urlDecodeUni") => try MsUrlDecode.urlDecodeMs(v, StandardCharsets.UTF_8, plusAsSpace = true) catch { case _: Throwable => v }
      case (v, "base64Decode") => Try(new String(Base64.getDecoder.decode(v))).getOrElse(v)
      case (v, "base64DecodeExt") => Try(new String(Base64.getDecoder.decode(v))).getOrElse(v)
      case (v, "base64Encode") => Base64.getEncoder.encodeToString(v.getBytes(StandardCharsets.UTF_8))
      case (v, "compressWhitespace") => v.replaceAll("\\s+", " ")
      case (v, "hexDecode") => try java.util.HexFormat.of().parseHex(v).mkString catch { case _: Throwable => v }
      case (v, "hexEncode") => try java.util.HexFormat.of().formatHex(v.getBytes(StandardCharsets.UTF_8)).toLowerCase catch { case _: Throwable => v }
      case (v, "length") => v.length.toString
      case (v, "md5") => try java.security.MessageDigest.getInstance("MD5").digest(v.getBytes(StandardCharsets.UTF_8)).map("%02x".format(_)).mkString catch { case _: Throwable => v }
      case (v, "none") => v
      case (v, "normalisePath") => v.split("/").filterNot(_.isEmpty).mkString("/")
      case (v, "normalizePath") => v.split("/").filterNot(_.isEmpty).mkString("/")
      case (v, "normalisePathWin") => v.split("[/\\\\]+").filterNot(_.isEmpty).mkString("/")
      case (v, "normalizePathWin") => v.split("[/\\\\]+").filterNot(_.isEmpty).mkString("/")
      case (v, "removeNulls") => v.replaceAll("\u0000", "")
      case (v, "removeWhitespace") => v.replaceAll("\\s+", "")
      case (v, "removeCommentsChar") => v.replaceAll("--[^\r\n]*", "").replaceAll("/\\*", "").replaceAll("\\*/", "").replaceAll("#", "")
      case (v, "urlDecode") => try URLDecoder.decode(v, StandardCharsets.UTF_8.name()) catch { case _: Throwable => v }
      case (v, "urlEncode") => try URLEncoder.encode(v, StandardCharsets.UTF_8.name()) catch { case _: Throwable => v }
      case (v, "sha1") => try java.security.MessageDigest.getInstance("SHA-1").digest(v.getBytes(StandardCharsets.UTF_8)).map("%02x".format(_)).mkString catch { case _: Throwable => v }
      case (v, "trimLeft") => v.dropWhile(_ == ' ')
      case (v, "trimRight") => v.reverse.dropWhile(_ == ' ').reverse
      case (v, "utf8toUnicode") => v //Transformations.utf8toUnicode(v)// TODO: fixme
      case (v, "jsDecode") => Transformations.jsDecode(v)
      case (v, "htmlEntityDecode") => Transformations.htmlEntityDecode(v)
      case (v, "cssDecode") => Transformations.cssDecode(v)
      case (v, "replaceComments") => Transformations.replaceComments(v)
      case (v, "cmdLine") => Transformations.cmdLine(v)
      case (v, "escapeSeqDecode") => EscapeSeq.escapeSeqDecode(v)
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      case (v, "replaceNulls") => unimplementedTransform("replaceNulls", v, integration) // TODO: implement it
      case (v, "parityEven7bit") => unimplementedTransform("parityEven7bit", v, integration) // TODO: implement it
      case (v, "parityOdd7bit") => unimplementedTransform("parityOdd7bit", v, integration) // TODO: implement it
      case (v, "parityZero7bit") => unimplementedTransform("parityZero7bit", v, integration) // TODO: implement it
      case (v, "sqlHexDecode") => unimplementedTransform("sqlHexDecode", v, integration) // TODO: implement it
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      case (v, _) => v
    }
  }
}
