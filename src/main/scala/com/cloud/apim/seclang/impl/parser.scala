package  com.cloud.apim.seclang.impl.parser

import com.cloud.apim.seclang.impl.model._
import fastparse.NoWhitespace._
import fastparse._

import scala.util.Try

object Implicits {
  implicit class BetterString(val obj: String) extends AnyVal {
    def toIntOption: Option[Int] = {
      Try(obj.toInt).toOption
    }
  }
}

object Utils {
  import Implicits._
  // ---------- helpers ----------
  def stripQuotes(s: String): String = {
    val t = s.trim
    if ((t.startsWith("\"") && t.endsWith("\"")) || (t.startsWith("'") && t.endsWith("'")))
      t.substring(1, t.length - 1)
    else t
  }

  // split "a,b,'c,d',e" -> respects quotes
  def splitActions(s: String): List[String] = {
    val out = scala.collection.mutable.ListBuffer.empty[String]
    val cur = new StringBuilder
    var inSingle = false
    var inDouble = false
    var i = 0
    while (i < s.length) {
      val ch = s.charAt(i)
      ch match {
        case '\'' if !inDouble =>
          inSingle = !inSingle; cur.append(ch)
        case '"' if !inSingle =>
          inDouble = !inDouble; cur.append(ch)
        case ',' if !inSingle && !inDouble =>
          out += cur.toString.trim
          cur.clear()
        case _ =>
          cur.append(ch)
      }
      i += 1
    }
    val last = cur.toString.trim
    if (last.nonEmpty) out += last
    out.toList.filter(_.nonEmpty)
  }

  def parseActionToken(tok: String): Action = {
    val t = tok.trim
    if (t.equalsIgnoreCase("deny")) Action.Deny
    else if (t.equalsIgnoreCase("drop")) Action.Drop
    else if (t.equalsIgnoreCase("pass")) Action.Pass
    else if (t.equalsIgnoreCase("log")) Action.Log
    else if (t.equalsIgnoreCase("nolog")) Action.NoLog
    else if (t.equalsIgnoreCase("chain")) Action.Chain
    else {
      val idx = t.indexOf(':')
      if (idx < 0) {
        // ctl:xxx=yyy without ':', or just unknown bareword
        if (t.startsWith("ctl:") && t.contains("=")) {
          val ctl = t.drop(4)
          val parts = ctl.split("=", 2)
          if (parts.length == 2 && parts(0) == "ruleRemoveById") {
            parts(1).trim.toIntOption.map(Action.CtlRuleRemoveById).getOrElse(Action.Raw("ctl", Some(t)))
          } else Action.Raw("ctl", Some(t))
        } else Action.Raw(t, None)
      } else {
        val name = t.substring(0, idx).trim
        val value = t.substring(idx + 1).trim
        name match {
          case "id"       => value.toIntOption.map(Action.Id).getOrElse(Action.Raw(name, Some(value)))
          case "phase"    => value.toIntOption.map(Action.Phase).getOrElse(Action.Raw(name, Some(value)))
          case "msg"      => Action.Msg(stripQuotes(value))
          case "status"   => value.toIntOption.map(Action.Status).getOrElse(Action.Raw(name, Some(value)))
          case "severity" => value.toIntOption.map(Action.Severity).getOrElse(Action.Raw(name, Some(value)))
          case "t"        => Action.Transform(stripQuotes(value))
          case "skipAfter" => Action.SkipAfter(stripQuotes(value))
          case "setvar"   => Action.SetVar(stripQuotes(value))
          case "ctl" =>
            // ctl:ruleRemoveById=123 (si écrit ctl:... dans value)
            if (value.startsWith("ruleRemoveById=")) {
              value.stripPrefix("ruleRemoveById=").trim.toIntOption
                .map(Action.CtlRuleRemoveById).getOrElse(Action.Raw(name, Some(value)))
            } else Action.Raw(name, Some(value))
          case other => Action.Raw(other, Some(stripQuotes(value)))
        }
      }
    }
  }

  def parseActions(actionsStr: String): List[Action] =
    splitActions(stripQuotes(actionsStr)).map(parseActionToken)
}

object FastParseParser {

  import Implicits._

  // ---------- fastparse grammar ----------
  private def wsp[_: P] = P( CharsWhileIn(" \t").rep )
  private def nl[_: P]  = P( ("\r\n" | "\n").rep(1) )

  private def comment[_: P]: P[Directive] =
    P( wsp ~ "#" ~ CharsWhile(_ != '\n', 0).! ).map(s => Comment("#" + s)).opaque("comment")

  private def bareword[_: P]: P[String] =
    P( CharsWhile(c => !c.isWhitespace && c != '"', 1).! )

  private def quoted[_: P]: P[String] =
    P(
      ("\"" ~/ CharsWhile(_ != '"', 0).! ~ "\"") |
      ("'" ~/ CharsWhile(_ != '\'', 0).! ~ "'")
    )

  private def variables[_: P]: P[List[VariableSelector]] =
    P( (bareword.rep(1, sep = wsp ~ "|" ~ wsp)).! ).map { s =>
      s.split("\\|").toList.map(_.trim).filter(_.nonEmpty).map { v =>
        val parts = v.split(":", 2)
        if (parts.length == 2) VariableSelector(parts(0), Some(parts(1)))
        else VariableSelector(parts(0), None)
      }
    }

  private def operator[_: P]: P[Operator] =
    P(
      ("@rx" ~ wsp ~ (quoted | bareword)).map(p => Operator.Rx(Utils.stripQuotes(p))) |
      ("@contains" ~ wsp ~ (quoted | bareword)).map(v => Operator.Contains(Utils.stripQuotes(v))) |
      ("@streq" ~ wsp ~ (quoted | bareword)).map(v => Operator.Streq(Utils.stripQuotes(v))) |
      ("@pm" ~ wsp ~ (quoted | bareword)).map(v => Operator.Pm(Utils.stripQuotes(v).split("\\s+").toList.filter(_.nonEmpty))) |
      ("@unconditionalMatch").!.map(_ => Operator.UnconditionalTrue()) |
      ("@" ~ bareword ~ wsp ~ (quoted | bareword)).map { case (op, arg) => Operator.Raw("@"+op, Utils.stripQuotes(arg)) }
    )

  private def secrule[_: P]: P[Directive] =
    P( wsp ~ "SecRule" ~ wsp ~ variables ~ wsp ~ operator ~ wsp ~ quoted.! ).map {
      case (vars, op, actStr) =>
        Rule(vars, op, Utils.parseActions(actStr), raw = s"SecRule ${vars.mkString("|")} $op $actStr")
    }

  private def secaction[_: P]: P[Directive] =
    P( wsp ~ "SecAction" ~ wsp ~ quoted.! ).map { actStr =>
      SecAction(Utils.parseActions(actStr), raw = s"SecAction $actStr")
    }

  private def secruleremovebyid[_: P]: P[Directive] =
    P( wsp ~ "SecRuleRemoveById" ~ wsp ~ CharsWhile(_ != '\n', 1).! ).map { rest =>
      val ids = rest.split("\\s+").toList.flatMap(_.trim.toIntOption)
      RuleRemoveById(ids, raw = s"SecRuleRemoveById $rest")
    }

  private def secmarker[_: P]: P[Directive] =
    P( wsp ~ "SecMarker" ~ wsp ~ bareword.! ).map { name =>
      Marker(name, raw = s"SecMarker $name")
    }

  private def unknownLine[_: P]: P[Directive] =
    P( wsp ~ CharsWhile(_ != '\n', 0).! ).map { s =>
      val t = s.trim
      if (t.isEmpty) Comment("")
      else Unknown(t)
    }

  private def line[_: P]: P[Directive] =
    P( comment | secruleremovebyid | secmarker | secaction | secrule | unknownLine )

  def parse(input: String): Either[String, List[Directive]] = {
    def p[_: P] = P((line ~ (nl | End)).rep.map(_.toList) ~ End )
    fastparse.parse(input, p(_)) match {
      case Parsed.Success(value, _) => Right(value.filterNot(_.isInstanceOf[Comment]).filter(_.raw.trim.nonEmpty))
      case f: Parsed.Failure        => Left(f.trace().longMsg)
    }
  }
}

object Parser2 extends scala.util.parsing.combinator.RegexParsers {

  import Implicits._

  override def skipWhitespace = false

  private def wsp: Parser[String] = """[ \t]*""".r

  private def nl: Parser[String] = """\r?\n""".r

  private def comment: Parser[Directive] =
    wsp ~> "#" ~> """[^\n]*""".r <~ opt(nl) ^^ { s => Comment("#" + s) }

  private def bareword: Parser[String] =
    """[^\s"']+""".r

  private def quotedDouble: Parser[String] =
    "\"" ~> """[^"]*""".r <~ "\""

  private def quotedSingle: Parser[String] =
    "'" ~> """[^']*""".r <~ "'"

  private def quoted: Parser[String] =
    quotedDouble | quotedSingle

  private def variableSelector: Parser[VariableSelector] =
    """[^|\s:]+""".r ~ opt(":" ~> """[^|\s]+""".r) ^^ {
      case collection ~ keyOpt => VariableSelector(collection, keyOpt)
    }

  private def variables: Parser[List[VariableSelector]] =
    rep1sep(variableSelector, wsp ~ "|" ~ wsp)

  private def operator: Parser[Operator] = {
    def rxOp: Parser[Operator] =
      "@rx" ~> wsp ~> (quoted | bareword) ^^ { p => Operator.Rx(Utils.stripQuotes(p)) }

    def containsOp: Parser[Operator] =
      "@contains" ~> wsp ~> (quoted | bareword) ^^ { v => Operator.Contains(Utils.stripQuotes(v)) }

    def streqOp: Parser[Operator] =
      "@streq" ~> wsp ~> (quoted | bareword) ^^ { v => Operator.Streq(Utils.stripQuotes(v)) }

    def pmOp: Parser[Operator] =
      "@pm" ~> wsp ~> (quoted | bareword) ^^ { v =>
        Operator.Pm(Utils.stripQuotes(v).split("\\s+").toList.filter(_.nonEmpty))
      }

    def unconditionalOp: Parser[Operator] =
      "@unconditionalMatch" ^^ { _ => Operator.UnconditionalTrue() }

    def rawOp: Parser[Operator] =
      "@" ~> bareword ~ wsp ~ (quoted | bareword) ^^ {
        case op ~ _ ~ arg => Operator.Raw("@" + op, Utils.stripQuotes(arg))
      }

    rxOp | containsOp | streqOp | pmOp | unconditionalOp | rawOp
  }

  private def secRule: Parser[Directive] =
    wsp ~> "SecRule" ~> wsp ~> variables ~ wsp ~ operator ~ wsp ~ quoted <~ opt(nl) ^^ {
      case vars ~ _ ~ op ~ _ ~ actStr =>
        Rule(vars, op, Utils.parseActions(actStr), raw = s"SecRule ${vars.mkString("|")} $op $actStr")
    }

  private def secAction: Parser[Directive] =
    wsp ~> "SecAction" ~> wsp ~> quoted <~ opt(nl) ^^ { actStr =>
      SecAction(Utils.parseActions(actStr), raw = s"SecAction $actStr")
    }

  private def secRuleRemoveById: Parser[Directive] =
    wsp ~> "SecRuleRemoveById" ~> wsp ~> """[^\n]+""".r <~ opt(nl) ^^ { rest =>
      val ids = rest.split("\\s+").toList.flatMap(_.trim.toIntOption)
      RuleRemoveById(ids, raw = s"SecRuleRemoveById $rest")
    }

  private def secMarker: Parser[Directive] =
    wsp ~> "SecMarker" ~> wsp ~> bareword <~ opt(nl) ^^ { name =>
      Marker(name, raw = s"SecMarker $name")
    }

  private def emptyLine: Parser[Directive] =
    wsp <~ nl ^^ { _ => Comment("") }

  private def unknownLine: Parser[Directive] =
    wsp ~> """[^\n]+""".r <~ opt(nl) ^^ { s =>
      val t = s.trim
      if (t.isEmpty) Comment("")
      else Unknown(t)
    }

  private def line: Parser[Directive] =
    comment | secRuleRemoveById | secMarker | secAction | secRule | emptyLine | unknownLine

  private def document: Parser[List[Directive]] =
    rep(line) <~ opt(wsp)

  def parse(input: String): Either[String, List[Directive]] = {
    parseAll(document, input) match {
      case Success(directives, _) =>
        Right(directives.filterNot(_.isInstanceOf[Comment]).filter(_.raw.trim.nonEmpty))
      case NoSuccess(msg, next) =>
        Left(s"Parse error at line ${next.pos.line}, column ${next.pos.column}: $msg")
    }
  }

  private def stripQuotes(s: String): String = Utils.stripQuotes(s)
  private def splitActions(s: String): List[String] = Utils.splitActions(s)
  private def parseActionToken(tok: String): Action = Utils.parseActionToken(tok)
  private def parseActions(actionsStr: String): List[Action] = Utils.parseActions(actionsStr)
}