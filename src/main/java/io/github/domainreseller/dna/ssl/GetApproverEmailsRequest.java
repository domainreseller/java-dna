package io.github.domainreseller.dna.ssl;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;
import java.util.UUID;

/**
 * {@code GET ssls/approvers}: the e-mail addresses allowed for e-mail validation. Ask either for an existing
 * certificate ({@link #GetApproverEmailsRequest(UUID)}) or, before a purchase, for a domain and the package you are
 * about to buy ({@link #GetApproverEmailsRequest(String, String)}).
 */
public final class GetApproverEmailsRequest extends BaseRequest<ApproverEmailsResult> {
    private final UUID certificateId;
    private final String domainName;
    private final String packageCode;

    /**
     * Creates the request for an existing certificate. Its domain and package are used.
     *
     * @param certificateId the certificate
     */
    public GetApproverEmailsRequest(UUID certificateId) {
        this.certificateId = certificateId;
        this.domainName = null;
        this.packageCode = null;
    }

    /**
     * Creates the request for a domain you have not bought a certificate for yet.
     *
     * @param domainName  the domain
     * @param packageCode the product code you are about to buy
     */
    public GetApproverEmailsRequest(String domainName, String packageCode) {
        this.certificateId = null;
        this.domainName = domainName;
        this.packageCode = packageCode;
    }

    /**
     * Returns the existing certificate.
     *
     * @return the certificate ID, or null before a purchase
     */
    public UUID certificateId() {
        return certificateId;
    }

    /**
     * Returns the domain.
     *
     * @return the domain name, or null for an existing certificate
     */
    public String domainName() {
        return domainName;
    }

    /**
     * Returns the product code.
     *
     * @return the package code, or null for an existing certificate
     */
    public String packageCode() {
        return packageCode;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }

    @Override
    public String path() {
        return "ssls/approvers";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("CertificateId", certificateId).add("DomainName", domainName).add("PackageCode", packageCode);
    }

    @Override
    protected void validate() {
        if (certificateId != null) {
            SslValidation.requireCertificateId(certificateId);
            return;
        }
        if (domainName == null || domainName.isBlank()) {
            throw new IllegalArgumentException("Either certificateId (an existing certificate) or domainName and "
                    + "packageCode (before a purchase) is required.");
        }
        if (packageCode == null || packageCode.isBlank()) {
            throw new IllegalArgumentException("packageCode is required with domainName. For a certificate you already "
                    + "bought, pass its certificateId instead.");
        }
    }
}
