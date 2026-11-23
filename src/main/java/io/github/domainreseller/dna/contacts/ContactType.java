package io.github.domainreseller.dna.contacts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import java.util.Locale;

/**
 * The role of a contact: {@link #REGISTRANT}, {@link #ADMIN}, {@link #TECH} or {@link #BILLING}. Any spelling is
 * accepted and normalised ({@code "admin"} becomes {@code Admin}); values compare ignoring case.
 */
public final class ContactType {
    private static final List<String> KNOWN = List.of("Registrant", "Admin", "Tech", "Billing");

    /** The domain owner. */
    public static final ContactType REGISTRANT = new ContactType("Registrant");

    /** The administrative contact. */
    public static final ContactType ADMIN = new ContactType("Admin");

    /** The technical contact. */
    public static final ContactType TECH = new ContactType("Tech");

    /** The billing contact. */
    public static final ContactType BILLING = new ContactType("Billing");

    /** All four roles, in the order registries list them. */
    public static final List<ContactType> ALL = List.of(REGISTRANT, ADMIN, TECH, BILLING);

    private final String value;

    private ContactType(String value) {
        this.value = value;
    }

    /**
     * Returns the role with the given name. Known names are normalised to the API's spelling.
     *
     * @param value the role name, for example {@code Registrant} or {@code tech}
     * @return the role
     */
    @JsonCreator
    public static ContactType of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("A contact type cannot be null.");
        }
        String trimmed = value.trim();
        for (String known : KNOWN) {
            if (known.equalsIgnoreCase(trimmed)) {
                return new ContactType(known);
            }
        }
        return new ContactType(trimmed);
    }

    /**
     * Returns the role name as the API spells it.
     *
     * @return the name
     */
    @JsonValue
    public String value() {
        return value;
    }

    /**
     * Returns whether the value is one of the four roles. The API silently treats unknown values as Registrant.
     *
     * @return whether the role is known
     */
    public boolean isKnown() {
        return KNOWN.contains(value);
    }

    /**
     * Compares two roles, ignoring case.
     *
     * @param other the other object
     * @return whether both are the same role
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof ContactType && ((ContactType) other).value.equalsIgnoreCase(value);
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
     * Returns the role name.
     *
     * @return the name
     */
    @Override
    public String toString() {
        return value;
    }
}
