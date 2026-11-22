package io.github.domainreseller.dna.ssl;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * The current state of a certificate.
 *
 * @param certificateId        the ID of the certificate that was checked
 * @param domainName           the domain
 * @param status               the state
 * @param statusMessage        a description of the state
 * @param isConfigured         whether the certificate has been configured (CSR, validation, contact)
 * @param isIssued             whether the certificate has been issued
 * @param certificateExpiresAt when the issued certificate expires, or null
 */
public record CertificateStatus(
        UUID certificateId,
        String domainName,
        String status,
        String statusMessage,
        @JsonProperty("isConfigured") boolean isConfigured,
        @JsonProperty("isIssued") boolean isIssued,
        OffsetDateTime certificateExpiresAt) {
}
