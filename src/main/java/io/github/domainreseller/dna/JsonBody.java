package io.github.domainreseller.dna;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A JSON object for a request body, written in the order the values are added. Null values are left out, so
 * optional fields can be added unconditionally. Values can be text, numbers, booleans, enums, lists, maps,
 * records and the SDK's models.
 *
 * <pre>{@code
 * @Override
 * protected Object body() {
 *     return JsonBody.create()
 *             .add("domainName", domainName)
 *             .add("period", period);
 * }
 * }</pre>
 */
public final class JsonBody extends AbstractMap<String, Object> {
    private final Map<String, Object> values = new LinkedHashMap<>();

    private JsonBody() {
    }

    /**
     * Creates an empty body.
     *
     * @return a new body
     */
    public static JsonBody create() {
        return new JsonBody();
    }

    /**
     * Adds a property. A null value is left out.
     *
     * @param name  the JSON property name, usually camelCase as in the API document
     * @param value the value
     * @return this object
     */
    public JsonBody add(String name, Object value) {
        if (value != null) {
            values.put(name, value);
        }
        return this;
    }

    /**
     * Returns the properties as an unmodifiable view.
     *
     * @return the properties
     */
    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return Collections.unmodifiableMap(values).entrySet();
    }
}
