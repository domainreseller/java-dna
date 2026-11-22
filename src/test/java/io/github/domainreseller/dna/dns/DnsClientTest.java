package io.github.domainreseller.dna.dns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.domainreseller.dna.TestClient;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DnsClientTest {
    @Test
    void listReadsRecordSets() {
        TestClient t = new TestClient();
        t.http().respondJson("""
                [{"name":"www.example.com.","type":"A","ttl":300,
                  "records":[{"content":"192.0.2.1","disabled":false},{"content":"192.0.2.2","disabled":true}]},
                 {"name":"","type":"txt","ttl":"3600","records":null}]
                """);

        List<DnsRecordSet> sets = t.client().dns().list("example.com");

        assertEquals("GET", t.lastRequest().method());
        assertEquals("domains/zones", t.lastRequest().apiPath());
        assertEquals("example.com", t.lastRequest().query("domainName"));
        assertEquals(RecordType.A, sets.get(0).type());
        assertEquals(List.of("192.0.2.1"), sets.get(0).values());
        assertEquals(RecordType.TXT, sets.get(1).type());
        assertEquals("TXT", sets.get(1).type().value());
        assertEquals(3600, sets.get(1).ttl());
        assertEquals(List.of(), sets.get(1).records());
    }

    @Test
    void addWrapsTheRecordInZoneStruct() {
        TestClient t = new TestClient();
        t.http().respond(200, "");

        t.client().dns().add("example.com", "@", RecordType.of("mx"), List.of("10 mx1.example.net", "20 mx2.example.net"), 600);

        var request = t.lastRequest();
        assertEquals("POST", request.method());
        assertEquals("example.com", request.query("domainName"));
        assertEquals("{\"zoneStruct\":{\"name\":\"@\",\"ttl\":600,\"type\":\"MX\",\"contents\":[\"10 mx1.example.net\",\"20 mx2.example.net\"]}}",
                request.body());
    }

    @Test
    void replaceTargetsTheRecordNameAndCanRename() {
        TestClient t = new TestClient();
        t.http().respond(200, "");

        t.client().dns().replace("example.com", "www", RecordType.A, List.of("192.0.2.9"), 300, "web");

        var request = t.lastRequest();
        assertEquals("PUT", request.method());
        assertEquals("www", request.query("recordName"));
        assertEquals("web", request.json().path("zoneStruct").path("name").asText());
        assertEquals(300, request.json().path("zoneStruct").path("ttl").asInt());
    }

    @Test
    void deleteSendsTheValueInTheQuery() {
        TestClient t = new TestClient();
        t.http().respond(200, "");

        t.client().dns().delete("example.com", "www", RecordType.TXT, "v=spf1 -all");

        var request = t.lastRequest();
        assertEquals("DELETE", request.method());
        assertEquals("www", request.query("Name"));
        assertEquals("v=spf1 -all", request.query("Record"));
        assertEquals("TXT", request.query("RecordType"));
        assertNull(request.body());
    }

    @Test
    void applyHasNoBody() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"nameServersChanged\":true,\"previousNameServers\":[\"ns1.old.net\"],\"systemNameServers\":[\"tr.apiname.com\"]}");

        ZoneApplyResult result = t.client().dns().apply("example.com");

        assertEquals("domains/zones/apply", t.lastRequest().apiPath());
        assertNull(t.lastRequest().body());
        assertTrue(result.nameServersChanged());
        assertEquals(List.of("tr.apiname.com"), result.systemNameServers());
        assertEquals(List.of(), result.currentNameServers());
    }

    @ParameterizedTest
    @CsvSource({
            "@, CNAME, target.example.net, 3600",
            "'', CNAME, target.example.net, 3600",
            "@, CAA, letsencrypt.org, 3600",
            "www, A, 192.0.2.1, 0",
            "www, A, 192.0.2.1, 86401",
            "www, A, ' ', 3600",
    })
    void rejectsRecordsTheApiWouldRefuse(String name, String type, String value, int ttl) {
        TestClient t = new TestClient();

        assertThrows(IllegalArgumentException.class,
                () -> t.client().dns().add("example.com", name, RecordType.of(type), value, ttl));
        assertTrue(t.requests().isEmpty());
    }

    @Test
    void acceptsAValidCaaRecord() {
        TestClient t = new TestClient();
        t.http().respond(200, "");

        t.client().dns().add("example.com", "@", RecordType.CAA, "0 issue \"letsencrypt.org\"");

        assertEquals("CAA", t.lastRequest().json().path("zoneStruct").path("type").asText());
    }
}
