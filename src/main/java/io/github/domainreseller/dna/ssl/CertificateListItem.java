package io.github.domainreseller.dna.ssl;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * One certificate in the list of your certificates. A domain can have several.
 *
 * @param certificateId            the certificate's ID; every operation on the certificate takes it
 * @param domainName               the domain
 * @param packageCode              the product code
 * @param packageName              the product name
 * @param status                   the state, for example {@code Active}, {@code WaitingForInstall},
 *                                 {@code Cancelled} or {@code Renewed}
 * @param statusReason             why the certificate is in this state, for example {@code None},
 *                                 {@code ConfigurationWindowExpired} or {@code ResellerRevoked}
 * @param isConfigured             whether the certificate has been configured (CSR, validation, contact)
 * @param isIssued                 whether the certificate has been issued
 * @param purchasedAt              when it was bought
 * @param subscriptionExpiresAt    when the paid subscription ends; renewal is allowed from 30 days before
 * @param certificateExpiresAt     when the issued certificate stops being valid; null until it is issued
 * @param renewedFromCertificateId on a renewal, the ID of the certificate it renews; null otherwise
 */
public record CertificateListItem(
        UUID certificateId,
        String domainName,
        String packageCode,
        String packageName,
        String status,
        String statusReason,
        @JsonProperty("isConfigured") boolean isConfigured,
        @JsonProperty("isIssued") boolean isIssued,
        OffsetDateTime purchasedAt,
        OffsetDateTime subscriptionExpiresAt,
        OffsetDateTime certificateExpiresAt,
        UUID renewedFromCertificateId) {
}
