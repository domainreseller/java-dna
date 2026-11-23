package io.github.domainreseller.dna.ssl;

/**
 * A certificate signing request and its private key. Keep the private key safe; the API does not store it.
 *
 * @param csr        the CSR in PEM format
 * @param privateKey the private key in PEM format
 */
public record GeneratedCsr(String csr, String privateKey) {
    /**
     * Returns the CSR only, so the private key does not end up in logs.
     *
     * @return a short description
     */
    @Override
    public String toString() {
        return "GeneratedCsr[csr=" + csr + ", privateKey=***]";
    }
}
