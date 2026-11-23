package io.github.domainreseller.dna;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EnvironmentTest {
    @ParameterizedTest
    @CsvSource({
            "'', PRODUCTION",
            "production, PRODUCTION",
            "PROD, PRODUCTION",
            "live, PRODUCTION",
            "false, PRODUCTION",
            "ote, OTE",
            "Test, OTE",
            "sandbox, OTE",
            "true, OTE",
            "' 1 ', OTE",
            "yes, OTE",
    })
    void parsesEnvironmentNames(String text, ApiEnvironment expected) {
        assertEquals(Optional.of(expected), ApiEnvironment.parse(text));
    }

    @Test
    void rejectsUnknownEnvironmentNames() {
        assertTrue(ApiEnvironment.parse("staging").isEmpty());
        assertEquals(Optional.of(ApiEnvironment.PRODUCTION), ApiEnvironment.parse(null));
    }

    @Test
    void readsTheSharedEnvironmentVariables() {
        Map<String, String> env = Map.of(
                "DNA_RESELLER_ID", " reseller ",
                "DNA_API_KEY", "key",
                "DNA_ENV", "ote",
                "DNA_BASE_URL", "http://localhost:9000/gateway",
                "DNA_READ_ONLY", "true");

        DomainNameApiClient client = DomainNameApiClient.Builder.fromEnvironment(env::get).build();

        assertEquals(ApiEnvironment.OTE, client.environment());
        assertEquals(URI.create("http://localhost:9000/gateway/"), client.baseUri());
        assertTrue(client.isReadOnly());
    }

    @Test
    void dnaEnvWinsOverTestMode() {
        Map<String, String> env = Map.of("DNA_ENV", "production", "DNA_TEST_MODE", "true");

        assertEquals(ApiEnvironment.PRODUCTION, DomainNameApiClient.Builder.fromEnvironment(env::get).build().environment());
    }

    @Test
    void testModeSelectsOte() {
        Map<String, String> env = Map.of("DNA_TEST_MODE", "1");

        DomainNameApiClient client = DomainNameApiClient.Builder.fromEnvironment(env::get).build();

        assertEquals(ApiEnvironment.OTE, client.environment());
        assertEquals(URI.create("https://ote.domainresellerapi.com/"), client.baseUri());
        assertFalse(client.isReadOnly());
    }

    @Test
    void rejectsUnreadableVariables() {
        IllegalArgumentException env = assertThrows(IllegalArgumentException.class,
                () -> DomainNameApiClient.Builder.fromEnvironment(Map.of("DNA_ENV", "staging")::get));
        assertTrue(env.getMessage().contains("staging"));
        assertThrows(IllegalArgumentException.class,
                () -> DomainNameApiClient.Builder.fromEnvironment(Map.of("DNA_BASE_URL", "not a url")::get));
    }

    @Test
    void resolvesBaseAddresses() {
        assertEquals(URI.create("https://proxy.example.net/dna/"),
                DomainNameApiClient.builder().baseUri(URI.create("https://proxy.example.net/dna")).build().baseUri());
        assertEquals(URI.create("https://proxy.example.net/"),
                DomainNameApiClient.builder().baseUri(URI.create("https://proxy.example.net")).build().baseUri());
        assertEquals(ApiEnvironment.OTE.baseUri(),
                DomainNameApiClient.builder().environment(ApiEnvironment.OTE).build().baseUri());
    }
}
