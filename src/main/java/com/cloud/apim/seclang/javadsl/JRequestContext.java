package com.cloud.apim.seclang.javadsl;

import com.cloud.apim.seclang.model.ByteString;
import com.cloud.apim.seclang.model.Headers;
import com.cloud.apim.seclang.model.RequestContext;
import scala.Option;
import scala.Some;
import scala.collection.JavaConverters;
import scala.collection.concurrent.TrieMap;

import java.util.*;

/**
 * Java-friendly wrapper for HTTP request context.
 *
 * <p>Use the builder to create instances:</p>
 * <pre>{@code
 * JRequestContext ctx = JRequestContext.builder()
 *     .method("POST")
 *     .uri("/api/users")
 *     .header("Content-Type", "application/json")
 *     .header("Authorization", "Bearer token123")
 *     .body("{\"name\": \"John\"}")
 *     .build();
 * }</pre>
 */
public final class JRequestContext {

    private final RequestContext underlying;

    private JRequestContext(RequestContext underlying) {
        this.underlying = underlying;
    }

    /**
     * Create a new builder for JRequestContext.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Get the underlying Scala RequestContext.
     */
    RequestContext toScala() {
        return underlying;
    }

    /**
     * Create from an existing Scala RequestContext.
     */
    public static JRequestContext fromScala(RequestContext ctx) {
        return new JRequestContext(ctx);
    }

    // Getters

    public String getRequestId() {
        return underlying.requestId();
    }

    public String getMethod() {
        return underlying.method();
    }

    public String getUri() {
        return underlying.uri();
    }

    public Map<String, List<String>> getHeaders() {
        return scalaMapToJava(underlying.headers().underlying());
    }

    public Map<String, List<String>> getCookies() {
        return scalaMapToJava(underlying.cookies());
    }

    public Map<String, List<String>> getQuery() {
        return scalaMapToJava(underlying.query());
    }

    public Optional<String> getBody() {
        Option<ByteString> body = underlying.body();
        if (body.isDefined()) {
            return Optional.of(body.get().utf8String());
        }
        return Optional.empty();
    }

    public Optional<Integer> getStatus() {
        Option<Object> status = underlying.status();
        if (status.isDefined()) {
            return Optional.of((Integer) status.get());
        }
        return Optional.empty();
    }

    public Optional<String> getStatusText() {
        Option<String> statusTxt = underlying.statusTxt();
        if (statusTxt.isDefined()) {
            return Optional.of(statusTxt.get());
        }
        return Optional.empty();
    }

    public long getStartTime() {
        return underlying.startTime();
    }

    public String getRemoteAddr() {
        return underlying.remoteAddr();
    }

    public int getRemotePort() {
        return underlying.remotePort();
    }

    public String getProtocol() {
        return underlying.protocol();
    }

    public boolean isSecure() {
        return underlying.secure();
    }

    public Map<String, String> getVariables() {
        return JavaConverters.mapAsJavaMapConverter(underlying.variables()).asJava();
    }

    public Optional<String> getUser() {
        Option<String> user = underlying.user();
        if (user.isDefined()) {
            return Optional.of(user.get());
        }
        return Optional.empty();
    }

    // Helper methods

    private static Map<String, List<String>> scalaMapToJava(scala.collection.immutable.Map<String, scala.collection.immutable.List<String>> scalaMap) {
        Map<String, List<String>> result = new HashMap<>();
        JavaConverters.mapAsJavaMapConverter(scalaMap).asJava().forEach((key, value) -> {
            result.put(key, JavaConverters.seqAsJavaListConverter(value).asJava());
        });
        return result;
    }

    /**
     * Builder for JRequestContext.
     */
    public static final class Builder {
        private String requestId = System.currentTimeMillis() + "." + String.format("%06d", new Random().nextInt(1000000));
        private String method = "GET";
        private String uri = "/";
        private Map<String, List<String>> headers = new HashMap<>();
        private Map<String, List<String>> cookies = new HashMap<>();
        private Map<String, List<String>> query = new HashMap<>();
        private String body = null;
        private Integer status = null;
        private String statusTxt = null;
        private long startTime = System.currentTimeMillis();
        private String remoteAddr = "0.0.0.0";
        private int remotePort = 1234;
        private String protocol = "HTTP/1.1";
        private boolean secure = false;
        private Map<String, String> variables = new HashMap<>();

        private Builder() {}

        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder headers(Map<String, List<String>> headers) {
            this.headers = new HashMap<>(headers);
            return this;
        }

        public Builder header(String name, String value) {
            this.headers.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
            return this;
        }

        public Builder cookies(Map<String, List<String>> cookies) {
            this.cookies = new HashMap<>(cookies);
            return this;
        }

        public Builder cookie(String name, String value) {
            this.cookies.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
            return this;
        }

        public Builder query(Map<String, List<String>> query) {
            this.query = new HashMap<>(query);
            return this;
        }

        public Builder queryParam(String name, String value) {
            this.query.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder statusText(String statusTxt) {
            this.statusTxt = statusTxt;
            return this;
        }

        public Builder startTime(long startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder remoteAddr(String remoteAddr) {
            this.remoteAddr = remoteAddr;
            return this;
        }

        public Builder remotePort(int remotePort) {
            this.remotePort = remotePort;
            return this;
        }

        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder secure(boolean secure) {
            this.secure = secure;
            return this;
        }

        public Builder variables(Map<String, String> variables) {
            this.variables = new HashMap<>(variables);
            return this;
        }

        public Builder variable(String name, String value) {
            this.variables.put(name, value);
            return this;
        }

        public JRequestContext build() {
            // Convert Java maps to Scala maps
            scala.collection.immutable.Map<String, scala.collection.immutable.List<String>> scalaHeaders = javaMapToScala(headers);
            scala.collection.immutable.Map<String, scala.collection.immutable.List<String>> scalaCookies = javaMapToScala(cookies);
            scala.collection.immutable.Map<String, scala.collection.immutable.List<String>> scalaQuery = javaMapToScala(query);
            scala.collection.immutable.Map<String, String> scalaVariables =
                scala.collection.JavaConverters.mapAsScalaMapConverter(variables).asScala().toMap(
                    scala.Predef.<scala.Tuple2<String, String>>conforms()
                );

            Option<ByteString> scalaBody = body != null ? new Some<>(new ByteString(body)) : Option.empty();
            Option<Object> scalaStatus = status != null ? new Some<>(status) : Option.empty();
            Option<String> scalaStatusTxt = statusTxt != null ? new Some<>(statusTxt) : Option.empty();

            RequestContext ctx = new RequestContext(
                requestId,
                method,
                uri,
                Headers.apply(scalaHeaders),
                scalaCookies,
                scalaQuery,
                scalaBody,
                scalaStatus,
                scalaStatusTxt,
                startTime,
                remoteAddr,
                remotePort,
                protocol,
                secure,
                scalaVariables
            );

            return new JRequestContext(ctx);
        }

        private static scala.collection.immutable.Map<String, scala.collection.immutable.List<String>> javaMapToScala(Map<String, List<String>> javaMap) {
            Map<String, scala.collection.immutable.List<String>> converted = new HashMap<>();
            javaMap.forEach((key, value) -> {
                converted.put(key, scala.collection.JavaConverters.asScalaBufferConverter(value).asScala().toList());
            });
            return scala.collection.JavaConverters.mapAsScalaMapConverter(converted).asScala().toMap(
                scala.Predef.<scala.Tuple2<String, scala.collection.immutable.List<String>>>conforms()
            );
        }
    }
}
