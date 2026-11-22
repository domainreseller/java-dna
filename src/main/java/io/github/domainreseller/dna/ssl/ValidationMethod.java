package io.github.domainreseller.dna.ssl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import java.util.Locale;

/**
 * How the certificate authority checks that you control the domain (the API's {@code authenticationType}):
 * {@link #EMAIL}, {@link #CNAME}, {@link #HTTP} or {@link #FILE}.
 * Any spelling of a known value is accepted and normalised; values compare ignoring case, and values
 * this SDK version does not know are kept as they are.
 */
public final class ValidationMethod {
    private static final List<String> KNOWN = List.of("Email", "Cname", "Http", "File");

    /** An approval e-mail to the approver address. */
    public static final ValidationMethod EMAIL = new ValidationMethod("Email");

    /** A DNS CNAME record. */
    public static final ValidationMethod CNAME = new ValidationMethod("Cname");

    /** A file served over HTTP on the site. */
    public static final ValidationMethod HTTP = new ValidationMethod("Http");

    /** File-based validation. */
    public static final ValidationMethod FILE = new ValidationMethod("File");

    private final String value;

    private ValidationMethod(String value) {
        this.value = value;
    }

    /**
     * Returns the value with the given name. Known names are normalised to the API's spelling.
     *
     * @param value the name
     * @return the value
     */
    @JsonCreator
    public static ValidationMethod of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("A ValidationMethod cannot be null.");
        }
        String trimmed = value.trim();
        for (String known : KNOWN) {
            if (known.equalsIgnoreCase(trimmed)) {
                return new ValidationMethod(known);
            }
        }
        return new ValidationMethod(trimmed);
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
        return other instanceof ValidationMethod && ((ValidationMethod) other).value.equalsIgnoreCase(value);
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
