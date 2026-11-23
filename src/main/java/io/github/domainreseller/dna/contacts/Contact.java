package io.github.domainreseller.dna.contacts;

import com.fasterxml.jackson.annotation.JsonProperty;

/** A stored contact, as the API returns it: its details, its role and its handle. */
public final class Contact extends ContactInfo {
    @JsonProperty("contactType")
    private ContactType contactType;
    @JsonProperty("handle")
    private String handle;

    /** Creates an empty contact. */
    public Contact() {
    }

    /**
     * Returns the role, when the contact is read from a domain.
     *
     * @return the role, or null
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
    public Contact contactType(ContactType contactType) {
        this.contactType = contactType;
        return this;
    }

    /**
     * Returns the contact handle, for example {@code DNA-12345}.
     *
     * @return the handle, or null
     */
    public String handle() {
        return handle;
    }

    /**
     * Sets the contact handle.
     *
     * @param handle the handle
     * @return this contact
     */
    public Contact handle(String handle) {
        this.handle = handle;
        return this;
    }
}
