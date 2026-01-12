package com.cloud.apim.seclang.javadsl;

import com.cloud.apim.seclang.model.*;
import scala.Option;
import scala.collection.JavaConverters;
import scala.concurrent.duration.FiniteDuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Integration interface for customizing SecLang engine behavior.
 *
 * <p>Implement this interface to customize logging, caching, and auditing.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Use default implementation
 * JSecLangIntegration integration = JSecLangIntegration.defaultIntegration();
 *
 * // Custom implementation
 * JSecLangIntegration integration = new JSecLangIntegration() {
 *     @Override
 *     public void logDebug(String msg) {
 *         myLogger.debug(msg);
 *     }
 *     // ... other methods
 * };
 * }</pre>
 */
public abstract class JSecLangIntegration {

    /**
     * Log a debug message.
     */
    public abstract void logDebug(String msg);

    /**
     * Log an info message.
     */
    public abstract void logInfo(String msg);

    /**
     * Log an audit message.
     */
    public abstract void logAudit(String msg);

    /**
     * Log an error message.
     */
    public abstract void logError(String msg);

    /**
     * Get environment variables.
     */
    public abstract Map<String, String> getEnv();

    /**
     * Audit callback when a rule matches.
     *
     * @param ruleId  the rule ID that matched
     * @param context the request context
     * @param phase   the phase in which the match occurred
     * @param msg     the message from the rule
     * @param logdata additional log data
     */
    public abstract void audit(int ruleId, JRequestContext context, int phase, String msg, List<String> logdata);

    /**
     * Get the default integration implementation.
     */
    public static JSecLangIntegration defaultIntegration() {
        return new DefaultJSecLangIntegration(1000);
    }

    /**
     * Get the default integration with custom cache size.
     */
    public static JSecLangIntegration defaultIntegration(int maxCacheItems) {
        return new DefaultJSecLangIntegration(maxCacheItems);
    }

    /**
     * Get a no-log integration (logs are silenced).
     */
    public static JSecLangIntegration noLogIntegration() {
        return new NoLogJSecLangIntegration(1000);
    }

    /**
     * Convert to Scala SecLangIntegration.
     */
    SecLangIntegration toScala() {
        JSecLangIntegration self = this;
        return new SecLangIntegration() {
            @Override
            public void logDebug(String msg) {
                self.logDebug(msg);
            }

            @Override
            public void logInfo(String msg) {
                self.logInfo(msg);
            }

            @Override
            public void logAudit(String msg) {
                self.logAudit(msg);
            }

            @Override
            public void logError(String msg) {
                self.logError(msg);
            }

            @Override
            public scala.collection.immutable.Map<String, String> getEnv() {
                return JavaConverters.mapAsScalaMapConverter(self.getEnv()).asScala().toMap(
                    scala.Predef.<scala.Tuple2<String, String>>conforms()
                );
            }

            @Override
            public Option<CompiledProgram> getCachedProgram(String key) {
                return Option.empty();
            }

            @Override
            public void putCachedProgram(String key, CompiledProgram program, FiniteDuration ttl) {
            }

            @Override
            public void removeCachedProgram(String key) {
            }

            @Override
            public void audit(int ruleId, RequestContext context, RuntimeState state, int phase, String msg, scala.collection.immutable.List<String> logdata) {
                self.audit(
                    ruleId,
                    JRequestContext.fromScala(context),
                    phase,
                    msg,
                    JavaConverters.seqAsJavaListConverter(logdata).asJava()
                );
            }
        };
    }

    /**
     * Default implementation of JSecLangIntegration.
     */
    private static class DefaultJSecLangIntegration extends JSecLangIntegration {

        private final DefaultSecLangIntegration underlying;

        DefaultJSecLangIntegration(int maxCacheItems) {
            this.underlying = new DefaultSecLangIntegration(maxCacheItems);
        }

        @Override
        public void logDebug(String msg) {
            System.out.println("[Debug]: " + msg);
        }

        @Override
        public void logInfo(String msg) {
            System.out.println("[Info]: " + msg);
        }

        @Override
        public void logAudit(String msg) {
            System.out.println("[Audit]: " + msg);
        }

        @Override
        public void logError(String msg) {
            System.out.println("[Error]: " + msg);
        }

        @Override
        public Map<String, String> getEnv() {
            return System.getenv();
        }

        @Override
        public void audit(int ruleId, JRequestContext context, int phase, String msg, List<String> logdata) {
        }

        @Override
        SecLangIntegration toScala() {
            return underlying;
        }
    }

    /**
     * No-log implementation of JSecLangIntegration.
     */
    private static class NoLogJSecLangIntegration extends DefaultJSecLangIntegration {

        NoLogJSecLangIntegration(int maxCacheItems) {
            super(maxCacheItems);
        }

        @Override
        public void logDebug(String msg) {
        }

        @Override
        public void logInfo(String msg) {
        }

        @Override
        public void logAudit(String msg) {
        }

        @Override
        public void logError(String msg) {
        }
    }
}
