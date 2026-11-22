package io.github.domainreseller.dna.accounts;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;

/**
 * {@code GET deposit/accounts/me}: your reseller ID, name, price group and TRY/USD balances. Also the cheapest way to
 * check that the credentials work.
 */
public final class GetAccountRequest extends BaseRequest<ResellerAccount> {
    /** Creates the request. */
    public GetAccountRequest() {
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }

    @Override
    public String path() {
        return "deposit/accounts/me";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }
}
