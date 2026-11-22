package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;

/**
 * {@code GET domains/forwards}: the URL forwarding of a domain. Answers 404 ({@code NOT_FOUND}) when none is set up.
 */
public final class GetForwardingRequest extends BaseRequest<DomainForwarding> {
    private final String domainName;

    /**
     * Creates the request.
     *
     * @param domainName the domain, for example {@code example.com}
     */
    public GetForwardingRequest(String domainName) {
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
        return "domains/forwards";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("domainName", domainName);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
    }
}
