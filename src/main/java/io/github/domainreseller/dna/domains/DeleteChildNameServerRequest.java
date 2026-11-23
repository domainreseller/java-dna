package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;

/** {@code DELETE domains/dns/host}: deletes a child nameserver. */
public final class DeleteChildNameServerRequest extends BaseRequest<Void> {
    private final String domainName;
    private final String hostName;

    /**
     * Creates the request.
     *
     * @param domainName the parent domain
     * @param hostName   the nameserver host name
     */
    public DeleteChildNameServerRequest(String domainName, String hostName) {
        this.domainName = domainName;
        this.hostName = hostName;
    }

    /**
     * Returns the parent domain.
     *
     * @return the domain name
     */
    public String domainName() {
        return domainName;
    }

    /**
     * Returns the host name.
     *
     * @return the host name
     */
    public String hostName() {
        return hostName;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.DELETE;
    }

    @Override
    public String path() {
        return "domains/dns/host";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.DESTRUCTIVE;
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("DomainName", domainName).add("HostName", hostName);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        require(hostName, "hostName");
    }
}
