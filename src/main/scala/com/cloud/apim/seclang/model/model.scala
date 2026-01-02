package com.cloud.apim.seclang.model

import akka.util.ByteString
import com.cloud.apim.seclang.impl.utils.StatusCodes
import play.api.libs.json._

import scala.collection.concurrent.TrieMap
import scala.util.{Failure, Success, Try}

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

object Configuration {
  def empty: Configuration = Configuration(List.empty)
  
  val format: Format[Configuration] = new Format[Configuration] {
    def reads(json: JsValue): JsResult[Configuration] = Try {
      Configuration((json \ "statements").asOpt[List[JsValue]].getOrElse(List.empty).flatMap { statement =>
        Statement.format.reads(statement).asOpt
      })
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(config) => JsSuccess(config)
    }
    
    def writes(config: Configuration): JsValue = config.json
  }

}

sealed trait Statement extends AstNode

object Statement {
  val format = new Format[Statement] {
    override def reads(json: JsValue): JsResult[Statement] = Try {
      val typ = (json \ "type").as[String]
      typ match {
        case "SecRule" => SecRule.format.reads(json).get
        case "SecRuleScript" => SecRuleScript.format.reads(json).get
        case "SecAction" => SecAction.format.reads(json).get
        case "SecRuleRemoveById" => SecRuleRemoveById.format.reads(json).get
        case "SecRuleRemoveByMsg" => SecRuleRemoveByMsg.format.reads(json).get
        case "SecRuleRemoveByTag" => SecRuleRemoveByTag.format.reads(json).get
        case "SecRuleUpdateActionById" => SecRuleUpdateActionById.format.reads(json).get
        case "SecMarker" => SecMarker.format.reads(json).get
        case "EngineConfigDirective" => EngineConfigDirective.format.reads(json).get
        case other => throw new RuntimeException(s"Unknown statement type: $other")
      }
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(statement) => JsSuccess(statement)
    }

    override def writes(o: Statement): JsValue = o.json
  }
}

object SecRule {
  val format: Format[SecRule] = new Format[SecRule] {
    def reads(json: JsValue): JsResult[SecRule] = Try {
      val commentBlock = (json \ "comment_block").asOpt[JsObject].flatMap(CommentBlock.format.reads(_).asOpt)
      val variables = Variables.format.reads((json \ "variables").as[JsObject]).get
      val operator = Operator.format.reads((json \ "operator").as[JsObject]).get
      val actions = (json \ "actions").asOpt[JsObject].flatMap(Actions.format.reads(_).asOpt)
      SecRule(commentBlock, variables, operator, actions)
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(rule) => JsSuccess(rule)
    }
    def writes(rule: SecRule): JsValue = rule.json
  }
}

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

object SecRuleScript {
  val format: Format[SecRuleScript] = new Format[SecRuleScript] {
    def reads(json: JsValue): JsResult[SecRuleScript] = Try {
      val commentBlock = (json \ "comment_block").asOpt[JsObject].flatMap(CommentBlock.format.reads(_).asOpt)
      val filePath = (json \ "file_path").as[String]
      val actions = (json \ "actions").asOpt[JsObject].flatMap(Actions.format.reads(_).asOpt)
      SecRuleScript(commentBlock, filePath, actions)
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(script) => JsSuccess(script)
    }
    def writes(script: SecRuleScript): JsValue = script.json
  }
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

object SecAction {
  val format: Format[SecAction] = new Format[SecAction] {
    def reads(json: JsValue): JsResult[SecAction] = Try {
      val commentBlock = (json \ "comment_block").asOpt[JsObject].flatMap(CommentBlock.format.reads(_).asOpt)
      val actions = Actions.format.reads((json \ "actions").as[JsObject]).get
      SecAction(commentBlock, actions)
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(action) => JsSuccess(action)
    }
    def writes(action: SecAction): JsValue = action.json
  }
}

final case class SecAction(
    commentBlock: Option[CommentBlock],
    actions: Actions
) extends Statement {
  lazy val id: Option[Int] = actions.actions.collectFirst {
    case Action.Id(v) => v
  }
  lazy val phase: Int = actions.actions.collectFirst {
    case Action.Phase(p) => p
  }.getOrElse(2)
  def json: JsValue = Json.obj(
    "type" -> "SecAction",
    "id" -> id,
    "phase" -> phase,
    "actions" -> actions.json,
    "comment_block" -> commentBlock.map(_.json).getOrElse(JsNull).as[JsValue],
  )
}

object SecRuleRemoveById {
  val format: Format[SecRuleRemoveById] = new Format[SecRuleRemoveById] {
    def reads(json: JsValue): JsResult[SecRuleRemoveById] = Try {
      val commentBlock = (json \ "comment_block").asOpt[JsObject].flatMap(CommentBlock.format.reads(_).asOpt)
      val ids = (json \ "ids").as[List[Int]]
      SecRuleRemoveById(commentBlock, ids)
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(remove) => JsSuccess(remove)
    }
    def writes(remove: SecRuleRemoveById): JsValue = remove.json
  }
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

object SecRuleRemoveByMsg {
  val format: Format[SecRuleRemoveByMsg] = new Format[SecRuleRemoveByMsg] {
    def reads(json: JsValue): JsResult[SecRuleRemoveByMsg] = Try {
      val commentBlock = (json \ "comment_block").asOpt[JsObject].flatMap(CommentBlock.format.reads(_).asOpt)
      val value = (json \ "value").as[String]
      SecRuleRemoveByMsg(commentBlock, value)
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(remove) => JsSuccess(remove)
    }
    def writes(remove: SecRuleRemoveByMsg): JsValue = remove.json
  }
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

object SecRuleRemoveByTag {
  val format: Format[SecRuleRemoveByTag] = new Format[SecRuleRemoveByTag] {
    def reads(json: JsValue): JsResult[SecRuleRemoveByTag] = Try {
      val commentBlock = (json \ "comment_block").asOpt[JsObject].flatMap(CommentBlock.format.reads(_).asOpt)
      val value = (json \ "value").as[String]
      SecRuleRemoveByTag(commentBlock, value)
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(remove) => JsSuccess(remove)
    }
    def writes(remove: SecRuleRemoveByTag): JsValue = remove.json
  }
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

object SecRuleUpdateActionById {
  val format: Format[SecRuleUpdateActionById] = new Format[SecRuleUpdateActionById] {
    def reads(json: JsValue): JsResult[SecRuleUpdateActionById] = Try {
      val commentBlock = (json \ "comment_block").asOpt[JsObject].flatMap(CommentBlock.format.reads(_).asOpt)
      val id = (json \ "id").as[Int]
      val actions = Actions.format.reads((json \ "actions").as[JsObject]).get
      SecRuleUpdateActionById(commentBlock, id, actions)
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(update) => JsSuccess(update)
    }
    def writes(update: SecRuleUpdateActionById): JsValue = update.json
  }
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

object SecMarker {
  val format: Format[SecMarker] = new Format[SecMarker] {
    def reads(json: JsValue): JsResult[SecMarker] = Try {
      val commentBlock = (json \ "comment_block").asOpt[JsObject].flatMap(CommentBlock.format.reads(_).asOpt)
      val name = (json \ "name").as[String]
      SecMarker(commentBlock, name)
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(marker) => JsSuccess(marker)
    }
    def writes(marker: SecMarker): JsValue = marker.json
  }
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

object EngineConfigDirective {
  val format: Format[EngineConfigDirective] = new Format[EngineConfigDirective] {
    def reads(json: JsValue): JsResult[EngineConfigDirective] = Try {
      val commentBlock = (json \ "comment_block").asOpt[JsObject].flatMap(CommentBlock.format.reads(_).asOpt)
      val directive = ConfigDirective.format.reads((json \ "directive").as[JsObject]).get
      EngineConfigDirective(commentBlock, directive)
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(config) => JsSuccess(config)
    }
    def writes(config: EngineConfigDirective): JsValue = config.json
  }
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

object CommentBlock {
  val format: Format[CommentBlock] = new Format[CommentBlock] {
    def reads(json: JsValue): JsResult[CommentBlock] = Try {
      val comments = (json \ "comments").as[List[JsValue]].flatMap { c =>
        Comment.format.reads(c).asOpt
      }
      CommentBlock(comments)
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(block) => JsSuccess(block)
    }
    def writes(block: CommentBlock): JsValue = block.json
  }
}

final case class CommentBlock(
    comments: List[Comment]
) extends AstNode {
  def json: JsValue = Json.obj(
    "type" -> "CommentBlock",
    "comments" -> JsArray(comments.map(_.json))
  )
}

object Comment {
  val format: Format[Comment] = new Format[Comment] {
    def reads(json: JsValue): JsResult[Comment] = Try {
      val text = (json \ "text").as[String]
      Comment(text)
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(comment) => JsSuccess(comment)
    }
    def writes(comment: Comment): JsValue = comment.json
  }
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
  val format: Format[ConfigDirective] = new Format[ConfigDirective] {
    def reads(json: JsValue): JsResult[ConfigDirective] = Try {
      val typ = (json \ "type").as[String]
      typ match {
        case "AuditLog" => AuditLog((json \ "value").as[String])
        case "AuditEngine" => AuditEngine((json \ "value").as[String])
        case "AuditLogParts" => AuditLogParts((json \ "parts").as[List[String]])
        case "ComponentSignature" => ComponentSignature((json \ "value").as[String])
        case "ServerSignature" => ServerSignature((json \ "value").as[String])
        case "WebAppId" => WebAppId((json \ "value").as[String])
        case "CacheTransformations" => CacheTransformations((json \ "options").as[List[JsValue]].flatMap(ConfigOption.format.reads(_).asOpt))
        case "ChrootDir" => ChrootDir((json \ "value").as[String])
        case "ConnEngine" => ConnEngine((json \ "value").as[String])
        case "ArgumentSeparator" => ArgumentSeparator((json \ "value").as[String])
        case "DebugLog" => DebugLog((json \ "value").as[String])
        case "DebugLogLevel" => DebugLogLevel((json \ "value").as[Int])
        case "GeoLookupDb" => GeoLookupDb((json \ "value").as[String])
        case "RuleEngine" => RuleEngine((json \ "value").as[String])
        case "RequestBodyAccess" => RequestBodyAccess((json \ "value").as[String])
        case "RequestBodyLimit" => RequestBodyLimit((json \ "value").as[Int])
        case "ResponseBodyAccess" => ResponseBodyAccess((json \ "value").as[String])
        case "ResponseBodyLimit" => ResponseBodyLimit((json \ "value").as[Int])
        case "DefaultAction" => DefaultAction(Actions.format.reads((json \ "actions").as[JsObject]).get)
        case "CollectionTimeout" => CollectionTimeout((json \ "value").as[Int])
        case "Include" => Include((json \ "path").as[String])
        case "DataDir" => DataDir((json \ "value").as[String])
        case "TmpDir" => TmpDir((json \ "value").as[String])
        case "Raw" => Raw((json \ "name").as[String], (json \ "value").as[String])
        case other => throw new RuntimeException(s"Unknown ConfigDirective type: $other")
      }
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(directive) => JsSuccess(directive)
    }
    def writes(directive: ConfigDirective): JsValue = directive.json
  }

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

object ConfigOption {
  val format: Format[ConfigOption] = new Format[ConfigOption] {
    def reads(json: JsValue): JsResult[ConfigOption] = Try {
      val name = (json \ "name").as[String]
      val value = (json \ "value").asOpt[String]
      ConfigOption(name, value)
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(option) => JsSuccess(option)
    }
    def writes(option: ConfigOption): JsValue = option.json
  }
}

final case class ConfigOption(
    name: String,
    value: Option[String]
) extends AstNode {
  def json: JsValue = Json.obj("type" -> "ConfigOption", "name" -> name, "value" -> value)
}

object Variables {
  val format: Format[Variables] = new Format[Variables] {
    def reads(json: JsValue): JsResult[Variables] = Try {
      val negated = (json \ "negated").as[Boolean]
      val count = (json \ "count").as[Boolean]
      val variables = (json \ "variables").as[List[JsValue]].flatMap(Variable.format.reads(_).asOpt)
      Variables(negated, count, variables)
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(vars) => JsSuccess(vars)
    }
    def writes(vars: Variables): JsValue = vars.json
  }
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

object UpdateVariables {
  val format: Format[UpdateVariables] = new Format[UpdateVariables] {
    def reads(json: JsValue): JsResult[UpdateVariables] = Try {
      val negated = (json \ "negated").as[Boolean]
      val count = (json \ "count").as[Boolean]
      val variables = (json \ "variables").as[List[JsValue]].flatMap(Variable.format.reads(_).asOpt)
      UpdateVariables(negated, count, variables)
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(vars) => JsSuccess(vars)
    }
    def writes(vars: UpdateVariables): JsValue = vars.json
  }
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
  val format: Format[Variable] = new Format[Variable] {
    def reads(json: JsValue): JsResult[Variable] = Try {
      val typ = (json \ "type").as[String]
      typ match {
        case "SimpleVariable" => Simple((json \ "name").as[String])
        case "CollectionVariable" => Collection(
          (json \ "collection").as[String],
          (json \ "key").asOpt[String]
        )
        case other => throw new RuntimeException(s"Unknown Variable type: $other")
      }
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(variable) => JsSuccess(variable)
    }
    def writes(variable: Variable): JsValue = variable.json
  }

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
  val format: Format[Operator] = new Format[Operator] {
    def reads(json: JsValue): JsResult[Operator] = Try {
      val operatorType = (json \ "operator_type").as[String]
      operatorType match {
        case "negated" => Negated(format.reads((json \ "operator").as[JsObject]).get)
        case "begins_with" => BeginsWith((json \ "value").as[String])
        case "contains" => Contains((json \ "value").as[String])
        case "contains_word" => ContainsWord((json \ "value").as[String])
        case "detect_sqli" => DetectSQLi((json \ "value").as[String])
        case "detect_xss" => DetectXSS((json \ "value").as[String])
        case "ends_with" => EndsWith((json \ "value").as[String])
        case "eq" => Eq((json \ "value").as[String])
        case "fuzzy_hash" => FuzzyHash((json \ "value").as[String])
        case "ge" => Ge((json \ "value").as[String])
        case "geo_lookup" => GeoLookup((json \ "value").as[String])
        case "gsb_lookup" => GsbLookup((json \ "value").as[String])
        case "gt" => Gt((json \ "value").as[String])
        case "inspect_file" => InspectFile((json \ "value").as[String])
        case "ip_match" => IpMatch((json \ "value").as[String])
        case "ip_match_from_file" => IpMatchFromFile((json \ "value").as[String])
        case "le" => Le((json \ "value").as[String])
        case "lt" => Lt((json \ "value").as[String])
        case "pm" => Pm((json \ "value").as[String])
        case "pm_from_file" => PmFromFile((json \ "value").as[String])
        case "rbl" => Rbl((json \ "value").as[String])
        case "rsub" => Rsub((json \ "value").as[String])
        case "rx" => Rx((json \ "pattern").as[String])
        case "rx_global" => RxGlobal((json \ "pattern").as[String])
        case "streq" => Streq((json \ "value").as[String])
        case "strmatch" => StrMatch((json \ "value").as[String])
        case "unconditional_match" => UnconditionalMatch()
        case "validate_byte_range" => ValidateByteRange((json \ "value").as[String])
        case "validate_dtd" => ValidateDTD((json \ "value").as[String])
        case "validate_hash" => ValidateHash((json \ "value").as[String])
        case "validate_schema" => ValidateSchema((json \ "value").as[String])
        case "validate_url_encoding" => ValidateUrlEncoding((json \ "value").as[String])
        case "validate_utf8_encoding" => ValidateUtf8Encoding((json \ "value").as[String])
        case "verify_cc" => VerifyCC((json \ "value").as[String])
        case "verify_cpf" => VerifyCPF((json \ "value").as[String])
        case "verify_ssn" => VerifySSN((json \ "value").as[String])
        case "verify_svnr" => VerifySVNR((json \ "value").as[String])
        case "within" => Within((json \ "value").as[String])
        case "raw" => Raw((json \ "name").as[String], (json \ "value").as[String])
        case other => throw new RuntimeException(s"Unknown Operator type: $other")
      }
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(operator) => JsSuccess(operator)
    }
    def writes(operator: Operator): JsValue = operator.json
  }
  
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
  final case class NoMatch() extends Operator {
    def json: JsValue = Json.obj("type" -> "Operator", "operator_type" -> "no_match")
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

object Actions {
  val format: Format[Actions] = new Format[Actions] {
    def reads(json: JsValue): JsResult[Actions] = Try {
      val actions = (json \ "actions").as[List[JsValue]].flatMap(Action.format.reads(_).asOpt)
      Actions(actions)
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(actions) => JsSuccess(actions)
    }
    def writes(actions: Actions): JsValue = actions.json
  }
}

final case class Actions(
    actions: List[Action]
) extends AstNode {
  def json: JsValue = Json.obj("type" -> "Actions", "actions" -> actions.map(_.json))
}

sealed trait Action extends AstNode {
  def json: JsValue
  def isMetaData: Boolean = false
  def isData: Boolean = false
  def isDisruptive: Boolean = false
  def isNonDisruptive: Boolean = false
  def isFlow: Boolean = false
  def needsRun: Boolean = false
  def isCtl: Boolean = false
}
sealed trait DataAction extends Action {
  override def isData: Boolean = true
}
sealed trait NonDisruptiveAction extends Action {
  override def isNonDisruptive: Boolean = true
}
sealed trait MetaDataAction extends Action {
  override def isMetaData: Boolean = true
}
sealed trait FlowAction extends Action {
  override def isFlow: Boolean = true
}
sealed trait DisruptiveAction extends Action {
  override def isDisruptive: Boolean = true
}
sealed trait NeedRunAction extends Action {
  override def needsRun: Boolean = true
}
sealed trait UnsupportedAction extends Action

object Action {
  val format: Format[Action] = new Format[Action] {
    def reads(json: JsValue): JsResult[Action] = Try {
      val typ = (json \ "type").as[String]
      val actionType = (json \ "action_type").as[String]
      
      (typ, actionType) match {
        case ("Action", "accuracy") => Accuracy((json \ "value").as[Int])
        case ("Action", "allow") => Allow((json \ "value").as[String])
        case ("Action", "append") => Append((json \ "value").as[String])
        case ("Action", "audit_log") => AuditLog()
        case ("Action", "block") => Block()
        case ("Action", "capture") => Capture()
        case ("Action", "chain") => Chain
        case ("Action", "deny") => Deny
        case ("Action", "deprecate_var") => DeprecateVar((json \ "expr").as[String])
        case ("Action", "drop") => Drop
        case ("Action", "exec") => Exec((json \ "value").as[String])
        case ("Action", "expire_var") => ExpireVar((json \ "expr").as[String])
        case ("Action", "id") => Id((json \ "value").as[Int])
        case ("Action", "init_col") => InitCol((json \ "value").as[String])
        case ("Action", "log_data") => LogData((json \ "value").as[String])
        case ("Action", "log") => Log
        case ("Action", "maturity") => Maturity((json \ "value").as[Int])
        case ("Action", "msg") => Msg((json \ "value").as[String])
        case ("Action", "multi_match") => MultiMatch
        case ("Action", "no_audit_log") => NoAuditLog
        case ("Action", "no_log") => NoLog
        case ("Action", "pass") => Pass
        case ("Action", "pause") => Pause((json \ "value").as[Int])
        case ("Action", "phase") => Phase((json \ "value").as[Int])
        case ("Action", "prepend") => Prepend((json \ "value").as[String])
        case ("Action", "proxy") => Proxy((json \ "value").as[String])
        case ("Action", "redirect") => Redirect((json \ "value").as[String])
        case ("Action", "rev") => Rev((json \ "value").as[String])
        case ("Action", "sanitise_arg") => SanitiseArg((json \ "value").as[String])
        case ("Action", "sanitise_matched_bytes") => SanitiseMatchedBytes((json \ "value").as[String])
        case ("Action", "sanitise_matched") => SanitiseMatched()
        case ("Action", "sanitise_request_header") => SanitiseRequestHeader((json \ "value").as[String])
        case ("Action", "sanitise_response_header") => SanitiseResponseHeader((json \ "value").as[String])
        case ("Action", "set_env") => SetEnv((json \ "value").as[String])
        case ("Action", "set_rsc") => SetRsc((json \ "value").as[String])
        case ("Action", "set_sid") => SetSid((json \ "value").as[String])
        case ("Action", "set_uid") => SetUid((json \ "value").as[String])
        case ("Action", "set_var") => SetVar((json \ "expr").as[String])
        case ("Action", "severity") => Severity(SeverityValue((json \ "value").as[String]))
        case ("Action", "skip_after") => SkipAfter((json \ "marker").as[String])
        case ("Action", "skip") => Skip((json \ "count").as[Int])
        case ("Action", "status") => Status((json \ "code").as[Int])
        case ("Action", "tag") => Tag((json \ "value").as[String])
        case ("Action", "ver") => Ver((json \ "value").as[String])
        case ("Action", "xmlns") => Xmlns((json \ "value").as[String])
        case ("Action", "transform") => Transform((json \ "name").as[String])
        case ("Action", "raw") => Raw((json \ "name").as[String], (json \ "value").asOpt[String])
        case ("CtlAction", "audit_engine") => CtlAction.AuditEngine((json \ "value").as[String])
        case ("CtlAction", "audit_log_parts") => CtlAction.AuditLogParts((json \ "parts").as[String])
        case ("CtlAction", "request_body_processor") => CtlAction.RequestBodyProcessor((json \ "value").as[String])
        case ("CtlAction", "force_request_body_variable") => CtlAction.ForceRequestBodyVariable((json \ "value").as[String])
        case ("CtlAction", "request_body_access") => CtlAction.RequestBodyAccess((json \ "value").as[String])
        case ("CtlAction", "rule_engine") => CtlAction.RuleEngine((json \ "value").as[String])
        case ("CtlAction", "rule_remove_by_tag") => CtlAction.RuleRemoveByTag((json \ "tag").as[String])
        case ("CtlAction", "rule_remove_by_id") => CtlAction.RuleRemoveById((json \ "id").as[Int])
        case ("CtlAction", "rule_remove_target_by_id") => CtlAction.RuleRemoveTargetById((json \ "id").as[Int], (json \ "target").as[String])
        case ("CtlAction", "rule_remove_target_by_tag") => CtlAction.RuleRemoveTargetByTag((json \ "tag").as[String], (json \ "target").as[String])
        case other => throw new RuntimeException(s"Unknown Action type: $other")
      }
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(action) => JsSuccess(action)
    }
    def writes(action: Action): JsValue = action.json
  }
  
  final case class Accuracy(value: Int) extends MetaDataAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "accuracy", "value" -> value)
  }
  final case class Allow(value: String) extends DisruptiveAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "allow", "value" -> value)
  }
  final case class Append(value: String) extends UnsupportedAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "append", "value" -> value)
  }
  final case class AuditLog() extends NonDisruptiveAction with NeedRunAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "audit_log")
  }
  final case class Block() extends DisruptiveAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "block")
  }
  final case class Capture() extends NonDisruptiveAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "capture")
  }
  case object Chain extends FlowAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "chain")
  }
  
  sealed trait CtlAction extends Action {
    def json: JsValue
    override def isCtl: Boolean = true
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
  
  case object Deny extends FlowAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "deny")
  }
  final case class DeprecateVar(expr: String) extends UnsupportedAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "deprecate_var", "expr" -> expr)
  }
  case object Drop extends FlowAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "drop")
  }
  final case class Exec(value: String) extends UnsupportedAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "exec", "value" -> value)
  }
  final case class ExpireVar(expr: String) extends NonDisruptiveAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "expire_var", "expr" -> expr)
  }
  final case class Id(value: Int) extends MetaDataAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "id", "value" -> value)
  }
  final case class InitCol(value: String) extends NonDisruptiveAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "init_col", "value" -> value)
  }
  final case class LogData(value: String) extends NonDisruptiveAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "log_data", "value" -> value)
  }
  case object Log extends NonDisruptiveAction with NeedRunAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "log")
  }
  final case class Maturity(value: Int) extends MetaDataAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "maturity", "value" -> value)
  }
  final case class Msg(value: String) extends MetaDataAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "msg", "value" -> value)
  }
  case object MultiMatch extends NonDisruptiveAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "multi_match")
  }
  case object NoAuditLog extends NonDisruptiveAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "no_audit_log")
  }
  case object NoLog extends NonDisruptiveAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "no_log")
  }
  case object Pass extends FlowAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "pass")
  }
  final case class Pause(value: Int) extends UnsupportedAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "pause", "value" -> value)
  }
  final case class Phase(value: Int) extends MetaDataAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "phase", "value" -> value)
  }
  final case class Prepend(value: String) extends UnsupportedAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "prepend", "value" -> value)
  }
  final case class Proxy(value: String) extends UnsupportedAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "proxy", "value" -> value)
  }
  final case class Redirect(value: String) extends DisruptiveAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "redirect", "value" -> value)
  }
  final case class Rev(value: String) extends MetaDataAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "rev", "value" -> value)
  }
  final case class SanitiseArg(value: String) extends UnsupportedAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "sanitise_arg", "value" -> value)
  }
  final case class SanitiseMatchedBytes(value: String) extends UnsupportedAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "sanitise_matched_bytes", "value" -> value)
  }
  final case class SanitiseMatched() extends UnsupportedAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "sanitise_matched")
  }
  final case class SanitiseRequestHeader(value: String) extends UnsupportedAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "sanitise_request_header", "value" -> value)
  }
  final case class SanitiseResponseHeader(value: String) extends UnsupportedAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "sanitise_response_header", "value" -> value)
  }
  final case class SetEnv(value: String) extends NonDisruptiveAction with NeedRunAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "set_env", "value" -> value)
  }
  final case class SetRsc(value: String) extends NonDisruptiveAction with NeedRunAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "set_rsc", "value" -> value)
  }
  final case class SetSid(value: String) extends NonDisruptiveAction with NeedRunAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "set_sid", "value" -> value)
  }
  final case class SetUid(value: String) extends NonDisruptiveAction with NeedRunAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "set_uid", "value" -> value)
  }
  final case class SetVar(expr: String) extends NonDisruptiveAction with NeedRunAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "set_var", "expr" -> expr)
  }
  final case class Severity(value: SeverityValue) extends MetaDataAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "severity", "value" -> value.toString)
  }
  final case class SkipAfter(marker: String) extends FlowAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "skip_after", "marker" -> marker)
  }
  final case class Skip(count: Int) extends FlowAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "skip", "count" -> count)
  }
  final case class Status(code: Int) extends DataAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "status", "code" -> code)
  }
  final case class Tag(value: String) extends MetaDataAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "tag", "value" -> value)
  }
  final case class Ver(value: String) extends MetaDataAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "ver", "value" -> value)
  }
  final case class Xmlns(value: String) extends DataAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "xmlns", "value" -> value)
  }
  final case class Transform(name: String) extends NonDisruptiveAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "transform", "name" -> name)
  }
  final case class Raw(name: String, value: Option[String]) extends UnsupportedAction {
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
    import com.cloud.apim.seclang.impl.utils.Implicits._
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

object RequestContext {
  def apply(json: JsValue): RequestContext = {
    val path = (json \ "uri").asOpt[String].getOrElse("/")
    val port = (json \ "port").asOpt[Int].getOrElse(80)
    val address = (json \ "dest_addr").asOpt[String].getOrElse("127.0.0.1")
    val scheme = if (port == 80) "http" else "https"
    val uri = s"$scheme://$address:$port$path"
    val body = (json \ "data").asOpt[String].map(s => ByteString(s))
    val isResponse = path.startsWith("/reflect")
    val respStruct = if (isResponse) Try(Json.parse(body.map(_.utf8String).getOrElse("{}"))).getOrElse(Json.obj("body" -> body.getOrElse(ByteString.empty).utf8String)) else Json.obj()
    val respStatus = if (isResponse) Some((respStruct \ "status").asOpt[Int].getOrElse(200)) else None
    val respStatusTxt = if (isResponse) StatusCodes.get((respStruct \ "status").asOpt[Int].getOrElse(200)) else None
    val headers: Map[String, List[String]] = (json \ "headers").asOpt[Map[String, String]].map(_.mapValues(v => List(v))).getOrElse(Map.empty)
    val respHeaders: Map[String, List[String]] = (respStruct \ "headers").asOpt[Map[String, String]].map(_.mapValues(v => List(v))).getOrElse(Map.empty)
    val encBody = (respStruct \ "encodedBody").asOpt[String].map(s => ByteString(s).decodeBase64)
    val respBody = encBody.orElse((respStruct \ "body").asOpt[String].map(s => ByteString(s)))
    RequestContext(
      method = (json \ "method").asOpt[String].getOrElse("GET"),
      uri = uri,
      status = respStatus,
      statusTxt = respStatusTxt,
      // cookies = (json \ "cookies").asOpt[Map[String, String]].map(_.mapValues(v => List(v))).getOrElse(Map.empty),
      headers = if (isResponse) respHeaders else headers,
      body = if (isResponse) respBody else body,
      protocol = (json \ "protocol").asOpt[String].getOrElse("HTTP/1.1"),
    )
  }
}

final case class RequestContext(
  requestId: String = s"${System.currentTimeMillis}.${scala.util.Random.nextInt(1000000).formatted("%06d")}",
  method: String,
  uri: String,
  headers: Map[String, List[String]] = Map.empty,
  cookies: Map[String, List[String]] = Map.empty,
  query: Map[String, List[String]] = Map.empty,
  body: Option[ByteString] = None,
  status: Option[Int] = None, // when used as response
  statusTxt: Option[String] = None, // when used as response
  startTime: Long = System.currentTimeMillis(),
  remoteAddr: String = "0.0.0.0",
  remotePort: Int = 1234,
  protocol: String = "HTTP/1.1",
  secure: Boolean = false,
  variables: Map[String, String] = Map.empty,
  cache: TrieMap[String, List[String]] = new TrieMap[String, List[String]],
) {
  def isResponse: Boolean = status.isDefined
  def uriRaw: String = {
    val host = headers.get("Host").orElse(headers.get("host")).getOrElse("")
    s"${if (secure) "https" else "http"}://$host$uri"
  }
  def statusLine: String = {
    s"${protocol} ${status.getOrElse("0")} ${statusTxt.orElse(StatusCodes.get(status.getOrElse(0))).getOrElse("--")}"
  }
  def contentType: Option[String] = {
    headers.get("Content-Type").orElse(headers.get("content-type")).flatMap(_.lastOption)
  }
  def contentLength: Option[String] = {
    headers.get("Content-Length").orElse(headers.get("content-length")).flatMap(_.lastOption)
  }
}

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
) {
  def display(): String = {
    s"${disposition}\n${events.filter(_.msg.isDefined).map(e => s"  - match phase=${e.phase} rule_id=${e.ruleId.getOrElse(0)} - ${e.msg.getOrElse("no msg")}").mkString("\n")}"
  }
  def displayPrintln(): EngineResult = {
    println(display())
    this
  }
}
final case class RuntimeState(disabledIds: Set[Int], events: List[MatchEvent])

final case class SecRulesEngineConfig()

object SecRulesEngineConfig {
  val default = SecRulesEngineConfig()
}