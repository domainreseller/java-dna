package io.github.domainreseller.dna.contacts;

import io.github.domainreseller.dna.internal.Values;
import java.util.List;

/**
 * The answer of {@link GetContactVerificationRequest}.
 *
 * @param results one entry per verified field; never null
 */
public record ContactVerificationResult(List<ContactVerification> results) {
    /**
     * Creates the result.
     *
     * @param results the entries; null becomes an empty list
     */
    public ContactVerificationResult {
        results = Values.list(results);
    }
}
