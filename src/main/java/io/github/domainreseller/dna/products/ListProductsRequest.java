package io.github.domainreseller.dna.products;

import io.github.domainreseller.dna.PagedRequest;
import io.github.domainreseller.dna.ProductType;
import io.github.domainreseller.dna.QueryParameters;

/** {@code GET products}: pages through the product catalogue. */
public final class ListProductsRequest extends PagedRequest<Product> {
    private String filterText;
    private ProductType productType;

    /** Creates a request for all products. */
    public ListProductsRequest() {
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
    public ListProductsRequest filterText(String filterText) {
        this.filterText = filterText;
        return this;
    }

    /**
     * Returns the product type filter.
     *
     * @return the type, or null for all
     */
    public ProductType productType() {
        return productType;
    }

    /**
     * Lists only products of this kind.
     *
     * @param productType the type
     * @return this request
     */
    public ListProductsRequest productType(ProductType productType) {
        this.productType = productType;
        return this;
    }

    @Override
    public ListProductsRequest skipCount(Integer skipCount) {
        super.skipCount(skipCount);
        return this;
    }

    @Override
    public ListProductsRequest maxResultCount(Integer maxResultCount) {
        super.maxResultCount(maxResultCount);
        return this;
    }

    @Override
    public ListProductsRequest sorting(String sorting) {
        super.sorting(sorting);
        return this;
    }

    @Override
    public String path() {
        return "products";
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("FilterText", filterText).add("ProductType", productType);
        super.addQueryParameters(query);
    }
}
