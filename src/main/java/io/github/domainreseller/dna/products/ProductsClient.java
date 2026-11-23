package io.github.domainreseller.dna.products;

import io.github.domainreseller.dna.DomainNameApiClient;
import io.github.domainreseller.dna.OrderType;
import io.github.domainreseller.dna.Page;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/** The product catalogue and prices. Reach it through {@link DomainNameApiClient#products()}. */
public final class ProductsClient {
    private final DomainNameApiClient api;

    /**
     * Creates the client. Use {@link DomainNameApiClient#products()} instead.
     *
     * @param api the client that sends the requests
     */
    public ProductsClient(DomainNameApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    /**
     * Returns the first page of the product catalogue.
     *
     * @return the page
     */
    public Page<Product> list() {
        return list(new ListProductsRequest());
    }

    /**
     * Returns one page of the product catalogue.
     *
     * @param request the filters and page
     * @return the page
     */
    public Page<Product> list(ListProductsRequest request) {
        return api.send(request);
    }

    /**
     * Returns every product that matches the filters, fetching pages as the stream is consumed.
     *
     * @param request the filters
     * @return the products
     */
    public Stream<Product> listAll(ListProductsRequest request) {
        return api.listAll(request);
    }

    /**
     * Returns the one-year price of a product for an order type.
     *
     * @param productName the product name, for example the TLD {@code com}
     * @param orderType   the kind of order
     * @return the price
     */
    public ProductPrice getPrice(String productName, OrderType orderType) {
        return getPrice(productName, orderType, 1);
    }

    /**
     * Returns the price of a product for an order type and period.
     *
     * @param productName the product name, for example the TLD {@code com}
     * @param orderType   the kind of order
     * @param years       the period in years
     * @return the price
     */
    public ProductPrice getPrice(String productName, OrderType orderType, int years) {
        return api.send(new GetProductPriceRequest(productName, orderType, years));
    }

    /**
     * Returns the first page of TLDs with prices, periods, rules and attributes.
     *
     * @return the page
     */
    public Page<Tld> listTlds() {
        return listTlds(new ListTldsRequest());
    }

    /**
     * Returns one page of TLDs with prices, periods, rules and attributes.
     *
     * @param request the filters and page
     * @return the page
     */
    public Page<Tld> listTlds(ListTldsRequest request) {
        return api.send(request);
    }

    /**
     * Returns every TLD, fetching pages as the stream is consumed.
     *
     * @return the TLDs
     */
    public Stream<Tld> listAllTlds() {
        return listAllTlds(new ListTldsRequest());
    }

    /**
     * Returns every TLD that matches the filters, fetching pages as the stream is consumed.
     *
     * @param request the filters
     * @return the TLDs
     */
    public Stream<Tld> listAllTlds(ListTldsRequest request) {
        return api.listAll(request);
    }

    /**
     * Returns one TLD by name, with prices in the account's currency.
     *
     * @param name the TLD, with or without a leading dot, for example {@code com} or {@code .com.tr}
     * @return the TLD, or empty when it is not sold
     */
    public Optional<Tld> getTld(String name) {
        return getTld(name, null);
    }

    /**
     * Returns one TLD by name.
     *
     * @param name     the TLD, with or without a leading dot
     * @param currency the price currency, or null for the account default
     * @return the TLD, or empty when it is not sold
     */
    public Optional<Tld> getTld(String name, String currency) {
        Objects.requireNonNull(name, "name");
        String wanted = stripDot(name.trim());
        try (Stream<Tld> tlds = api.listAll(new ListTldsRequest().filterText(wanted).currency(currency))) {
            return tlds.filter(tld -> tld.name() != null && stripDot(tld.name()).equalsIgnoreCase(wanted)).findFirst();
        }
    }

    /**
     * Returns the first page of SSL certificate products with prices.
     *
     * @return the page
     */
    public Page<SslProduct> listSslProducts() {
        return listSslProducts(new ListSslProductsRequest());
    }

    /**
     * Returns one page of SSL certificate products with prices.
     *
     * @param request the filters and page
     * @return the page
     */
    public Page<SslProduct> listSslProducts(ListSslProductsRequest request) {
        return api.send(request);
    }

    /**
     * Returns every SSL certificate product that matches the filters, fetching pages as the stream is consumed.
     *
     * @param request the filters
     * @return the products
     */
    public Stream<SslProduct> listAllSslProducts(ListSslProductsRequest request) {
        return api.listAll(request);
    }

    private static String stripDot(String name) {
        return name.startsWith(".") ? name.substring(1) : name;
    }
}
