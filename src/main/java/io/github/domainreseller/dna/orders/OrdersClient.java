package io.github.domainreseller.dna.orders;

import io.github.domainreseller.dna.DomainNameApiClient;
import java.util.Objects;

/** Orders. Reach it through {@link DomainNameApiClient#orders()}. */
public final class OrdersClient {
    private final DomainNameApiClient api;

    /**
     * Creates the client. Use {@link DomainNameApiClient#orders()} instead.
     *
     * @param api the client that sends the requests
     */
    public OrdersClient(DomainNameApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    /**
     * Returns an order and its lines.
     *
     * @param orderCode the order code
     * @return the order
     */
    public Order get(String orderCode) {
        return api.send(new GetOrderRequest(orderCode));
    }
}
