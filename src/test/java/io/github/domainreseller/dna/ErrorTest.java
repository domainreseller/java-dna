package io.github.domainreseller.dna;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.domainreseller.dna.domains.LockDomainRequest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ErrorTest {
    private static DomainNameApiException fail(TestClient t, Runnable call) {
        return assertThrows(DomainNameApiException.class, call::run);
    }

    @ParameterizedTest
    @ValueSource(ints = {301, 302})
    void aRedirectMeansAnUnknownOrSuspendedReseller(int status) {
        TestClient t = new TestClient();
        t.http().respond(status, "", "Location", "/Error/Suspend");

        DomainNameApiException e = fail(t, () -> t.client().account().get());

        assertEquals(ApiErrorKind.AUTHENTICATION, e.kind());
        assertEquals("INVALID_RESELLER", e.code());
        assertTrue(e.hint().contains("environment"));
    }

    @Test
    void aFollowedRedirectToTheErrorPageIsStillAnAuthenticationError() {
        TestClient t = new TestClient();
        t.http().enqueue(request -> new FakeHttpClient.FakeResponse(request,
                URI.create("https://ote.domainresellerapi.com/Error/Suspend"), 200, "<html>Suspended</html>"));

        DomainNameApiException e = fail(t, () -> t.client().account().get());

        assertEquals("INVALID_RESELLER", e.code());
    }

    @ParameterizedTest
    @CsvSource({
            "API key is missing, MISSING_API_KEY",
            "ResellerId is missing, MISSING_RESELLER_ID",
            "Invalid API key, INVALID_API_KEY",
            "You do not have authorization, SUBRESELLER_NOT_ALLOWED",
            "Nope, UNAUTHORIZED",
    })
    void classifiesUnauthorizedAnswers(String message, String code) {
        TestClient t = new TestClient();
        t.http().respond(401, "{\"error\":{\"message\":\"" + message + "\"}}");

        DomainNameApiException e = fail(t, () -> t.client().account().get());

        assertEquals(ApiErrorKind.AUTHENTICATION, e.kind());
        assertEquals(code, e.code());
        assertEquals(message, e.getMessage());
    }

    @Test
    void readsTheGatewayErrorWithValidationErrors() {
        TestClient t = new TestClient();
        t.http().respond(400, """
                {"error":{"code":"Dna.DomainService:Domain:1018","message":"Your request is not valid!",
                  "details":"The following errors were detected during validation.",
                  "validationErrors":[{"message":"The eMail field is not a valid e-mail address.","members":["contacts[0].eMail"]},
                                      "Period is too long"]}}
                """, "X-Correlation-Id", "corr-1");

        DomainNameApiException e = fail(t, () -> t.client().domains().lock("example.com"));

        assertEquals(ApiErrorKind.VALIDATION, e.kind());
        assertEquals("Dna.DomainService:Domain:1018", e.code());
        assertEquals("Your request is not valid!", e.getMessage());
        assertEquals(400, e.statusCode());
        assertEquals("corr-1", e.correlationId());
        assertEquals("POST domains/lock", e.operation());
        assertEquals(OperationSafety.WRITE, e.safety());
        assertEquals(2, e.validationErrors().size());
        assertEquals(List.of("contacts[0].eMail"), e.validationErrors().get(0).members());
        assertEquals("contacts[0].eMail: The eMail field is not a valid e-mail address.", e.validationErrors().get(0).toString());
        assertEquals("Period is too long", e.validationErrors().get(1).message());
        assertEquals("Fix the listed fields and send the request again.", e.hint());
        assertTrue(e.toString().contains("Correlation ID: corr-1"));
        assertFalse(e.isRetryable());
    }

    @Test
    void readsAFlatErrorObject() {
        TestClient t = new TestClient();
        t.http().respond(409, "{\"Code\":\"2302\",\"Message\":\"Object exists\"}");

        DomainNameApiException e = fail(t, () -> t.client().domains().lock("example.com"));

        assertEquals(ApiErrorKind.CONFLICT, e.kind());
        assertEquals("2302", e.code());
        assertEquals("Object exists", e.getMessage());
    }

    @Test
    void replacesBoilerplateWithTheDetails() {
        TestClient t = new TestClient();
        t.http().respond(400, "{\"error\":{\"code\":\"Dna.X:1\",\"message\":\"Exception of type 'Volo.Abp.BusinessException' was thrown.\",\"details\":\"Dna Domain is locked\"}}");

        DomainNameApiException e = fail(t, () -> t.client().domains().lock("example.com"));

        assertEquals("Domain is locked", e.getMessage());
        assertEquals("Dna Domain is locked", e.details());
    }

    @Test
    void replacesAnEchoedCodeWithAGenericMessage() {
        TestClient t = new TestClient();
        t.http().respond(400, "{\"error\":{\"code\":\"Dna.X:7\",\"message\":\"Dna.X:7\"}}");

        DomainNameApiException e = fail(t, () -> t.client().domains().lock("example.com"));

        assertEquals("The operation could not be completed (error Dna.X:7).", e.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "500, SERVER, true",
            "501, SERVER, false",
            "403, FORBIDDEN, false",
            "404, NOT_FOUND, false",
            "409, CONFLICT, false",
            "422, VALIDATION, false",
            "418, UNKNOWN, false",
    })
    void classifiesStatusCodes(int status, ApiErrorKind kind, boolean retryable) {
        TestClient t = new TestClient(b -> b.maxRetries(0));
        t.http().respond(status, "");

        DomainNameApiException e = fail(t, () -> t.client().account().get());

        assertEquals(kind, e.kind());
        assertEquals("HTTP_" + status, e.code());
        assertEquals(retryable, e.isRetryable());
        assertTrue(e.getMessage().startsWith("HTTP " + status + " "));
    }

    @Test
    void aServerErrorHintQuotesTheCorrelationId() {
        TestClient t = new TestClient(b -> b.maxRetries(0));
        t.http().respond(500, "", "X-Correlation-Id", "xyz");

        DomainNameApiException e = fail(t, () -> t.client().account().get());

        assertTrue(e.hint().contains("xyz"));
    }

    @Test
    void keepsATextBodyAsTheMessage() {
        TestClient t = new TestClient();
        t.http().respond(404, "Redirect information not found");

        DomainNameApiException e = fail(t, () -> t.client().domains().forwarding().get("example.com"));

        assertEquals(ApiErrorKind.NOT_FOUND, e.kind());
        assertEquals("Redirect information not found", e.getMessage());
        assertEquals("Redirect information not found", e.responseBody());
        assertTrue(e.hint().contains("environment"));
    }

    @Test
    void anEmptySuccessfulAnswerIsAnError() {
        TestClient t = new TestClient();
        t.http().respond(200, " ");

        DomainNameApiException e = fail(t, () -> t.client().account().get());

        assertEquals(ApiErrorKind.UNKNOWN, e.kind());
        assertEquals("EMPTY_RESPONSE", e.code());
    }

    @Test
    void anUnreadableSuccessfulAnswerIsAnError() {
        TestClient t = new TestClient();
        t.http().respond(200, "<html>maintenance</html>");

        DomainNameApiException e = fail(t, () -> t.client().account().get());

        assertEquals("INVALID_RESPONSE", e.code());
        assertTrue(e.getMessage().contains("ResellerAccount"));
        assertNotNull(e.getCause());
    }

    @Test
    void aRequestWithoutAnAnswerIgnoresTheBody() {
        TestClient t = new TestClient();
        t.http().respond(200, "<html>whatever</html>");

        assertNull(t.client().send(new LockDomainRequest("example.com")));
    }

    @Test
    void aNetworkFailureOfABillableRequestHasAnUnknownOutcome() {
        TestClient t = new TestClient();
        t.http().fail(new IOException("Connection reset"));

        DomainNameApiException e = fail(t, () -> t.client().domains().renew("example.com"));

        assertEquals(ApiErrorKind.NETWORK, e.kind());
        assertEquals("NETWORK", e.code());
        assertNull(e.statusCode());
        assertTrue(e.isOutcomeUnknown());
        assertTrue(e.isRetryable());
        assertTrue(e.getMessage().contains("Connection reset"));
        assertTrue(e.hint().contains("Check the current state"));
        assertEquals(1, t.requests().size(), "a billable request is never sent twice");
    }

    @Test
    void slowAnswersTimeOut() {
        TestClient t = new TestClient(b -> b.timeout(Duration.ofMillis(1500)).maxRetries(0));
        t.http().fail(new HttpTimeoutException("request timed out"));

        DomainNameApiException e = fail(t, () -> t.client().account().get());

        assertEquals(ApiErrorKind.TIMEOUT, e.kind());
        assertEquals("TIMEOUT", e.code());
        assertEquals("No answer from the API within 1.5 s.", e.getMessage());
        assertFalse(e.isOutcomeUnknown(), "a read changes nothing");
    }

    @Test
    void exceptionsCanBeBuiltForTests() {
        DomainNameApiException e = DomainNameApiException.builder(ApiErrorKind.RATE_LIMITED, "RATE_LIMITED", "slow down")
                .statusCode(429).retryAfter(Duration.ofSeconds(2)).build();

        assertEquals(429, e.statusCode());
        assertTrue(e.isRetryable());
        assertEquals(List.of(), e.validationErrors());
    }
}
