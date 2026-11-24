<div align="center">
  <a href="README-TR.md"   >   TR <img style="padding-top: 8px" src="https://raw.githubusercontent.com/yammadev/flag-icons/master/png/TR.png" alt="TR" height="20" /></a>
  <a href="README.md"> | EN <img style="padding-top: 8px" src="https://raw.githubusercontent.com/yammadev/flag-icons/master/png/US.png" alt="EN" height="20" /></a>
   <a href="README-DE.md"> | DE <img style="padding-top: 8px" src="https://raw.githubusercontent.com/yammadev/flag-icons/master/png/DE.png" alt="DE" height="20" /></a>
  <a href="README-SA.md"> | SA <img style="padding-top: 8px" src="https://raw.githubusercontent.com/yammadev/flag-icons/master/png/SA.png" alt="AR" height="20" /></a>
  <a href="README-NL.md"> | NL <img style="padding-top: 8px" src="https://raw.githubusercontent.com/yammadev/flag-icons/master/png/NL.png" alt="NL" height="20" /></a>
  <a href="README-AZ.md"> | AZ <img style="padding-top: 8px" src="https://raw.githubusercontent.com/yammadev/flag-icons/master/png/AZ.png" alt="AZ" height="20" /></a>
  <a href="README-CN.md"> | CN <img style="padding-top: 8px" src="https://raw.githubusercontent.com/yammadev/flag-icons/master/png/CN.png" alt="CN" height="20" /></a>
  <a href="README-FR.md"> | FR <img style="padding-top: 8px" src="https://raw.githubusercontent.com/yammadev/flag-icons/master/png/FR.png" alt="FR" height="20" /></a>
  <a href="README-IT.md"> | IT <img style="padding-top: 8px" src="https://raw.githubusercontent.com/yammadev/flag-icons/master/png/IT.png" alt="IT" height="20" /></a>
</div>

# DomainNameAPI Java SDK (java-dna)

<p>
  <a href="https://github.com/domainreseller/java-dna/actions/workflows/ci.yml"><img src="https://img.shields.io/github/actions/workflow/status/domainreseller/java-dna/ci.yml?branch=main&label=CI&style=flat-square" alt="CI"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-Apache--2.0-0B8DB5?style=flat-square" alt="Apache-2.0"></a>
  <img src="https://img.shields.io/badge/Java-17%2B-0B8DB5?style=flat-square" alt="Java 17+">
</p>

The official Java SDK for the [Domain Name API](https://www.domainnameapi.com) reseller platform. It covers all
67 operations of the REST API with typed clients and models: domains, contacts, DNS, DNSSEC, SSL certificates,
hosting, orders, products and your account.

```java
DomainNameApiClient client = DomainNameApiClient.create(resellerId, apiKey, ApiEnvironment.OTE);

DomainSearchResult search = client.domains().search("example.com");
if (search.info() != null && search.info().isAvailable()) {
    client.domains().register("example.com", contact, List.of("tr.apiname.com", "eu.apiname.com"));
}
```

- **Typed everything.** Every operation is a request class and every answer a record with Javadoc. No maps, no JSON parsing.
- **Your own requests.** Every request extends `BaseRequest`. Subclass it for an endpoint the SDK does not cover yet.
- **Safe with money.** Every request declares whether it reads, writes, charges your balance or deletes something. Billable requests are never retried, and a read-only mode blocks every change.
- **Built-in resilience.** Rate limits are respected automatically, 429 answers are retried, and network errors are retried for reads.
- **Clear errors.** One unchecked exception with a stable kind, the API's error code, field-level validation errors, the correlation ID and a hint.
- **Modern Java.** Java 17+, records, lazy `Stream` paging, `CompletableFuture` variants, a thread-safe client on `java.net.http`.
- **Private.** No telemetry. Nothing leaves your process except the requests you send to the API.

It is the Java counterpart of [`dna`, the Domain Name API CLI](https://github.com/domainreseller/cli): the same API
document, the same safety classes, retry rules and error codes.

## Contents

- [Install](#install)
- [Credentials](#credentials)
- [Quick start](#quick-start)
- [Configuration](#configuration)
- [Usage](#usage): [account](#account) · [domains](#domains) · [contacts](#contacts) · [DNS](#dns) · [DNSSEC](#dnssec) · [SSL](#ssl) · [hosting](#hosting) · [orders and products](#orders-and-products)
- [Requests and `BaseRequest`](#requests-and-baserequest)
- [Errors](#errors)
- [Safety, retries and rate limits](#safety-retries-and-rate-limits)
- [Asynchronous calls](#asynchronous-calls)
- [Testing your code](#testing-your-code)
- [Project layout](#project-layout)
- [Development](#development)
- [Upgrading from 1.x](#upgrading-from-1x)

## Install

> [!IMPORTANT]
> **Always use the [latest release](https://github.com/domainreseller/java-dna/releases/latest).** Release packages are versioned and tested.
> The green **Code → Download ZIP** button downloads the development branch instead.

Coordinates: `io.github.domainreseller:domainnameapi:2.0.0`.

```xml
<dependency>
    <groupId>io.github.domainreseller</groupId>
    <artifactId>domainnameapi</artifactId>
    <version>2.0.0</version>
</dependency>
```

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.github.domainreseller:domainnameapi:2.0.0")
}
```

Until the artifact is available from a public repository, install it into your local Maven repository from the
release source (`mvn -DskipTests install`) and add `mavenLocal()` to your Gradle repositories.

**Requirements:** Java 17 or later. The only dependency is [Jackson](https://github.com/FasterXML/jackson-databind)
(`jackson-databind`) for JSON; HTTP goes through the JDK's `java.net.http.HttpClient`.

## Credentials

The API authenticates every request with two values, which the SDK sends for you:

| Value | Header | Where to find it |
|---|---|---|
| Reseller ID (a UUID) | `__reseller` | Reseller panel → **API settings** |
| API key | `X-API-KEY` | Reseller panel → **API settings** |

> [!NOTE]
> These are **API credentials**: your panel login does not work here. **Production and OTE (the test environment) use
> different API keys.** Using one with the other fails with `INVALID_API_KEY` or `INVALID_RESELLER`.

## Quick start

```java
import io.github.domainreseller.dna.ApiEnvironment;
import io.github.domainreseller.dna.DomainNameApiClient;
import io.github.domainreseller.dna.accounts.ResellerAccount;
import io.github.domainreseller.dna.contacts.ContactInfo;
import io.github.domainreseller.dna.domains.DomainOperationResult;
import io.github.domainreseller.dna.domains.DomainSearchResult;
import java.util.List;

// OTE is the test environment: real API, its own data, no real money.
DomainNameApiClient client = DomainNameApiClient.create(resellerId, apiKey, ApiEnvironment.OTE);

// 1. Check the credentials and the balance.
ResellerAccount account = client.account().get();
System.out.println(account.resellerName() + ": " + account.usdBalance() + " USD");

// 2. Is the domain available, and at what price?
DomainSearchResult search = client.domains().search("my-new-domain.com");
System.out.println(search.info().status() + " " + search.info().price() + " " + search.info().currency());

// 3. Register it with one contact for all four roles. This charges the balance.
ContactInfo contact = new ContactInfo()
        .firstName("Ada").lastName("Lovelace").email("ada@example.com")
        .address("1 Example Street").city("Istanbul").state("Istanbul").postalCode("34000")
        .country("TR").phoneCountryCode("90").phone("5551234567");
DomainOperationResult result = client.domains().register("my-new-domain.com", contact,
        List.of("tr.apiname.com", "eu.apiname.com"));
System.out.println(result.success() + " " + result.status() + ", expires " + result.expirationDate());

// 4. List every domain; pages are fetched as the stream is consumed.
client.domains().listAll().forEach(domain ->
        System.out.println(domain.domainName() + " expires in " + domain.remainingDays() + " days"));
```

The [examples](examples) folder has runnable programs for these scenarios. They read the `DNA_*` environment
variables, so point them at OTE:

```bash
export DNA_RESELLER_ID=... DNA_API_KEY=... DNA_ENV=ote
```

## Configuration

There are four ways to create a client:

```java
// Credentials and environment
DomainNameApiClient client = DomainNameApiClient.create(resellerId, apiKey, ApiEnvironment.PRODUCTION);

// All settings
DomainNameApiClient client = DomainNameApiClient.builder()
        .credentials(resellerId, apiKey)
        .environment(ApiEnvironment.OTE)
        .timeout(Duration.ofSeconds(30))
        .readOnly(true)
        .build();

// The same environment variables the dna CLI reads
DomainNameApiClient client = DomainNameApiClient.fromEnvironment();

// The environment variables, plus your own settings
DomainNameApiClient client = DomainNameApiClient.Builder.fromEnvironment()
        .httpClient(myHttpClient)        // for example with a proxy
        .build();
```

| Builder method | Default | Meaning |
|---|---|---|
| `credentials(resellerId, apiKey)` | (required) | The credentials. |
| `environment(...)` | `PRODUCTION` | `PRODUCTION` (`https://api.domainresellerapi.com`) or `OTE` (`https://ote.domainresellerapi.com`). |
| `baseUri(...)` | none | Overrides the environment's address, for example for a proxy. |
| `timeout(...)` | 60 s | Time limit of one attempt. |
| `maxRetries(...)` | 2 | How often a failed request is repeated (see [retries](#safety-retries-and-rate-limits)). 0 turns retries off. |
| `maxRetryDelay(...)` | 30 s | The longest wait before a retry. |
| `paceRequests(...)` | `true` | Spaces requests to stay under the gateway's rate limits. |
| `readOnly(...)` | `false` | Blocks every request that is not a read, before it is sent. |
| `userAgent(...)` | `DomainNameAPI-Java/<version>` | The `User-Agent` header. |
| `app(name, version)` | `java`, the SDK version | The application that uses the SDK, sent as `X-App` and `X-Version` (see [below](#identifying-your-application)). |
| `httpClient(...)` | the SDK's own | Your own `java.net.http.HttpClient` (proxy, TLS, executor). |
| `executor(...)` | a shared daemon pool | Where the `sendAsync` methods run. |

`fromEnvironment()` reads:

| Variable | Meaning |
|---|---|
| `DNA_RESELLER_ID`, `DNA_API_KEY` | The credentials. |
| `DNA_ENV` | `production` or `ote` (also `prod`, `live`, `test`, `sandbox`). |
| `DNA_TEST_MODE` | `true`, `1`, `on` or `yes` selects OTE. Used when `DNA_ENV` is not set. |
| `DNA_BASE_URL` | Optional base address override. |
| `DNA_READ_ONLY` | `true` turns on read-only mode. |
| `DNA_APP`, `DNA_APP_VERSION` | The application that uses the SDK and its version (`X-App`, `X-Version`). |

### Identifying your application

Every request carries two headers that tell the API which integration sent it: `X-App`, the application name in
lowercase with only `a-z`, `0-9`, `.`, `_` and `-` kept, and `X-Version`, its version with digits and dots only. By
default they are `java` and the SDK version. Name your panel or module with `app(name, version)` or
`DNA_APP`/`DNA_APP_VERSION`; an empty or invalid name falls back to `java`, an empty version to the SDK version. Nothing
else is sent, and the `User-Agent` stays the same.

```java
DomainNameApiClient client = DomainNameApiClient.builder()
        .credentials(resellerId, apiKey)
        .app("WiseCP", "V2.0.20-beta")   // sends X-App: wisecp, X-Version: 2.0.20
        .build();
```

The client is thread-safe. Create one per set of credentials and reuse it for the lifetime of the application.

## Usage

Each area of the API has its own client. Methods take simple arguments; for more options, every method also has an
overload (or a sibling) that takes the request object. Methods that charge your balance say so in their Javadoc.

### Account

```java
ResellerAccount account = client.account().get();          // resellerId, resellerName, tryBalance, usdBalance
ResellerProfile profile = client.account().getProfile();   // name, companyName, accountCurrency, status, ...

// This month's charges, across all pages
ListTransactionsRequest request = new ListTransactionsRequest()
        .chargeType(ChargeType.SUBTRACT)
        .startDate(OffsetDateTime.parse("2026-11-01T00:00:00Z"))
        .transactionTypes(TransactionType.REGISTER, TransactionType.RENEW);
client.account().listAllTransactions(request).forEach(t ->
        System.out.println(t.creationTime() + " " + t.type() + " " + t.amount() + " " + t.amountCurrency()));
```

### Domains

**Search**

```java
DomainSearchResult one = client.domains().search("example.com", "USD");
boolean available = one.info() != null && one.info().isAvailable();

BulkSearchResult many = client.domains().bulkSearch(List.of("example.com", "example.net", "example.com.tr"));
for (DomainAvailability domain : many.available()) {
    System.out.println(domain.domainName() + ": " + domain.price() + " " + domain.currency());
}
```

**List and details**

```java
Page<Domain> page = client.domains().list(new ListDomainsRequest().maxResultCount(50).statuses(DomainStatus.ACTIVE));

client.domains().listAll(new ListDomainsRequest().domainName("example"))
        .forEach(d -> System.out.println(d.domainName() + " " + d.status() + " " + d.expirationDate()));

DomainDetails details = client.domains().get("example.com");
System.out.println(details.authCode());
details.contact(ContactType.REGISTRANT).ifPresent(c -> System.out.println(c.email()));
```

**Register, renew, transfer, restore** (these charge your balance)

```java
// One contact for all four roles, two years
client.domains().register("example.com", contact, List.of("tr.apiname.com", "eu.apiname.com"), 2);

// Full control: different contacts per role, registry attributes, trustee service
client.domains().register(new RegisterDomainRequest("example.com.tr")
        .period(1)
        .nameServers("tr.apiname.com", "eu.apiname.com")
        .contacts(DomainContact.forAllRoles(contact))
        .tldAttributes(Map.of("<key>", "<value>")));   // the keys: client.products().getTld("com.tr").get().attributes()

// With existing contact handles
client.domains().registerWithHandle("example.com", "DNA-12345", List.of("tr.apiname.com", "eu.apiname.com"), 1);

RenewalCheck renewal = client.domains().checkRenewal("example.com");   // free
if (renewal.isRenewable()) {
    client.domains().renew("example.com", 1);
}

TransferCheck check = client.domains().checkTransfer("example.com", "EPP-CODE");   // free
if (check.isTransferable()) {
    client.domains().transfer("example.com", "EPP-CODE");
}

client.domains().restore("example.com");
```

Requests are checked before they are sent. A registration without all four contact roles, a contact without an e-mail
address or a period of 0 years throws an `IllegalArgumentException`, and nothing reaches the API.

> [!NOTE]
> The API can answer a registration, renewal or transfer with HTTP 200 and `"success": false` (for example when the
> registry refuses the domain). The SDK returns that answer as it is: check `DomainOperationResult.success()` and
> `message()`.

**Settings**

```java
client.domains().lock("example.com");
client.domains().unlock("example.com");
client.domains().setPrivacyProtection("example.com", true);
client.domains().setNameServers("example.com", List.of("ns1.example.net", "ns2.example.net"));
client.domains().setNameServers("example.com", List.of("ns1.example.net"), DnssecMode.DISABLE_DNSSEC);  // a signed domain
DomainAuthCode authCode = client.domains().updateAuthCode("example.com");

client.domains().updateContacts("example.com", DomainContact.forAllRoles(contact));
client.domains().setContactHandles("example.com", List.of(new ContactHandle(ContactType.TECH, "DNA-12345")));
```

**URL forwarding, child nameservers and documents**

```java
client.domains().forwarding().set("example.com", "https://www.example.net", ForwardType.STANDARD);
DomainForwarding forwarding = client.domains().forwarding().get("example.com");
client.domains().forwarding().delete("example.com");

// The IP version of each address is detected
client.domains().childNameServers().add("example.com", "ns1.example.com", "192.0.2.10", "2001:db8::10");
client.domains().childNameServers().update("example.com", "ns1.example.com", List.of("192.0.2.20"));
client.domains().childNameServers().delete("example.com", "ns1.example.com");

// Registry documents (mostly .tr)
client.domains().documents().upload("example.com.tr",
        List.of(DocumentUpload.fromFile(DocumentType.IDENTITY_CARD, Path.of("id.pdf"))));
client.domains().documents().listAll("example.com.tr")
        .forEach(document -> System.out.println(document.name() + ": " + document.status()));
```

### Contacts

Contact handles are reusable contacts. Create one once and use it for many domains.

```java
CreateContactResult created = client.contacts().create(contact);
String handle = created.handle();                                  // for example DNA-12345

ContactDetails details = client.contacts().get(handle);            // the contact and its verification state
HandleUpdateResult result = client.contacts().update(handle, contact);
System.out.println(result.stats().successCount() + " of " + result.stats().totalDomainCount() + " domains updated");

List<ContactVerification> verifications = client.contacts().getVerification(handle);
client.contacts().resendVerification(handle);
```

### DNS

Zone records of domains on the platform nameservers. Records resolve publicly once the domain uses those nameservers,
which `apply` takes care of.

```java
for (DnsRecordSet set : client.dns().list("example.com")) {
    System.out.println(set.name() + " " + set.type() + " " + set.ttl() + " " + set.values());
}

client.dns().add("example.com", "www", RecordType.A, "192.0.2.10");
client.dns().add("example.com", "@", RecordType.of("txt"), "v=spf1 include:_spf.example.net ~all");   // "txt" becomes TXT
client.dns().add("example.com", "@", RecordType.MX, List.of("10 mx1.example.net", "20 mx2.example.net"), 3600);
client.dns().replace("example.com", "www", RecordType.A, List.of("192.0.2.20"), 300);
client.dns().delete("example.com", "www", RecordType.A, "192.0.2.20");

ZoneApplyResult applied = client.dns().apply("example.com");
```

The SDK checks what the API would refuse: a CNAME at the apex, a CAA value without its three parts, a TTL outside
1–86400. Zone calls are kept 1 second apart, the gateway's limit.

### DNSSEC

```java
DnssecInfo info = client.dnssec().get("example.com");   // status, dsRecords, allowedAlgorithms, ...

DsRecordData record = DsRecordData.parse("12345 13 2 49FD46E6C4B45C55D4AC69CBD3CD34AC1AFE51DE");
client.dnssec().add("example.com", record);
client.dnssec().remove("example.com", record);
client.dnssec().disable("example.com");                 // removes every DS record
```

### SSL

A domain can have several certificates, so an existing certificate is addressed by its **certificate ID** (a `UUID`),
never by its domain name. `purchase` and `renew` return the ID: store it. `list` finds the IDs of the certificates
you already have.

```java
// 1. Buy (charges the balance). Product codes come from client.products().listSslProducts().
List<String> approvers = client.ssl().getApproverEmails("example.com", "PKG-CODE");   // before the purchase
SslOperationResult order = client.ssl().purchase("example.com", "PKG-CODE");
UUID certificateId = order.certificateId();             // store it: every later call takes it

// 2. A CSR and private key, if you do not have one
GeneratedCsr csr = client.ssl().generateCsr(new GenerateCsrRequest("example.com")
        .country("TR").state("Istanbul").locality("Istanbul").organization("Example Ltd").organizationUnit("IT"));

// 3. Configure within 15 days: CSR, validation method and contact
client.ssl().configure(new ConfigureCertificateRequest(certificateId, csr.csr(), certificateContact)
        .validationMethod(ValidationMethod.EMAIL)
        .approverEmail(approvers.get(0))
        .webServerType(WebServerType.APACHE));

// 4. Wait for issuance, then download
CertificateStatus status = client.ssl().refreshStatus(certificateId);
if (status.isIssued()) {
    CertificateContent content = client.ssl().getContent(certificateId);
    Files.writeString(Path.of("fullchain.pem"), content.fullChain());
}

CertificateDetails details = client.ssl().get(certificateId);   // subscription, configuration, contacts
client.ssl().reissue(new ReissueCertificateRequest(certificateId).csr(newCsr).privateKey(newKey));
client.ssl().revoke(certificateId);                              // cannot be undone
```

**List certificates and find an ID**

```java
Page<CertificateListItem> page = client.ssl().list(new ListCertificatesRequest().domainName("example.com"));   // newest first

client.ssl().listAll().forEach(c ->
        System.out.println(c.certificateId() + " " + c.domainName() + " " + c.status() + " " + c.subscriptionExpiresAt()));

// The approver addresses of a certificate you already have
List<String> approverEmails = client.ssl().getApproverEmails(certificateId);
```

**Renew** (this charges your balance)

```java
// Allowed from 30 days before details.subscription().expiresAt(). The renewal is a new certificate with its own ID.
SslOperationResult renewal = client.ssl().renew(certificateId);
client.ssl().configure(new ConfigureCertificateRequest(renewal.certificateId(), newCsr, certificateContact)
        .validationMethod(ValidationMethod.EMAIL)
        .approverEmail(approvers.get(0))
        .webServerType(WebServerType.APACHE));
```

A purchased or renewed certificate must be configured within 15 days; after that it is cancelled. To renew, call
`renew` and then `configure` with the new ID. Do not buy again with `purchase`: that is a separate purchase at the
purchase price, and the old certificate is never marked as renewed. An empty certificate ID throws an
`IllegalArgumentException` before anything is sent.

### Hosting

```java
List<HostingPackage> packages = client.hosting().listPackages();
HostingCreateResult created = client.hosting().create("example.com", "starter-linux");   // charges the balance
HostingStatus status = client.hosting().getStatus("example.com");                         // panel access once ready
HostingService hosting = client.hosting().get("example.com");
List<ServicePlan> plans = client.hosting().listPlans();
```

### Orders and products

```java
Order order = client.orders().get("ORDER-CODE");

client.products().getTld("com").ifPresent(com -> System.out.println(
        com.registrationPrice(1).map(Tld.Price::price).orElse(null) + ", DNSSEC: " + com.supportsDnssec()));

client.products().listAllTlds(new ListTldsRequest().currency("USD")).forEach(tld -> System.out.println(tld.name()));

ProductPrice price = client.products().getPrice("com", OrderType.RENEW, 1);
Page<SslProduct> ssl = client.products().listSslProducts(new ListSslProductsRequest().isWildcard(true));
```

## Requests and `BaseRequest`

Every operation is a request class, for example `RenewDomainRequest`, `ListDomainsRequest` or `AddDnsRecordRequest`.
The client methods above build them for you, and you can also create and send them yourself:

```java
RenewDomainRequest request = new RenewDomainRequest("example.com", 2);
System.out.println(request + " is " + request.safety());   // "POST domains/renew is BILLABLE"

DomainOperationResult result = client.send(request);

// The same, with the HTTP details: correlation ID, soft-quota state, attempts, duration
ApiResponse<DomainOperationResult> response = client.sendWithResponse(request);
System.out.println(response.correlationId() + " quota " + response.quota());
```

All requests extend one of two base classes:

| Base class | For |
|---|---|
| `BaseRequest<T>` | Every operation. `T` is the answer type; `Void` for operations that return nothing (lock, unlock, add a DNS record, ...). |
| `PagedRequest<T>` | List operations. Adds `skipCount`, `maxResultCount` and `sorting`, and works with `listAll`. |

A request says what it is with a few overrides: `method()`, `path()` (below `/api/v1/`) and `safety()`. Query
parameters are added in `addQueryParameters`, the JSON body comes from `body()` (`JsonBody` builds one and leaves out
nulls), and checks before sending go in `validate()`. To call an endpoint the SDK does not cover yet, write your own:

```java
public final class GetDomainNotesRequest extends BaseRequest<DomainNotes> {
    private final String domainName;

    public GetDomainNotesRequest(String domainName) {
        this.domainName = domainName;
    }

    @Override public HttpMethod method() { return HttpMethod.GET; }
    @Override public String path() { return "domains/notes"; }               // below /api/v1/
    @Override public OperationSafety safety() { return OperationSafety.READ; }

    @Override protected void addQueryParameters(QueryParameters query) {
        query.add("DomainName", domainName);
    }

    @Override protected void validate() {
        require(domainName, "domainName");
    }
}

public record DomainNotes(List<String> notes) { }

DomainNotes notes = client.send(new GetDomainNotesRequest("example.com"));
```

Your request gets everything the built-in ones get: credentials, retries by safety class, pacing, read-only mode and
error handling. The answer type is read from `extends BaseRequest<DomainNotes>`; override `responseType()` only for
generic request classes. Override `isIdempotent()` when a write is safe to repeat.

## Errors

Every failed request throws a `DomainNameApiException` (unchecked). Invalid arguments throw
`IllegalArgumentException` before anything is sent.

```java
try {
    client.domains().renew("example.com", 1);
} catch (DomainNameApiException e) {
    if (e.kind() == ApiErrorKind.VALIDATION) {
        System.out.println(e.code() + ": " + e.getMessage());   // Dna.DomainService:Domain:10007: ...
        e.validationErrors().forEach(field -> System.out.println("  " + field));
    } else if (e.isOutcomeUnknown()) {
        // A billable or changing request failed on the network: it may have reached the API.
        // Check the state before sending it again.
        DomainDetails details = client.domains().get("example.com");
    } else {
        throw e;
    }
}
```

| Method | Meaning |
|---|---|
| `kind()` | A stable classification (table below). Branch on it, not on the message. |
| `code()` | The API's error code (such as `Dna.DomainService:Domain:10007`), or one of the SDK's codes below. |
| `getMessage()` | What happened. Boilerplate messages are replaced with the API's details. |
| `statusCode()` | The HTTP status, or null when no answer arrived. |
| `details()`, `validationErrors()` | Additional detail and field-level problems from the API. |
| `correlationId()` | The gateway's `X-Correlation-Id`. Quote it when contacting support. |
| `hint()` | A short suggestion on what to do next. |
| `isRetryable()` | Whether repeating the same request later can succeed. |
| `isOutcomeUnknown()` | A request that changes something failed on the network, so it may have run. |
| `operation()`, `safety()` | The failed request (`POST domains/renew`) and its safety class. |
| `retryAfter()`, `responseBody()` | The gateway's `Retry-After` and the raw answer. |

| `kind()` | When |
|---|---|
| `AUTHENTICATION` | Wrong or missing credentials, or an unknown or suspended reseller in this environment (401, redirect). |
| `FORBIDDEN` | Authenticated but not allowed (403). |
| `NOT_FOUND` | The domain, contact, order or other resource does not exist (404). |
| `VALIDATION` | The API refused the input or a business rule failed: bad field, domain locked, insufficient balance (400, 422). |
| `CONFLICT` | The resource's state does not allow the operation (409). |
| `RATE_LIMITED` | Still rate limited after the automatic retries (429). |
| `SERVER` | A server-side failure (5xx). Often temporary. |
| `NETWORK`, `TIMEOUT` | No answer: DNS, TLS, proxy, connection reset, or no answer within the timeout. |
| `READ_ONLY` | Blocked by read-only mode; nothing was sent. |
| `UNKNOWN` | Anything else, including an answer the SDK could not read. |

Codes the SDK sets itself:

| Code | Meaning |
|---|---|
| `INVALID_RESELLER` | The gateway redirected to its suspend page: the reseller ID is unknown, inactive or suspended **in this environment**. |
| `INVALID_API_KEY` | The key does not match the reseller. Production and OTE keys differ. |
| `MISSING_API_KEY`, `MISSING_RESELLER_ID`, `NO_CREDENTIALS` | A credential is empty. |
| `SUBRESELLER_NOT_ALLOWED` | Sub-reseller accounts cannot use the API. Use the main reseller's credentials. |
| `UNAUTHORIZED` | Any other 401. |
| `RATE_LIMITED` | 429 after the retries. |
| `NETWORK`, `TIMEOUT` | No answer arrived. |
| `READ_ONLY` | Blocked by read-only mode. |
| `EMPTY_RESPONSE`, `INVALID_RESPONSE` | A successful answer was empty or could not be read. |
| `HTTP_<status>` | The API sent no error code. |

When the thread is interrupted while a request waits, the SDK sets the interrupt flag again and throws a
`CancellationException` instead of an API error.

## Safety, retries and rate limits

Every request has a safety class, the same one the `dna` CLI uses:

| `OperationSafety` | Means | Examples |
|---|---|---|
| `READ` | No side effects, even when the API uses POST or PUT | search, details, lists, renewal and transfer checks, CSR generation |
| `WRITE` | A free change that can be undone | DNS records, lock, privacy, nameservers, contacts |
| `BILLABLE` | Charges your balance | register, renew, transfer, restore, SSL purchase and renewal, hosting order |
| `DESTRUCTIVE` | Deletes something or is hard to undo | DNS record deletion, DNSSEC removal, certificate revocation |

- **Read-only mode** (`readOnly(true)` or `DNA_READ_ONLY=true`) blocks everything that is not a `READ` before it is sent.
  Use it while exploring, in reports and in dashboards.
- **Retries.** 429 answers are always retried, honouring `Retry-After`. This is safe even for billable requests, because
  the gateway refuses them before anything runs. Network errors, timeouts and 502/503/504 answers are retried only for
  reads (and for writes that are safe to repeat, such as replacing nameservers). A registration or renewal is never sent twice.
- **Pacing.** The gateway allows 5 requests per second per key, 2 per second for domain searches and 20 per second for
  DNS zone calls. The client spaces requests to stay under these limits (zone calls 1 second apart). Domain info and the
  TLD list are not rate limited.
- **Soft quota.** Searches and info lookups cost 1 point, everything else 10. Past 200 points the gateway slows each
  request down by up to 5 seconds; points decay by 20% per minute. `sendWithResponse` shows the state in `quota()`.

## Asynchronous calls

The client is synchronous: each method blocks until the answer arrives, which suits server code and virtual threads.
`sendAsync` and `sendWithResponseAsync` run a request on the client's executor and return a `CompletableFuture`:

```java
CompletableFuture<DomainDetails> details = client.sendAsync(new GetDomainRequest("example.com"));
details.thenAccept(d -> System.out.println(d.expirationDate()));
```

The future fails with the same exceptions the synchronous methods throw (wrapped in a `CompletionException` by
`join()`). Set your own executor with `builder().executor(...)`, for example `Executors.newVirtualThreadPerTaskExecutor()`
on Java 21.

## Testing your code

The SDK only needs an HTTP endpoint. To test code that uses it without calling the API, point `baseUri` at a local
server, for example the JDK's built-in one:

```java
HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
server.createContext("/api/v1/deposit/accounts/me", exchange -> {
    byte[] body = "{\"resellerName\":\"Test\",\"usdBalance\":10}".getBytes(StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(200, body.length);
    exchange.getResponseBody().write(body);
    exchange.close();
});
server.start();

DomainNameApiClient client = DomainNameApiClient.builder()
        .credentials("test", "test")
        .baseUri(URI.create("http://127.0.0.1:" + server.getAddress().getPort()))
        .paceRequests(false)
        .build();

ResellerAccount account = client.account().get();   // usdBalance() == 10
```

You can also pass your own `HttpClient` subclass with `httpClient(...)`, and create exceptions for your mocks with
`DomainNameApiException.builder(...)`. For tests against the real API, use OTE with its own key.

## Project layout

```
src/main/java/io/github/domainreseller/dna/
├── DomainNameApiClient.java   The entry point: builder, area clients, send, retries, pacing
├── BaseRequest, PagedRequest, QueryParameters, JsonBody, Page, ApiResponse, DomainNameApiException, enums
├── internal/                  JSON settings, error parsing, the rate-limit pacer (not public API)
├── accounts/   AccountClient  Balances, profile and transactions
├── contacts/   ContactsClient Contact handles
├── domains/    DomainsClient  Domains, plus forwarding(), childNameServers() and documents()
├── dns/        DnsClient      Zone records
├── dnssec/     DnssecClient   DS records
├── ssl/        SslClient      Certificates
├── hosting/    HostingClient  Hosting packages and services
├── orders/     OrdersClient   Orders
└── products/   ProductsClient TLDs, SSL products and prices
```

Every area package has the same shape: an `XxxClient`, one request class per operation (`XxxRequest`) and the models
and enums of its answers.

## Development

```bash
mvn verify                          # build and run the tests
mvn -Pexamples -DskipTests compile  # also compile the examples
```

Without a local JDK, use Docker:

```bash
docker run --rm -v "$PWD":/src -w /src maven:3.9-eclipse-temurin-17 mvn -B verify
```

The tests never call the real API: they use a fake `HttpClient`, plus a local HTTP server on 127.0.0.1 for the
transport. CI runs them on Java 17, 21 and 25.

`src/test/resources/openapi.json` is the API document the `dna` CLI embeds. `SpecCoverageTest` checks that every
operation in it has exactly one request class with the CLI's safety class, and that every request sends only query
parameters and body properties the document describes. When the API gains an operation, update the document and the
test fails until the request exists.

## Upgrading from 1.x

2.0 is a new SDK. 1.x was a port of the PHP library and returned untyped `Map` envelopes; 2.0 has typed requests and
models, exceptions and a builder.

| 1.x | 2.0 |
|---|---|
| `new DomainNameApi(id, key, true)` | `DomainNameApiClient.create(id, key, ApiEnvironment.OTE)` |
| `dna.getResellerDetails()`, `dna.getCurrentBalance()` | `client.account().get()` / `getProfile()` |
| `dna.checkAvailability(...)` | `client.domains().search(...)` / `bulkSearch(...)` |
| `dna.getList()` | `client.domains().list()` / `listAll()` |
| `dna.getDetails(domain)` | `client.domains().get(domain)` |
| `dna.registerWithContactInfo(...)` | `client.domains().register(...)` |
| `dna.renew(domain, period)` | `client.domains().renew(domain, years)` |
| `dna.transfer(domain, eppCode, period)` | `client.domains().transfer(domain, authCode, years)` |
| `dna.modifyNameServer(...)` | `client.domains().setNameServers(...)` |
| `dna.enableTheftProtectionLock(domain)` | `client.domains().lock(domain)` |
| `dna.modifyPrivacyProtectionStatus(...)` | `client.domains().setPrivacyProtection(...)` |
| `dna.saveContacts(...)` | `client.domains().updateContacts(...)` |
| `dna.addChildNameServer(...)` | `client.domains().childNameServers().add(...)` |
| `dna.getResourceRecords(domain)` | `client.dns().list(domain)` |
| `dna.addResourceRecord(...)` | `client.dns().add(...)` |
| `dna.setResourceRecordSet(...)` | `client.dns().replace(...)` |
| `dna.applyResourceRecords(domain)` | `client.dns().apply(domain)` |
| `dna.setForwarding(...)` | `client.domains().forwarding().set(...)` |
| `dna.getTldList()` | `client.products().listAllTlds()` |
| `result.get("result").equals("ERROR")` | `catch (DomainNameApiException e)` |

The Java baseline moves from 11 to 17, and Jackson is now a dependency. 1.x sent error reports to Sentry; 2.0 sends
nothing anywhere except to the API.

## Links

- [`dna` CLI and MCP server](https://github.com/domainreseller/cli): the same API from the terminal and from AI agents.
- [API documentation](https://www.domainnameapi.com/docs/)
- [Changelog](CHANGELOG.md)

## License

Apache License 2.0. See [LICENSE](LICENSE).
