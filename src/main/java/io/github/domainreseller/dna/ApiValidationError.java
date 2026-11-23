package io.github.domainreseller.dna;

import io.github.domainreseller.dna.internal.Values;
import java.util.List;

/**
 * One field-level problem reported by the API.
 *
 * @param message what is wrong; never null
 * @param members the fields the problem refers to, for example {@code contacts[0].eMail}; never null
 */
public record ApiValidationError(String message, List<String> members) {
    /**
     * Creates a validation error.
     *
     * @param message what is wrong; null becomes an empty text
     * @param members the fields; null becomes an empty list
     */
    public ApiValidationError {
        message = message == null ? "" : message;
        members = Values.list(members);
    }

    /**
     * Returns {@code field, field: message}, or only the message when no field is named.
     *
     * @return a readable description
     */
    @Override
    public String toString() {
        return members.isEmpty() ? message : String.join(", ", members) + ": " + message;
    }
}
