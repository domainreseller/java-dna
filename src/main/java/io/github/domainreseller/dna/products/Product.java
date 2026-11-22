package io.github.domainreseller.dna.products;

import io.github.domainreseller.dna.ProductType;
import java.util.UUID;

/**
 * A product in the catalogue.
 *
 * @param id              the product ID
 * @param parentProductId the parent product, or null
 * @param productName     the product name, for example a TLD such as {@code com}
 * @param productCode     the product code
 * @param entityId        the ID of the entity the product sells
 * @param productType     the kind of product
 */
public record Product(
        UUID id,
        UUID parentProductId,
        String productName,
        String productCode,
        String entityId,
        ProductType productType) {
}
