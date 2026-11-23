package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;

/**
 * {@code GET domains/info}: the full details of a domain: dates, state, lock and privacy, nameservers, child
 * nameservers, contacts, auth code and registry attributes. Not rate limited.
 */
public final class GetDomainRequest extends BaseRequest<DomainDetails> {
    private final String domainName;

    /**
     * Creates the request.
     *
     * @param domainName the domain, for example {@code example.com}
     */
    public GetDomainRequest(String domainName) {
        this.domainName = domainName;
    }

    /**
     * Returns the domain.
     *
     * @return the domain name
     */
    public String domainName() {
        return domainName;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }

    @Override
    public String path() {
        return "domains/info";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("DomainName", domainName);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
    }
}
