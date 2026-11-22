package io.github.domainreseller.dna;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** A request the fake HTTP client received. */
public final class RecordedRequest {
    private static final ObjectMapper JSON = new ObjectMapper();

    private final String method;
    private final URI uri;
    private final Map<String, String> headers;
    private final String body;

    RecordedRequest(String method, URI uri, Map<String, String> headers, String body) {
        this.method = method;
        this.uri = uri;
        this.headers = headers;
        this.body = body;
    }

    public String method() {
        return method;
    }

    public URI uri() {
        return uri;
    }

    public String header(String name) {
        return headers.get(name);
    }

    public String body() {
        return body;
    }

    /** The path below /api/v1/, for example {@code domains/info}. */
    public String apiPath() {
        String path = uri.getRawPath();
        return path.startsWith("/api/v1/") ? path.substring("/api/v1/".length()) : path;
    }

    /** The decoded query parameters, in order. */
    public List<Map.Entry<String, String>> queryParameters() {
        List<Map.Entry<String, String>> result = new ArrayList<>();
        String query = uri.getRawQuery();
        if (query == null || query.isEmpty()) {
            return result;
        }
        for (String pair : query.split("&")) {
            int eq = pair.indexOf('=');
            String name = eq < 0 ? pair : pair.substring(0, eq);
            String value = eq < 0 ? "" : pair.substring(eq + 1);
            result.add(new AbstractMap.SimpleEntry<>(decode(name), decode(value)));
        }
        return result;
    }

    /** The first value of a query parameter, or null. */
    public String query(String name) {
        return queryParameters().stream().filter(p -> p.getKey().equals(name)).map(Map.Entry::getValue).findFirst().orElse(null);
    }

    /** Every value of a repeated query parameter. */
    public List<String> queryAll(String name) {
        List<String> values = new ArrayList<>();
        for (Map.Entry<String, String> p : queryParameters()) {
            if (p.getKey().equals(name)) {
                values.add(p.getValue());
            }
        }
        return values;
    }

    /** The JSON body. */
    public JsonNode json() {
        if (body == null) {
            throw new AssertionError(this + " has no body.");
        }
        try {
            return JSON.readTree(body);
        } catch (Exception e) {
            throw new AssertionError("The body is not JSON: " + body, e);
        }
    }

    @Override
    public String toString() {
        return method + " " + uri;
    }

    private static String decode(String text) {
        return URLDecoder.decode(text.replace("+", "%2B"), StandardCharsets.UTF_8);
    }
}
