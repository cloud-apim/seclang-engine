package com.cloud.apim.seclang.javadsl;

import com.cloud.apim.seclang.impl.factory.SecLangEngineFactory;
import com.cloud.apim.seclang.model.EngineResult;
import scala.collection.JavaConverters;

import java.util.Arrays;
import java.util.List;

/**
 * Factory for evaluating requests against dynamically composed rule sets.
 *
 * <p>The factory supports composing rules from multiple sources:</p>
 * <ul>
 *   <li>Pre-compiled presets using {@code @import_preset preset_name}</li>
 *   <li>Inline SecLang rules</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Create presets
 * Map<String, JSecLangPreset> presets = new HashMap<>();
 * presets.put("no_firefox", JSecLangPreset.withNoFiles("no_firefox",
 *     "SecRule REQUEST_HEADERS:User-Agent \"@pm firefox\" \"id:1,phase:1,block\""));
 * presets.put("health_check", JSecLangPreset.withNoFiles("health_check",
 *     "SecRule REQUEST_URI \"@contains /health\" \"id:2,phase:1,pass\""));
 *
 * // Create factory
 * JSecLangEngineFactory factory = SecLang.factory(presets);
 *
 * // Define rule configuration (can vary per tenant/request)
 * List<String> rulesConfig = Arrays.asList(
 *     "@import_preset no_firefox",
 *     "@import_preset health_check",
 *     "SecRuleEngine On"
 * );
 *
 * // Evaluate request
 * JRequestContext ctx = JRequestContext.builder()
 *     .method("GET")
 *     .uri("/")
 *     .header("User-Agent", "Firefox/128.0")
 *     .build();
 *
 * JEngineResult result = factory.evaluate(rulesConfig, ctx);
 * if (result.isBlocked()) {
 *     System.out.println("Request blocked!");
 * }
 * }</pre>
 */
public final class JSecLangEngineFactory {

    private final SecLangEngineFactory underlying;

    JSecLangEngineFactory(SecLangEngineFactory underlying) {
        this.underlying = underlying;
    }

    /**
     * Evaluate a request against a dynamically composed rule set.
     *
     * <p>Uses default phases 1 and 2 (request headers and request body).</p>
     *
     * @param configs list of rule configurations (preset imports or inline rules)
     * @param ctx     the request context to evaluate
     * @return the evaluation result
     */
    public JEngineResult evaluate(List<String> configs, JRequestContext ctx) {
        return evaluate(configs, ctx, Arrays.asList(1, 2));
    }

    /**
     * Evaluate a request against a dynamically composed rule set for specific phases.
     *
     * <p>Rule configurations can be:</p>
     * <ul>
     *   <li>{@code @import_preset preset_name} - import a pre-compiled preset</li>
     *   <li>Any valid SecLang directive (SecRule, SecAction, SecRuleEngine, etc.)</li>
     * </ul>
     *
     * @param configs list of rule configurations
     * @param ctx     the request context to evaluate
     * @param phases  the phases to evaluate
     * @return the evaluation result
     */
    @SuppressWarnings("unchecked")
    public JEngineResult evaluate(List<String> configs, JRequestContext ctx, List<Integer> phases) {
        scala.collection.immutable.List<String> scalaConfigs =
            JavaConverters.asScalaBufferConverter(configs).asScala().toList();
        scala.collection.immutable.List<Object> scalaPhases =
            (scala.collection.immutable.List<Object>)(Object)JavaConverters.asScalaBufferConverter(phases).asScala().toList();
        EngineResult result = underlying.evaluate(scalaConfigs, ctx.toScala(), scalaPhases);
        return JEngineResult.fromScala(result);
    }
}
