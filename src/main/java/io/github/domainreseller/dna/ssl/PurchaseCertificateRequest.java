package io.github.domainreseller.dna.ssl;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;

/**
 * {@code POST ssls/initialize}: buys an SSL certificate package for a domain. <b>Charges your balance.</b> Store the
 * {@link SslOperationResult#certificateId()} of the answer, then submit the CSR and validation details with
 * {@link ConfigureCertificateRequest} within 15 days.
 */
public final class PurchaseCertificateRequest extends BaseRequest<SslOperationResult> {
    private final String domainName;
    private final String packageCode;

    /**
     * Creates the request.
     *
     * @param domainName  the domain
     * @param packageCode the product code, from {@code client.products().listSslProducts()}
     */
    public PurchaseCertificateRequest(String domainName, String packageCode) {
        this.domainName = domainName;
        this.packageCode = packageCode;
    }

    /**
     * Returns the domain.
     *
     * @return the domain name
     */
    public String domainName() {
        return domainName;
    }

    /**
     * Returns the product code.
     *
     * @return the package code
     */
    public String packageCode() {
        return packageCode;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "ssls/initialize";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.BILLABLE;
    }

    @Override
    protected Object body() {
        return JsonBody.create().add("packageCode", packageCode).add("domainName", domainName);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        require(packageCode, "packageCode");
    }
}
