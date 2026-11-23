package io.github.domainreseller.dna.products;

import io.github.domainreseller.dna.PagedRequest;
import io.github.domainreseller.dna.QueryParameters;

/** {@code GET products/ssls}: pages through the SSL certificate products with prices. */
public final class ListSslProductsRequest extends PagedRequest<SslProduct> {
    private String filterText;
    private String currency;
    private Integer validationType;
    private Boolean isWildcard;
    private Boolean isMultiDomain;
    private String vendorName;

    /** Creates a request for all SSL products. */
    public ListSslProductsRequest() {
    }

    /**
     * Returns the free-text search.
     *
     * @return the text, or null
     */
    public String filterText() {
        return filterText;
    }

    /**
     * Sets a free-text search.
     *
     * @param filterText the text
     * @return this request
     */
    public ListSslProductsRequest filterText(String filterText) {
        this.filterText = filterText;
        return this;
    }

    /**
     * Returns the price currency.
     *
     * @return the currency, or null
     */
    public String currency() {
        return currency;
    }

    /**
     * Sets the price currency.
     *
     * @param currency the currency
     * @return this request
     */
    public ListSslProductsRequest currency(String currency) {
        this.currency = currency;
        return this;
    }

    /**
     * Returns the validation level filter.
     *
     * @return the level as the API numbers it, or null
     */
    public Integer validationType() {
        return validationType;
    }

    /**
     * Lists only this validation level, as the API numbers it.
     *
     * @param validationType the level
     * @return this request
     */
    public ListSslProductsRequest validationType(Integer validationType) {
        this.validationType = validationType;
        return this;
    }

    /**
     * Returns the wildcard filter.
     *
     * @return the filter, or null
     */
    public Boolean isWildcard() {
        return isWildcard;
    }

    /**
     * Lists only wildcard (true) or only non-wildcard (false) products.
     *
     * @param isWildcard the filter, or null for both
     * @return this request
     */
    public ListSslProductsRequest isWildcard(Boolean isWildcard) {
        this.isWildcard = isWildcard;
        return this;
    }

    /**
     * Returns the multi-domain filter.
     *
     * @return the filter, or null
     */
    public Boolean isMultiDomain() {
        return isMultiDomain;
    }

    /**
     * Lists only multi-domain (true) or only single-domain (false) products.
     *
     * @param isMultiDomain the filter, or null for both
     * @return this request
     */
    public ListSslProductsRequest isMultiDomain(Boolean isMultiDomain) {
        this.isMultiDomain = isMultiDomain;
        return this;
    }

    /**
     * Returns the certificate authority filter.
     *
     * @return the vendor, or null
     */
    public String vendorName() {
        return vendorName;
    }

    /**
     * Lists only products of this certificate authority.
     *
     * @param vendorName the vendor
     * @return this request
     */
    public ListSslProductsRequest vendorName(String vendorName) {
        this.vendorName = vendorName;
        return this;
    }

    @Override
    public ListSslProductsRequest skipCount(Integer skipCount) {
        super.skipCount(skipCount);
        return this;
    }

    @Override
    public ListSslProductsRequest maxResultCount(Integer maxResultCount) {
        super.maxResultCount(maxResultCount);
        return this;
    }

    @Override
    public ListSslProductsRequest sorting(String sorting) {
        super.sorting(sorting);
        return this;
    }

    @Override
    public String path() {
        return "products/ssls";
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("FilterText", filterText)
                .add("Currency", currency)
                .add("ValidationType", validationType)
                .add("IsWildcard", isWildcard)
                .add("IsMultiDomain", isMultiDomain)
                .add("VendorName", vendorName);
        super.addQueryParameters(query);
    }
}
