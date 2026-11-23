package io.github.domainreseller.dna.contacts;

import io.github.domainreseller.dna.DomainNameApiClient;
import java.util.List;
import java.util.Objects;

/**
 * Reusable contact handles (registrant, admin, tech, billing) and their verification. Create a handle once and use
 * it for many domains. Reach it through {@link DomainNameApiClient#contacts()}.
 */
public final class ContactsClient {
    private final DomainNameApiClient api;

    /**
     * Creates the client. Use {@link DomainNameApiClient#contacts()} instead.
     *
     * @param api the client that sends the requests
     */
    public ContactsClient(DomainNameApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    /**
     * Returns a contact handle and the state of its verification.
     *
     * @param handle the contact handle, for example {@code DNA-12345}
     * @return the contact and its verification records
     */
    public ContactDetails get(String handle) {
        return api.send(new GetContactRequest(handle));
    }

    /**
     * Creates a reusable contact handle.
     *
     * @param contact the contact details
     * @return the result, with the new handle in {@link CreateContactResult#handle()}
     */
    public CreateContactResult create(ContactInfo contact) {
        return api.send(new CreateContactRequest(contact));
    }

    /**
     * Updates a contact handle and the domains that use it.
     *
     * @param handle  the contact handle
     * @param contact the new details
     * @return what happened to the contact and to each domain that uses it
     */
    public HandleUpdateResult update(String handle, ContactInfo contact) {
        return api.send(new UpdateContactRequest(handle, contact));
    }

    /**
     * Returns the e-mail and phone verification state of a contact.
     *
     * @param handle the contact handle
     * @return one entry per verified field
     */
    public List<ContactVerification> getVerification(String handle) {
        return api.send(new GetContactVerificationRequest(handle)).results();
    }

    /**
     * Sends the contact verification notice again.
     *
     * @param handle the contact handle
     */
    public void resendVerification(String handle) {
        api.send(new ResendContactVerificationRequest(handle));
    }
}
