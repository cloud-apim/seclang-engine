package com.cloud.apim.seclang.model

import akka.util.ByteString
import com.cloud.apim.seclang.impl.compiler.Compiler
import com.cloud.apim.seclang.impl.parser.AntlrParser
import com.cloud.apim.seclang.impl.utils.{HashUtilsFast, StatusCodes}
import com.github.blemale.scaffeine.Scaffeine
import play.api.libs.json._

import java.io.File
import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.nio.file.{Files, Path, Paths}
import java.util.concurrent.atomic.AtomicReference
import scala.collection.JavaConverters.asScalaIteratorConverter
import scala.collection.concurrent.TrieMap
import scala.concurrent.duration.FiniteDuration
import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}

sealed trait AstNode {
  def json: JsValue
}

final case class Configuration(
    statements: List[Statement],
    hash: String,
) extends AstNode {
  def json: JsValue = Json.obj(
    "type" -> "Configuration",
    "statements" -> JsArray(statements.map(_.json)),
    "hash" -> hash
  )
}

object Configuration {
  def empty: Configuration = Configuration(List.empty, "")
  
  val format: Format[Configuration] = new Format[Configuration] {
    def reads(json: JsValue): JsResult[Configuration] = Try {
      Configuration(
        statements = (json \ "statements").asOpt[List[JsValue]].getOrElse(List.empty).flatMap { statement =>
          Statement.format.reads(statement).asOpt
        },
        hash = (json \ "hash").asOpt[String].getOrElse("")
      )
    } match {
      case Failure(ex) => 
        ex.printStackTrace()
        JsError(ex.getMessage)
      case Success(config) => JsSuccess(config)
    }
    
    def writes(config: Configuration): JsValue = config.json
  }

  def fromSource(source: ConfigurationSource): Configuration = {
    AntlrParser.parse(source.getRules()).right.get
  }

  def fromList(source: ConfigurationSourceList): Configuration = {
    val src = source.sources.map(_.getRules()).mkString("\n")
    AntlrParser.parse(src).right.get
  }
}

trait FilesSource {
  def getFiles(): Map[String, String]
}
case class FilesSourceList(sources: List[FilesSource])

case class FsFilesSource(virtPath: String, path: String) extends FilesSource {
  override def getFiles(): Map[String, String] = {
    Map(virtPath -> Files.readString(new File(path).toPath))
  }
}

case class FsScanFilesSource(dirPath: String, pattern: String) extends FilesSource {
  private val regex: Regex = pattern.r
  override def getFiles(): Map[String, String] = {
    val root = Paths.get(dirPath)

    if (!Files.exists(root) || !Files.isDirectory(root)) {
      return Map.empty
    }

    val files: Seq[Path] =
      Files.walk(root)
        .iterator()
        .asScala
        .filter(p => Files.isRegularFile(p))
        .filter(p => regex.findFirstIn(p.getFileName.toString).isDefined)
        .toSeq
        .sortBy(_.toString)

    files
      .map { path =>
        (path.toAbsolutePath.toString.replaceFirst(dirPath, ""), Files.readString(path))
      }.toMap
  }
}

object HttpFilesSource {
  val client = HttpClient.newHttpClient()
}

case class HttpFilesSource(virtPath: String, url: String) extends FilesSource {
  override def getFiles(): Map[String, String] = {
    val request = HttpRequest.newBuilder()
      .uri(URI.create(url))
      .GET()
      .build()
    val response = HttpConfigurationSource.client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    )
    Map(virtPath -> response.body())
  }
}

case class RawFilesSource(virtPath: String, content: String) extends FilesSource {
  override def getFiles(): Map[String, String] = Map(virtPath -> content)
}

case class RawFsFilesSource(files: Map[String, String]) extends FilesSource {
  override def getFiles(): Map[String, String] = files
}


object ConfigurationSourceList {
  val empty: ConfigurationSourceList = ConfigurationSourceList(List.empty)
}

case class ConfigurationSourceList(sources: List[ConfigurationSource])

trait ConfigurationSource {
  def getRules(): String
  def asList(): ConfigurationSourceList = ConfigurationSourceList(List(this))
}

case class FileConfigurationSource(path: String) extends ConfigurationSource {
  def getRules(): String = {
    Files.readString(new File(path).toPath)
  }
}

case class FileScanConfigurationSource(dirPath: String, pattern: String) extends ConfigurationSource {
  private val regex: Regex = pattern.r
  def getRules(): String = {
    val root = Paths.get(dirPath)

    if (!Files.exists(root) || !Files.isDirectory(root)) {
      return ""
    }

    val files: Seq[Path] =
      Files.walk(root)
        .iterator()
        .asScala
        .filter(p => Files.isRegularFile(p))
        .filter(p => regex.findFirstIn(p.getFileName.toString).isDefined)
        .toSeq
        .sortBy(_.toString)

    files
      .map { path =>
        Files.readString(path)
      }
      .mkString("\n")
  }
}

object HttpConfigurationSource {
  val client = HttpClient.newHttpClient()
}

case class HttpConfigurationSource(url: String) extends ConfigurationSource {
  override def getRules(): String = {
    val request = HttpRequest.newBuilder()
      .uri(URI.create(url))
      .GET()
      .build()
    val response = HttpConfigurationSource.client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    )
    response.body()
  }
}

case class RawConfigurationSource(rules: String) extends ConfigurationSource {
  override def getRules(): String = rules
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

  lazy val tags: Set[String] = actions.toList.flatMap(_.actions.collect {
    case Action.Tag(v) => v
  }).toSet

  lazy val msgs: Set[String] = actions.toList.flatMap(_.actions.collect {
    case Action.Msg(v) => v
  }).toSet

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
  lazy val tags: Set[String] = actions.actions.collect {
    case Action.Tag(v) => v
  }.toSet
  lazy val msgs: Set[String] = actions.actions.collect {
    case Action.Msg(v) => v
  }.toSet
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
      val negated_variables = (json \ "negated_variables").as[List[JsValue]].flatMap(Variable.format.reads(_).asOpt)
      Variables(negated, count, variables, negated_variables)
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
    variables: List[Variable],
    negatedVariables: List[Variable]
) extends AstNode {
  def json: JsValue = Json.obj(
    "type" -> "Variables",
    "negated" -> negated,
    "count" -> count,
    "variables" -> JsArray(variables.map(_.json)),
    "negated_variables" -> JsArray(negatedVariables.map(_.json)),
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
  lazy val hasPhase: Boolean = phase.isDefined
  lazy val phase: Option[Int] = actions.collectFirst {
    case Action.Phase(p) => p
  }
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
  final case class Capture() extends NonDisruptiveAction with NeedRunAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "capture")
  }
  case object Chain extends FlowAction {
    def json: JsValue = Json.obj("type" -> "Action", "action_type" -> "chain")
  }
  
  sealed trait CtlAction extends Action with NeedRunAction {
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
  def isXwwwFormUrlEncoded: Boolean = {
    contentType.contains("application/www-form-urlencoded") || contentType.contains("application/x-www-form-urlencoded")
  }
  def isWwwFormUrlEncoded: Boolean = {
    contentType.contains("application/www-form-urlencoded") || contentType.contains("application/x-www-form-urlencoded")
  }
  def json: JsValue = Json.obj(
    "requestId" -> requestId,
    "method" -> method,
    "uri" -> uri,
    "headers" -> headers,
    "cookies" -> cookies,
    "query" -> query,
    "body" -> body,
    "status" -> status,
    "statusTxt" -> statusTxt,
    "startTime" -> startTime,
    "remoteAddr" -> remoteAddr,
    "remotePort" -> remotePort,
    "protocol" -> protocol,
    "secure" -> secure,
    "variables" -> variables,
  )
}

sealed trait Disposition {
  def json: JsValue
}
object Disposition {
  case object Continue extends Disposition {
    def json: JsValue = Json.obj("type" -> "Disposition", "action_type" -> "continue")
  }
  final case class Block(status: Int, msg: Option[String], ruleId: Option[Int]) extends Disposition {
    val message = msg.getOrElse("--")
    val id = ruleId.getOrElse(0)
    def json: JsValue = Json.obj("type" -> "Disposition", "action_type" -> "block", "status" -> status, "msg" -> message, "rule_id" -> id)
  }
}

final case class MatchEvent(
    ruleId: Option[Int],
    msg: Option[String],
    phase: Int,
    raw: String
) {
  def simpleJson: JsValue = {
    JsString(s"phase=${phase} rule_id=${ruleId.getOrElse(0)} - ${msg.getOrElse("no msg")}")
  }
  def json: JsValue = {
    val message = msg.getOrElse("--")
    Json.obj(
      "rule_id" -> ruleId,
      "msg" -> message,
      "phase" -> phase,
      "raw" -> raw
    )
  }
}

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
  def json: JsValue = Json.obj(
    "disposition" -> disposition.json,
    "events" -> JsArray(events.filter(_.msg.isDefined).map(_.simpleJson))
  )
}

object RuntimeState {
  // (?i) => case-insensitive
  private val TxExpr: Regex = """(?i)%\{tx\.([a-z0-9_.-]+)\}""".r
}
final case class RuntimeState(mode: EngineMode, disabledIds: Set[Int], events: List[MatchEvent], txMap: TrieMap[String, String], envMap: TrieMap[String, String], uidRef: AtomicReference[String]) {

  def evalTxExpressions(input: String, state: RuntimeState): String = {
    if (input.contains("%{")) {
      val finalInput: String = if (input.contains("%{MATCHED_VAR}") || input.contains("%{MATCHED_VAR_NAME}")) {
        try {
          input
            .replaceAll("%\\{MATCHED_VAR\\}", state.txMap.getOrElse("matched_var", "--"))
            .replaceAll("%\\{MATCHED_VAR_NAME\\}", state.txMap.getOrElse("matched_var_name", "--"))
        } catch {
          case t: Throwable => input
        }
      } else {
        input
      }
      try {
        RuntimeState.TxExpr.replaceAllIn(finalInput, m => {
          val key = m.group(1).toLowerCase
          state.txMap.getOrElse(key, m.matched)
        })
      } catch {
        case t: Throwable => finalInput
      }
    } else {
      input
    }
  }
}

final case class SecLangEngineConfig(debugRules: List[Int])

object SecLangEngineConfig {
  val default = SecLangEngineConfig(debugRules = List.empty)
}

sealed trait CompiledItem

final case class RuleChain(rules: List[SecRule]) extends CompiledItem {
  require(rules.nonEmpty)
  lazy val phase: Int = rules.head.phase
  lazy val id: Option[Int] = rules.last.id.orElse(rules.head.id)
}

final case class MarkerItem(name: String) extends CompiledItem
final case class ActionItem(action: SecAction) extends CompiledItem {
  lazy val phase: Int = action.phase
  lazy val id: Option[Int] = action.id
}

sealed trait EngineMode {
  def isOff: Boolean = this == EngineMode.Off
  def isOn: Boolean = !isOff
  def isDetectionOnly: Boolean = this == EngineMode.DetectionOnly
  def isBlocking: Boolean = this == EngineMode.On
}
object EngineMode {
  case object Off extends EngineMode
  case object On extends EngineMode
  case object DetectionOnly extends EngineMode
  def apply(str: String): EngineMode = str.toLowerCase match {
    case "on" => On
    case "detectiononly" => DetectionOnly
    case _ => Off
  }
}

sealed trait CompiledProgram {
  def mode: Option[EngineMode]
  def hash: String
  def itemsForPhase(phase: Int): Vector[CompiledItem]
  def containsRemovedRuleId(id: Int): Boolean
}

final case class SimpleCompiledProgram(
  itemsByPhase: Map[Int, Vector[CompiledItem]],
  removedRuleIds: Set[Int],
  mode: Option[EngineMode],
  hash: String,
) extends CompiledProgram {
  def itemsForPhase(phase: Int): Vector[CompiledItem] = itemsByPhase.getOrElse(phase, Vector.empty)
  def containsRemovedRuleId(id: Int): Boolean = removedRuleIds.contains(id)
}

final case class ComposedCompiledProgram(programs: List[CompiledProgram]) extends CompiledProgram {
  def mode: Option[EngineMode] = programs.flatMap(_.mode).lastOption
  def hash: String = HashUtilsFast.sha512Hex(programs.map(_.hash).mkString("."))
  def itemsForPhase(phase: Int): Vector[CompiledItem] = programs.flatMap(_.itemsForPhase(phase)).toVector
  def containsRemovedRuleId(id: Int): Boolean = programs.exists(_.containsRemovedRuleId(id))
}

trait SecLangIntegration {
  def logDebug(msg: String): Unit
  def logInfo(msg: String): Unit
  def logAudit(msg: String): Unit
  def logError(msg: String): Unit
  def getEnv: Map[String, String]
  def getCachedProgram(key: String): Option[CompiledProgram]
  def putCachedProgram(key: String, program: CompiledProgram, ttl: FiniteDuration): Unit
  def removeCachedProgram(key: String): Unit
  def audit(ruleId: Int, context: RequestContext, state: RuntimeState, phase: Int, msg: String, logdata: List[String]): Unit
}

class DefaultSecLangIntegration(maxCacheItems: Int = 1000) extends SecLangIntegration {

  private val cache = Scaffeine()
    .expireAfter[String, (CompiledProgram, FiniteDuration)](
      create = (key, value) => value._2,
      update = (key, value, currentDuration) => currentDuration,
      read = (key, value, currentDuration) => currentDuration
    )
    .maximumSize(maxCacheItems)
    .build[String, (CompiledProgram, FiniteDuration)]()

  def logDebug(msg: String): Unit = println(s"[Debug]: $msg")
  def logInfo(msg: String): Unit = println(s"[Info]: $msg")
  def logAudit(msg: String): Unit = println(s"[Audit]: $msg")
  def logError(msg: String): Unit = println(s"[Error]: $msg")
  def getEnv(): Map[String, String] = sys.env

  def getCachedProgram(key: String): Option[CompiledProgram] = cache.getIfPresent(key).map(_._1)
  def putCachedProgram(key: String, program: CompiledProgram, ttl: FiniteDuration): Unit = cache.put(key, (program, ttl))
  def removeCachedProgram(key: String): Unit = cache.invalidate(key)
  def audit(ruleId: Int, context: RequestContext, state: RuntimeState, phase: Int, msg: String, logdata: List[String]): Unit = {
  }
}

object DefaultSecLangIntegration {
  val default = new DefaultSecLangIntegration()
  def apply(maxCacheItems: Int = 1000): DefaultSecLangIntegration = new DefaultSecLangIntegration(maxCacheItems)
}

final case class SecLangPreset(name: String, program: CompiledProgram, files: Map[String, String])

object SecLangPreset {
  def withNoFiles(name: String, rules: String): SecLangPreset = {
    val parsed = AntlrParser.parse(rules).right.get
    val program = Compiler.compile(parsed)
    SecLangPreset(name, program, Map.empty)
  }
  def withFiles(name: String, rules: String, files: Map[String, String]): SecLangPreset = {
    val parsed = AntlrParser.parse(rules).right.get
    val program = Compiler.compile(parsed)
    SecLangPreset(name, program, files)
  }

  def fromSource(name: String, rulesSource: ConfigurationSourceList = ConfigurationSourceList.empty, filesSource: FilesSourceList): SecLangPreset = {
    SecLangPreset(
      name = name,
      program = Compiler.compile(Configuration.fromList(rulesSource)),
      files = filesSource.sources.map(_.getFiles()).reduce((a, b) => a ++ b)
    )
  }
}
