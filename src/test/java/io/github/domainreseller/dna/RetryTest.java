package io.github.domainreseller.dna;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.domainreseller.dna.accounts.GetAccountRequest;
import java.io.IOException;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RetryTest {
    @Test
    void rateLimitedRequestsAreRetriedEvenWhenBillable() {
        TestClient t = new TestClient();
        t.http().respond(429, "", "Retry-After", "2").respondJson("{\"success\":true}");

        assertTrue(t.client().domains().renew("example.com").success());

        assertEquals(2, t.requests().size());
        assertEquals(List.of(Duration.ofMillis(2050)), t.delays());
    }

    @Test
    void givesUpAfterMaxRetries() {
        TestClient t = new TestClient(b -> b.maxRetries(2));
        t.http().respond(429, "").respond(429, "").respond(429, "", "Retry-After", "1.5");

        DomainNameApiException e = assertThrows(DomainNameApiException.class, () -> t.client().account().get());

        assertEquals(ApiErrorKind.RATE_LIMITED, e.kind());
        assertEquals("RATE_LIMITED", e.code());
        assertEquals(Duration.ofMillis(1500), e.retryAfter());
        assertEquals("Rate limit exceeded; retry after 1.5 s.", e.getMessage());
        assertEquals(3, t.requests().size());
    }

    @Test
    void doesNotWaitLongerThanMaxRetryDelay() {
        TestClient t = new TestClient(b -> b.maxRetryDelay(Duration.ofSeconds(5)));
        t.http().respond(429, "", "Retry-After", "60");

        assertThrows(DomainNameApiException.class, () -> t.client().account().get());

        assertEquals(1, t.requests().size());
        assertTrue(t.delays().isEmpty());
    }

    @Test
    void readsRetryAfterAsAnHttpDate() {
        TestClient t = new TestClient();
        String date = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(3));
        t.http().respond(429, "", "Retry-After", date).respondJson("{}");

        t.client().account().get();

        Duration wait = t.delays().get(0);
        assertTrue(wait.compareTo(Duration.ofSeconds(1)) > 0 && wait.compareTo(Duration.ofSeconds(4)) <= 0, wait.toString());
    }

    @ParameterizedTest
    @ValueSource(ints = {502, 503, 504})
    void readsAreRetriedOnGatewayErrors(int status) {
        TestClient t = new TestClient();
        t.http().respond(status, "").respondJson("{}");

        t.client().account().get();

        assertEquals(2, t.requests().size());
        Duration wait = t.delays().get(0);
        assertTrue(wait.toMillis() >= 500 && wait.toMillis() < 750, wait.toString());
    }

    @Test
    void billableRequestsAreNotRetriedOnGatewayErrors() {
        TestClient t = new TestClient();
        t.http().respond(503, "");

        DomainNameApiException e = assertThrows(DomainNameApiException.class, () -> t.client().domains().renew("example.com"));

        assertEquals(ApiErrorKind.SERVER, e.kind());
        assertEquals(1, t.requests().size());
    }

    @Test
    void idempotentWritesAreRetried() {
        TestClient t = new TestClient();
        t.http().respond(502, "").respond(200, "");

        t.client().domains().setNameServers("example.com", List.of("ns1.example.net", "ns2.example.net"));

        assertEquals(2, t.requests().size());
    }

    @Test
    void readsAreRetriedAfterNetworkErrors() {
        TestClient t = new TestClient();
        t.http().fail(new IOException("reset")).fail(new IOException("reset")).respondJson("{}");

        ApiResponse<?> response = t.client().sendWithResponse(new GetAccountRequest());

        assertEquals(3, response.attempts());
        assertEquals(2, t.delays().size());
        assertTrue(t.delays().get(1).toMillis() >= 1000, "the backoff doubles");
    }

    @Test
    void writesAreNotRetriedAfterNetworkErrors() {
        TestClient t = new TestClient();
        t.http().fail(new IOException("reset"));

        DomainNameApiException e = assertThrows(DomainNameApiException.class, () -> t.client().domains().lock("example.com"));

        assertTrue(e.isOutcomeUnknown());
        assertEquals(1, t.requests().size());
    }

    @Test
    void maxRetriesZeroTurnsRetriesOff() {
        TestClient t = new TestClient(b -> b.maxRetries(0));
        t.http().respond(429, "", "Retry-After", "1");

        assertThrows(DomainNameApiException.class, () -> t.client().account().get());

        assertEquals(1, t.requests().size());
    }

    @Test
    void aRedirectIsNeverRetried() {
        TestClient t = new TestClient();
        t.http().respond(302, "");

        assertThrows(DomainNameApiException.class, () -> t.client().account().get());

        assertEquals(1, t.requests().size());
    }
}
