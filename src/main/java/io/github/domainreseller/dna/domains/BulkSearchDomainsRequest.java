package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.internal.Values;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** {@code POST domains/bulk-search}: availability and prices of several domains in one call. */
public final class BulkSearchDomainsRequest extends BaseRequest<BulkSearchResult> {
    private final List<String> domainNames;
    private final String currency;

    /**
     * Creates the request with the account's default currency.
     *
     * @param domainNames the domains, for example {@code example.com} and {@code example.net}
     */
    public BulkSearchDomainsRequest(Collection<String> domainNames) {
        this(domainNames, null);
    }

    /**
     * Creates the request.
     *
     * @param domainNames the domains
     * @param currency    the price currency, used for every domain; null for the account default
     */
    public BulkSearchDomainsRequest(Collection<String> domainNames, String currency) {
        this.domainNames = Values.list(domainNames);
        this.currency = currency;
    }

    /**
     * Returns the domains.
     *
     * @return the domain names
     */
    public List<String> domainNames() {
        return domainNames;
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
        return "domains/bulk-search";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }

    /** The API expects an array of {@code {domainName, currency}} objects. */
    @Override
    protected Object body() {
        List<JsonBody> items = new ArrayList<>();
        for (String name : domainNames) {
            items.add(JsonBody.create().add("domainName", name.trim()).add("currency", currency));
        }
        return items;
    }

    @Override
    protected void validate() {
        require(domainNames, "domainNames");
        for (String name : domainNames) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("domainNames contains an empty value.");
            }
        }
    }
}
