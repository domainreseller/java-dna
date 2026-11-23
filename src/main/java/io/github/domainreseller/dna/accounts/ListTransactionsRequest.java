package io.github.domainreseller.dna.accounts;

import io.github.domainreseller.dna.PagedRequest;
import io.github.domainreseller.dna.ProductType;
import io.github.domainreseller.dna.QueryParameters;
import io.github.domainreseller.dna.internal.Values;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * {@code GET deposit/transactions}: pages through the balance ledger, with optional filters.
 *
 * <pre>{@code
 * ListTransactionsRequest request = new ListTransactionsRequest()
 *         .chargeType(ChargeType.SUBTRACT)
 *         .startDate(OffsetDateTime.parse("2026-11-01T00:00:00Z"))
 *         .transactionTypes(TransactionType.REGISTER, TransactionType.RENEW);
 * client.account().listAllTransactions(request).forEach(System.out::println);
 * }</pre>
 */
public final class ListTransactionsRequest extends PagedRequest<AccountTransaction> {
    private List<TransactionType> transactionTypes = List.of();
    private List<ProductType> productTypes = List.of();
    private List<String> currencies = List.of();
    private ChargeType chargeType;
    private String traceId;
    private String description;
    private BigDecimal amount;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private String filterText;

    /** Creates a request for all transactions. */
    public ListTransactionsRequest() {
    }

    /**
     * Returns the transaction types to list.
     *
     * @return the types; empty for all
     */
    public List<TransactionType> transactionTypes() {
        return transactionTypes;
    }

    /**
     * Lists only these transaction types.
     *
     * @param transactionTypes the types
     * @return this request
     */
    public ListTransactionsRequest transactionTypes(TransactionType... transactionTypes) {
        this.transactionTypes = Values.list(Arrays.asList(transactionTypes));
        return this;
    }

    /**
     * Returns the product types to list.
     *
     * @return the types; empty for all
     */
    public List<ProductType> productTypes() {
        return productTypes;
    }

    /**
     * Lists only these product types.
     *
     * @param productTypes the types
     * @return this request
     */
    public ListTransactionsRequest productTypes(ProductType... productTypes) {
        this.productTypes = Values.list(Arrays.asList(productTypes));
        return this;
    }

    /**
     * Returns the account currencies to list.
     *
     * @return the currencies; empty for all
     */
    public List<String> currencies() {
        return currencies;
    }

    /**
     * Lists only these account currencies, for example {@code USD} or {@code TRY}.
     *
     * @param currencies the currencies
     * @return this request
     */
    public ListTransactionsRequest currencies(String... currencies) {
        this.currencies = Values.list(Arrays.asList(currencies));
        return this;
    }

    /**
     * Returns the charge type filter.
     *
     * @return the charge type, or null for both
     */
    public ChargeType chargeType() {
        return chargeType;
    }

    /**
     * Lists only credits or only debits.
     *
     * @param chargeType the charge type, or null for both
     * @return this request
     */
    public ListTransactionsRequest chargeType(ChargeType chargeType) {
        this.chargeType = chargeType;
        return this;
    }

    /**
     * Returns the trace ID filter.
     *
     * @return the trace ID, or null
     */
    public String traceId() {
        return traceId;
    }

    /**
     * Lists only the transaction with this trace ID.
     *
     * @param traceId the trace ID
     * @return this request
     */
    public ListTransactionsRequest traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    /**
     * Returns the description filter.
     *
     * @return the text, or null
     */
    public String description() {
        return description;
    }

    /**
     * Lists only transactions whose description contains this text.
     *
     * @param description the text
     * @return this request
     */
    public ListTransactionsRequest description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Returns the amount filter.
     *
     * @return the amount, or null
     */
    public BigDecimal amount() {
        return amount;
    }

    /**
     * Lists only transactions of this amount.
     *
     * @param amount the amount
     * @return this request
     */
    public ListTransactionsRequest amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Returns the start of the period.
     *
     * @return the start, or null
     */
    public OffsetDateTime startDate() {
        return startDate;
    }

    /**
     * Lists only transactions from this time.
     *
     * @param startDate the start
     * @return this request
     */
    public ListTransactionsRequest startDate(OffsetDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    /**
     * Returns the end of the period.
     *
     * @return the end, or null
     */
    public OffsetDateTime endDate() {
        return endDate;
    }

    /**
     * Lists only transactions until this time.
     *
     * @param endDate the end
     * @return this request
     */
    public ListTransactionsRequest endDate(OffsetDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    /**
     * Returns the free-text search.
     *
     * @return the text, or null
     */
    public String filterText() {
        return filterText;
    }

    /**
     * Sets a free-text search.
     *
     * @param filterText the text
     * @return this request
     */
    public ListTransactionsRequest filterText(String filterText) {
        this.filterText = filterText;
        return this;
    }

    @Override
    public ListTransactionsRequest skipCount(Integer skipCount) {
        super.skipCount(skipCount);
        return this;
    }

    @Override
    public ListTransactionsRequest maxResultCount(Integer maxResultCount) {
        super.maxResultCount(maxResultCount);
        return this;
    }

    @Override
    public ListTransactionsRequest sorting(String sorting) {
        super.sorting(sorting);
        return this;
    }

    @Override
    public String path() {
        return "deposit/transactions";
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.addEach("TransactionTypes", transactionTypes)
                .addEach("ProductTypes", productTypes)
                .addEach("AccountCurrencyTypes", currencies)
                .add("ChargeType", chargeType)
                .add("TraceId", traceId)
                .add("Description", description)
                .add("Amount", amount)
                .add("StartDate", startDate)
                .add("EndDate", endDate)
                .add("FilterText", filterText);
        super.addQueryParameters(query);
    }
}
