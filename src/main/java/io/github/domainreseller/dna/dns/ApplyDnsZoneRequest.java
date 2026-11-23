package io.github.domainreseller.dna.dns;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;

/**
 * {@code POST domains/zones/apply}: switches the domain to the platform nameservers so its DNS zone is served. Until
 * then, records you add are stored but not visible on the internet. The request has no body.
 */
public final class ApplyDnsZoneRequest extends BaseRequest<ZoneApplyResult> {
    private final String domainName;

    /**
     * Creates the request.
     *
     * @param domainName the domain
     */
    public ApplyDnsZoneRequest(String domainName) {
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
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "domains/zones/apply";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.WRITE;
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
