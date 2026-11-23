package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;

/** {@code POST domains/privacy}: turns WHOIS privacy protection on or off. */
public final class SetPrivacyProtectionRequest extends BaseRequest<Void> {
    private final String domainName;
    private final boolean enabled;

    /**
     * Creates the request.
     *
     * @param domainName the domain
     * @param enabled    true to hide the contacts in WHOIS, false to show them
     */
    public SetPrivacyProtectionRequest(String domainName, boolean enabled) {
        this.domainName = domainName;
        this.enabled = enabled;
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
     * Returns whether privacy protection is turned on.
     *
     * @return true to turn it on
     */
    public boolean enabled() {
        return enabled;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "domains/privacy";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.WRITE;
    }

    @Override
    protected Object body() {
        return JsonBody.create().add("domainName", domainName).add("privacyStatus", enabled);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
    }
}
