package io.github.domainreseller.dna.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.domainreseller.dna.internal.Values;
import java.math.BigDecimal;
import java.util.List;

/**
 * An SSL certificate product with its prices.
 *
 * @param name             the product name
 * @param productCode      the product code, used to buy the certificate
 * @param vendorName       the certificate authority
 * @param validationType   the validation level (DV, OV, EV)
 * @param isWildcard       whether it covers {@code *.example.com}
 * @param isMultiDomain    whether it covers several domain names
 * @param defaultSanCount  domain names included in the price
 * @param maxSanCount      the most domain names it can cover
 * @param warrantyAmount   the warranty amount
 * @param issueTimeMinutes the usual time to issue, in minutes
 * @param isReissueFree    whether reissuing is free
 * @param requiresCsr      whether a CSR is needed
 * @param prices           prices per price group; never null
 */
public record SslProduct(
        String name,
        String productCode,
        String vendorName,
        String validationType,
        @JsonProperty("isWildcard") boolean isWildcard,
        @JsonProperty("isMultiDomain") boolean isMultiDomain,
        int defaultSanCount,
        int maxSanCount,
        BigDecimal warrantyAmount,
        int issueTimeMinutes,
        @JsonProperty("isReissueFree") boolean isReissueFree,
        boolean requiresCsr,
        List<PriceGroup> prices) {
    /**
     * Creates a product.
     *
     * @param name             the product name
     * @param productCode      the product code
     * @param vendorName       the certificate authority
     * @param validationType   the validation level
     * @param isWildcard       whether it is a wildcard certificate
     * @param isMultiDomain    whether it covers several names
     * @param defaultSanCount  names included in the price
     * @param maxSanCount      the most names it can cover
     * @param warrantyAmount   the warranty amount
     * @param issueTimeMinutes the usual time to issue
     * @param isReissueFree    whether reissuing is free
     * @param requiresCsr      whether a CSR is needed
     * @param prices           prices per price group; null becomes an empty list
     */
    public SslProduct {
        prices = Values.list(prices);
    }

    /**
     * The prices of an SSL product in one price group.
     *
     * @param priceGroup the price group
     * @param purchase   purchase prices per period; never null
     */
    public record PriceGroup(String priceGroup, @JsonProperty("create") List<Price> purchase) {
        /**
         * Creates a price group.
         *
         * @param priceGroup the price group
         * @param purchase   purchase prices; null becomes an empty list
         */
        public PriceGroup {
            purchase = Values.list(purchase);
        }
    }

    /**
     * A price for a period.
     *
     * @param period   the period in years
     * @param price    the price
     * @param currency the currency
     */
    public record Price(int period, BigDecimal price, String currency) {
    }
}
