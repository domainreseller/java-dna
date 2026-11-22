package io.github.domainreseller.dna.products;

import io.github.domainreseller.dna.PagedRequest;
import io.github.domainreseller.dna.QueryParameters;

/** {@code GET products/tlds}: pages through the TLDs with prices, periods, rules and attributes. Not rate limited. */
public final class ListTldsRequest extends PagedRequest<Tld> {
    private String filterText;
    private String currency;

    /** Creates a request for all TLDs. */
    public ListTldsRequest() {
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
     * Sets a free-text search, for example {@code com}.
     *
     * @param filterText the text
     * @return this request
     */
    public ListTldsRequest filterText(String filterText) {
        this.filterText = filterText;
        return this;
    }

    /**
     * Returns the price currency.
     *
     * @return the currency, or null for the account default
     */
    public String currency() {
        return currency;
    }

    /**
     * Sets the price currency, for example {@code USD} or {@code TRY}.
     *
     * @param currency the currency
     * @return this request
     */
    public ListTldsRequest currency(String currency) {
        this.currency = currency;
        return this;
    }

    @Override
    public ListTldsRequest skipCount(Integer skipCount) {
        super.skipCount(skipCount);
        return this;
    }

    @Override
    public ListTldsRequest maxResultCount(Integer maxResultCount) {
        super.maxResultCount(maxResultCount);
        return this;
    }

    @Override
    public ListTldsRequest sorting(String sorting) {
        super.sorting(sorting);
        return this;
    }

    @Override
    public String path() {
        return "products/tlds";
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("FilterText", filterText).add("Currency", currency);
        super.addQueryParameters(query);
    }
}
