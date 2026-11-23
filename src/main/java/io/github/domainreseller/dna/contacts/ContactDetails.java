package io.github.domainreseller.dna.contacts;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.domainreseller.dna.internal.Values;
import java.util.List;

/**
 * A contact handle and the state of its e-mail and phone verification.
 *
 * @param contact       the contact
 * @param verifications the verification records of the contact's e-mail address and phone number; never null
 */
public record ContactDetails(
        Contact contact,
        @JsonProperty("verificationObjects") List<ContactVerification> verifications) {
    /**
     * Creates the details.
     *
     * @param contact       the contact
     * @param verifications the verification records; null becomes an empty list
     */
    public ContactDetails {
        verifications = Values.list(verifications);
    }
}
