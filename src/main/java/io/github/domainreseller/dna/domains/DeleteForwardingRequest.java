package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;

/**
 * {@code DELETE domains/forwards}: removes the URL forwarding of a domain.
 */
public final class DeleteForwardingRequest extends BaseRequest<Void> {
    private final String domainName;

    /**
     * Creates the request.
     *
     * @param domainName the domain, for example {@code example.com}
     */
    public DeleteForwardingRequest(String domainName) {
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
        return HttpMethod.DELETE;
    }

    @Override
    public String path() {
        return "domains/forwards";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.DESTRUCTIVE;
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
