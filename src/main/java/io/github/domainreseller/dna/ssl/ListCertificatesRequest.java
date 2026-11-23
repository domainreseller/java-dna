package io.github.domainreseller.dna.ssl;

import io.github.domainreseller.dna.PagedRequest;
import io.github.domainreseller.dna.QueryParameters;

/**
 * {@code GET ssls}: pages through your certificates, newest first. Use it to find a certificate's ID. The API does not
 * sort this list on request, so {@link #sorting(String)} must stay unset.
 */
public final class ListCertificatesRequest extends PagedRequest<CertificateListItem> {
    private String domainName;

    /** Creates a request for all certificates. */
    public ListCertificatesRequest() {
    }

    /**
     * Returns the domain filter.
     *
     * @return the domain, or null for all
     */
    public String domainName() {
        return domainName;
    }

    /**
     * Lists only certificates of this domain. For wildcard certificates both {@code *.example.com} and
     * {@code example.com} match.
     *
     * @param domainName the domain
     * @return this request
     */
    public ListCertificatesRequest domainName(String domainName) {
        this.domainName = domainName;
        return this;
    }

    @Override
    public ListCertificatesRequest skipCount(Integer skipCount) {
        super.skipCount(skipCount);
        return this;
    }

    @Override
    public ListCertificatesRequest maxResultCount(Integer maxResultCount) {
        super.maxResultCount(maxResultCount);
        return this;
    }

    @Override
    public ListCertificatesRequest sorting(String sorting) {
        super.sorting(sorting);
        return this;
    }

    @Override
    public String path() {
        return "ssls";
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("DomainName", domainName).add("SkipCount", skipCount()).add("MaxResultCount", maxResultCount());
    }

    @Override
    protected void validate() {
        super.validate();
        if (sorting() != null && !sorting().isBlank()) {
            throw new IllegalArgumentException(
                    "The certificate list cannot be sorted; it is always newest first. Leave sorting unset.");
        }
    }
}
