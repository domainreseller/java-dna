package io.github.domainreseller.dna.ssl;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;
import java.util.UUID;

/**
 * {@code PUT ssls/check-status}: asks the certificate authority for the current state and returns it.
 */
public final class RefreshCertificateStatusRequest extends BaseRequest<CertificateStatus> {
    private final UUID certificateId;

    /**
     * Creates the request.
     *
     * @param certificateId the certificate
     */
    public RefreshCertificateStatusRequest(UUID certificateId) {
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
        return HttpMethod.PUT;
    }

    @Override
    public String path() {
        return "ssls/check-status";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }

    @Override
    protected Object body() {
        return JsonBody.create().add("certificateId", certificateId);
    }

    @Override
    protected void validate() {
        SslValidation.requireCertificateId(certificateId);
    }
}
