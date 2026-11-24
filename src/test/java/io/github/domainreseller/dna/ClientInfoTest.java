package io.github.domainreseller.dna;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ClientInfoTest {
    private static final String SDK_VERSION = DomainNameApiClient.normalizeVersion(DomainNameApiClient.VERSION);

    @Test
    void sendsJavaAndTheSdkVersionByDefault() {
        TestClient t = new TestClient();
        t.http().respondJson("{}");

        t.client().account().get();

        assertEquals("java", t.lastRequest().header("X-App"));
        assertEquals(SDK_VERSION, t.lastRequest().header("X-Version"));
        assertTrue(SDK_VERSION.matches("[0-9]+(\\.[0-9]+)*"), SDK_VERSION);
        assertEquals("java", t.client().app());
        assertEquals(SDK_VERSION, t.client().appVersion());
    }

    @Test
    void sendsTheApplicationNamedOnTheBuilder() {
        TestClient t = new TestClient(b -> b.app("WiseCP5", "V2.0.20-beta"));
        t.http().respondJson("{}");

        t.client().account().get();

        assertEquals("wisecp5", t.lastRequest().header("X-App"));
        assertEquals("2.0.20", t.lastRequest().header("X-Version"));
    }

    @Test
    void keepsTheUserAgent() {
        TestClient t = new TestClient(b -> b.app("whmcs", "8.13.1"));
        t.http().respondJson("{}");

        t.client().account().get();

        assertEquals(DomainNameApiClient.DEFAULT_USER_AGENT, t.lastRequest().header("User-Agent"));
    }

    @Test
    void sendsTheHeadersOnEveryAttempt() {
        TestClient t = new TestClient(b -> b.app("whmcs", "8.13.1"));
        t.http().respond(503, "").respond(429, "", "Retry-After", "0").respondJson("{}");

        t.client().account().get();

        List<RecordedRequest> requests = t.requests();
        assertEquals(3, requests.size());
        for (RecordedRequest request : requests) {
            assertEquals("whmcs", request.header("X-App"));
            assertEquals("8.13.1", request.header("X-Version"));
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
            "WiseCP5|wisecp5",
            "' My Panel '|mypanel",
            "Host_Bill.v-2|host_bill.v-2",
            "Ünlü|nl",
            "''|java",
            "'  '|java",
            "'!!!'|java",
            "NULL|java",
    }, delimiter = '|', nullValues = "NULL")
    void normalizesTheApplicationName(String name, String expected) {
        assertEquals(expected, new TestClient(b -> b.app(name, "1.0")).client().app());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "V2.0.20-beta|2.0.20",
            "8.13.1|8.13.1",
            "v.1.2.|1.2",
            "' 3 '|3",
            "''|SDK",
            "beta|SDK",
            "NULL|SDK",
    }, delimiter = '|', nullValues = "NULL")
    void normalizesTheVersion(String version, String expected) {
        String want = expected.equals("SDK") ? SDK_VERSION : expected;
        assertEquals(want, new TestClient(b -> b.app("whmcs", version)).client().appVersion());
    }

    @Test
    void readsTheApplicationFromTheEnvironment() {
        Map<String, String> env = Map.of("DNA_APP", "HostBill", "DNA_APP_VERSION", "V2024-01");

        DomainNameApiClient client = DomainNameApiClient.Builder.fromEnvironment(env::get).build();

        assertEquals("hostbill", client.app());
        assertEquals("202401", client.appVersion());
    }

    @Test
    void environmentWithoutApplicationKeepsTheDefaults() {
        DomainNameApiClient client = DomainNameApiClient.Builder.fromEnvironment(Map.<String, String>of()::get).build();

        assertEquals("java", client.app());
        assertEquals(SDK_VERSION, client.appVersion());
    }

    @Test
    void builderOverridesTheEnvironment() {
        Map<String, String> env = Map.of("DNA_APP", "hostbill", "DNA_APP_VERSION", "1.0");

        DomainNameApiClient client = DomainNameApiClient.Builder.fromEnvironment(env::get).app("blesta", "5.10.2").build();

        assertEquals("blesta", client.app());
        assertEquals("5.10.2", client.appVersion());
    }
}
