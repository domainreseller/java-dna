package io.github.domainreseller.dna.dnssec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.domainreseller.dna.TestClient;
import java.util.List;
import org.junit.jupiter.api.Test;

class DnssecClientTest {
    private static final DsRecordData RECORD = DsRecordData.parse("12345 13 2 49FD46E6C4B45C55D4AC69CBD3CD34AC1AFE51DE");

    @Test
    void getReadsTheState() {
        TestClient t = new TestClient();
        t.http().respondJson("""
                {"domainName":"example.com","isDnssecAvailable":true,"status":2,"delegationSigned":true,
                 "dsRecords":[{"keyTag":12345,"algorithm":13,"digestType":2,"digest":"49FD","status":1}],
                 "allowedAlgorithms":[8,13,15],"allowedDigestTypes":[2,4],"maxDsRecordCount":4}
                """);

        DnssecInfo info = t.client().dnssec().get("example.com");

        assertEquals("example.com", t.lastRequest().query("DomainName"));
        assertEquals(DnssecStatus.SIGNED, info.status());
        assertTrue(info.isDelegationSigned());
        assertEquals(DsRecordStatus.ACTIVE, info.dsRecords().get(0).status());
        assertEquals(List.of(DnssecAlgorithm.RSA_SHA256, DnssecAlgorithm.ECDSA_P256_SHA256, DnssecAlgorithm.ED25519),
                info.allowedAlgorithms());
        assertEquals(List.of(DsDigestType.SHA256, DsDigestType.SHA384), info.allowedDigestTypes());
    }

    @Test
    void addSendsNumbers() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"items\":[{\"keyTag\":12345,\"operationType\":0,\"isSuccess\":true,\"isPending\":true}]}");

        List<DnssecOperationResult> results = t.client().dnssec().add("example.com", RECORD);

        assertEquals("{\"domainName\":\"example.com\",\"records\":[{\"keyTag\":12345,\"algorithm\":13,\"digestType\":2,"
                + "\"digest\":\"49FD46E6C4B45C55D4AC69CBD3CD34AC1AFE51DE\"}]}", t.lastRequest().body());
        assertEquals(DnssecOperationType.ADD, results.get(0).operationType());
        assertTrue(results.get(0).isPending());
    }

    @Test
    void removeAndDisable() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"operationType\":1,\"isSuccess\":true}").respondJson("{\"operationType\":2,\"isSuccess\":true}");

        t.client().dnssec().remove("example.com", RECORD);
        var remove = t.lastRequest();
        assertEquals("DELETE", remove.method());
        assertEquals("12345", remove.query("KeyTag"));
        assertEquals("13", remove.query("Algorithm"));
        assertEquals("2", remove.query("DigestType"));
        assertEquals(RECORD.digest(), remove.query("Digest"));

        DnssecOperationResult disabled = t.client().dnssec().disable("example.com");
        assertEquals("domains/dns/dnssec/disable", t.lastRequest().apiPath());
        assertEquals(DnssecOperationType.REMOVE_ALL, disabled.operationType());
    }
}
