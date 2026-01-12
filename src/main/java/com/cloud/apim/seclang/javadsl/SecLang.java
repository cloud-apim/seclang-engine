package com.cloud.apim.seclang.javadsl;

import com.cloud.apim.seclang.impl.compiler.Compiler;
import com.cloud.apim.seclang.impl.engine.SecLangEngine;
import com.cloud.apim.seclang.impl.parser.AntlrParser;
import com.cloud.apim.seclang.model.CompiledProgram;
import com.cloud.apim.seclang.model.Configuration;
import scala.collection.JavaConverters;
import scala.util.Either;

import java.util.HashMap;
import java.util.Map;

/**
 * Main entry point for the SecLang Java API.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * String rules = "SecRule REQUEST_URI \"@contains /admin\" \"id:1,phase:1,block\"";
 *
 * SecLang.ParseResult result = SecLang.parse(rules);
 * if (result.isSuccess()) {
 *     CompiledProgram program = SecLang.compile(result.getConfiguration());
 *     JSecLangEngine engine = SecLang.engine(
 *         program,
 *         JSecLangEngineConfig.defaultConfig(),
 *         Map.of(),
 *         JSecLangIntegration.defaultIntegration()
 *     );
 *
 *     JRequestContext ctx = JRequestContext.builder()
 *         .method("GET")
 *         .uri("/admin")
 *         .build();
 *
 *     JEngineResult res = engine.evaluate(ctx, List.of(1, 2));
 *     if (res.isBlocked()) {
 *         System.out.println("Blocked: " + res.getDisposition().getMessage());
 *     }
 * }
 * }</pre>
 */
public final class SecLang {

    private SecLang() {

        // Utility class
    }

    /**
     * Result of a parse operation.
     */
    public static final class ParseResult {
        private final Configuration configuration;
        private final String error;

        private ParseResult(Configuration configuration, String error) {
            this.configuration = configuration;
            this.error = error;
        }

        public boolean isSuccess() {
            return configuration != null;
        }

        public boolean isError() {
            return error != null;
        }

        public Configuration getConfiguration() {
            if (configuration == null) {
                throw new IllegalStateException("Parse failed: " + error);
            }
            return configuration;
        }

        public String getError() {
            return error;
        }

        static ParseResult success(Configuration config) {
            return new ParseResult(config, null);
        }

        static ParseResult error(String msg) {
            return new ParseResult(null, msg);
        }
    }

    /**
     * Parse SecLang rules from a string.
     *
     * @param input the SecLang rules as a string
     * @return a ParseResult containing either the Configuration or an error message
     */
    public static ParseResult parse(String input) {
        Either<String, Configuration> result = AntlrParser.parse(input);
        if (result.isRight()) {
            return ParseResult.success(result.right().get());
        } else {
            return ParseResult.error(result.left().get());
        }
    }

    /**
     * Compile a parsed configuration into an executable program.
     *
     * @param configuration the parsed configuration
     * @return a compiled program ready for execution
     */
    public static CompiledProgram compile(Configuration configuration) {
        return Compiler.compile(configuration);
    }

    /**
     * Create a SecLang engine from a compiled program.
     *
     * @param program     the compiled program
     * @param config      the engine configuration
     * @param files       additional data files (for @pmFromFile, @ipMatchFromFile, etc.)
     * @param integration the integration for logging, caching, and auditing
     * @return a SecLang engine ready to evaluate requests
     */
    public static JSecLangEngine engine(
            CompiledProgram program,
            JSecLangEngineConfig config,
            Map<String, String> files,
            JSecLangIntegration integration) {
        scala.collection.immutable.Map<String, String> scalaFiles =
            JavaConverters.mapAsScalaMapConverter(files != null ? files : new HashMap<>()).asScala().toMap(
                scala.Predef.<scala.Tuple2<String, String>>conforms()
            );
        SecLangEngine engine = new SecLangEngine(
            program,
            config.toScala(),
            scalaFiles,
            integration.toScala()
        );
        return new JSecLangEngine(engine);
    }

    /**
     * Create a SecLang engine with default configuration.
     *
     * @param program the compiled program
     * @return a SecLang engine ready to evaluate requests
     */
    public static JSecLangEngine engine(CompiledProgram program) {
        return engine(program, JSecLangEngineConfig.defaultConfig(), new HashMap<>(), JSecLangIntegration.defaultIntegration());
    }

    /**
     * Create a SecLang engine with files but default config and integration.
     *
     * @param program the compiled program
     * @param files   additional data files
     * @return a SecLang engine ready to evaluate requests
     */
    public static JSecLangEngine engine(CompiledProgram program, Map<String, String> files) {
        return engine(program, JSecLangEngineConfig.defaultConfig(), files, JSecLangIntegration.defaultIntegration());
    }

}
