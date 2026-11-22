package io.github.domainreseller.dna.ssl;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.domainreseller.dna.internal.Values;
import java.util.List;

/**
 * The answer of {@link GetApproverEmailsRequest}.
 *
 * @param emails the e-mail addresses allowed for e-mail validation; never null
 */
public record ApproverEmailsResult(@JsonProperty("mails") List<String> emails) {
    /**
     * Creates the result.
     *
     * @param emails the addresses; null becomes an empty list
     */
    public ApproverEmailsResult {
        emails = Values.list(emails);
    }
}
