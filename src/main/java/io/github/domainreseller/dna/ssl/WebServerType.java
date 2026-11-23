package io.github.domainreseller.dna.ssl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import java.util.Locale;

/**
 * The web server software a certificate is for.
 * Any spelling of a known value is accepted and normalised; values compare ignoring case, and values
 * this SDK version does not know are kept as they are.
 */
public final class WebServerType {
    private static final List<String> KNOWN = List.of("Apache", "IIS", "AOL", "C2Net");

    /** Apache, nginx and most other servers that use PEM files. */
    public static final WebServerType APACHE = new WebServerType("Apache");

    /** Microsoft IIS. */
    public static final WebServerType IIS = new WebServerType("IIS");

    /** AOL server. */
    public static final WebServerType AOL = new WebServerType("AOL");

    /** C2Net Stronghold. */
    public static final WebServerType C2NET = new WebServerType("C2Net");

    private final String value;

    private WebServerType(String value) {
        this.value = value;
    }

    /**
     * Returns the value with the given name. Known names are normalised to the API's spelling.
     *
     * @param value the name
     * @return the value
     */
    @JsonCreator
    public static WebServerType of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("A WebServerType cannot be null.");
        }
        String trimmed = value.trim();
        for (String known : KNOWN) {
            if (known.equalsIgnoreCase(trimmed)) {
                return new WebServerType(known);
            }
        }
        return new WebServerType(trimmed);
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
        return other instanceof WebServerType && ((WebServerType) other).value.equalsIgnoreCase(value);
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
