package io.github.domainreseller.dna;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.domainreseller.dna.accounts.ChargeType;
import io.github.domainreseller.dna.accounts.GetAccountRequest;
import io.github.domainreseller.dna.accounts.GetProfileRequest;
import io.github.domainreseller.dna.accounts.ListTransactionsRequest;
import io.github.domainreseller.dna.accounts.TransactionType;
import io.github.domainreseller.dna.contacts.ContactHandle;
import io.github.domainreseller.dna.contacts.ContactInfo;
import io.github.domainreseller.dna.contacts.CreateContactRequest;
import io.github.domainreseller.dna.contacts.DomainContact;
import io.github.domainreseller.dna.contacts.GetContactRequest;
import io.github.domainreseller.dna.contacts.GetContactVerificationRequest;
import io.github.domainreseller.dna.contacts.ResendContactVerificationRequest;
import io.github.domainreseller.dna.contacts.UpdateContactRequest;
import io.github.domainreseller.dna.dns.AddDnsRecordRequest;
import io.github.domainreseller.dna.dns.ApplyDnsZoneRequest;
import io.github.domainreseller.dna.dns.DeleteDnsRecordRequest;
import io.github.domainreseller.dna.dns.ListDnsRecordsRequest;
import io.github.domainreseller.dna.dns.RecordType;
import io.github.domainreseller.dna.dns.ReplaceDnsRecordSetRequest;
import io.github.domainreseller.dna.dnssec.AddDsRecordsRequest;
import io.github.domainreseller.dna.dnssec.DisableDnssecRequest;
import io.github.domainreseller.dna.dnssec.DsRecordData;
import io.github.domainreseller.dna.dnssec.GetDnssecRequest;
import io.github.domainreseller.dna.dnssec.RemoveDsRecordRequest;
import io.github.domainreseller.dna.domains.AddChildNameServerRequest;
import io.github.domainreseller.dna.domains.BulkSearchDomainsRequest;
import io.github.domainreseller.dna.domains.CheckRenewalRequest;
import io.github.domainreseller.dna.domains.CheckTransferRequest;
import io.github.domainreseller.dna.domains.DeleteChildNameServerRequest;
import io.github.domainreseller.dna.domains.DeleteForwardingRequest;
import io.github.domainreseller.dna.domains.DnssecMode;
import io.github.domainreseller.dna.domains.DocumentType;
import io.github.domainreseller.dna.domains.DocumentUpload;
import io.github.domainreseller.dna.domains.DomainStatus;
import io.github.domainreseller.dna.domains.ForwardType;
import io.github.domainreseller.dna.domains.GetDomainRequest;
import io.github.domainreseller.dna.domains.GetForwardingRequest;
import io.github.domainreseller.dna.domains.GetTransferStatusRequest;
import io.github.domainreseller.dna.domains.HostIpAddress;
import io.github.domainreseller.dna.domains.ListDocumentsRequest;
import io.github.domainreseller.dna.domains.ListDomainsRequest;
import io.github.domainreseller.dna.domains.LockDomainRequest;
import io.github.domainreseller.dna.domains.RegisterDomainRequest;
import io.github.domainreseller.dna.domains.RegisterDomainWithHandlesRequest;
import io.github.domainreseller.dna.domains.RenewDomainRequest;
import io.github.domainreseller.dna.domains.RestoreDomainRequest;
import io.github.domainreseller.dna.domains.SearchDomainRequest;
import io.github.domainreseller.dna.domains.SetDomainContactHandlesRequest;
import io.github.domainreseller.dna.domains.SetForwardingRequest;
import io.github.domainreseller.dna.domains.SetNameServersRequest;
import io.github.domainreseller.dna.domains.SetPrivacyProtectionRequest;
import io.github.domainreseller.dna.domains.TransferDomainRequest;
import io.github.domainreseller.dna.domains.UnlockDomainRequest;
import io.github.domainreseller.dna.domains.UpdateAuthCodeRequest;
import io.github.domainreseller.dna.domains.UpdateChildNameServerRequest;
import io.github.domainreseller.dna.domains.UpdateDomainContactsRequest;
import io.github.domainreseller.dna.domains.UploadDocumentsRequest;
import io.github.domainreseller.dna.hosting.CreateHostingRequest;
import io.github.domainreseller.dna.hosting.GetHostingByTokenRequest;
import io.github.domainreseller.dna.hosting.GetHostingRequest;
import io.github.domainreseller.dna.hosting.GetHostingStatusRequest;
import io.github.domainreseller.dna.hosting.ListHostingPackagesRequest;
import io.github.domainreseller.dna.hosting.ListHostingPlansRequest;
import io.github.domainreseller.dna.internal.Json;
import io.github.domainreseller.dna.orders.GetOrderRequest;
import io.github.domainreseller.dna.products.GetProductPriceRequest;
import io.github.domainreseller.dna.products.ListProductsRequest;
import io.github.domainreseller.dna.products.ListSslProductsRequest;
import io.github.domainreseller.dna.products.ListTldsRequest;
import io.github.domainreseller.dna.ssl.CertificateContact;
import io.github.domainreseller.dna.ssl.ConfigureCertificateRequest;
import io.github.domainreseller.dna.ssl.DecodeCsrRequest;
import io.github.domainreseller.dna.ssl.GenerateCsrRequest;
import io.github.domainreseller.dna.ssl.GetApproverEmailsRequest;
import io.github.domainreseller.dna.ssl.GetCertificateContentRequest;
import io.github.domainreseller.dna.ssl.GetCertificateRequest;
import io.github.domainreseller.dna.ssl.ListCertificatesRequest;
import io.github.domainreseller.dna.ssl.PurchaseCertificateRequest;
import io.github.domainreseller.dna.ssl.RefreshCertificateStatusRequest;
import io.github.domainreseller.dna.ssl.ReissueCertificateRequest;
import io.github.domainreseller.dna.ssl.RenewCertificateRequest;
import io.github.domainreseller.dna.ssl.RevokeCertificateRequest;
import io.github.domainreseller.dna.ssl.ValidationMethod;
import io.github.domainreseller.dna.ssl.WebServerType;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Keeps the SDK in step with the API: every operation in the OpenAPI document ({@code src/test/resources/openapi.json},
 * the same document the {@code dna} CLI embeds) has exactly one request class, with the CLI's safety class, and every
 * request sends only query parameters and body properties the document describes. When the API adds an operation,
 * this test fails until a request class exists.
 */
class SpecCoverageTest {
    private static final UUID CERTIFICATE_ID = UUID.fromString("7d9f1a52-3c1e-4c9e-9a51-6b8c2f3e4d10");
    private static final ContactInfo CONTACT = new ContactInfo()
            .firstName("Ada").lastName("Lovelace").companyName("Example Ltd").email("ada@example.com")
            .address("1 Example Street").city("Istanbul").state("Istanbul").postalCode("34000").country("TR")
            .phoneCountryCode("90").phone("5551234567").faxCountryCode("90").fax("5551234568").discloseFlag(true);
    private static final CertificateContact CERTIFICATE_CONTACT = new CertificateContact()
            .title("CTO").firstName("Ada").lastName("Lovelace").companyName("Example Ltd").email("ada@example.com")
            .address("1 Example Street").city("Istanbul").state("Istanbul").postalCode("34000").country("TR")
            .phoneCountryCode("90").phone("5551234567").faxCountryCode("90").fax("5551234568");
    private static final DsRecordData DS = DsRecordData.parse("12345 13 2 49FD46E6C4B45C55D4AC69CBD3CD34AC1AFE51DE");

    /** A request of every operation, every optional value set, with the safety class the dna CLI assigns to it. */
    private static final List<Map.Entry<BaseRequest<?>, OperationSafety>> REQUESTS = List.of(
            entry(new GetAccountRequest(), OperationSafety.READ),
            entry(new GetProfileRequest(), OperationSafety.READ),
            entry(new ListTransactionsRequest()
                    .transactionTypes(TransactionType.REGISTER, TransactionType.RENEW)
                    .productTypes(ProductType.DOMAIN).currencies("USD").chargeType(ChargeType.SUBTRACT)
                    .traceId("trace").description("example.com").amount(new BigDecimal("12.50"))
                    .startDate(OffsetDateTime.parse("2026-11-01T00:00:00Z")).endDate(OffsetDateTime.parse("2026-11-30T00:00:00Z"))
                    .filterText("x").sorting("creationTime desc").skipCount(0).maxResultCount(10), OperationSafety.READ),

            entry(new GetContactRequest("DNA-1"), OperationSafety.READ),
            entry(new CreateContactRequest(CONTACT), OperationSafety.WRITE),
            entry(new UpdateContactRequest("DNA-1", CONTACT), OperationSafety.WRITE),
            entry(new GetContactVerificationRequest("DNA-1"), OperationSafety.READ),
            entry(new ResendContactVerificationRequest("DNA-1"), OperationSafety.WRITE),

            entry(new SearchDomainRequest("example.com", "USD"), OperationSafety.READ),
            entry(new BulkSearchDomainsRequest(List.of("example.com", "example.net"), "USD"), OperationSafety.READ),
            entry(new ListDomainsRequest().domainName("example").statuses(DomainStatus.ACTIVE, DomainStatus.PENDING_DELETE)
                    .sorting("expirationDate desc").skipCount(10).maxResultCount(10), OperationSafety.READ),
            entry(new GetDomainRequest("example.com"), OperationSafety.READ),
            entry(new RegisterDomainRequest("example.com").period(2).nameServers("ns1.example.net", "ns2.example.net")
                    .contacts(DomainContact.forAllRoles(CONTACT)).tldAttributes(Map.of("key", "value"))
                    .useTrusteeContact(true), OperationSafety.BILLABLE),
            entry(new RegisterDomainWithHandlesRequest("example.com").period(2).nameServers("ns1.example.net", "ns2.example.net")
                    .handles(ContactHandle.forAllRoles("DNA-1")).tldAttributes(Map.of("key", "value")), OperationSafety.BILLABLE),
            entry(new RenewDomainRequest("example.com", 2), OperationSafety.BILLABLE),
            entry(new CheckRenewalRequest("example.com", 2), OperationSafety.READ),
            entry(new TransferDomainRequest("example.com", "code").period(1).contacts(List.of(CONTACT)), OperationSafety.BILLABLE),
            entry(new CheckTransferRequest("example.com", "code"), OperationSafety.READ),
            entry(new GetTransferStatusRequest("example.com"), OperationSafety.READ),
            entry(new LockDomainRequest("example.com"), OperationSafety.WRITE),
            entry(new UnlockDomainRequest("example.com"), OperationSafety.WRITE),
            entry(new SetPrivacyProtectionRequest("example.com", true), OperationSafety.WRITE),
            entry(new RestoreDomainRequest("example.com"), OperationSafety.BILLABLE),
            entry(new UpdateAuthCodeRequest("example.com"), OperationSafety.WRITE),
            entry(new UpdateDomainContactsRequest("example.com", DomainContact.forAllRoles(CONTACT)), OperationSafety.WRITE),
            entry(new SetDomainContactHandlesRequest("example.com", ContactHandle.forAllRoles("DNA-1")), OperationSafety.WRITE),
            entry(new SetNameServersRequest("example.com", List.of("ns1.example.net")).dnssecMode(DnssecMode.DISABLE_DNSSEC),
                    OperationSafety.WRITE),
            entry(new GetForwardingRequest("example.com"), OperationSafety.READ),
            entry(new SetForwardingRequest("example.com", "https://example.net", ForwardType.FRAME), OperationSafety.WRITE),
            entry(new DeleteForwardingRequest("example.com"), OperationSafety.DESTRUCTIVE),
            entry(new AddChildNameServerRequest("example.com", "ns1.example.com", List.of(HostIpAddress.parse("192.0.2.1"))),
                    OperationSafety.WRITE),
            entry(new UpdateChildNameServerRequest("example.com", "ns1.example.com", List.of(HostIpAddress.parse("2001:db8::1")))
                    .newHostName("ns2.example.com"), OperationSafety.WRITE),
            entry(new DeleteChildNameServerRequest("example.com", "ns1.example.com"), OperationSafety.DESTRUCTIVE),
            entry(new ListDocumentsRequest("example.com.tr").documentTypes(DocumentType.PASSPORT).sorting("name")
                    .skipCount(0).maxResultCount(5), OperationSafety.READ),
            entry(new UploadDocumentsRequest("example.com.tr",
                    List.of(DocumentUpload.fromBytes(DocumentType.PASSPORT, "id.pdf", new byte[] {1, 2}))), OperationSafety.WRITE),

            entry(new ListDnsRecordsRequest("example.com"), OperationSafety.READ),
            entry(new AddDnsRecordRequest("example.com", "www", RecordType.A, List.of("192.0.2.1")).ttl(300), OperationSafety.WRITE),
            entry(new ReplaceDnsRecordSetRequest("example.com", "www", RecordType.A, List.of("192.0.2.1")).newName("web"),
                    OperationSafety.WRITE),
            entry(new DeleteDnsRecordRequest("example.com", "www", RecordType.A, "192.0.2.1"), OperationSafety.DESTRUCTIVE),
            entry(new ApplyDnsZoneRequest("example.com"), OperationSafety.WRITE),

            entry(new GetDnssecRequest("example.com"), OperationSafety.READ),
            entry(new AddDsRecordsRequest("example.com", List.of(DS)), OperationSafety.WRITE),
            entry(new RemoveDsRecordRequest("example.com", DS), OperationSafety.DESTRUCTIVE),
            entry(new DisableDnssecRequest("example.com"), OperationSafety.DESTRUCTIVE),

            entry(new GenerateCsrRequest("example.com").country("TR").state("Istanbul").locality("Istanbul")
                    .organization("Example").organizationUnit("IT").alternativeNames("www.example.com"), OperationSafety.READ),
            entry(new DecodeCsrRequest("csr"), OperationSafety.READ),
            entry(new PurchaseCertificateRequest("example.com", "PKG"), OperationSafety.BILLABLE),
            entry(new RenewCertificateRequest(CERTIFICATE_ID), OperationSafety.BILLABLE),
            entry(new ListCertificatesRequest().domainName("example.com").skipCount(0).maxResultCount(10), OperationSafety.READ),
            entry(new ConfigureCertificateRequest(CERTIFICATE_ID, "csr", CERTIFICATE_CONTACT).privateKey("key")
                    .validationMethod(ValidationMethod.EMAIL).approverEmail("admin@example.com")
                    .webServerType(WebServerType.APACHE).sanList("www.example.com").wildcardList("*.example.com"),
                    OperationSafety.WRITE),
            entry(new RefreshCertificateStatusRequest(CERTIFICATE_ID), OperationSafety.READ),
            entry(new GetCertificateRequest(CERTIFICATE_ID), OperationSafety.READ),
            entry(new GetCertificateContentRequest(CERTIFICATE_ID), OperationSafety.READ),
            entry(new ReissueCertificateRequest(CERTIFICATE_ID).csr("csr").privateKey("key").approverEmail("admin@example.com"),
                    OperationSafety.WRITE),
            entry(new GetApproverEmailsRequest(CERTIFICATE_ID), OperationSafety.READ),
            entry(new RevokeCertificateRequest(CERTIFICATE_ID), OperationSafety.DESTRUCTIVE),

            entry(new ListHostingPackagesRequest(), OperationSafety.READ),
            entry(new CreateHostingRequest("example.com", "plan"), OperationSafety.BILLABLE),
            entry(GetHostingRequest.byDomain("example.com"), OperationSafety.READ),
            entry(new GetHostingByTokenRequest("token"), OperationSafety.READ),
            entry(GetHostingStatusRequest.byServiceId("svc-1"), OperationSafety.READ),
            entry(new ListHostingPlansRequest(UUID.randomUUID()), OperationSafety.READ),

            entry(new GetOrderRequest("ORD-1"), OperationSafety.READ),

            entry(new ListProductsRequest().filterText("com").productType(ProductType.DOMAIN).skipCount(0).maxResultCount(10),
                    OperationSafety.READ),
            entry(new GetProductPriceRequest("com", OrderType.REGISTER, 1), OperationSafety.READ),
            entry(new ListTldsRequest().filterText("com").currency("USD").skipCount(0).maxResultCount(10), OperationSafety.READ),
            entry(new ListSslProductsRequest().filterText("x").currency("USD").validationType(1).isWildcard(true)
                    .isMultiDomain(false).vendorName("Sectigo").skipCount(0).maxResultCount(10), OperationSafety.READ));

    /** Requests with other shapes of the same operation: other constructors and lookups. */
    private static final List<BaseRequest<?>> VARIANTS = List.of(
            new GetApproverEmailsRequest("example.com", "PKG"),
            GetHostingRequest.byServiceId("svc-1"),
            GetHostingStatusRequest.byDomain("example.com"),
            new ListHostingPlansRequest(),
            new SetNameServersRequest("example.com", List.of("ns1.example.net")));

    private static final JsonNode SPEC = readSpec();

    private static Map.Entry<BaseRequest<?>, OperationSafety> entry(BaseRequest<?> request, OperationSafety safety) {
        return Map.entry(request, safety);
    }

    @Test
    void everyApiOperationHasExactlyOneRequest() {
        Set<String> spec = specOperations();
        List<String> covered = REQUESTS.stream().map(e -> key(e.getKey())).collect(Collectors.toList());

        Set<String> missing = new TreeSet<>(spec);
        missing.removeAll(covered);
        assertTrue(missing.isEmpty(), "operations without a request: " + missing);

        Set<String> unknown = new TreeSet<>(covered);
        unknown.removeAll(spec);
        assertTrue(unknown.isEmpty(), "requests for operations the API does not have: " + unknown);

        assertEquals(covered.size(), new HashSet<>(covered).size(), "an operation has two requests");
        assertEquals(67, spec.size());
    }

    @Test
    void everyRequestClassIsInTheTable() throws IOException, URISyntaxException {
        Set<String> requestClasses = new TreeSet<>();
        Path root = Paths.get(BaseRequest.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        try (Stream<Path> files = Files.walk(root)) {
            for (Path file : (Iterable<Path>) files.filter(f -> f.toString().endsWith(".class"))::iterator) {
                String name = root.relativize(file).toString().replace('/', '.').replace('\\', '.').replaceAll("\\.class$", "");
                Class<?> type;
                try {
                    type = Class.forName(name, false, BaseRequest.class.getClassLoader());
                } catch (ClassNotFoundException e) {
                    throw new AssertionError(e);
                }
                if (BaseRequest.class.isAssignableFrom(type) && !Modifier.isAbstract(type.getModifiers())) {
                    requestClasses.add(type.getName());
                }
            }
        }
        Set<String> table = REQUESTS.stream().map(e -> e.getKey().getClass().getName()).collect(Collectors.toCollection(TreeSet::new));

        assertEquals(requestClasses, table);
    }

    @Test
    void safetyMatchesTheCli() {
        for (Map.Entry<BaseRequest<?>, OperationSafety> e : REQUESTS) {
            assertEquals(e.getValue(), e.getKey().safety(), e.getKey() + " has the wrong safety class");
        }
    }

    @Test
    void readsAreRetriedAndBillableRequestsNever() {
        for (Map.Entry<BaseRequest<?>, OperationSafety> e : REQUESTS) {
            if (e.getValue() == OperationSafety.READ) {
                assertTrue(e.getKey().isIdempotent(), e.getKey() + " is a read and should be retried");
            }
            if (e.getValue() == OperationSafety.BILLABLE) {
                assertFalse(e.getKey().isIdempotent(), e.getKey() + " charges money and must never be retried");
            }
        }
    }

    @Test
    void everyRequestIsValidAndHasAnAnswerType() {
        for (Map.Entry<BaseRequest<?>, OperationSafety> e : REQUESTS) {
            e.getKey().validate();
            assertNotNull(Json.responseType(e.getKey().getClass(), BaseRequest.class, e.getKey().responseType()));
        }
        VARIANTS.forEach(BaseRequest::validate);
    }

    @Test
    void requestsSendOnlyWhatTheApiDocumentDescribes() {
        List<BaseRequest<?>> all = new ArrayList<>();
        REQUESTS.forEach(e -> all.add(e.getKey()));
        all.addAll(VARIANTS);
        for (BaseRequest<?> request : all) {
            JsonNode operation = operation(request);

            Set<String> documented = new HashSet<>();
            Set<String> required = new HashSet<>();
            for (JsonNode parameter : operation.path("parameters")) {
                if (parameter.path("in").asText().equals("query")) {
                    documented.add(parameter.path("name").asText());
                    if (parameter.path("required").asBoolean()) {
                        required.add(parameter.path("name").asText());
                    }
                }
            }
            QueryParameters query = new QueryParameters();
            request.addQueryParameters(query);
            Set<String> sent = new HashSet<>(query.names());
            Set<String> extra = new TreeSet<>(sent);
            extra.removeAll(documented);
            assertTrue(extra.isEmpty(), request + " sends query parameters the API does not have: " + extra);
            Set<String> missing = new TreeSet<>(required);
            missing.removeAll(sent);
            assertTrue(missing.isEmpty(), request + " does not send the required query parameters " + missing);

            Object body = request.body();
            if (!operation.has("requestBody")) {
                assertNull(body, request + " has no body in the API document");
                continue;
            }
            assertNotNull(body, request + " needs a body");
            JsonNode schema = operation.path("requestBody").path("content").elements().next().path("schema");
            checkValue(request + " body", new ObjectMapper().valueToTree(toTree(body)), schema);
        }
    }

    @Test
    void theBodyCheckCatchesPropertiesTheApiDoesNotHave() {
        JsonNode schema = operation(new LockDomainRequest("example.com")).path("requestBody").path("content")
                .elements().next().path("schema");
        JsonNode body = toTree(JsonBody.create().add("domainName", "example.com").add("bogus", 1));

        AssertionError error = org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class,
                () -> checkValue("lock", body, schema));
        assertTrue(error.getMessage().contains("bogus"));
    }

    @Test
    void enumsUseTheNumbersOfTheApiDocument() {
        // Spot checks of coded values that are easy to get wrong.
        assertEquals(4, DomainStatus.TRANSFERRED_OUT.code());
        assertEquals(19, DomainStatus.PENDING_RESTORE.code());
        assertEquals(2, DnssecMode.DISABLE_DNSSEC.code());
        assertEquals(99, DocumentType.OTHER.code());
        assertEquals(12, OrderType.OWNER_CHANGE.code());
        assertEquals(6, ProductType.OWNER_CHANGE.code());
    }

    private static JsonNode toTree(Object body) {
        try {
            return Json.MAPPER.readTree(Json.write(body));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /** Compares a JSON value with a schema of the API document, recursively. */
    private static void checkValue(String where, JsonNode value, JsonNode schema) {
        schema = resolve(schema);
        if (schema.path("type").asText().equals("array") || schema.has("items")) {
            assertTrue(value.isArray(), where + " should be an array");
            for (int i = 0; i < value.size(); i++) {
                checkValue(where + "[" + i + "]", value.get(i), schema.path("items"));
            }
            return;
        }
        JsonNode properties = schema.path("properties");
        if (!value.isObject() || properties.isMissingNode()) {
            return; // a scalar or a free-form map
        }
        Map<String, JsonNode> documented = new TreeMap<>();
        properties.properties().forEach(p -> documented.put(p.getKey(), p.getValue()));
        for (Map.Entry<String, JsonNode> field : value.properties()) {
            JsonNode propertySchema = documented.get(field.getKey());
            if (propertySchema == null) {
                fail(where + " sends '" + field.getKey() + "', which the API document does not have. Expected one of "
                        + documented.keySet());
            }
            checkValue(where + "." + field.getKey(), field.getValue(), propertySchema);
        }
        for (JsonNode name : schema.path("required")) {
            assertTrue(value.has(name.asText()), where + " is missing the required property '" + name.asText() + "'");
        }
    }

    private static JsonNode resolve(JsonNode schema) {
        if (schema.has("$ref")) {
            String name = schema.path("$ref").asText().substring("#/components/schemas/".length());
            return SPEC.path("components").path("schemas").path(name);
        }
        if (schema.has("allOf") && schema.path("allOf").size() == 1) {
            return resolve(schema.path("allOf").get(0));
        }
        return schema;
    }

    private static JsonNode operation(BaseRequest<?> request) {
        String path = "/api/v1/" + request.path();
        JsonNode node = SPEC.path("paths").path(path);
        if (node.isMissingNode()) {
            node = SPEC.path("paths").path(path.replaceAll("^/api/v1/(contacts|order)/[^/]+$", "/api/v1/$1/{x}")
                    .replace("{x}", request.path().startsWith("contacts") ? "{handleCode}" : "{orderCode}"));
        }
        JsonNode operation = node.path(request.method().name().toLowerCase(Locale.ROOT));
        assertFalse(operation.isMissingNode(), request + " is not in the API document");
        return operation;
    }

    private static String key(BaseRequest<?> request) {
        // contacts/DNA-1 -> contacts/{}, order/ORD-1 -> order/{}
        String path = request.path().replaceAll("^(contacts|order)/(?!verification).+$", "$1/{}");
        return request.method() + " " + path;
    }

    private static Set<String> specOperations() {
        Set<String> result = new TreeSet<>();
        SPEC.path("paths").properties().forEach(path -> path.getValue().properties().forEach(operation -> {
            String method = operation.getKey();
            if (List.of("get", "post", "put", "delete").contains(method)) {
                String relative = path.getKey().substring("/api/v1/".length()).replaceAll("\\{[^}]+}", "{}");
                result.add(method.toUpperCase(Locale.ROOT) + " " + relative);
            }
        }));
        return result;
    }

    private static JsonNode readSpec() {
        try (InputStream in = SpecCoverageTest.class.getResourceAsStream("/openapi.json")) {
            return new ObjectMapper().readTree(in);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
