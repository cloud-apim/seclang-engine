package com.cloud.apim.seclang.javadsl;

import com.cloud.apim.seclang.impl.engine.SecLangEngine;
import com.cloud.apim.seclang.model.EngineResult;
import scala.collection.JavaConverters;
import scala.collection.concurrent.TrieMap;

import java.util.Arrays;
import java.util.List;

/**
 * SecLang WAF engine for evaluating HTTP requests against security rules.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * String rules = "SecRule REQUEST_URI \"@contains /admin\" \"id:1,phase:1,block\"";
 *
 * SecLang.ParseResult result = SecLang.parse(rules);
 * if (result.isSuccess()) {
 *     JSecLangEngine engine = SecLang.engine(result.getConfiguration());
 *
 *     JRequestContext ctx = JRequestContext.builder()
 *         .method("GET")
 *         .uri("/admin/users")
 *         .build();
 *
 *     JEngineResult res = engine.evaluate(ctx);
 *     if (res.isBlocked()) {
 *         System.out.println("Request blocked!");
 *     }
 * }
 * }</pre>
 */
public final class JSecLangEngine {

    private final SecLangEngine underlying;

    JSecLangEngine(SecLangEngine underlying) {
        this.underlying = underlying;
    }

    /**
     * Evaluate a request against the compiled rules.
     *
     * <p>Uses default phases 1 and 2 (request headers and request body).</p>
     *
     * @param ctx the request context to evaluate
     * @return the evaluation result
     */
    public JEngineResult evaluate(JRequestContext ctx) {
        return evaluate(ctx, Arrays.asList(1, 2));
    }

    /**
     * Evaluate a request against the compiled rules for specific phases.
     *
     * <p>WAF phases:</p>
     * <ul>
     *   <li>Phase 1: Request headers</li>
     *   <li>Phase 2: Request body</li>
     *   <li>Phase 3: Response headers</li>
     *   <li>Phase 4: Response body</li>
     *   <li>Phase 5: Logging</li>
     * </ul>
     *
     * @param ctx    the request context to evaluate
     * @param phases the phases to evaluate (e.g., [1, 2] for request processing)
     * @return the evaluation result
     */
    public JEngineResult evaluate(JRequestContext ctx, List<Integer> phases) {
        @SuppressWarnings("unchecked")
        scala.collection.immutable.List<Object> scalaPhases =
            (scala.collection.immutable.List<Object>)(Object)JavaConverters.asScalaBufferConverter(phases).asScala().toList();
        EngineResult result = underlying.evaluate(ctx.toScala(), scalaPhases, scala.None$.empty());
        return JEngineResult.fromScala(result);
    }

    public JEngineResult evaluate(JRequestContext ctx, List<Integer> phases, TrieMap<String, String> txMap) {
        @SuppressWarnings("unchecked")
        scala.collection.immutable.List<Object> scalaPhases =
                (scala.collection.immutable.List<Object>)(Object)JavaConverters.asScalaBufferConverter(phases).asScala().toList();
        EngineResult result = underlying.evaluate(ctx.toScala(), scalaPhases, scala.Some.apply(txMap));
        return JEngineResult.fromScala(result);
    }
}
