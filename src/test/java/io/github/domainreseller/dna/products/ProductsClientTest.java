package io.github.domainreseller.dna.products;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.domainreseller.dna.OrderType;
import io.github.domainreseller.dna.ProductType;
import io.github.domainreseller.dna.TestClient;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ProductsClientTest {
    @Test
    void getPriceSendsTheOrderTypeAsANumber() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"productName\":\"com\",\"productType\":1,\"sellingPrice\":\"10.50\",\"sellingCurrency\":\"USD\"}");

        ProductPrice price = t.client().products().getPrice("com", OrderType.RENEW, 2);

        assertEquals("com", t.lastRequest().query("ProductName"));
        assertEquals("2", t.lastRequest().query("OrderType"));
        assertEquals("2", t.lastRequest().query("Period"));
        assertEquals(new BigDecimal("10.50"), price.sellingPrice());
        assertEquals(ProductType.DOMAIN, price.productType());
    }

    @Test
    void tldsAndTheirPrices() {
        TestClient t = new TestClient();
        t.http().respondJson("""
                {"totalCount":1,"items":[{"name":"com","isDnssecAvailable":true,"isSupportTransfer":true,
                  "prices":[{"priceGroup":"Gold","register":[{"period":1,"price":9.99,"currency":"USD"}],
                             "renew":[{"period":1,"price":10.99,"currency":"USD"}]}],
                  "attributes":[{"key":"x","isRequired":true,"options":[{"value":"a"}]}]}]}
                """);

        Tld com = t.client().products().listTlds(new ListTldsRequest().currency("USD")).items().get(0);

        assertEquals("products/tlds", t.lastRequest().apiPath());
        assertEquals("USD", t.lastRequest().query("Currency"));
        assertTrue(com.supportsDnssec());
        assertTrue(com.supportsTransfer());
        assertEquals(new BigDecimal("9.99"), com.registrationPrice(1).orElseThrow().price());
        assertEquals(new BigDecimal("10.99"), com.renewalPrice(1).orElseThrow().price());
        assertTrue(com.registrationPrice(5).isEmpty());
        assertTrue(com.attributes().get(0).isRequired());
        assertEquals("a", com.attributes().get(0).options().get(0).value());
    }

    @Test
    void getTldFindsTheExactName() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"totalCount\":2,\"items\":[{\"name\":\"com.tr\"},{\"name\":\"com\"}]}");

        Optional<Tld> com = t.client().products().getTld(".com");

        assertEquals("com", t.lastRequest().query("FilterText"));
        assertEquals("com", com.orElseThrow().name());
    }

    @Test
    void getTldIsEmptyWhenTheTldIsNotSold() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"totalCount\":0,\"items\":[]}");

        assertTrue(t.client().products().getTld("nope").isEmpty());
    }

    @Test
    void sslProductFilters() {
        TestClient t = new TestClient();
        t.http().respondJson("""
                {"totalCount":1,"items":[{"name":"PositiveSSL Wildcard","productCode":"PSW","isWildcard":true,
                  "prices":[{"priceGroup":"Gold","create":[{"period":1,"price":49,"currency":"USD"}]}]}]}
                """);

        SslProduct product = t.client().products().listSslProducts(new ListSslProductsRequest().isWildcard(true).vendorName("Sectigo"))
                .items().get(0);

        assertEquals("true", t.lastRequest().query("IsWildcard"));
        assertEquals("Sectigo", t.lastRequest().query("VendorName"));
        assertTrue(product.isWildcard());
        assertEquals(new BigDecimal("49"), product.prices().get(0).purchase().get(0).price());
    }

    @Test
    void listProductsByType() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"totalCount\":1,\"items\":[{\"productName\":\"PositiveSSL\",\"productType\":2}]}");

        Product product = t.client().products().list(new ListProductsRequest().productType(ProductType.SSL)).items().get(0);

        assertEquals("2", t.lastRequest().query("ProductType"));
        assertEquals(ProductType.SSL, product.productType());
    }
}
