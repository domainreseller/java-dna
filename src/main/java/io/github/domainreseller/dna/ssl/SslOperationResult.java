package io.github.domainreseller.dna.ssl;

import java.util.UUID;

/**
 * The result of buying, renewing, configuring, reissuing or revoking a certificate.
 *
 * @param success       whether the operation succeeded
 * @param message       the API's message
 * @param certificateId the certificate's ID; store it, every later operation on the certificate takes it. After a
 *                      renewal it is the ID of the new certificate, not of the one that was renewed
 * @param status        the certificate's state after the operation
 * @param dcvDetails    what to publish for domain control validation (DNS record or file), when it applies
 * @param csr           the CSR, when the API generated one
 * @param privateKey    the private key, when the API generated one
 */
public record SslOperationResult(
        boolean success,
        String message,
        UUID certificateId,
        String status,
        String dcvDetails,
        String csr,
        String privateKey) {
    /**
     * Returns the result without the private key, so it does not end up in logs.
     *
     * @return a short description
     */
    @Override
    public String toString() {
        return "SslOperationResult[success=" + success + ", message=" + message + ", certificateId=" + certificateId
                + ", status=" + status + (privateKey == null ? "" : ", privateKey=***") + "]";
    }
}
