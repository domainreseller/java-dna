package io.github.domainreseller.dna.internal;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Small helpers for null-safe, unmodifiable collections in models. Not part of the public API. */
public final class Values {
    private Values() {
    }

    /**
     * Returns an unmodifiable copy of the list; null becomes an empty list. Null elements are kept.
     *
     * @param values the values, or null
     * @param <T>    the element type
     * @return an unmodifiable list
     */
    public static <T> List<T> list(Collection<? extends T> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        return Collections.unmodifiableList(new ArrayList<>(values));
    }

    /**
     * Returns an unmodifiable copy of the map that keeps its order; null becomes an empty map.
     *
     * @param values the values, or null
     * @param <K>    the key type
     * @param <V>    the value type
     * @return an unmodifiable map
     */
    public static <K, V> Map<K, V> map(Map<? extends K, ? extends V> values) {
        if (values == null || values.isEmpty()) {
            return Map.of();
        }
        return Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    /**
     * Escapes a value for use as one path segment, for example a contact handle in {@code contacts/{handle}}.
     *
     * @param value the value, or null
     * @return the escaped value; empty for null
     */
    public static String pathSegment(String value) {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }
}
