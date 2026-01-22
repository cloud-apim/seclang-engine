package com.cloud.apim.seclang.javadsl;

import com.cloud.apim.seclang.model.SecLangEngineConfig;
import scala.collection.JavaConverters;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for the SecLang engine.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Default configuration
 * JSecLangEngineConfig config = JSecLangEngineConfig.defaultConfig();
 *
 * // Configuration with debug rules
 * JSecLangEngineConfig config = JSecLangEngineConfig.builder()
 *     .debugRule(920100)
 *     .debugRule(920200)
 *     .build();
 * }</pre>
 */
public final class JSecLangEngineConfig {

    private final List<Integer> debugRules;

    private JSecLangEngineConfig(List<Integer> debugRules) {
        this.debugRules = new ArrayList<>(debugRules);
    }

    /**
     * Get the default configuration.
     */
    public static JSecLangEngineConfig defaultConfig() {
        return new JSecLangEngineConfig(new ArrayList<>());
    }

    /**
     * Create a new builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Get the list of rule IDs to debug.
     */
    public List<Integer> getDebugRules() {
        return new ArrayList<>(debugRules);
    }

    /**
     * Convert to Scala SecLangEngineConfig.
     */
    @SuppressWarnings("unchecked")
    SecLangEngineConfig toScala() {
        scala.collection.mutable.Buffer<Object> buffer =
            (scala.collection.mutable.Buffer<Object>)(Object)JavaConverters.asScalaBufferConverter(debugRules).asScala();
        scala.collection.immutable.List<Object> scalaList = buffer.toList();
        return new SecLangEngineConfig((scala.collection.immutable.List<Object>)(Object)scalaList, false, false);
    }

    /**
     * Builder for JSecLangEngineConfig.
     */
    public static final class Builder {
        private final List<Integer> debugRules = new ArrayList<>();

        private Builder() {}

        /**
         * Add a rule ID to debug during evaluation.
         */
        public Builder debugRule(int ruleId) {
            debugRules.add(ruleId);
            return this;
        }

        /**
         * Add multiple rule IDs to debug.
         */
        public Builder debugRules(List<Integer> ruleIds) {
            debugRules.addAll(ruleIds);
            return this;
        }

        /**
         * Build the configuration.
         */
        public JSecLangEngineConfig build() {
            return new JSecLangEngineConfig(debugRules);
        }
    }
}
