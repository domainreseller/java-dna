package io.github.domainreseller.dna;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.domainreseller.dna.accounts.GetAccountRequest;
import io.github.domainreseller.dna.accounts.ResellerAccount;
import io.github.domainreseller.dna.domains.LockDomainRequest;
import io.github.domainreseller.dna.domains.SearchDomainRequest;
import java.math.BigDecimal;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.Test;

class ClientTest {
    @Test
    void sendsCredentialsAndStandardHeaders() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"resellerName\":\"Test\"}");

        t.client().account().get();

        RecordedRequest request = t.lastRequest();
        assertEquals("GET", request.method());
        assertEquals("https://ote.domainresellerapi.com/api/v1/deposit/accounts/me", request.uri().toString());
        assertEquals(TestClient.API_KEY, request.header("X-API-KEY"));
        assertEquals(TestClient.RESELLER_ID, request.header("__reseller"));
        assertEquals("application/json", request.header("Accept"));
        assertTrue(request.header("User-Agent").startsWith("DomainNameAPI-Java/"));
        assertNull(request.body());
    }

    @Test
    void usesTheProductionAddressByDefault() {
        DomainNameApiClient client = DomainNameApiClient.create("id", "key");
        assertEquals(ApiEnvironment.PRODUCTION, client.environment());
        assertEquals(URI.create("https://api.domainresellerapi.com/"), client.baseUri());
    }

    @Test
    void baseUriOverrideKeepsItsPathPrefix() {
        TestClient t = new TestClient(b -> b.baseUri(URI.create("http://localhost:8080/proxy?x=1")));
        t.http().respondJson("{}");

        t.client().account().get();

        assertEquals("http://localhost:8080/proxy/api/v1/deposit/accounts/me", t.lastRequest().uri().toString());
    }

    @Test
    void customUserAgentReplacesTheDefault() {
        TestClient t = new TestClient(b -> b.userAgent("MyPanel/1.0"));
        t.http().respondJson("{}");

        t.client().account().get();

        assertEquals("MyPanel/1.0", t.lastRequest().header("User-Agent"));
    }

    @Test
    void credentialsAreTrimmed() {
        TestClient t = new TestClient(b -> b.credentials("  " + TestClient.RESELLER_ID + " ", " key\n"));
        t.http().respondJson("{}");

        t.client().account().get();

        assertEquals(TestClient.RESELLER_ID, t.lastRequest().header("__reseller"));
        assertEquals("key", t.lastRequest().header("X-API-KEY"));
    }

    @Test
    void decodesTheResponse() {
        TestClient t = new TestClient();
        t.http().respondJson("""
                {"resellerId":"7d9f1a52-3c1e-4c9e-9a51-6b8c2f3e4d10","resellerName":"Acme","resellerGroupName":"Gold",
                 "tryBalance":"1250.50","usdBalance":42.1,"somethingNew":true}
                """);

        ResellerAccount account = t.client().account().get();

        assertEquals("Acme", account.resellerName());
        assertEquals(new BigDecimal("1250.50"), account.tryBalance());
        assertEquals(new BigDecimal("42.1"), account.usdBalance());
        assertEquals("7d9f1a52-3c1e-4c9e-9a51-6b8c2f3e4d10", account.resellerId().toString());
    }

    @Test
    void missingCredentialsFailBeforeSending() {
        TestClient t = new TestClient(b -> b.credentials("", " "));

        DomainNameApiException e = assertThrows(DomainNameApiException.class, () -> t.client().account().get());

        assertEquals(ApiErrorKind.AUTHENTICATION, e.kind());
        assertEquals("NO_CREDENTIALS", e.code());
        assertNotNull(e.hint());
        assertTrue(t.requests().isEmpty());
    }

    @Test
    void invalidArgumentsFailBeforeSending() {
        TestClient t = new TestClient();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> t.client().domains().get(" "));

        assertTrue(e.getMessage().contains("domainName"));
        assertTrue(t.requests().isEmpty());
    }

    @Test
    void readOnlyModeBlocksChangesButAllowsReads() {
        TestClient t = new TestClient(b -> b.readOnly(true));
        t.http().respondJson("{}");

        t.client().account().get();
        DomainNameApiException e = assertThrows(DomainNameApiException.class,
                () -> t.client().domains().renew("example.com"));

        assertEquals(ApiErrorKind.READ_ONLY, e.kind());
        assertEquals("READ_ONLY", e.code());
        assertEquals(OperationSafety.BILLABLE, e.safety());
        assertTrue(e.getMessage().contains("billable"));
        assertEquals(1, t.requests().size());
        assertTrue(t.client().isReadOnly());
    }

    @Test
    void rejectsInvalidOptions() {
        assertThrows(IllegalArgumentException.class, () -> DomainNameApiClient.builder().timeout(Duration.ZERO));
        assertThrows(IllegalArgumentException.class, () -> DomainNameApiClient.builder().maxRetries(-1));
        assertThrows(IllegalArgumentException.class, () -> DomainNameApiClient.builder().maxRetryDelay(Duration.ofSeconds(-1)));
        assertThrows(IllegalArgumentException.class, () -> DomainNameApiClient.builder().baseUri(URI.create("relative/path")));
    }

    @Test
    void builderChangesAfterBuildHaveNoEffect() {
        DomainNameApiClient.Builder builder = DomainNameApiClient.builder().credentials("id", "key").environment(ApiEnvironment.OTE);
        DomainNameApiClient client = builder.build();
        builder.environment(ApiEnvironment.PRODUCTION).readOnly(true);

        assertEquals(ApiEnvironment.OTE, client.environment());
        assertFalse(client.isReadOnly());
    }

    @Test
    void sendWithResponseExposesCorrelationIdQuotaAndAttempts() {
        TestClient t = new TestClient();
        t.http().respond(200, "{\"resellerName\":\"Acme\"}",
                "X-Correlation-Id", "abc123",
                "X-Quota-Scope", "deposit", "X-Quota-Cost", "1", "X-Quota-Used", "12.5",
                "X-Quota-Limit", "200", "X-Quota-Delay", "0", "X-Quota-Reset", "60");

        ApiResponse<ResellerAccount> response = t.client().sendWithResponse(new GetAccountRequest());

        assertEquals("Acme", response.value().resellerName());
        assertEquals(200, response.statusCode());
        assertEquals("abc123", response.correlationId());
        assertEquals(1, response.attempts());
        assertEquals("deposit", response.quota().scope());
        assertEquals(12.5, response.quota().used());
        assertEquals(200, response.quota().limit());
        assertFalse(response.quota().wasDelayed());
        assertEquals("abc123", response.headers().get("x-correlation-id"));
    }

    @Test
    void noQuotaWhenTheGatewaySendsNone() {
        TestClient t = new TestClient();
        t.http().respondJson("{}");

        assertNull(t.client().sendWithResponse(new GetAccountRequest()).quota());
    }

    @Test
    void anInterruptedThreadIsNotTurnedIntoAnApiError() {
        TestClient t = new TestClient();
        t.http().enqueue(request -> {
            throw new InterruptedException();
        });

        assertThrows(CancellationException.class, () -> t.client().account().get());
        assertTrue(Thread.interrupted(), "the interrupt flag is set again");
    }

    @Test
    void versionComesFromTheBuild() {
        assertEquals("2.1.0", DomainNameApiClient.VERSION);
        assertEquals("DomainNameAPI-Java/2.1.0 (+https://github.com/domainreseller/java-dna)", DomainNameApiClient.DEFAULT_USER_AGENT);
    }

    @Test
    void nullValuesAreLeftOutOfTheBody() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"success\":true}");

        t.client().domains().search("example.com");

        assertEquals("{\"domainName\":\"example.com\"}", t.lastRequest().body());
        assertEquals("application/json; charset=utf-8", t.lastRequest().header("Content-Type"));
    }

    @Test
    void requestsWithoutAnAnswerReturnNull() {
        TestClient t = new TestClient();
        t.http().respond(204, "");

        assertNull(t.client().send(new LockDomainRequest("example.com")));
    }

    @Test
    void sendAcceptsCustomRequests() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"notes\":[\"first\",\"second\"]}");

        DomainNotes notes = t.client().send(new GetDomainNotesRequest("example.com"));

        assertEquals(List.of("first", "second"), notes.notes());
        assertEquals("domains/notes", t.lastRequest().apiPath());
        assertEquals("example.com", t.lastRequest().query("DomainName"));
    }

    @Test
    void sendAsyncCompletesWithTheAnswerOrTheException() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"success\":true}").respond(404, "");

        assertTrue(t.client().sendAsync(new SearchDomainRequest("example.com")).join().success());
        CompletionException e = assertThrows(CompletionException.class,
                () -> t.client().sendAsync(new SearchDomainRequest("example.com")).join());
        assertEquals(ApiErrorKind.NOT_FOUND, ((DomainNameApiException) e.getCause()).kind());
    }

    @Test
    void toStringNeverShowsTheCredentials() {
        String text = new TestClient().client().toString();
        assertFalse(text.contains(TestClient.API_KEY));
        assertFalse(text.contains(TestClient.RESELLER_ID));
    }

    /** The answer of the custom request below. */
    record DomainNotes(List<String> notes) {
    }

    /** A request the SDK does not have, written the way the README shows. */
    static final class GetDomainNotesRequest extends BaseRequest<DomainNotes> {
        private final String domainName;

        GetDomainNotesRequest(String domainName) {
            this.domainName = domainName;
        }

        @Override
        public HttpMethod method() {
            return HttpMethod.GET;
        }

        @Override
        public String path() {
            return "domains/notes";
        }

        @Override
        public OperationSafety safety() {
            return OperationSafety.READ;
        }

        @Override
        protected void addQueryParameters(QueryParameters query) {
            query.add("DomainName", domainName);
        }

        @Override
        protected void validate() {
            require(domainName, "domainName");
        }
    }
}
