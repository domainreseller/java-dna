package io.github.domainreseller.dna.ssl;

import java.util.UUID;

/** Checks certificate IDs before anything is sent. */
final class SslValidation {
    private SslValidation() {
    }

    /**
     * Throws for a missing or empty certificate ID. That is what code gets when it never stored the ID, for example
     * code written when certificates were still addressed by domain name.
     */
    static void requireCertificateId(UUID certificateId) {
        if (certificateId == null || (certificateId.getMostSignificantBits() == 0 && certificateId.getLeastSignificantBits() == 0)) {
            throw new IllegalArgumentException("certificateId must be a certificate ID (UUID) and cannot be empty. "
                    + "Certificates are not addressed by domain name: use the certificateId returned by "
                    + "ssl().purchase(...) or ssl().renew(...), or look it up with "
                    + "ssl().list(new ListCertificatesRequest().domainName(\"example.com\")).");
        }
    }
}
