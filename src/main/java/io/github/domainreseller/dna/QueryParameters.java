package io.github.domainreseller.dna;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Builds a query string. Null values are skipped, so optional filters can be added unconditionally.
 * Lists are sent as repeated parameters ({@code Types=1&Types=2}) and {@link CodedEnum} values as their numbers.
 *
 * <pre>{@code
 * query.add("DomainName", domainName)
 *      .add("SkipCount", skipCount)
 *      .addEach("TransactionTypes", transactionTypes);
 * }</pre>
 */
public final class QueryParameters implements Iterable<Map.Entry<String, String>> {
    private final List<Map.Entry<String, String>> items = new ArrayList<>();

    /** Creates an empty query string. */
    public QueryParameters() {
    }

    /**
     * Returns the number of parameters added so far.
     *
     * @return the number of parameters
     */
    public int size() {
        return items.size();
    }

    /**
     * Adds a text parameter. Null or empty values are skipped.
     *
     * @param name  the parameter name
     * @param value the value
     * @return this object
     */
    public QueryParameters add(String name, String value) {
        if (value != null && !value.isEmpty()) {
            items.add(new AbstractMap.SimpleImmutableEntry<>(name, value));
        }
        return this;
    }

    /**
     * Adds a whole number. Null is skipped.
     *
     * @param name  the parameter name
     * @param value the value
     * @return this object
     */
    public QueryParameters add(String name, Integer value) {
        return value == null ? this : add(name, value.toString());
    }

    /**
     * Adds a whole number. Null is skipped.
     *
     * @param name  the parameter name
     * @param value the value
     * @return this object
     */
    public QueryParameters add(String name, Long value) {
        return value == null ? this : add(name, value.toString());
    }

    /**
     * Adds a decimal number without exponent, for example {@code 12.5}. Null is skipped.
     *
     * @param name  the parameter name
     * @param value the value
     * @return this object
     */
    public QueryParameters add(String name, BigDecimal value) {
        return value == null ? this : add(name, value.toPlainString());
    }

    /**
     * Adds {@code true} or {@code false}. Null is skipped.
     *
     * @param name  the parameter name
     * @param value the value
     * @return this object
     */
    public QueryParameters add(String name, Boolean value) {
        return value == null ? this : add(name, value ? "true" : "false");
    }

    /**
     * Adds an ISO 8601 date, time and offset, for example {@code 2026-11-01T00:00:00Z}. Null is skipped.
     *
     * @param name  the parameter name
     * @param value the value
     * @return this object
     */
    public QueryParameters add(String name, OffsetDateTime value) {
        return value == null ? this : add(name, DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value));
    }

    /**
     * Adds a UUID. Null is skipped.
     *
     * @param name  the parameter name
     * @param value the value
     * @return this object
     */
    public QueryParameters add(String name, UUID value) {
        return value == null ? this : add(name, value.toString());
    }

    /**
     * Adds an enum as its number. Null is skipped.
     *
     * @param name  the parameter name
     * @param value the value
     * @return this object
     */
    public QueryParameters add(String name, CodedEnum value) {
        return value == null ? this : add(name, Integer.toString(value.code()));
    }

    /**
     * Adds one parameter per value, for example {@code Currencies=USD&Currencies=TRY}. Null or empty values are
     * skipped; a {@link CodedEnum} is sent as its number and anything else with {@code toString()}.
     *
     * @param name   the parameter name
     * @param values the values; null adds nothing
     * @return this object
     */
    public QueryParameters addEach(String name, Collection<?> values) {
        if (values != null) {
            for (Object value : values) {
                add(name, text(value));
            }
        }
        return this;
    }

    /**
     * Adds the values as one comma-separated parameter, for example {@code StatusIds=0,7}. Nothing is added for a
     * null or empty collection.
     *
     * @param name   the parameter name
     * @param values the values; enums are sent as their numbers
     * @return this object
     */
    public QueryParameters addCommaSeparated(String name, Collection<?> values) {
        if (values == null || values.isEmpty()) {
            return this;
        }
        StringJoiner joined = new StringJoiner(",");
        for (Object value : values) {
            String text = text(value);
            if (text != null && !text.isEmpty()) {
                joined.add(text);
            }
        }
        return add(name, joined.toString());
    }

    /**
     * Returns the first value of a parameter.
     *
     * @param name the parameter name
     * @return the value, or null when the parameter was not added
     */
    public String get(String name) {
        for (Map.Entry<String, String> item : items) {
            if (item.getKey().equals(name)) {
                return item.getValue();
            }
        }
        return null;
    }

    /**
     * Returns the parameter names in the order they were added (a repeated parameter appears once per value).
     *
     * @return the names
     */
    public List<String> names() {
        List<String> names = new ArrayList<>(items.size());
        for (Map.Entry<String, String> item : items) {
            names.add(item.getKey());
        }
        return names;
    }

    /**
     * Iterates over the parameters in the order they were added.
     *
     * @return an iterator of name/value pairs
     */
    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return Collections.unmodifiableList(items).iterator();
    }

    /**
     * Returns the encoded query string without the leading {@code ?}, for example {@code DomainName=example.com}.
     *
     * @return the encoded query string
     */
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("&");
        for (Map.Entry<String, String> item : items) {
            joiner.add(encode(item.getKey()) + "=" + encode(item.getValue()));
        }
        return joiner.toString();
    }

    private static String text(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof CodedEnum) {
            return Integer.toString(((CodedEnum) value).code());
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).toPlainString();
        }
        return value.toString();
    }

    private static String encode(String text) {
        // RFC 3986 percent-encoding: spaces as %20, not +.
        return URLEncoder.encode(text, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~");
    }
}
