package io.github.domainreseller.dna;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Runs the SDK's own {@code java.net.http} transport against a local HTTP server on 127.0.0.1, to check what the fake
 * client cannot: real headers, bodies, redirects and time limits. Nothing leaves the machine.
 */
class TransportTest {
    private HttpServer server;
    private final Map<String, String> seen = new ConcurrentHashMap<>();

    @BeforeEach
    void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/api/v1/domains/renew", exchange -> {
            seen.put("method", exchange.getRequestMethod());
            seen.put("reseller", exchange.getRequestHeaders().getFirst("__reseller"));
            seen.put("key", exchange.getRequestHeaders().getFirst("X-API-KEY"));
            seen.put("contentType", exchange.getRequestHeaders().getFirst("Content-Type"));
            seen.put("body", new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            respond(exchange, 200, "{\"success\":true,\"status\":\"Active\"}", "X-Correlation-Id", "c-1");
        });
        server.createContext("/api/v1/deposit/accounts/me", exchange ->
                respond(exchange, 302, "", "Location", "/Error/Suspend"));
        server.createContext("/api/v1/domains/info", exchange -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            respond(exchange, 200, "{}");
        });
        server.start();
    }

    @AfterEach
    void stop() {
        server.stop(0);
    }

    private DomainNameApiClient client(Duration timeout) {
        return DomainNameApiClient.builder()
                .credentials("reseller-1", "key-1")
                .baseUri(URI.create("http://127.0.0.1:" + server.getAddress().getPort()))
                .timeout(timeout)
                .maxRetries(0)
                .paceRequests(false)
                .build();
    }

    private static void respond(com.sun.net.httpserver.HttpExchange exchange, int status, String body, String... headers)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i + 1 < headers.length; i += 2) {
            exchange.getResponseHeaders().add(headers[i], headers[i + 1]);
        }
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    @Test
    void sendsHeadersAndBodyOverRealHttp() {
        ApiResponse<?> response = client(Duration.ofSeconds(5))
                .sendWithResponse(new io.github.domainreseller.dna.domains.RenewDomainRequest("example.com", 2));

        assertEquals("POST", seen.get("method"));
        assertEquals("reseller-1", seen.get("reseller"));
        assertEquals("key-1", seen.get("key"));
        assertTrue(seen.get("contentType").startsWith("application/json"));
        assertEquals("{\"domainName\":\"example.com\",\"period\":2}", seen.get("body"));
        assertEquals("c-1", response.correlationId());
    }

    @Test
    void redirectsAreNotFollowed() {
        DomainNameApiException e = assertThrows(DomainNameApiException.class,
                () -> client(Duration.ofSeconds(5)).account().get());

        assertEquals("INVALID_RESELLER", e.code());
        assertEquals(302, e.statusCode());
    }

    @Test
    void eachAttemptHasATimeLimit() {
        DomainNameApiException e = assertThrows(DomainNameApiException.class,
                () -> client(Duration.ofMillis(300)).domains().get("example.com"));

        assertEquals(ApiErrorKind.TIMEOUT, e.kind());
        assertEquals(List.of(), e.validationErrors());
    }
}
