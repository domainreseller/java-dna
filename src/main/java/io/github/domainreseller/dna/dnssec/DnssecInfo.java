package io.github.domainreseller.dna.dnssec;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.domainreseller.dna.internal.Values;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * The DNSSEC state of a domain: its DS records and what the registry allows.
 *
 * @param domainName         the domain name
 * @param isDnssecAvailable  whether the registry supports DNSSEC for this domain
 * @param status             the DNSSEC state
 * @param isDelegationSigned whether the delegation is signed at the registry
 * @param maxSigLife         the maximum signature lifetime, when the registry sets one
 * @param lastChangedAt      when the DNSSEC state last changed, or null
 * @param dsRecords          the DS records at the registry; never null
 * @param allowedAlgorithms  the algorithms the registry accepts; never null
 * @param allowedDigestTypes the digest types the registry accepts; never null
 * @param maxDsRecordCount   the most DS records the domain can have
 */
public record DnssecInfo(
        String domainName,
        @JsonProperty("isDnssecAvailable") boolean isDnssecAvailable,
        DnssecStatus status,
        @JsonProperty("delegationSigned") boolean isDelegationSigned,
        Integer maxSigLife,
        OffsetDateTime lastChangedAt,
        List<DsRecord> dsRecords,
        List<DnssecAlgorithm> allowedAlgorithms,
        List<DsDigestType> allowedDigestTypes,
        int maxDsRecordCount) {
    /**
     * Creates the state.
     *
     * @param domainName         the domain name
     * @param isDnssecAvailable  whether the registry supports DNSSEC
     * @param status             the DNSSEC state
     * @param isDelegationSigned whether the delegation is signed
     * @param maxSigLife         the maximum signature lifetime
     * @param lastChangedAt      when the state last changed
     * @param dsRecords          the DS records; null becomes an empty list
     * @param allowedAlgorithms  the accepted algorithms; null becomes an empty list
     * @param allowedDigestTypes the accepted digest types; null becomes an empty list
     * @param maxDsRecordCount   the most DS records allowed
     */
    public DnssecInfo {
        dsRecords = Values.list(dsRecords);
        allowedAlgorithms = Values.list(allowedAlgorithms);
        allowedDigestTypes = Values.list(allowedDigestTypes);
    }
}
