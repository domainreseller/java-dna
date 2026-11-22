package io.github.domainreseller.dna.domains;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.domainreseller.dna.ApiErrorKind;
import io.github.domainreseller.dna.DomainNameApiException;
import io.github.domainreseller.dna.TestClient;
import io.github.domainreseller.dna.contacts.ContactHandle;
import io.github.domainreseller.dna.contacts.ContactInfo;
import io.github.domainreseller.dna.contacts.ContactType;
import io.github.domainreseller.dna.contacts.DomainContact;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class DomainsClientTest {
    private static final List<String> NAME_SERVERS = List.of("tr.apiname.com", "eu.apiname.com");

    private static ContactInfo contact() {
        return new ContactInfo()
                .firstName("Ada").lastName("Lovelace").email("ada@example.com").address("1 Example Street")
                .city("Istanbul").state("Istanbul").postalCode("34000").country("TR")
                .phoneCountryCode("90").phone("5551234567");
    }

    @Test
    void searchPostsTheDomainAndCurrency() {
        TestClient t = new TestClient(b -> b.readOnly(true));
        t.http().respondJson("""
                {"success":true,"info":{"domainName":"example.com","tld":"COM","status":"AVAILABLE","isPremium":false,
                 "isDocumentRequired":false,"price":"9.99","currency":"USD","period":1}}
                """);

        DomainSearchResult result = t.client().domains().search("example.com", "USD");

        assertEquals("POST", t.lastRequest().method());
        assertEquals("{\"domainName\":\"example.com\",\"currency\":\"USD\"}", t.lastRequest().body());
        assertTrue(result.info().isAvailable());
        assertEquals(new BigDecimal("9.99"), result.info().price());
        assertFalse(result.info().isPremium());
    }

    @Test
    void bulkSearchSendsAnArray() {
        TestClient t = new TestClient();
        t.http().respondJson("""
                {"success":true,"infos":[{"domainName":"example.com","status":"AVAILABLE"},
                                         {"domainName":"example.net","status":"NOTAVAILABLE","reason":"Domain exists"}]}
                """);

        BulkSearchResult result = t.client().domains().bulkSearch(List.of(" example.com", "example.net"));

        assertEquals("[{\"domainName\":\"example.com\"},{\"domainName\":\"example.net\"}]", t.lastRequest().body());
        assertEquals(2, result.results().size());
        assertEquals(List.of("example.com"),
                result.available().stream().map(DomainAvailability::domainName).collect(Collectors.toList()));
    }

    @Test
    void bulkSearchNeedsAtLeastOneDomain() {
        TestClient t = new TestClient();
        assertThrows(IllegalArgumentException.class, () -> t.client().domains().bulkSearch(List.of()));
        assertThrows(IllegalArgumentException.class, () -> t.client().domains().bulkSearch(List.of("example.com", " ")));
        assertTrue(t.requests().isEmpty());
    }

    @Test
    void listSendsStatusIdsAsACommaSeparatedList() {
        TestClient t = new TestClient();
        t.http().respondJson("""
                {"totalCount":1,"items":[{"domainName":"example.com","status":0,"lockStatus":true,"remainingDay":30,
                  "nameservers":[{"name":"tr.apiname.com","isChildnameServer":false}]}]}
                """);

        var page = t.client().domains().list(new ListDomainsRequest()
                .domainName("example").statuses(DomainStatus.ACTIVE, DomainStatus.PENDING_DELETE).maxResultCount(50));

        assertEquals("example", t.lastRequest().query("DomainName"));
        assertEquals("0,7", t.lastRequest().query("StatusIds"));
        assertEquals("50", t.lastRequest().query("MaxResultCount"));
        Domain domain = page.items().get(0);
        assertEquals(DomainStatus.ACTIVE, domain.status());
        assertTrue(domain.isLocked());
        assertEquals(30, domain.remainingDays());
        assertEquals("tr.apiname.com", domain.nameServers().get(0).name());
    }

    @Test
    void getReadsTheFullDetails() {
        TestClient t = new TestClient();
        t.http().respondJson("""
                {"domainName":"example.com.tr","status":"Active","authCode":"EPP-1","lockStatus":"true",
                 "privacyProtectionStatus":false,"nameservers":["tr.apiname.com"],
                 "hosts":[{"name":"ns1.example.com.tr","ipAddresses":[{"ipAddress":"192.0.2.1","ipVersion":"v4"}]}],
                 "contacts":[{"contactType":"registrant","firstName":"Ada","eMail":"ada@example.com","handle":"DNA-1"}],
                 "additionalAttributes":{"TRABISDOMAINCATEGORY":1,"TRABISCITIZIENID":"123"}}
                """);

        DomainDetails details = t.client().domains().get("example.com.tr");

        assertEquals("domains/info", t.lastRequest().apiPath());
        assertEquals("example.com.tr", t.lastRequest().query("DomainName"));
        assertEquals("EPP-1", details.authCode());
        assertTrue(details.isLocked());
        assertEquals(IpVersion.V4, details.hosts().get(0).ipAddresses().get(0).ipVersion());
        assertEquals("ada@example.com", details.contact(ContactType.REGISTRANT).orElseThrow().email());
        assertTrue(details.contact(ContactType.TECH).isEmpty());
        assertEquals(Map.of("TRABISDOMAINCATEGORY", "1", "TRABISCITIZIENID", "123"), details.additionalAttributes());
    }

    @Test
    void registerSendsAllFourContactRoles() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"success\":true,\"status\":\"Active\",\"expirationDate\":\"2028-11-22T00:00:00Z\"}");

        DomainOperationResult result = t.client().domains().register("example.com", contact(), NAME_SERVERS, 2);

        JsonNode body = t.lastRequest().json();
        assertEquals("domains/register-with-contacts", t.lastRequest().apiPath());
        assertEquals(2, body.path("period").asInt());
        assertEquals(2, body.path("nameServers").size());
        assertEquals(4, body.path("contacts").size());
        assertEquals("Billing", body.path("contacts").get(3).path("contactType").asText());
        assertEquals("ada@example.com", body.path("contacts").get(0).path("eMail").asText());
        assertFalse(body.path("useTrusteeContact").asBoolean());
        assertTrue(result.success());
    }

    @Test
    void registerRefusesMissingRoles() {
        TestClient t = new TestClient();
        RegisterDomainRequest request = new RegisterDomainRequest("example.com")
                .nameServers(NAME_SERVERS)
                .contacts(List.of(new DomainContact(contact(), ContactType.REGISTRANT)));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> t.client().domains().register(request));

        assertTrue(e.getMessage().contains("Admin, Tech, Billing"));
        assertTrue(t.requests().isEmpty());
    }

    @Test
    void registerWithATrusteeNeedsNoOtherRoles() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"success\":true}");

        t.client().domains().register(new RegisterDomainRequest("example.com.tr")
                .nameServers(NAME_SERVERS)
                .contacts(List.of(new DomainContact(contact(), ContactType.REGISTRANT)))
                .tldAttributes(Map.of("TRABISDOMAINCATEGORY", "1"))
                .useTrusteeContact(true));

        JsonNode body = t.lastRequest().json();
        assertTrue(body.path("useTrusteeContact").asBoolean());
        assertEquals("1", body.path("tldAttributes").path("TRABISDOMAINCATEGORY").asText());
    }

    @Test
    void registerRefusesAZeroPeriod() {
        TestClient t = new TestClient();
        assertThrows(IllegalArgumentException.class,
                () -> t.client().domains().register("example.com", contact(), NAME_SERVERS, 0));
    }

    @Test
    void registerWithHandleUsesItForEveryRole() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"success\":true}");

        t.client().domains().registerWithHandle("example.com", "DNA-1", NAME_SERVERS, 1);

        JsonNode handles = t.lastRequest().json().path("handles");
        assertEquals("domains/register", t.lastRequest().apiPath());
        assertEquals(4, handles.size());
        assertEquals("Tech", handles.get(2).path("contactType").asText());
        assertEquals("DNA-1", handles.get(2).path("handle").asText());
    }

    @Test
    void anAnswerWithSuccessFalseIsReturnedForTheCallerToCheck() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"success\":false,\"message\":\"Domain is not available\"}");

        DomainOperationResult result = t.client().domains().renew("example.com");

        assertFalse(result.success());
        assertEquals("Domain is not available", result.message());
    }

    @Test
    void renewTransferAndChecks() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"success\":true}")
                .respondJson("{\"renewabilityStatus\":true,\"isPremium\":true,\"premiumPrice\":120,\"premiumCurrency\":\"USD\"}")
                .respondJson("{\"transferAvailabilityStatus\":true,\"authCodeIsValid\":true,\"domainStatuses\":[\"ok\"]}")
                .respondJson("{\"success\":true}")
                .respondJson("{\"domainName\":\"example.com\",\"status\":\"pending\"}");

        t.client().domains().renew("example.com", 3);
        assertEquals("{\"domainName\":\"example.com\",\"period\":3}", t.lastRequest().body());

        RenewalCheck renewal = t.client().domains().checkRenewal("example.com");
        assertEquals("domains/renew/check", t.lastRequest().apiPath());
        assertTrue(renewal.isRenewable());
        assertEquals(new BigDecimal("120"), renewal.premiumPrice());

        TransferCheck check = t.client().domains().checkTransfer("example.com", "EPP");
        assertEquals("{\"domainName\":\"example.com\",\"authCode\":\"EPP\"}", t.lastRequest().body());
        assertTrue(check.isTransferable());
        assertTrue(check.isAuthCodeValid());
        assertEquals(List.of("ok"), check.domainStatuses());

        t.client().domains().transfer("example.com", "EPP");
        assertEquals("{\"domainName\":\"example.com\",\"authCode\":\"EPP\",\"period\":1}", t.lastRequest().body());

        TransferStatus status = t.client().domains().getTransferStatus("example.com");
        assertEquals("domains/transfers/query", t.lastRequest().apiPath());
        assertEquals("pending", status.status());
    }

    @Test
    void lockPrivacyRestoreAndAuthCode() {
        TestClient t = new TestClient();
        t.http().respond(200, "").respond(200, "").respond(200, "").respond(200, "")
                .respondJson("{\"domainName\":\"example.com\",\"authCode\":\"NEW-CODE\"}");

        t.client().domains().lock("example.com");
        assertEquals("domains/lock", t.lastRequest().apiPath());
        t.client().domains().unlock("example.com");
        assertEquals("domains/unlock", t.lastRequest().apiPath());
        t.client().domains().setPrivacyProtection("example.com", true);
        assertEquals("{\"domainName\":\"example.com\",\"privacyStatus\":true}", t.lastRequest().body());
        t.client().domains().restore("example.com");
        assertEquals("domains/restore", t.lastRequest().apiPath());
        DomainAuthCode code = t.client().domains().updateAuthCode("example.com");
        assertEquals("PUT", t.lastRequest().method());
        assertEquals("NEW-CODE", code.authCode());
    }

    @Test
    void setNameServersSendsTheDnssecModeAsANumber() {
        TestClient t = new TestClient();
        t.http().respond(200, "");

        t.client().domains().setNameServers("example.com", NAME_SERVERS, DnssecMode.DISABLE_DNSSEC);

        assertEquals("{\"domainName\":\"example.com\",\"nameServers\":[\"tr.apiname.com\",\"eu.apiname.com\"],\"dnssecMode\":2}",
                t.lastRequest().body());
    }

    @Test
    void contactChanges() {
        TestClient t = new TestClient();
        t.http().respond(200, "").respond(200, "");

        t.client().domains().updateContacts("example.com", DomainContact.forAllRoles(contact()));
        assertEquals("domains/contacts/update", t.lastRequest().apiPath());
        assertEquals(4, t.lastRequest().json().path("contacts").size());

        t.client().domains().setContactHandles("example.com", List.of(new ContactHandle(ContactType.TECH, "DNA-9")));
        assertEquals("{\"domainName\":\"example.com\",\"handleItems\":[{\"contactType\":\"Tech\",\"handle\":\"DNA-9\"}]}",
                t.lastRequest().body());
    }

    @Test
    void forwarding() {
        TestClient t = new TestClient();
        t.http().respond(200, "")
                .respondJson("{\"redirectAlias\":\"https://www.example.net\",\"redirectType\":\"frame\"}")
                .respond(200, "")
                .respond(404, "Redirect information not found");

        t.client().domains().forwarding().set("example.com", "https://www.example.net", ForwardType.FRAME);
        assertEquals("{\"domainName\":\"example.com\",\"redirectAddress\":\"https://www.example.net\",\"forwardType\":\"Frame\"}",
                t.lastRequest().body());

        DomainForwarding forwarding = t.client().domains().forwarding().get("example.com");
        assertEquals("example.com", t.lastRequest().query("domainName"));
        assertEquals("https://www.example.net", forwarding.redirectAddress());
        assertEquals(ForwardType.FRAME, forwarding.forwardType());

        t.client().domains().forwarding().delete("example.com");
        assertEquals("DELETE", t.lastRequest().method());
        assertEquals("example.com", t.lastRequest().query("DomainName"));

        DomainNameApiException e = assertThrows(DomainNameApiException.class,
                () -> t.client().domains().forwarding().get("example.com"));
        assertEquals(ApiErrorKind.NOT_FOUND, e.kind());
    }

    @Test
    void childNameServersDetectIpVersions() {
        TestClient t = new TestClient();
        t.http().respond(200, "").respond(200, "").respond(200, "");

        t.client().domains().childNameServers().add("example.com", "ns1.example.com", "192.0.2.10", "2001:db8::10");
        assertEquals("{\"domainName\":\"example.com\",\"hostName\":\"ns1.example.com\",\"ipAddresses\":["
                + "{\"ipAddress\":\"192.0.2.10\",\"ipVersion\":\"v4\"},{\"ipAddress\":\"2001:db8::10\",\"ipVersion\":\"v6\"}]}",
                t.lastRequest().body());

        t.client().domains().childNameServers().update("example.com", "ns1.example.com", List.of("192.0.2.20"), "ns2.example.com");
        assertEquals("PUT", t.lastRequest().method());
        assertEquals("ns2.example.com", t.lastRequest().json().path("newHostName").asText());

        t.client().domains().childNameServers().delete("example.com", "ns1.example.com");
        assertEquals("ns1.example.com", t.lastRequest().query("HostName"));
    }

    @Test
    void childNameServersRejectBadAddressesBeforeSending() {
        TestClient t = new TestClient();

        assertThrows(IllegalArgumentException.class,
                () -> t.client().domains().childNameServers().add("example.com", "ns1.example.com", "ns.example.net"));
        assertTrue(t.requests().isEmpty());
    }

    @Test
    void documents() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"uploaded\":[{\"name\":\"id.pdf\",\"size\":2,\"type\":2}],\"totalDocumentCount\":1}")
                .respondJson("{\"totalCount\":1,\"items\":[{\"name\":\"id.pdf\",\"type\":2,\"status\":3,\"operationType\":1}]}");

        DocumentUploadResult upload = t.client().domains().documents().upload("example.com.tr",
                List.of(DocumentUpload.fromBytes(DocumentType.PASSPORT, "id.pdf", new byte[] {1, 2})));
        assertEquals(2, t.lastRequest().json().path("documents").get(0).path("documentType").asInt());
        assertEquals(DocumentType.PASSPORT, upload.uploaded().get(0).type());

        List<DomainDocument> documents = t.client().domains().documents().listAll("example.com.tr").collect(Collectors.toList());
        assertEquals("domains/documents", t.lastRequest().apiPath());
        assertEquals(DocumentStatus.APPROVED, documents.get(0).status());
        assertEquals(DocumentOperationType.CREATE, documents.get(0).operationType());
    }
}
