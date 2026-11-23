package io.github.domainreseller.dna.dnssec;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

/**
 * A DS record at the registry.
 *
 * @param keyTag                    the key tag
 * @param algorithm                 the algorithm
 * @param digestType                the digest type
 * @param digest                    the digest, in hexadecimal
 * @param status                    the state; several records can be active during a key rollover
 * @param isAlgorithmNotRecommended whether the algorithm is no longer recommended
 * @param createdAt                 when the record was added
 * @param activatedAt               when the record became active, or null
 * @param removedAt                 when the record was removed, or null
 */
public record DsRecord(
        int keyTag,
        DnssecAlgorithm algorithm,
        DsDigestType digestType,
        String digest,
        DsRecordStatus status,
        @JsonProperty("isAlgorithmNotRecommended") boolean isAlgorithmNotRecommended,
        OffsetDateTime createdAt,
        OffsetDateTime activatedAt,
        OffsetDateTime removedAt) {
}
