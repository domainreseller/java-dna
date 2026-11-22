package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;

/** {@code POST domains/forwards}: forwards a domain to a URL. The API rewrites the domain's zone for it. */
public final class SetForwardingRequest extends BaseRequest<Void> {
    private final String domainName;
    private final String redirectAddress;
    private final ForwardType forwardType;

    /**
     * Creates the request with a standard (HTTP 301) forward.
     *
     * @param domainName      the domain
     * @param redirectAddress the target URL, for example {@code https://www.example.net}
     */
    public SetForwardingRequest(String domainName, String redirectAddress) {
        this(domainName, redirectAddress, ForwardType.STANDARD);
    }

    /**
     * Creates the request.
     *
     * @param domainName      the domain
     * @param redirectAddress the target URL, for example {@code https://www.example.net}
     * @param forwardType     {@link ForwardType#STANDARD} (HTTP 301) or {@link ForwardType#FRAME} (masked); null
     *                        means standard
     */
    public SetForwardingRequest(String domainName, String redirectAddress, ForwardType forwardType) {
        this.domainName = domainName;
        this.redirectAddress = redirectAddress;
        this.forwardType = forwardType == null ? ForwardType.STANDARD : forwardType;
    }

    /**
     * Returns the domain.
     *
     * @return the domain name
     */
    public String domainName() {
        return domainName;
    }

    /**
     * Returns the target URL.
     *
     * @return the URL
     */
    public String redirectAddress() {
        return redirectAddress;
    }

    /**
     * Returns how the domain forwards.
     *
     * @return the forward type
     */
    public ForwardType forwardType() {
        return forwardType;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "domains/forwards";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.WRITE;
    }

    @Override
    protected Object body() {
        return JsonBody.create()
                .add("domainName", domainName)
                .add("redirectAddress", redirectAddress)
                .add("forwardType", forwardType);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        require(redirectAddress, "redirectAddress");
    }
}
