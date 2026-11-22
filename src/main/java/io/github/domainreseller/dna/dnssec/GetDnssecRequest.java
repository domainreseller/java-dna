package io.github.domainreseller.dna.dnssec;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;

/** {@code GET domains/dns/dnssec}: DNSSEC state, DS records and the algorithms the registry allows. */
public final class GetDnssecRequest extends BaseRequest<DnssecInfo> {
    private final String domainName;

    /**
     * Creates the request.
     *
     * @param domainName the domain
     */
    public GetDnssecRequest(String domainName) {
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
        return "domains/dns/dnssec";
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
