package io.github.domainreseller.dna.ssl;

import io.github.domainreseller.dna.DomainNameApiClient;
import io.github.domainreseller.dna.Page;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * SSL certificates. Reach it through {@link DomainNameApiClient#ssl()}.
 *
 * <p>The usual flow: {@link #purchase}, then {@link #configure} with a CSR ({@link #generateCsr} can make one), then
 * {@link #refreshStatus} until it is issued, then {@link #getContent}.
 *
 * <p>A domain can have several certificates, so an existing certificate is addressed by its ID, never by its domain
 * name. {@link #purchase} and {@link #renew} return the ID ({@link SslOperationResult#certificateId()}): store it.
 * {@link #list(ListCertificatesRequest)} finds the IDs of the certificates you already have. A purchased or renewed
 * certificate must be configured within 15 days. To renew, call {@link #renew} and then {@link #configure} with the new
 * ID; a second {@link #purchase} is not a renewal.
 */
public final class SslClient {
    private final DomainNameApiClient api;

    /**
     * Creates the client. Use {@link DomainNameApiClient#ssl()} instead.
     *
     * @param api the client that sends the requests
     */
    public SslClient(DomainNameApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    /**
     * Returns the first page of your certificates, newest first.
     *
     * @return the page
     */
    public Page<CertificateListItem> list() {
        return list(new ListCertificatesRequest());
    }

    /**
     * Returns one page of your certificates, newest first. Use it to find a certificate's ID.
     *
     * @param request the filter and page
     * @return the page
     */
    public Page<CertificateListItem> list(ListCertificatesRequest request) {
        return api.send(request);
    }

    /**
     * Returns every certificate, newest first, fetching pages as the stream is consumed.
     *
     * @return the certificates
     */
    public Stream<CertificateListItem> listAll() {
        return listAll(new ListCertificatesRequest());
    }

    /**
     * Returns every certificate that matches the filter, newest first, fetching pages as the stream is consumed.
     *
     * @param request the filter
     * @return the certificates
     */
    public Stream<CertificateListItem> listAll(ListCertificatesRequest request) {
        return api.listAll(request);
    }

    /**
     * Generates a CSR and a private key. Charges nothing.
     *
     * @param request the subject of the CSR
     * @return the CSR and its private key
     */
    public GeneratedCsr generateCsr(GenerateCsrRequest request) {
        return api.send(request);
    }

    /**
     * Decodes a CSR and returns its subject.
     *
     * @param csr the CSR in PEM format
     * @return the subject
     */
    public CsrDetails decodeCsr(String csr) {
        return api.send(new DecodeCsrRequest(csr));
    }

    /**
     * Buys an SSL certificate package for a domain. <b>Charges your balance.</b> Store the returned
     * {@link SslOperationResult#certificateId()} and configure the certificate within 15 days.
     *
     * @param domainName  the domain
     * @param packageCode the product code
     * @return the result with the new certificate's ID
     */
    public SslOperationResult purchase(String domainName, String packageCode) {
        return api.send(new PurchaseCertificateRequest(domainName, packageCode));
    }

    /**
     * Renews a certificate for another year. <b>Charges your balance</b> (the renewal price). The returned
     * {@link SslOperationResult#certificateId()} is the ID of a <b>new</b> certificate: configure that one with
     * {@link #configure} within 15 days. Allowed from 30 days before the subscription ends.
     *
     * @param certificateId the certificate to renew
     * @return the result with the new certificate's ID
     */
    public SslOperationResult renew(UUID certificateId) {
        return api.send(new RenewCertificateRequest(certificateId));
    }

    /**
     * Submits the CSR, validation method and contact so the certificate can be issued.
     *
     * @param request the configuration
     * @return the result
     */
    public SslOperationResult configure(ConfigureCertificateRequest request) {
        return api.send(request);
    }

    /**
     * Asks the certificate authority for the current state and returns it.
     *
     * @param certificateId the certificate
     * @return the state
     */
    public CertificateStatus refreshStatus(UUID certificateId) {
        return api.send(new RefreshCertificateStatusRequest(certificateId));
    }

    /**
     * Returns the details of a certificate.
     *
     * @param certificateId the certificate
     * @return the details
     */
    public CertificateDetails get(UUID certificateId) {
        return api.send(new GetCertificateRequest(certificateId));
    }

    /**
     * Downloads the issued certificate and its chain in PEM format.
     *
     * @param certificateId the certificate
     * @return the certificate and its chain
     */
    public CertificateContent getContent(UUID certificateId) {
        return api.send(new GetCertificateContentRequest(certificateId));
    }

    /**
     * Reissues a certificate with a new CSR and key, or a new approver.
     *
     * @param request the reissue
     * @return the result
     */
    public SslOperationResult reissue(ReissueCertificateRequest request) {
        return api.send(request);
    }

    /**
     * Returns the e-mail addresses allowed for e-mail validation of an existing certificate.
     *
     * @param certificateId the certificate
     * @return the addresses
     */
    public List<String> getApproverEmails(UUID certificateId) {
        return api.send(new GetApproverEmailsRequest(certificateId)).emails();
    }

    /**
     * Returns the e-mail addresses allowed for e-mail validation of a domain, before you buy a certificate for it.
     *
     * @param domainName  the domain
     * @param packageCode the product code you are about to buy
     * @return the addresses
     */
    public List<String> getApproverEmails(String domainName, String packageCode) {
        return api.send(new GetApproverEmailsRequest(domainName, packageCode)).emails();
    }

    /**
     * Revokes a certificate. <b>This cannot be undone.</b>
     *
     * @param certificateId the certificate
     * @return the result
     */
    public SslOperationResult revoke(UUID certificateId) {
        return api.send(new RevokeCertificateRequest(certificateId));
    }
}
