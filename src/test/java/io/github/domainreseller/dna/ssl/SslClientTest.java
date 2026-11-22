package io.github.domainreseller.dna.ssl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.domainreseller.dna.DomainNameApiException;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.TestClient;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SslClientTest {
    private static final UUID ID = UUID.fromString("7d9f1a52-3c1e-4c9e-9a51-6b8c2f3e4d10");
    private static final UUID NEW_ID = UUID.fromString("11111111-2222-4333-8444-555555555555");

    private static CertificateContact contact() {
        return new CertificateContact().firstName("Ada").lastName("Lovelace").companyName("Example Ltd")
                .email("ada@example.com").address("1 Example Street").city("Istanbul").postalCode("34000")
                .country("TR").phoneCountryCode("90").phone("5551234567");
    }

    @Test
    void purchaseIsBillable() {
        TestClient t = new TestClient(b -> b.readOnly(true));

        DomainNameApiException e = assertThrows(DomainNameApiException.class, () -> t.client().ssl().purchase("example.com", "PKG"));

        assertEquals(OperationSafety.BILLABLE, e.safety());
        assertTrue(t.requests().isEmpty());
    }

    @Test
    void purchaseAndConfigure() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"success\":true,\"certificateId\":\"" + ID + "\",\"status\":\"WaitingForConfiguration\"}")
                .respondJson("{\"success\":true,\"certificateId\":\"" + ID + "\",\"dcvDetails\":\"_dcv.example.com CNAME x\"}");

        SslOperationResult order = t.client().ssl().purchase("example.com", "PKG");
        assertEquals("ssls/initialize", t.lastRequest().apiPath());
        assertEquals(ID, order.certificateId());

        SslOperationResult configured = t.client().ssl().configure(new ConfigureCertificateRequest(order.certificateId(), "CSR", contact())
                .validationMethod(ValidationMethod.EMAIL).approverEmail("admin@example.com").webServerType(WebServerType.APACHE));

        JsonNode body = t.lastRequest().json();
        assertEquals("PUT", t.lastRequest().method());
        assertEquals(ID.toString(), body.path("certificateId").asText());
        assertEquals("Email", body.path("authenticationType").asText());
        assertEquals("admin@example.com", body.path("approverMail").asText());
        assertEquals("Apache", body.path("webServerType").asText());
        assertEquals("ada@example.com", body.path("contact").path("eMail").asText());
        assertTrue(body.path("contact").path("type").isMissingNode(), "the role is never sent");
        assertEquals("_dcv.example.com CNAME x", configured.dcvDetails());
    }

    @Test
    void emailValidationNeedsAnApprover() {
        TestClient t = new TestClient();

        assertThrows(IllegalArgumentException.class, () -> t.client().ssl().configure(
                new ConfigureCertificateRequest(ID, "CSR", contact()).validationMethod(ValidationMethod.of("email"))));
        assertTrue(t.requests().isEmpty());
    }

    @Test
    void configureNeedsACompleteContact() {
        TestClient t = new TestClient();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> t.client().ssl().configure(
                new ConfigureCertificateRequest(ID, "CSR", contact().companyName(null))));
        assertTrue(e.getMessage().contains("companyName"));
    }

    @Test
    void csrStatusDetailsContentReissueAndRevoke() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"domainName\":\"example.com\",\"isWildcard\":false}")
                .respondJson("{\"certificateId\":\"" + ID + "\",\"status\":\"Active\",\"isIssued\":true}")
                .respondJson("""
                        {"certificateId":"%s","domainName":"example.com","subscription":{"expiresAt":"2027-11-22T00:00:00Z"},
                         "configuration":{"approverMail":"admin@example.com"},
                         "contacts":[{"firstName":"Ada","eMail":"ada@example.com","type":"Admin"}]}
                        """.formatted(ID))
                .respondJson("{\"certificateId\":\"" + ID + "\",\"certificate\":\"CERT\",\"intermediateCertificate1\":\"INT\"}")
                .respondJson("{\"success\":true}")
                .respondJson("{\"success\":true,\"status\":\"Revoked\"}");

        CsrDetails csr = t.client().ssl().decodeCsr("CSR");
        assertEquals("{\"csr\":\"CSR\"}", t.lastRequest().body());
        assertEquals("example.com", csr.domainName());

        CertificateStatus status = t.client().ssl().refreshStatus(ID);
        assertEquals("PUT", t.lastRequest().method());
        assertEquals("{\"certificateId\":\"" + ID + "\"}", t.lastRequest().body());
        assertTrue(status.isIssued());

        CertificateDetails details = t.client().ssl().get(ID);
        assertEquals(ID.toString(), t.lastRequest().query("CertificateId"));
        assertEquals(2027, details.subscription().expiresAt().getYear());
        assertEquals("Admin", details.contacts().get(0).type());
        assertEquals("ada@example.com", details.contacts().get(0).email());

        CertificateContent content = t.client().ssl().getContent(ID);
        assertEquals("ssls/certificate-content", t.lastRequest().apiPath());
        assertEquals("CERT\nINT\n", content.fullChain());

        t.client().ssl().reissue(new ReissueCertificateRequest(ID).csr("NEW").approverEmail("admin@example.com"));
        assertEquals("{\"certificateId\":\"" + ID + "\",\"approverMail\":\"admin@example.com\",\"csr\":\"NEW\"}",
                t.lastRequest().body());

        SslOperationResult revoked = t.client().ssl().revoke(ID);
        assertEquals("DELETE", t.lastRequest().method());
        assertEquals(ID.toString(), t.lastRequest().query("CertificateId"));
        assertEquals("Revoked", revoked.status());
    }

    @Test
    void renewReturnsTheNewCertificate() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"success\":true,\"certificateId\":\"" + NEW_ID + "\"}");

        SslOperationResult renewal = t.client().ssl().renew(ID);

        assertEquals("ssls/renew", t.lastRequest().apiPath());
        assertEquals("{\"certificateId\":\"" + ID + "\"}", t.lastRequest().body());
        assertEquals(NEW_ID, renewal.certificateId());
    }

    @Test
    void renewIsNotSentAgainAfterANetworkError() {
        TestClient t = new TestClient();
        t.http().fail(new IOException("reset"));

        DomainNameApiException e = assertThrows(DomainNameApiException.class, () -> t.client().ssl().renew(ID));

        assertTrue(e.isOutcomeUnknown());
        assertEquals(1, t.requests().size());
    }

    @Test
    void listSendsTheFilterAndReadsTheItems() {
        TestClient t = new TestClient();
        t.http().respondJson("""
                {"totalCount":2,"items":[
                  {"certificateId":"%s","domainName":"example.com","status":"WaitingForInstall","isConfigured":false,
                   "renewedFromCertificateId":"%s","certificateExpiresAt":null},
                  {"certificateId":"%s","domainName":"example.com","status":"Renewed","isIssued":true}]}
                """.formatted(NEW_ID, ID, ID));

        var page = t.client().ssl().list(new ListCertificatesRequest().domainName("example.com").maxResultCount(10));

        assertEquals("ssls", t.lastRequest().apiPath());
        assertEquals("example.com", t.lastRequest().query("DomainName"));
        assertEquals("10", t.lastRequest().query("MaxResultCount"));
        assertNull(t.lastRequest().query("Sorting"));
        assertEquals(ID, page.items().get(0).renewedFromCertificateId());
        assertNull(page.items().get(0).certificateExpiresAt());
        assertTrue(page.items().get(1).isIssued());
    }

    @Test
    void listWithoutARequestSendsNoParameters() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"totalCount\":0,\"items\":[]}");

        t.client().ssl().list();

        assertEquals(null, t.lastRequest().uri().getQuery());
    }

    @Test
    void listAllPagesUntilTheTotalIsReached() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"totalCount\":2,\"items\":[{\"domainName\":\"a.com\"}]}")
                .respondJson("{\"totalCount\":2,\"items\":[{\"domainName\":\"b.com\"}]}");

        List<String> names = t.client().ssl().listAll(new ListCertificatesRequest().maxResultCount(1))
                .map(CertificateListItem::domainName).collect(Collectors.toList());

        assertEquals(List.of("a.com", "b.com"), names);
    }

    @Test
    void listCannotBeSorted() {
        TestClient t = new TestClient();

        assertThrows(IllegalArgumentException.class,
                () -> t.client().ssl().list(new ListCertificatesRequest().sorting("domainName")));
    }

    @Test
    void approversOfAnExistingCertificate() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"mails\":[\"admin@example.com\",\"webmaster@example.com\"]}");

        List<String> emails = t.client().ssl().getApproverEmails(ID);

        assertEquals(ID.toString(), t.lastRequest().query("CertificateId"));
        assertNull(t.lastRequest().query("DomainName"));
        assertEquals(List.of("admin@example.com", "webmaster@example.com"), emails);
    }

    @Test
    void approversBeforeAPurchase() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"mails\":[]}");

        t.client().ssl().getApproverEmails("example.com", "PKG");

        assertEquals("example.com", t.lastRequest().query("DomainName"));
        assertEquals("PKG", t.lastRequest().query("PackageCode"));
        assertNull(t.lastRequest().query("CertificateId"));
    }

    @Test
    void approversNeedACertificateOrADomainWithItsPackage() {
        TestClient t = new TestClient();

        assertThrows(IllegalArgumentException.class, () -> t.client().ssl().getApproverEmails("example.com", ""));
        assertThrows(IllegalArgumentException.class, () -> t.client().ssl().getApproverEmails(" ", "PKG"));
        assertTrue(t.requests().isEmpty());
    }

    static Stream<Arguments> operationsOnACertificate() {
        UUID empty = new UUID(0, 0);
        return Stream.<Consumer<SslClient>>of(
                ssl -> ssl.renew(empty),
                ssl -> ssl.refreshStatus(empty),
                ssl -> ssl.get(null),
                ssl -> ssl.getContent(empty),
                ssl -> ssl.reissue(new ReissueCertificateRequest(empty)),
                ssl -> ssl.getApproverEmails(empty),
                ssl -> ssl.revoke(empty),
                ssl -> ssl.configure(new ConfigureCertificateRequest(empty, "CSR", contact()))).map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("operationsOnACertificate")
    void anEmptyCertificateIdIsRefusedBeforeSending(Consumer<SslClient> call) {
        TestClient t = new TestClient();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> call.accept(t.client().ssl()));

        assertTrue(e.getMessage().contains("certificateId"));
        assertTrue(t.requests().isEmpty());
    }

    @Test
    void generateCsrNeedsTheFullSubject() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"csr\":\"CSR\",\"privateKey\":\"KEY\"}");

        assertThrows(IllegalArgumentException.class, () -> t.client().ssl().generateCsr(new GenerateCsrRequest("example.com")));

        GeneratedCsr csr = t.client().ssl().generateCsr(new GenerateCsrRequest("example.com")
                .country("TR").state("Istanbul").locality("Istanbul").organization("Example").organizationUnit("IT"));
        assertEquals("KEY", csr.privateKey());
        assertFalse(csr.toString().contains("KEY"), "the private key stays out of logs");
    }
}
