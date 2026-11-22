package io.github.domainreseller.dna.ssl;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An issued certificate and its chain, in PEM format.
 *
 * @param certificateId            the ID of the certificate this content belongs to
 * @param certificate              the certificate
 * @param intermediateCertificate1 the first intermediate certificate
 * @param intermediateCertificate2 the second intermediate certificate, if any
 * @param rootCertificate          the root certificate
 */
public record CertificateContent(
        UUID certificateId,
        String certificate,
        String intermediateCertificate1,
        String intermediateCertificate2,
        String rootCertificate) {
    /**
     * Returns the certificate followed by its intermediates: what most web servers expect as the "full chain" file.
     *
     * @return the PEM blocks joined by line breaks, ending with a line break
     */
    public String fullChain() {
        return Stream.of(certificate, intermediateCertificate1, intermediateCertificate2)
                .filter(pem -> pem != null && !pem.isBlank())
                .map(String::trim)
                .collect(Collectors.joining("\n")) + "\n";
    }
}
