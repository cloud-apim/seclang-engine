// Generated from SecLangParser.g4 by ANTLR 4.13.2
package com.cloud.apim.seclang.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SecLangParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SecLangParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SecLangParser#configuration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConfiguration(SecLangParser.ConfigurationContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmt(SecLangParser.StmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#comment_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComment_block(SecLangParser.Comment_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComment(SecLangParser.CommentContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#engine_config_rule_directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEngine_config_rule_directive(SecLangParser.Engine_config_rule_directiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#engine_config_directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEngine_config_directive(SecLangParser.Engine_config_directiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#string_engine_config_directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString_engine_config_directive(SecLangParser.String_engine_config_directiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#sec_marker_directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSec_marker_directive(SecLangParser.Sec_marker_directiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#engine_config_directive_with_param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEngine_config_directive_with_param(SecLangParser.Engine_config_directive_with_paramContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#rule_script_directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_script_directive(SecLangParser.Rule_script_directiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#file_path}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile_path(SecLangParser.File_pathContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#remove_rule_by_id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRemove_rule_by_id(SecLangParser.Remove_rule_by_idContext ctx);
	/**
	 * Visit a parse tree produced by the {@code remove_rule_by_id_int}
	 * labeled alternative in {@link SecLangParser#remove_rule_by_id_values}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRemove_rule_by_id_int(SecLangParser.Remove_rule_by_id_intContext ctx);
	/**
	 * Visit a parse tree produced by the {@code remove_rule_by_id_int_range}
	 * labeled alternative in {@link SecLangParser#remove_rule_by_id_values}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRemove_rule_by_id_int_range(SecLangParser.Remove_rule_by_id_int_rangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#operator_int_range}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperator_int_range(SecLangParser.Operator_int_rangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#int_range}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInt_range(SecLangParser.Int_rangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#range_start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRange_start(SecLangParser.Range_startContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#range_end}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRange_end(SecLangParser.Range_endContext ctx);
	/**
	 * Visit a parse tree produced by the {@code remove_rule_by_msg}
	 * labeled alternative in {@link SecLangParser#string_remove_rules}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRemove_rule_by_msg(SecLangParser.Remove_rule_by_msgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code remove_rule_by_tag}
	 * labeled alternative in {@link SecLangParser#string_remove_rules}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRemove_rule_by_tag(SecLangParser.Remove_rule_by_tagContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#string_remove_rules_values}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString_remove_rules_values(SecLangParser.String_remove_rules_valuesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code update_target_by_id}
	 * labeled alternative in {@link SecLangParser#update_target_rules}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdate_target_by_id(SecLangParser.Update_target_by_idContext ctx);
	/**
	 * Visit a parse tree produced by the {@code update_target_by_msg}
	 * labeled alternative in {@link SecLangParser#update_target_rules}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdate_target_by_msg(SecLangParser.Update_target_by_msgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code update_target_by_tag}
	 * labeled alternative in {@link SecLangParser#update_target_rules}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdate_target_by_tag(SecLangParser.Update_target_by_tagContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#update_action_rule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdate_action_rule(SecLangParser.Update_action_ruleContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(SecLangParser.IdContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#engine_config_sec_cache_transformations}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEngine_config_sec_cache_transformations(SecLangParser.Engine_config_sec_cache_transformationsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#option_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOption_list(SecLangParser.Option_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#option}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOption(SecLangParser.OptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#option_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOption_name(SecLangParser.Option_nameContext ctx);
	/**
	 * Visit a parse tree produced by the {@code config_dir_sec_action}
	 * labeled alternative in {@link SecLangParser#engine_config_action_directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConfig_dir_sec_action(SecLangParser.Config_dir_sec_actionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code config_dir_sec_default_action}
	 * labeled alternative in {@link SecLangParser#engine_config_action_directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConfig_dir_sec_default_action(SecLangParser.Config_dir_sec_default_actionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#stmt_audit_log}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmt_audit_log(SecLangParser.Stmt_audit_logContext ctx);
	/**
	 * Visit a parse tree produced by the {@code values}
	 * labeled alternative in {@link SecLangParser#remove_rule_by_id_valuesremove_rule_by_id_valuesstring_remove_rulesstring_remove_rulesupdate_target_rulesupdate_target_rulesupdate_target_rulesengine_config_action_directiveengine_config_action_directivemetadata_action_with_paramsmetadata_action_with_paramsmetadata_action_with_paramsmetadata_action_with_paramsmetadata_action_with_paramsmetadata_action_with_paramsmetadata_action_with_paramsmetadata_action_with_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValues(SecLangParser.ValuesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#action_ctl_target_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_ctl_target_value(SecLangParser.Action_ctl_target_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#update_target_rules_values}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdate_target_rules_values(SecLangParser.Update_target_rules_valuesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#operator_not}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperator_not(SecLangParser.Operator_notContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperator(SecLangParser.OperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#operator_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperator_name(SecLangParser.Operator_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#operator_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperator_value(SecLangParser.Operator_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#var_not}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_not(SecLangParser.Var_notContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#var_count}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_count(SecLangParser.Var_countContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#variables}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariables(SecLangParser.VariablesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#update_variables}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdate_variables(SecLangParser.Update_variablesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#new_target}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNew_target(SecLangParser.New_targetContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#var_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_stmt(SecLangParser.Var_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#variable_enum}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable_enum(SecLangParser.Variable_enumContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#ctl_variable_enum}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCtl_variable_enum(SecLangParser.Ctl_variable_enumContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#collection_enum}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCollection_enum(SecLangParser.Collection_enumContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#ctl_collection_enum}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCtl_collection_enum(SecLangParser.Ctl_collection_enumContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#actions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActions(SecLangParser.ActionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction(SecLangParser.ActionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#action_only}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_only(SecLangParser.Action_onlyContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#disruptive_action_only}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDisruptive_action_only(SecLangParser.Disruptive_action_onlyContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#non_disruptive_action_only}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNon_disruptive_action_only(SecLangParser.Non_disruptive_action_onlyContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#flow_action_only}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlow_action_only(SecLangParser.Flow_action_onlyContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#action_with_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_with_params(SecLangParser.Action_with_paramsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ACTION_PHASE}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitACTION_PHASE(SecLangParser.ACTION_PHASEContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ACTION_ID}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitACTION_ID(SecLangParser.ACTION_IDContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ACTION_MATURITY}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitACTION_MATURITY(SecLangParser.ACTION_MATURITYContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ACTION_MSG}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitACTION_MSG(SecLangParser.ACTION_MSGContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ACTION_REV}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitACTION_REV(SecLangParser.ACTION_REVContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ACTION_SEVERITY}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitACTION_SEVERITY(SecLangParser.ACTION_SEVERITYContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ACTION_TAG}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitACTION_TAG(SecLangParser.ACTION_TAGContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ACTION_VER}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitACTION_VER(SecLangParser.ACTION_VERContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#disruptive_action_with_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDisruptive_action_with_params(SecLangParser.Disruptive_action_with_paramsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#non_disruptive_action_with_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNon_disruptive_action_with_params(SecLangParser.Non_disruptive_action_with_paramsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#data_action_with_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_action_with_params(SecLangParser.Data_action_with_paramsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#flow_action_with_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlow_action_with_params(SecLangParser.Flow_action_with_paramsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#action_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_value(SecLangParser.Action_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#action_value_types}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_value_types(SecLangParser.Action_value_typesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#string_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString_literal(SecLangParser.String_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#ctl_action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCtl_action(SecLangParser.Ctl_actionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#transformation_action_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTransformation_action_value(SecLangParser.Transformation_action_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#collection_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCollection_value(SecLangParser.Collection_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#ctl_collection_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCtl_collection_value(SecLangParser.Ctl_collection_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#setvar_action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetvar_action(SecLangParser.Setvar_actionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#col_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCol_name(SecLangParser.Col_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#setvar_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetvar_stmt(SecLangParser.Setvar_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(SecLangParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#var_assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_assignment(SecLangParser.Var_assignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link SecLangParser#ctl_id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCtl_id(SecLangParser.Ctl_idContext ctx);
}