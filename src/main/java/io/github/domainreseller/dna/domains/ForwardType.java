package io.github.domainreseller.dna.domains;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import java.util.Locale;

/**
 * How a domain forwards to a URL: {@link #STANDARD} (HTTP 301) or {@link #FRAME} (masked).
 * Any spelling of a known value is accepted and normalised; values compare ignoring case, and values
 * this SDK version does not know are kept as they are.
 */
public final class ForwardType {
    private static final List<String> KNOWN = List.of("Standard", "Frame");

    /** An HTTP 301 redirect. */
    public static final ForwardType STANDARD = new ForwardType("Standard");

    /** Shows the target inside a frame, keeping the domain in the address bar. */
    public static final ForwardType FRAME = new ForwardType("Frame");

    private final String value;

    private ForwardType(String value) {
        this.value = value;
    }

    /**
     * Returns the value with the given name. Known names are normalised to the API's spelling.
     *
     * @param value the name
     * @return the value
     */
    @JsonCreator
    public static ForwardType of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("A ForwardType cannot be null.");
        }
        String trimmed = value.trim();
        for (String known : KNOWN) {
            if (known.equalsIgnoreCase(trimmed)) {
                return new ForwardType(known);
            }
        }
        return new ForwardType(trimmed);
    }

    /**
     * Returns the name as the API spells it.
     *
     * @return the name
     */
    @JsonValue
    public String value() {
        return value;
    }

    /**
     * Returns whether the value is one this SDK version knows.
     *
     * @return whether the value is known
     */
    public boolean isKnown() {
        return KNOWN.contains(value);
    }

    /**
     * Compares two values, ignoring case.
     *
     * @param other the other object
     * @return whether both are the same value
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof ForwardType && ((ForwardType) other).value.equalsIgnoreCase(value);
    }

    /**
     * Returns a hash code that ignores case.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return value.toLowerCase(Locale.ROOT).hashCode();
    }

    /**
     * Returns the name.
     *
     * @return the name
     */
    @Override
    public String toString() {
        return value;
    }
}
