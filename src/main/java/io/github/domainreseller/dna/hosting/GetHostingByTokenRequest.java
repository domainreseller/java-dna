package io.github.domainreseller.dna.hosting;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;

/** {@code GET hostings/by-token}: a hosting, looked up by the token returned when it was ordered. */
public final class GetHostingByTokenRequest extends BaseRequest<HostingService> {
    private final String token;

    /**
     * Creates the request.
     *
     * @param token the token from {@link HostingCreateResult#token()}
     */
    public GetHostingByTokenRequest(String token) {
        this.token = token;
    }

    /**
     * Returns the token.
     *
     * @return the token
     */
    public String token() {
        return token;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }

    @Override
    public String path() {
        return "hostings/by-token";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("token", token);
    }

    @Override
    protected void validate() {
        require(token, "token");
    }
}
