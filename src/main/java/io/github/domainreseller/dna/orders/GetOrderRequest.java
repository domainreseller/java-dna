package io.github.domainreseller.dna.orders;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.internal.Values;

/** {@code GET order/{orderCode}}: an order and its lines. */
public final class GetOrderRequest extends BaseRequest<Order> {
    private final String orderCode;

    /**
     * Creates the request.
     *
     * @param orderCode the order code
     */
    public GetOrderRequest(String orderCode) {
        this.orderCode = orderCode;
    }

    /**
     * Returns the order code.
     *
     * @return the order code
     */
    public String orderCode() {
        return orderCode;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }

    @Override
    public String path() {
        return "order/" + Values.pathSegment(orderCode);
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }

    @Override
    protected void validate() {
        require(orderCode, "orderCode");
    }
}
