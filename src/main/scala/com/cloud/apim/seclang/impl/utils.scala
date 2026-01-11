package com.cloud.apim.seclang.impl.utils

import com.comcast.ip4s._
import org.apache.commons.text.StringEscapeUtils
import org.w3c.dom.{Document, Node, NodeList}
import org.xml.sax.{ErrorHandler, InputSource, SAXParseException}

import java.io.{StringReader, StringWriter}
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.stream.{XMLInputFactory, XMLStreamConstants, XMLStreamReader}
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.{OutputKeys, TransformerFactory}
import javax.xml.xpath.{XPathConstants, XPathFactory}
import scala.collection.concurrent.TrieMap
import scala.collection.mutable.ArrayBuffer
import scala.util.Try
import scala.util.matching.Regex

object RegexPool {
  private val regexCache = new TrieMap[String, Regex]()
  def regex(regex: String): Regex = {
    regexCache.getOrElseUpdate(regex, regex.r)
  }
}

object HashUtilsFast {
  private val HEX = "0123456789abcdef".toCharArray

  def sha512Hex(input: String): String = {
    val md    = MessageDigest.getInstance("SHA-512")
    val bytes = md.digest(input.getBytes(StandardCharsets.UTF_8))

    val out = new Array[Char](bytes.length * 2)
    var i = 0
    while (i < bytes.length) {
      val v = bytes(i) & 0xff
      out(i * 2)     = HEX(v >>> 4)
      out(i * 2 + 1) = HEX(v & 0x0f)
      i += 1
    }
    new String(out)
  }
}


object Implicits {
  implicit class BetterString(val obj: String) extends AnyVal {
    def toIntOption: Option[Int] = {
      Try(obj.toInt).toOption
    }
  }
}

object MultipartVars {

  private val NameRx: Regex =
    RegexPool.regex("""(?i)\bcontent-disposition\s*:\s*.*?\bname="([^"]+)"""")

  // Extrait boundary depuis Content-Type: multipart/form-data; boundary=...
  def boundaryFromContentType(contentTypeOpt: Option[String]): Option[String] = {
    contentTypeOpt.map(_.trim).flatMap { ct =>
      val lower = ct.toLowerCase
      if (!lower.startsWith("multipart/form-data")) None
      else {
        // boundary peut être quoted ou pas
        val B1 = RegexPool.regex("""(?i)\bboundary="?([^";]+)"?""")
        B1.findFirstMatchIn(ct).map(_.group(1))
      }
    }
  }

  def multipartPartHeaders(body: String, boundary: String): Map[String, List[String]] = {
    val startBoundary = s"--$boundary"
    val endBoundary   = s"--$boundary--"

    var inPart = false
    var inHeaders = false
    var currentPartName: Option[String] = None
    val acc = scala.collection.mutable.HashMap.empty[String, List[String]].withDefaultValue(List.empty)

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

  def multipartPartHeadersFromCtx(body: String, contentTypeOpt: Option[String]): Option[Map[String, List[String]]] =
    boundaryFromContentType(contentTypeOpt).map(b => multipartPartHeaders(body, b))
}

object FormUrlEncoded {

  /** Parse application/x-www-form-urlencoded into Map[key -> Seq(values)] */
  def parse(body: String, charset: String = "UTF-8"): Map[String, List[String]] = {
    if (body == null || body.isEmpty) return Map.empty

    def dec(s: String): String = try {
      URLDecoder.decode(s, StandardCharsets.UTF_8.name()) // charset param could be used; UTF-8 is typical
    } catch {
      case e: Throwable => s // TODO: print exception...
    }

    body
      .split("&", -1)                    // keep empty segments
      .iterator
      .map(_.trim)
      .filter(_.nonEmpty)
      .flatMap { pair =>
        try {
          val idx = pair.indexOf('=')
          if (idx < 0) {
            val k = dec(pair)
            Some(k -> "")
          } else {
            val k = dec(pair.substring(0, idx))
            val v = dec(pair.substring(idx + 1))
            Some(k -> v)
          }
        } catch {
          case t: Throwable =>
            //t.printStackTrace()
            None
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
    builder.setErrorHandler(new ErrorHandler() {
      override def warning(exception: SAXParseException): Unit = ()
      override def error(exception: SAXParseException): Unit = ()
      override def fatalError(exception: SAXParseException): Unit = throw exception
    })
    builder.parse(new InputSource(new StringReader(xml)))
  }

  def evalXPath(xml: String, xpathExpr: String): List[String] = {
    val doc = parseXml(xml)
    evalXPath(doc, xpathExpr)
  }

  def evalXPath(doc: Document, xpathExpr: String): List[String] = {
    try {
      val xp = XPathFactory.newInstance().newXPath()
      val expr = xp.compile(xpathExpr)

      val nodes =
        try expr.evaluate(doc, XPathConstants.NODESET).asInstanceOf[NodeList]
        catch {
          case _: Throwable => null
        }

      if (nodes != null && nodes.getLength > 0) {
        val b = List.newBuilder[String]
        var i = 0
        while (i < nodes.getLength) {
          b += nodeToString(nodes.item(i))
          i += 1
        }
        b.result()
      } else {
        val v = expr.evaluate(doc, XPathConstants.STRING).asInstanceOf[String]
        if (v == null || v.isEmpty) Nil else List(v)
      }
    } catch {
      case t: Throwable =>
        // t.printStackTrace() TODO: print exception
        Nil
    }
  }

  def nodeToString(node: Node): String = {
    node.getNodeType match {
      case Node.TEXT_NODE | Node.ATTRIBUTE_NODE =>
        node.getNodeValue

      case _ =>
        val tf = TransformerFactory.newInstance().newTransformer()
        tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
        tf.setOutputProperty(OutputKeys.INDENT, "no")

        val sw = new StringWriter()
        tf.transform(new DOMSource(node), new StreamResult(sw))
        sw.toString
    }
  }

  //def xpathNodes(doc: Document, expr: String): NodeList = {
  //  val xpath: XPath = XPathFactory.newInstance().newXPath()
  //  xpath.evaluate(expr, doc, XPathConstants.NODESET).asInstanceOf[NodeList]
  //}
  //def xpathValues(xml: String, expr: String): List[String] = {
  //  val doc = parseXml(xml)
  //  val xpath = XPathFactory.newInstance().newXPath()
  //  val nodes = xpath
  //    .evaluate(expr, doc, XPathConstants.NODESET)
  //    .asInstanceOf[NodeList]
  //  (0 until nodes.getLength).toList
  //    .map(i => nodes.item(i).getTextContent)
  //}
  //def xpathString(doc: Document, expr: String): String = {
  //  val xpath: XPath = XPathFactory.newInstance().newXPath()
  //  xpath.evaluate(expr, doc)
  //}
}

object SimpleXmlSelector {

  def select(xml: String, path: String): List[String] = try {
    path match {
      case "//@*" => selectAllAttributes(xml)
      case "/*"   => selectAllText(xml)
      case _      => Nil
    }
  } catch {
    case _: Throwable => Nil
  }

  def selectAttributesAndText(xml: String): Map[String, List[String]] = {
    try {
      val (attrs, texts) = parseOnce(xml)
      Map(
        "/*" -> texts,
        "//@*" -> attrs,
      )
    } catch {
      case _: Throwable => Map.empty
    }
  }

  private def selectAllAttributes(xml: String): List[String] = {
    selectAllAttributes(createReader(xml))
  }

  private def selectAllAttributes(xsr: XMLStreamReader): List[String] = {
    val attrs = ArrayBuffer.empty[String]
    try {
      while (xsr.hasNext) {
        xsr.next() match {
          case XMLStreamConstants.START_ELEMENT =>
            val n = xsr.getAttributeCount
            var i = 0
            while (i < n) {
              val v = xsr.getAttributeValue(i)
              if (v != null) attrs += v
              i += 1
            }
          case _ => ()
        }
      }
      attrs.toList
    } finally {
      try xsr.close() catch { case _: Throwable => () }
    }
  }

  private def selectAllText(xml: String): List[String] = {
    selectAllText(createReader(xml))
  }

  private def selectAllText(xsr: XMLStreamReader): List[String] = {
    val texts = ArrayBuffer.empty[String]
    try {
      while (xsr.hasNext) {
        xsr.next() match {
          case XMLStreamConstants.CHARACTERS | XMLStreamConstants.CDATA =>
            val t = xsr.getText.trim
            if (t.nonEmpty) texts += t
          case _ => ()
        }
      }
      texts.toList
    } finally {
      try xsr.close() catch { case _: Throwable => () }
    }
  }

  private def createReader(xml: String): XMLStreamReader = {
    val f = XMLInputFactory.newInstance()
    try f.setProperty(XMLInputFactory.SUPPORT_DTD, false) catch { case _: Throwable => () }
    try f.setProperty("javax.xml.stream.isSupportingExternalEntities", false) catch { case _: Throwable => () }
    try f.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, true) catch { case _: Throwable => () }
    try f.setProperty(XMLInputFactory.IS_COALESCING, true) catch { case _: Throwable => () }
    f.createXMLStreamReader(new StringReader(xml))
  }

  private def parseOnce(xml: String): (List[String], List[String]) = {
    val attrs = scala.collection.mutable.ArrayBuffer.empty[String]
    val texts = scala.collection.mutable.ArrayBuffer.empty[String]

    val f = XMLInputFactory.newInstance()
    setIfSupported(f, XMLInputFactory.SUPPORT_DTD, java.lang.Boolean.FALSE)
    setIfSupported(f, "javax.xml.stream.isSupportingExternalEntities", java.lang.Boolean.FALSE)
    setIfSupported(f, XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, java.lang.Boolean.TRUE)
    setIfSupported(f, XMLInputFactory.IS_COALESCING, java.lang.Boolean.TRUE)

    val xsr = f.createXMLStreamReader(new StringReader(xml))
    try {
      while (xsr.hasNext) {
        xsr.next() match {
          case XMLStreamConstants.START_ELEMENT =>
            val n = xsr.getAttributeCount
            var i = 0
            while (i < n) {
              val v = xsr.getAttributeValue(i)
              if (v != null) attrs += v
              i += 1
            }

          case XMLStreamConstants.CHARACTERS | XMLStreamConstants.CDATA =>
            val t = xsr.getText.trim
            if (t.nonEmpty) texts += t

          case _ => ()
        }
      }
      (attrs.toList, texts.toList)
    } finally {
      try xsr.close() catch { case _: Throwable => () }
    }
  }

  private def setIfSupported(f: XMLInputFactory, key: String, value: AnyRef): Unit = {
    try f.setProperty(key, value)
    catch { case _: IllegalArgumentException => () }
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

object Transformations {

  /** ModSecurity-like jsDecode transformation */
  def jsDecode(in: String): String = {
    val out = new StringBuilder(in.length)

    def hexVal(c: Char): Int =
      if (c >= '0' && c <= '9') c - '0'
      else if (c >= 'a' && c <= 'f') 10 + (c - 'a')
      else if (c >= 'A' && c <= 'F') 10 + (c - 'A')
      else -1

    def parseHex2(s: String, i: Int): Int = {
      if (i + 1 >= s.length) -1
      else {
        val h1 = hexVal(s.charAt(i))
        val h2 = hexVal(s.charAt(i + 1))
        if (h1 < 0 || h2 < 0) -1 else (h1 << 4) | h2
      }
    }

    def parseHex4(s: String, i: Int): Int = {
      if (i + 3 >= s.length) -1
      else {
        val h1 = hexVal(s.charAt(i))
        val h2 = hexVal(s.charAt(i + 1))
        val h3 = hexVal(s.charAt(i + 2))
        val h4 = hexVal(s.charAt(i + 3))
        if (h1 < 0 || h2 < 0 || h3 < 0 || h4 < 0) -1
        else (h1 << 12) | (h2 << 8) | (h3 << 4) | h4
      }
    }

    var i = 0
    while (i < in.length) {
      val c = in.charAt(i)

      if (c != '\\' || i + 1 >= in.length) {
        out.append(c)
        i += 1
      } else {
        val n = in.charAt(i + 1)

        n match {
          case 'u' =>
            val v = parseHex4(in, i + 2)
            if (v >= 0) {
              val decoded =
                if (v >= 0xFF01 && v <= 0xFF5E) v - 0xFEE0
                else (v & 0xFF) // keep low byte only, zero high byte
              out.append(decoded.toChar)
              i += 6
            } else {
              out.append('\\'); out.append('u')
              i += 2
            }

          // \xHH
          case 'x' =>
            val v = parseHex2(in, i + 2)
            if (v >= 0) {
              out.append((v & 0xFF).toChar)
              i += 4
            } else {
              out.append('\\'); out.append('x')
              i += 2
            }

          // octal: \0 .. \777 (up to 3 digits)
          case d if d >= '0' && d <= '7' =>
            var j = i + 1
            var count = 0
            var acc = 0
            while (j < in.length && count < 3) {
              val ch = in.charAt(j)
              if (ch >= '0' && ch <= '7') {
                acc = (acc << 3) | (ch - '0')
                j += 1
                count += 1
              } else {
                j = in.length // break
              }
            }
            out.append((acc & 0xFF).toChar)
            i += (1 + count)

          // classic JS escapes
          case 'n'  => out.append('\n'); i += 2
          case 'r'  => out.append('\r'); i += 2
          case 't'  => out.append('\t'); i += 2
          case 'b'  => out.append('\b'); i += 2
          case 'f'  => out.append('\f'); i += 2
          case 'v'  => out.append(0x0B.toChar); i += 2
          case '\\' => out.append('\\'); i += 2
          case '\'' => out.append('\''); i += 2
          case '"'  => out.append('"'); i += 2
          case '/'  => out.append('/'); i += 2

          // unknown escape: keep as-is (backslash + char)
          case _ =>
            out.append('\\')
            out.append(n)
            i += 2
        }
      }
    }

    out.result()
  }

  private val hexEntity: Regex =
    RegexPool.regex("""&#x([0-9a-fA-F]{1,4});?""")

  private val decEntity: Regex =
    RegexPool.regex("""&#([0-9]{1,5});?""")

  private val namedEntities: Map[String, Char] = Map(
    "quot" -> '"',
    "nbsp" -> ' ',
    "lt"   -> '<',
    "gt"   -> '>'
  )

  def htmlEntityDecode(input: String): String = try {
    return StringEscapeUtils.unescapeHtml4(input)
    var out = input

    // Hexadecimal entities
    try {
      out = hexEntity.replaceAllIn(out, m => {
        val value = Integer.parseInt(m.group(1), 16) & 0xff
        value.toChar.toString
      })
    } catch {
      case t: Throwable => // TODO: print exception
    }

    // Decimal entities
    try {
      out = decEntity.replaceAllIn(out, m => {
        val value = Integer.parseInt(m.group(1), 10) & 0xff
        value.toChar.toString
      })
    } catch {
      case t: Throwable => // TODO: print exception
    }

    // Named entities (with or without ;)
    try {
      namedEntities.foreach { case (name, char) =>
        out = out
          .replace(s"&$name;", char.toString)
          .replace(s"&$name", char.toString)
      }
    } catch {
      case t: Throwable => // TODO: print exception
    }

    out
  } catch {
    case t: Throwable =>
      //t.printStackTrace()
      input
  }

  /** ModSecurity / CRS "cmdLine" transformation
   *
   * - delete \  "  '  ^
   * - delete spaces before / and before (
   * - replace , and ; with a space
   * - collapse all whitespace (tab/newline/etc.) to single space
   * - lowercase
   */
  def cmdLine(v: String): String = {
    if (v == null) return ""

    val noEscapes =
      v
        .replace("\\", "")
        .replace("\"", "")
        .replace("'", "")
        .replace("^", "")

    val noSpacesBeforeSlashOrParen =
      noEscapes
        .replaceAll("""\s+(/)""", "$1")   // spaces before /
        .replaceAll("""\s+(\()""", "$1")  // spaces before (

    val commasSemicolonsToSpace =
      noSpacesBeforeSlashOrParen.replaceAll("""[;,]""", " ")

    val normalizedSpaces =
      commasSemicolonsToSpace
        .replaceAll("""\s+""", " ")
        .trim

    normalizedSpaces.toLowerCase(java.util.Locale.ROOT)
  }

  def utf8toUnicode(v: String): String = {
    if (v == null || v.isEmpty) return v

    val sb = new StringBuilder(v.length * 6)

    var i = 0
    while (i < v.length) {
      val cp = v.codePointAt(i)

      if (cp <= 0xFFFF) {
        // "%u" est du texte, on formate uniquement l'hexadécimal
        sb.append("%u")
        sb.append(f"${cp}%04X")
      } else {
        // convertir en surrogate pair UTF-16
        val uPrime = cp - 0x10000
        val high = 0xD800 + (uPrime >> 10)
        val low  = 0xDC00 + (uPrime & 0x3FF)

        sb.append("%u"); sb.append(f"${high}%04X")
        sb.append("%u"); sb.append(f"${low}%04X")
      }

      i += Character.charCount(cp)
    }

    sb.result()
  }

  private def isHex(c: Char): Boolean =
    (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')

  private def isCssWhitespace(c: Char): Boolean =
    c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f'

  /** cssDecode (ModSecurity-like)
   * - CSS 2.x escapes: \HHHHHH (1..6 hex) optionally followed by a single whitespace
   * - "evasion": backslash + non-hex => drop '\' (ja\vascript => javascript)
   * - uses only up to two bytes: value masked to 0xFFFF
   */
  def cssDecode(in: String): String = {
    if (in == null || in.isEmpty) return in

    val out = new java.lang.StringBuilder(in.length)
    var i = 0
    val n = in.length

    while (i < n) {
      val c = in.charAt(i)
      if (c != '\\') {
        out.append(c)
        i += 1
      } else {
        // backslash
        if (i + 1 >= n) {
          // trailing '\' => drop it
          i += 1
        } else {
          val next = in.charAt(i + 1)

          // CSS line continuation: "\" + newline/carriage-return/formfeed => removed
          if (next == '\n' || next == '\f') {
            i += 2
          } else if (next == '\r') {
            // consume optional '\n' after '\r'
            i += 2
            if (i < n && in.charAt(i) == '\n') i += 1
          } else if (isHex(next)) {
            // read 1..6 hex digits
            var j = i + 1
            var count = 0
            var value = 0

            while (j < n && count < 6 && isHex(in.charAt(j))) {
              val h = in.charAt(j)
              val digit =
                if (h >= '0' && h <= '9') h - '0'
                else if (h >= 'a' && h <= 'f') 10 + (h - 'a')
                else 10 + (h - 'A')
              value = (value << 4) | digit
              j += 1
              count += 1
            }

            // mask to 2 bytes (16 bits)
            val decoded = (value & 0xFFFF).toChar
            out.append(decoded)

            // optional single whitespace after hex escape
            if (j < n && isCssWhitespace(in.charAt(j))) j += 1

            i = j
          } else {
            // evasion: drop '\' and keep the next char as-is
            out.append(next)
            i += 2
          }
        }
      }
    }

    out.toString
  }

  // replaceComments: replace each C-style comment (/* ... */) with a single space.
  // Unterminated /* ... at end is also replaced with a single space.
  // Standalone */ is not modified.
  def replaceComments(input: String): String = {
    if (input == null || input.isEmpty) return input

    val out = new java.lang.StringBuilder(input.length)
    var i = 0
    val n = input.length

    while (i < n) {
      // Detect start of comment "/*"
      if (input.charAt(i) == '/' && i + 1 < n && input.charAt(i + 1) == '*') {
        // We found a comment start. Skip until closing "*/" or end-of-input.
        i += 2
        var closed = false
        while (i < n && !closed) {
          if (input.charAt(i) == '*' && i + 1 < n && input.charAt(i + 1) == '/') {
            i += 2
            closed = true
          } else {
            i += 1
          }
        }
        // Replace the whole comment (closed or not) with a single space.
        out.append(' ')
      } else {
        // Any other char, including standalone "*/", is copied as-is.
        out.append(input.charAt(i))
        i += 1
      }
    }

    out.toString
  }
}

object ByteRangeValidator {

  type ByteRange = (Int, Int)

  def validateByteRange(
                         input: String,
                         rangesStr: String
                       ): Boolean = {

    val ranges = parseByteRanges(rangesStr)
    val allowed: Int => Boolean = b =>
      ranges.exists { case (min, max) => b >= min && b <= max }

    input
      .getBytes(StandardCharsets.UTF_8)
      .exists { byte =>
        val unsigned = byte & 0xff
        !allowed(unsigned)
      }
  }

  def parseByteRanges(input: String): Seq[ByteRange] = {
    input
      .split(",")
      .iterator
      .map(_.trim)
      .filter(_.nonEmpty)
      .map {
        case range if range.contains("-") =>
          val Array(start, end) = range.split("-", 2).map(_.trim.toInt)
          require(start >= 0 && end <= 255 && start <= end,
            s"Invalid byte range: $range")
          start -> end

        case single =>
          val value = single.toInt
          require(value >= 0 && value <= 255,
            s"Invalid byte value: $value")
          value -> value
      }
      .toSeq
  }
}

object IpMatch {

  /** Compile le paramètre "@ipMatch ..." une seule fois (perf) */
  final case class Compiled(entries: Vector[Either[IpAddress, Cidr[IpAddress]]]) {
    def matches(remoteAddr: String): Boolean = {
      IpAddress.fromString(remoteAddr.trim) match {
        case None => false
        case Some(remote) =>
          var i = 0
          while (i < entries.length) {
            entries(i) match {
              case Left(ip) =>
                if (ip == remote) return true
              case Right(cidr) =>
                // IpCidr est typé v4/v6 : contains(remote) ne matche que si même famille
                if (cidr.contains(remote)) return true
            }
            i += 1
          }
          false
      }
    }
  }

  /** Parse "192.168.1.100, 10.10.50.0/24, 2001:db8::/32" */
  def compile(param: String): Compiled = {
    val entries =
      param
        .split(',')
        .iterator
        .map(_.trim)
        .filter(_.nonEmpty)
        .flatMap { token =>
          // CIDR d'abord, sinon IP pleine
          Cidr.fromString(token).map(Right(_))
            .orElse(IpAddress.fromString(token).map(Left(_)))
            .iterator
        }
        .toVector

    Compiled(entries)
  }

  /** Helper directe : parse à chaque appel (moins perf) */
  def ipMatch(remoteAddr: String, param: String): Boolean =
    compile(param).matches(remoteAddr)

  /** Variante “1 argument” si tu veux capturer le paramètre et obtenir une fonction (String) => Boolean */
  def ipMatch(param: String): String => Boolean = {
    val compiled = compile(param)
    (remoteAddr: String) => compiled.matches(remoteAddr)
  }
}

object EncodingHelper {

  def validateUrlEncoding(input: String): Boolean = {
    //try {
    //  URLDecoder.decode(input, StandardCharsets.UTF_8)
    //  true
    //} catch {
    //  case _: Throwable => false
    //}
    val len = input.length
    var i = 0

    while (i < len) {
      if (input.charAt(i) == '%') {
        // must have two characters after %
        if (i + 2 >= len) return false

        val c1 = input.charAt(i + 1)
        val c2 = input.charAt(i + 2)

        def isHex(c: Char): Boolean =
          (c >= '0' && c <= '9') ||
            (c >= 'a' && c <= 'f') ||
            (c >= 'A' && c <= 'F')

        if (!isHex(c1) || !isHex(c2)) return false

        i += 3 // skip the %HH sequence
      } else {
        i += 1
      }
    }

    true
  }

  def validateUtf8Encoding(input: String): Boolean = {
    val bytes = input
    val len = bytes.length

    @inline def u(i: Int): Int = bytes(i) & 0xff
    @inline def isCont(b: Int): Boolean = (b & 0xC0) == 0x80 // 10xxxxxx

    var i = 0
    while (i < len) {
      val b0 = u(i)

      if (b0 <= 0x7F) {
        // ASCII
        i += 1

      } else if (b0 >= 0xC2 && b0 <= 0xDF) {
        // 2 bytes
        if (i + 1 >= len) return false
        if (!isCont(u(i + 1))) return false
        i += 2

      } else if (b0 == 0xE0) {
        // 3 bytes (anti-overlong)
        if (i + 2 >= len) return false
        val b1 = u(i + 1)
        val b2 = u(i + 2)
        if (b1 < 0xA0 || b1 > 0xBF) return false
        if (!isCont(b2)) return false
        i += 3

      } else if ((b0 >= 0xE1 && b0 <= 0xEC) || b0 == 0xEE || b0 == 0xEF) {
        // 3 bytes standard
        if (i + 2 >= len) return false
        if (!isCont(u(i + 1)) || !isCont(u(i + 2))) return false
        i += 3

      } else if (b0 == 0xED) {
        // 3 bytes (exclusion surrogates UTF-16)
        if (i + 2 >= len) return false
        val b1 = u(i + 1)
        val b2 = u(i + 2)
        if (b1 < 0x80 || b1 > 0x9F) return false
        if (!isCont(b2)) return false
        i += 3

      } else if (b0 == 0xF0) {
        // 4 bytes (anti-overlong)
        if (i + 3 >= len) return false
        val b1 = u(i + 1)
        if (b1 < 0x90 || b1 > 0xBF) return false
        if (!isCont(u(i + 2)) || !isCont(u(i + 3))) return false
        i += 4

      } else if (b0 >= 0xF1 && b0 <= 0xF3) {
        // 4 bytes standard
        if (i + 3 >= len) return false
        if (!isCont(u(i + 1)) || !isCont(u(i + 2)) || !isCont(u(i + 3))) return false
        i += 4

      } else if (b0 == 0xF4) {
        // 4 bytes (<= U+10FFFF)
        if (i + 3 >= len) return false
        val b1 = u(i + 1)
        if (b1 < 0x80 || b1 > 0x8F) return false
        if (!isCont(u(i + 2)) || !isCont(u(i + 3))) return false
        i += 4

      } else {
        // 0x80..0xBF continuation seule
        // 0xC0..0xC1 overlong
        // 0xF5..0xFF hors Unicode
        return false
      }
    }

    true
  }
}

object EscapeSeq {

  /** Decodes ANSI C escape sequences:
   * \a, \b, \f, \n, \r, \t, \v, \\, \?, \', \", \xHH (1..2 hex digits), \0OOO (octal, 1..3 digits)
   * Invalid encodings are left in the output.
   */
  def escapeSeqDecode(input: String): String = {
    if (input == null || input.isEmpty) return input

    val sb = new StringBuilder(input.length)
    val n  = input.length

    @inline def isHex(c: Char): Boolean =
      (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')

    @inline def hexVal(c: Char): Int =
      if (c >= '0' && c <= '9') c - '0'
      else if (c >= 'a' && c <= 'f') 10 + (c - 'a')
      else 10 + (c - 'A')

    @inline def isOct(c: Char): Boolean = c >= '0' && c <= '7'
    @inline def octVal(c: Char): Int = c - '0'

    var i = 0
    while (i < n) {
      val ch = input.charAt(i)
      if (ch != '\\') {
        sb.append(ch)
        i += 1
      } else {
        // '\' at end => invalid, keep as-is
        if (i + 1 >= n) {
          sb.append('\\')
          i += 1
        } else {
          val next = input.charAt(i + 1)
          next match {
            case 'a'  => sb.append('\u0007'); i += 2 // BEL
            case 'b'  => sb.append('\b');     i += 2
            case 'f'  => sb.append('\f');     i += 2
            case 'n'  => sb.append('\n');     i += 2
            case 'r'  => sb.append('\r');     i += 2
            case 't'  => sb.append('\t');     i += 2
            case 'v'  => sb.append('\u000B'); i += 2 // VT
            case '\\' => sb.append('\\');     i += 2
            case '?'  => sb.append('?');      i += 2
            case '\'' => sb.append('\'');     i += 2
            case '"'  => sb.append('"');      i += 2

            case 'x' =>
              // \xHH where H is hex; accept 1..2 hex digits (common C behavior); otherwise invalid
              var j = i + 2
              var value = 0
              var count = 0
              while (j < n && count < 2 && isHex(input.charAt(j))) {
                value = (value << 4) | hexVal(input.charAt(j))
                j += 1
                count += 1
              }
              if (count == 0) {
                // invalid: keep "\x" literally
                sb.append('\\').append('x')
                i += 2
              } else {
                sb.append((value & 0xFF).toChar)
                i = j
              }

            case '0' =>
              // \0OOO octal: after \0, take 0..3 octal digits (total 1..4 including the first 0)
              var j = i + 2
              var value = 0
              var count = 0
              while (j < n && count < 3 && isOct(input.charAt(j))) {
                value = (value << 3) | octVal(input.charAt(j))
                j += 1
                count += 1
              }
              // Even if there are no extra digits, "\0" is valid => NUL
              sb.append((value & 0xFF).toChar)
              i = j

            case _ =>
              // Not a recognized escape => keep '\' and the next char as-is
              sb.append('\\').append(next)
              i += 2
          }
        }
      }
    }

    sb.result()
  }
}

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

object MsUrlDecode {

  /**
   * Like URL decode, but supports Microsoft-specific %uHHHH encoding.
   *
   * Rules for %uHHHH:
   * - If code in 0xFF01..0xFF5E (full-width ASCII), adjust low byte using high byte:
   *     resultByte = (low + 0x20)
   * - Otherwise: keep only low byte and zero the high byte:
   *     resultByte = low
   *
   * Invalid escapes are left verbatim in output.
   */
  def urlDecodeMs(
                   input: String,
                   charset: Charset = StandardCharsets.UTF_8,
                   plusAsSpace: Boolean = true
                 ): String = {
    val out = new StringBuilder(input.length)
    val bytes = new ByteArrayOutputStream(32)

    def flushBytes(): Unit = {
      if (bytes.size() > 0) {
        out.append(new String(bytes.toByteArray, charset))
        bytes.reset()
      }
    }

    def hexVal(c: Char): Int = {
      if (c >= '0' && c <= '9') c - '0'
      else if (c >= 'a' && c <= 'f') c - 'a' + 10
      else if (c >= 'A' && c <= 'F') c - 'A' + 10
      else -1
    }

    def parseHex2(i: Int): Int = {
      val h1 = hexVal(input.charAt(i))
      val h2 = hexVal(input.charAt(i + 1))
      if (h1 < 0 || h2 < 0) -1 else (h1 << 4) | h2
    }

    def parseHex4(i: Int): Int = {
      val a = hexVal(input.charAt(i))
      val b = hexVal(input.charAt(i + 1))
      val c = hexVal(input.charAt(i + 2))
      val d = hexVal(input.charAt(i + 3))
      if (a < 0 || b < 0 || c < 0 || d < 0) -1
      else (a << 12) | (b << 8) | (c << 4) | d
    }

    var i = 0
    while (i < input.length) {
      val ch = input.charAt(i)

      if (plusAsSpace && ch == '+') {
        flushBytes()
        out.append(' ')
        i += 1
      } else if (ch == '%' && i + 2 < input.length) {
        // Microsoft %uHHHH ?
        if ((i + 5) < input.length && (input.charAt(i + 1) == 'u' || input.charAt(i + 1) == 'U')) {
          val code = parseHex4(i + 2)
          if (code >= 0) {
            val high = (code >>> 8) & 0xff
            val low  = code & 0xff

            val decodedByte =
              if (code >= 0xFF01 && code <= 0xFF5E && high == 0xFF) {
                // full-width ASCII -> normalize
                ((low + 0x20) & 0xff)
              } else {
                // keep only low byte, zero high byte
                low & 0xff
              }

            bytes.write(decodedByte)
            i += 6 // "%u" + 4 hex
          } else {
            // invalid %u escape -> keep verbatim
            flushBytes()
            out.append(ch)
            i += 1
          }
        } else {
          // Standard %HH
          val b = parseHex2(i + 1)
          if (b >= 0) {
            bytes.write(b)
            i += 3
          } else {
            // invalid %HH -> keep verbatim
            flushBytes()
            out.append(ch)
            i += 1
          }
        }
      } else {
        flushBytes()
        out.append(ch)
        i += 1
      }
    }

    flushBytes()
    out.toString()
  }
}

