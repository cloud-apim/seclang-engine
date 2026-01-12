package com.cloud.apim.seclang.javadsl;

import com.cloud.apim.seclang.model.MatchEvent;
import scala.Option;

import java.util.Optional;

/**
 * Represents a rule match event during WAF evaluation.
 */
public final class JMatchEvent {

    private final Integer ruleId;
    private final String message;
    private final int phase;
    private final String raw;

    private JMatchEvent(Integer ruleId, String message, int phase, String raw) {
        this.ruleId = ruleId;
        this.message = message;
        this.phase = phase;
        this.raw = raw;
    }

    /**
     * Create from a Scala MatchEvent.
     */
    public static JMatchEvent fromScala(MatchEvent event) {
        Option<Object> rid = event.ruleId();
        Option<String> msg = event.msg();
        return new JMatchEvent(
            rid.isDefined() ? (Integer) rid.get() : null,
            msg.isDefined() ? msg.get() : null,
            event.phase(),
            event.raw()
        );
    }

    /**
     * Get the ID of the rule that matched.
     */
    public Optional<Integer> getRuleId() {
        return Optional.ofNullable(ruleId);
    }

    /**
     * Get the message associated with the match.
     */
    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

    /**
     * Get the phase in which the match occurred.
     */
    public int getPhase() {
        return phase;
    }

    /**
     * Get the raw rule definition.
     */
    public String getRaw() {
        return raw;
    }

    @Override
    public String toString() {
        return "MatchEvent(ruleId=" + ruleId + ", phase=" + phase + ", message=" + message + ")";
    }
}
