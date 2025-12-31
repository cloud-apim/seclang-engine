package com.cloud.apim.seclang.impl.utils

import org.w3c.dom.{Document, NodeList}
import org.xml.sax.InputSource

import java.io.StringReader
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.{XPath, XPathConstants, XPathFactory}
import scala.util.Try
import scala.util.matching.Regex

object Implicits {
  implicit class BetterString(val obj: String) extends AnyVal {
    def toIntOption: Option[Int] = {
      Try(obj.toInt).toOption
    }
  }
}

object MultipartVars {

  private val NameRx: Regex =
    """(?i)\bcontent-disposition\s*:\s*.*?\bname="([^"]+)"""".r

  // Extrait boundary depuis Content-Type: multipart/form-data; boundary=...
  def boundaryFromContentType(contentTypeOpt: Option[String]): Option[String] = {
    contentTypeOpt.map(_.trim).flatMap { ct =>
      val lower = ct.toLowerCase
      if (!lower.startsWith("multipart/form-data")) None
      else {
        // boundary peut être quoted ou pas
        val B1 = """(?i)\bboundary="?([^";]+)"?""".r
        B1.findFirstMatchIn(ct).map(_.group(1))
      }
    }
  }

  def multipartPartHeaders(body: String, boundary: String): Map[String, Vector[String]] = {
    val startBoundary = s"--$boundary"
    val endBoundary   = s"--$boundary--"

    var inPart = false
    var inHeaders = false
    var currentPartName: Option[String] = None
    val acc = scala.collection.mutable.HashMap.empty[String, Vector[String]].withDefaultValue(Vector.empty)

    body.linesIterator.foreach { raw =>
      val line = raw.stripLineEnd

      if (line == startBoundary) {
        // nouveau part
        inPart = true
        inHeaders = true
        currentPartName = None
      } else if (line == endBoundary) {
        // fin du multipart
        inPart = false
        inHeaders = false
        currentPartName = None
      } else if (inPart && inHeaders) {
        if (line.isEmpty) {
          inHeaders = false
        } else {
          line match {
            case NameRx(name) =>
              currentPartName = Some(name)
              acc.update(name, acc(name) :+ line)

            case _ =>
              currentPartName.foreach { name =>
                acc.update(name, acc(name) :+ line)
              }
          }
        }
      } else {
      }
    }

    acc.toMap
  }

  def multipartPartHeadersFromCtx(body: String, contentTypeOpt: Option[String]): Option[Map[String, Vector[String]]] =
    boundaryFromContentType(contentTypeOpt).map(b => multipartPartHeaders(body, b))
}

object FormUrlEncoded {

  /** Parse application/x-www-form-urlencoded into Map[key -> Seq(values)] */
  def parse(body: String, charset: String = "UTF-8"): Map[String, List[String]] = {
    if (body == null || body.isEmpty) return Map.empty

    def dec(s: String): String =
      URLDecoder.decode(s, StandardCharsets.UTF_8.name()) // charset param could be used; UTF-8 is typical

    body
      .split("&", -1)                    // keep empty segments
      .iterator
      .map(_.trim)
      .filter(_.nonEmpty)
      .flatMap { pair =>
        val idx = pair.indexOf('=')
        if (idx < 0) {
          val k = dec(pair)
          Some(k -> "")
        } else {
          val k = dec(pair.substring(0, idx))
          val v = dec(pair.substring(idx + 1))
          Some(k -> v)
        }
      }
      .toList
      .groupBy(_._1)
      .mapValues(_.map(_._2))
  }

  /** Convenience: keep only the last value for each key */
  def parseLast(body: String, charset: String = "UTF-8"): Map[String, String] =
    parse(body, charset).mapValues(_.lastOption.getOrElse("")).toMap
}

object XmlXPathParser {

  def parseXml(xml: String): Document = {
    val factory = DocumentBuilderFactory.newInstance()
    factory.setNamespaceAware(true) // important pour XPath
    val builder = factory.newDocumentBuilder()
    builder.parse(new InputSource(new StringReader(xml)))
  }

  def xpathNodes(doc: Document, expr: String): NodeList = {
    val xpath: XPath = XPathFactory.newInstance().newXPath()
    xpath.evaluate(expr, doc, XPathConstants.NODESET).asInstanceOf[NodeList]
  }

  def xpathValues(xml: String, expr: String): List[String] = {
    val doc = parseXml(xml)
    val xpath = XPathFactory.newInstance().newXPath()
    val nodes = xpath
      .evaluate(expr, doc, XPathConstants.NODESET)
      .asInstanceOf[NodeList]

    (0 until nodes.getLength).toList
      .map(i => nodes.item(i).getTextContent)
  }

  def xpathString(doc: Document, expr: String): String = {
    val xpath: XPath = XPathFactory.newInstance().newXPath()
    xpath.evaluate(expr, doc)
  }
}

object StatusCodes {
  val httpStatusTexts: Map[Int, String] = Map(
    // 1xx – Informational
    100 -> "Continue",
    101 -> "Switching Protocols",
    102 -> "Processing",

    // 2xx – Success
    200 -> "OK",
    201 -> "Created",
    202 -> "Accepted",
    203 -> "Non-Authoritative Information",
    204 -> "No Content",
    205 -> "Reset Content",
    206 -> "Partial Content",

    // 3xx – Redirection
    300 -> "Multiple Choices",
    301 -> "Moved Permanently",
    302 -> "Found",
    303 -> "See Other",
    304 -> "Not Modified",
    307 -> "Temporary Redirect",
    308 -> "Permanent Redirect",

    // 4xx – Client Error
    400 -> "Bad Request",
    401 -> "Unauthorized",
    402 -> "Payment Required",
    403 -> "Forbidden",
    404 -> "Not Found",
    405 -> "Method Not Allowed",
    406 -> "Not Acceptable",
    408 -> "Request Timeout",
    409 -> "Conflict",
    410 -> "Gone",
    413 -> "Payload Too Large",
    415 -> "Unsupported Media Type",
    418 -> "I'm a teapot",
    422 -> "Unprocessable Entity",
    429 -> "Too Many Requests",

    // 5xx – Server Error
    500 -> "Internal Server Error",
    501 -> "Not Implemented",
    502 -> "Bad Gateway",
    503 -> "Service Unavailable",
    504 -> "Gateway Timeout",
    505 -> "HTTP Version Not Supported"
  )
  def get(status: Int): Option[String] = httpStatusTexts.get(status)
}