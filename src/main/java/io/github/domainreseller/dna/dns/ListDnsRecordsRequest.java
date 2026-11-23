package io.github.domainreseller.dna.dns;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;
import java.util.List;

/** {@code GET domains/zones}: the DNS record sets of a domain. */
public final class ListDnsRecordsRequest extends BaseRequest<List<DnsRecordSet>> {
    private final String domainName;

    /**
     * Creates the request.
     *
     * @param domainName the domain
     */
    public ListDnsRecordsRequest(String domainName) {
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
        return "domains/zones";
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
