package com.cloud.apim.seclang.javadsl;

import com.cloud.apim.seclang.model.Disposition;
import scala.Option;

import java.util.Optional;

/**
 * Represents the disposition (outcome) of a WAF rule evaluation.
 *
 * <p>A disposition is either:</p>
 * <ul>
 *   <li>{@link #isContinue()} - the request should proceed</li>
 *   <li>{@link #isBlock()} - the request should be blocked</li>
 * </ul>
 */
public final class JDisposition {

    private final boolean blocked;
    private final int status;
    private final String message;
    private final Integer ruleId;

    private JDisposition(boolean blocked, int status, String message, Integer ruleId) {
        this.blocked = blocked;
        this.status = status;
        this.message = message;
        this.ruleId = ruleId;
    }

    /**
     * Create a Continue disposition (request allowed to proceed).
     */
    public static JDisposition continueRequest() {
        return new JDisposition(false, 0, null, null);
    }

    /**
     * Create a Block disposition (request should be blocked).
     *
     * @param status HTTP status code to return
     * @param message optional message describing the block reason
     * @param ruleId optional ID of the rule that triggered the block
     */
    public static JDisposition block(int status, String message, Integer ruleId) {
        return new JDisposition(true, status, message, ruleId);
    }

    /**
     * Create from a Scala Disposition.
     */
    public static JDisposition fromScala(Disposition disposition) {
        if (disposition instanceof Disposition.Continue$) {
            return continueRequest();
        } else if (disposition instanceof Disposition.Block) {
            Disposition.Block block = (Disposition.Block) disposition;
            Option<String> msg = block.msg();
            Option<Object> rid = block.ruleId();
            return new JDisposition(
                true,
                block.status(),
                msg.isDefined() ? msg.get() : null,
                rid.isDefined() ? (Integer) rid.get() : null
            );
        }
        return continueRequest();
    }

    /**
     * Returns true if the request should continue (not blocked).
     */
    public boolean isContinue() {
        return !blocked;
    }

    /**
     * Returns true if the request should be blocked.
     */
    public boolean isBlock() {
        return blocked;
    }

    /**
     * Get the HTTP status code (only meaningful if blocked).
     */
    public int getStatus() {
        return status;
    }

    /**
     * Get the block message, if any.
     */
    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

    /**
     * Get the ID of the rule that triggered the block, if any.
     */
    public Optional<Integer> getRuleId() {
        return Optional.ofNullable(ruleId);
    }

    @Override
    public String toString() {
        if (blocked) {
            return "Block(status=" + status + ", message=" + message + ", ruleId=" + ruleId + ")";
        }
        return "Continue";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JDisposition that = (JDisposition) o;
        if (blocked != that.blocked) return false;
        if (status != that.status) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        return ruleId != null ? ruleId.equals(that.ruleId) : that.ruleId == null;
    }

    @Override
    public int hashCode() {
        int result = (blocked ? 1 : 0);
        result = 31 * result + status;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (ruleId != null ? ruleId.hashCode() : 0);
        return result;
    }
}
