package com.cloud.apim.seclang.antlr

import com.cloud.apim.seclang.antlr.SecLangParser.{ActionContext, Remove_rule_by_id_intContext, Remove_rule_by_msgContext, Remove_rule_by_tagContext, String_remove_rules_valuesContext, Update_target_by_idContext, Update_target_by_msgContext, Update_target_by_tagContext}
import com.cloud.apim.seclang.impl.model.Directive
import com.cloud.apim.seclang.model._

import scala.collection.JavaConverters._

class AstBuilderVisitor extends SecLangParserBaseVisitor[AstNode] {

  import com.cloud.apim.seclang.impl.parser.Implicits._
  
  override def visitConfiguration(ctx: SecLangParser.ConfigurationContext): Configuration = {
    val statements = ctx.stmt().asScala.toList.flatMap { stmtCtx =>
      Option(visitStmt(stmtCtx)).collect { case s: Statement => s }
    }
    Configuration(statements)
  }
  
  override def visitStmt(ctx: SecLangParser.StmtContext): Statement = {
    val commentBlock = Option(ctx.comment_block()).map(visitCommentBlock)
    
    if (ctx.engine_config_rule_directive() != null && ctx.variables() != null && ctx.operator() != null) {
      val variables = visitVariables(ctx.variables())
      val operator = visitOperator(ctx.operator())
      val actions = Option(ctx.actions()).map(visitActions)
      SecRule(commentBlock, variables, operator, actions)
      
    } else if (ctx.rule_script_directive() != null && ctx.file_path() != null) {
      val filePath = ctx.file_path().getText
      val actions = Option(ctx.actions()).map(visitActions)
      SecRuleScript(commentBlock, filePath, actions)
      
    } else if (ctx.remove_rule_by_id() != null) {
      val ids = ctx.remove_rule_by_id_values().asScala.toList.map {
        case idCtx: Remove_rule_by_id_intContext if idCtx.INT() != null => idCtx.INT().getText.toInt
        case _ => 0
      }
      SecRuleRemoveById(commentBlock, ids)
      
    } else if (ctx.string_remove_rules() != null && ctx.string_remove_rules_values() != null) {
      val directive = ctx.string_remove_rules()
      val value = ctx.string_remove_rules_values().getText.replaceAll("\"", "")
      directive match {
        case d: Remove_rule_by_msgContext if d.CONFIG_SEC_RULE_REMOVE_BY_MSG() != null =>
          SecRuleRemoveByMsg(commentBlock, value)
        case d: Remove_rule_by_tagContext if d.CONFIG_SEC_RULE_REMOVE_BY_TAG() != null =>
          SecRuleRemoveByTag(commentBlock, value)
        case _ =>
          SecRuleRemoveByMsg(commentBlock, value)
      }
    } else if (ctx.update_target_rules() != null && ctx.update_variables() != null) {
      val updateRules = ctx.update_target_rules()
      val variables = visitUpdateVariables(ctx.update_variables())
      if (ctx.update_target_rules_values() != null) {
        val value = ctx.update_target_rules_values().getText.replaceAll("\"", "")
        updateRules match {
          case ur: Update_target_by_idContext if ur.CONFIG_SEC_RULE_UPDATE_TARGET_BY_ID() != null =>
            SecRuleUpdateTargetById(commentBlock, value.toInt, variables)
          case ur: Update_target_by_msgContext if ur.CONFIG_SEC_RULE_UPDATE_TARGET_BY_MSG() != null =>
            SecRuleUpdateTargetByMsg(commentBlock, value, variables)
          case ur: Update_target_by_tagContext if ur.CONFIG_SEC_RULE_UPDATE_TARGET_BY_TAG() != null =>
            SecRuleUpdateTargetByTag(commentBlock, value, variables)
          case _ =>
            SecRuleUpdateTargetByMsg(commentBlock, value, variables)
        }
      } else {
        SecRuleUpdateTargetByMsg(commentBlock, "", variables)
      }
    } else if (ctx.update_action_rule() != null && ctx.id() != null && ctx.actions() != null) {
      val id = ctx.id().INT().getText.toInt
      val actions = visitActions(ctx.actions())
      SecRuleUpdateActionById(commentBlock, id, actions)
    } else if (ctx.engine_config_directive() != null && ctx.engine_config_directive().sec_marker_directive() != null) {
      val name = ctx.getText.split("SecMarker").tail.mkString("SecMarker").replaceAll("\"", "").replaceAll("'", "")
      SecMarker(commentBlock, name)
    } else if (ctx.engine_config_directive() != null) {
      val directive = visitEngineConfigDirective(ctx.engine_config_directive())
      EngineConfigDirective(commentBlock, directive)
    } else {
      SecMarker(commentBlock, "unknown")
    }
  }
  
  def visitCommentBlock(ctx: SecLangParser.Comment_blockContext): CommentBlock = {
    val comments = ctx.comment().asScala.toList.map(visitComment)
    CommentBlock(comments)
  }
  
  override def visitComment(ctx: SecLangParser.CommentContext): Comment = {
    val text = if (ctx.COMMENT() != null) ctx.COMMENT().getText else ""
    Comment(text)
  }
  
  def visitEngineConfigDirective(ctx: SecLangParser.Engine_config_directiveContext): ConfigDirective = {
    if (ctx.stmt_audit_log() != null) {
      val value = ctx.values().getText.replaceAll("\"", "")
      ConfigDirective.AuditLog(value)
    } else if (ctx.engine_config_action_directive() != null && ctx.actions() != null) {
      val actions = visitActions(ctx.actions())
      ConfigDirective.DefaultAction(actions)
    } else {
      ConfigDirective.Raw("unknown", "")
    }
  }
  
  override def visitVariables(ctx: SecLangParser.VariablesContext): Variables = {
    val negated = ctx.var_not() != null
    val count = ctx.var_count() != null
    val variables = ctx.var_stmt().asScala.toList.map(visitVarStmt)
    Variables(negated, count, variables)
  }
  
  def visitUpdateVariables(ctx: SecLangParser.Update_variablesContext): UpdateVariables = {
    val negated = ctx.var_not().asScala.nonEmpty
    val count = ctx.var_count() != null
    val variables = ctx.var_stmt().asScala.toList.map(visitVarStmt)
    UpdateVariables(negated, count, variables)
  }
  
  def visitVarStmt(ctx: SecLangParser.Var_stmtContext): Variable = {
    if (ctx.variable_enum() != null) {
      Variable.Simple(ctx.variable_enum().getText)
    } else if (ctx.collection_enum() != null) {
      val collection = ctx.collection_enum().getText
      val key = Option(ctx.collection_value()).map(_.getText)
      Variable.Collection(collection, key)
    } else {
      Variable.Simple("UNKNOWN")
    }
  }
  
  override def visitOperator(ctx: SecLangParser.OperatorContext): Operator = {
    val negated = ctx.operator_not() != null
    val op = visitOperatorInner(ctx)
    if (negated) Operator.Negated(op) else op
  }
  
  def visitOperatorInner(ctx: SecLangParser.OperatorContext): Operator = {
    val opName = ctx.operator_name()
    val opValue = if (ctx.operator_value() != null) {
      ctx.operator_value().getText.replaceAll("\"", "")
    } else ""
    
    if (opName.OPERATOR_RX() != null) Operator.Rx(opValue)
    else if (opName.OPERATOR_CONTAINS() != null) Operator.Contains(opValue)
    else if (opName.OPERATOR_STR_EQ() != null) Operator.Streq(opValue)
    else if (opName.OPERATOR_PM() != null) Operator.Pm(opValue)
    else if (opName.OPERATOR_BEGINS_WITH() != null) Operator.BeginsWith(opValue)
    else if (opName.OPERATOR_ENDS_WITH() != null) Operator.EndsWith(opValue)
    else if (opName.OPERATOR_CONTAINS_WORD() != null) Operator.ContainsWord(opValue)
    else if (opName.OPERATOR_DETECT_SQLI() != null) Operator.DetectSQLi(opValue)
    else if (opName.OPERATOR_DETECT_XSS() != null) Operator.DetectXSS(opValue)
    else if (opName.OPERATOR_EQ() != null) Operator.Eq(opValue)
    else if (opName.OPERATOR_GE() != null) Operator.Ge(opValue)
    else if (opName.OPERATOR_GT() != null) Operator.Gt(opValue)
    else if (opName.OPERATOR_LE() != null) Operator.Le(opValue)
    else if (opName.OPERATOR_LT() != null) Operator.Lt(opValue)
    else if (opName.OPERATOR_IP_MATCH() != null) Operator.IpMatch(opValue)
    else if (opName.OPERATOR_IP_MATCH_FROM_FILE() != null) Operator.IpMatchFromFile(opValue)
    else if (opName.OPERATOR_PM_FROM_FILE() != null) Operator.PmFromFile(opValue)
    else if (opName.OPERATOR_RX_GLOBAL() != null) Operator.RxGlobal(opValue)
    else if (opName.OPERATOR_STR_MATCH() != null) Operator.StrMatch(opValue)
    else if (opName.OPERATOR_UNCONDITIONAL_MATCH() != null) Operator.UnconditionalMatch()
    else if (opName.OPERATOR_WITHIN() != null) Operator.Within(opValue)
    else Operator.Raw(opName.getText, opValue)
  }
  
  override def visitActions(ctx: SecLangParser.ActionsContext): Actions = {
    val actions = ctx.action().asScala.toList.flatMap(visitOneAction)
    Actions(actions)
  }
  
  def visitOneAction(ctx: SecLangParser.ActionContext): Option[Action] = {
    if (ctx.action_only() != null) {
      visitActionOnly(ctx.action_only())
    } else if (ctx.action_with_params() != null) {
      visitActionWithParams(ctx.action_with_params())
    } else {
      None
    }
  }
  
  def visitActionOnly(ctx: SecLangParser.Action_onlyContext): Option[Action] = {
    if (ctx.disruptive_action_only() != null) {
      val disruptive = ctx.disruptive_action_only()
      if (disruptive.ACTION_DENY() != null) Some(Action.Deny)
      else if (disruptive.ACTION_DROP() != null) Some(Action.Drop)
      else if (disruptive.ACTION_PASS() != null) Some(Action.Pass)
      else if (disruptive.ACTION_BLOCK() != null) Some(Action.Block())
      else None
    } else if (ctx.non_disruptive_action_only() != null) {
      val nonDisruptive = ctx.non_disruptive_action_only()
      if (nonDisruptive.ACTION_LOG() != null) Some(Action.Log)
      else if (nonDisruptive.ACTION_NO_LOG() != null) Some(Action.NoLog)
      else if (nonDisruptive.ACTION_NO_AUDIT_LOG() != null) Some(Action.NoAuditLog)
      else if (nonDisruptive.ACTION_AUDIT_LOG() != null) Some(Action.AuditLog())
      else if (nonDisruptive.ACTION_CAPTURE() != null) Some(Action.Capture())
      else if (nonDisruptive.ACTION_MULTI_MATCH() != null) Some(Action.MultiMatch)
      else if (nonDisruptive.ACTION_SANITISE_MATCHED() != null) Some(Action.SanitiseMatched())
      else None
    } else if (ctx.flow_action_only() != null) {
      val flow = ctx.flow_action_only()
      if (flow.ACTION_CHAIN() != null) Some(Action.Chain)
      else None
    } else {
      None
    }
  }
  
  def visitActionWithParams(ctx: SecLangParser.Action_with_paramsContext): Option[Action] = {
    val actionContext = ctx.getParent.asInstanceOf[ActionContext]
    val value = actionContext.action_value().getText.replaceAll("\"", "").replaceAll("'", "")
    if (actionContext.ACTION_TRANSFORMATION() != null) {
      Some(Action.Transform(actionContext.transformation_action_value().getText))
    } else if (ctx.metadata_action_with_params() != null) {
      val meta = ctx.metadata_action_with_params()
      meta match {
        case m: SecLangParser.ACTION_IDContext if m.ACTION_ID() != null => Some(Action.Id(value.toIntOption.getOrElse(0)))
        case m: SecLangParser.ACTION_PHASEContext if m.ACTION_PHASE() != null => Some(Action.Phase(value.toIntOption.getOrElse(2)))
        case m: SecLangParser.ACTION_MSGContext if m.ACTION_MSG() != null => Some(Action.Msg(value))
        case m: SecLangParser.ACTION_TAGContext if m.ACTION_TAG() != null => Some(Action.Tag(value))
        case m: SecLangParser.ACTION_REVContext if m.ACTION_REV() != null => Some(Action.Rev(value))
        case m: SecLangParser.ACTION_VERContext if m.ACTION_VER() != null => Some(Action.Ver(value))
        case m: SecLangParser.ACTION_MATURITYContext if m.ACTION_MATURITY() != null => Some(Action.Maturity(value.toIntOption.getOrElse(0)))
        case m: SecLangParser.ACTION_SEVERITYContext if m.ACTION_SEVERITY() != null => Some(Action.Severity(SeverityValue(value)))
        case _ => None
      }
    } else if (ctx.disruptive_action_with_params() != null) {
      val disruptive = ctx.disruptive_action_with_params()
      if (disruptive.ACTION_REDIRECT() != null) Some(Action.Redirect(value))
      else if (disruptive.ACTION_PROXY() != null) Some(Action.Proxy(value))
      else None
    } else if (ctx.non_disruptive_action_with_params() != null) {
      val nonDisruptive = ctx.non_disruptive_action_with_params()
      if (nonDisruptive.ACTION_INITCOL() != null) Some(Action.InitCol(value))
      else if (nonDisruptive.ACTION_APPEND() != null) Some(Action.Append(value))
      else if (nonDisruptive.ACTION_CTL() != null)  visitCtlAction(actionContext.action_value().action_value_types().ctl_action())
      else if (nonDisruptive.ACTION_EXEC() != null) Some(Action.Exec(value))
      else if (nonDisruptive.ACTION_EXPIRE_VAR() != null) Some(Action.ExpireVar(value))
      else if (nonDisruptive.ACTION_DEPRECATE_VAR() != null) Some(Action.DeprecateVar(value))
      else if (nonDisruptive.ACTION_INITCOL() != null) Some(Action.InitCol(value))
      else if (nonDisruptive.ACTION_LOG_DATA() != null) Some(Action.LogData(value))
      else if (nonDisruptive.ACTION_PREPEND() != null) Some(Action.Prepend(value))
      else if (nonDisruptive.ACTION_SANITISE_ARG() != null) Some(Action.SanitiseArg(value))
      else if (nonDisruptive.ACTION_SANITISE_MATCHED_BYTES() != null) Some(Action.SanitiseMatchedBytes(value))
      else if (nonDisruptive.ACTION_SANITISE_REQUEST_HEADER() != null) Some(Action.SanitiseRequestHeader(value))
      else if (nonDisruptive.ACTION_SANITISE_RESPONSE_HEADER() != null) Some(Action.SanitiseResponseHeader(value))
      else if (nonDisruptive.ACTION_SETUID() != null) Some(Action.SetUid(value))
      else if (nonDisruptive.ACTION_SETRSC() != null) Some(Action.SetRsc(value))
      else if (nonDisruptive.ACTION_SETSID() != null) Some(Action.SetSid(value))
      else if (nonDisruptive.ACTION_SETENV() != null) Some(Action.SetEnv(value))
      else if (nonDisruptive.ACTION_SETVAR() != null) Some(Action.SetVar(value))
      else None
    } else if (ctx.data_action_with_params() != null) {
      val data = ctx.data_action_with_params()
      if (data.ACTION_XMLNS() != null) Some(Action.Xmlns(value))
      else if (data.ACTION_STATUS() != null) Some(Action.Status(value.toIntOption.getOrElse(0)))
      else None
    } else if (ctx.flow_action_with_params() != null) {
      val flow = ctx.flow_action_with_params()
      if (flow.ACTION_SKIP() != null) Some(Action.Skip(value.toIntOption.getOrElse(0)))
      else if (flow.ACTION_SKIP_AFTER() != null) Some(Action.SkipAfter(value))
      else None
    } else {
      None
    }
  }
  
  def visitCtlAction(ctx: SecLangParser.Ctl_actionContext): Option[Action] = {
    val value = ctx.getParent.getText.split("=").tail.mkString("=")
    if (ctx.ACTION_CTL_FORCE_REQ_BODY_VAR() != null) Some(Action.CtlAction.ForceRequestBodyVariable(value))
    else if (ctx.ACTION_CTL_REQUEST_BODY_ACCESS() != null) Some(Action.CtlAction.RequestBodyAccess(value))
    else if (ctx.ACTION_CTL_RULE_ENGINE() != null) Some(Action.CtlAction.RuleEngine(value))
    else if (ctx.ACTION_CTL_RULE_REMOVE_BY_ID() != null) Some(Action.CtlAction.RuleRemoveById(value.toIntOption.getOrElse(0)))
    else if (ctx.ACTION_CTL_RULE_REMOVE_BY_TAG() != null) Some(Action.CtlAction.RuleRemoveByTag(value))
    else if (ctx.ACTION_CTL_RULE_REMOVE_TARGET_BY_ID() != null) {
      val parts = value.split(";")
      val id = parts.headOption.flatMap(_.toIntOption).getOrElse(0)
      val target  = parts.lastOption.getOrElse("--")
      Some(Action.CtlAction.RuleRemoveTargetById(id, target))
    }
    else if (ctx.ACTION_CTL_RULE_REMOVE_TARGET_BY_TAG() != null) {
      val parts = value.split(";")
      val tag = parts.headOption.getOrElse("--")
      val target = parts.lastOption.getOrElse("--")
      Some(Action.CtlAction.RuleRemoveTargetByTag(tag, target))
    }
    else if (ctx.ACTION_CTL_AUDIT_ENGINE() != null) Some(Action.CtlAction.AuditEngine(value))
    else if (ctx.ACTION_CTL_AUDIT_LOG_PARTS() != null) Some(Action.CtlAction.AuditLogParts(value))
    else if (ctx.ACTION_CTL_REQUEST_BODY_PROCESSOR() != null) Some(Action.CtlAction.RequestBodyProcessor(value))
    else None
  }
}

object AntlrParser {
  def parse(in: String): Either[String, Configuration] = {
    import org.antlr.v4.runtime._
    
    try {
      val input = CharStreams.fromString(in)
      val lexer = new SecLangLexer(input)
      val tokens = new CommonTokenStream(lexer)
      val parser = new SecLangParser(tokens)
      
      parser.removeErrorListeners()
      val errorListener = new BaseErrorListener {
        override def syntaxError(
            recognizer: Recognizer[_, _],
            offendingSymbol: Any,
            line: Int,
            charPositionInLine: Int,
            msg: String,
            e: RecognitionException
        ): Unit = {
          throw new RuntimeException(s"Parse error at line $line:$charPositionInLine - $msg")
        }
      }
      parser.addErrorListener(errorListener)
      
      val tree = parser.configuration()
      val visitor = new AstBuilderVisitor()
      val result = visitor.visitConfiguration(tree)
      Right(result)
    } catch {
      case e: Exception => Left(s"Parse error: ${e.getMessage}")
    }
  }
}
