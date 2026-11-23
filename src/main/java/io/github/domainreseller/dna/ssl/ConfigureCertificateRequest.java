package io.github.domainreseller.dna.ssl;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;
import java.util.UUID;

/**
 * {@code PUT ssls/configure}: submits the CSR, the validation method and the contact so the certificate can be
 * issued. A certificate must be configured within 15 days of its purchase or renewal; after that it is cancelled.
 *
 * <pre>{@code
 * client.ssl().configure(new ConfigureCertificateRequest(certificateId, csr, contact)
 *         .validationMethod(ValidationMethod.EMAIL)
 *         .approverEmail("admin@example.com")
 *         .webServerType(WebServerType.APACHE));
 * }</pre>
 */
public final class ConfigureCertificateRequest extends BaseRequest<SslOperationResult> {
    private final UUID certificateId;
    private final String csr;
    private final CertificateContact contact;
    private String privateKey;
    private ValidationMethod validationMethod;
    private String approverEmail;
    private WebServerType webServerType;
    private String sanList;
    private String wildcardList;

    /**
     * Creates the request.
     *
     * @param certificateId the certificate to configure: the ID returned by the purchase or the renewal
     * @param csr           the CSR in PEM format; its common name must be the certificate's domain
     * @param contact       the certificate contact
     */
    public ConfigureCertificateRequest(UUID certificateId, String csr, CertificateContact contact) {
        this.certificateId = certificateId;
        this.csr = csr;
        this.contact = contact;
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
     * Returns the CSR.
     *
     * @return the CSR in PEM format
     */
    public String csr() {
        return csr;
    }

    /**
     * Returns the certificate contact.
     *
     * @return the contact
     */
    public CertificateContact contact() {
        return contact;
    }

    /**
     * Returns the private key.
     *
     * @return the private key, or null
     */
    public String privateKey() {
        return privateKey;
    }

    /**
     * Sets the private key that belongs to the CSR, if you want the API to keep it with the order.
     *
     * @param privateKey the private key in PEM format
     * @return this request
     */
    public ConfigureCertificateRequest privateKey(String privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    /**
     * Returns how domain control is validated.
     *
     * @return the validation method, or null
     */
    public ValidationMethod validationMethod() {
        return validationMethod;
    }

    /**
     * Sets how domain control is validated (the API's {@code authenticationType}). E-mail validation needs an
     * approver address.
     *
     * @param validationMethod the validation method
     * @return this request
     */
    public ConfigureCertificateRequest validationMethod(ValidationMethod validationMethod) {
        this.validationMethod = validationMethod;
        return this;
    }

    /**
     * Returns the approver address.
     *
     * @return the approver e-mail address, or null
     */
    public String approverEmail() {
        return approverEmail;
    }

    /**
     * Sets the approver address for e-mail validation (the API's {@code approverMail}). The allowed addresses come
     * from {@link SslClient#getApproverEmails(UUID)}.
     *
     * @param approverEmail the approver e-mail address
     * @return this request
     */
    public ConfigureCertificateRequest approverEmail(String approverEmail) {
        this.approverEmail = approverEmail;
        return this;
    }

    /**
     * Returns the web server software.
     *
     * @return the web server type, or null
     */
    public WebServerType webServerType() {
        return webServerType;
    }

    /**
     * Sets the web server software.
     *
     * @param webServerType the web server type
     * @return this request
     */
    public ConfigureCertificateRequest webServerType(WebServerType webServerType) {
        this.webServerType = webServerType;
        return this;
    }

    /**
     * Returns the additional domain names.
     *
     * @return the names, or null
     */
    public String sanList() {
        return sanList;
    }

    /**
     * Sets additional domain names for multi-domain certificates.
     *
     * @param sanList the names, as the API expects them
     * @return this request
     */
    public ConfigureCertificateRequest sanList(String sanList) {
        this.sanList = sanList;
        return this;
    }

    /**
     * Returns the wildcard names.
     *
     * @return the names, or null
     */
    public String wildcardList() {
        return wildcardList;
    }

    /**
     * Sets wildcard names.
     *
     * @param wildcardList the names, as the API expects them
     * @return this request
     */
    public ConfigureCertificateRequest wildcardList(String wildcardList) {
        this.wildcardList = wildcardList;
        return this;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.PUT;
    }

    @Override
    public String path() {
        return "ssls/configure";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.WRITE;
    }

    @Override
    protected Object body() {
        return JsonBody.create()
                .add("certificateId", certificateId)
                .add("csr", csr)
                .add("privateKey", privateKey)
                .add("approverMail", approverEmail)
                .add("authenticationType", validationMethod)
                .add("webServerType", webServerType)
                .add("sanList", sanList)
                .add("wildcardList", wildcardList)
                .add("contact", contact);
    }

    @Override
    protected void validate() {
        SslValidation.requireCertificateId(certificateId);
        require(csr, "csr");
        require(contact, "contact");
        contact.ensureComplete("contact");
        if (ValidationMethod.EMAIL.equals(validationMethod) && (approverEmail == null || approverEmail.isBlank())) {
            throw new IllegalArgumentException("E-mail validation needs an approverEmail.");
        }
    }
}
