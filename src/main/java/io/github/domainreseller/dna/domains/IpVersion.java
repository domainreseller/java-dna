package io.github.domainreseller.dna.domains;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import java.util.Locale;

/**
 * The IP version of a child nameserver address: {@link #V4} or {@link #V6}.
 * Any spelling of a known value is accepted and normalised; values compare ignoring case, and values
 * this SDK version does not know are kept as they are.
 */
public final class IpVersion {
    private static final List<String> KNOWN = List.of("v4", "v6");

    /** IPv4. */
    public static final IpVersion V4 = new IpVersion("v4");

    /** IPv6. Rejected when the TLD does not support IPv6 glue. */
    public static final IpVersion V6 = new IpVersion("v6");

    private final String value;

    private IpVersion(String value) {
        this.value = value;
    }

    /**
     * Returns the value with the given name. Known names are normalised to the API's spelling.
     *
     * @param value the name
     * @return the value
     */
    @JsonCreator
    public static IpVersion of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("A IpVersion cannot be null.");
        }
        String trimmed = value.trim();
        for (String known : KNOWN) {
            if (known.equalsIgnoreCase(trimmed)) {
                return new IpVersion(known);
            }
        }
        return new IpVersion(trimmed);
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
        return other instanceof IpVersion && ((IpVersion) other).value.equalsIgnoreCase(value);
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
