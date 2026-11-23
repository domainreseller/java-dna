package io.github.domainreseller.dna;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.domainreseller.dna.accounts.TransactionType;
import io.github.domainreseller.dna.contacts.ContactInfo;
import io.github.domainreseller.dna.contacts.ContactType;
import io.github.domainreseller.dna.dns.RecordType;
import io.github.domainreseller.dna.dnssec.DnssecAlgorithm;
import io.github.domainreseller.dna.dnssec.DsDigestType;
import io.github.domainreseller.dna.dnssec.DsRecordData;
import io.github.domainreseller.dna.domains.DocumentType;
import io.github.domainreseller.dna.domains.DocumentUpload;
import io.github.domainreseller.dna.domains.Domain;
import io.github.domainreseller.dna.domains.DomainStatus;
import io.github.domainreseller.dna.domains.ForwardType;
import io.github.domainreseller.dna.domains.HostIpAddress;
import io.github.domainreseller.dna.domains.IpVersion;
import io.github.domainreseller.dna.internal.Json;
import io.github.domainreseller.dna.ssl.CertificateContent;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class SerializationTest {
    private static <T> T read(String json, Class<T> type) throws JsonProcessingException {
        return Json.MAPPER.readValue(json, type);
    }

    @ParameterizedTest
    @CsvSource({"txt, TXT", "' cname ', CNAME", "dnskey, DNSKey", "nsec3param, NSEC3Param", "SPF, SPF"})
    void recordTypesAreNormalised(String input, String expected) {
        assertEquals(expected, RecordType.of(input).value());
    }

    @Test
    void textEnumsCompareIgnoringCase() {
        assertEquals(RecordType.TXT, RecordType.of("txt"));
        assertEquals(RecordType.TXT.hashCode(), RecordType.of("tXt").hashCode());
        assertNotEquals(RecordType.A, RecordType.AAAA);
        assertTrue(RecordType.CAA.isKnown());
        assertFalse(RecordType.of("SPF").isKnown());
        assertEquals(ForwardType.FRAME, ForwardType.of("frame"));
    }

    @Test
    void textEnumsRoundTripAsJsonStrings() throws JsonProcessingException {
        assertEquals("\"Frame\"", Json.write(ForwardType.FRAME));
        assertEquals(IpVersion.V6, read("\"V6\"", IpVersion.class));
        assertEquals("v6", read("\"V6\"", IpVersion.class).value());
    }

    @Test
    void contactTypesKnowAllFourRoles() {
        assertEquals(List.of("Registrant", "Admin", "Tech", "Billing"),
                ContactType.ALL.stream().map(ContactType::value).collect(java.util.stream.Collectors.toList()));
        assertEquals(ContactType.ADMIN, ContactType.of("admin"));
        assertFalse(ContactType.of("Owner").isKnown());
    }

    @Test
    void codedEnumsAreWrittenAsNumbersAndReadFromNumbersOrNames() throws JsonProcessingException {
        assertEquals("[1,99]", Json.write(List.of(DocumentType.IDENTITY_CARD, DocumentType.OTHER)));
        assertEquals(DomainStatus.TRANSFERRED_OUT, read("4", DomainStatus.class));
        assertEquals(DomainStatus.TRANSFERRED_OUT, read("\"4\"", DomainStatus.class));
        assertEquals(DomainStatus.WAITING_FOR_REGISTRATION, read("\"WaitingForRegistration\"", DomainStatus.class));
        assertEquals(TransactionType.INTERNAL_TRANSFER, read("\"internal_transfer\"", TransactionType.class));
    }

    @Test
    void unknownEnumNumbersBecomeUnknown() throws JsonProcessingException {
        assertEquals(DomainStatus.UNKNOWN, read("42", DomainStatus.class));
        assertEquals(-1, DomainStatus.UNKNOWN.code());
    }

    @Test
    void readsLooselyTypedAnswers() throws JsonProcessingException {
        Domain domain = read("""
                {"domainName":"example.com","isIdnName":"1","status":"0","remainingDay":"42",
                 "expirationDate":"2027-01-02T03:04:05","startDate":"2020-01-01T00:00:00+03:00",
                 "transferDate":"0001-01-01T00:00:00","updatedDate":"",
                 "lockStatus":"yes","privacyProtectionStatus":null,"nameservers":null,
                 "contacts":{"Registrant":"DNA-1","Admin":null,"Extra":7}}
                """, Domain.class);

        assertTrue(domain.isIdn());
        assertEquals(DomainStatus.ACTIVE, domain.status());
        assertEquals(42, domain.remainingDays());
        assertEquals(OffsetDateTime.parse("2027-01-02T03:04:05Z"), domain.expirationDate());
        assertEquals(OffsetDateTime.parse("2020-01-01T00:00:00+03:00"), domain.startDate());
        assertNull(domain.transferDate(), "the year-1 placeholder means no date");
        assertNull(domain.updatedDate());
        assertTrue(domain.isLocked());
        assertFalse(domain.isPrivacyProtected());
        assertEquals(List.of(), domain.nameServers());
        assertEquals(Map.of("Registrant", "DNA-1", "Admin", "", "Extra", "7"), domain.contacts());
    }

    @Test
    void missingPropertiesGetDefaults() throws JsonProcessingException {
        Domain domain = read("{}", Domain.class);

        assertNull(domain.domainName());
        assertFalse(domain.isLocked());
        assertEquals(0, domain.remainingDays());
        assertEquals(List.of(), domain.nameServers());
        assertEquals(Map.of(), domain.contacts());
    }

    @Test
    void attributesAcceptAnEmptyArray() throws JsonProcessingException {
        Domain domain = read("{\"contacts\":[]}", Domain.class);
        assertEquals(Map.of(), domain.contacts());
    }

    @ParameterizedTest
    @CsvSource({"192.0.2.10, v4", "' 2001:db8::10 ', v6", "::1, v6"})
    void detectsIpVersions(String address, String version) {
        HostIpAddress parsed = HostIpAddress.parse(address);
        assertEquals(address.trim(), parsed.ipAddress());
        assertEquals(IpVersion.of(version), parsed.ipVersion());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ns1.example.com", "256.1.1.1", "1.2.3", "", "2001:db8::zz", "localhost"})
    void rejectsInvalidIpAddresses(String address) {
        assertThrows(IllegalArgumentException.class, () -> HostIpAddress.parse(address));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345 13 2 49FD46E6C4B45C55D4AC69CBD3CD34AC1AFE51DE",
            "example.com. 3600 IN DS 12345 13 2 49FD46E6C4B45C55 D4AC69CBD3CD34AC1AFE51DE",
    })
    void parsesDsRecords(String text) {
        DsRecordData record = DsRecordData.parse(text);

        assertEquals(12345, record.keyTag());
        assertEquals(DnssecAlgorithm.ECDSA_P256_SHA256, record.algorithm());
        assertEquals(DsDigestType.SHA256, record.digestType());
        assertEquals("49FD46E6C4B45C55D4AC69CBD3CD34AC1AFE51DE", record.digest());
    }

    @Test
    void rejectsInvalidDsRecords() {
        assertThrows(IllegalArgumentException.class, () -> DsRecordData.parse("12345 x 2 ABCD"));
        assertThrows(IllegalArgumentException.class, () -> DsRecordData.parse("12345 13 2"));
    }

    @Test
    void dsRecordsAreWrittenWithNumbers() {
        assertEquals("{\"keyTag\":1,\"algorithm\":13,\"digestType\":2,\"digest\":\"AB\"}",
                Json.write(new DsRecordData(1, DnssecAlgorithm.ECDSA_P256_SHA256, DsDigestType.SHA256, "AB")));
    }

    @Test
    void queryParametersSkipNullsAndRepeatLists() {
        QueryParameters query = new QueryParameters()
                .add("A", "x y&z")
                .add("B", (String) null)
                .add("C", "")
                .add("D", 5)
                .add("E", Boolean.TRUE)
                .add("F", new BigDecimal("1E+2"))
                .add("G", DomainStatus.DELETED)
                .add("H", UUID.fromString("7d9f1a52-3c1e-4c9e-9a51-6b8c2f3e4d10"))
                .add("I", OffsetDateTime.parse("2026-11-01T00:00:00Z"))
                .addEach("J", List.of("USD", "TRY"))
                .addCommaSeparated("K", List.of(DomainStatus.ACTIVE, DomainStatus.PENDING_DELETE))
                .addCommaSeparated("L", List.of());

        assertEquals("A=x%20y%26z&D=5&E=true&F=100&G=8&H=7d9f1a52-3c1e-4c9e-9a51-6b8c2f3e4d10"
                + "&I=2026-11-01T00%3A00%3A00Z&J=USD&J=TRY&K=0%2C7", query.toString());
        assertEquals(List.of("A", "D", "E", "F", "G", "H", "I", "J", "J", "K"), query.names());
        assertEquals("USD", query.get("J"));
    }

    @Test
    void jsonBodiesLeaveOutNulls() {
        assertEquals("{\"a\":1,\"c\":[\"x\"]}", Json.write(JsonBody.create().add("a", 1).add("b", null).add("c", List.of("x"))));
    }

    @Test
    void documentUploadsAreBase64() {
        DocumentUpload upload = DocumentUpload.fromBytes(DocumentType.PASSPORT, "id.pdf", "hello".getBytes(StandardCharsets.UTF_8));

        assertEquals(Base64.getEncoder().encodeToString("hello".getBytes(StandardCharsets.UTF_8)), upload.fileContent());
        assertEquals("{\"documentType\":2,\"fileName\":\"id.pdf\",\"fileContent\":\"aGVsbG8=\"}", Json.write(upload));
    }

    @Test
    void fullChainJoinsTheCertificateAndItsIntermediates() {
        CertificateContent content = new CertificateContent(null, "CERT\n", " INT1 ", null, "ROOT");
        assertEquals("CERT\nINT1\n", content.fullChain());
    }

    @Test
    void contactValidationNamesEveryMissingField() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new ContactInfo().firstName("Ada").email(" ").ensureComplete("contact"));

        assertEquals("contact is missing required fields: lastName, email, address, city, state, postalCode, country, "
                + "phoneCountryCode, phone.", e.getMessage());
    }

    @Test
    void contactsAreWrittenWithTheApiNames() {
        String json = Json.write(new ContactInfo().firstName("Ada").email("ada@example.com"));
        assertEquals("{\"firstName\":\"Ada\",\"eMail\":\"ada@example.com\"}", json);
    }
}
