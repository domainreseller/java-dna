package io.github.domainreseller.dna.accounts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.domainreseller.dna.ProductType;
import io.github.domainreseller.dna.TestClient;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AccountClientTest {
    @Test
    void getProfileReadsTheResellerProfile() {
        TestClient t = new TestClient();
        t.http().respondJson("""
                {"id":"7d9f1a52-3c1e-4c9e-9a51-6b8c2f3e4d10","resellerAutoId":17,"name":"Acme","companyName":"Acme Ltd",
                 "resellerGroupName":"Gold","accountCurrency":2,"status":0,"isSubReseller":false,
                 "isBalanceSuspended":true,"isMcpEnabled":true}
                """);

        ResellerProfile profile = t.client().account().getProfile();

        assertEquals("resellers/me", t.lastRequest().apiPath());
        assertEquals(17, profile.resellerAutoId());
        assertEquals("Acme Ltd", profile.companyName());
        assertEquals(AccountCurrency.USD, profile.accountCurrency());
        assertEquals(ResellerStatus.ACTIVE, profile.status());
        assertTrue(profile.isBalanceSuspended());
        assertTrue(profile.isMcpEnabled());
    }

    @ParameterizedTest
    @CsvSource({"1, TRY", "2, USD", "3, EURO", "9, UNKNOWN"})
    void profileCurrenciesUseTheApiNumbers(int number, AccountCurrency expected) {
        TestClient t = new TestClient();
        t.http().respondJson("{\"accountCurrency\":" + number + "}");

        assertEquals(expected, t.client().account().getProfile().accountCurrency());
    }

    @Test
    void theProfileIsAReadAndWorksInReadOnlyMode() {
        TestClient t = new TestClient(b -> b.readOnly(true));
        t.http().respondJson("{}");

        t.client().account().getProfile();

        assertEquals(1, t.requests().size());
    }

    @Test
    void listTransactionsSendsTheFilters() {
        TestClient t = new TestClient();
        t.http().respondJson("""
                {"totalCount":1,"items":[{"traceId":"t-1","type":2,"status":1,"chargeType":1,"amount":9.99,
                 "amountCurrency":"USD","productType":1,"description":"example.com","period":1,
                 "creationTime":"2026-11-05T10:00:00Z","balance":90.01,"oldBalance":100}]}
                """);

        var page = t.client().account().listTransactions(new ListTransactionsRequest()
                .transactionTypes(TransactionType.REGISTER, TransactionType.RENEW)
                .productTypes(ProductType.DOMAIN)
                .currencies("USD")
                .chargeType(ChargeType.SUBTRACT)
                .amount(new BigDecimal("9.99"))
                .startDate(OffsetDateTime.parse("2026-11-01T00:00:00Z"))
                .maxResultCount(25));

        var request = t.lastRequest();
        assertEquals("deposit/transactions", request.apiPath());
        assertEquals(List.of("1", "2"), request.queryAll("TransactionTypes"));
        assertEquals(List.of("1"), request.queryAll("ProductTypes"));
        assertEquals("USD", request.query("AccountCurrencyTypes"));
        assertEquals("1", request.query("ChargeType"));
        assertEquals("9.99", request.query("Amount"));
        assertEquals("2026-11-01T00:00:00Z", request.query("StartDate"));
        assertEquals("25", request.query("MaxResultCount"));

        AccountTransaction transaction = page.items().get(0);
        assertEquals(1, page.totalCount());
        assertEquals(TransactionType.RENEW, transaction.type());
        assertEquals(TransactionStatus.SUCCESS, transaction.status());
        assertEquals(ChargeType.SUBTRACT, transaction.chargeType());
        assertEquals(new BigDecimal("9.99"), transaction.amount());
        assertEquals(OffsetDateTime.parse("2026-11-05T10:00:00Z"), transaction.creationTime());
    }

    @Test
    void listAllPagesUntilTheTotalIsReached() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"totalCount\":3,\"items\":[{\"traceId\":\"a\"},{\"traceId\":\"b\"}]}")
                .respondJson("{\"totalCount\":3,\"items\":[{\"traceId\":\"c\"}]}");

        List<String> ids = t.client().account().listAllTransactions(new ListTransactionsRequest().maxResultCount(2))
                .map(AccountTransaction::traceId).collect(Collectors.toList());

        assertEquals(List.of("a", "b", "c"), ids);
        assertEquals("0", t.requests().get(0).query("SkipCount"));
        assertEquals("2", t.requests().get(1).query("SkipCount"));
        assertEquals("2", t.requests().get(1).query("MaxResultCount"));
    }

    @Test
    void listAllStopsOnAnEmptyPageAndIsLazy() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"totalCount\":50,\"items\":[{\"traceId\":\"a\"}]}")
                .respondJson("{\"totalCount\":50,\"items\":[]}");

        var stream = t.client().account().listAllTransactions();
        assertTrue(t.requests().isEmpty(), "nothing is fetched before the stream is used");

        assertEquals(1, stream.count());
        assertEquals(2, t.requests().size());
        assertEquals("100", t.requests().get(0).query("MaxResultCount"));
    }

    @Test
    void listAllDoesNotChangeTheRequest() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"totalCount\":1,\"items\":[{}]}");
        ListTransactionsRequest request = new ListTransactionsRequest();

        t.client().account().listAllTransactions(request).count();

        assertEquals(null, request.skipCount());
        assertEquals(null, request.maxResultCount());
    }

    @Test
    void rejectsANegativeSkipCount() {
        TestClient t = new TestClient();

        assertThrows(IllegalArgumentException.class,
                () -> t.client().account().listTransactions(new ListTransactionsRequest().skipCount(-1)));
        assertTrue(t.requests().isEmpty());
    }
}
