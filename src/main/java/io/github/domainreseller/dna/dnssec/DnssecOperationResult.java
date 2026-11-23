package io.github.domainreseller.dna.dnssec;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The result of a DNSSEC change.
 *
 * @param keyTag        the key tag of the DS record involved, or null
 * @param operationType what was done
 * @param isSuccess     whether the registry accepted the change
 * @param isPending     whether the registry still has to confirm the change
 * @param resultMessage the registry's message
 */
public record DnssecOperationResult(
        Integer keyTag,
        DnssecOperationType operationType,
        @JsonProperty("isSuccess") boolean isSuccess,
        @JsonProperty("isPending") boolean isPending,
        String resultMessage) {
}
