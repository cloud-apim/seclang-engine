package com.cloud.apim.seclang.javadsl;

import com.cloud.apim.seclang.model.*;
import scala.Function1;
import scala.Option;
import scala.collection.JavaConverters;
import scala.concurrent.duration.FiniteDuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public abstract Optional<JSecLangPreset> getExternalPreset(String name);

    public abstract Optional<CompiledProgram> getCachedProgram(String key);

    public abstract void putCachedProgram(String key, CompiledProgram program, Duration ttl);

    public abstract void removeCachedProgram(String key);

    /**
     * Audit callback when a rule matches.
     *
     * @param ruleId  the rule ID that matched
     * @param context the request context
     * @param phase   the phase in which the match occurred
     * @param msg     the message from the rule
     * @param logdata additional log data
     */
    public abstract void audit(int ruleId, JRequestContext context, RuntimeState state, int phase, String msg, List<String> logdata);

    /**
     * Get the default integration implementation.
     */
    public static JSecLangIntegration defaultIntegration() {
        return new DefaultJSecLangIntegration(1000, Map.of());
    }

    /**
     * Get the default integration with custom cache size.
     */
    public static JSecLangIntegration defaultIntegration(int maxCacheItems) {
        return new DefaultJSecLangIntegration(maxCacheItems, Map.of());
    }

    public static JSecLangIntegration defaultIntegration(int maxCacheItems, Map<String, JSecLangPreset> presets) {
        return new DefaultJSecLangIntegration(maxCacheItems, presets);
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
        return new WrapperSecLangIntegration(this);
    }

    private static class WrapperSecLangIntegration implements SecLangIntegration {

        private final JSecLangIntegration self;

        public WrapperSecLangIntegration(JSecLangIntegration self) {
            this.self = self;
        }

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
        public Option<SecLangPreset> getExternalPreset(String name) {
            Optional<JSecLangPreset> p = self.getExternalPreset(name);
            if (p.isPresent()) {
                return scala.Some.apply(p.get().toScala());
            } else {
                return scala.None$.empty();
            }
        }

        @Override
        public Option<CompiledProgram> getCachedProgram(String key) {
            Optional<CompiledProgram> p = self.getCachedProgram(key);
            if (p.isPresent()) {
                return scala.Some.apply(p.get());
            } else {
                return scala.None$.empty();
            }
        }

        @Override
        public void putCachedProgram(String key, CompiledProgram program, FiniteDuration ttl) {
            self.putCachedProgram(key, program, Duration.of(ttl.toMillis(), ChronoUnit.MILLIS));
        }

        @Override
        public void removeCachedProgram(String key) {
            self.removeCachedProgram(key);
        }

        @Override
        public void audit(int ruleId, RequestContext context, RuntimeState state, int phase, String msg, scala.collection.immutable.List<String> logdata) {
            self.audit(
                    ruleId,
                    JRequestContext.fromScala(context),
                    state,
                    phase,
                    msg,
                    JavaConverters.seqAsJavaListConverter(logdata).asJava()
            );
        }
    }

    /**
     * Default implementation of JSecLangIntegration.
     */
    private static class DefaultJSecLangIntegration extends JSecLangIntegration {

        private final DefaultSecLangIntegration underlying;

        DefaultJSecLangIntegration(int maxCacheItems, Map<String, JSecLangPreset> presets) {
            scala.collection.immutable.Map<String, SecLangPreset> spresets = JavaConverters.mapAsScalaMapConverter(presets).asScala().toMap(
                    scala.Predef.<scala.Tuple2<String, JSecLangPreset>>conforms()
            ).mapValues(new Function1<JSecLangPreset, SecLangPreset>() {
                public SecLangPreset apply(JSecLangPreset preset) {
                    return preset.toScala();
                }
            });
            this.underlying = new DefaultSecLangIntegration(maxCacheItems, spresets);
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
        public Optional<JSecLangPreset> getExternalPreset(String name) {
            scala.Option<SecLangPreset> opt = underlying.getExternalPreset(name);
            if (opt.isDefined()) {
                return Optional.of(JSecLangPreset.fromScala(opt.get()));
            } else {
                return Optional.empty();
            }
        }

        @Override
        public Optional<CompiledProgram> getCachedProgram(String key) {
            scala.Option<CompiledProgram> opt = underlying.getCachedProgram(key);
            if (opt.isDefined()) {
                return Optional.of(opt.get());
            } else {
                return Optional.empty();
            }
        }

        @Override
        public void putCachedProgram(String key, CompiledProgram program, Duration ttl) {
            underlying.putCachedProgram(key, program, FiniteDuration.apply(ttl.toMillis(), TimeUnit.MILLISECONDS));
        }

        @Override
        public void removeCachedProgram(String key) {
            underlying.removeCachedProgram(key);
        }

        @Override
        public void audit(int ruleId, JRequestContext context, RuntimeState state, int phase, String msg, List<String> logdata) {
            underlying.audit(ruleId, context.toScala(), state, phase, msg, JavaConverters.asScalaBuffer(logdata).toList());
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
            super(maxCacheItems, Map.of());
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
