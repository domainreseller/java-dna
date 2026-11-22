package io.github.domainreseller.dna.ssl;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;
import java.util.UUID;

/** {@code POST ssls/reissue}: reissues a certificate with a new CSR and key, or a new approver. */
public final class ReissueCertificateRequest extends BaseRequest<SslOperationResult> {
    private final UUID certificateId;
    private String csr;
    private String privateKey;
    private String approverEmail;

    /**
     * Creates the request.
     *
     * @param certificateId the certificate to reissue
     */
    public ReissueCertificateRequest(UUID certificateId) {
        this.certificateId = certificateId;
    }

    /**
     * Returns the certificate ID.
     *
     * @return the certificate ID
     */
    public UUID certificateId() {
        return certificateId;
    }

    /**
     * Returns the new CSR.
     *
     * @return the CSR, or null to keep the current one
     */
    public String csr() {
        return csr;
    }

    /**
     * Sets a new CSR.
     *
     * @param csr the CSR in PEM format, or null to keep the current one
     * @return this request
     */
    public ReissueCertificateRequest csr(String csr) {
        this.csr = csr;
        return this;
    }

    /**
     * Returns the private key of the new CSR.
     *
     * @return the private key, or null
     */
    public String privateKey() {
        return privateKey;
    }

    /**
     * Sets the private key of the new CSR.
     *
     * @param privateKey the private key in PEM format
     * @return this request
     */
    public ReissueCertificateRequest privateKey(String privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    /**
     * Returns the new approver address.
     *
     * @return the approver e-mail address, or null
     */
    public String approverEmail() {
        return approverEmail;
    }

    /**
     * Sets a new approver address for e-mail validation (the API's {@code approverMail}).
     *
     * @param approverEmail the approver e-mail address
     * @return this request
     */
    public ReissueCertificateRequest approverEmail(String approverEmail) {
        this.approverEmail = approverEmail;
        return this;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "ssls/reissue";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.WRITE;
    }

    @Override
    protected Object body() {
        return JsonBody.create()
                .add("certificateId", certificateId)
                .add("approverMail", approverEmail)
                .add("csr", csr)
                .add("privateKey", privateKey);
    }

    @Override
    protected void validate() {
        SslValidation.requireCertificateId(certificateId);
    }
}
