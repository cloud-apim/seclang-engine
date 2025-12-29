// Generated from SecLangParser.g4 by ANTLR 4.13.2
package com.cloud.apim.seclang.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SecLangParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		QUOTE=1, SINGLE_QUOTE=2, EQUAL=3, COLON=4, EQUALS_PLUS=5, EQUALS_MINUS=6, 
		COMMA=7, PIPE=8, CONFIG_VALUE_PATH=9, NOT=10, WS=11, HASH=12, PLUS=13, 
		MINUS=14, STAR=15, SLASH=16, ASSIGN=17, SEMI=18, NOT_EQUAL=19, LT=20, 
		LE=21, GE=22, GT=23, LPAREN=24, RPAREN=25, ACTION_ACCURACY=26, ACTION_ALLOW=27, 
		ACTION_APPEND=28, ACTION_AUDIT_LOG=29, ACTION_BLOCK=30, ACTION_CAPTURE=31, 
		ACTION_CHAIN=32, ACTION_CTL=33, ACTION_CTL_AUDIT_ENGINE=34, ACTION_CTL_AUDIT_LOG_PARTS=35, 
		ACTION_CTL_REQUEST_BODY_PROCESSOR=36, ACTION_CTL_FORCE_REQ_BODY_VAR=37, 
		ACTION_CTL_REQUEST_BODY_ACCESS=38, ACTION_CTL_RULE_ENGINE=39, ACTION_CTL_RULE_REMOVE_BY_TAG=40, 
		ACTION_CTL_RULE_REMOVE_BY_ID=41, ACTION_CTL_RULE_REMOVE_TARGET_BY_ID=42, 
		ACTION_CTL_RULE_REMOVE_TARGET_BY_TAG=43, ACTION_DENY=44, ACTION_DEPRECATE_VAR=45, 
		ACTION_DROP=46, ACTION_EXEC=47, ACTION_EXPIRE_VAR=48, ACTION_ID=49, ACTION_INITCOL=50, 
		ACTION_LOG_DATA=51, ACTION_LOG=52, ACTION_MATURITY=53, ACTION_MSG=54, 
		ACTION_MULTI_MATCH=55, ACTION_NO_AUDIT_LOG=56, ACTION_NO_LOG=57, ACTION_PASS=58, 
		ACTION_PAUSE=59, ACTION_PHASE=60, ACTION_PREPEND=61, ACTION_PROXY=62, 
		ACTION_REDIRECT=63, ACTION_REV=64, ACTION_SANITISE_ARG=65, ACTION_SANITISE_MATCHED_BYTES=66, 
		ACTION_SANITISE_MATCHED=67, ACTION_SANITISE_REQUEST_HEADER=68, ACTION_SANITISE_RESPONSE_HEADER=69, 
		ACTION_SETENV=70, ACTION_SETRSC=71, ACTION_SETSID=72, ACTION_SETUID=73, 
		ACTION_SETVAR=74, ACTION_SEVERITY=75, ACTION_SEVERITY_VALUE=76, ACTION_SKIP_AFTER=77, 
		ACTION_SKIP=78, ACTION_STATUS=79, ACTION_TAG=80, ACTION_VER=81, ACTION_XMLNS=82, 
		ACTION_TRANSFORMATION=83, TRANSFORMATION_VALUE=84, COLLECTION_NAME_ENUM=85, 
		VARIABLE_NAME_ENUM=86, RUN_TIME_VAR_XML=87, VAR_COUNT=88, OPERATOR_BEGINS_WITH=89, 
		OPERATOR_CONTAINS=90, OPERATOR_CONTAINS_WORD=91, OPERATOR_DETECT_SQLI=92, 
		OPERATOR_DETECT_XSS=93, OPERATOR_ENDS_WITH=94, OPERATOR_EQ=95, OPERATOR_FUZZY_HASH=96, 
		OPERATOR_GE=97, OPERATOR_GEOLOOKUP=98, OPERATOR_GSB_LOOKUP=99, OPERATOR_GT=100, 
		OPERATOR_INSPECT_FILE=101, OPERATOR_IP_MATCH_FROM_FILE=102, OPERATOR_IP_MATCH=103, 
		OPERATOR_LE=104, OPERATOR_LT=105, OPERATOR_PM_FROM_FILE=106, OPERATOR_PM=107, 
		OPERATOR_RBL=108, OPERATOR_RSUB=109, OPERATOR_RX=110, OPERATOR_RX_GLOBAL=111, 
		OPERATOR_STR_EQ=112, OPERATOR_STR_MATCH=113, OPERATOR_UNCONDITIONAL_MATCH=114, 
		OPERATOR_VALIDATE_BYTE_RANGE=115, OPERATOR_VALIDATE_DTD=116, OPERATOR_VALIDATE_HASH=117, 
		OPERATOR_VALIDATE_SCHEMA=118, OPERATOR_VALIDATE_URL_ENCODING=119, OPERATOR_VALIDATE_UTF8_ENCODING=120, 
		OPERATOR_VERIFY_CC=121, OPERATOR_VERIFY_CPF=122, OPERATOR_VERIFY_SSN=123, 
		OPERATOR_VERIFY_SVNR=124, OPERATOR_WITHIN=125, AUDIT_PARTS=126, CONFIG_COMPONENT_SIG=127, 
		CONFIG_SEC_SERVER_SIG=128, CONFIG_SEC_WEB_APP_ID=129, CONFIG_SEC_CACHE_TRANSFORMATIONS=130, 
		CONFIG_SEC_CHROOT_DIR=131, CONFIG_CONN_ENGINE=132, CONFIG_SEC_HASH_ENGINE=133, 
		CONFIG_SEC_HASH_KEY=134, CONFIG_SEC_HASH_PARAM=135, CONFIG_SEC_HASH_METHOD_RX=136, 
		CONFIG_SEC_HASH_METHOD_PM=137, CONFIG_CONTENT_INJECTION=138, CONFIG_SEC_ARGUMENT_SEPARATOR=139, 
		CONFIG_DIR_AUDIT_DIR=140, CONFIG_DIR_AUDIT_DIR_MOD=141, CONFIG_DIR_AUDIT_ENG=142, 
		CONFIG_DIR_AUDIT_FILE_MODE=143, CONFIG_DIR_AUDIT_LOG2=144, CONFIG_DIR_AUDIT_LOG=145, 
		CONFIG_DIR_AUDIT_LOG_FMT=146, CONFIG_DIR_AUDIT_LOG_P=147, CONFIG_DIR_AUDIT_STS=148, 
		CONFIG_DIR_AUDIT_TYPE=149, CONFIG_DIR_DEBUG_LOG=150, CONFIG_DIR_DEBUG_LVL=151, 
		CONFIG_DIR_GEO_DB=152, CONFIG_DIR_GSB_DB=153, CONFIG_SEC_GUARDIAN_LOG=154, 
		CONFIG_SEC_INTERCEPT_ON_ERROR=155, CONFIG_SEC_CONN_R_STATE_LIMIT=156, 
		CONFIG_SEC_CONN_W_STATE_LIMIT=157, CONFIG_SEC_SENSOR_ID=158, CONFIG_SEC_RULE_INHERITANCE=159, 
		CONFIG_SEC_RULE_PERF_TIME=160, CONFIG_SEC_STREAM_IN_BODY_INSPECTION=161, 
		CONFIG_SEC_STREAM_OUT_BODY_INSPECTION=162, CONFIG_DIR_PCRE_MATCH_LIMIT=163, 
		CONFIG_DIR_PCRE_MATCH_LIMIT_RECURSION=164, CONFIG_DIR_ARGS_LIMIT=165, 
		CONFIG_DIR_REQ_BODY_JSON_DEPTH_LIMIT=166, CONFIG_DIR_REQ_BODY=167, CONFIG_DIR_REQ_BODY_IN_MEMORY_LIMIT=168, 
		CONFIG_DIR_REQ_BODY_LIMIT=169, CONFIG_DIR_REQ_BODY_LIMIT_ACTION=170, CONFIG_DIR_REQ_BODY_NO_FILES_LIMIT=171, 
		CONFIG_DIR_RES_BODY=172, CONFIG_DIR_RES_BODY_LIMIT=173, CONFIG_DIR_RES_BODY_LIMIT_ACTION=174, 
		CONFIG_DIR_RULE_ENG=175, CONFIG_DIR_SEC_ACTION=176, CONFIG_DIR_SEC_DEFAULT_ACTION=177, 
		CONFIG_SEC_DISABLE_BACKEND_COMPRESS=178, CONFIG_DIR_SEC_MARKER=179, CONFIG_DIR_UNICODE_MAP_FILE=180, 
		CONFIG_INCLUDE=181, CONFIG_SEC_COLLECTION_TIMEOUT=182, CONFIG_SEC_HTTP_BLKEY=183, 
		CONFIG_SEC_REMOTE_RULES=184, CONFIG_SEC_REMOTE_RULES_FAIL_ACTION=185, 
		CONFIG_SEC_RULE_REMOVE_BY_ID=186, CONFIG_SEC_RULE_REMOVE_BY_MSG=187, CONFIG_SEC_RULE_REMOVE_BY_TAG=188, 
		CONFIG_SEC_RULE_UPDATE_TARGET_BY_TAG=189, CONFIG_SEC_RULE_UPDATE_TARGET_BY_MSG=190, 
		CONFIG_SEC_RULE_UPDATE_TARGET_BY_ID=191, CONFIG_SEC_RULE_UPDATE_ACTION_BY_ID=192, 
		CONFIG_UPLOAD_KEEP_FILES=193, CONFIG_UPLOAD_SAVE_TMP_FILES=194, CONFIG_UPLOAD_DIR=195, 
		CONFIG_UPLOAD_FILE_LIMIT=196, CONFIG_UPLOAD_FILE_MODE=197, CONFIG_VALUE_ABORT=198, 
		CONFIG_VALUE_DETC=199, CONFIG_VALUE_HTTPS=200, CONFIG_VALUE_OFF=201, CONFIG_VALUE_ON=202, 
		CONFIG_VALUE_PARALLEL=203, CONFIG_VALUE_PROCESS_PARTIAL=204, CONFIG_VALUE_REJECT=205, 
		CONFIG_VALUE_RELEVANT_ONLY=206, CONFIG_VALUE_SERIAL=207, CONFIG_VALUE_WARN=208, 
		CONFIG_XML_EXTERNAL_ENTITY=209, CONFIG_DIR_RESPONSE_BODY_MP=210, CONFIG_DIR_RESPONSE_BODY_MP_CLEAR=211, 
		CONFIG_DIR_SEC_COOKIE_FORMAT=212, CONFIG_SEC_COOKIEV0_SEPARATOR=213, CONFIG_DIR_SEC_DATA_DIR=214, 
		CONFIG_DIR_SEC_STATUS_ENGINE=215, CONFIG_DIR_SEC_TMP_DIR=216, CONFIG_DIR_SEC_RULE=217, 
		DIRECTIVE_SECRULESCRIPT=218, OPTION_NAME=219, SINGLE_QUOTE_BUT_SCAPED=220, 
		DOUBLE_SINGLE_QUOTE_BUT_SCAPED=221, COMMA_BUT_SCAPED=222, NATIVE=223, 
		NEWLINE=224, VARIABLE_NAME=225, IDENT=226, INT=227, DIGIT=228, LETTER=229, 
		DICT_ELEMENT_REGEXP=230, FREE_TEXT_QUOTE_MACRO_EXPANSION=231, WS_STRING_MODE=232, 
		STRING=233, MACRO_EXPANSION=234, COLLECTION_NAME_SETVAR=235, DOT=236, 
		COLLECTION_ELEMENT=237, COLLECTION_WITH_MACRO=238, VAR_ASSIGNMENT=239, 
		SPACE_SETVAR_ASSIGNMENT=240, COMMA_SEPARATED_STRING=241, WS_FILE_PATH_MODE=242, 
		XPATH_EXPRESSION=243, XPATH_MODE_POP_CHARS=244, ACTION_CTL_BODY_PROCESSOR_TYPE=245, 
		STRING_LITERAL=246, SPACE_COL=247, SPACE_VAR=248, NEWLINE_VAR=249, COLLECTION_ELEMENT_VALUE=250, 
		SPACE_COL_ELEM=251, NEWLINE_COL_ELEM=252, SKIP_CHARS=253, OPERATOR_UNQUOTED_STRING=254, 
		AT=255, OPERATOR_QUOTED_STRING=256, COMMENT=257, HASH_COMMENT_BLOCK=258, 
		BLOCK_COMMENT_END=259, WS_REMOVE_SPACE=260, INT_RANGE_VALUE=261, MINUS_INT_RANGE=262, 
		WS_INT_RANGE=263, PIPE_DEFAULT=264, COMMA_DEFAULT=265, COLON_DEFAULT=266, 
		EQUAL_DEFAULT=267, NOT_DEFAULT=268, QUOTE_DEFAULT=269, SINGLE_QUOTE_SETVAR=270;
	public static final int
		RULE_configuration = 0, RULE_stmt = 1, RULE_comment_block = 2, RULE_comment = 3, 
		RULE_engine_config_rule_directive = 4, RULE_engine_config_directive = 5, 
		RULE_string_engine_config_directive = 6, RULE_sec_marker_directive = 7, 
		RULE_engine_config_directive_with_param = 8, RULE_rule_script_directive = 9, 
		RULE_file_path = 10, RULE_remove_rule_by_id = 11, RULE_remove_rule_by_id_values = 12, 
		RULE_operator_int_range = 13, RULE_int_range = 14, RULE_range_start = 15, 
		RULE_range_end = 16, RULE_string_remove_rules = 17, RULE_string_remove_rules_values = 18, 
		RULE_update_target_rules = 19, RULE_update_action_rule = 20, RULE_id = 21, 
		RULE_engine_config_sec_cache_transformations = 22, RULE_option_list = 23, 
		RULE_option = 24, RULE_option_name = 25, RULE_engine_config_action_directive = 26, 
		RULE_stmt_audit_log = 27, RULE_values = 28, RULE_action_ctl_target_value = 29, 
		RULE_update_target_rules_values = 30, RULE_operator_not = 31, RULE_operator = 32, 
		RULE_operator_name = 33, RULE_operator_value = 34, RULE_var_not = 35, 
		RULE_var_count = 36, RULE_variables = 37, RULE_update_variables = 38, 
		RULE_new_target = 39, RULE_var_stmt = 40, RULE_variable_enum = 41, RULE_ctl_variable_enum = 42, 
		RULE_collection_enum = 43, RULE_ctl_collection_enum = 44, RULE_actions = 45, 
		RULE_action = 46, RULE_action_only = 47, RULE_disruptive_action_only = 48, 
		RULE_non_disruptive_action_only = 49, RULE_flow_action_only = 50, RULE_action_with_params = 51, 
		RULE_metadata_action_with_params = 52, RULE_disruptive_action_with_params = 53, 
		RULE_non_disruptive_action_with_params = 54, RULE_data_action_with_params = 55, 
		RULE_flow_action_with_params = 56, RULE_action_value = 57, RULE_action_value_types = 58, 
		RULE_string_literal = 59, RULE_ctl_action = 60, RULE_transformation_action_value = 61, 
		RULE_collection_value = 62, RULE_ctl_collection_value = 63, RULE_setvar_action = 64, 
		RULE_col_name = 65, RULE_setvar_stmt = 66, RULE_assignment = 67, RULE_var_assignment = 68, 
		RULE_ctl_id = 69;
	private static String[] makeRuleNames() {
		return new String[] {
			"configuration", "stmt", "comment_block", "comment", "engine_config_rule_directive", 
			"engine_config_directive", "string_engine_config_directive", "sec_marker_directive", 
			"engine_config_directive_with_param", "rule_script_directive", "file_path", 
			"remove_rule_by_id", "remove_rule_by_id_values", "operator_int_range", 
			"int_range", "range_start", "range_end", "string_remove_rules", "string_remove_rules_values", 
			"update_target_rules", "update_action_rule", "id", "engine_config_sec_cache_transformations", 
			"option_list", "option", "option_name", "engine_config_action_directive", 
			"stmt_audit_log", "values", "action_ctl_target_value", "update_target_rules_values", 
			"operator_not", "operator", "operator_name", "operator_value", "var_not", 
			"var_count", "variables", "update_variables", "new_target", "var_stmt", 
			"variable_enum", "ctl_variable_enum", "collection_enum", "ctl_collection_enum", 
			"actions", "action", "action_only", "disruptive_action_only", "non_disruptive_action_only", 
			"flow_action_only", "action_with_params", "metadata_action_with_params", 
			"disruptive_action_with_params", "non_disruptive_action_with_params", 
			"data_action_with_params", "flow_action_with_params", "action_value", 
			"action_value_types", "string_literal", "ctl_action", "transformation_action_value", 
			"collection_value", "ctl_collection_value", "setvar_action", "col_name", 
			"setvar_stmt", "assignment", "var_assignment", "ctl_id"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"'#'", "'+'", null, "'*'", "'/'", "':='", "';'", "'<>'", "'<'", "'<='", 
			"'>='", "'>'", "'('", "')'", "'accuracy'", null, "'append'", "'auditlog'", 
			"'block'", "'capture'", "'chain'", "'ctl'", "'auditEngine'", "'auditLogParts'", 
			"'requestBodyProcessor'", "'forceRequestBodyVariable'", "'requestBodyAccess'", 
			"'ruleEngine'", "'ruleRemoveByTag'", "'ruleRemoveById'", "'ruleRemoveTargetById'", 
			"'ruleRemoveTargetByTag'", "'deny'", "'deprecatevar'", "'drop'", "'exec'", 
			"'expirevar'", "'id'", "'initcol'", "'logdata'", "'log'", "'maturity'", 
			"'msg'", "'multiMatch'", "'noauditlog'", "'nolog'", "'pass'", "'pause'", 
			"'phase'", "'prepend'", "'proxy'", "'redirect'", "'rev'", "'sanitiseArg'", 
			"'sanitiseMatchedBytes'", "'sanitiseMatched'", "'sanitiseRequestHeader'", 
			"'sanitiseResponseHeader'", "'setenv'", "'setrsc'", "'setsid'", "'setuid'", 
			"'setvar'", "'severity'", null, "'skipAfter'", "'skip'", "'status'", 
			"'tag'", "'ver'", "'xmlns'", "'t'", null, null, null, "'XML'", "'&'", 
			"'beginsWith'", "'contains'", "'containsWord'", "'detectSQLi'", "'detectXSS'", 
			"'endsWith'", "'eq'", "'fuzzyHash'", "'ge'", "'geoLookup'", "'gsbLookup'", 
			"'gt'", "'inspectFile'", null, "'ipMatch'", "'le'", "'lt'", null, "'pm'", 
			"'rbl'", "'rsub'", "'rx'", "'rxGlobal'", "'streq'", "'strmatch'", "'unconditionalMatch'", 
			"'validateByteRange'", "'validateDTD'", "'validateHash'", "'validateSchema'", 
			"'validateUrlEncoding'", "'validateUtf8Encoding'", "'verifyCC'", "'verifyCPF'", 
			"'verifySSN'", "'verifySVNR'", "'within'", null, "'SecComponentSignature'", 
			"'SecServerSignature'", "'SecWebAppId'", "'SecCacheTransformations'", 
			"'SecChrootDir'", "'SecConnEngine'", "'SecHashEngine'", "'SecHashKey'", 
			"'SecHashParam'", "'SecHashMethodRx'", "'SecHashMethodPm'", "'SecContentInjection'", 
			"'SecArgumentSeparator'", "'SecAuditLogStorageDir'", "'SecAuditLogDirMode'", 
			"'SecAuditEngine'", "'SecAuditLogFileMode'", "'SecAuditLog2'", "'SecAuditLog'", 
			"'SecAuditLogFormat'", "'SecAuditLogParts'", "'SecAuditLogRelevantStatus'", 
			"'SecAuditLogType'", "'SecDebugLog'", "'SecDebugLogLevel'", "'SecGeoLookupDb'", 
			"'SecGsbLookupDb'", "'SecGuardianLog'", "'SecInterceptOnError'", "'SecConnReadStateLimit'", 
			"'SecConnWriteStateLimit'", "'SecSensorId'", "'SecRuleInheritance'", 
			"'SecRulePerfTime'", "'SecStreamInBodyInspection'", "'SecStreamOutBodyInspection'", 
			"'SecPcreMatchLimit'", "'SecPcreMatchLimitRecursion'", "'SecArgumentsLimit'", 
			"'SecRequestBodyJsonDepthLimit'", "'SecRequestBodyAccess'", "'SecRequestBodyInMemoryLimit'", 
			"'SecRequestBodyLimit'", "'SecRequestBodyLimitAction'", "'SecRequestBodyNoFilesLimit'", 
			"'SecResponseBodyAccess'", "'SecResponseBodyLimit'", "'SecResponseBodyLimitAction'", 
			"'SecRuleEngine'", "'SecAction'", "'SecDefaultAction'", "'SecDisableBackendCompression'", 
			"'SecMarker'", "'SecUnicodeMapFile'", "'Include'", "'SecCollectionTimeout'", 
			"'SecHttpBlKey'", "'SecRemoteRules'", "'SecRemoteRulesFailAction'", null, 
			"'SecRuleRemoveByMsg'", "'SecRuleRemoveByTag'", "'SecRuleUpdateTargetByTag'", 
			"'SecRuleUpdateTargetByMsg'", "'SecRuleUpdateTargetById'", "'SecRuleUpdateActionById'", 
			"'SecUploadKeepFiles'", "'SecTmpSaveUploadedFiles'", "'SecUploadDir'", 
			"'SecUploadFileLimit'", "'SecUploadFileMode'", "'Abort'", "'DetectionOnly'", 
			"'https'", "'Off'", "'On'", null, "'ProcessPartial'", "'Reject'", "'RelevantOnly'", 
			"'Serial'", "'Warn'", "'SecXmlExternalEntity'", "'SecResponseBodyMimeType'", 
			"'SecResponseBodyMimeTypesClear'", "'SecCookieFormat'", "'SecCookieV0Separator'", 
			"'SecDataDir'", "'SecStatusEngine'", "'SecTmpDir'", "'SecRule'", "'SecRuleScript'", 
			null, null, null, null, "'NATIVE'", null, null, null, null, null, null, 
			null, null, null, null, null, null, "'.'", null, "'%{'", null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, "'@'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "QUOTE", "SINGLE_QUOTE", "EQUAL", "COLON", "EQUALS_PLUS", "EQUALS_MINUS", 
			"COMMA", "PIPE", "CONFIG_VALUE_PATH", "NOT", "WS", "HASH", "PLUS", "MINUS", 
			"STAR", "SLASH", "ASSIGN", "SEMI", "NOT_EQUAL", "LT", "LE", "GE", "GT", 
			"LPAREN", "RPAREN", "ACTION_ACCURACY", "ACTION_ALLOW", "ACTION_APPEND", 
			"ACTION_AUDIT_LOG", "ACTION_BLOCK", "ACTION_CAPTURE", "ACTION_CHAIN", 
			"ACTION_CTL", "ACTION_CTL_AUDIT_ENGINE", "ACTION_CTL_AUDIT_LOG_PARTS", 
			"ACTION_CTL_REQUEST_BODY_PROCESSOR", "ACTION_CTL_FORCE_REQ_BODY_VAR", 
			"ACTION_CTL_REQUEST_BODY_ACCESS", "ACTION_CTL_RULE_ENGINE", "ACTION_CTL_RULE_REMOVE_BY_TAG", 
			"ACTION_CTL_RULE_REMOVE_BY_ID", "ACTION_CTL_RULE_REMOVE_TARGET_BY_ID", 
			"ACTION_CTL_RULE_REMOVE_TARGET_BY_TAG", "ACTION_DENY", "ACTION_DEPRECATE_VAR", 
			"ACTION_DROP", "ACTION_EXEC", "ACTION_EXPIRE_VAR", "ACTION_ID", "ACTION_INITCOL", 
			"ACTION_LOG_DATA", "ACTION_LOG", "ACTION_MATURITY", "ACTION_MSG", "ACTION_MULTI_MATCH", 
			"ACTION_NO_AUDIT_LOG", "ACTION_NO_LOG", "ACTION_PASS", "ACTION_PAUSE", 
			"ACTION_PHASE", "ACTION_PREPEND", "ACTION_PROXY", "ACTION_REDIRECT", 
			"ACTION_REV", "ACTION_SANITISE_ARG", "ACTION_SANITISE_MATCHED_BYTES", 
			"ACTION_SANITISE_MATCHED", "ACTION_SANITISE_REQUEST_HEADER", "ACTION_SANITISE_RESPONSE_HEADER", 
			"ACTION_SETENV", "ACTION_SETRSC", "ACTION_SETSID", "ACTION_SETUID", "ACTION_SETVAR", 
			"ACTION_SEVERITY", "ACTION_SEVERITY_VALUE", "ACTION_SKIP_AFTER", "ACTION_SKIP", 
			"ACTION_STATUS", "ACTION_TAG", "ACTION_VER", "ACTION_XMLNS", "ACTION_TRANSFORMATION", 
			"TRANSFORMATION_VALUE", "COLLECTION_NAME_ENUM", "VARIABLE_NAME_ENUM", 
			"RUN_TIME_VAR_XML", "VAR_COUNT", "OPERATOR_BEGINS_WITH", "OPERATOR_CONTAINS", 
			"OPERATOR_CONTAINS_WORD", "OPERATOR_DETECT_SQLI", "OPERATOR_DETECT_XSS", 
			"OPERATOR_ENDS_WITH", "OPERATOR_EQ", "OPERATOR_FUZZY_HASH", "OPERATOR_GE", 
			"OPERATOR_GEOLOOKUP", "OPERATOR_GSB_LOOKUP", "OPERATOR_GT", "OPERATOR_INSPECT_FILE", 
			"OPERATOR_IP_MATCH_FROM_FILE", "OPERATOR_IP_MATCH", "OPERATOR_LE", "OPERATOR_LT", 
			"OPERATOR_PM_FROM_FILE", "OPERATOR_PM", "OPERATOR_RBL", "OPERATOR_RSUB", 
			"OPERATOR_RX", "OPERATOR_RX_GLOBAL", "OPERATOR_STR_EQ", "OPERATOR_STR_MATCH", 
			"OPERATOR_UNCONDITIONAL_MATCH", "OPERATOR_VALIDATE_BYTE_RANGE", "OPERATOR_VALIDATE_DTD", 
			"OPERATOR_VALIDATE_HASH", "OPERATOR_VALIDATE_SCHEMA", "OPERATOR_VALIDATE_URL_ENCODING", 
			"OPERATOR_VALIDATE_UTF8_ENCODING", "OPERATOR_VERIFY_CC", "OPERATOR_VERIFY_CPF", 
			"OPERATOR_VERIFY_SSN", "OPERATOR_VERIFY_SVNR", "OPERATOR_WITHIN", "AUDIT_PARTS", 
			"CONFIG_COMPONENT_SIG", "CONFIG_SEC_SERVER_SIG", "CONFIG_SEC_WEB_APP_ID", 
			"CONFIG_SEC_CACHE_TRANSFORMATIONS", "CONFIG_SEC_CHROOT_DIR", "CONFIG_CONN_ENGINE", 
			"CONFIG_SEC_HASH_ENGINE", "CONFIG_SEC_HASH_KEY", "CONFIG_SEC_HASH_PARAM", 
			"CONFIG_SEC_HASH_METHOD_RX", "CONFIG_SEC_HASH_METHOD_PM", "CONFIG_CONTENT_INJECTION", 
			"CONFIG_SEC_ARGUMENT_SEPARATOR", "CONFIG_DIR_AUDIT_DIR", "CONFIG_DIR_AUDIT_DIR_MOD", 
			"CONFIG_DIR_AUDIT_ENG", "CONFIG_DIR_AUDIT_FILE_MODE", "CONFIG_DIR_AUDIT_LOG2", 
			"CONFIG_DIR_AUDIT_LOG", "CONFIG_DIR_AUDIT_LOG_FMT", "CONFIG_DIR_AUDIT_LOG_P", 
			"CONFIG_DIR_AUDIT_STS", "CONFIG_DIR_AUDIT_TYPE", "CONFIG_DIR_DEBUG_LOG", 
			"CONFIG_DIR_DEBUG_LVL", "CONFIG_DIR_GEO_DB", "CONFIG_DIR_GSB_DB", "CONFIG_SEC_GUARDIAN_LOG", 
			"CONFIG_SEC_INTERCEPT_ON_ERROR", "CONFIG_SEC_CONN_R_STATE_LIMIT", "CONFIG_SEC_CONN_W_STATE_LIMIT", 
			"CONFIG_SEC_SENSOR_ID", "CONFIG_SEC_RULE_INHERITANCE", "CONFIG_SEC_RULE_PERF_TIME", 
			"CONFIG_SEC_STREAM_IN_BODY_INSPECTION", "CONFIG_SEC_STREAM_OUT_BODY_INSPECTION", 
			"CONFIG_DIR_PCRE_MATCH_LIMIT", "CONFIG_DIR_PCRE_MATCH_LIMIT_RECURSION", 
			"CONFIG_DIR_ARGS_LIMIT", "CONFIG_DIR_REQ_BODY_JSON_DEPTH_LIMIT", "CONFIG_DIR_REQ_BODY", 
			"CONFIG_DIR_REQ_BODY_IN_MEMORY_LIMIT", "CONFIG_DIR_REQ_BODY_LIMIT", "CONFIG_DIR_REQ_BODY_LIMIT_ACTION", 
			"CONFIG_DIR_REQ_BODY_NO_FILES_LIMIT", "CONFIG_DIR_RES_BODY", "CONFIG_DIR_RES_BODY_LIMIT", 
			"CONFIG_DIR_RES_BODY_LIMIT_ACTION", "CONFIG_DIR_RULE_ENG", "CONFIG_DIR_SEC_ACTION", 
			"CONFIG_DIR_SEC_DEFAULT_ACTION", "CONFIG_SEC_DISABLE_BACKEND_COMPRESS", 
			"CONFIG_DIR_SEC_MARKER", "CONFIG_DIR_UNICODE_MAP_FILE", "CONFIG_INCLUDE", 
			"CONFIG_SEC_COLLECTION_TIMEOUT", "CONFIG_SEC_HTTP_BLKEY", "CONFIG_SEC_REMOTE_RULES", 
			"CONFIG_SEC_REMOTE_RULES_FAIL_ACTION", "CONFIG_SEC_RULE_REMOVE_BY_ID", 
			"CONFIG_SEC_RULE_REMOVE_BY_MSG", "CONFIG_SEC_RULE_REMOVE_BY_TAG", "CONFIG_SEC_RULE_UPDATE_TARGET_BY_TAG", 
			"CONFIG_SEC_RULE_UPDATE_TARGET_BY_MSG", "CONFIG_SEC_RULE_UPDATE_TARGET_BY_ID", 
			"CONFIG_SEC_RULE_UPDATE_ACTION_BY_ID", "CONFIG_UPLOAD_KEEP_FILES", "CONFIG_UPLOAD_SAVE_TMP_FILES", 
			"CONFIG_UPLOAD_DIR", "CONFIG_UPLOAD_FILE_LIMIT", "CONFIG_UPLOAD_FILE_MODE", 
			"CONFIG_VALUE_ABORT", "CONFIG_VALUE_DETC", "CONFIG_VALUE_HTTPS", "CONFIG_VALUE_OFF", 
			"CONFIG_VALUE_ON", "CONFIG_VALUE_PARALLEL", "CONFIG_VALUE_PROCESS_PARTIAL", 
			"CONFIG_VALUE_REJECT", "CONFIG_VALUE_RELEVANT_ONLY", "CONFIG_VALUE_SERIAL", 
			"CONFIG_VALUE_WARN", "CONFIG_XML_EXTERNAL_ENTITY", "CONFIG_DIR_RESPONSE_BODY_MP", 
			"CONFIG_DIR_RESPONSE_BODY_MP_CLEAR", "CONFIG_DIR_SEC_COOKIE_FORMAT", 
			"CONFIG_SEC_COOKIEV0_SEPARATOR", "CONFIG_DIR_SEC_DATA_DIR", "CONFIG_DIR_SEC_STATUS_ENGINE", 
			"CONFIG_DIR_SEC_TMP_DIR", "CONFIG_DIR_SEC_RULE", "DIRECTIVE_SECRULESCRIPT", 
			"OPTION_NAME", "SINGLE_QUOTE_BUT_SCAPED", "DOUBLE_SINGLE_QUOTE_BUT_SCAPED", 
			"COMMA_BUT_SCAPED", "NATIVE", "NEWLINE", "VARIABLE_NAME", "IDENT", "INT", 
			"DIGIT", "LETTER", "DICT_ELEMENT_REGEXP", "FREE_TEXT_QUOTE_MACRO_EXPANSION", 
			"WS_STRING_MODE", "STRING", "MACRO_EXPANSION", "COLLECTION_NAME_SETVAR", 
			"DOT", "COLLECTION_ELEMENT", "COLLECTION_WITH_MACRO", "VAR_ASSIGNMENT", 
			"SPACE_SETVAR_ASSIGNMENT", "COMMA_SEPARATED_STRING", "WS_FILE_PATH_MODE", 
			"XPATH_EXPRESSION", "XPATH_MODE_POP_CHARS", "ACTION_CTL_BODY_PROCESSOR_TYPE", 
			"STRING_LITERAL", "SPACE_COL", "SPACE_VAR", "NEWLINE_VAR", "COLLECTION_ELEMENT_VALUE", 
			"SPACE_COL_ELEM", "NEWLINE_COL_ELEM", "SKIP_CHARS", "OPERATOR_UNQUOTED_STRING", 
			"AT", "OPERATOR_QUOTED_STRING", "COMMENT", "HASH_COMMENT_BLOCK", "BLOCK_COMMENT_END", 
			"WS_REMOVE_SPACE", "INT_RANGE_VALUE", "MINUS_INT_RANGE", "WS_INT_RANGE", 
			"PIPE_DEFAULT", "COMMA_DEFAULT", "COLON_DEFAULT", "EQUAL_DEFAULT", "NOT_DEFAULT", 
			"QUOTE_DEFAULT", "SINGLE_QUOTE_SETVAR"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "SecLangParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SecLangParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConfigurationContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SecLangParser.EOF, 0); }
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public ConfigurationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_configuration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterConfiguration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitConfiguration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitConfiguration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConfigurationContext configuration() throws RecognitionException {
		ConfigurationContext _localctx = new ConfigurationContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_configuration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==QUOTE || _la==HASH || ((((_la - 127)) & ~0x3f) == 0 && ((1L << (_la - 127)) & -162131785608593409L) != 0) || ((((_la - 191)) & ~0x3f) == 0 && ((1L << (_la - 191)) & 68987650175L) != 0)) {
				{
				{
				setState(140);
				stmt();
				}
				}
				setState(145);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(146);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StmtContext extends ParserRuleContext {
		public Engine_config_rule_directiveContext engine_config_rule_directive() {
			return getRuleContext(Engine_config_rule_directiveContext.class,0);
		}
		public VariablesContext variables() {
			return getRuleContext(VariablesContext.class,0);
		}
		public OperatorContext operator() {
			return getRuleContext(OperatorContext.class,0);
		}
		public Comment_blockContext comment_block() {
			return getRuleContext(Comment_blockContext.class,0);
		}
		public ActionsContext actions() {
			return getRuleContext(ActionsContext.class,0);
		}
		public Rule_script_directiveContext rule_script_directive() {
			return getRuleContext(Rule_script_directiveContext.class,0);
		}
		public File_pathContext file_path() {
			return getRuleContext(File_pathContext.class,0);
		}
		public List<TerminalNode> QUOTE() { return getTokens(SecLangParser.QUOTE); }
		public TerminalNode QUOTE(int i) {
			return getToken(SecLangParser.QUOTE, i);
		}
		public Remove_rule_by_idContext remove_rule_by_id() {
			return getRuleContext(Remove_rule_by_idContext.class,0);
		}
		public List<Remove_rule_by_id_valuesContext> remove_rule_by_id_values() {
			return getRuleContexts(Remove_rule_by_id_valuesContext.class);
		}
		public Remove_rule_by_id_valuesContext remove_rule_by_id_values(int i) {
			return getRuleContext(Remove_rule_by_id_valuesContext.class,i);
		}
		public String_remove_rulesContext string_remove_rules() {
			return getRuleContext(String_remove_rulesContext.class,0);
		}
		public String_remove_rules_valuesContext string_remove_rules_values() {
			return getRuleContext(String_remove_rules_valuesContext.class,0);
		}
		public Update_target_rulesContext update_target_rules() {
			return getRuleContext(Update_target_rulesContext.class,0);
		}
		public Update_target_rules_valuesContext update_target_rules_values() {
			return getRuleContext(Update_target_rules_valuesContext.class,0);
		}
		public Update_variablesContext update_variables() {
			return getRuleContext(Update_variablesContext.class,0);
		}
		public TerminalNode PIPE() { return getToken(SecLangParser.PIPE, 0); }
		public New_targetContext new_target() {
			return getRuleContext(New_targetContext.class,0);
		}
		public Update_action_ruleContext update_action_rule() {
			return getRuleContext(Update_action_ruleContext.class,0);
		}
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public Engine_config_directiveContext engine_config_directive() {
			return getRuleContext(Engine_config_directiveContext.class,0);
		}
		public StmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StmtContext stmt() throws RecognitionException {
		StmtContext _localctx = new StmtContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_stmt);
		int _la;
		try {
			int _alt;
			setState(246);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(149);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HASH) {
					{
					setState(148);
					comment_block();
					}
				}

				setState(151);
				engine_config_rule_directive();
				setState(152);
				variables();
				setState(153);
				operator();
				setState(155);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
				case 1:
					{
					setState(154);
					actions();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(158);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HASH) {
					{
					setState(157);
					comment_block();
					}
				}

				setState(160);
				rule_script_directive();
				setState(161);
				file_path();
				setState(163);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
				case 1:
					{
					setState(162);
					actions();
					}
					break;
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(166);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HASH) {
					{
					setState(165);
					comment_block();
					}
				}

				setState(168);
				rule_script_directive();
				setState(169);
				match(QUOTE);
				setState(170);
				file_path();
				setState(171);
				match(QUOTE);
				setState(173);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
				case 1:
					{
					setState(172);
					actions();
					}
					break;
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(176);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HASH) {
					{
					setState(175);
					comment_block();
					}
				}

				setState(178);
				remove_rule_by_id();
				setState(180); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(179);
						remove_rule_by_id_values();
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(182); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(185);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HASH) {
					{
					setState(184);
					comment_block();
					}
				}

				setState(187);
				string_remove_rules();
				setState(188);
				string_remove_rules_values();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(191);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HASH) {
					{
					setState(190);
					comment_block();
					}
				}

				setState(193);
				string_remove_rules();
				setState(194);
				match(QUOTE);
				setState(195);
				string_remove_rules_values();
				setState(196);
				match(QUOTE);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(199);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HASH) {
					{
					setState(198);
					comment_block();
					}
				}

				setState(201);
				update_target_rules();
				setState(202);
				update_target_rules_values();
				setState(203);
				update_variables();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(206);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HASH) {
					{
					setState(205);
					comment_block();
					}
				}

				setState(208);
				update_target_rules();
				setState(209);
				match(QUOTE);
				setState(210);
				update_target_rules_values();
				setState(211);
				match(QUOTE);
				setState(212);
				update_variables();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(215);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HASH) {
					{
					setState(214);
					comment_block();
					}
				}

				setState(217);
				update_target_rules();
				setState(218);
				update_target_rules_values();
				setState(219);
				update_variables();
				setState(220);
				match(PIPE);
				setState(221);
				new_target();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(224);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HASH) {
					{
					setState(223);
					comment_block();
					}
				}

				setState(226);
				update_target_rules();
				setState(227);
				match(QUOTE);
				setState(228);
				update_target_rules_values();
				setState(229);
				match(QUOTE);
				setState(230);
				update_variables();
				setState(231);
				match(PIPE);
				setState(232);
				new_target();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(235);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HASH) {
					{
					setState(234);
					comment_block();
					}
				}

				setState(237);
				update_action_rule();
				setState(238);
				id();
				setState(239);
				actions();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(242);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HASH) {
					{
					setState(241);
					comment_block();
					}
				}

				setState(244);
				engine_config_directive();
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(245);
				comment_block();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comment_blockContext extends ParserRuleContext {
		public List<CommentContext> comment() {
			return getRuleContexts(CommentContext.class);
		}
		public CommentContext comment(int i) {
			return getRuleContext(CommentContext.class,i);
		}
		public TerminalNode BLOCK_COMMENT_END() { return getToken(SecLangParser.BLOCK_COMMENT_END, 0); }
		public TerminalNode EOF() { return getToken(SecLangParser.EOF, 0); }
		public Comment_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comment_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterComment_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitComment_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitComment_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comment_blockContext comment_block() throws RecognitionException {
		Comment_blockContext _localctx = new Comment_blockContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_comment_block);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(249); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(248);
					comment();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(251); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(254);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				setState(253);
				_la = _input.LA(1);
				if ( !(_la==EOF || _la==BLOCK_COMMENT_END) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CommentContext extends ParserRuleContext {
		public TerminalNode HASH() { return getToken(SecLangParser.HASH, 0); }
		public TerminalNode COMMENT() { return getToken(SecLangParser.COMMENT, 0); }
		public CommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CommentContext comment() throws RecognitionException {
		CommentContext _localctx = new CommentContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_comment);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(256);
			match(HASH);
			setState(258);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMENT) {
				{
				setState(257);
				match(COMMENT);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Engine_config_rule_directiveContext extends ParserRuleContext {
		public TerminalNode CONFIG_DIR_SEC_RULE() { return getToken(SecLangParser.CONFIG_DIR_SEC_RULE, 0); }
		public Engine_config_rule_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_engine_config_rule_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterEngine_config_rule_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitEngine_config_rule_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitEngine_config_rule_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Engine_config_rule_directiveContext engine_config_rule_directive() throws RecognitionException {
		Engine_config_rule_directiveContext _localctx = new Engine_config_rule_directiveContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_engine_config_rule_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(260);
			match(CONFIG_DIR_SEC_RULE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Engine_config_directiveContext extends ParserRuleContext {
		public Stmt_audit_logContext stmt_audit_log() {
			return getRuleContext(Stmt_audit_logContext.class,0);
		}
		public ValuesContext values() {
			return getRuleContext(ValuesContext.class,0);
		}
		public List<TerminalNode> QUOTE() { return getTokens(SecLangParser.QUOTE); }
		public TerminalNode QUOTE(int i) {
			return getToken(SecLangParser.QUOTE, i);
		}
		public Engine_config_action_directiveContext engine_config_action_directive() {
			return getRuleContext(Engine_config_action_directiveContext.class,0);
		}
		public ActionsContext actions() {
			return getRuleContext(ActionsContext.class,0);
		}
		public String_engine_config_directiveContext string_engine_config_directive() {
			return getRuleContext(String_engine_config_directiveContext.class,0);
		}
		public Sec_marker_directiveContext sec_marker_directive() {
			return getRuleContext(Sec_marker_directiveContext.class,0);
		}
		public Engine_config_directive_with_paramContext engine_config_directive_with_param() {
			return getRuleContext(Engine_config_directive_with_paramContext.class,0);
		}
		public Engine_config_sec_cache_transformationsContext engine_config_sec_cache_transformations() {
			return getRuleContext(Engine_config_sec_cache_transformationsContext.class,0);
		}
		public Option_listContext option_list() {
			return getRuleContext(Option_listContext.class,0);
		}
		public Engine_config_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_engine_config_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterEngine_config_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitEngine_config_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitEngine_config_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Engine_config_directiveContext engine_config_directive() throws RecognitionException {
		Engine_config_directiveContext _localctx = new Engine_config_directiveContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_engine_config_directive);
		try {
			setState(290);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(262);
				stmt_audit_log();
				setState(263);
				values();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(265);
				stmt_audit_log();
				setState(266);
				match(QUOTE);
				setState(267);
				values();
				setState(268);
				match(QUOTE);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(270);
				engine_config_action_directive();
				setState(271);
				actions();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(273);
				string_engine_config_directive();
				setState(274);
				match(QUOTE);
				setState(275);
				values();
				setState(276);
				match(QUOTE);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(278);
				sec_marker_directive();
				setState(279);
				match(QUOTE);
				setState(280);
				values();
				setState(281);
				match(QUOTE);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(283);
				engine_config_directive_with_param();
				setState(284);
				values();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(286);
				engine_config_sec_cache_transformations();
				setState(287);
				values();
				setState(288);
				option_list();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class String_engine_config_directiveContext extends ParserRuleContext {
		public TerminalNode CONFIG_COMPONENT_SIG() { return getToken(SecLangParser.CONFIG_COMPONENT_SIG, 0); }
		public TerminalNode CONFIG_SEC_SERVER_SIG() { return getToken(SecLangParser.CONFIG_SEC_SERVER_SIG, 0); }
		public TerminalNode CONFIG_SEC_WEB_APP_ID() { return getToken(SecLangParser.CONFIG_SEC_WEB_APP_ID, 0); }
		public String_engine_config_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string_engine_config_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterString_engine_config_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitString_engine_config_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitString_engine_config_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final String_engine_config_directiveContext string_engine_config_directive() throws RecognitionException {
		String_engine_config_directiveContext _localctx = new String_engine_config_directiveContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_string_engine_config_directive);
		try {
			setState(296);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case QUOTE:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case CONFIG_COMPONENT_SIG:
				enterOuterAlt(_localctx, 2);
				{
				setState(293);
				match(CONFIG_COMPONENT_SIG);
				}
				break;
			case CONFIG_SEC_SERVER_SIG:
				enterOuterAlt(_localctx, 3);
				{
				setState(294);
				match(CONFIG_SEC_SERVER_SIG);
				}
				break;
			case CONFIG_SEC_WEB_APP_ID:
				enterOuterAlt(_localctx, 4);
				{
				setState(295);
				match(CONFIG_SEC_WEB_APP_ID);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Sec_marker_directiveContext extends ParserRuleContext {
		public TerminalNode CONFIG_DIR_SEC_MARKER() { return getToken(SecLangParser.CONFIG_DIR_SEC_MARKER, 0); }
		public Sec_marker_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sec_marker_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterSec_marker_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitSec_marker_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitSec_marker_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Sec_marker_directiveContext sec_marker_directive() throws RecognitionException {
		Sec_marker_directiveContext _localctx = new Sec_marker_directiveContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_sec_marker_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(298);
			match(CONFIG_DIR_SEC_MARKER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Engine_config_directive_with_paramContext extends ParserRuleContext {
		public TerminalNode CONFIG_CONN_ENGINE() { return getToken(SecLangParser.CONFIG_CONN_ENGINE, 0); }
		public TerminalNode CONFIG_CONTENT_INJECTION() { return getToken(SecLangParser.CONFIG_CONTENT_INJECTION, 0); }
		public TerminalNode CONFIG_DIR_ARGS_LIMIT() { return getToken(SecLangParser.CONFIG_DIR_ARGS_LIMIT, 0); }
		public TerminalNode CONFIG_DIR_DEBUG_LOG() { return getToken(SecLangParser.CONFIG_DIR_DEBUG_LOG, 0); }
		public TerminalNode CONFIG_DIR_DEBUG_LVL() { return getToken(SecLangParser.CONFIG_DIR_DEBUG_LVL, 0); }
		public TerminalNode CONFIG_DIR_GEO_DB() { return getToken(SecLangParser.CONFIG_DIR_GEO_DB, 0); }
		public TerminalNode CONFIG_DIR_GSB_DB() { return getToken(SecLangParser.CONFIG_DIR_GSB_DB, 0); }
		public TerminalNode CONFIG_DIR_PCRE_MATCH_LIMIT() { return getToken(SecLangParser.CONFIG_DIR_PCRE_MATCH_LIMIT, 0); }
		public TerminalNode CONFIG_DIR_PCRE_MATCH_LIMIT_RECURSION() { return getToken(SecLangParser.CONFIG_DIR_PCRE_MATCH_LIMIT_RECURSION, 0); }
		public TerminalNode CONFIG_DIR_REQ_BODY() { return getToken(SecLangParser.CONFIG_DIR_REQ_BODY, 0); }
		public TerminalNode CONFIG_DIR_REQ_BODY_JSON_DEPTH_LIMIT() { return getToken(SecLangParser.CONFIG_DIR_REQ_BODY_JSON_DEPTH_LIMIT, 0); }
		public TerminalNode CONFIG_DIR_REQ_BODY_LIMIT() { return getToken(SecLangParser.CONFIG_DIR_REQ_BODY_LIMIT, 0); }
		public TerminalNode CONFIG_DIR_REQ_BODY_LIMIT_ACTION() { return getToken(SecLangParser.CONFIG_DIR_REQ_BODY_LIMIT_ACTION, 0); }
		public TerminalNode CONFIG_DIR_REQ_BODY_NO_FILES_LIMIT() { return getToken(SecLangParser.CONFIG_DIR_REQ_BODY_NO_FILES_LIMIT, 0); }
		public TerminalNode CONFIG_DIR_RESPONSE_BODY_MP() { return getToken(SecLangParser.CONFIG_DIR_RESPONSE_BODY_MP, 0); }
		public TerminalNode CONFIG_DIR_RESPONSE_BODY_MP_CLEAR() { return getToken(SecLangParser.CONFIG_DIR_RESPONSE_BODY_MP_CLEAR, 0); }
		public TerminalNode CONFIG_DIR_RES_BODY() { return getToken(SecLangParser.CONFIG_DIR_RES_BODY, 0); }
		public TerminalNode CONFIG_DIR_RES_BODY_LIMIT() { return getToken(SecLangParser.CONFIG_DIR_RES_BODY_LIMIT, 0); }
		public TerminalNode CONFIG_DIR_RES_BODY_LIMIT_ACTION() { return getToken(SecLangParser.CONFIG_DIR_RES_BODY_LIMIT_ACTION, 0); }
		public TerminalNode CONFIG_DIR_RULE_ENG() { return getToken(SecLangParser.CONFIG_DIR_RULE_ENG, 0); }
		public TerminalNode CONFIG_DIR_SEC_COOKIE_FORMAT() { return getToken(SecLangParser.CONFIG_DIR_SEC_COOKIE_FORMAT, 0); }
		public TerminalNode CONFIG_DIR_SEC_DATA_DIR() { return getToken(SecLangParser.CONFIG_DIR_SEC_DATA_DIR, 0); }
		public TerminalNode CONFIG_DIR_SEC_STATUS_ENGINE() { return getToken(SecLangParser.CONFIG_DIR_SEC_STATUS_ENGINE, 0); }
		public TerminalNode CONFIG_DIR_SEC_TMP_DIR() { return getToken(SecLangParser.CONFIG_DIR_SEC_TMP_DIR, 0); }
		public TerminalNode CONFIG_DIR_UNICODE_MAP_FILE() { return getToken(SecLangParser.CONFIG_DIR_UNICODE_MAP_FILE, 0); }
		public TerminalNode CONFIG_SEC_ARGUMENT_SEPARATOR() { return getToken(SecLangParser.CONFIG_SEC_ARGUMENT_SEPARATOR, 0); }
		public TerminalNode CONFIG_SEC_CHROOT_DIR() { return getToken(SecLangParser.CONFIG_SEC_CHROOT_DIR, 0); }
		public TerminalNode CONFIG_SEC_COLLECTION_TIMEOUT() { return getToken(SecLangParser.CONFIG_SEC_COLLECTION_TIMEOUT, 0); }
		public TerminalNode CONFIG_SEC_CONN_R_STATE_LIMIT() { return getToken(SecLangParser.CONFIG_SEC_CONN_R_STATE_LIMIT, 0); }
		public TerminalNode CONFIG_SEC_CONN_W_STATE_LIMIT() { return getToken(SecLangParser.CONFIG_SEC_CONN_W_STATE_LIMIT, 0); }
		public TerminalNode CONFIG_SEC_COOKIEV0_SEPARATOR() { return getToken(SecLangParser.CONFIG_SEC_COOKIEV0_SEPARATOR, 0); }
		public TerminalNode CONFIG_SEC_DISABLE_BACKEND_COMPRESS() { return getToken(SecLangParser.CONFIG_SEC_DISABLE_BACKEND_COMPRESS, 0); }
		public TerminalNode CONFIG_SEC_GUARDIAN_LOG() { return getToken(SecLangParser.CONFIG_SEC_GUARDIAN_LOG, 0); }
		public TerminalNode CONFIG_SEC_HASH_ENGINE() { return getToken(SecLangParser.CONFIG_SEC_HASH_ENGINE, 0); }
		public TerminalNode CONFIG_SEC_HASH_KEY() { return getToken(SecLangParser.CONFIG_SEC_HASH_KEY, 0); }
		public TerminalNode CONFIG_SEC_HASH_METHOD_PM() { return getToken(SecLangParser.CONFIG_SEC_HASH_METHOD_PM, 0); }
		public TerminalNode CONFIG_SEC_HASH_METHOD_RX() { return getToken(SecLangParser.CONFIG_SEC_HASH_METHOD_RX, 0); }
		public TerminalNode CONFIG_SEC_HASH_PARAM() { return getToken(SecLangParser.CONFIG_SEC_HASH_PARAM, 0); }
		public TerminalNode CONFIG_SEC_HTTP_BLKEY() { return getToken(SecLangParser.CONFIG_SEC_HTTP_BLKEY, 0); }
		public TerminalNode CONFIG_SEC_INTERCEPT_ON_ERROR() { return getToken(SecLangParser.CONFIG_SEC_INTERCEPT_ON_ERROR, 0); }
		public TerminalNode CONFIG_SEC_REMOTE_RULES_FAIL_ACTION() { return getToken(SecLangParser.CONFIG_SEC_REMOTE_RULES_FAIL_ACTION, 0); }
		public TerminalNode CONFIG_SEC_RULE_INHERITANCE() { return getToken(SecLangParser.CONFIG_SEC_RULE_INHERITANCE, 0); }
		public TerminalNode CONFIG_SEC_RULE_PERF_TIME() { return getToken(SecLangParser.CONFIG_SEC_RULE_PERF_TIME, 0); }
		public TerminalNode CONFIG_SEC_SENSOR_ID() { return getToken(SecLangParser.CONFIG_SEC_SENSOR_ID, 0); }
		public TerminalNode CONFIG_SEC_STREAM_IN_BODY_INSPECTION() { return getToken(SecLangParser.CONFIG_SEC_STREAM_IN_BODY_INSPECTION, 0); }
		public TerminalNode CONFIG_SEC_STREAM_OUT_BODY_INSPECTION() { return getToken(SecLangParser.CONFIG_SEC_STREAM_OUT_BODY_INSPECTION, 0); }
		public TerminalNode CONFIG_XML_EXTERNAL_ENTITY() { return getToken(SecLangParser.CONFIG_XML_EXTERNAL_ENTITY, 0); }
		public Engine_config_directive_with_paramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_engine_config_directive_with_param; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterEngine_config_directive_with_param(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitEngine_config_directive_with_param(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitEngine_config_directive_with_param(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Engine_config_directive_with_paramContext engine_config_directive_with_param() throws RecognitionException {
		Engine_config_directive_with_paramContext _localctx = new Engine_config_directive_with_paramContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_engine_config_directive_with_param);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(300);
			_la = _input.LA(1);
			if ( !(((((_la - 131)) & ~0x3f) == 0 && ((1L << (_la - 131)) & 25508532324925951L) != 0) || ((((_la - 209)) & ~0x3f) == 0 && ((1L << (_la - 209)) & 255L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Rule_script_directiveContext extends ParserRuleContext {
		public TerminalNode DIRECTIVE_SECRULESCRIPT() { return getToken(SecLangParser.DIRECTIVE_SECRULESCRIPT, 0); }
		public Rule_script_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_script_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterRule_script_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitRule_script_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitRule_script_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_script_directiveContext rule_script_directive() throws RecognitionException {
		Rule_script_directiveContext _localctx = new Rule_script_directiveContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_rule_script_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(302);
			match(DIRECTIVE_SECRULESCRIPT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class File_pathContext extends ParserRuleContext {
		public TerminalNode CONFIG_VALUE_PATH() { return getToken(SecLangParser.CONFIG_VALUE_PATH, 0); }
		public File_pathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file_path; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterFile_path(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitFile_path(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitFile_path(this);
			else return visitor.visitChildren(this);
		}
	}

	public final File_pathContext file_path() throws RecognitionException {
		File_pathContext _localctx = new File_pathContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_file_path);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(304);
			match(CONFIG_VALUE_PATH);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Remove_rule_by_idContext extends ParserRuleContext {
		public TerminalNode CONFIG_SEC_RULE_REMOVE_BY_ID() { return getToken(SecLangParser.CONFIG_SEC_RULE_REMOVE_BY_ID, 0); }
		public Remove_rule_by_idContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_remove_rule_by_id; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterRemove_rule_by_id(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitRemove_rule_by_id(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitRemove_rule_by_id(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Remove_rule_by_idContext remove_rule_by_id() throws RecognitionException {
		Remove_rule_by_idContext _localctx = new Remove_rule_by_idContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_remove_rule_by_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(306);
			match(CONFIG_SEC_RULE_REMOVE_BY_ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Remove_rule_by_id_valuesContext extends ParserRuleContext {
		public Remove_rule_by_id_valuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_remove_rule_by_id_values; }
	 
		public Remove_rule_by_id_valuesContext() { }
		public void copyFrom(Remove_rule_by_id_valuesContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Remove_rule_by_id_intContext extends Remove_rule_by_id_valuesContext {
		public TerminalNode INT() { return getToken(SecLangParser.INT, 0); }
		public Remove_rule_by_id_intContext(Remove_rule_by_id_valuesContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterRemove_rule_by_id_int(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitRemove_rule_by_id_int(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitRemove_rule_by_id_int(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Remove_rule_by_id_int_rangeContext extends Remove_rule_by_id_valuesContext {
		public Int_rangeContext int_range() {
			return getRuleContext(Int_rangeContext.class,0);
		}
		public Remove_rule_by_id_int_rangeContext(Remove_rule_by_id_valuesContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterRemove_rule_by_id_int_range(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitRemove_rule_by_id_int_range(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitRemove_rule_by_id_int_range(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Remove_rule_by_id_valuesContext remove_rule_by_id_values() throws RecognitionException {
		Remove_rule_by_id_valuesContext _localctx = new Remove_rule_by_id_valuesContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_remove_rule_by_id_values);
		try {
			setState(310);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				_localctx = new Remove_rule_by_id_intContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(308);
				match(INT);
				}
				break;
			case 2:
				_localctx = new Remove_rule_by_id_int_rangeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(309);
				int_range();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Operator_int_rangeContext extends ParserRuleContext {
		public List<TerminalNode> INT_RANGE_VALUE() { return getTokens(SecLangParser.INT_RANGE_VALUE); }
		public TerminalNode INT_RANGE_VALUE(int i) {
			return getToken(SecLangParser.INT_RANGE_VALUE, i);
		}
		public TerminalNode MINUS_INT_RANGE() { return getToken(SecLangParser.MINUS_INT_RANGE, 0); }
		public Operator_int_rangeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operator_int_range; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterOperator_int_range(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitOperator_int_range(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitOperator_int_range(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Operator_int_rangeContext operator_int_range() throws RecognitionException {
		Operator_int_rangeContext _localctx = new Operator_int_rangeContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_operator_int_range);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(312);
			match(INT_RANGE_VALUE);
			setState(313);
			match(MINUS_INT_RANGE);
			setState(314);
			match(INT_RANGE_VALUE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Int_rangeContext extends ParserRuleContext {
		public Range_startContext range_start() {
			return getRuleContext(Range_startContext.class,0);
		}
		public TerminalNode MINUS() { return getToken(SecLangParser.MINUS, 0); }
		public Range_endContext range_end() {
			return getRuleContext(Range_endContext.class,0);
		}
		public Int_rangeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_int_range; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterInt_range(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitInt_range(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitInt_range(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Int_rangeContext int_range() throws RecognitionException {
		Int_rangeContext _localctx = new Int_rangeContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_int_range);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(316);
			range_start();
			setState(317);
			match(MINUS);
			setState(318);
			range_end();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Range_startContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(SecLangParser.INT, 0); }
		public Range_startContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_range_start; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterRange_start(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitRange_start(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitRange_start(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Range_startContext range_start() throws RecognitionException {
		Range_startContext _localctx = new Range_startContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_range_start);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(320);
			match(INT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Range_endContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(SecLangParser.INT, 0); }
		public Range_endContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_range_end; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterRange_end(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitRange_end(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitRange_end(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Range_endContext range_end() throws RecognitionException {
		Range_endContext _localctx = new Range_endContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_range_end);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(322);
			match(INT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class String_remove_rulesContext extends ParserRuleContext {
		public String_remove_rulesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string_remove_rules; }
	 
		public String_remove_rulesContext() { }
		public void copyFrom(String_remove_rulesContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Remove_rule_by_msgContext extends String_remove_rulesContext {
		public TerminalNode CONFIG_SEC_RULE_REMOVE_BY_MSG() { return getToken(SecLangParser.CONFIG_SEC_RULE_REMOVE_BY_MSG, 0); }
		public Remove_rule_by_msgContext(String_remove_rulesContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterRemove_rule_by_msg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitRemove_rule_by_msg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitRemove_rule_by_msg(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Remove_rule_by_tagContext extends String_remove_rulesContext {
		public TerminalNode CONFIG_SEC_RULE_REMOVE_BY_TAG() { return getToken(SecLangParser.CONFIG_SEC_RULE_REMOVE_BY_TAG, 0); }
		public Remove_rule_by_tagContext(String_remove_rulesContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterRemove_rule_by_tag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitRemove_rule_by_tag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitRemove_rule_by_tag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final String_remove_rulesContext string_remove_rules() throws RecognitionException {
		String_remove_rulesContext _localctx = new String_remove_rulesContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_string_remove_rules);
		try {
			setState(326);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CONFIG_SEC_RULE_REMOVE_BY_MSG:
				_localctx = new Remove_rule_by_msgContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(324);
				match(CONFIG_SEC_RULE_REMOVE_BY_MSG);
				}
				break;
			case CONFIG_SEC_RULE_REMOVE_BY_TAG:
				_localctx = new Remove_rule_by_tagContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(325);
				match(CONFIG_SEC_RULE_REMOVE_BY_TAG);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class String_remove_rules_valuesContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(SecLangParser.STRING, 0); }
		public TerminalNode VARIABLE_NAME() { return getToken(SecLangParser.VARIABLE_NAME, 0); }
		public TerminalNode COMMA_SEPARATED_STRING() { return getToken(SecLangParser.COMMA_SEPARATED_STRING, 0); }
		public String_remove_rules_valuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string_remove_rules_values; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterString_remove_rules_values(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitString_remove_rules_values(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitString_remove_rules_values(this);
			else return visitor.visitChildren(this);
		}
	}

	public final String_remove_rules_valuesContext string_remove_rules_values() throws RecognitionException {
		String_remove_rules_valuesContext _localctx = new String_remove_rules_valuesContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_string_remove_rules_values);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(328);
			_la = _input.LA(1);
			if ( !(((((_la - 225)) & ~0x3f) == 0 && ((1L << (_la - 225)) & 65793L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Update_target_rulesContext extends ParserRuleContext {
		public Update_target_rulesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_update_target_rules; }
	 
		public Update_target_rulesContext() { }
		public void copyFrom(Update_target_rulesContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Update_target_by_tagContext extends Update_target_rulesContext {
		public TerminalNode CONFIG_SEC_RULE_UPDATE_TARGET_BY_TAG() { return getToken(SecLangParser.CONFIG_SEC_RULE_UPDATE_TARGET_BY_TAG, 0); }
		public Update_target_by_tagContext(Update_target_rulesContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterUpdate_target_by_tag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitUpdate_target_by_tag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitUpdate_target_by_tag(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Update_target_by_idContext extends Update_target_rulesContext {
		public TerminalNode CONFIG_SEC_RULE_UPDATE_TARGET_BY_ID() { return getToken(SecLangParser.CONFIG_SEC_RULE_UPDATE_TARGET_BY_ID, 0); }
		public Update_target_by_idContext(Update_target_rulesContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterUpdate_target_by_id(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitUpdate_target_by_id(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitUpdate_target_by_id(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Update_target_by_msgContext extends Update_target_rulesContext {
		public TerminalNode CONFIG_SEC_RULE_UPDATE_TARGET_BY_MSG() { return getToken(SecLangParser.CONFIG_SEC_RULE_UPDATE_TARGET_BY_MSG, 0); }
		public Update_target_by_msgContext(Update_target_rulesContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterUpdate_target_by_msg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitUpdate_target_by_msg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitUpdate_target_by_msg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Update_target_rulesContext update_target_rules() throws RecognitionException {
		Update_target_rulesContext _localctx = new Update_target_rulesContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_update_target_rules);
		try {
			setState(333);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CONFIG_SEC_RULE_UPDATE_TARGET_BY_ID:
				_localctx = new Update_target_by_idContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(330);
				match(CONFIG_SEC_RULE_UPDATE_TARGET_BY_ID);
				}
				break;
			case CONFIG_SEC_RULE_UPDATE_TARGET_BY_MSG:
				_localctx = new Update_target_by_msgContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(331);
				match(CONFIG_SEC_RULE_UPDATE_TARGET_BY_MSG);
				}
				break;
			case CONFIG_SEC_RULE_UPDATE_TARGET_BY_TAG:
				_localctx = new Update_target_by_tagContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(332);
				match(CONFIG_SEC_RULE_UPDATE_TARGET_BY_TAG);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Update_action_ruleContext extends ParserRuleContext {
		public TerminalNode CONFIG_SEC_RULE_UPDATE_ACTION_BY_ID() { return getToken(SecLangParser.CONFIG_SEC_RULE_UPDATE_ACTION_BY_ID, 0); }
		public Update_action_ruleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_update_action_rule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterUpdate_action_rule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitUpdate_action_rule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitUpdate_action_rule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Update_action_ruleContext update_action_rule() throws RecognitionException {
		Update_action_ruleContext _localctx = new Update_action_ruleContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_update_action_rule);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(335);
			match(CONFIG_SEC_RULE_UPDATE_ACTION_BY_ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IdContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(SecLangParser.INT, 0); }
		public IdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_id; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitId(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitId(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdContext id() throws RecognitionException {
		IdContext _localctx = new IdContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(337);
			match(INT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Engine_config_sec_cache_transformationsContext extends ParserRuleContext {
		public TerminalNode CONFIG_SEC_CACHE_TRANSFORMATIONS() { return getToken(SecLangParser.CONFIG_SEC_CACHE_TRANSFORMATIONS, 0); }
		public Engine_config_sec_cache_transformationsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_engine_config_sec_cache_transformations; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterEngine_config_sec_cache_transformations(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitEngine_config_sec_cache_transformations(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitEngine_config_sec_cache_transformations(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Engine_config_sec_cache_transformationsContext engine_config_sec_cache_transformations() throws RecognitionException {
		Engine_config_sec_cache_transformationsContext _localctx = new Engine_config_sec_cache_transformationsContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_engine_config_sec_cache_transformations);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(339);
			match(CONFIG_SEC_CACHE_TRANSFORMATIONS);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Option_listContext extends ParserRuleContext {
		public List<TerminalNode> QUOTE() { return getTokens(SecLangParser.QUOTE); }
		public TerminalNode QUOTE(int i) {
			return getToken(SecLangParser.QUOTE, i);
		}
		public List<OptionContext> option() {
			return getRuleContexts(OptionContext.class);
		}
		public OptionContext option(int i) {
			return getRuleContext(OptionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SecLangParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SecLangParser.COMMA, i);
		}
		public Option_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_option_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterOption_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitOption_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitOption_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Option_listContext option_list() throws RecognitionException {
		Option_listContext _localctx = new Option_listContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_option_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(341);
			match(QUOTE);
			setState(342);
			option();
			setState(347);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(343);
				match(COMMA);
				setState(344);
				option();
				}
				}
				setState(349);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(350);
			match(QUOTE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OptionContext extends ParserRuleContext {
		public Option_nameContext option_name() {
			return getRuleContext(Option_nameContext.class,0);
		}
		public TerminalNode COLON() { return getToken(SecLangParser.COLON, 0); }
		public ValuesContext values() {
			return getRuleContext(ValuesContext.class,0);
		}
		public OptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_option; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterOption(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitOption(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitOption(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OptionContext option() throws RecognitionException {
		OptionContext _localctx = new OptionContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_option);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(352);
			option_name();
			setState(353);
			match(COLON);
			setState(354);
			values();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Option_nameContext extends ParserRuleContext {
		public TerminalNode OPTION_NAME() { return getToken(SecLangParser.OPTION_NAME, 0); }
		public Option_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_option_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterOption_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitOption_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitOption_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Option_nameContext option_name() throws RecognitionException {
		Option_nameContext _localctx = new Option_nameContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_option_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(356);
			match(OPTION_NAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Engine_config_action_directiveContext extends ParserRuleContext {
		public Engine_config_action_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_engine_config_action_directive; }
	 
		public Engine_config_action_directiveContext() { }
		public void copyFrom(Engine_config_action_directiveContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Config_dir_sec_actionContext extends Engine_config_action_directiveContext {
		public TerminalNode CONFIG_DIR_SEC_ACTION() { return getToken(SecLangParser.CONFIG_DIR_SEC_ACTION, 0); }
		public Config_dir_sec_actionContext(Engine_config_action_directiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterConfig_dir_sec_action(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitConfig_dir_sec_action(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitConfig_dir_sec_action(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Config_dir_sec_default_actionContext extends Engine_config_action_directiveContext {
		public TerminalNode CONFIG_DIR_SEC_DEFAULT_ACTION() { return getToken(SecLangParser.CONFIG_DIR_SEC_DEFAULT_ACTION, 0); }
		public Config_dir_sec_default_actionContext(Engine_config_action_directiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterConfig_dir_sec_default_action(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitConfig_dir_sec_default_action(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitConfig_dir_sec_default_action(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Engine_config_action_directiveContext engine_config_action_directive() throws RecognitionException {
		Engine_config_action_directiveContext _localctx = new Engine_config_action_directiveContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_engine_config_action_directive);
		try {
			setState(360);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CONFIG_DIR_SEC_ACTION:
				_localctx = new Config_dir_sec_actionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(358);
				match(CONFIG_DIR_SEC_ACTION);
				}
				break;
			case CONFIG_DIR_SEC_DEFAULT_ACTION:
				_localctx = new Config_dir_sec_default_actionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(359);
				match(CONFIG_DIR_SEC_DEFAULT_ACTION);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Stmt_audit_logContext extends ParserRuleContext {
		public TerminalNode CONFIG_DIR_AUDIT_DIR_MOD() { return getToken(SecLangParser.CONFIG_DIR_AUDIT_DIR_MOD, 0); }
		public TerminalNode CONFIG_DIR_AUDIT_DIR() { return getToken(SecLangParser.CONFIG_DIR_AUDIT_DIR, 0); }
		public TerminalNode CONFIG_DIR_AUDIT_ENG() { return getToken(SecLangParser.CONFIG_DIR_AUDIT_ENG, 0); }
		public TerminalNode CONFIG_DIR_AUDIT_FILE_MODE() { return getToken(SecLangParser.CONFIG_DIR_AUDIT_FILE_MODE, 0); }
		public TerminalNode CONFIG_DIR_AUDIT_LOG2() { return getToken(SecLangParser.CONFIG_DIR_AUDIT_LOG2, 0); }
		public TerminalNode CONFIG_DIR_AUDIT_LOG_P() { return getToken(SecLangParser.CONFIG_DIR_AUDIT_LOG_P, 0); }
		public TerminalNode CONFIG_DIR_AUDIT_LOG() { return getToken(SecLangParser.CONFIG_DIR_AUDIT_LOG, 0); }
		public TerminalNode CONFIG_DIR_AUDIT_LOG_FMT() { return getToken(SecLangParser.CONFIG_DIR_AUDIT_LOG_FMT, 0); }
		public TerminalNode CONFIG_DIR_AUDIT_STS() { return getToken(SecLangParser.CONFIG_DIR_AUDIT_STS, 0); }
		public TerminalNode CONFIG_DIR_AUDIT_TYPE() { return getToken(SecLangParser.CONFIG_DIR_AUDIT_TYPE, 0); }
		public TerminalNode CONFIG_UPLOAD_KEEP_FILES() { return getToken(SecLangParser.CONFIG_UPLOAD_KEEP_FILES, 0); }
		public TerminalNode CONFIG_UPLOAD_FILE_LIMIT() { return getToken(SecLangParser.CONFIG_UPLOAD_FILE_LIMIT, 0); }
		public TerminalNode CONFIG_UPLOAD_FILE_MODE() { return getToken(SecLangParser.CONFIG_UPLOAD_FILE_MODE, 0); }
		public TerminalNode CONFIG_UPLOAD_DIR() { return getToken(SecLangParser.CONFIG_UPLOAD_DIR, 0); }
		public TerminalNode CONFIG_UPLOAD_SAVE_TMP_FILES() { return getToken(SecLangParser.CONFIG_UPLOAD_SAVE_TMP_FILES, 0); }
		public TerminalNode INT() { return getToken(SecLangParser.INT, 0); }
		public Stmt_audit_logContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmt_audit_log; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterStmt_audit_log(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitStmt_audit_log(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitStmt_audit_log(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Stmt_audit_logContext stmt_audit_log() throws RecognitionException {
		Stmt_audit_logContext _localctx = new Stmt_audit_logContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_stmt_audit_log);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(362);
			_la = _input.LA(1);
			if ( !(((((_la - 140)) & ~0x3f) == 0 && ((1L << (_la - 140)) & 279223176896971775L) != 0) || _la==INT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ValuesContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(SecLangParser.INT, 0); }
		public Int_rangeContext int_range() {
			return getRuleContext(Int_rangeContext.class,0);
		}
		public TerminalNode CONFIG_VALUE_ON() { return getToken(SecLangParser.CONFIG_VALUE_ON, 0); }
		public TerminalNode CONFIG_VALUE_OFF() { return getToken(SecLangParser.CONFIG_VALUE_OFF, 0); }
		public TerminalNode CONFIG_VALUE_SERIAL() { return getToken(SecLangParser.CONFIG_VALUE_SERIAL, 0); }
		public TerminalNode CONFIG_VALUE_PARALLEL() { return getToken(SecLangParser.CONFIG_VALUE_PARALLEL, 0); }
		public TerminalNode CONFIG_VALUE_HTTPS() { return getToken(SecLangParser.CONFIG_VALUE_HTTPS, 0); }
		public TerminalNode CONFIG_VALUE_RELEVANT_ONLY() { return getToken(SecLangParser.CONFIG_VALUE_RELEVANT_ONLY, 0); }
		public TerminalNode NATIVE() { return getToken(SecLangParser.NATIVE, 0); }
		public TerminalNode CONFIG_VALUE_ABORT() { return getToken(SecLangParser.CONFIG_VALUE_ABORT, 0); }
		public TerminalNode CONFIG_VALUE_WARN() { return getToken(SecLangParser.CONFIG_VALUE_WARN, 0); }
		public TerminalNode CONFIG_VALUE_DETC() { return getToken(SecLangParser.CONFIG_VALUE_DETC, 0); }
		public TerminalNode CONFIG_VALUE_PROCESS_PARTIAL() { return getToken(SecLangParser.CONFIG_VALUE_PROCESS_PARTIAL, 0); }
		public TerminalNode CONFIG_VALUE_REJECT() { return getToken(SecLangParser.CONFIG_VALUE_REJECT, 0); }
		public TerminalNode CONFIG_VALUE_PATH() { return getToken(SecLangParser.CONFIG_VALUE_PATH, 0); }
		public TerminalNode STRING() { return getToken(SecLangParser.STRING, 0); }
		public TerminalNode VARIABLE_NAME() { return getToken(SecLangParser.VARIABLE_NAME, 0); }
		public TerminalNode COMMA_SEPARATED_STRING() { return getToken(SecLangParser.COMMA_SEPARATED_STRING, 0); }
		public TerminalNode ACTION_CTL_BODY_PROCESSOR_TYPE() { return getToken(SecLangParser.ACTION_CTL_BODY_PROCESSOR_TYPE, 0); }
		public TerminalNode AUDIT_PARTS() { return getToken(SecLangParser.AUDIT_PARTS, 0); }
		public Action_ctl_target_valueContext action_ctl_target_value() {
			return getRuleContext(Action_ctl_target_valueContext.class,0);
		}
		public ValuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_values; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterValues(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitValues(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValuesContext values() throws RecognitionException {
		ValuesContext _localctx = new ValuesContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_values);
		try {
			setState(387);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(364);
				match(INT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(365);
				int_range();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(366);
				match(CONFIG_VALUE_ON);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(367);
				match(CONFIG_VALUE_OFF);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(368);
				match(CONFIG_VALUE_SERIAL);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(369);
				match(CONFIG_VALUE_PARALLEL);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(370);
				match(CONFIG_VALUE_HTTPS);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(371);
				match(CONFIG_VALUE_RELEVANT_ONLY);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(372);
				match(NATIVE);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(373);
				match(CONFIG_VALUE_ABORT);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(374);
				match(CONFIG_VALUE_WARN);
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(375);
				match(CONFIG_VALUE_DETC);
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(376);
				match(CONFIG_VALUE_PROCESS_PARTIAL);
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(377);
				match(CONFIG_VALUE_REJECT);
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(378);
				match(CONFIG_VALUE_PATH);
				setState(379);
				match(INT);
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(380);
				match(CONFIG_VALUE_PATH);
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(381);
				match(STRING);
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				setState(382);
				match(VARIABLE_NAME);
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				setState(383);
				match(COMMA_SEPARATED_STRING);
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				setState(384);
				match(ACTION_CTL_BODY_PROCESSOR_TYPE);
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				setState(385);
				match(AUDIT_PARTS);
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				setState(386);
				action_ctl_target_value();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Action_ctl_target_valueContext extends ParserRuleContext {
		public TerminalNode SEMI() { return getToken(SecLangParser.SEMI, 0); }
		public Ctl_variable_enumContext ctl_variable_enum() {
			return getRuleContext(Ctl_variable_enumContext.class,0);
		}
		public Ctl_idContext ctl_id() {
			return getRuleContext(Ctl_idContext.class,0);
		}
		public List<TerminalNode> SINGLE_QUOTE() { return getTokens(SecLangParser.SINGLE_QUOTE); }
		public TerminalNode SINGLE_QUOTE(int i) {
			return getToken(SecLangParser.SINGLE_QUOTE, i);
		}
		public String_literalContext string_literal() {
			return getRuleContext(String_literalContext.class,0);
		}
		public TerminalNode VARIABLE_NAME() { return getToken(SecLangParser.VARIABLE_NAME, 0); }
		public Ctl_collection_enumContext ctl_collection_enum() {
			return getRuleContext(Ctl_collection_enumContext.class,0);
		}
		public TerminalNode COLON() { return getToken(SecLangParser.COLON, 0); }
		public Ctl_collection_valueContext ctl_collection_value() {
			return getRuleContext(Ctl_collection_valueContext.class,0);
		}
		public Action_ctl_target_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_ctl_target_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterAction_ctl_target_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitAction_ctl_target_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitAction_ctl_target_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Action_ctl_target_valueContext action_ctl_target_value() throws RecognitionException {
		Action_ctl_target_valueContext _localctx = new Action_ctl_target_valueContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_action_ctl_target_value);
		int _la;
		try {
			setState(413);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(395);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case IDENT:
				case INT:
					{
					setState(389);
					ctl_id();
					}
					break;
				case SINGLE_QUOTE:
					{
					setState(390);
					match(SINGLE_QUOTE);
					setState(391);
					string_literal();
					setState(392);
					match(SINGLE_QUOTE);
					}
					break;
				case VARIABLE_NAME:
					{
					setState(394);
					match(VARIABLE_NAME);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(397);
				match(SEMI);
				setState(398);
				ctl_variable_enum();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(405);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case IDENT:
				case INT:
					{
					setState(399);
					ctl_id();
					}
					break;
				case SINGLE_QUOTE:
					{
					setState(400);
					match(SINGLE_QUOTE);
					setState(401);
					string_literal();
					setState(402);
					match(SINGLE_QUOTE);
					}
					break;
				case VARIABLE_NAME:
					{
					setState(404);
					match(VARIABLE_NAME);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(407);
				match(SEMI);
				setState(408);
				ctl_collection_enum();
				setState(411);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COLON) {
					{
					setState(409);
					match(COLON);
					setState(410);
					ctl_collection_value();
					}
				}

				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Update_target_rules_valuesContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(SecLangParser.INT, 0); }
		public Int_rangeContext int_range() {
			return getRuleContext(Int_rangeContext.class,0);
		}
		public TerminalNode STRING() { return getToken(SecLangParser.STRING, 0); }
		public Update_target_rules_valuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_update_target_rules_values; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterUpdate_target_rules_values(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitUpdate_target_rules_values(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitUpdate_target_rules_values(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Update_target_rules_valuesContext update_target_rules_values() throws RecognitionException {
		Update_target_rules_valuesContext _localctx = new Update_target_rules_valuesContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_update_target_rules_values);
		try {
			setState(418);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(415);
				match(INT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(416);
				int_range();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(417);
				match(STRING);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Operator_notContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(SecLangParser.NOT, 0); }
		public Operator_notContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operator_not; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterOperator_not(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitOperator_not(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitOperator_not(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Operator_notContext operator_not() throws RecognitionException {
		Operator_notContext _localctx = new Operator_notContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_operator_not);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(420);
			match(NOT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OperatorContext extends ParserRuleContext {
		public List<TerminalNode> QUOTE() { return getTokens(SecLangParser.QUOTE); }
		public TerminalNode QUOTE(int i) {
			return getToken(SecLangParser.QUOTE, i);
		}
		public TerminalNode AT() { return getToken(SecLangParser.AT, 0); }
		public Operator_nameContext operator_name() {
			return getRuleContext(Operator_nameContext.class,0);
		}
		public Operator_notContext operator_not() {
			return getRuleContext(Operator_notContext.class,0);
		}
		public Operator_valueContext operator_value() {
			return getRuleContext(Operator_valueContext.class,0);
		}
		public OperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OperatorContext operator() throws RecognitionException {
		OperatorContext _localctx = new OperatorContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_operator);
		int _la;
		try {
			setState(438);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(422);
				match(QUOTE);
				setState(424);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(423);
					operator_not();
					}
				}

				setState(426);
				match(AT);
				setState(427);
				operator_name();
				setState(429);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VARIABLE_NAME_ENUM || ((((_la - 233)) & ~0x3f) == 0 && ((1L << (_la - 233)) & 1352663041L) != 0)) {
					{
					setState(428);
					operator_value();
					}
				}

				setState(431);
				match(QUOTE);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(433);
				match(QUOTE);
				setState(434);
				operator_value();
				setState(435);
				match(QUOTE);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(437);
				operator_value();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Operator_nameContext extends ParserRuleContext {
		public TerminalNode OPERATOR_UNCONDITIONAL_MATCH() { return getToken(SecLangParser.OPERATOR_UNCONDITIONAL_MATCH, 0); }
		public TerminalNode OPERATOR_DETECT_SQLI() { return getToken(SecLangParser.OPERATOR_DETECT_SQLI, 0); }
		public TerminalNode OPERATOR_DETECT_XSS() { return getToken(SecLangParser.OPERATOR_DETECT_XSS, 0); }
		public TerminalNode OPERATOR_VALIDATE_URL_ENCODING() { return getToken(SecLangParser.OPERATOR_VALIDATE_URL_ENCODING, 0); }
		public TerminalNode OPERATOR_VALIDATE_UTF8_ENCODING() { return getToken(SecLangParser.OPERATOR_VALIDATE_UTF8_ENCODING, 0); }
		public TerminalNode OPERATOR_INSPECT_FILE() { return getToken(SecLangParser.OPERATOR_INSPECT_FILE, 0); }
		public TerminalNode OPERATOR_FUZZY_HASH() { return getToken(SecLangParser.OPERATOR_FUZZY_HASH, 0); }
		public TerminalNode OPERATOR_VALIDATE_BYTE_RANGE() { return getToken(SecLangParser.OPERATOR_VALIDATE_BYTE_RANGE, 0); }
		public TerminalNode OPERATOR_VALIDATE_DTD() { return getToken(SecLangParser.OPERATOR_VALIDATE_DTD, 0); }
		public TerminalNode OPERATOR_VALIDATE_HASH() { return getToken(SecLangParser.OPERATOR_VALIDATE_HASH, 0); }
		public TerminalNode OPERATOR_VALIDATE_SCHEMA() { return getToken(SecLangParser.OPERATOR_VALIDATE_SCHEMA, 0); }
		public TerminalNode OPERATOR_VERIFY_CC() { return getToken(SecLangParser.OPERATOR_VERIFY_CC, 0); }
		public TerminalNode OPERATOR_VERIFY_CPF() { return getToken(SecLangParser.OPERATOR_VERIFY_CPF, 0); }
		public TerminalNode OPERATOR_VERIFY_SSN() { return getToken(SecLangParser.OPERATOR_VERIFY_SSN, 0); }
		public TerminalNode OPERATOR_VERIFY_SVNR() { return getToken(SecLangParser.OPERATOR_VERIFY_SVNR, 0); }
		public TerminalNode OPERATOR_GSB_LOOKUP() { return getToken(SecLangParser.OPERATOR_GSB_LOOKUP, 0); }
		public TerminalNode OPERATOR_RSUB() { return getToken(SecLangParser.OPERATOR_RSUB, 0); }
		public TerminalNode OPERATOR_WITHIN() { return getToken(SecLangParser.OPERATOR_WITHIN, 0); }
		public TerminalNode OPERATOR_CONTAINS_WORD() { return getToken(SecLangParser.OPERATOR_CONTAINS_WORD, 0); }
		public TerminalNode OPERATOR_CONTAINS() { return getToken(SecLangParser.OPERATOR_CONTAINS, 0); }
		public TerminalNode OPERATOR_ENDS_WITH() { return getToken(SecLangParser.OPERATOR_ENDS_WITH, 0); }
		public TerminalNode OPERATOR_EQ() { return getToken(SecLangParser.OPERATOR_EQ, 0); }
		public TerminalNode OPERATOR_GE() { return getToken(SecLangParser.OPERATOR_GE, 0); }
		public TerminalNode OPERATOR_GT() { return getToken(SecLangParser.OPERATOR_GT, 0); }
		public TerminalNode OPERATOR_IP_MATCH_FROM_FILE() { return getToken(SecLangParser.OPERATOR_IP_MATCH_FROM_FILE, 0); }
		public TerminalNode OPERATOR_IP_MATCH() { return getToken(SecLangParser.OPERATOR_IP_MATCH, 0); }
		public TerminalNode OPERATOR_LE() { return getToken(SecLangParser.OPERATOR_LE, 0); }
		public TerminalNode OPERATOR_LT() { return getToken(SecLangParser.OPERATOR_LT, 0); }
		public TerminalNode OPERATOR_PM_FROM_FILE() { return getToken(SecLangParser.OPERATOR_PM_FROM_FILE, 0); }
		public TerminalNode OPERATOR_PM() { return getToken(SecLangParser.OPERATOR_PM, 0); }
		public TerminalNode OPERATOR_RBL() { return getToken(SecLangParser.OPERATOR_RBL, 0); }
		public TerminalNode OPERATOR_RX() { return getToken(SecLangParser.OPERATOR_RX, 0); }
		public TerminalNode OPERATOR_RX_GLOBAL() { return getToken(SecLangParser.OPERATOR_RX_GLOBAL, 0); }
		public TerminalNode OPERATOR_STR_EQ() { return getToken(SecLangParser.OPERATOR_STR_EQ, 0); }
		public TerminalNode OPERATOR_STR_MATCH() { return getToken(SecLangParser.OPERATOR_STR_MATCH, 0); }
		public TerminalNode OPERATOR_BEGINS_WITH() { return getToken(SecLangParser.OPERATOR_BEGINS_WITH, 0); }
		public TerminalNode OPERATOR_GEOLOOKUP() { return getToken(SecLangParser.OPERATOR_GEOLOOKUP, 0); }
		public Operator_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operator_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterOperator_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitOperator_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitOperator_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Operator_nameContext operator_name() throws RecognitionException {
		Operator_nameContext _localctx = new Operator_nameContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_operator_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(440);
			_la = _input.LA(1);
			if ( !(((((_la - 89)) & ~0x3f) == 0 && ((1L << (_la - 89)) & 137438953471L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Operator_valueContext extends ParserRuleContext {
		public Variable_enumContext variable_enum() {
			return getRuleContext(Variable_enumContext.class,0);
		}
		public TerminalNode STRING() { return getToken(SecLangParser.STRING, 0); }
		public List<TerminalNode> INT_RANGE_VALUE() { return getTokens(SecLangParser.INT_RANGE_VALUE); }
		public TerminalNode INT_RANGE_VALUE(int i) {
			return getToken(SecLangParser.INT_RANGE_VALUE, i);
		}
		public List<Operator_int_rangeContext> operator_int_range() {
			return getRuleContexts(Operator_int_rangeContext.class);
		}
		public Operator_int_rangeContext operator_int_range(int i) {
			return getRuleContext(Operator_int_rangeContext.class,i);
		}
		public List<TerminalNode> WS_INT_RANGE() { return getTokens(SecLangParser.WS_INT_RANGE); }
		public TerminalNode WS_INT_RANGE(int i) {
			return getToken(SecLangParser.WS_INT_RANGE, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SecLangParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SecLangParser.COMMA, i);
		}
		public TerminalNode OPERATOR_UNQUOTED_STRING() { return getToken(SecLangParser.OPERATOR_UNQUOTED_STRING, 0); }
		public TerminalNode OPERATOR_QUOTED_STRING() { return getToken(SecLangParser.OPERATOR_QUOTED_STRING, 0); }
		public Operator_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operator_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterOperator_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitOperator_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitOperator_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Operator_valueContext operator_value() throws RecognitionException {
		Operator_valueContext _localctx = new Operator_valueContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_operator_value);
		int _la;
		try {
			setState(472);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VARIABLE_NAME_ENUM:
				enterOuterAlt(_localctx, 1);
				{
				setState(442);
				variable_enum();
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 2);
				{
				setState(443);
				match(STRING);
				}
				break;
			case INT_RANGE_VALUE:
			case WS_INT_RANGE:
				enterOuterAlt(_localctx, 3);
				{
				setState(447);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==WS_INT_RANGE) {
					{
					{
					setState(444);
					match(WS_INT_RANGE);
					}
					}
					setState(449);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(452);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
				case 1:
					{
					setState(450);
					match(INT_RANGE_VALUE);
					}
					break;
				case 2:
					{
					setState(451);
					operator_int_range();
					}
					break;
				}
				setState(467);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(454);
					match(COMMA);
					setState(458);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==WS_INT_RANGE) {
						{
						{
						setState(455);
						match(WS_INT_RANGE);
						}
						}
						setState(460);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(463);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
					case 1:
						{
						setState(461);
						match(INT_RANGE_VALUE);
						}
						break;
					case 2:
						{
						setState(462);
						operator_int_range();
						}
						break;
					}
					}
					}
					setState(469);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case OPERATOR_UNQUOTED_STRING:
				enterOuterAlt(_localctx, 4);
				{
				setState(470);
				match(OPERATOR_UNQUOTED_STRING);
				}
				break;
			case OPERATOR_QUOTED_STRING:
				enterOuterAlt(_localctx, 5);
				{
				setState(471);
				match(OPERATOR_QUOTED_STRING);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Var_notContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(SecLangParser.NOT, 0); }
		public Var_notContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_not; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterVar_not(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitVar_not(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitVar_not(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Var_notContext var_not() throws RecognitionException {
		Var_notContext _localctx = new Var_notContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_var_not);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(474);
			match(NOT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Var_countContext extends ParserRuleContext {
		public TerminalNode VAR_COUNT() { return getToken(SecLangParser.VAR_COUNT, 0); }
		public Var_countContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_count; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterVar_count(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitVar_count(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitVar_count(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Var_countContext var_count() throws RecognitionException {
		Var_countContext _localctx = new Var_countContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_var_count);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(476);
			match(VAR_COUNT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VariablesContext extends ParserRuleContext {
		public List<Var_stmtContext> var_stmt() {
			return getRuleContexts(Var_stmtContext.class);
		}
		public Var_stmtContext var_stmt(int i) {
			return getRuleContext(Var_stmtContext.class,i);
		}
		public List<TerminalNode> QUOTE() { return getTokens(SecLangParser.QUOTE); }
		public TerminalNode QUOTE(int i) {
			return getToken(SecLangParser.QUOTE, i);
		}
		public List<Var_notContext> var_not() {
			return getRuleContexts(Var_notContext.class);
		}
		public Var_notContext var_not(int i) {
			return getRuleContext(Var_notContext.class,i);
		}
		public Var_countContext var_count() {
			return getRuleContext(Var_countContext.class,0);
		}
		public List<TerminalNode> PIPE() { return getTokens(SecLangParser.PIPE); }
		public TerminalNode PIPE(int i) {
			return getToken(SecLangParser.PIPE, i);
		}
		public VariablesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variables; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterVariables(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitVariables(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitVariables(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariablesContext variables() throws RecognitionException {
		VariablesContext _localctx = new VariablesContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_variables);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(479);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==QUOTE) {
				{
				setState(478);
				match(QUOTE);
				}
			}

			setState(482);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(481);
				var_not();
				}
			}

			setState(485);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR_COUNT) {
				{
				setState(484);
				var_count();
				}
			}

			setState(487);
			var_stmt();
			setState(489);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				{
				setState(488);
				match(QUOTE);
				}
				break;
			}
			setState(504);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PIPE) {
				{
				{
				setState(491);
				match(PIPE);
				setState(493);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==QUOTE) {
					{
					setState(492);
					match(QUOTE);
					}
				}

				setState(496);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(495);
					var_not();
					}
				}

				setState(498);
				var_stmt();
				setState(500);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
				case 1:
					{
					setState(499);
					match(QUOTE);
					}
					break;
				}
				}
				}
				setState(506);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Update_variablesContext extends ParserRuleContext {
		public List<Var_stmtContext> var_stmt() {
			return getRuleContexts(Var_stmtContext.class);
		}
		public Var_stmtContext var_stmt(int i) {
			return getRuleContext(Var_stmtContext.class,i);
		}
		public List<TerminalNode> QUOTE() { return getTokens(SecLangParser.QUOTE); }
		public TerminalNode QUOTE(int i) {
			return getToken(SecLangParser.QUOTE, i);
		}
		public List<Var_notContext> var_not() {
			return getRuleContexts(Var_notContext.class);
		}
		public Var_notContext var_not(int i) {
			return getRuleContext(Var_notContext.class,i);
		}
		public Var_countContext var_count() {
			return getRuleContext(Var_countContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(SecLangParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SecLangParser.COMMA, i);
		}
		public Update_variablesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_update_variables; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterUpdate_variables(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitUpdate_variables(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitUpdate_variables(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Update_variablesContext update_variables() throws RecognitionException {
		Update_variablesContext _localctx = new Update_variablesContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_update_variables);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(508);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==QUOTE) {
				{
				setState(507);
				match(QUOTE);
				}
			}

			setState(511);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(510);
				var_not();
				}
			}

			setState(514);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR_COUNT) {
				{
				setState(513);
				var_count();
				}
			}

			setState(516);
			var_stmt();
			setState(518);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
			case 1:
				{
				setState(517);
				match(QUOTE);
				}
				break;
			}
			setState(533);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(520);
				match(COMMA);
				setState(522);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==QUOTE) {
					{
					setState(521);
					match(QUOTE);
					}
				}

				setState(525);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(524);
					var_not();
					}
				}

				setState(527);
				var_stmt();
				setState(529);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
				case 1:
					{
					setState(528);
					match(QUOTE);
					}
					break;
				}
				}
				}
				setState(535);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class New_targetContext extends ParserRuleContext {
		public Var_stmtContext var_stmt() {
			return getRuleContext(Var_stmtContext.class,0);
		}
		public New_targetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_new_target; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterNew_target(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitNew_target(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitNew_target(this);
			else return visitor.visitChildren(this);
		}
	}

	public final New_targetContext new_target() throws RecognitionException {
		New_targetContext _localctx = new New_targetContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_new_target);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(536);
			var_stmt();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Var_stmtContext extends ParserRuleContext {
		public Variable_enumContext variable_enum() {
			return getRuleContext(Variable_enumContext.class,0);
		}
		public Collection_enumContext collection_enum() {
			return getRuleContext(Collection_enumContext.class,0);
		}
		public TerminalNode COLON() { return getToken(SecLangParser.COLON, 0); }
		public Collection_valueContext collection_value() {
			return getRuleContext(Collection_valueContext.class,0);
		}
		public Var_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterVar_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitVar_stmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitVar_stmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Var_stmtContext var_stmt() throws RecognitionException {
		Var_stmtContext _localctx = new Var_stmtContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_var_stmt);
		int _la;
		try {
			setState(544);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VARIABLE_NAME_ENUM:
				enterOuterAlt(_localctx, 1);
				{
				setState(538);
				variable_enum();
				}
				break;
			case COLLECTION_NAME_ENUM:
			case RUN_TIME_VAR_XML:
				enterOuterAlt(_localctx, 2);
				{
				setState(539);
				collection_enum();
				setState(542);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COLON) {
					{
					setState(540);
					match(COLON);
					setState(541);
					collection_value();
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Variable_enumContext extends ParserRuleContext {
		public TerminalNode VARIABLE_NAME_ENUM() { return getToken(SecLangParser.VARIABLE_NAME_ENUM, 0); }
		public Variable_enumContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable_enum; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterVariable_enum(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitVariable_enum(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitVariable_enum(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Variable_enumContext variable_enum() throws RecognitionException {
		Variable_enumContext _localctx = new Variable_enumContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_variable_enum);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(546);
			match(VARIABLE_NAME_ENUM);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Ctl_variable_enumContext extends ParserRuleContext {
		public TerminalNode VARIABLE_NAME_ENUM() { return getToken(SecLangParser.VARIABLE_NAME_ENUM, 0); }
		public Ctl_variable_enumContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ctl_variable_enum; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterCtl_variable_enum(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitCtl_variable_enum(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitCtl_variable_enum(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ctl_variable_enumContext ctl_variable_enum() throws RecognitionException {
		Ctl_variable_enumContext _localctx = new Ctl_variable_enumContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_ctl_variable_enum);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(548);
			match(VARIABLE_NAME_ENUM);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Collection_enumContext extends ParserRuleContext {
		public TerminalNode COLLECTION_NAME_ENUM() { return getToken(SecLangParser.COLLECTION_NAME_ENUM, 0); }
		public TerminalNode RUN_TIME_VAR_XML() { return getToken(SecLangParser.RUN_TIME_VAR_XML, 0); }
		public Collection_enumContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_collection_enum; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterCollection_enum(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitCollection_enum(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitCollection_enum(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Collection_enumContext collection_enum() throws RecognitionException {
		Collection_enumContext _localctx = new Collection_enumContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_collection_enum);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(550);
			_la = _input.LA(1);
			if ( !(_la==COLLECTION_NAME_ENUM || _la==RUN_TIME_VAR_XML) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Ctl_collection_enumContext extends ParserRuleContext {
		public TerminalNode COLLECTION_NAME_ENUM() { return getToken(SecLangParser.COLLECTION_NAME_ENUM, 0); }
		public TerminalNode RUN_TIME_VAR_XML() { return getToken(SecLangParser.RUN_TIME_VAR_XML, 0); }
		public Ctl_collection_enumContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ctl_collection_enum; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterCtl_collection_enum(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitCtl_collection_enum(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitCtl_collection_enum(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ctl_collection_enumContext ctl_collection_enum() throws RecognitionException {
		Ctl_collection_enumContext _localctx = new Ctl_collection_enumContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_ctl_collection_enum);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(552);
			_la = _input.LA(1);
			if ( !(_la==COLLECTION_NAME_ENUM || _la==RUN_TIME_VAR_XML) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ActionsContext extends ParserRuleContext {
		public List<TerminalNode> QUOTE() { return getTokens(SecLangParser.QUOTE); }
		public TerminalNode QUOTE(int i) {
			return getToken(SecLangParser.QUOTE, i);
		}
		public List<ActionContext> action() {
			return getRuleContexts(ActionContext.class);
		}
		public ActionContext action(int i) {
			return getRuleContext(ActionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SecLangParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SecLangParser.COMMA, i);
		}
		public ActionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterActions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitActions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitActions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActionsContext actions() throws RecognitionException {
		ActionsContext _localctx = new ActionsContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_actions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(554);
			match(QUOTE);
			setState(555);
			action();
			setState(560);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(556);
				match(COMMA);
				setState(557);
				action();
				}
				}
				setState(562);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(563);
			match(QUOTE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ActionContext extends ParserRuleContext {
		public Action_with_paramsContext action_with_params() {
			return getRuleContext(Action_with_paramsContext.class,0);
		}
		public TerminalNode COLON() { return getToken(SecLangParser.COLON, 0); }
		public Action_valueContext action_value() {
			return getRuleContext(Action_valueContext.class,0);
		}
		public TerminalNode NOT() { return getToken(SecLangParser.NOT, 0); }
		public TerminalNode EQUAL() { return getToken(SecLangParser.EQUAL, 0); }
		public TerminalNode ACTION_TRANSFORMATION() { return getToken(SecLangParser.ACTION_TRANSFORMATION, 0); }
		public Transformation_action_valueContext transformation_action_value() {
			return getRuleContext(Transformation_action_valueContext.class,0);
		}
		public Action_onlyContext action_only() {
			return getRuleContext(Action_onlyContext.class,0);
		}
		public ActionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitAction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActionContext action() throws RecognitionException {
		ActionContext _localctx = new ActionContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_action);
		int _la;
		try {
			setState(583);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(565);
				action_with_params();
				setState(566);
				match(COLON);
				setState(568);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(567);
					match(NOT);
					}
				}

				setState(571);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EQUAL) {
					{
					setState(570);
					match(EQUAL);
					}
				}

				setState(573);
				action_value();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(575);
				action_with_params();
				setState(576);
				match(COLON);
				setState(577);
				action_value();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(579);
				match(ACTION_TRANSFORMATION);
				setState(580);
				match(COLON);
				setState(581);
				transformation_action_value();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(582);
				action_only();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Action_onlyContext extends ParserRuleContext {
		public Disruptive_action_onlyContext disruptive_action_only() {
			return getRuleContext(Disruptive_action_onlyContext.class,0);
		}
		public Non_disruptive_action_onlyContext non_disruptive_action_only() {
			return getRuleContext(Non_disruptive_action_onlyContext.class,0);
		}
		public Flow_action_onlyContext flow_action_only() {
			return getRuleContext(Flow_action_onlyContext.class,0);
		}
		public Action_onlyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_only; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterAction_only(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitAction_only(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitAction_only(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Action_onlyContext action_only() throws RecognitionException {
		Action_onlyContext _localctx = new Action_onlyContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_action_only);
		try {
			setState(588);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ACTION_ALLOW:
			case ACTION_BLOCK:
			case ACTION_DENY:
			case ACTION_DROP:
			case ACTION_PASS:
			case ACTION_PAUSE:
				enterOuterAlt(_localctx, 1);
				{
				setState(585);
				disruptive_action_only();
				}
				break;
			case ACTION_AUDIT_LOG:
			case ACTION_CAPTURE:
			case ACTION_LOG:
			case ACTION_MULTI_MATCH:
			case ACTION_NO_AUDIT_LOG:
			case ACTION_NO_LOG:
			case ACTION_SANITISE_MATCHED:
				enterOuterAlt(_localctx, 2);
				{
				setState(586);
				non_disruptive_action_only();
				}
				break;
			case ACTION_CHAIN:
				enterOuterAlt(_localctx, 3);
				{
				setState(587);
				flow_action_only();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Disruptive_action_onlyContext extends ParserRuleContext {
		public TerminalNode ACTION_ALLOW() { return getToken(SecLangParser.ACTION_ALLOW, 0); }
		public TerminalNode ACTION_BLOCK() { return getToken(SecLangParser.ACTION_BLOCK, 0); }
		public TerminalNode ACTION_DENY() { return getToken(SecLangParser.ACTION_DENY, 0); }
		public TerminalNode ACTION_DROP() { return getToken(SecLangParser.ACTION_DROP, 0); }
		public TerminalNode ACTION_PASS() { return getToken(SecLangParser.ACTION_PASS, 0); }
		public TerminalNode ACTION_PAUSE() { return getToken(SecLangParser.ACTION_PAUSE, 0); }
		public Disruptive_action_onlyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_disruptive_action_only; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterDisruptive_action_only(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitDisruptive_action_only(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitDisruptive_action_only(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Disruptive_action_onlyContext disruptive_action_only() throws RecognitionException {
		Disruptive_action_onlyContext _localctx = new Disruptive_action_onlyContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_disruptive_action_only);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(590);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 864779090593316864L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Non_disruptive_action_onlyContext extends ParserRuleContext {
		public TerminalNode ACTION_AUDIT_LOG() { return getToken(SecLangParser.ACTION_AUDIT_LOG, 0); }
		public TerminalNode ACTION_CAPTURE() { return getToken(SecLangParser.ACTION_CAPTURE, 0); }
		public TerminalNode ACTION_SANITISE_MATCHED() { return getToken(SecLangParser.ACTION_SANITISE_MATCHED, 0); }
		public TerminalNode ACTION_LOG() { return getToken(SecLangParser.ACTION_LOG, 0); }
		public TerminalNode ACTION_MULTI_MATCH() { return getToken(SecLangParser.ACTION_MULTI_MATCH, 0); }
		public TerminalNode ACTION_NO_AUDIT_LOG() { return getToken(SecLangParser.ACTION_NO_AUDIT_LOG, 0); }
		public TerminalNode ACTION_NO_LOG() { return getToken(SecLangParser.ACTION_NO_LOG, 0); }
		public Non_disruptive_action_onlyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_non_disruptive_action_only; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterNon_disruptive_action_only(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitNon_disruptive_action_only(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitNon_disruptive_action_only(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Non_disruptive_action_onlyContext non_disruptive_action_only() throws RecognitionException {
		Non_disruptive_action_onlyContext _localctx = new Non_disruptive_action_onlyContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_non_disruptive_action_only);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(592);
			_la = _input.LA(1);
			if ( !(((((_la - 29)) & ~0x3f) == 0 && ((1L << (_la - 29)) & 275356057605L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Flow_action_onlyContext extends ParserRuleContext {
		public TerminalNode ACTION_CHAIN() { return getToken(SecLangParser.ACTION_CHAIN, 0); }
		public Flow_action_onlyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flow_action_only; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterFlow_action_only(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitFlow_action_only(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitFlow_action_only(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Flow_action_onlyContext flow_action_only() throws RecognitionException {
		Flow_action_onlyContext _localctx = new Flow_action_onlyContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_flow_action_only);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(594);
			match(ACTION_CHAIN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Action_with_paramsContext extends ParserRuleContext {
		public Metadata_action_with_paramsContext metadata_action_with_params() {
			return getRuleContext(Metadata_action_with_paramsContext.class,0);
		}
		public Disruptive_action_with_paramsContext disruptive_action_with_params() {
			return getRuleContext(Disruptive_action_with_paramsContext.class,0);
		}
		public Non_disruptive_action_with_paramsContext non_disruptive_action_with_params() {
			return getRuleContext(Non_disruptive_action_with_paramsContext.class,0);
		}
		public Flow_action_with_paramsContext flow_action_with_params() {
			return getRuleContext(Flow_action_with_paramsContext.class,0);
		}
		public Data_action_with_paramsContext data_action_with_params() {
			return getRuleContext(Data_action_with_paramsContext.class,0);
		}
		public Action_with_paramsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_with_params; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterAction_with_params(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitAction_with_params(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitAction_with_params(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Action_with_paramsContext action_with_params() throws RecognitionException {
		Action_with_paramsContext _localctx = new Action_with_paramsContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_action_with_params);
		try {
			setState(601);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ACTION_ID:
			case ACTION_MATURITY:
			case ACTION_MSG:
			case ACTION_PHASE:
			case ACTION_REV:
			case ACTION_SEVERITY:
			case ACTION_TAG:
			case ACTION_VER:
				enterOuterAlt(_localctx, 1);
				{
				setState(596);
				metadata_action_with_params();
				}
				break;
			case ACTION_PROXY:
			case ACTION_REDIRECT:
				enterOuterAlt(_localctx, 2);
				{
				setState(597);
				disruptive_action_with_params();
				}
				break;
			case ACTION_APPEND:
			case ACTION_CTL:
			case ACTION_DEPRECATE_VAR:
			case ACTION_EXEC:
			case ACTION_EXPIRE_VAR:
			case ACTION_INITCOL:
			case ACTION_LOG_DATA:
			case ACTION_PREPEND:
			case ACTION_SANITISE_ARG:
			case ACTION_SANITISE_MATCHED_BYTES:
			case ACTION_SANITISE_REQUEST_HEADER:
			case ACTION_SANITISE_RESPONSE_HEADER:
			case ACTION_SETENV:
			case ACTION_SETRSC:
			case ACTION_SETSID:
			case ACTION_SETUID:
			case ACTION_SETVAR:
				enterOuterAlt(_localctx, 3);
				{
				setState(598);
				non_disruptive_action_with_params();
				}
				break;
			case ACTION_SKIP_AFTER:
			case ACTION_SKIP:
				enterOuterAlt(_localctx, 4);
				{
				setState(599);
				flow_action_with_params();
				}
				break;
			case ACTION_STATUS:
			case ACTION_XMLNS:
				enterOuterAlt(_localctx, 5);
				{
				setState(600);
				data_action_with_params();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Metadata_action_with_paramsContext extends ParserRuleContext {
		public Metadata_action_with_paramsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_metadata_action_with_params; }
	 
		public Metadata_action_with_paramsContext() { }
		public void copyFrom(Metadata_action_with_paramsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ACTION_MATURITYContext extends Metadata_action_with_paramsContext {
		public TerminalNode ACTION_MATURITY() { return getToken(SecLangParser.ACTION_MATURITY, 0); }
		public ACTION_MATURITYContext(Metadata_action_with_paramsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterACTION_MATURITY(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitACTION_MATURITY(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitACTION_MATURITY(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ACTION_REVContext extends Metadata_action_with_paramsContext {
		public TerminalNode ACTION_REV() { return getToken(SecLangParser.ACTION_REV, 0); }
		public ACTION_REVContext(Metadata_action_with_paramsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterACTION_REV(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitACTION_REV(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitACTION_REV(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ACTION_VERContext extends Metadata_action_with_paramsContext {
		public TerminalNode ACTION_VER() { return getToken(SecLangParser.ACTION_VER, 0); }
		public ACTION_VERContext(Metadata_action_with_paramsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterACTION_VER(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitACTION_VER(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitACTION_VER(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ACTION_SEVERITYContext extends Metadata_action_with_paramsContext {
		public TerminalNode ACTION_SEVERITY() { return getToken(SecLangParser.ACTION_SEVERITY, 0); }
		public ACTION_SEVERITYContext(Metadata_action_with_paramsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterACTION_SEVERITY(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitACTION_SEVERITY(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitACTION_SEVERITY(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ACTION_MSGContext extends Metadata_action_with_paramsContext {
		public TerminalNode ACTION_MSG() { return getToken(SecLangParser.ACTION_MSG, 0); }
		public ACTION_MSGContext(Metadata_action_with_paramsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterACTION_MSG(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitACTION_MSG(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitACTION_MSG(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ACTION_PHASEContext extends Metadata_action_with_paramsContext {
		public TerminalNode ACTION_PHASE() { return getToken(SecLangParser.ACTION_PHASE, 0); }
		public ACTION_PHASEContext(Metadata_action_with_paramsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterACTION_PHASE(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitACTION_PHASE(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitACTION_PHASE(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ACTION_IDContext extends Metadata_action_with_paramsContext {
		public TerminalNode ACTION_ID() { return getToken(SecLangParser.ACTION_ID, 0); }
		public ACTION_IDContext(Metadata_action_with_paramsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterACTION_ID(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitACTION_ID(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitACTION_ID(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ACTION_TAGContext extends Metadata_action_with_paramsContext {
		public TerminalNode ACTION_TAG() { return getToken(SecLangParser.ACTION_TAG, 0); }
		public ACTION_TAGContext(Metadata_action_with_paramsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterACTION_TAG(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitACTION_TAG(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitACTION_TAG(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Metadata_action_with_paramsContext metadata_action_with_params() throws RecognitionException {
		Metadata_action_with_paramsContext _localctx = new Metadata_action_with_paramsContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_metadata_action_with_params);
		try {
			setState(611);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ACTION_PHASE:
				_localctx = new ACTION_PHASEContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(603);
				match(ACTION_PHASE);
				}
				break;
			case ACTION_ID:
				_localctx = new ACTION_IDContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(604);
				match(ACTION_ID);
				}
				break;
			case ACTION_MATURITY:
				_localctx = new ACTION_MATURITYContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(605);
				match(ACTION_MATURITY);
				}
				break;
			case ACTION_MSG:
				_localctx = new ACTION_MSGContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(606);
				match(ACTION_MSG);
				}
				break;
			case ACTION_REV:
				_localctx = new ACTION_REVContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(607);
				match(ACTION_REV);
				}
				break;
			case ACTION_SEVERITY:
				_localctx = new ACTION_SEVERITYContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(608);
				match(ACTION_SEVERITY);
				}
				break;
			case ACTION_TAG:
				_localctx = new ACTION_TAGContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(609);
				match(ACTION_TAG);
				}
				break;
			case ACTION_VER:
				_localctx = new ACTION_VERContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(610);
				match(ACTION_VER);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Disruptive_action_with_paramsContext extends ParserRuleContext {
		public TerminalNode ACTION_PROXY() { return getToken(SecLangParser.ACTION_PROXY, 0); }
		public TerminalNode ACTION_REDIRECT() { return getToken(SecLangParser.ACTION_REDIRECT, 0); }
		public Disruptive_action_with_paramsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_disruptive_action_with_params; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterDisruptive_action_with_params(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitDisruptive_action_with_params(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitDisruptive_action_with_params(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Disruptive_action_with_paramsContext disruptive_action_with_params() throws RecognitionException {
		Disruptive_action_with_paramsContext _localctx = new Disruptive_action_with_paramsContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_disruptive_action_with_params);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(613);
			_la = _input.LA(1);
			if ( !(_la==ACTION_PROXY || _la==ACTION_REDIRECT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Non_disruptive_action_with_paramsContext extends ParserRuleContext {
		public TerminalNode ACTION_APPEND() { return getToken(SecLangParser.ACTION_APPEND, 0); }
		public TerminalNode ACTION_CTL() { return getToken(SecLangParser.ACTION_CTL, 0); }
		public TerminalNode ACTION_EXEC() { return getToken(SecLangParser.ACTION_EXEC, 0); }
		public TerminalNode ACTION_EXPIRE_VAR() { return getToken(SecLangParser.ACTION_EXPIRE_VAR, 0); }
		public TerminalNode ACTION_DEPRECATE_VAR() { return getToken(SecLangParser.ACTION_DEPRECATE_VAR, 0); }
		public TerminalNode ACTION_INITCOL() { return getToken(SecLangParser.ACTION_INITCOL, 0); }
		public TerminalNode ACTION_LOG_DATA() { return getToken(SecLangParser.ACTION_LOG_DATA, 0); }
		public TerminalNode ACTION_PREPEND() { return getToken(SecLangParser.ACTION_PREPEND, 0); }
		public TerminalNode ACTION_SANITISE_ARG() { return getToken(SecLangParser.ACTION_SANITISE_ARG, 0); }
		public TerminalNode ACTION_SANITISE_MATCHED_BYTES() { return getToken(SecLangParser.ACTION_SANITISE_MATCHED_BYTES, 0); }
		public TerminalNode ACTION_SANITISE_REQUEST_HEADER() { return getToken(SecLangParser.ACTION_SANITISE_REQUEST_HEADER, 0); }
		public TerminalNode ACTION_SANITISE_RESPONSE_HEADER() { return getToken(SecLangParser.ACTION_SANITISE_RESPONSE_HEADER, 0); }
		public TerminalNode ACTION_SETUID() { return getToken(SecLangParser.ACTION_SETUID, 0); }
		public TerminalNode ACTION_SETRSC() { return getToken(SecLangParser.ACTION_SETRSC, 0); }
		public TerminalNode ACTION_SETSID() { return getToken(SecLangParser.ACTION_SETSID, 0); }
		public TerminalNode ACTION_SETENV() { return getToken(SecLangParser.ACTION_SETENV, 0); }
		public TerminalNode ACTION_SETVAR() { return getToken(SecLangParser.ACTION_SETVAR, 0); }
		public Non_disruptive_action_with_paramsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_non_disruptive_action_with_params; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterNon_disruptive_action_with_params(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitNon_disruptive_action_with_params(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitNon_disruptive_action_with_params(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Non_disruptive_action_with_paramsContext non_disruptive_action_with_params() throws RecognitionException {
		Non_disruptive_action_with_paramsContext _localctx = new Non_disruptive_action_with_paramsContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_non_disruptive_action_with_params);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(615);
			_la = _input.LA(1);
			if ( !(((((_la - 28)) & ~0x3f) == 0 && ((1L << (_la - 28)) & 140058897809441L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_action_with_paramsContext extends ParserRuleContext {
		public TerminalNode ACTION_XMLNS() { return getToken(SecLangParser.ACTION_XMLNS, 0); }
		public TerminalNode ACTION_STATUS() { return getToken(SecLangParser.ACTION_STATUS, 0); }
		public Data_action_with_paramsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_action_with_params; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterData_action_with_params(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitData_action_with_params(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitData_action_with_params(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_action_with_paramsContext data_action_with_params() throws RecognitionException {
		Data_action_with_paramsContext _localctx = new Data_action_with_paramsContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_data_action_with_params);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(617);
			_la = _input.LA(1);
			if ( !(_la==ACTION_STATUS || _la==ACTION_XMLNS) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Flow_action_with_paramsContext extends ParserRuleContext {
		public TerminalNode ACTION_SKIP() { return getToken(SecLangParser.ACTION_SKIP, 0); }
		public TerminalNode ACTION_SKIP_AFTER() { return getToken(SecLangParser.ACTION_SKIP_AFTER, 0); }
		public Flow_action_with_paramsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flow_action_with_params; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterFlow_action_with_params(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitFlow_action_with_params(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitFlow_action_with_params(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Flow_action_with_paramsContext flow_action_with_params() throws RecognitionException {
		Flow_action_with_paramsContext _localctx = new Flow_action_with_paramsContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_flow_action_with_params);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(619);
			_la = _input.LA(1);
			if ( !(_la==ACTION_SKIP_AFTER || _la==ACTION_SKIP) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Action_valueContext extends ParserRuleContext {
		public Action_value_typesContext action_value_types() {
			return getRuleContext(Action_value_typesContext.class,0);
		}
		public List<TerminalNode> SINGLE_QUOTE() { return getTokens(SecLangParser.SINGLE_QUOTE); }
		public TerminalNode SINGLE_QUOTE(int i) {
			return getToken(SecLangParser.SINGLE_QUOTE, i);
		}
		public String_literalContext string_literal() {
			return getRuleContext(String_literalContext.class,0);
		}
		public Action_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterAction_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitAction_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitAction_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Action_valueContext action_value() throws RecognitionException {
		Action_valueContext _localctx = new Action_valueContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_action_value);
		try {
			setState(630);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(621);
				action_value_types();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(622);
				match(SINGLE_QUOTE);
				setState(623);
				action_value_types();
				setState(624);
				match(SINGLE_QUOTE);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(626);
				match(SINGLE_QUOTE);
				setState(627);
				string_literal();
				setState(628);
				match(SINGLE_QUOTE);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Action_value_typesContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(SecLangParser.INT, 0); }
		public Collection_valueContext collection_value() {
			return getRuleContext(Collection_valueContext.class,0);
		}
		public Setvar_actionContext setvar_action() {
			return getRuleContext(Setvar_actionContext.class,0);
		}
		public Ctl_actionContext ctl_action() {
			return getRuleContext(Ctl_actionContext.class,0);
		}
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public ValuesContext values() {
			return getRuleContext(ValuesContext.class,0);
		}
		public TerminalNode VARIABLE_NAME() { return getToken(SecLangParser.VARIABLE_NAME, 0); }
		public TerminalNode ACTION_SEVERITY_VALUE() { return getToken(SecLangParser.ACTION_SEVERITY_VALUE, 0); }
		public TerminalNode FREE_TEXT_QUOTE_MACRO_EXPANSION() { return getToken(SecLangParser.FREE_TEXT_QUOTE_MACRO_EXPANSION, 0); }
		public TerminalNode COMMA_SEPARATED_STRING() { return getToken(SecLangParser.COMMA_SEPARATED_STRING, 0); }
		public Action_value_typesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_value_types; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterAction_value_types(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitAction_value_types(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitAction_value_types(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Action_value_typesContext action_value_types() throws RecognitionException {
		Action_value_typesContext _localctx = new Action_value_typesContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_action_value_types);
		try {
			setState(643);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
				enterOuterAlt(_localctx, 1);
				{
				setState(632);
				match(INT);
				}
				break;
			case QUOTE:
			case SINGLE_QUOTE:
			case COMMA:
			case XPATH_EXPRESSION:
			case COLLECTION_ELEMENT_VALUE:
				enterOuterAlt(_localctx, 2);
				{
				setState(633);
				collection_value();
				}
				break;
			case COLLECTION_NAME_SETVAR:
				enterOuterAlt(_localctx, 3);
				{
				setState(634);
				setvar_action();
				}
				break;
			case ACTION_CTL_AUDIT_ENGINE:
			case ACTION_CTL_AUDIT_LOG_PARTS:
			case ACTION_CTL_REQUEST_BODY_PROCESSOR:
			case ACTION_CTL_FORCE_REQ_BODY_VAR:
			case ACTION_CTL_REQUEST_BODY_ACCESS:
			case ACTION_CTL_RULE_ENGINE:
			case ACTION_CTL_RULE_REMOVE_BY_TAG:
			case ACTION_CTL_RULE_REMOVE_BY_ID:
			case ACTION_CTL_RULE_REMOVE_TARGET_BY_ID:
			case ACTION_CTL_RULE_REMOVE_TARGET_BY_TAG:
				enterOuterAlt(_localctx, 4);
				{
				setState(635);
				ctl_action();
				setState(636);
				assignment();
				setState(637);
				values();
				}
				break;
			case VARIABLE_NAME:
				enterOuterAlt(_localctx, 5);
				{
				setState(639);
				match(VARIABLE_NAME);
				}
				break;
			case ACTION_SEVERITY_VALUE:
				enterOuterAlt(_localctx, 6);
				{
				setState(640);
				match(ACTION_SEVERITY_VALUE);
				}
				break;
			case FREE_TEXT_QUOTE_MACRO_EXPANSION:
				enterOuterAlt(_localctx, 7);
				{
				setState(641);
				match(FREE_TEXT_QUOTE_MACRO_EXPANSION);
				}
				break;
			case COMMA_SEPARATED_STRING:
				enterOuterAlt(_localctx, 8);
				{
				setState(642);
				match(COMMA_SEPARATED_STRING);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class String_literalContext extends ParserRuleContext {
		public TerminalNode STRING_LITERAL() { return getToken(SecLangParser.STRING_LITERAL, 0); }
		public String_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterString_literal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitString_literal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitString_literal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final String_literalContext string_literal() throws RecognitionException {
		String_literalContext _localctx = new String_literalContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_string_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(645);
			match(STRING_LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Ctl_actionContext extends ParserRuleContext {
		public TerminalNode ACTION_CTL_FORCE_REQ_BODY_VAR() { return getToken(SecLangParser.ACTION_CTL_FORCE_REQ_BODY_VAR, 0); }
		public TerminalNode ACTION_CTL_REQUEST_BODY_ACCESS() { return getToken(SecLangParser.ACTION_CTL_REQUEST_BODY_ACCESS, 0); }
		public TerminalNode ACTION_CTL_RULE_ENGINE() { return getToken(SecLangParser.ACTION_CTL_RULE_ENGINE, 0); }
		public TerminalNode ACTION_CTL_RULE_REMOVE_BY_ID() { return getToken(SecLangParser.ACTION_CTL_RULE_REMOVE_BY_ID, 0); }
		public TerminalNode ACTION_CTL_RULE_REMOVE_BY_TAG() { return getToken(SecLangParser.ACTION_CTL_RULE_REMOVE_BY_TAG, 0); }
		public TerminalNode ACTION_CTL_RULE_REMOVE_TARGET_BY_ID() { return getToken(SecLangParser.ACTION_CTL_RULE_REMOVE_TARGET_BY_ID, 0); }
		public TerminalNode ACTION_CTL_RULE_REMOVE_TARGET_BY_TAG() { return getToken(SecLangParser.ACTION_CTL_RULE_REMOVE_TARGET_BY_TAG, 0); }
		public TerminalNode ACTION_CTL_AUDIT_ENGINE() { return getToken(SecLangParser.ACTION_CTL_AUDIT_ENGINE, 0); }
		public TerminalNode ACTION_CTL_AUDIT_LOG_PARTS() { return getToken(SecLangParser.ACTION_CTL_AUDIT_LOG_PARTS, 0); }
		public TerminalNode ACTION_CTL_REQUEST_BODY_PROCESSOR() { return getToken(SecLangParser.ACTION_CTL_REQUEST_BODY_PROCESSOR, 0); }
		public Ctl_actionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ctl_action; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterCtl_action(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitCtl_action(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitCtl_action(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ctl_actionContext ctl_action() throws RecognitionException {
		Ctl_actionContext _localctx = new Ctl_actionContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_ctl_action);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(647);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 17575006175232L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Transformation_action_valueContext extends ParserRuleContext {
		public TerminalNode TRANSFORMATION_VALUE() { return getToken(SecLangParser.TRANSFORMATION_VALUE, 0); }
		public Transformation_action_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_transformation_action_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterTransformation_action_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitTransformation_action_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitTransformation_action_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Transformation_action_valueContext transformation_action_value() throws RecognitionException {
		Transformation_action_valueContext _localctx = new Transformation_action_valueContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_transformation_action_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(649);
			match(TRANSFORMATION_VALUE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Collection_valueContext extends ParserRuleContext {
		public TerminalNode XPATH_EXPRESSION() { return getToken(SecLangParser.XPATH_EXPRESSION, 0); }
		public TerminalNode COLLECTION_ELEMENT_VALUE() { return getToken(SecLangParser.COLLECTION_ELEMENT_VALUE, 0); }
		public Collection_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_collection_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterCollection_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitCollection_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitCollection_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Collection_valueContext collection_value() throws RecognitionException {
		Collection_valueContext _localctx = new Collection_valueContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_collection_value);
		try {
			setState(654);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EOF:
			case QUOTE:
			case SINGLE_QUOTE:
			case COMMA:
			case PIPE:
			case HASH:
			case VARIABLE_NAME_ENUM:
			case CONFIG_COMPONENT_SIG:
			case CONFIG_SEC_SERVER_SIG:
			case CONFIG_SEC_WEB_APP_ID:
			case CONFIG_SEC_CACHE_TRANSFORMATIONS:
			case CONFIG_SEC_CHROOT_DIR:
			case CONFIG_CONN_ENGINE:
			case CONFIG_SEC_HASH_ENGINE:
			case CONFIG_SEC_HASH_KEY:
			case CONFIG_SEC_HASH_PARAM:
			case CONFIG_SEC_HASH_METHOD_RX:
			case CONFIG_SEC_HASH_METHOD_PM:
			case CONFIG_CONTENT_INJECTION:
			case CONFIG_SEC_ARGUMENT_SEPARATOR:
			case CONFIG_DIR_AUDIT_DIR:
			case CONFIG_DIR_AUDIT_DIR_MOD:
			case CONFIG_DIR_AUDIT_ENG:
			case CONFIG_DIR_AUDIT_FILE_MODE:
			case CONFIG_DIR_AUDIT_LOG2:
			case CONFIG_DIR_AUDIT_LOG:
			case CONFIG_DIR_AUDIT_LOG_FMT:
			case CONFIG_DIR_AUDIT_LOG_P:
			case CONFIG_DIR_AUDIT_STS:
			case CONFIG_DIR_AUDIT_TYPE:
			case CONFIG_DIR_DEBUG_LOG:
			case CONFIG_DIR_DEBUG_LVL:
			case CONFIG_DIR_GEO_DB:
			case CONFIG_DIR_GSB_DB:
			case CONFIG_SEC_GUARDIAN_LOG:
			case CONFIG_SEC_INTERCEPT_ON_ERROR:
			case CONFIG_SEC_CONN_R_STATE_LIMIT:
			case CONFIG_SEC_CONN_W_STATE_LIMIT:
			case CONFIG_SEC_SENSOR_ID:
			case CONFIG_SEC_RULE_INHERITANCE:
			case CONFIG_SEC_RULE_PERF_TIME:
			case CONFIG_SEC_STREAM_IN_BODY_INSPECTION:
			case CONFIG_SEC_STREAM_OUT_BODY_INSPECTION:
			case CONFIG_DIR_PCRE_MATCH_LIMIT:
			case CONFIG_DIR_PCRE_MATCH_LIMIT_RECURSION:
			case CONFIG_DIR_ARGS_LIMIT:
			case CONFIG_DIR_REQ_BODY_JSON_DEPTH_LIMIT:
			case CONFIG_DIR_REQ_BODY:
			case CONFIG_DIR_REQ_BODY_LIMIT:
			case CONFIG_DIR_REQ_BODY_LIMIT_ACTION:
			case CONFIG_DIR_REQ_BODY_NO_FILES_LIMIT:
			case CONFIG_DIR_RES_BODY:
			case CONFIG_DIR_RES_BODY_LIMIT:
			case CONFIG_DIR_RES_BODY_LIMIT_ACTION:
			case CONFIG_DIR_RULE_ENG:
			case CONFIG_DIR_SEC_ACTION:
			case CONFIG_DIR_SEC_DEFAULT_ACTION:
			case CONFIG_SEC_DISABLE_BACKEND_COMPRESS:
			case CONFIG_DIR_SEC_MARKER:
			case CONFIG_DIR_UNICODE_MAP_FILE:
			case CONFIG_SEC_COLLECTION_TIMEOUT:
			case CONFIG_SEC_HTTP_BLKEY:
			case CONFIG_SEC_REMOTE_RULES_FAIL_ACTION:
			case CONFIG_SEC_RULE_REMOVE_BY_ID:
			case CONFIG_SEC_RULE_REMOVE_BY_MSG:
			case CONFIG_SEC_RULE_REMOVE_BY_TAG:
			case CONFIG_SEC_RULE_UPDATE_TARGET_BY_TAG:
			case CONFIG_SEC_RULE_UPDATE_TARGET_BY_MSG:
			case CONFIG_SEC_RULE_UPDATE_TARGET_BY_ID:
			case CONFIG_SEC_RULE_UPDATE_ACTION_BY_ID:
			case CONFIG_UPLOAD_KEEP_FILES:
			case CONFIG_UPLOAD_SAVE_TMP_FILES:
			case CONFIG_UPLOAD_DIR:
			case CONFIG_UPLOAD_FILE_LIMIT:
			case CONFIG_UPLOAD_FILE_MODE:
			case CONFIG_XML_EXTERNAL_ENTITY:
			case CONFIG_DIR_RESPONSE_BODY_MP:
			case CONFIG_DIR_RESPONSE_BODY_MP_CLEAR:
			case CONFIG_DIR_SEC_COOKIE_FORMAT:
			case CONFIG_SEC_COOKIEV0_SEPARATOR:
			case CONFIG_DIR_SEC_DATA_DIR:
			case CONFIG_DIR_SEC_STATUS_ENGINE:
			case CONFIG_DIR_SEC_TMP_DIR:
			case CONFIG_DIR_SEC_RULE:
			case DIRECTIVE_SECRULESCRIPT:
			case INT:
			case STRING:
			case OPERATOR_UNQUOTED_STRING:
			case OPERATOR_QUOTED_STRING:
			case INT_RANGE_VALUE:
			case WS_INT_RANGE:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case XPATH_EXPRESSION:
				enterOuterAlt(_localctx, 2);
				{
				setState(652);
				match(XPATH_EXPRESSION);
				}
				break;
			case COLLECTION_ELEMENT_VALUE:
				enterOuterAlt(_localctx, 3);
				{
				setState(653);
				match(COLLECTION_ELEMENT_VALUE);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Ctl_collection_valueContext extends ParserRuleContext {
		public TerminalNode XPATH_EXPRESSION() { return getToken(SecLangParser.XPATH_EXPRESSION, 0); }
		public TerminalNode COLLECTION_ELEMENT_VALUE() { return getToken(SecLangParser.COLLECTION_ELEMENT_VALUE, 0); }
		public Ctl_collection_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ctl_collection_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterCtl_collection_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitCtl_collection_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitCtl_collection_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ctl_collection_valueContext ctl_collection_value() throws RecognitionException {
		Ctl_collection_valueContext _localctx = new Ctl_collection_valueContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_ctl_collection_value);
		try {
			setState(659);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EOF:
			case QUOTE:
			case SINGLE_QUOTE:
			case COMMA:
			case HASH:
			case CONFIG_COMPONENT_SIG:
			case CONFIG_SEC_SERVER_SIG:
			case CONFIG_SEC_WEB_APP_ID:
			case CONFIG_SEC_CACHE_TRANSFORMATIONS:
			case CONFIG_SEC_CHROOT_DIR:
			case CONFIG_CONN_ENGINE:
			case CONFIG_SEC_HASH_ENGINE:
			case CONFIG_SEC_HASH_KEY:
			case CONFIG_SEC_HASH_PARAM:
			case CONFIG_SEC_HASH_METHOD_RX:
			case CONFIG_SEC_HASH_METHOD_PM:
			case CONFIG_CONTENT_INJECTION:
			case CONFIG_SEC_ARGUMENT_SEPARATOR:
			case CONFIG_DIR_AUDIT_DIR:
			case CONFIG_DIR_AUDIT_DIR_MOD:
			case CONFIG_DIR_AUDIT_ENG:
			case CONFIG_DIR_AUDIT_FILE_MODE:
			case CONFIG_DIR_AUDIT_LOG2:
			case CONFIG_DIR_AUDIT_LOG:
			case CONFIG_DIR_AUDIT_LOG_FMT:
			case CONFIG_DIR_AUDIT_LOG_P:
			case CONFIG_DIR_AUDIT_STS:
			case CONFIG_DIR_AUDIT_TYPE:
			case CONFIG_DIR_DEBUG_LOG:
			case CONFIG_DIR_DEBUG_LVL:
			case CONFIG_DIR_GEO_DB:
			case CONFIG_DIR_GSB_DB:
			case CONFIG_SEC_GUARDIAN_LOG:
			case CONFIG_SEC_INTERCEPT_ON_ERROR:
			case CONFIG_SEC_CONN_R_STATE_LIMIT:
			case CONFIG_SEC_CONN_W_STATE_LIMIT:
			case CONFIG_SEC_SENSOR_ID:
			case CONFIG_SEC_RULE_INHERITANCE:
			case CONFIG_SEC_RULE_PERF_TIME:
			case CONFIG_SEC_STREAM_IN_BODY_INSPECTION:
			case CONFIG_SEC_STREAM_OUT_BODY_INSPECTION:
			case CONFIG_DIR_PCRE_MATCH_LIMIT:
			case CONFIG_DIR_PCRE_MATCH_LIMIT_RECURSION:
			case CONFIG_DIR_ARGS_LIMIT:
			case CONFIG_DIR_REQ_BODY_JSON_DEPTH_LIMIT:
			case CONFIG_DIR_REQ_BODY:
			case CONFIG_DIR_REQ_BODY_LIMIT:
			case CONFIG_DIR_REQ_BODY_LIMIT_ACTION:
			case CONFIG_DIR_REQ_BODY_NO_FILES_LIMIT:
			case CONFIG_DIR_RES_BODY:
			case CONFIG_DIR_RES_BODY_LIMIT:
			case CONFIG_DIR_RES_BODY_LIMIT_ACTION:
			case CONFIG_DIR_RULE_ENG:
			case CONFIG_DIR_SEC_ACTION:
			case CONFIG_DIR_SEC_DEFAULT_ACTION:
			case CONFIG_SEC_DISABLE_BACKEND_COMPRESS:
			case CONFIG_DIR_SEC_MARKER:
			case CONFIG_DIR_UNICODE_MAP_FILE:
			case CONFIG_SEC_COLLECTION_TIMEOUT:
			case CONFIG_SEC_HTTP_BLKEY:
			case CONFIG_SEC_REMOTE_RULES_FAIL_ACTION:
			case CONFIG_SEC_RULE_REMOVE_BY_ID:
			case CONFIG_SEC_RULE_REMOVE_BY_MSG:
			case CONFIG_SEC_RULE_REMOVE_BY_TAG:
			case CONFIG_SEC_RULE_UPDATE_TARGET_BY_TAG:
			case CONFIG_SEC_RULE_UPDATE_TARGET_BY_MSG:
			case CONFIG_SEC_RULE_UPDATE_TARGET_BY_ID:
			case CONFIG_SEC_RULE_UPDATE_ACTION_BY_ID:
			case CONFIG_UPLOAD_KEEP_FILES:
			case CONFIG_UPLOAD_SAVE_TMP_FILES:
			case CONFIG_UPLOAD_DIR:
			case CONFIG_UPLOAD_FILE_LIMIT:
			case CONFIG_UPLOAD_FILE_MODE:
			case CONFIG_XML_EXTERNAL_ENTITY:
			case CONFIG_DIR_RESPONSE_BODY_MP:
			case CONFIG_DIR_RESPONSE_BODY_MP_CLEAR:
			case CONFIG_DIR_SEC_COOKIE_FORMAT:
			case CONFIG_SEC_COOKIEV0_SEPARATOR:
			case CONFIG_DIR_SEC_DATA_DIR:
			case CONFIG_DIR_SEC_STATUS_ENGINE:
			case CONFIG_DIR_SEC_TMP_DIR:
			case CONFIG_DIR_SEC_RULE:
			case DIRECTIVE_SECRULESCRIPT:
			case INT:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case XPATH_EXPRESSION:
				enterOuterAlt(_localctx, 2);
				{
				setState(657);
				match(XPATH_EXPRESSION);
				}
				break;
			case COLLECTION_ELEMENT_VALUE:
				enterOuterAlt(_localctx, 3);
				{
				setState(658);
				match(COLLECTION_ELEMENT_VALUE);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Setvar_actionContext extends ParserRuleContext {
		public Col_nameContext col_name() {
			return getRuleContext(Col_nameContext.class,0);
		}
		public TerminalNode DOT() { return getToken(SecLangParser.DOT, 0); }
		public Setvar_stmtContext setvar_stmt() {
			return getRuleContext(Setvar_stmtContext.class,0);
		}
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public Var_assignmentContext var_assignment() {
			return getRuleContext(Var_assignmentContext.class,0);
		}
		public Setvar_actionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_setvar_action; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterSetvar_action(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitSetvar_action(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitSetvar_action(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Setvar_actionContext setvar_action() throws RecognitionException {
		Setvar_actionContext _localctx = new Setvar_actionContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_setvar_action);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(661);
			col_name();
			setState(662);
			match(DOT);
			setState(663);
			setvar_stmt();
			setState(664);
			assignment();
			setState(665);
			var_assignment();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Col_nameContext extends ParserRuleContext {
		public TerminalNode COLLECTION_NAME_SETVAR() { return getToken(SecLangParser.COLLECTION_NAME_SETVAR, 0); }
		public Col_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_col_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterCol_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitCol_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitCol_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Col_nameContext col_name() throws RecognitionException {
		Col_nameContext _localctx = new Col_nameContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_col_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(667);
			match(COLLECTION_NAME_SETVAR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Setvar_stmtContext extends ParserRuleContext {
		public List<TerminalNode> COLLECTION_ELEMENT() { return getTokens(SecLangParser.COLLECTION_ELEMENT); }
		public TerminalNode COLLECTION_ELEMENT(int i) {
			return getToken(SecLangParser.COLLECTION_ELEMENT, i);
		}
		public List<TerminalNode> COLLECTION_WITH_MACRO() { return getTokens(SecLangParser.COLLECTION_WITH_MACRO); }
		public TerminalNode COLLECTION_WITH_MACRO(int i) {
			return getToken(SecLangParser.COLLECTION_WITH_MACRO, i);
		}
		public List<TerminalNode> MACRO_EXPANSION() { return getTokens(SecLangParser.MACRO_EXPANSION); }
		public TerminalNode MACRO_EXPANSION(int i) {
			return getToken(SecLangParser.MACRO_EXPANSION, i);
		}
		public Setvar_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_setvar_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterSetvar_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitSetvar_stmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitSetvar_stmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Setvar_stmtContext setvar_stmt() throws RecognitionException {
		Setvar_stmtContext _localctx = new Setvar_stmtContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_setvar_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(672); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(672);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case COLLECTION_ELEMENT:
					{
					setState(669);
					match(COLLECTION_ELEMENT);
					}
					break;
				case COLLECTION_WITH_MACRO:
					{
					setState(670);
					match(COLLECTION_WITH_MACRO);
					setState(671);
					match(MACRO_EXPANSION);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(674); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==COLLECTION_ELEMENT || _la==COLLECTION_WITH_MACRO );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AssignmentContext extends ParserRuleContext {
		public TerminalNode EQUAL() { return getToken(SecLangParser.EQUAL, 0); }
		public TerminalNode EQUALS_PLUS() { return getToken(SecLangParser.EQUALS_PLUS, 0); }
		public TerminalNode EQUALS_MINUS() { return getToken(SecLangParser.EQUALS_MINUS, 0); }
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitAssignment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_assignment);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(676);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 104L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Var_assignmentContext extends ParserRuleContext {
		public TerminalNode VAR_ASSIGNMENT() { return getToken(SecLangParser.VAR_ASSIGNMENT, 0); }
		public Var_assignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_assignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterVar_assignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitVar_assignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitVar_assignment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Var_assignmentContext var_assignment() throws RecognitionException {
		Var_assignmentContext _localctx = new Var_assignmentContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_var_assignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(678);
			match(VAR_ASSIGNMENT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Ctl_idContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(SecLangParser.INT, 0); }
		public TerminalNode IDENT() { return getToken(SecLangParser.IDENT, 0); }
		public Ctl_idContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ctl_id; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).enterCtl_id(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SecLangParserListener ) ((SecLangParserListener)listener).exitCtl_id(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SecLangParserVisitor ) return ((SecLangParserVisitor<? extends T>)visitor).visitCtl_id(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ctl_idContext ctl_id() throws RecognitionException {
		Ctl_idContext _localctx = new Ctl_idContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_ctl_id);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(680);
			_la = _input.LA(1);
			if ( !(_la==IDENT || _la==INT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u010e\u02ab\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007"+
		"\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007"+
		"\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007"+
		"\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007"+
		"\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007"+
		"\"\u0002#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007"+
		"\'\u0002(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007"+
		",\u0002-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u0007"+
		"1\u00022\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u0007"+
		"6\u00027\u00077\u00028\u00078\u00029\u00079\u0002:\u0007:\u0002;\u0007"+
		";\u0002<\u0007<\u0002=\u0007=\u0002>\u0007>\u0002?\u0007?\u0002@\u0007"+
		"@\u0002A\u0007A\u0002B\u0007B\u0002C\u0007C\u0002D\u0007D\u0002E\u0007"+
		"E\u0001\u0000\u0005\u0000\u008e\b\u0000\n\u0000\f\u0000\u0091\t\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0001\u0003\u0001\u0096\b\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001\u009c\b\u0001\u0001\u0001"+
		"\u0003\u0001\u009f\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001"+
		"\u00a4\b\u0001\u0001\u0001\u0003\u0001\u00a7\b\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001\u00ae\b\u0001\u0001"+
		"\u0001\u0003\u0001\u00b1\b\u0001\u0001\u0001\u0001\u0001\u0004\u0001\u00b5"+
		"\b\u0001\u000b\u0001\f\u0001\u00b6\u0001\u0001\u0003\u0001\u00ba\b\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001\u00c0\b\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0003\u0001\u00c8\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0003\u0001\u00cf\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001\u00d8\b\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0003\u0001\u00e1\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0003\u0001\u00ec\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0003\u0001\u00f3\b\u0001\u0001\u0001\u0001\u0001\u0003\u0001"+
		"\u00f7\b\u0001\u0001\u0002\u0004\u0002\u00fa\b\u0002\u000b\u0002\f\u0002"+
		"\u00fb\u0001\u0002\u0003\u0002\u00ff\b\u0002\u0001\u0003\u0001\u0003\u0003"+
		"\u0003\u0103\b\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0003\u0005\u0123\b\u0005\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0003\u0006\u0129\b\u0006\u0001\u0007\u0001\u0007\u0001"+
		"\b\u0001\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001"+
		"\f\u0001\f\u0003\f\u0137\b\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u0010"+
		"\u0001\u0010\u0001\u0011\u0001\u0011\u0003\u0011\u0147\b\u0011\u0001\u0012"+
		"\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u014e\b\u0013"+
		"\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0005\u0017\u015a\b\u0017"+
		"\n\u0017\f\u0017\u015d\t\u0017\u0001\u0017\u0001\u0017\u0001\u0018\u0001"+
		"\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u001a\u0001"+
		"\u001a\u0003\u001a\u0169\b\u001a\u0001\u001b\u0001\u001b\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0003\u001c\u0184\b\u001c\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003"+
		"\u001d\u018c\b\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u0196\b\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u019c\b\u001d\u0003"+
		"\u001d\u019e\b\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0003\u001e\u01a3"+
		"\b\u001e\u0001\u001f\u0001\u001f\u0001 \u0001 \u0003 \u01a9\b \u0001 "+
		"\u0001 \u0001 \u0003 \u01ae\b \u0001 \u0001 \u0001 \u0001 \u0001 \u0001"+
		" \u0001 \u0003 \u01b7\b \u0001!\u0001!\u0001\"\u0001\"\u0001\"\u0005\""+
		"\u01be\b\"\n\"\f\"\u01c1\t\"\u0001\"\u0001\"\u0003\"\u01c5\b\"\u0001\""+
		"\u0001\"\u0005\"\u01c9\b\"\n\"\f\"\u01cc\t\"\u0001\"\u0001\"\u0003\"\u01d0"+
		"\b\"\u0005\"\u01d2\b\"\n\"\f\"\u01d5\t\"\u0001\"\u0001\"\u0003\"\u01d9"+
		"\b\"\u0001#\u0001#\u0001$\u0001$\u0001%\u0003%\u01e0\b%\u0001%\u0003%"+
		"\u01e3\b%\u0001%\u0003%\u01e6\b%\u0001%\u0001%\u0003%\u01ea\b%\u0001%"+
		"\u0001%\u0003%\u01ee\b%\u0001%\u0003%\u01f1\b%\u0001%\u0001%\u0003%\u01f5"+
		"\b%\u0005%\u01f7\b%\n%\f%\u01fa\t%\u0001&\u0003&\u01fd\b&\u0001&\u0003"+
		"&\u0200\b&\u0001&\u0003&\u0203\b&\u0001&\u0001&\u0003&\u0207\b&\u0001"+
		"&\u0001&\u0003&\u020b\b&\u0001&\u0003&\u020e\b&\u0001&\u0001&\u0003&\u0212"+
		"\b&\u0005&\u0214\b&\n&\f&\u0217\t&\u0001\'\u0001\'\u0001(\u0001(\u0001"+
		"(\u0001(\u0003(\u021f\b(\u0003(\u0221\b(\u0001)\u0001)\u0001*\u0001*\u0001"+
		"+\u0001+\u0001,\u0001,\u0001-\u0001-\u0001-\u0001-\u0005-\u022f\b-\n-"+
		"\f-\u0232\t-\u0001-\u0001-\u0001.\u0001.\u0001.\u0003.\u0239\b.\u0001"+
		".\u0003.\u023c\b.\u0001.\u0001.\u0001.\u0001.\u0001.\u0001.\u0001.\u0001"+
		".\u0001.\u0001.\u0003.\u0248\b.\u0001/\u0001/\u0001/\u0003/\u024d\b/\u0001"+
		"0\u00010\u00011\u00011\u00012\u00012\u00013\u00013\u00013\u00013\u0001"+
		"3\u00033\u025a\b3\u00014\u00014\u00014\u00014\u00014\u00014\u00014\u0001"+
		"4\u00034\u0264\b4\u00015\u00015\u00016\u00016\u00017\u00017\u00018\u0001"+
		"8\u00019\u00019\u00019\u00019\u00019\u00019\u00019\u00019\u00019\u0003"+
		"9\u0277\b9\u0001:\u0001:\u0001:\u0001:\u0001:\u0001:\u0001:\u0001:\u0001"+
		":\u0001:\u0001:\u0003:\u0284\b:\u0001;\u0001;\u0001<\u0001<\u0001=\u0001"+
		"=\u0001>\u0001>\u0001>\u0003>\u028f\b>\u0001?\u0001?\u0001?\u0003?\u0294"+
		"\b?\u0001@\u0001@\u0001@\u0001@\u0001@\u0001@\u0001A\u0001A\u0001B\u0001"+
		"B\u0001B\u0004B\u02a1\bB\u000bB\fB\u02a2\u0001C\u0001C\u0001D\u0001D\u0001"+
		"E\u0001E\u0001E\u0000\u0000F\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"+
		"\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPR"+
		"TVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a\u0000\u000f"+
		"\u0001\u0001\u0103\u0103\b\u0000\u0083\u008b\u0096\u00a7\u00a9\u00af\u00b2"+
		"\u00b2\u00b4\u00b4\u00b6\u00b7\u00b9\u00b9\u00d1\u00d8\u0003\u0000\u00e1"+
		"\u00e1\u00e9\u00e9\u00f1\u00f1\u0003\u0000\u008c\u0095\u00c1\u00c5\u00e3"+
		"\u00e3\u0001\u0000Y}\u0002\u0000UUWW\u0005\u0000\u001b\u001b\u001e\u001e"+
		",,..:;\u0005\u0000\u001d\u001d\u001f\u001f4479CC\u0001\u0000>?\b\u0000"+
		"\u001c\u001c!!--/023==ABDJ\u0002\u0000OORR\u0001\u0000MN\u0001\u0000\""+
		"+\u0002\u0000\u0003\u0003\u0005\u0006\u0001\u0000\u00e2\u00e3\u02f1\u0000"+
		"\u008f\u0001\u0000\u0000\u0000\u0002\u00f6\u0001\u0000\u0000\u0000\u0004"+
		"\u00f9\u0001\u0000\u0000\u0000\u0006\u0100\u0001\u0000\u0000\u0000\b\u0104"+
		"\u0001\u0000\u0000\u0000\n\u0122\u0001\u0000\u0000\u0000\f\u0128\u0001"+
		"\u0000\u0000\u0000\u000e\u012a\u0001\u0000\u0000\u0000\u0010\u012c\u0001"+
		"\u0000\u0000\u0000\u0012\u012e\u0001\u0000\u0000\u0000\u0014\u0130\u0001"+
		"\u0000\u0000\u0000\u0016\u0132\u0001\u0000\u0000\u0000\u0018\u0136\u0001"+
		"\u0000\u0000\u0000\u001a\u0138\u0001\u0000\u0000\u0000\u001c\u013c\u0001"+
		"\u0000\u0000\u0000\u001e\u0140\u0001\u0000\u0000\u0000 \u0142\u0001\u0000"+
		"\u0000\u0000\"\u0146\u0001\u0000\u0000\u0000$\u0148\u0001\u0000\u0000"+
		"\u0000&\u014d\u0001\u0000\u0000\u0000(\u014f\u0001\u0000\u0000\u0000*"+
		"\u0151\u0001\u0000\u0000\u0000,\u0153\u0001\u0000\u0000\u0000.\u0155\u0001"+
		"\u0000\u0000\u00000\u0160\u0001\u0000\u0000\u00002\u0164\u0001\u0000\u0000"+
		"\u00004\u0168\u0001\u0000\u0000\u00006\u016a\u0001\u0000\u0000\u00008"+
		"\u0183\u0001\u0000\u0000\u0000:\u019d\u0001\u0000\u0000\u0000<\u01a2\u0001"+
		"\u0000\u0000\u0000>\u01a4\u0001\u0000\u0000\u0000@\u01b6\u0001\u0000\u0000"+
		"\u0000B\u01b8\u0001\u0000\u0000\u0000D\u01d8\u0001\u0000\u0000\u0000F"+
		"\u01da\u0001\u0000\u0000\u0000H\u01dc\u0001\u0000\u0000\u0000J\u01df\u0001"+
		"\u0000\u0000\u0000L\u01fc\u0001\u0000\u0000\u0000N\u0218\u0001\u0000\u0000"+
		"\u0000P\u0220\u0001\u0000\u0000\u0000R\u0222\u0001\u0000\u0000\u0000T"+
		"\u0224\u0001\u0000\u0000\u0000V\u0226\u0001\u0000\u0000\u0000X\u0228\u0001"+
		"\u0000\u0000\u0000Z\u022a\u0001\u0000\u0000\u0000\\\u0247\u0001\u0000"+
		"\u0000\u0000^\u024c\u0001\u0000\u0000\u0000`\u024e\u0001\u0000\u0000\u0000"+
		"b\u0250\u0001\u0000\u0000\u0000d\u0252\u0001\u0000\u0000\u0000f\u0259"+
		"\u0001\u0000\u0000\u0000h\u0263\u0001\u0000\u0000\u0000j\u0265\u0001\u0000"+
		"\u0000\u0000l\u0267\u0001\u0000\u0000\u0000n\u0269\u0001\u0000\u0000\u0000"+
		"p\u026b\u0001\u0000\u0000\u0000r\u0276\u0001\u0000\u0000\u0000t\u0283"+
		"\u0001\u0000\u0000\u0000v\u0285\u0001\u0000\u0000\u0000x\u0287\u0001\u0000"+
		"\u0000\u0000z\u0289\u0001\u0000\u0000\u0000|\u028e\u0001\u0000\u0000\u0000"+
		"~\u0293\u0001\u0000\u0000\u0000\u0080\u0295\u0001\u0000\u0000\u0000\u0082"+
		"\u029b\u0001\u0000\u0000\u0000\u0084\u02a0\u0001\u0000\u0000\u0000\u0086"+
		"\u02a4\u0001\u0000\u0000\u0000\u0088\u02a6\u0001\u0000\u0000\u0000\u008a"+
		"\u02a8\u0001\u0000\u0000\u0000\u008c\u008e\u0003\u0002\u0001\u0000\u008d"+
		"\u008c\u0001\u0000\u0000\u0000\u008e\u0091\u0001\u0000\u0000\u0000\u008f"+
		"\u008d\u0001\u0000\u0000\u0000\u008f\u0090\u0001\u0000\u0000\u0000\u0090"+
		"\u0092\u0001\u0000\u0000\u0000\u0091\u008f\u0001\u0000\u0000\u0000\u0092"+
		"\u0093\u0005\u0000\u0000\u0001\u0093\u0001\u0001\u0000\u0000\u0000\u0094"+
		"\u0096\u0003\u0004\u0002\u0000\u0095\u0094\u0001\u0000\u0000\u0000\u0095"+
		"\u0096\u0001\u0000\u0000\u0000\u0096\u0097\u0001\u0000\u0000\u0000\u0097"+
		"\u0098\u0003\b\u0004\u0000\u0098\u0099\u0003J%\u0000\u0099\u009b\u0003"+
		"@ \u0000\u009a\u009c\u0003Z-\u0000\u009b\u009a\u0001\u0000\u0000\u0000"+
		"\u009b\u009c\u0001\u0000\u0000\u0000\u009c\u00f7\u0001\u0000\u0000\u0000"+
		"\u009d\u009f\u0003\u0004\u0002\u0000\u009e\u009d\u0001\u0000\u0000\u0000"+
		"\u009e\u009f\u0001\u0000\u0000\u0000\u009f\u00a0\u0001\u0000\u0000\u0000"+
		"\u00a0\u00a1\u0003\u0012\t\u0000\u00a1\u00a3\u0003\u0014\n\u0000\u00a2"+
		"\u00a4\u0003Z-\u0000\u00a3\u00a2\u0001\u0000\u0000\u0000\u00a3\u00a4\u0001"+
		"\u0000\u0000\u0000\u00a4\u00f7\u0001\u0000\u0000\u0000\u00a5\u00a7\u0003"+
		"\u0004\u0002\u0000\u00a6\u00a5\u0001\u0000\u0000\u0000\u00a6\u00a7\u0001"+
		"\u0000\u0000\u0000\u00a7\u00a8\u0001\u0000\u0000\u0000\u00a8\u00a9\u0003"+
		"\u0012\t\u0000\u00a9\u00aa\u0005\u0001\u0000\u0000\u00aa\u00ab\u0003\u0014"+
		"\n\u0000\u00ab\u00ad\u0005\u0001\u0000\u0000\u00ac\u00ae\u0003Z-\u0000"+
		"\u00ad\u00ac\u0001\u0000\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000\u0000"+
		"\u00ae\u00f7\u0001\u0000\u0000\u0000\u00af\u00b1\u0003\u0004\u0002\u0000"+
		"\u00b0\u00af\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001\u0000\u0000\u0000"+
		"\u00b1\u00b2\u0001\u0000\u0000\u0000\u00b2\u00b4\u0003\u0016\u000b\u0000"+
		"\u00b3\u00b5\u0003\u0018\f\u0000\u00b4\u00b3\u0001\u0000\u0000\u0000\u00b5"+
		"\u00b6\u0001\u0000\u0000\u0000\u00b6\u00b4\u0001\u0000\u0000\u0000\u00b6"+
		"\u00b7\u0001\u0000\u0000\u0000\u00b7\u00f7\u0001\u0000\u0000\u0000\u00b8"+
		"\u00ba\u0003\u0004\u0002\u0000\u00b9\u00b8\u0001\u0000\u0000\u0000\u00b9"+
		"\u00ba\u0001\u0000\u0000\u0000\u00ba\u00bb\u0001\u0000\u0000\u0000\u00bb"+
		"\u00bc\u0003\"\u0011\u0000\u00bc\u00bd\u0003$\u0012\u0000\u00bd\u00f7"+
		"\u0001\u0000\u0000\u0000\u00be\u00c0\u0003\u0004\u0002\u0000\u00bf\u00be"+
		"\u0001\u0000\u0000\u0000\u00bf\u00c0\u0001\u0000\u0000\u0000\u00c0\u00c1"+
		"\u0001\u0000\u0000\u0000\u00c1\u00c2\u0003\"\u0011\u0000\u00c2\u00c3\u0005"+
		"\u0001\u0000\u0000\u00c3\u00c4\u0003$\u0012\u0000\u00c4\u00c5\u0005\u0001"+
		"\u0000\u0000\u00c5\u00f7\u0001\u0000\u0000\u0000\u00c6\u00c8\u0003\u0004"+
		"\u0002\u0000\u00c7\u00c6\u0001\u0000\u0000\u0000\u00c7\u00c8\u0001\u0000"+
		"\u0000\u0000\u00c8\u00c9\u0001\u0000\u0000\u0000\u00c9\u00ca\u0003&\u0013"+
		"\u0000\u00ca\u00cb\u0003<\u001e\u0000\u00cb\u00cc\u0003L&\u0000\u00cc"+
		"\u00f7\u0001\u0000\u0000\u0000\u00cd\u00cf\u0003\u0004\u0002\u0000\u00ce"+
		"\u00cd\u0001\u0000\u0000\u0000\u00ce\u00cf\u0001\u0000\u0000\u0000\u00cf"+
		"\u00d0\u0001\u0000\u0000\u0000\u00d0\u00d1\u0003&\u0013\u0000\u00d1\u00d2"+
		"\u0005\u0001\u0000\u0000\u00d2\u00d3\u0003<\u001e\u0000\u00d3\u00d4\u0005"+
		"\u0001\u0000\u0000\u00d4\u00d5\u0003L&\u0000\u00d5\u00f7\u0001\u0000\u0000"+
		"\u0000\u00d6\u00d8\u0003\u0004\u0002\u0000\u00d7\u00d6\u0001\u0000\u0000"+
		"\u0000\u00d7\u00d8\u0001\u0000\u0000\u0000\u00d8\u00d9\u0001\u0000\u0000"+
		"\u0000\u00d9\u00da\u0003&\u0013\u0000\u00da\u00db\u0003<\u001e\u0000\u00db"+
		"\u00dc\u0003L&\u0000\u00dc\u00dd\u0005\b\u0000\u0000\u00dd\u00de\u0003"+
		"N\'\u0000\u00de\u00f7\u0001\u0000\u0000\u0000\u00df\u00e1\u0003\u0004"+
		"\u0002\u0000\u00e0\u00df\u0001\u0000\u0000\u0000\u00e0\u00e1\u0001\u0000"+
		"\u0000\u0000\u00e1\u00e2\u0001\u0000\u0000\u0000\u00e2\u00e3\u0003&\u0013"+
		"\u0000\u00e3\u00e4\u0005\u0001\u0000\u0000\u00e4\u00e5\u0003<\u001e\u0000"+
		"\u00e5\u00e6\u0005\u0001\u0000\u0000\u00e6\u00e7\u0003L&\u0000\u00e7\u00e8"+
		"\u0005\b\u0000\u0000\u00e8\u00e9\u0003N\'\u0000\u00e9\u00f7\u0001\u0000"+
		"\u0000\u0000\u00ea\u00ec\u0003\u0004\u0002\u0000\u00eb\u00ea\u0001\u0000"+
		"\u0000\u0000\u00eb\u00ec\u0001\u0000\u0000\u0000\u00ec\u00ed\u0001\u0000"+
		"\u0000\u0000\u00ed\u00ee\u0003(\u0014\u0000\u00ee\u00ef\u0003*\u0015\u0000"+
		"\u00ef\u00f0\u0003Z-\u0000\u00f0\u00f7\u0001\u0000\u0000\u0000\u00f1\u00f3"+
		"\u0003\u0004\u0002\u0000\u00f2\u00f1\u0001\u0000\u0000\u0000\u00f2\u00f3"+
		"\u0001\u0000\u0000\u0000\u00f3\u00f4\u0001\u0000\u0000\u0000\u00f4\u00f7"+
		"\u0003\n\u0005\u0000\u00f5\u00f7\u0003\u0004\u0002\u0000\u00f6\u0095\u0001"+
		"\u0000\u0000\u0000\u00f6\u009e\u0001\u0000\u0000\u0000\u00f6\u00a6\u0001"+
		"\u0000\u0000\u0000\u00f6\u00b0\u0001\u0000\u0000\u0000\u00f6\u00b9\u0001"+
		"\u0000\u0000\u0000\u00f6\u00bf\u0001\u0000\u0000\u0000\u00f6\u00c7\u0001"+
		"\u0000\u0000\u0000\u00f6\u00ce\u0001\u0000\u0000\u0000\u00f6\u00d7\u0001"+
		"\u0000\u0000\u0000\u00f6\u00e0\u0001\u0000\u0000\u0000\u00f6\u00eb\u0001"+
		"\u0000\u0000\u0000\u00f6\u00f2\u0001\u0000\u0000\u0000\u00f6\u00f5\u0001"+
		"\u0000\u0000\u0000\u00f7\u0003\u0001\u0000\u0000\u0000\u00f8\u00fa\u0003"+
		"\u0006\u0003\u0000\u00f9\u00f8\u0001\u0000\u0000\u0000\u00fa\u00fb\u0001"+
		"\u0000\u0000\u0000\u00fb\u00f9\u0001\u0000\u0000\u0000\u00fb\u00fc\u0001"+
		"\u0000\u0000\u0000\u00fc\u00fe\u0001\u0000\u0000\u0000\u00fd\u00ff\u0007"+
		"\u0000\u0000\u0000\u00fe\u00fd\u0001\u0000\u0000\u0000\u00fe\u00ff\u0001"+
		"\u0000\u0000\u0000\u00ff\u0005\u0001\u0000\u0000\u0000\u0100\u0102\u0005"+
		"\f\u0000\u0000\u0101\u0103\u0005\u0101\u0000\u0000\u0102\u0101\u0001\u0000"+
		"\u0000\u0000\u0102\u0103\u0001\u0000\u0000\u0000\u0103\u0007\u0001\u0000"+
		"\u0000\u0000\u0104\u0105\u0005\u00d9\u0000\u0000\u0105\t\u0001\u0000\u0000"+
		"\u0000\u0106\u0107\u00036\u001b\u0000\u0107\u0108\u00038\u001c\u0000\u0108"+
		"\u0123\u0001\u0000\u0000\u0000\u0109\u010a\u00036\u001b\u0000\u010a\u010b"+
		"\u0005\u0001\u0000\u0000\u010b\u010c\u00038\u001c\u0000\u010c\u010d\u0005"+
		"\u0001\u0000\u0000\u010d\u0123\u0001\u0000\u0000\u0000\u010e\u010f\u0003"+
		"4\u001a\u0000\u010f\u0110\u0003Z-\u0000\u0110\u0123\u0001\u0000\u0000"+
		"\u0000\u0111\u0112\u0003\f\u0006\u0000\u0112\u0113\u0005\u0001\u0000\u0000"+
		"\u0113\u0114\u00038\u001c\u0000\u0114\u0115\u0005\u0001\u0000\u0000\u0115"+
		"\u0123\u0001\u0000\u0000\u0000\u0116\u0117\u0003\u000e\u0007\u0000\u0117"+
		"\u0118\u0005\u0001\u0000\u0000\u0118\u0119\u00038\u001c\u0000\u0119\u011a"+
		"\u0005\u0001\u0000\u0000\u011a\u0123\u0001\u0000\u0000\u0000\u011b\u011c"+
		"\u0003\u0010\b\u0000\u011c\u011d\u00038\u001c\u0000\u011d\u0123\u0001"+
		"\u0000\u0000\u0000\u011e\u011f\u0003,\u0016\u0000\u011f\u0120\u00038\u001c"+
		"\u0000\u0120\u0121\u0003.\u0017\u0000\u0121\u0123\u0001\u0000\u0000\u0000"+
		"\u0122\u0106\u0001\u0000\u0000\u0000\u0122\u0109\u0001\u0000\u0000\u0000"+
		"\u0122\u010e\u0001\u0000\u0000\u0000\u0122\u0111\u0001\u0000\u0000\u0000"+
		"\u0122\u0116\u0001\u0000\u0000\u0000\u0122\u011b\u0001\u0000\u0000\u0000"+
		"\u0122\u011e\u0001\u0000\u0000\u0000\u0123\u000b\u0001\u0000\u0000\u0000"+
		"\u0124\u0129\u0001\u0000\u0000\u0000\u0125\u0129\u0005\u007f\u0000\u0000"+
		"\u0126\u0129\u0005\u0080\u0000\u0000\u0127\u0129\u0005\u0081\u0000\u0000"+
		"\u0128\u0124\u0001\u0000\u0000\u0000\u0128\u0125\u0001\u0000\u0000\u0000"+
		"\u0128\u0126\u0001\u0000\u0000\u0000\u0128\u0127\u0001\u0000\u0000\u0000"+
		"\u0129\r\u0001\u0000\u0000\u0000\u012a\u012b\u0005\u00b3\u0000\u0000\u012b"+
		"\u000f\u0001\u0000\u0000\u0000\u012c\u012d\u0007\u0001\u0000\u0000\u012d"+
		"\u0011\u0001\u0000\u0000\u0000\u012e\u012f\u0005\u00da\u0000\u0000\u012f"+
		"\u0013\u0001\u0000\u0000\u0000\u0130\u0131\u0005\t\u0000\u0000\u0131\u0015"+
		"\u0001\u0000\u0000\u0000\u0132\u0133\u0005\u00ba\u0000\u0000\u0133\u0017"+
		"\u0001\u0000\u0000\u0000\u0134\u0137\u0005\u00e3\u0000\u0000\u0135\u0137"+
		"\u0003\u001c\u000e\u0000\u0136\u0134\u0001\u0000\u0000\u0000\u0136\u0135"+
		"\u0001\u0000\u0000\u0000\u0137\u0019\u0001\u0000\u0000\u0000\u0138\u0139"+
		"\u0005\u0105\u0000\u0000\u0139\u013a\u0005\u0106\u0000\u0000\u013a\u013b"+
		"\u0005\u0105\u0000\u0000\u013b\u001b\u0001\u0000\u0000\u0000\u013c\u013d"+
		"\u0003\u001e\u000f\u0000\u013d\u013e\u0005\u000e\u0000\u0000\u013e\u013f"+
		"\u0003 \u0010\u0000\u013f\u001d\u0001\u0000\u0000\u0000\u0140\u0141\u0005"+
		"\u00e3\u0000\u0000\u0141\u001f\u0001\u0000\u0000\u0000\u0142\u0143\u0005"+
		"\u00e3\u0000\u0000\u0143!\u0001\u0000\u0000\u0000\u0144\u0147\u0005\u00bb"+
		"\u0000\u0000\u0145\u0147\u0005\u00bc\u0000\u0000\u0146\u0144\u0001\u0000"+
		"\u0000\u0000\u0146\u0145\u0001\u0000\u0000\u0000\u0147#\u0001\u0000\u0000"+
		"\u0000\u0148\u0149\u0007\u0002\u0000\u0000\u0149%\u0001\u0000\u0000\u0000"+
		"\u014a\u014e\u0005\u00bf\u0000\u0000\u014b\u014e\u0005\u00be\u0000\u0000"+
		"\u014c\u014e\u0005\u00bd\u0000\u0000\u014d\u014a\u0001\u0000\u0000\u0000"+
		"\u014d\u014b\u0001\u0000\u0000\u0000\u014d\u014c\u0001\u0000\u0000\u0000"+
		"\u014e\'\u0001\u0000\u0000\u0000\u014f\u0150\u0005\u00c0\u0000\u0000\u0150"+
		")\u0001\u0000\u0000\u0000\u0151\u0152\u0005\u00e3\u0000\u0000\u0152+\u0001"+
		"\u0000\u0000\u0000\u0153\u0154\u0005\u0082\u0000\u0000\u0154-\u0001\u0000"+
		"\u0000\u0000\u0155\u0156\u0005\u0001\u0000\u0000\u0156\u015b\u00030\u0018"+
		"\u0000\u0157\u0158\u0005\u0007\u0000\u0000\u0158\u015a\u00030\u0018\u0000"+
		"\u0159\u0157\u0001\u0000\u0000\u0000\u015a\u015d\u0001\u0000\u0000\u0000"+
		"\u015b\u0159\u0001\u0000\u0000\u0000\u015b\u015c\u0001\u0000\u0000\u0000"+
		"\u015c\u015e\u0001\u0000\u0000\u0000\u015d\u015b\u0001\u0000\u0000\u0000"+
		"\u015e\u015f\u0005\u0001\u0000\u0000\u015f/\u0001\u0000\u0000\u0000\u0160"+
		"\u0161\u00032\u0019\u0000\u0161\u0162\u0005\u0004\u0000\u0000\u0162\u0163"+
		"\u00038\u001c\u0000\u01631\u0001\u0000\u0000\u0000\u0164\u0165\u0005\u00db"+
		"\u0000\u0000\u01653\u0001\u0000\u0000\u0000\u0166\u0169\u0005\u00b0\u0000"+
		"\u0000\u0167\u0169\u0005\u00b1\u0000\u0000\u0168\u0166\u0001\u0000\u0000"+
		"\u0000\u0168\u0167\u0001\u0000\u0000\u0000\u01695\u0001\u0000\u0000\u0000"+
		"\u016a\u016b\u0007\u0003\u0000\u0000\u016b7\u0001\u0000\u0000\u0000\u016c"+
		"\u0184\u0005\u00e3\u0000\u0000\u016d\u0184\u0003\u001c\u000e\u0000\u016e"+
		"\u0184\u0005\u00ca\u0000\u0000\u016f\u0184\u0005\u00c9\u0000\u0000\u0170"+
		"\u0184\u0005\u00cf\u0000\u0000\u0171\u0184\u0005\u00cb\u0000\u0000\u0172"+
		"\u0184\u0005\u00c8\u0000\u0000\u0173\u0184\u0005\u00ce\u0000\u0000\u0174"+
		"\u0184\u0005\u00df\u0000\u0000\u0175\u0184\u0005\u00c6\u0000\u0000\u0176"+
		"\u0184\u0005\u00d0\u0000\u0000\u0177\u0184\u0005\u00c7\u0000\u0000\u0178"+
		"\u0184\u0005\u00cc\u0000\u0000\u0179\u0184\u0005\u00cd\u0000\u0000\u017a"+
		"\u017b\u0005\t\u0000\u0000\u017b\u0184\u0005\u00e3\u0000\u0000\u017c\u0184"+
		"\u0005\t\u0000\u0000\u017d\u0184\u0005\u00e9\u0000\u0000\u017e\u0184\u0005"+
		"\u00e1\u0000\u0000\u017f\u0184\u0005\u00f1\u0000\u0000\u0180\u0184\u0005"+
		"\u00f5\u0000\u0000\u0181\u0184\u0005~\u0000\u0000\u0182\u0184\u0003:\u001d"+
		"\u0000\u0183\u016c\u0001\u0000\u0000\u0000\u0183\u016d\u0001\u0000\u0000"+
		"\u0000\u0183\u016e\u0001\u0000\u0000\u0000\u0183\u016f\u0001\u0000\u0000"+
		"\u0000\u0183\u0170\u0001\u0000\u0000\u0000\u0183\u0171\u0001\u0000\u0000"+
		"\u0000\u0183\u0172\u0001\u0000\u0000\u0000\u0183\u0173\u0001\u0000\u0000"+
		"\u0000\u0183\u0174\u0001\u0000\u0000\u0000\u0183\u0175\u0001\u0000\u0000"+
		"\u0000\u0183\u0176\u0001\u0000\u0000\u0000\u0183\u0177\u0001\u0000\u0000"+
		"\u0000\u0183\u0178\u0001\u0000\u0000\u0000\u0183\u0179\u0001\u0000\u0000"+
		"\u0000\u0183\u017a\u0001\u0000\u0000\u0000\u0183\u017c\u0001\u0000\u0000"+
		"\u0000\u0183\u017d\u0001\u0000\u0000\u0000\u0183\u017e\u0001\u0000\u0000"+
		"\u0000\u0183\u017f\u0001\u0000\u0000\u0000\u0183\u0180\u0001\u0000\u0000"+
		"\u0000\u0183\u0181\u0001\u0000\u0000\u0000\u0183\u0182\u0001\u0000\u0000"+
		"\u0000\u01849\u0001\u0000\u0000\u0000\u0185\u018c\u0003\u008aE\u0000\u0186"+
		"\u0187\u0005\u0002\u0000\u0000\u0187\u0188\u0003v;\u0000\u0188\u0189\u0005"+
		"\u0002\u0000\u0000\u0189\u018c\u0001\u0000\u0000\u0000\u018a\u018c\u0005"+
		"\u00e1\u0000\u0000\u018b\u0185\u0001\u0000\u0000\u0000\u018b\u0186\u0001"+
		"\u0000\u0000\u0000\u018b\u018a\u0001\u0000\u0000\u0000\u018c\u018d\u0001"+
		"\u0000\u0000\u0000\u018d\u018e\u0005\u0012\u0000\u0000\u018e\u019e\u0003"+
		"T*\u0000\u018f\u0196\u0003\u008aE\u0000\u0190\u0191\u0005\u0002\u0000"+
		"\u0000\u0191\u0192\u0003v;\u0000\u0192\u0193\u0005\u0002\u0000\u0000\u0193"+
		"\u0196\u0001\u0000\u0000\u0000\u0194\u0196\u0005\u00e1\u0000\u0000\u0195"+
		"\u018f\u0001\u0000\u0000\u0000\u0195\u0190\u0001\u0000\u0000\u0000\u0195"+
		"\u0194\u0001\u0000\u0000\u0000\u0196\u0197\u0001\u0000\u0000\u0000\u0197"+
		"\u0198\u0005\u0012\u0000\u0000\u0198\u019b\u0003X,\u0000\u0199\u019a\u0005"+
		"\u0004\u0000\u0000\u019a\u019c\u0003~?\u0000\u019b\u0199\u0001\u0000\u0000"+
		"\u0000\u019b\u019c\u0001\u0000\u0000\u0000\u019c\u019e\u0001\u0000\u0000"+
		"\u0000\u019d\u018b\u0001\u0000\u0000\u0000\u019d\u0195\u0001\u0000\u0000"+
		"\u0000\u019e;\u0001\u0000\u0000\u0000\u019f\u01a3\u0005\u00e3\u0000\u0000"+
		"\u01a0\u01a3\u0003\u001c\u000e\u0000\u01a1\u01a3\u0005\u00e9\u0000\u0000"+
		"\u01a2\u019f\u0001\u0000\u0000\u0000\u01a2\u01a0\u0001\u0000\u0000\u0000"+
		"\u01a2\u01a1\u0001\u0000\u0000\u0000\u01a3=\u0001\u0000\u0000\u0000\u01a4"+
		"\u01a5\u0005\n\u0000\u0000\u01a5?\u0001\u0000\u0000\u0000\u01a6\u01a8"+
		"\u0005\u0001\u0000\u0000\u01a7\u01a9\u0003>\u001f\u0000\u01a8\u01a7\u0001"+
		"\u0000\u0000\u0000\u01a8\u01a9\u0001\u0000\u0000\u0000\u01a9\u01aa\u0001"+
		"\u0000\u0000\u0000\u01aa\u01ab\u0005\u00ff\u0000\u0000\u01ab\u01ad\u0003"+
		"B!\u0000\u01ac\u01ae\u0003D\"\u0000\u01ad\u01ac\u0001\u0000\u0000\u0000"+
		"\u01ad\u01ae\u0001\u0000\u0000\u0000\u01ae\u01af\u0001\u0000\u0000\u0000"+
		"\u01af\u01b0\u0005\u0001\u0000\u0000\u01b0\u01b7\u0001\u0000\u0000\u0000"+
		"\u01b1\u01b2\u0005\u0001\u0000\u0000\u01b2\u01b3\u0003D\"\u0000\u01b3"+
		"\u01b4\u0005\u0001\u0000\u0000\u01b4\u01b7\u0001\u0000\u0000\u0000\u01b5"+
		"\u01b7\u0003D\"\u0000\u01b6\u01a6\u0001\u0000\u0000\u0000\u01b6\u01b1"+
		"\u0001\u0000\u0000\u0000\u01b6\u01b5\u0001\u0000\u0000\u0000\u01b7A\u0001"+
		"\u0000\u0000\u0000\u01b8\u01b9\u0007\u0004\u0000\u0000\u01b9C\u0001\u0000"+
		"\u0000\u0000\u01ba\u01d9\u0003R)\u0000\u01bb\u01d9\u0005\u00e9\u0000\u0000"+
		"\u01bc\u01be\u0005\u0107\u0000\u0000\u01bd\u01bc\u0001\u0000\u0000\u0000"+
		"\u01be\u01c1\u0001\u0000\u0000\u0000\u01bf\u01bd\u0001\u0000\u0000\u0000"+
		"\u01bf\u01c0\u0001\u0000\u0000\u0000\u01c0\u01c4\u0001\u0000\u0000\u0000"+
		"\u01c1\u01bf\u0001\u0000\u0000\u0000\u01c2\u01c5\u0005\u0105\u0000\u0000"+
		"\u01c3\u01c5\u0003\u001a\r\u0000\u01c4\u01c2\u0001\u0000\u0000\u0000\u01c4"+
		"\u01c3\u0001\u0000\u0000\u0000\u01c5\u01d3\u0001\u0000\u0000\u0000\u01c6"+
		"\u01ca\u0005\u0007\u0000\u0000\u01c7\u01c9\u0005\u0107\u0000\u0000\u01c8"+
		"\u01c7\u0001\u0000\u0000\u0000\u01c9\u01cc\u0001\u0000\u0000\u0000\u01ca"+
		"\u01c8\u0001\u0000\u0000\u0000\u01ca\u01cb\u0001\u0000\u0000\u0000\u01cb"+
		"\u01cf\u0001\u0000\u0000\u0000\u01cc\u01ca\u0001\u0000\u0000\u0000\u01cd"+
		"\u01d0\u0005\u0105\u0000\u0000\u01ce\u01d0\u0003\u001a\r\u0000\u01cf\u01cd"+
		"\u0001\u0000\u0000\u0000\u01cf\u01ce\u0001\u0000\u0000\u0000\u01d0\u01d2"+
		"\u0001\u0000\u0000\u0000\u01d1\u01c6\u0001\u0000\u0000\u0000\u01d2\u01d5"+
		"\u0001\u0000\u0000\u0000\u01d3\u01d1\u0001\u0000\u0000\u0000\u01d3\u01d4"+
		"\u0001\u0000\u0000\u0000\u01d4\u01d9\u0001\u0000\u0000\u0000\u01d5\u01d3"+
		"\u0001\u0000\u0000\u0000\u01d6\u01d9\u0005\u00fe\u0000\u0000\u01d7\u01d9"+
		"\u0005\u0100\u0000\u0000\u01d8\u01ba\u0001\u0000\u0000\u0000\u01d8\u01bb"+
		"\u0001\u0000\u0000\u0000\u01d8\u01bf\u0001\u0000\u0000\u0000\u01d8\u01d6"+
		"\u0001\u0000\u0000\u0000\u01d8\u01d7\u0001\u0000\u0000\u0000\u01d9E\u0001"+
		"\u0000\u0000\u0000\u01da\u01db\u0005\n\u0000\u0000\u01dbG\u0001\u0000"+
		"\u0000\u0000\u01dc\u01dd\u0005X\u0000\u0000\u01ddI\u0001\u0000\u0000\u0000"+
		"\u01de\u01e0\u0005\u0001\u0000\u0000\u01df\u01de\u0001\u0000\u0000\u0000"+
		"\u01df\u01e0\u0001\u0000\u0000\u0000\u01e0\u01e2\u0001\u0000\u0000\u0000"+
		"\u01e1\u01e3\u0003F#\u0000\u01e2\u01e1\u0001\u0000\u0000\u0000\u01e2\u01e3"+
		"\u0001\u0000\u0000\u0000\u01e3\u01e5\u0001\u0000\u0000\u0000\u01e4\u01e6"+
		"\u0003H$\u0000\u01e5\u01e4\u0001\u0000\u0000\u0000\u01e5\u01e6\u0001\u0000"+
		"\u0000\u0000\u01e6\u01e7\u0001\u0000\u0000\u0000\u01e7\u01e9\u0003P(\u0000"+
		"\u01e8\u01ea\u0005\u0001\u0000\u0000\u01e9\u01e8\u0001\u0000\u0000\u0000"+
		"\u01e9\u01ea\u0001\u0000\u0000\u0000\u01ea\u01f8\u0001\u0000\u0000\u0000"+
		"\u01eb\u01ed\u0005\b\u0000\u0000\u01ec\u01ee\u0005\u0001\u0000\u0000\u01ed"+
		"\u01ec\u0001\u0000\u0000\u0000\u01ed\u01ee\u0001\u0000\u0000\u0000\u01ee"+
		"\u01f0\u0001\u0000\u0000\u0000\u01ef\u01f1\u0003F#\u0000\u01f0\u01ef\u0001"+
		"\u0000\u0000\u0000\u01f0\u01f1\u0001\u0000\u0000\u0000\u01f1\u01f2\u0001"+
		"\u0000\u0000\u0000\u01f2\u01f4\u0003P(\u0000\u01f3\u01f5\u0005\u0001\u0000"+
		"\u0000\u01f4\u01f3\u0001\u0000\u0000\u0000\u01f4\u01f5\u0001\u0000\u0000"+
		"\u0000\u01f5\u01f7\u0001\u0000\u0000\u0000\u01f6\u01eb\u0001\u0000\u0000"+
		"\u0000\u01f7\u01fa\u0001\u0000\u0000\u0000\u01f8\u01f6\u0001\u0000\u0000"+
		"\u0000\u01f8\u01f9\u0001\u0000\u0000\u0000\u01f9K\u0001\u0000\u0000\u0000"+
		"\u01fa\u01f8\u0001\u0000\u0000\u0000\u01fb\u01fd\u0005\u0001\u0000\u0000"+
		"\u01fc\u01fb\u0001\u0000\u0000\u0000\u01fc\u01fd\u0001\u0000\u0000\u0000"+
		"\u01fd\u01ff\u0001\u0000\u0000\u0000\u01fe\u0200\u0003F#\u0000\u01ff\u01fe"+
		"\u0001\u0000\u0000\u0000\u01ff\u0200\u0001\u0000\u0000\u0000\u0200\u0202"+
		"\u0001\u0000\u0000\u0000\u0201\u0203\u0003H$\u0000\u0202\u0201\u0001\u0000"+
		"\u0000\u0000\u0202\u0203\u0001\u0000\u0000\u0000\u0203\u0204\u0001\u0000"+
		"\u0000\u0000\u0204\u0206\u0003P(\u0000\u0205\u0207\u0005\u0001\u0000\u0000"+
		"\u0206\u0205\u0001\u0000\u0000\u0000\u0206\u0207\u0001\u0000\u0000\u0000"+
		"\u0207\u0215\u0001\u0000\u0000\u0000\u0208\u020a\u0005\u0007\u0000\u0000"+
		"\u0209\u020b\u0005\u0001\u0000\u0000\u020a\u0209\u0001\u0000\u0000\u0000"+
		"\u020a\u020b\u0001\u0000\u0000\u0000\u020b\u020d\u0001\u0000\u0000\u0000"+
		"\u020c\u020e\u0003F#\u0000\u020d\u020c\u0001\u0000\u0000\u0000\u020d\u020e"+
		"\u0001\u0000\u0000\u0000\u020e\u020f\u0001\u0000\u0000\u0000\u020f\u0211"+
		"\u0003P(\u0000\u0210\u0212\u0005\u0001\u0000\u0000\u0211\u0210\u0001\u0000"+
		"\u0000\u0000\u0211\u0212\u0001\u0000\u0000\u0000\u0212\u0214\u0001\u0000"+
		"\u0000\u0000\u0213\u0208\u0001\u0000\u0000\u0000\u0214\u0217\u0001\u0000"+
		"\u0000\u0000\u0215\u0213\u0001\u0000\u0000\u0000\u0215\u0216\u0001\u0000"+
		"\u0000\u0000\u0216M\u0001\u0000\u0000\u0000\u0217\u0215\u0001\u0000\u0000"+
		"\u0000\u0218\u0219\u0003P(\u0000\u0219O\u0001\u0000\u0000\u0000\u021a"+
		"\u0221\u0003R)\u0000\u021b\u021e\u0003V+\u0000\u021c\u021d\u0005\u0004"+
		"\u0000\u0000\u021d\u021f\u0003|>\u0000\u021e\u021c\u0001\u0000\u0000\u0000"+
		"\u021e\u021f\u0001\u0000\u0000\u0000\u021f\u0221\u0001\u0000\u0000\u0000"+
		"\u0220\u021a\u0001\u0000\u0000\u0000\u0220\u021b\u0001\u0000\u0000\u0000"+
		"\u0221Q\u0001\u0000\u0000\u0000\u0222\u0223\u0005V\u0000\u0000\u0223S"+
		"\u0001\u0000\u0000\u0000\u0224\u0225\u0005V\u0000\u0000\u0225U\u0001\u0000"+
		"\u0000\u0000\u0226\u0227\u0007\u0005\u0000\u0000\u0227W\u0001\u0000\u0000"+
		"\u0000\u0228\u0229\u0007\u0005\u0000\u0000\u0229Y\u0001\u0000\u0000\u0000"+
		"\u022a\u022b\u0005\u0001\u0000\u0000\u022b\u0230\u0003\\.\u0000\u022c"+
		"\u022d\u0005\u0007\u0000\u0000\u022d\u022f\u0003\\.\u0000\u022e\u022c"+
		"\u0001\u0000\u0000\u0000\u022f\u0232\u0001\u0000\u0000\u0000\u0230\u022e"+
		"\u0001\u0000\u0000\u0000\u0230\u0231\u0001\u0000\u0000\u0000\u0231\u0233"+
		"\u0001\u0000\u0000\u0000\u0232\u0230\u0001\u0000\u0000\u0000\u0233\u0234"+
		"\u0005\u0001\u0000\u0000\u0234[\u0001\u0000\u0000\u0000\u0235\u0236\u0003"+
		"f3\u0000\u0236\u0238\u0005\u0004\u0000\u0000\u0237\u0239\u0005\n\u0000"+
		"\u0000\u0238\u0237\u0001\u0000\u0000\u0000\u0238\u0239\u0001\u0000\u0000"+
		"\u0000\u0239\u023b\u0001\u0000\u0000\u0000\u023a\u023c\u0005\u0003\u0000"+
		"\u0000\u023b\u023a\u0001\u0000\u0000\u0000\u023b\u023c\u0001\u0000\u0000"+
		"\u0000\u023c\u023d\u0001\u0000\u0000\u0000\u023d\u023e\u0003r9\u0000\u023e"+
		"\u0248\u0001\u0000\u0000\u0000\u023f\u0240\u0003f3\u0000\u0240\u0241\u0005"+
		"\u0004\u0000\u0000\u0241\u0242\u0003r9\u0000\u0242\u0248\u0001\u0000\u0000"+
		"\u0000\u0243\u0244\u0005S\u0000\u0000\u0244\u0245\u0005\u0004\u0000\u0000"+
		"\u0245\u0248\u0003z=\u0000\u0246\u0248\u0003^/\u0000\u0247\u0235\u0001"+
		"\u0000\u0000\u0000\u0247\u023f\u0001\u0000\u0000\u0000\u0247\u0243\u0001"+
		"\u0000\u0000\u0000\u0247\u0246\u0001\u0000\u0000\u0000\u0248]\u0001\u0000"+
		"\u0000\u0000\u0249\u024d\u0003`0\u0000\u024a\u024d\u0003b1\u0000\u024b"+
		"\u024d\u0003d2\u0000\u024c\u0249\u0001\u0000\u0000\u0000\u024c\u024a\u0001"+
		"\u0000\u0000\u0000\u024c\u024b\u0001\u0000\u0000\u0000\u024d_\u0001\u0000"+
		"\u0000\u0000\u024e\u024f\u0007\u0006\u0000\u0000\u024fa\u0001\u0000\u0000"+
		"\u0000\u0250\u0251\u0007\u0007\u0000\u0000\u0251c\u0001\u0000\u0000\u0000"+
		"\u0252\u0253\u0005 \u0000\u0000\u0253e\u0001\u0000\u0000\u0000\u0254\u025a"+
		"\u0003h4\u0000\u0255\u025a\u0003j5\u0000\u0256\u025a\u0003l6\u0000\u0257"+
		"\u025a\u0003p8\u0000\u0258\u025a\u0003n7\u0000\u0259\u0254\u0001\u0000"+
		"\u0000\u0000\u0259\u0255\u0001\u0000\u0000\u0000\u0259\u0256\u0001\u0000"+
		"\u0000\u0000\u0259\u0257\u0001\u0000\u0000\u0000\u0259\u0258\u0001\u0000"+
		"\u0000\u0000\u025ag\u0001\u0000\u0000\u0000\u025b\u0264\u0005<\u0000\u0000"+
		"\u025c\u0264\u00051\u0000\u0000\u025d\u0264\u00055\u0000\u0000\u025e\u0264"+
		"\u00056\u0000\u0000\u025f\u0264\u0005@\u0000\u0000\u0260\u0264\u0005K"+
		"\u0000\u0000\u0261\u0264\u0005P\u0000\u0000\u0262\u0264\u0005Q\u0000\u0000"+
		"\u0263\u025b\u0001\u0000\u0000\u0000\u0263\u025c\u0001\u0000\u0000\u0000"+
		"\u0263\u025d\u0001\u0000\u0000\u0000\u0263\u025e\u0001\u0000\u0000\u0000"+
		"\u0263\u025f\u0001\u0000\u0000\u0000\u0263\u0260\u0001\u0000\u0000\u0000"+
		"\u0263\u0261\u0001\u0000\u0000\u0000\u0263\u0262\u0001\u0000\u0000\u0000"+
		"\u0264i\u0001\u0000\u0000\u0000\u0265\u0266\u0007\b\u0000\u0000\u0266"+
		"k\u0001\u0000\u0000\u0000\u0267\u0268\u0007\t\u0000\u0000\u0268m\u0001"+
		"\u0000\u0000\u0000\u0269\u026a\u0007\n\u0000\u0000\u026ao\u0001\u0000"+
		"\u0000\u0000\u026b\u026c\u0007\u000b\u0000\u0000\u026cq\u0001\u0000\u0000"+
		"\u0000\u026d\u0277\u0003t:\u0000\u026e\u026f\u0005\u0002\u0000\u0000\u026f"+
		"\u0270\u0003t:\u0000\u0270\u0271\u0005\u0002\u0000\u0000\u0271\u0277\u0001"+
		"\u0000\u0000\u0000\u0272\u0273\u0005\u0002\u0000\u0000\u0273\u0274\u0003"+
		"v;\u0000\u0274\u0275\u0005\u0002\u0000\u0000\u0275\u0277\u0001\u0000\u0000"+
		"\u0000\u0276\u026d\u0001\u0000\u0000\u0000\u0276\u026e\u0001\u0000\u0000"+
		"\u0000\u0276\u0272\u0001\u0000\u0000\u0000\u0277s\u0001\u0000\u0000\u0000"+
		"\u0278\u0284\u0005\u00e3\u0000\u0000\u0279\u0284\u0003|>\u0000\u027a\u0284"+
		"\u0003\u0080@\u0000\u027b\u027c\u0003x<\u0000\u027c\u027d\u0003\u0086"+
		"C\u0000\u027d\u027e\u00038\u001c\u0000\u027e\u0284\u0001\u0000\u0000\u0000"+
		"\u027f\u0284\u0005\u00e1\u0000\u0000\u0280\u0284\u0005L\u0000\u0000\u0281"+
		"\u0284\u0005\u00e7\u0000\u0000\u0282\u0284\u0005\u00f1\u0000\u0000\u0283"+
		"\u0278\u0001\u0000\u0000\u0000\u0283\u0279\u0001\u0000\u0000\u0000\u0283"+
		"\u027a\u0001\u0000\u0000\u0000\u0283\u027b\u0001\u0000\u0000\u0000\u0283"+
		"\u027f\u0001\u0000\u0000\u0000\u0283\u0280\u0001\u0000\u0000\u0000\u0283"+
		"\u0281\u0001\u0000\u0000\u0000\u0283\u0282\u0001\u0000\u0000\u0000\u0284"+
		"u\u0001\u0000\u0000\u0000\u0285\u0286\u0005\u00f6\u0000\u0000\u0286w\u0001"+
		"\u0000\u0000\u0000\u0287\u0288\u0007\f\u0000\u0000\u0288y\u0001\u0000"+
		"\u0000\u0000\u0289\u028a\u0005T\u0000\u0000\u028a{\u0001\u0000\u0000\u0000"+
		"\u028b\u028f\u0001\u0000\u0000\u0000\u028c\u028f\u0005\u00f3\u0000\u0000"+
		"\u028d\u028f\u0005\u00fa\u0000\u0000\u028e\u028b\u0001\u0000\u0000\u0000"+
		"\u028e\u028c\u0001\u0000\u0000\u0000\u028e\u028d\u0001\u0000\u0000\u0000"+
		"\u028f}\u0001\u0000\u0000\u0000\u0290\u0294\u0001\u0000\u0000\u0000\u0291"+
		"\u0294\u0005\u00f3\u0000\u0000\u0292\u0294\u0005\u00fa\u0000\u0000\u0293"+
		"\u0290\u0001\u0000\u0000\u0000\u0293\u0291\u0001\u0000\u0000\u0000\u0293"+
		"\u0292\u0001\u0000\u0000\u0000\u0294\u007f\u0001\u0000\u0000\u0000\u0295"+
		"\u0296\u0003\u0082A\u0000\u0296\u0297\u0005\u00ec\u0000\u0000\u0297\u0298"+
		"\u0003\u0084B\u0000\u0298\u0299\u0003\u0086C\u0000\u0299\u029a\u0003\u0088"+
		"D\u0000\u029a\u0081\u0001\u0000\u0000\u0000\u029b\u029c\u0005\u00eb\u0000"+
		"\u0000\u029c\u0083\u0001\u0000\u0000\u0000\u029d\u02a1\u0005\u00ed\u0000"+
		"\u0000\u029e\u029f\u0005\u00ee\u0000\u0000\u029f\u02a1\u0005\u00ea\u0000"+
		"\u0000\u02a0\u029d\u0001\u0000\u0000\u0000\u02a0\u029e\u0001\u0000\u0000"+
		"\u0000\u02a1\u02a2\u0001\u0000\u0000\u0000\u02a2\u02a0\u0001\u0000\u0000"+
		"\u0000\u02a2\u02a3\u0001\u0000\u0000\u0000\u02a3\u0085\u0001\u0000\u0000"+
		"\u0000\u02a4\u02a5\u0007\r\u0000\u0000\u02a5\u0087\u0001\u0000\u0000\u0000"+
		"\u02a6\u02a7\u0005\u00ef\u0000\u0000\u02a7\u0089\u0001\u0000\u0000\u0000"+
		"\u02a8\u02a9\u0007\u000e\u0000\u0000\u02a9\u008b\u0001\u0000\u0000\u0000"+
		"J\u008f\u0095\u009b\u009e\u00a3\u00a6\u00ad\u00b0\u00b6\u00b9\u00bf\u00c7"+
		"\u00ce\u00d7\u00e0\u00eb\u00f2\u00f6\u00fb\u00fe\u0102\u0122\u0128\u0136"+
		"\u0146\u014d\u015b\u0168\u0183\u018b\u0195\u019b\u019d\u01a2\u01a8\u01ad"+
		"\u01b6\u01bf\u01c4\u01ca\u01cf\u01d3\u01d8\u01df\u01e2\u01e5\u01e9\u01ed"+
		"\u01f0\u01f4\u01f8\u01fc\u01ff\u0202\u0206\u020a\u020d\u0211\u0215\u021e"+
		"\u0220\u0230\u0238\u023b\u0247\u024c\u0259\u0263\u0276\u0283\u028e\u0293"+
		"\u02a0\u02a2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}