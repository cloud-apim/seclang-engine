// Generated from SecLangParser.g4 by ANTLR 4.13.2
package com.cloud.apim.seclang.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SecLangParser}.
 */
public interface SecLangParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SecLangParser#configuration}.
	 * @param ctx the parse tree
	 */
	void enterConfiguration(SecLangParser.ConfigurationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#configuration}.
	 * @param ctx the parse tree
	 */
	void exitConfiguration(SecLangParser.ConfigurationContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmt(SecLangParser.StmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmt(SecLangParser.StmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#comment_block}.
	 * @param ctx the parse tree
	 */
	void enterComment_block(SecLangParser.Comment_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#comment_block}.
	 * @param ctx the parse tree
	 */
	void exitComment_block(SecLangParser.Comment_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#comment}.
	 * @param ctx the parse tree
	 */
	void enterComment(SecLangParser.CommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#comment}.
	 * @param ctx the parse tree
	 */
	void exitComment(SecLangParser.CommentContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#engine_config_rule_directive}.
	 * @param ctx the parse tree
	 */
	void enterEngine_config_rule_directive(SecLangParser.Engine_config_rule_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#engine_config_rule_directive}.
	 * @param ctx the parse tree
	 */
	void exitEngine_config_rule_directive(SecLangParser.Engine_config_rule_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#engine_config_directive}.
	 * @param ctx the parse tree
	 */
	void enterEngine_config_directive(SecLangParser.Engine_config_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#engine_config_directive}.
	 * @param ctx the parse tree
	 */
	void exitEngine_config_directive(SecLangParser.Engine_config_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#string_engine_config_directive}.
	 * @param ctx the parse tree
	 */
	void enterString_engine_config_directive(SecLangParser.String_engine_config_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#string_engine_config_directive}.
	 * @param ctx the parse tree
	 */
	void exitString_engine_config_directive(SecLangParser.String_engine_config_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#sec_marker_directive}.
	 * @param ctx the parse tree
	 */
	void enterSec_marker_directive(SecLangParser.Sec_marker_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#sec_marker_directive}.
	 * @param ctx the parse tree
	 */
	void exitSec_marker_directive(SecLangParser.Sec_marker_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#engine_config_directive_with_param}.
	 * @param ctx the parse tree
	 */
	void enterEngine_config_directive_with_param(SecLangParser.Engine_config_directive_with_paramContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#engine_config_directive_with_param}.
	 * @param ctx the parse tree
	 */
	void exitEngine_config_directive_with_param(SecLangParser.Engine_config_directive_with_paramContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#rule_script_directive}.
	 * @param ctx the parse tree
	 */
	void enterRule_script_directive(SecLangParser.Rule_script_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#rule_script_directive}.
	 * @param ctx the parse tree
	 */
	void exitRule_script_directive(SecLangParser.Rule_script_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#file_path}.
	 * @param ctx the parse tree
	 */
	void enterFile_path(SecLangParser.File_pathContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#file_path}.
	 * @param ctx the parse tree
	 */
	void exitFile_path(SecLangParser.File_pathContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#remove_rule_by_id}.
	 * @param ctx the parse tree
	 */
	void enterRemove_rule_by_id(SecLangParser.Remove_rule_by_idContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#remove_rule_by_id}.
	 * @param ctx the parse tree
	 */
	void exitRemove_rule_by_id(SecLangParser.Remove_rule_by_idContext ctx);
	/**
	 * Enter a parse tree produced by the {@code remove_rule_by_id_int}
	 * labeled alternative in {@link SecLangParser#remove_rule_by_id_values}.
	 * @param ctx the parse tree
	 */
	void enterRemove_rule_by_id_int(SecLangParser.Remove_rule_by_id_intContext ctx);
	/**
	 * Exit a parse tree produced by the {@code remove_rule_by_id_int}
	 * labeled alternative in {@link SecLangParser#remove_rule_by_id_values}.
	 * @param ctx the parse tree
	 */
	void exitRemove_rule_by_id_int(SecLangParser.Remove_rule_by_id_intContext ctx);
	/**
	 * Enter a parse tree produced by the {@code remove_rule_by_id_int_range}
	 * labeled alternative in {@link SecLangParser#remove_rule_by_id_values}.
	 * @param ctx the parse tree
	 */
	void enterRemove_rule_by_id_int_range(SecLangParser.Remove_rule_by_id_int_rangeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code remove_rule_by_id_int_range}
	 * labeled alternative in {@link SecLangParser#remove_rule_by_id_values}.
	 * @param ctx the parse tree
	 */
	void exitRemove_rule_by_id_int_range(SecLangParser.Remove_rule_by_id_int_rangeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#operator_int_range}.
	 * @param ctx the parse tree
	 */
	void enterOperator_int_range(SecLangParser.Operator_int_rangeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#operator_int_range}.
	 * @param ctx the parse tree
	 */
	void exitOperator_int_range(SecLangParser.Operator_int_rangeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#int_range}.
	 * @param ctx the parse tree
	 */
	void enterInt_range(SecLangParser.Int_rangeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#int_range}.
	 * @param ctx the parse tree
	 */
	void exitInt_range(SecLangParser.Int_rangeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#range_start}.
	 * @param ctx the parse tree
	 */
	void enterRange_start(SecLangParser.Range_startContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#range_start}.
	 * @param ctx the parse tree
	 */
	void exitRange_start(SecLangParser.Range_startContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#range_end}.
	 * @param ctx the parse tree
	 */
	void enterRange_end(SecLangParser.Range_endContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#range_end}.
	 * @param ctx the parse tree
	 */
	void exitRange_end(SecLangParser.Range_endContext ctx);
	/**
	 * Enter a parse tree produced by the {@code remove_rule_by_msg}
	 * labeled alternative in {@link SecLangParser#string_remove_rules}.
	 * @param ctx the parse tree
	 */
	void enterRemove_rule_by_msg(SecLangParser.Remove_rule_by_msgContext ctx);
	/**
	 * Exit a parse tree produced by the {@code remove_rule_by_msg}
	 * labeled alternative in {@link SecLangParser#string_remove_rules}.
	 * @param ctx the parse tree
	 */
	void exitRemove_rule_by_msg(SecLangParser.Remove_rule_by_msgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code remove_rule_by_tag}
	 * labeled alternative in {@link SecLangParser#string_remove_rules}.
	 * @param ctx the parse tree
	 */
	void enterRemove_rule_by_tag(SecLangParser.Remove_rule_by_tagContext ctx);
	/**
	 * Exit a parse tree produced by the {@code remove_rule_by_tag}
	 * labeled alternative in {@link SecLangParser#string_remove_rules}.
	 * @param ctx the parse tree
	 */
	void exitRemove_rule_by_tag(SecLangParser.Remove_rule_by_tagContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#string_remove_rules_values}.
	 * @param ctx the parse tree
	 */
	void enterString_remove_rules_values(SecLangParser.String_remove_rules_valuesContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#string_remove_rules_values}.
	 * @param ctx the parse tree
	 */
	void exitString_remove_rules_values(SecLangParser.String_remove_rules_valuesContext ctx);
	/**
	 * Enter a parse tree produced by the {@code update_target_by_id}
	 * labeled alternative in {@link SecLangParser#update_target_rules}.
	 * @param ctx the parse tree
	 */
	void enterUpdate_target_by_id(SecLangParser.Update_target_by_idContext ctx);
	/**
	 * Exit a parse tree produced by the {@code update_target_by_id}
	 * labeled alternative in {@link SecLangParser#update_target_rules}.
	 * @param ctx the parse tree
	 */
	void exitUpdate_target_by_id(SecLangParser.Update_target_by_idContext ctx);
	/**
	 * Enter a parse tree produced by the {@code update_target_by_msg}
	 * labeled alternative in {@link SecLangParser#update_target_rules}.
	 * @param ctx the parse tree
	 */
	void enterUpdate_target_by_msg(SecLangParser.Update_target_by_msgContext ctx);
	/**
	 * Exit a parse tree produced by the {@code update_target_by_msg}
	 * labeled alternative in {@link SecLangParser#update_target_rules}.
	 * @param ctx the parse tree
	 */
	void exitUpdate_target_by_msg(SecLangParser.Update_target_by_msgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code update_target_by_tag}
	 * labeled alternative in {@link SecLangParser#update_target_rules}.
	 * @param ctx the parse tree
	 */
	void enterUpdate_target_by_tag(SecLangParser.Update_target_by_tagContext ctx);
	/**
	 * Exit a parse tree produced by the {@code update_target_by_tag}
	 * labeled alternative in {@link SecLangParser#update_target_rules}.
	 * @param ctx the parse tree
	 */
	void exitUpdate_target_by_tag(SecLangParser.Update_target_by_tagContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#update_action_rule}.
	 * @param ctx the parse tree
	 */
	void enterUpdate_action_rule(SecLangParser.Update_action_ruleContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#update_action_rule}.
	 * @param ctx the parse tree
	 */
	void exitUpdate_action_rule(SecLangParser.Update_action_ruleContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#id}.
	 * @param ctx the parse tree
	 */
	void enterId(SecLangParser.IdContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#id}.
	 * @param ctx the parse tree
	 */
	void exitId(SecLangParser.IdContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#engine_config_sec_cache_transformations}.
	 * @param ctx the parse tree
	 */
	void enterEngine_config_sec_cache_transformations(SecLangParser.Engine_config_sec_cache_transformationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#engine_config_sec_cache_transformations}.
	 * @param ctx the parse tree
	 */
	void exitEngine_config_sec_cache_transformations(SecLangParser.Engine_config_sec_cache_transformationsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#option_list}.
	 * @param ctx the parse tree
	 */
	void enterOption_list(SecLangParser.Option_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#option_list}.
	 * @param ctx the parse tree
	 */
	void exitOption_list(SecLangParser.Option_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#option}.
	 * @param ctx the parse tree
	 */
	void enterOption(SecLangParser.OptionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#option}.
	 * @param ctx the parse tree
	 */
	void exitOption(SecLangParser.OptionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#option_name}.
	 * @param ctx the parse tree
	 */
	void enterOption_name(SecLangParser.Option_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#option_name}.
	 * @param ctx the parse tree
	 */
	void exitOption_name(SecLangParser.Option_nameContext ctx);
	/**
	 * Enter a parse tree produced by the {@code config_dir_sec_action}
	 * labeled alternative in {@link SecLangParser#engine_config_action_directive}.
	 * @param ctx the parse tree
	 */
	void enterConfig_dir_sec_action(SecLangParser.Config_dir_sec_actionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code config_dir_sec_action}
	 * labeled alternative in {@link SecLangParser#engine_config_action_directive}.
	 * @param ctx the parse tree
	 */
	void exitConfig_dir_sec_action(SecLangParser.Config_dir_sec_actionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code config_dir_sec_default_action}
	 * labeled alternative in {@link SecLangParser#engine_config_action_directive}.
	 * @param ctx the parse tree
	 */
	void enterConfig_dir_sec_default_action(SecLangParser.Config_dir_sec_default_actionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code config_dir_sec_default_action}
	 * labeled alternative in {@link SecLangParser#engine_config_action_directive}.
	 * @param ctx the parse tree
	 */
	void exitConfig_dir_sec_default_action(SecLangParser.Config_dir_sec_default_actionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#stmt_audit_log}.
	 * @param ctx the parse tree
	 */
	void enterStmt_audit_log(SecLangParser.Stmt_audit_logContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#stmt_audit_log}.
	 * @param ctx the parse tree
	 */
	void exitStmt_audit_log(SecLangParser.Stmt_audit_logContext ctx);
	/**
	 * Enter a parse tree produced by the {@code values}
	 * labeled alternative in {@link SecLangParser#remove_rule_by_id_valuesremove_rule_by_id_valuesstring_remove_rulesstring_remove_rulesupdate_target_rulesupdate_target_rulesupdate_target_rulesengine_config_action_directiveengine_config_action_directivemetadata_action_with_paramsmetadata_action_with_paramsmetadata_action_with_paramsmetadata_action_with_paramsmetadata_action_with_paramsmetadata_action_with_paramsmetadata_action_with_paramsmetadata_action_with_params}.
	 * @param ctx the parse tree
	 */
	void enterValues(SecLangParser.ValuesContext ctx);
	/**
	 * Exit a parse tree produced by the {@code values}
	 * labeled alternative in {@link SecLangParser#remove_rule_by_id_valuesremove_rule_by_id_valuesstring_remove_rulesstring_remove_rulesupdate_target_rulesupdate_target_rulesupdate_target_rulesengine_config_action_directiveengine_config_action_directivemetadata_action_with_paramsmetadata_action_with_paramsmetadata_action_with_paramsmetadata_action_with_paramsmetadata_action_with_paramsmetadata_action_with_paramsmetadata_action_with_paramsmetadata_action_with_params}.
	 * @param ctx the parse tree
	 */
	void exitValues(SecLangParser.ValuesContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#action_ctl_target_value}.
	 * @param ctx the parse tree
	 */
	void enterAction_ctl_target_value(SecLangParser.Action_ctl_target_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#action_ctl_target_value}.
	 * @param ctx the parse tree
	 */
	void exitAction_ctl_target_value(SecLangParser.Action_ctl_target_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#update_target_rules_values}.
	 * @param ctx the parse tree
	 */
	void enterUpdate_target_rules_values(SecLangParser.Update_target_rules_valuesContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#update_target_rules_values}.
	 * @param ctx the parse tree
	 */
	void exitUpdate_target_rules_values(SecLangParser.Update_target_rules_valuesContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#operator_not}.
	 * @param ctx the parse tree
	 */
	void enterOperator_not(SecLangParser.Operator_notContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#operator_not}.
	 * @param ctx the parse tree
	 */
	void exitOperator_not(SecLangParser.Operator_notContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#operator}.
	 * @param ctx the parse tree
	 */
	void enterOperator(SecLangParser.OperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#operator}.
	 * @param ctx the parse tree
	 */
	void exitOperator(SecLangParser.OperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#operator_name}.
	 * @param ctx the parse tree
	 */
	void enterOperator_name(SecLangParser.Operator_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#operator_name}.
	 * @param ctx the parse tree
	 */
	void exitOperator_name(SecLangParser.Operator_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#operator_value}.
	 * @param ctx the parse tree
	 */
	void enterOperator_value(SecLangParser.Operator_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#operator_value}.
	 * @param ctx the parse tree
	 */
	void exitOperator_value(SecLangParser.Operator_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#var_not}.
	 * @param ctx the parse tree
	 */
	void enterVar_not(SecLangParser.Var_notContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#var_not}.
	 * @param ctx the parse tree
	 */
	void exitVar_not(SecLangParser.Var_notContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#var_count}.
	 * @param ctx the parse tree
	 */
	void enterVar_count(SecLangParser.Var_countContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#var_count}.
	 * @param ctx the parse tree
	 */
	void exitVar_count(SecLangParser.Var_countContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#variables}.
	 * @param ctx the parse tree
	 */
	void enterVariables(SecLangParser.VariablesContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#variables}.
	 * @param ctx the parse tree
	 */
	void exitVariables(SecLangParser.VariablesContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#update_variables}.
	 * @param ctx the parse tree
	 */
	void enterUpdate_variables(SecLangParser.Update_variablesContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#update_variables}.
	 * @param ctx the parse tree
	 */
	void exitUpdate_variables(SecLangParser.Update_variablesContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#new_target}.
	 * @param ctx the parse tree
	 */
	void enterNew_target(SecLangParser.New_targetContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#new_target}.
	 * @param ctx the parse tree
	 */
	void exitNew_target(SecLangParser.New_targetContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#var_stmt}.
	 * @param ctx the parse tree
	 */
	void enterVar_stmt(SecLangParser.Var_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#var_stmt}.
	 * @param ctx the parse tree
	 */
	void exitVar_stmt(SecLangParser.Var_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#variable_enum}.
	 * @param ctx the parse tree
	 */
	void enterVariable_enum(SecLangParser.Variable_enumContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#variable_enum}.
	 * @param ctx the parse tree
	 */
	void exitVariable_enum(SecLangParser.Variable_enumContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#ctl_variable_enum}.
	 * @param ctx the parse tree
	 */
	void enterCtl_variable_enum(SecLangParser.Ctl_variable_enumContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#ctl_variable_enum}.
	 * @param ctx the parse tree
	 */
	void exitCtl_variable_enum(SecLangParser.Ctl_variable_enumContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#collection_enum}.
	 * @param ctx the parse tree
	 */
	void enterCollection_enum(SecLangParser.Collection_enumContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#collection_enum}.
	 * @param ctx the parse tree
	 */
	void exitCollection_enum(SecLangParser.Collection_enumContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#ctl_collection_enum}.
	 * @param ctx the parse tree
	 */
	void enterCtl_collection_enum(SecLangParser.Ctl_collection_enumContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#ctl_collection_enum}.
	 * @param ctx the parse tree
	 */
	void exitCtl_collection_enum(SecLangParser.Ctl_collection_enumContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#actions}.
	 * @param ctx the parse tree
	 */
	void enterActions(SecLangParser.ActionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#actions}.
	 * @param ctx the parse tree
	 */
	void exitActions(SecLangParser.ActionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#action}.
	 * @param ctx the parse tree
	 */
	void enterAction(SecLangParser.ActionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#action}.
	 * @param ctx the parse tree
	 */
	void exitAction(SecLangParser.ActionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#action_only}.
	 * @param ctx the parse tree
	 */
	void enterAction_only(SecLangParser.Action_onlyContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#action_only}.
	 * @param ctx the parse tree
	 */
	void exitAction_only(SecLangParser.Action_onlyContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#disruptive_action_only}.
	 * @param ctx the parse tree
	 */
	void enterDisruptive_action_only(SecLangParser.Disruptive_action_onlyContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#disruptive_action_only}.
	 * @param ctx the parse tree
	 */
	void exitDisruptive_action_only(SecLangParser.Disruptive_action_onlyContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#non_disruptive_action_only}.
	 * @param ctx the parse tree
	 */
	void enterNon_disruptive_action_only(SecLangParser.Non_disruptive_action_onlyContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#non_disruptive_action_only}.
	 * @param ctx the parse tree
	 */
	void exitNon_disruptive_action_only(SecLangParser.Non_disruptive_action_onlyContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#flow_action_only}.
	 * @param ctx the parse tree
	 */
	void enterFlow_action_only(SecLangParser.Flow_action_onlyContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#flow_action_only}.
	 * @param ctx the parse tree
	 */
	void exitFlow_action_only(SecLangParser.Flow_action_onlyContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#action_with_params}.
	 * @param ctx the parse tree
	 */
	void enterAction_with_params(SecLangParser.Action_with_paramsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#action_with_params}.
	 * @param ctx the parse tree
	 */
	void exitAction_with_params(SecLangParser.Action_with_paramsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ACTION_PHASE}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 */
	void enterACTION_PHASE(SecLangParser.ACTION_PHASEContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ACTION_PHASE}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 */
	void exitACTION_PHASE(SecLangParser.ACTION_PHASEContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ACTION_ID}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 */
	void enterACTION_ID(SecLangParser.ACTION_IDContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ACTION_ID}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 */
	void exitACTION_ID(SecLangParser.ACTION_IDContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ACTION_MATURITY}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 */
	void enterACTION_MATURITY(SecLangParser.ACTION_MATURITYContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ACTION_MATURITY}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 */
	void exitACTION_MATURITY(SecLangParser.ACTION_MATURITYContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ACTION_MSG}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 */
	void enterACTION_MSG(SecLangParser.ACTION_MSGContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ACTION_MSG}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 */
	void exitACTION_MSG(SecLangParser.ACTION_MSGContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ACTION_REV}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 */
	void enterACTION_REV(SecLangParser.ACTION_REVContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ACTION_REV}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 */
	void exitACTION_REV(SecLangParser.ACTION_REVContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ACTION_SEVERITY}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 */
	void enterACTION_SEVERITY(SecLangParser.ACTION_SEVERITYContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ACTION_SEVERITY}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 */
	void exitACTION_SEVERITY(SecLangParser.ACTION_SEVERITYContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ACTION_TAG}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 */
	void enterACTION_TAG(SecLangParser.ACTION_TAGContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ACTION_TAG}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 */
	void exitACTION_TAG(SecLangParser.ACTION_TAGContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ACTION_VER}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 */
	void enterACTION_VER(SecLangParser.ACTION_VERContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ACTION_VER}
	 * labeled alternative in {@link SecLangParser#metadata_action_with_params}.
	 * @param ctx the parse tree
	 */
	void exitACTION_VER(SecLangParser.ACTION_VERContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#disruptive_action_with_params}.
	 * @param ctx the parse tree
	 */
	void enterDisruptive_action_with_params(SecLangParser.Disruptive_action_with_paramsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#disruptive_action_with_params}.
	 * @param ctx the parse tree
	 */
	void exitDisruptive_action_with_params(SecLangParser.Disruptive_action_with_paramsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#non_disruptive_action_with_params}.
	 * @param ctx the parse tree
	 */
	void enterNon_disruptive_action_with_params(SecLangParser.Non_disruptive_action_with_paramsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#non_disruptive_action_with_params}.
	 * @param ctx the parse tree
	 */
	void exitNon_disruptive_action_with_params(SecLangParser.Non_disruptive_action_with_paramsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#data_action_with_params}.
	 * @param ctx the parse tree
	 */
	void enterData_action_with_params(SecLangParser.Data_action_with_paramsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#data_action_with_params}.
	 * @param ctx the parse tree
	 */
	void exitData_action_with_params(SecLangParser.Data_action_with_paramsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#flow_action_with_params}.
	 * @param ctx the parse tree
	 */
	void enterFlow_action_with_params(SecLangParser.Flow_action_with_paramsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#flow_action_with_params}.
	 * @param ctx the parse tree
	 */
	void exitFlow_action_with_params(SecLangParser.Flow_action_with_paramsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#action_value}.
	 * @param ctx the parse tree
	 */
	void enterAction_value(SecLangParser.Action_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#action_value}.
	 * @param ctx the parse tree
	 */
	void exitAction_value(SecLangParser.Action_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#action_value_types}.
	 * @param ctx the parse tree
	 */
	void enterAction_value_types(SecLangParser.Action_value_typesContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#action_value_types}.
	 * @param ctx the parse tree
	 */
	void exitAction_value_types(SecLangParser.Action_value_typesContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#string_literal}.
	 * @param ctx the parse tree
	 */
	void enterString_literal(SecLangParser.String_literalContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#string_literal}.
	 * @param ctx the parse tree
	 */
	void exitString_literal(SecLangParser.String_literalContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#ctl_action}.
	 * @param ctx the parse tree
	 */
	void enterCtl_action(SecLangParser.Ctl_actionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#ctl_action}.
	 * @param ctx the parse tree
	 */
	void exitCtl_action(SecLangParser.Ctl_actionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#transformation_action_value}.
	 * @param ctx the parse tree
	 */
	void enterTransformation_action_value(SecLangParser.Transformation_action_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#transformation_action_value}.
	 * @param ctx the parse tree
	 */
	void exitTransformation_action_value(SecLangParser.Transformation_action_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#collection_value}.
	 * @param ctx the parse tree
	 */
	void enterCollection_value(SecLangParser.Collection_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#collection_value}.
	 * @param ctx the parse tree
	 */
	void exitCollection_value(SecLangParser.Collection_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#ctl_collection_value}.
	 * @param ctx the parse tree
	 */
	void enterCtl_collection_value(SecLangParser.Ctl_collection_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#ctl_collection_value}.
	 * @param ctx the parse tree
	 */
	void exitCtl_collection_value(SecLangParser.Ctl_collection_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#setvar_action}.
	 * @param ctx the parse tree
	 */
	void enterSetvar_action(SecLangParser.Setvar_actionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#setvar_action}.
	 * @param ctx the parse tree
	 */
	void exitSetvar_action(SecLangParser.Setvar_actionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#col_name}.
	 * @param ctx the parse tree
	 */
	void enterCol_name(SecLangParser.Col_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#col_name}.
	 * @param ctx the parse tree
	 */
	void exitCol_name(SecLangParser.Col_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#setvar_stmt}.
	 * @param ctx the parse tree
	 */
	void enterSetvar_stmt(SecLangParser.Setvar_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#setvar_stmt}.
	 * @param ctx the parse tree
	 */
	void exitSetvar_stmt(SecLangParser.Setvar_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(SecLangParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(SecLangParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#var_assignment}.
	 * @param ctx the parse tree
	 */
	void enterVar_assignment(SecLangParser.Var_assignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#var_assignment}.
	 * @param ctx the parse tree
	 */
	void exitVar_assignment(SecLangParser.Var_assignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link SecLangParser#ctl_id}.
	 * @param ctx the parse tree
	 */
	void enterCtl_id(SecLangParser.Ctl_idContext ctx);
	/**
	 * Exit a parse tree produced by {@link SecLangParser#ctl_id}.
	 * @param ctx the parse tree
	 */
	void exitCtl_id(SecLangParser.Ctl_idContext ctx);
}