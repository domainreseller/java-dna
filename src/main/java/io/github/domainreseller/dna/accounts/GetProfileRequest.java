package io.github.domainreseller.dna.accounts;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;

/**
 * {@code GET resellers/me}: your reseller profile (name, company, price group, account currency, status and flags).
 * For the balances use {@link GetAccountRequest}.
 */
public final class GetProfileRequest extends BaseRequest<ResellerProfile> {
    /** Creates the request. */
    public GetProfileRequest() {
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }

    @Override
    public String path() {
        return "resellers/me";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }
}
