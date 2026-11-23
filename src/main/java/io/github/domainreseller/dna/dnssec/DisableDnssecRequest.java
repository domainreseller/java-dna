package io.github.domainreseller.dna.dnssec;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;

/** {@code POST domains/dns/dnssec/disable}: removes every DS record, which turns DNSSEC off. */
public final class DisableDnssecRequest extends BaseRequest<DnssecOperationResult> {
    private final String domainName;

    /**
     * Creates the request.
     *
     * @param domainName the domain
     */
    public DisableDnssecRequest(String domainName) {
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
        return "domains/dns/dnssec/disable";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.DESTRUCTIVE;
    }

    @Override
    protected Object body() {
        return JsonBody.create().add("domainName", domainName);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
    }
}
