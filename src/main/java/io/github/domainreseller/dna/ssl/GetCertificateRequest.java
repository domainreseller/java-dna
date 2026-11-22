package io.github.domainreseller.dna.ssl;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;
import java.util.UUID;

/**
 * {@code GET ssls/certificate}: the details of a certificate (subscription, configuration, contacts).
 */
public final class GetCertificateRequest extends BaseRequest<CertificateDetails> {
    private final UUID certificateId;

    /**
     * Creates the request.
     *
     * @param certificateId the certificate
     */
    public GetCertificateRequest(UUID certificateId) {
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
        return HttpMethod.GET;
    }

    @Override
    public String path() {
        return "ssls/certificate";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
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
