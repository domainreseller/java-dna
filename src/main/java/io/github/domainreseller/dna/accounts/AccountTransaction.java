package io.github.domainreseller.dna.accounts;

import io.github.domainreseller.dna.ProductType;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * One entry of the balance ledger: a charge, refund or deposit.
 *
 * @param traceId          the trace ID that ties the transaction to an operation
 * @param type             what the transaction was for
 * @param status           the outcome
 * @param chargeType       whether money was added or taken
 * @param amount           the amount in {@code amountCurrency}
 * @param amountCurrency   the currency of {@code amount}
 * @param exchangeAmount   the amount after currency conversion
 * @param exchangeCurrency the exchange figure the API reports with {@code exchangeAmount}
 * @param productType      the kind of product involved
 * @param productId        the product's ID
 * @param description      a description, usually naming the domain or product
 * @param period           the period in years
 * @param creationTime     when the transaction was recorded
 * @param resellerName     the reseller the transaction belongs to
 * @param balance          the balance after the transaction
 * @param oldBalance       the balance before the transaction
 */
public record AccountTransaction(
        String traceId,
        TransactionType type,
        TransactionStatus status,
        ChargeType chargeType,
        BigDecimal amount,
        String amountCurrency,
        BigDecimal exchangeAmount,
        BigDecimal exchangeCurrency,
        ProductType productType,
        UUID productId,
        String description,
        int period,
        OffsetDateTime creationTime,
        String resellerName,
        BigDecimal balance,
        BigDecimal oldBalance) {
}
