package io.github.domainreseller.dna.contacts;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The result of creating a contact.
 *
 * @param handle           the new contact handle; use it to register domains or change their contacts
 * @param operationMessage the API's message about the operation
 * @param isSuccess        whether the contact was created
 */
public record CreateContactResult(
        String handle,
        String operationMessage,
        @JsonProperty("isSuccess") boolean isSuccess) {
}
