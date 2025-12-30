package com.cloud.apim.seclang.model

sealed trait AstNode

final case class Configuration(
    statements: List[Statement]
) extends AstNode

sealed trait Statement extends AstNode

final case class SecRule(
    commentBlock: Option[CommentBlock],
    variables: Variables,
    operator: Operator,
    actions: Option[Actions]
) extends Statement {
  lazy val id: Option[Int] = actions.flatMap(_.actions.collectFirst {
    case Action.Id(v) => v
  })
  
  lazy val phase: Int = actions.flatMap(_.actions.collectFirst {
    case Action.Phase(p) => p
  }).getOrElse(2)
  
  lazy val isChain: Boolean = actions.exists(_.actions.exists(_ == Action.Chain))
}

final case class SecRuleScript(
    commentBlock: Option[CommentBlock],
    filePath: String,
    actions: Option[Actions]
) extends Statement

final case class SecAction(
    commentBlock: Option[CommentBlock],
    actions: Actions
) extends Statement

final case class SecRuleRemoveById(
    commentBlock: Option[CommentBlock],
    ids: List[Int]
) extends Statement

final case class SecRuleRemoveByMsg(
    commentBlock: Option[CommentBlock],
    value: String
) extends Statement

final case class SecRuleRemoveByTag(
    commentBlock: Option[CommentBlock],
    value: String
) extends Statement

final case class SecRuleUpdateTargetById(
    commentBlock: Option[CommentBlock],
    id: Int,
    variables: UpdateVariables
) extends Statement

final case class SecRuleUpdateTargetByMsg(
    commentBlock: Option[CommentBlock],
    value: String,
    variables: UpdateVariables
) extends Statement

final case class SecRuleUpdateTargetByTag(
    commentBlock: Option[CommentBlock],
    value: String,
    variables: UpdateVariables
) extends Statement

final case class SecRuleUpdateActionById(
    commentBlock: Option[CommentBlock],
    id: Int,
    actions: Actions
) extends Statement

final case class SecMarker(
    commentBlock: Option[CommentBlock],
    name: String
) extends Statement

final case class EngineConfigDirective(
    commentBlock: Option[CommentBlock],
    directive: ConfigDirective
) extends Statement

final case class CommentBlock(
    comments: List[Comment]
) extends AstNode

final case class Comment(
    text: String
) extends AstNode

sealed trait ConfigDirective extends AstNode

object ConfigDirective {
  final case class AuditLog(value: String) extends ConfigDirective
  final case class AuditEngine(value: String) extends ConfigDirective
  final case class AuditLogParts(parts: List[String]) extends ConfigDirective
  final case class ComponentSignature(value: String) extends ConfigDirective
  final case class ServerSignature(value: String) extends ConfigDirective
  final case class WebAppId(value: String) extends ConfigDirective
  final case class CacheTransformations(options: List[ConfigOption]) extends ConfigDirective
  final case class ChrootDir(value: String) extends ConfigDirective
  final case class ConnEngine(value: String) extends ConfigDirective
  final case class ArgumentSeparator(value: String) extends ConfigDirective
  final case class DebugLog(value: String) extends ConfigDirective
  final case class DebugLogLevel(value: Int) extends ConfigDirective
  final case class GeoLookupDb(value: String) extends ConfigDirective
  final case class RuleEngine(value: String) extends ConfigDirective
  final case class RequestBodyAccess(value: String) extends ConfigDirective
  final case class RequestBodyLimit(value: Int) extends ConfigDirective
  final case class ResponseBodyAccess(value: String) extends ConfigDirective
  final case class ResponseBodyLimit(value: Int) extends ConfigDirective
  final case class DefaultAction(actions: Actions) extends ConfigDirective
  final case class CollectionTimeout(value: Int) extends ConfigDirective
  final case class Include(path: String) extends ConfigDirective
  final case class DataDir(value: String) extends ConfigDirective
  final case class TmpDir(value: String) extends ConfigDirective
  final case class Raw(name: String, value: String) extends ConfigDirective
}

final case class ConfigOption(
    name: String,
    value: Option[String]
) extends AstNode

final case class Variables(
    negated: Boolean,
    count: Boolean,
    variables: List[Variable]
) extends AstNode

final case class UpdateVariables(
    negated: Boolean,
    count: Boolean,
    variables: List[Variable]
) extends AstNode

sealed trait Variable extends AstNode

object Variable {
  final case class Simple(name: String) extends Variable
  final case class Collection(collection: String, key: Option[String]) extends Variable {
    override def toString: String = key match {
      case None => collection
      case Some(k) => s"$collection:$k"
    }
  }
}

sealed trait Operator extends AstNode

object Operator {
  final case class Negated(operator: Operator) extends Operator
  
  final case class BeginsWith(value: String) extends Operator
  final case class Contains(value: String) extends Operator
  final case class ContainsWord(value: String) extends Operator
  final case class DetectSQLi(value: String) extends Operator
  final case class DetectXSS(value: String) extends Operator
  final case class EndsWith(value: String) extends Operator
  final case class Eq(value: String) extends Operator
  final case class FuzzyHash(value: String) extends Operator
  final case class Ge(value: String) extends Operator
  final case class GeoLookup(value: String) extends Operator
  final case class GsbLookup(value: String) extends Operator
  final case class Gt(value: String) extends Operator
  final case class InspectFile(value: String) extends Operator
  final case class IpMatch(value: String) extends Operator
  final case class IpMatchFromFile(value: String) extends Operator
  final case class Le(value: String) extends Operator
  final case class Lt(value: String) extends Operator
  final case class Pm(value: String) extends Operator
  final case class PmFromFile(value: String) extends Operator
  final case class Rbl(value: String) extends Operator
  final case class Rsub(value: String) extends Operator
  final case class Rx(pattern: String) extends Operator
  final case class RxGlobal(pattern: String) extends Operator
  final case class Streq(value: String) extends Operator
  final case class StrMatch(value: String) extends Operator
  final case class UnconditionalMatch() extends Operator
  final case class ValidateByteRange(value: String) extends Operator
  final case class ValidateDTD(value: String) extends Operator
  final case class ValidateHash(value: String) extends Operator
  final case class ValidateSchema(value: String) extends Operator
  final case class ValidateUrlEncoding(value: String) extends Operator
  final case class ValidateUtf8Encoding(value: String) extends Operator
  final case class VerifyCC(value: String) extends Operator
  final case class VerifyCPF(value: String) extends Operator
  final case class VerifySSN(value: String) extends Operator
  final case class VerifySVNR(value: String) extends Operator
  final case class Within(value: String) extends Operator
  final case class Raw(name: String, value: String) extends Operator
}

final case class Actions(
    actions: List[Action]
) extends AstNode

sealed trait Action extends AstNode

object Action {
  final case class Accuracy(value: Int) extends Action
  final case class Allow(value: String) extends Action
  final case class Append(value: String) extends Action
  final case class AuditLog() extends Action
  final case class Block() extends Action
  final case class Capture() extends Action
  case object Chain extends Action
  
  sealed trait CtlAction extends Action
  object CtlAction {
    final case class AuditEngine(value: String) extends CtlAction
    final case class AuditLogParts(parts: String) extends CtlAction
    final case class RequestBodyProcessor(value: String) extends CtlAction
    final case class ForceRequestBodyVariable(value: String) extends CtlAction
    final case class RequestBodyAccess(value: String) extends CtlAction
    final case class RuleEngine(value: String) extends CtlAction
    final case class RuleRemoveByTag(tag: String) extends CtlAction
    final case class RuleRemoveById(id: Int) extends CtlAction
    final case class RuleRemoveTargetById(id: Int, target: String) extends CtlAction
    final case class RuleRemoveTargetByTag(tag: String, target: String) extends CtlAction
  }
  
  case object Deny extends Action
  final case class DeprecateVar(expr: String) extends Action
  case object Drop extends Action
  final case class Exec(value: String) extends Action
  final case class ExpireVar(expr: String) extends Action
  final case class Id(value: Int) extends Action
  final case class InitCol(value: String) extends Action
  final case class LogData(value: String) extends Action
  case object Log extends Action
  final case class Maturity(value: Int) extends Action
  final case class Msg(value: String) extends Action
  case object MultiMatch extends Action
  case object NoAuditLog extends Action
  case object NoLog extends Action
  case object Pass extends Action
  final case class Pause(value: Int) extends Action
  final case class Phase(value: Int) extends Action
  final case class Prepend(value: String) extends Action
  final case class Proxy(value: String) extends Action
  final case class Redirect(value: String) extends Action
  final case class Rev(value: String) extends Action
  final case class SanitiseArg(value: String) extends Action
  final case class SanitiseMatchedBytes(value: String) extends Action
  final case class SanitiseMatched() extends Action
  final case class SanitiseRequestHeader(value: String) extends Action
  final case class SanitiseResponseHeader(value: String) extends Action
  final case class SetEnv(value: String) extends Action
  final case class SetRsc(value: String) extends Action
  final case class SetSid(value: String) extends Action
  final case class SetUid(value: String) extends Action
  final case class SetVar(expr: String) extends Action
  final case class Severity(value: SeverityValue) extends Action
  final case class SkipAfter(marker: String) extends Action
  final case class Skip(count: Int) extends Action
  final case class Status(code: Int) extends Action
  final case class Tag(value: String) extends Action
  final case class Ver(value: String) extends Action
  final case class Xmlns(value: String) extends Action
  final case class Transform(name: String) extends Action
  
  final case class Raw(name: String, value: Option[String]) extends Action
}

sealed trait SeverityValue extends AstNode
object SeverityValue {

  case object Emergency extends SeverityValue
  case object Alert extends SeverityValue
  case object Critical extends SeverityValue
  case object Error extends SeverityValue
  case object Warning extends SeverityValue
  case object Notice extends SeverityValue
  case object Info extends SeverityValue
  case object Debug extends SeverityValue
  final case class Numeric(value: Int) extends SeverityValue

  def apply(value: String): SeverityValue = {
    import com.cloud.apim.seclang.impl.parser.Implicits._
    value.toLowerCase match {
      case "emergency" | "0" => SeverityValue.Emergency
      case "alert" | "1" => SeverityValue.Alert
      case "critical" | "2" => SeverityValue.Critical
      case "error" | "3" => SeverityValue.Error
      case "warning" | "4" => SeverityValue.Warning
      case "notice" | "5" => SeverityValue.Notice
      case "info" | "6" => SeverityValue.Info
      case "debug" | "7" => SeverityValue.Debug
      case n => SeverityValue.Numeric(n.toIntOption.getOrElse(0))
    }
  }
}

final case class RequestContext(
    method: String,
    uri: String,
    headers: Map[String, List[String]],
    query: Map[String, List[String]],
    body: Option[String] = None
)

sealed trait Disposition
object Disposition {
  case object Continue extends Disposition
  final case class Block(status: Int, msg: Option[String], ruleId: Option[Int]) extends Disposition
}

final case class MatchEvent(
    ruleId: Option[Int],
    msg: Option[String],
    phase: Int,
    raw: String
)

final case class EngineResult(
    disposition: Disposition,
    events: List[MatchEvent]
)
