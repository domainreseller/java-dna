package io.github.domainreseller.dna.ssl;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;
import java.util.UUID;

/**
 * {@code POST ssls/renew}: renews a certificate for another year. <b>Charges your balance</b> (the renewal price).
 * The answer carries the ID of a <b>new</b> certificate, which you then configure with
 * {@link ConfigureCertificateRequest} within 15 days. Allowed from 30 days before the subscription ends.
 *
 * <p>Do not renew by purchasing again: {@link PurchaseCertificateRequest} is an unrelated new purchase at the purchase
 * price, and the old certificate would never be marked {@code Renewed}.
 */
public final class RenewCertificateRequest extends BaseRequest<SslOperationResult> {
    private final UUID certificateId;

    /**
     * Creates the request.
     *
     * @param certificateId the certificate to renew
     */
    public RenewCertificateRequest(UUID certificateId) {
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
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "ssls/renew";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.BILLABLE;
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
