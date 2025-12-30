package com.cloud.apim.seclang.model

import play.api.libs.json._

sealed trait AstNode {
  def json: JsValue
}

final case class Configuration(
    statements: List[Statement]
) extends AstNode {
  def json: JsValue = Json.obj(
    "type" -> "Configuration",
    "statements" -> JsArray(statements.map(_.json))
  )
}

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

  def json: JsValue = Json.obj(
    "type" -> "SecRule",
    "id" -> id.map(v => JsNumber(BigDecimal.valueOf(v))).getOrElse(JsNull).as[JsValue],
    "phase" -> phase,
    "chain" -> isChain,
    "comment_block" -> commentBlock.map(_.json).getOrElse(JsNull).as[JsValue],
    "variables" -> variables.json,
    "operator" -> operator.json,
    "actions" -> actions.map(_.json).getOrElse(JsNull).as[JsValue],
  )
}

final case class SecRuleScript(
    commentBlock: Option[CommentBlock],
    filePath: String,
    actions: Option[Actions]
) extends Statement {
  def json: JsValue = Json.obj(
    "type" -> "SecRuleScript",
    "file_path" -> filePath,
    "actions" -> actions.map(_.json).getOrElse(JsNull).as[JsValue],
    "comment_block" -> commentBlock.map(_.json).getOrElse(JsNull).as[JsValue],
  )
}

final case class SecAction(
    commentBlock: Option[CommentBlock],
    actions: Actions
) extends Statement {
  def json: JsValue = Json.obj(
    "type" -> "SecAction",
    "actions" -> actions.json,
    "comment_block" -> commentBlock.map(_.json).getOrElse(JsNull).as[JsValue],
  )
}

final case class SecRuleRemoveById(
    commentBlock: Option[CommentBlock],
    ids: List[Int]
) extends Statement {
  def json: JsValue = Json.obj(
    "type" -> "SecRuleRemoveById",
    "ids" -> JsArray(ids.map(id => JsNumber(BigDecimal.valueOf(id)))),
    "comment_block" -> commentBlock.map(_.json).getOrElse(JsNull).as[JsValue],
  )
}

final case class SecRuleRemoveByMsg(
    commentBlock: Option[CommentBlock],
    value: String
) extends Statement {
  def json: JsValue = Json.obj(
    "type" -> "SecRuleRemoveByMsg",
    "value" -> value,
    "comment_block" -> commentBlock.map(_.json).getOrElse(JsNull).as[JsValue],
  )
}

final case class SecRuleRemoveByTag(
    commentBlock: Option[CommentBlock],
    value: String
) extends Statement {
  def json: JsValue = Json.obj(
    "type" -> "SecRuleRemoveByTag",
    "value" -> value,
    "comment_block" -> commentBlock.map(_.json).getOrElse(JsNull).as[JsValue],
  )
}

final case class SecRuleUpdateTargetById(
    commentBlock: Option[CommentBlock],
    id: Int,
    variables: UpdateVariables
) extends Statement {
  def json: JsValue = Json.obj(
    "id" -> id,
    "variables" -> variables.json,
    "comment_block" -> commentBlock.map(_.json).getOrElse(JsNull).as[JsValue],
  )
}

final case class SecRuleUpdateTargetByMsg(
    commentBlock: Option[CommentBlock],
    value: String,
    variables: UpdateVariables
) extends Statement {
  def json: JsValue = Json.obj(
    "type" -> "SecRuleUpdateTargetByMsg",
    "value" -> value,
    "variables" -> variables.json,
    "comment_block" -> commentBlock.map(_.json).getOrElse(JsNull).as[JsValue],
  )
}

final case class SecRuleUpdateTargetByTag(
    commentBlock: Option[CommentBlock],
    value: String,
    variables: UpdateVariables
) extends Statement {
  def json: JsValue = Json.obj(
    "type" -> "SecRuleUpdateTargetByTag",
    "value" -> value,
    "variables" -> variables.json,
    "comment_block" -> commentBlock.map(_.json).getOrElse(JsNull).as[JsValue],
  )
}

final case class SecRuleUpdateActionById(
    commentBlock: Option[CommentBlock],
    id: Int,
    actions: Actions
) extends Statement {
  def json: JsValue = Json.obj(
    "type" -> "SecRuleUpdateActionById",
    "id" -> id,
    "actions" -> actions.json,
    "comment_block" -> commentBlock.map(_.json).getOrElse(JsNull).as[JsValue],
  )
}

final case class SecMarker(
    commentBlock: Option[CommentBlock],
    name: String
) extends Statement {
  def json: JsValue = Json.obj(
    "type" -> "SecMarker",
    "name" -> name,
    "comment_block" -> commentBlock.map(_.json).getOrElse(JsNull).as[JsValue],
  )
}

final case class EngineConfigDirective(
    commentBlock: Option[CommentBlock],
    directive: ConfigDirective
) extends Statement {
  def json: JsValue = Json.obj(
    "type" -> "EngineConfigDirective",
    "directive" -> directive.json,
    "comment_block" -> commentBlock.map(_.json).getOrElse(JsNull).as[JsValue],
  )
}

final case class CommentBlock(
    comments: List[Comment]
) extends AstNode {
  def json: JsValue = Json.obj(
    "type" -> "CommentBlock",
    "comments" -> JsArray(comments.map(_.json))
  )
}

final case class Comment(
    text: String
) extends AstNode {
  def json: JsValue = Json.obj(
    "type" -> "Comment",
    "text" -> text
  )
}

sealed trait ConfigDirective extends AstNode {
  def json: JsValue
}

object ConfigDirective {
  final case class AuditLog(value: String) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "AuditLog", "value" -> value)
  }
  final case class AuditEngine(value: String) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "AuditEngine", "value" -> value)
  }
  final case class AuditLogParts(parts: List[String]) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "AuditLogParts", "parts" -> parts)
  }
  final case class ComponentSignature(value: String) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "ComponentSignature", "value" -> value)
  }
  final case class ServerSignature(value: String) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "ServerSignature", "value" -> value)
  }
  final case class WebAppId(value: String) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "WebAppId", "value" -> value)
  }
  final case class CacheTransformations(options: List[ConfigOption]) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "CacheTransformations", "options" -> options.map(_.json))
  }
  final case class ChrootDir(value: String) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "ChrootDir", "value" -> value)
  }
  final case class ConnEngine(value: String) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "ConnEngine", "value" -> value)
  }
  final case class ArgumentSeparator(value: String) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "ArgumentSeparator", "value" -> value)
  }
  final case class DebugLog(value: String) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "DebugLog", "value" -> value)
  }
  final case class DebugLogLevel(value: Int) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "DebugLogLevel", "value" -> value)
  }
  final case class GeoLookupDb(value: String) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "GeoLookupDb", "value" -> value)
  }
  final case class RuleEngine(value: String) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "RuleEngine", "value" -> value)
  }
  final case class RequestBodyAccess(value: String) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "RequestBodyAccess", "value" -> value)
  }
  final case class RequestBodyLimit(value: Int) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "RequestBodyLimit", "value" -> value)
  }
  final case class ResponseBodyAccess(value: String) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "ResponseBodyAccess", "value" -> value)
  }
  final case class ResponseBodyLimit(value: Int) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "ResponseBodyLimit", "value" -> value)
  }
  final case class DefaultAction(actions: Actions) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "DefaultAction", "actions" -> actions.json)
  }
  final case class CollectionTimeout(value: Int) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "CollectionTimeout", "value" -> value)
  }
  final case class Include(path: String) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "Include", "path" -> path)
  }
  final case class DataDir(value: String) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "DataDir", "value" -> value)
  }
  final case class TmpDir(value: String) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "TmpDir", "value" -> value)
  }
  final case class Raw(name: String, value: String) extends ConfigDirective {
    def json: JsValue = Json.obj("type" -> "Raw", "name" -> name, "value" -> value)
  }
}

final case class ConfigOption(
    name: String,
    value: Option[String]
) extends AstNode {
  def json: JsValue = Json.obj("type" -> "ConfigOption", "name" -> name, "value" -> value)
}

final case class Variables(
    negated: Boolean,
    count: Boolean,
    variables: List[Variable]
) extends AstNode {
  def json: JsValue = Json.obj(
    "type" -> "Variables",
    "negated" -> negated,
    "count" -> count,
    "variables" -> JsArray(variables.map(_.json))
  )
}

final case class UpdateVariables(
    negated: Boolean,
    count: Boolean,
    variables: List[Variable]
) extends AstNode {
  def json: JsValue = Json.obj(
    "type" -> "UpdateVariables",
    "negated" -> negated,
    "count" -> count,
    "variables" -> JsArray(variables.map(_.json))
  )
}

sealed trait Variable extends AstNode

object Variable {
  final case class Simple(name: String) extends Variable {
    def json: JsValue = Json.obj("type" -> "SimpleVariable", "name" -> name)
  }
  final case class Collection(collection: String, key: Option[String]) extends Variable {
    def json: JsValue = Json.obj("type" -> "CollectionVariable", "collection" -> collection, "key" -> key)
    override def toString: String = key match {
      case None => collection
      case Some(k) => s"$collection:$k"
    }
  }
}

sealed trait Operator extends AstNode

object Operator {
  final case class Negated(operator: Operator) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "negated", "operator" -> operator.json)
  }
  
  final case class BeginsWith(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "begins_with", "value" -> value)
  }
  final case class Contains(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "contains", "value" -> value)
  }
  final case class ContainsWord(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "contains_word", "value" -> value)
  }
  final case class DetectSQLi(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "detect_sqli", "value" -> value)
  }
  final case class DetectXSS(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "detect_xss", "value" -> value)
  }
  final case class EndsWith(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "ends_with", "value" -> value)
  }
  final case class Eq(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "eq", "value" -> value)
  }
  final case class FuzzyHash(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "fuzzy_hash", "value" -> value)
  }
  final case class Ge(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "ge", "value" -> value)
  }
  final case class GeoLookup(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "geo_lookup", "value" -> value)
  }
  final case class GsbLookup(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "gsb_lookup", "value" -> value)
  }
  final case class Gt(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "gt", "value" -> value)
  }
  final case class InspectFile(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "inspect_file", "value" -> value)
  }
  final case class IpMatch(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "ip_match", "value" -> value)
  }
  final case class IpMatchFromFile(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "ip_match_from_file", "value" -> value)
  }
  final case class Le(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "le", "value" -> value)
  }
  final case class Lt(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "lt", "value" -> value)
  }
  final case class Pm(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "pm", "value" -> value)
  }
  final case class PmFromFile(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "pm_from_file", "value" -> value)
  }
  final case class Rbl(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "rbl", "value" -> value)
  }
  final case class Rsub(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "rsub", "value" -> value)
  }
  final case class Rx(pattern: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "rx", "pattern" -> pattern)
  }
  final case class RxGlobal(pattern: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "rx_global", "pattern" -> pattern)
  }
  final case class Streq(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "streq", "value" -> value)
  }
  final case class StrMatch(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "strmatch", "value" -> value)
  }
  final case class UnconditionalMatch() extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "unconditional_match")
  }
  final case class ValidateByteRange(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "validate_byte_range", "value" -> value)
  }
  final case class ValidateDTD(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "validate_dtd", "value" -> value)
  }
  final case class ValidateHash(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "validate_hash", "value" -> value)
  }
  final case class ValidateSchema(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "validate_schema", "value" -> value)
  }
  final case class ValidateUrlEncoding(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "validate_url_encoding", "value" -> value)
  }
  final case class ValidateUtf8Encoding(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "validate_utf8_encoding", "value" -> value)
  }
  final case class VerifyCC(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "verify_cc", "value" -> value)
  }
  final case class VerifyCPF(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "verify_cpf", "value" -> value)
  }
  final case class VerifySSN(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "verify_ssn", "value" -> value)
  }
  final case class VerifySVNR(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "verify_svnr", "value" -> value)
  }
  final case class Within(value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "within", "value" -> value)
  }
  final case class Raw(name: String, value: String) extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "raw", "name" -> name, "value" -> value)
  }
}

final case class Actions(
    actions: List[Action]
) extends AstNode {
  def json: JsValue = Json.obj("type" -> "Actions", "actions" -> actions.map(_.json))
}

sealed trait Action extends AstNode {
  def json: JsValue
}

object Action {
  final case class Accuracy(value: Int) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "accuracy", "value" -> value)
  }
  final case class Allow(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "allow", "value" -> value)
  }
  final case class Append(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "append", "value" -> value)
  }
  final case class AuditLog() extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "audit_log")
  }
  final case class Block() extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "block")
  }
  final case class Capture() extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "capture")
  }
  case object Chain extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "chain")
  }
  
  sealed trait CtlAction extends Action {
    def json: JsValue
  }
  object CtlAction {
    final case class AuditEngine(value: String) extends CtlAction {
      def json: JsValue = Json.obj("type" -> "CtlAction", "action_type" -> "audit_engine", "value" -> value)
    }
    final case class AuditLogParts(parts: String) extends CtlAction {
      def json: JsValue = Json.obj("type" -> "CtlAction", "action_type" -> "audit_log_parts", "parts" -> parts)
    }
    final case class RequestBodyProcessor(value: String) extends CtlAction {
      def json: JsValue = Json.obj("type" -> "CtlAction", "action_type" -> "request_body_processor", "value" -> value)
    }
    final case class ForceRequestBodyVariable(value: String) extends CtlAction {
      def json: JsValue = Json.obj("type" -> "CtlAction", "action_type" -> "force_request_body_variable", "value" -> value)
    }
    final case class RequestBodyAccess(value: String) extends CtlAction {
      def json: JsValue = Json.obj("type" -> "CtlAction", "action_type" -> "request_body_access", "value" -> value)
    }
    final case class RuleEngine(value: String) extends CtlAction {
      def json: JsValue = Json.obj("type" -> "CtlAction", "action_type" -> "rule_engine", "value" -> value)
    }
    final case class RuleRemoveByTag(tag: String) extends CtlAction {
      def json: JsValue = Json.obj("type" -> "CtlAction", "action_type" -> "rule_remove_by_tag", "tag" -> tag)
    }
    final case class RuleRemoveById(id: Int) extends CtlAction {
      def json: JsValue = Json.obj("type" -> "CtlAction", "action_type" -> "rule_remove_by_id", "id" -> id)
    }
    final case class RuleRemoveTargetById(id: Int, target: String) extends CtlAction {
      def json: JsValue = Json.obj("type" -> "CtlAction", "action_type" -> "rule_remove_target_by_id", "id" -> id, "target" -> target)
    }
    final case class RuleRemoveTargetByTag(tag: String, target: String) extends CtlAction {
      def json: JsValue = Json.obj("type" -> "CtlAction", "action_type" -> "rule_remove_target_by_tag", "tag" -> tag, "target" -> target)
    }
  }
  
  case object Deny extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "deny")
  }
  final case class DeprecateVar(expr: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "deprecate_var", "expr" -> expr)
  }
  case object Drop extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "drop")
  }
  final case class Exec(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "exec", "value" -> value)
  }
  final case class ExpireVar(expr: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "expire_var", "expr" -> expr)
  }
  final case class Id(value: Int) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "id", "value" -> value)
  }
  final case class InitCol(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "init_col", "value" -> value)
  }
  final case class LogData(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "log_data", "value" -> value)
  }
  case object Log extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "log")
  }
  final case class Maturity(value: Int) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "maturity", "value" -> value)
  }
  final case class Msg(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "msg", "value" -> value)
  }
  case object MultiMatch extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "multi_match")
  }
  case object NoAuditLog extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "no_audit_log")
  }
  case object NoLog extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "no_log")
  }
  case object Pass extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "pass")
  }
  final case class Pause(value: Int) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "pause", "value" -> value)
  }
  final case class Phase(value: Int) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "phase", "value" -> value)
  }
  final case class Prepend(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "prepend", "value" -> value)
  }
  final case class Proxy(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "proxy", "value" -> value)
  }
  final case class Redirect(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "redirect", "value" -> value)
  }
  final case class Rev(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "rev", "value" -> value)
  }
  final case class SanitiseArg(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "sanitise_arg", "value" -> value)
  }
  final case class SanitiseMatchedBytes(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "sanitise_matched_bytes", "value" -> value)
  }
  final case class SanitiseMatched() extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "sanitise_matched")
  }
  final case class SanitiseRequestHeader(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "sanitise_request_header", "value" -> value)
  }
  final case class SanitiseResponseHeader(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "sanitise_response_header", "value" -> value)
  }
  final case class SetEnv(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "set_env", "value" -> value)
  }
  final case class SetRsc(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "set_rsc", "value" -> value)
  }
  final case class SetSid(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "set_sid", "value" -> value)
  }
  final case class SetUid(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "set_uid", "value" -> value)
  }
  final case class SetVar(expr: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "set_var", "expr" -> expr)
  }
  final case class Severity(value: SeverityValue) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "severity", "value" -> value.toString)
  }
  final case class SkipAfter(marker: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "skip_after", "marker" -> marker)
  }
  final case class Skip(count: Int) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "skip", "count" -> count)
  }
  final case class Status(code: Int) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "status", "code" -> code)
  }
  final case class Tag(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "tag", "value" -> value)
  }
  final case class Ver(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "ver", "value" -> value)
  }
  final case class Xmlns(value: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "xmlns", "value" -> value)
  }
  final case class Transform(name: String) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "transform", "name" -> name)
  }
  
  final case class Raw(name: String, value: Option[String]) extends Action {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "raw", "name" -> name, "value" -> value)
  }
}

sealed trait SeverityValue extends AstNode {
  def json: JsValue
}
object SeverityValue {

  case object Emergency extends SeverityValue {
    def json: JsValue = Json.obj("type" -> "SeverityValue", "value" -> "emergency")
  }
  case object Alert extends SeverityValue {
    def json: JsValue = Json.obj("type" -> "SeverityValue", "value" -> "alert")
  }
  case object Critical extends SeverityValue {
    def json: JsValue = Json.obj("type" -> "SeverityValue", "value" -> "critical")
  }
  case object Error extends SeverityValue {
    def json: JsValue = Json.obj("type" -> "SeverityValue", "value" -> "error")
  }
  case object Warning extends SeverityValue {
    def json: JsValue = Json.obj("type" -> "SeverityValue", "value" -> "warning")
  }
  case object Notice extends SeverityValue {
    def json: JsValue = Json.obj("type" -> "SeverityValue", "value" -> "notice")
  }
  case object Info extends SeverityValue {
    def json: JsValue = Json.obj("type" -> "SeverityValue", "value" -> "info")
  }
  case object Debug extends SeverityValue {
    def json: JsValue = Json.obj("type" -> "SeverityValue", "value" -> "debug")
  }
  final case class Numeric(value: Int) extends SeverityValue {
    def json: JsValue = Json.obj("type" -> "SeverityValue", "value" -> value)
  }

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
