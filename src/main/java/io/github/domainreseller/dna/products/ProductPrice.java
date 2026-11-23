package io.github.domainreseller.dna.products;

import io.github.domainreseller.dna.ProductType;
import io.github.domainreseller.dna.internal.Values;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * The price of a product for an order type and period.
 *
 * @param parentProductId the parent product, or null
 * @param productName     the product name
 * @param productCode     the product code
 * @param entityId        the ID of the entity the product sells
 * @param productType     the kind of product
 * @param sellingPrice    your price
 * @param sellingCurrency the currency of {@code sellingPrice}
 * @param subproducts     products included in this one; never null
 */
public record ProductPrice(
        UUID parentProductId,
        String productName,
        String productCode,
        String entityId,
        ProductType productType,
        BigDecimal sellingPrice,
        String sellingCurrency,
        List<Subproduct> subproducts) {
    /**
     * Creates a price.
     *
     * @param parentProductId the parent product
     * @param productName     the product name
     * @param productCode     the product code
     * @param entityId        the entity ID
     * @param productType     the kind of product
     * @param sellingPrice    your price
     * @param sellingCurrency the currency
     * @param subproducts     included products; null becomes an empty list
     */
    public ProductPrice {
        subproducts = Values.list(subproducts);
    }

    /**
     * A product included in another one.
     *
     * @param parentProductId the parent product
     * @param productName     the product name
     * @param productCode     the product code
     * @param productType     the kind of product
     * @param entityId        the ID of the entity the product sells
     * @param buyingPrice     the cost price
     * @param buyingCurrency  the currency of {@code buyingPrice}
     * @param sellingPrice    your price
     * @param sellingCurrency the currency of {@code sellingPrice}
     */
    public record Subproduct(
            UUID parentProductId,
            String productName,
            String productCode,
            ProductType productType,
            String entityId,
            BigDecimal buyingPrice,
            String buyingCurrency,
            BigDecimal sellingPrice,
            String sellingCurrency) {
    }
}
