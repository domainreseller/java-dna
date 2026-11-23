package io.github.domainreseller.dna.accounts;

import io.github.domainreseller.dna.DomainNameApiClient;
import io.github.domainreseller.dna.Page;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Your reseller account: balances, profile and the transaction ledger. Reach it through
 * {@link DomainNameApiClient#account()}.
 */
public final class AccountClient {
    private final DomainNameApiClient api;

    /**
     * Creates the client. Use {@link DomainNameApiClient#account()} instead.
     *
     * @param api the client that sends the requests
     */
    public AccountClient(DomainNameApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    /**
     * Returns your reseller ID, name, price group and TRY/USD balances.
     *
     * @return the account
     */
    public ResellerAccount get() {
        return api.send(new GetAccountRequest());
    }

    /**
     * Returns your reseller profile: name, company, price group, account currency, status and flags.
     *
     * @return the profile
     */
    public ResellerProfile getProfile() {
        return api.send(new GetProfileRequest());
    }

    /**
     * Returns the first page of the balance ledger.
     *
     * @return the page
     */
    public Page<AccountTransaction> listTransactions() {
        return listTransactions(new ListTransactionsRequest());
    }

    /**
     * Returns one page of the balance ledger.
     *
     * @param request the filters and the page
     * @return the page
     */
    public Page<AccountTransaction> listTransactions(ListTransactionsRequest request) {
        return api.send(request);
    }

    /**
     * Returns every transaction, fetching pages as the stream is consumed.
     *
     * @return the transactions
     */
    public Stream<AccountTransaction> listAllTransactions() {
        return listAllTransactions(new ListTransactionsRequest());
    }

    /**
     * Returns every transaction that matches the filters, fetching pages as the stream is consumed.
     *
     * @param request the filters
     * @return the transactions
     */
    public Stream<AccountTransaction> listAllTransactions(ListTransactionsRequest request) {
        return api.listAll(request);
    }
}
