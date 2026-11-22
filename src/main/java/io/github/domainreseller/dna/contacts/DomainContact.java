package io.github.domainreseller.dna.contacts;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** A contact together with the role it plays for a domain, used to register a domain or replace its contacts. */
public final class DomainContact extends ContactInfo {
    @JsonProperty("contactType")
    private ContactType contactType;

    /** Creates an empty contact without a role. */
    public DomainContact() {
    }

    /**
     * Uses the details of a contact for the given role.
     *
     * @param contact     the details, which are copied
     * @param contactType the role
     */
    public DomainContact(ContactInfo contact, ContactType contactType) {
        super(contact);
        this.contactType = contactType;
    }

    /**
     * Uses the same details for all four roles (Registrant, Admin, Tech and Billing).
     *
     * @param contact the details
     * @return four contacts, one per role
     */
    public static List<DomainContact> forAllRoles(ContactInfo contact) {
        Objects.requireNonNull(contact, "contact");
        List<DomainContact> contacts = new ArrayList<>();
        for (ContactType role : ContactType.ALL) {
            contacts.add(new DomainContact(contact, role));
        }
        return contacts;
    }

    /**
     * Returns the role.
     *
     * @return the role: Registrant, Admin, Tech or Billing
     */
    public ContactType contactType() {
        return contactType;
    }

    /**
     * Sets the role.
     *
     * @param contactType the role
     * @return this contact
     */
    public DomainContact contactType(ContactType contactType) {
        this.contactType = contactType;
        return this;
    }
}
