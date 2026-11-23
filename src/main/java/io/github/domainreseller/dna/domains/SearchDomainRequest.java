package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;

/**
 * {@code POST domains/search}: whether a domain can be registered, its price and whether the registry needs
 * documents. The gateway allows 2 searches per second.
 */
public final class SearchDomainRequest extends BaseRequest<DomainSearchResult> {
    private final String domainName;
    private final String currency;

    /**
     * Creates the request with the account's default currency.
     *
     * @param domainName the domain, for example {@code example.com}
     */
    public SearchDomainRequest(String domainName) {
        this(domainName, null);
    }

    /**
     * Creates the request.
     *
     * @param domainName the domain, for example {@code example.com}
     * @param currency   the price currency, for example {@code USD} or {@code TRY}; null for the account default
     */
    public SearchDomainRequest(String domainName, String currency) {
        this.domainName = domainName;
        this.currency = currency;
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
     * Returns the price currency.
     *
     * @return the currency, or null for the account default
     */
    public String currency() {
        return currency;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "domains/search";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }

    @Override
    protected Object body() {
        return JsonBody.create().add("domainName", domainName).add("currency", currency);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
    }
}
