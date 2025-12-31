package com.cloud.apim.seclang.impl.utils

import com.comcast.ip4s._
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
    """&#x([0-9a-fA-F]{1,4});?""".r

  private val decEntity: Regex =
    """&#([0-9]{1,5});?""".r

  private val namedEntities: Map[String, Char] = Map(
    "quot" -> '"',
    "nbsp" -> ' ',
    "lt"   -> '<',
    "gt"   -> '>'
  )

  def htmlEntityDecode(input: String): String = {
    var out = input

    // Hexadecimal entities
    out = hexEntity.replaceAllIn(out, m => {
      val value = Integer.parseInt(m.group(1), 16) & 0xff
      value.toChar.toString
    })

    // Decimal entities
    out = decEntity.replaceAllIn(out, m => {
      val value = Integer.parseInt(m.group(1), 10) & 0xff
      value.toChar.toString
    })

    // Named entities (with or without ;)
    namedEntities.foreach { case (name, char) =>
      out = out
        .replace(s"&$name;", char.toString)
        .replace(s"&$name", char.toString)
    }

    out
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

  import java.lang.{Character => JChar}

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

