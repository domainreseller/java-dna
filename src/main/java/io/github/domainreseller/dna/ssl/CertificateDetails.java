package io.github.domainreseller.dna.ssl;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.domainreseller.dna.internal.Values;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Everything about a certificate: subscription, configuration, issued certificate and contacts.
 *
 * @param certificateId the certificate's ID; every operation on the certificate takes it
 * @param domainName    the domain
 * @param status        the state
 * @param isConfigured  whether the certificate has been configured
 * @param isIssued      whether the certificate has been issued
 * @param packageName   the product name
 * @param packageCode   the product code
 * @param subscription  the purchase and expiry of the subscription
 * @param configuration the configuration that was submitted
 * @param certificate   the issued certificate's validity, once issued
 * @param contacts      the certificate contacts; never null
 */
public record CertificateDetails(
        UUID certificateId,
        String domainName,
        String status,
        @JsonProperty("isConfigured") boolean isConfigured,
        @JsonProperty("isIssued") boolean isIssued,
        String packageName,
        String packageCode,
        Subscription subscription,
        Configuration configuration,
        IssuedCertificate certificate,
        List<CertificateContact> contacts) {
    /**
     * Creates the details.
     *
     * @param certificateId the certificate's ID
     * @param domainName    the domain
     * @param status        the state
     * @param isConfigured  whether it has been configured
     * @param isIssued      whether it has been issued
     * @param packageName   the product name
     * @param packageCode   the product code
     * @param subscription  the subscription
     * @param configuration the configuration
     * @param certificate   the issued certificate's validity
     * @param contacts      the contacts; null becomes an empty list
     */
    public CertificateDetails {
        contacts = Values.list(contacts);
    }

    /**
     * The purchase and expiry of a certificate subscription.
     *
     * @param purchasedAt when it was bought
     * @param expiresAt   when it expires; renewal is allowed from 30 days before
     */
    public record Subscription(OffsetDateTime purchasedAt, OffsetDateTime expiresAt) {
    }

    /**
     * The configuration submitted for a certificate.
     *
     * @param configuredAt       when it was submitted
     * @param configurationLink  a link to the certificate authority's configuration page
     * @param pin                the configuration PIN
     * @param webServer          the web server type
     * @param authenticationType the validation method
     * @param sanList            additional domain names
     * @param wildcardList       wildcard names
     * @param approverMail       the approver e-mail address
     * @param csr                the CSR
     * @param dcvDetails         what to publish for domain control validation
     */
    public record Configuration(
            OffsetDateTime configuredAt,
            String configurationLink,
            String pin,
            String webServer,
            String authenticationType,
            String sanList,
            String wildcardList,
            String approverMail,
            String csr,
            String dcvDetails) {
    }

    /**
     * The validity of an issued certificate.
     *
     * @param issuedAt   when it was issued
     * @param validUntil when it stops being valid
     */
    public record IssuedCertificate(OffsetDateTime issuedAt, OffsetDateTime validUntil) {
    }
}
