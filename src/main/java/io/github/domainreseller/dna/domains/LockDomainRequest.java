package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;

/**
 * {@code POST domains/lock}: turns the transfer lock on.
 */
public final class LockDomainRequest extends BaseRequest<Void> {
    private final String domainName;

    /**
     * Creates the request.
     *
     * @param domainName the domain, for example {@code example.com}
     */
    public LockDomainRequest(String domainName) {
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
        return "domains/lock";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.WRITE;
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
