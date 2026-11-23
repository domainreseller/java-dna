package io.github.domainreseller.dna.ssl;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;
import java.util.UUID;

/**
 * {@code DELETE ssls/revoke}: revokes a certificate. <b>This cannot be undone.</b>
 */
public final class RevokeCertificateRequest extends BaseRequest<SslOperationResult> {
    private final UUID certificateId;

    /**
     * Creates the request.
     *
     * @param certificateId the certificate to revoke
     */
    public RevokeCertificateRequest(UUID certificateId) {
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

    @Override
    public HttpMethod method() {
        return HttpMethod.DELETE;
    }

    @Override
    public String path() {
        return "ssls/revoke";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.DESTRUCTIVE;
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("CertificateId", certificateId);
    }

    @Override
    protected void validate() {
        SslValidation.requireCertificateId(certificateId);
    }
}
