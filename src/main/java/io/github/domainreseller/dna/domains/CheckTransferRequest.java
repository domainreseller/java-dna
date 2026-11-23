package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;

/** {@code POST domains/transfers/check}: whether a domain can be transferred in with an auth code. Charges nothing. */
public final class CheckTransferRequest extends BaseRequest<TransferCheck> {
    private final String domainName;
    private final String authCode;

    /**
     * Creates the request.
     *
     * @param domainName the domain
     * @param authCode   the transfer (EPP) code
     */
    public CheckTransferRequest(String domainName, String authCode) {
        this.domainName = domainName;
        this.authCode = authCode;
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
     * Returns the transfer code.
     *
     * @return the auth code
     */
    public String authCode() {
        return authCode;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "domains/transfers/check";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }

    @Override
    protected Object body() {
        return JsonBody.create().add("domainName", domainName).add("authCode", authCode);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        require(authCode, "authCode");
    }
}
