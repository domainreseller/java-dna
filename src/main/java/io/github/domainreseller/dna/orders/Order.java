package io.github.domainreseller.dna.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.domainreseller.dna.OrderType;
import io.github.domainreseller.dna.ProductType;
import io.github.domainreseller.dna.internal.Values;
import java.math.BigDecimal;
import java.util.List;

/**
 * An order and its lines.
 *
 * @param orderCode      the order code
 * @param status         the state
 * @param amount         the total amount
 * @param amountCurrency the currency of {@code amount}
 * @param lines          the order lines; never null
 */
public record Order(
        String orderCode,
        OrderStatus status,
        BigDecimal amount,
        String amountCurrency,
        @JsonProperty("products") List<Line> lines) {
    /**
     * Creates an order.
     *
     * @param orderCode      the order code
     * @param status         the state
     * @param amount         the total amount
     * @param amountCurrency the currency
     * @param lines          the order lines; null becomes an empty list
     */
    public Order {
        lines = Values.list(lines);
    }

    /**
     * One line of an order.
     *
     * @param productCode    the product code
     * @param name           the product name
     * @param description    a description, usually naming the domain
     * @param productType    the kind of product
     * @param orderType      the kind of order
     * @param status         the state of the line
     * @param price          the price
     * @param priceCurrency  the currency of {@code price}
     * @param period         the period in years
     * @param additionalData extra data the API stores with the line
     */
    public record Line(
            String productCode,
            String name,
            String description,
            ProductType productType,
            OrderType orderType,
            OrderLineStatus status,
            BigDecimal price,
            String priceCurrency,
            int period,
            String additionalData) {
    }
}
