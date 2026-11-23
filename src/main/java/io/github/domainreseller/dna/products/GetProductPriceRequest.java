package io.github.domainreseller.dna.products;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.OrderType;
import io.github.domainreseller.dna.QueryParameters;

/** {@code GET products/info}: the price of a product for an order type and period. */
public final class GetProductPriceRequest extends BaseRequest<ProductPrice> {
    private final String productName;
    private final OrderType orderType;
    private final int period;

    /**
     * Creates the request.
     *
     * @param productName the product name, for example the TLD {@code com}
     * @param orderType   the kind of order, for example {@link OrderType#RENEW}
     * @param period      the period in years
     */
    public GetProductPriceRequest(String productName, OrderType orderType, int period) {
        this.productName = productName;
        this.orderType = orderType;
        this.period = period;
    }

    /**
     * Returns the product name.
     *
     * @return the product name
     */
    public String productName() {
        return productName;
    }

    /**
     * Returns the kind of order.
     *
     * @return the order type
     */
    public OrderType orderType() {
        return orderType;
    }

    /**
     * Returns the period.
     *
     * @return the period in years
     */
    public int period() {
        return period;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }

    @Override
    public String path() {
        return "products/info";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("ProductName", productName).add("OrderType", orderType).add("Period", period);
    }

    @Override
    protected void validate() {
        require(productName, "productName");
        require(orderType, "orderType");
    }
}
