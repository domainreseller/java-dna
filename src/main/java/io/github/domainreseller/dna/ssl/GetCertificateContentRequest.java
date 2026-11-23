package io.github.domainreseller.dna.ssl;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;
import java.util.UUID;

/**
 * {@code GET ssls/certificate-content}: the issued certificate and its chain in PEM format.
 */
public final class GetCertificateContentRequest extends BaseRequest<CertificateContent> {
    private final UUID certificateId;

    /**
     * Creates the request.
     *
     * @param certificateId the certificate
     */
    public GetCertificateContentRequest(UUID certificateId) {
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
        return "ssls/certificate-content";
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
