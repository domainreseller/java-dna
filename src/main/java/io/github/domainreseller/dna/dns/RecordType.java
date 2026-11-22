package io.github.domainreseller.dna.dns;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import java.util.Locale;

/**
 * A DNS record type such as {@link #A}, {@link #CNAME} or {@link #TXT}. The API is case-sensitive; any spelling of a
 * known type is normalised ({@code "txt"} becomes {@code TXT}, {@code "dnskey"} becomes {@code DNSKey}).
 * Values compare ignoring case, and types this SDK version does not know (such as {@code SPF}) are kept as they are.
 */
public final class RecordType {
    private static final List<String> KNOWN = List.of(
            "A", "AAAA", "ALIAS", "NS", "CNAME", "MX", "SOA", "TXT", "PTR", "SRV", "DNSKey", "DS", "NSEC", "NSEC3",
            "NSEC3Param", "RRSIG", "AFSDB", "ATMA", "CAA", "CERT", "DHCID", "DName", "HInfo", "ISDN", "LOC", "MB", "MG",
            "MInfo", "MR", "NAPTR", "NSAP", "RP", "RT", "TLSA", "X25");

    /** IPv4 address. */
    public static final RecordType A = new RecordType("A");

    /** IPv6 address. */
    public static final RecordType AAAA = new RecordType("AAAA");

    /** Alias for the zone apex (CNAME-like, allowed at {@code @}). */
    public static final RecordType ALIAS = new RecordType("ALIAS");

    /** Certification authority authorisation. Content needs three parts: {@code 0 issue "letsencrypt.org"}. */
    public static final RecordType CAA = new RecordType("CAA");

    /** Canonical name. Needs a record name (not the apex). */
    public static final RecordType CNAME = new RecordType("CNAME");

    /** Mail exchanger, for example {@code 10 mail.example.com}. */
    public static final RecordType MX = new RecordType("MX");

    /** Naming authority pointer. */
    public static final RecordType NAPTR = new RecordType("NAPTR");

    /** Nameserver delegation. */
    public static final RecordType NS = new RecordType("NS");

    /** Pointer (reverse DNS). */
    public static final RecordType PTR = new RecordType("PTR");

    /** Service locator, for example {@code 10 5 5060 sip.example.com}. */
    public static final RecordType SRV = new RecordType("SRV");

    /** TLS certificate association (DANE). */
    public static final RecordType TLSA = new RecordType("TLSA");

    /** Text, for example an SPF or verification value. */
    public static final RecordType TXT = new RecordType("TXT");

    private final String value;

    private RecordType(String value) {
        this.value = value;
    }

    /**
     * Returns the value with the given name. Known names are normalised to the API's spelling.
     *
     * @param value the name
     * @return the value
     */
    @JsonCreator
    public static RecordType of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("A RecordType cannot be null.");
        }
        String trimmed = value.trim();
        for (String known : KNOWN) {
            if (known.equalsIgnoreCase(trimmed)) {
                return new RecordType(known);
            }
        }
        return new RecordType(trimmed);
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
        return other instanceof RecordType && ((RecordType) other).value.equalsIgnoreCase(value);
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
