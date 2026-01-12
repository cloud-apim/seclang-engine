package com.cloud.apim.seclang.javadsl;

import com.cloud.apim.seclang.model.EngineResult;
import com.cloud.apim.seclang.model.MatchEvent;
import scala.collection.JavaConverters;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Result of a WAF rule evaluation.
 *
 * <p>Contains the disposition (continue or block) and a list of all match events.</p>
 */
public final class JEngineResult {

    private final EngineResult underlying;
    private final JDisposition disposition;
    private final List<JMatchEvent> events;

    private JEngineResult(EngineResult underlying) {
        this.underlying = underlying;
        this.disposition = JDisposition.fromScala(underlying.disposition());
        this.events = JavaConverters.seqAsJavaListConverter(underlying.events()).asJava()
            .stream()
            .map(JMatchEvent::fromScala)
            .collect(Collectors.toList());
    }

    /**
     * Create from a Scala EngineResult.
     */
    public static JEngineResult fromScala(EngineResult result) {
        return new JEngineResult(result);
    }

    /**
     * Get the disposition (continue or block).
     */
    public JDisposition getDisposition() {
        return disposition;
    }

    /**
     * Returns true if the request was blocked.
     */
    public boolean isBlocked() {
        return disposition.isBlock();
    }

    /**
     * Returns true if the request should continue.
     */
    public boolean isContinue() {
        return disposition.isContinue();
    }

    /**
     * Get all match events from the evaluation.
     */
    public List<JMatchEvent> getEvents() {
        return events;
    }

    /**
     * Get match events that have a message.
     */
    public List<JMatchEvent> getEventsWithMessages() {
        return events.stream()
            .filter(e -> e.getMessage().isPresent())
            .collect(Collectors.toList());
    }

    /**
     * Get a display string of the result.
     */
    public String display() {
        return underlying.display();
    }

    /**
     * Print the result to stdout and return this.
     */
    public JEngineResult displayPrintln() {
        System.out.println(display());
        return this;
    }

    @Override
    public String toString() {
        return "EngineResult(" + disposition + ", events=" + events.size() + ")";
    }
}
