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

De officiële Java SDK voor het resellerplatform van [Domain Name API](https://www.domainnameapi.com). Hij dekt alle
67 operaties van de REST API met getypeerde clients en modellen: domeinen, contacten, DNS, DNSSEC, SSL-certificaten,
hosting, bestellingen, producten en je account.

```java
DomainNameApiClient client = DomainNameApiClient.create(resellerId, apiKey, ApiEnvironment.OTE);

DomainSearchResult search = client.domains().search("example.com");
if (search.info() != null && search.info().isAvailable()) {
    client.domains().register("example.com", contact, List.of("tr.apiname.com", "eu.apiname.com"));
}
```

- **Alles getypeerd.** Elke operatie is een requestklasse en elk antwoord een record met Javadoc. Geen maps, geen JSON-parsing.
- **Je eigen requests.** Elk request erft van `BaseRequest`. Maak een subklasse voor een endpoint dat de SDK nog niet dekt.
- **Veilig met geld.** Elk request geeft aan of het leest, schrijft, je saldo belast of iets verwijdert. Billable requests worden nooit herhaald en een read-only modus blokkeert elke wijziging.
- **Ingebouwde veerkracht.** Rate limits worden automatisch gerespecteerd, 429-antwoorden worden herhaald en netwerkfouten worden herhaald bij leesacties.
- **Duidelijke fouten.** Eén unchecked exception met een vaste soort, de foutcode van de API, validatiefouten per veld, de correlation ID en een hint.
- **Modern Java.** Java 17+, records, lazy paginering met `Stream`, `CompletableFuture`-varianten, een thread-safe client op `java.net.http`.
- **Privé.** Geen telemetrie. Niets verlaat je proces behalve de requests die je naar de API stuurt.

Het is de Java-tegenhanger van [`dna`, de Domain Name API CLI](https://github.com/domainreseller/cli): hetzelfde API-document,
dezelfde veiligheidsklassen, retryregels en foutcodes.

## Inhoud

- [Installatie](#install)
- [Inloggegevens](#credentials)
- [Snel aan de slag](#quick-start)
- [Configuratie](#configuration)
- [Gebruik](#usage): [account](#account) · [domeinen](#domains) · [contacten](#contacts) · [DNS](#dns) · [DNSSEC](#dnssec) · [SSL](#ssl) · [hosting](#hosting) · [bestellingen en producten](#orders-and-products)
- [Requests en `BaseRequest`](#requests-and-baserequest)
- [Fouten](#errors)
- [Veiligheid, retries en rate limits](#safety-retries-and-rate-limits)
- [Asynchrone aanroepen](#asynchronous-calls)
- [Je code testen](#testing-your-code)
- [Projectindeling](#project-layout)
- [Ontwikkeling](#development)
- [Upgraden van 1.x](#upgrading-from-1x)

<a id="install"></a>

## Installatie

> [!IMPORTANT]
> **Gebruik altijd de [nieuwste release](https://github.com/domainreseller/java-dna/releases/latest).** Release-pakketten hebben een versienummer en zijn getest.
> De groene knop **Code → Download ZIP** downloadt in plaats daarvan de ontwikkelbranch.

Coördinaten: `io.github.domainreseller:domainnameapi:2.0.0`.

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

Zolang het artifact nog niet in een publieke repository beschikbaar is, installeer je het vanuit de broncode van de
release in je lokale Maven-repository (`mvn -DskipTests install`) en voeg je `mavenLocal()` toe aan je Gradle-repositories.

**Vereisten:** Java 17 of nieuwer. De enige dependency is [Jackson](https://github.com/FasterXML/jackson-databind)
(`jackson-databind`) voor JSON; HTTP loopt via de `java.net.http.HttpClient` van de JDK.

<a id="credentials"></a>

## Inloggegevens

De API authenticeert elk request met twee waarden, die de SDK voor je meestuurt:

| Waarde | Header | Waar je het vindt |
|---|---|---|
| Reseller ID (een UUID) | `__reseller` | Resellerpaneel → **API settings** |
| API-sleutel | `X-API-KEY` | Resellerpaneel → **API settings** |

> [!NOTE]
> Dit zijn **API-inloggegevens**: je paneelaccount werkt hier niet. **Productie en OTE (de testomgeving) gebruiken
> verschillende API-sleutels.** Als je de ene bij de andere gebruikt, krijg je `INVALID_API_KEY` of `INVALID_RESELLER`.

<a id="quick-start"></a>

## Snel aan de slag

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

De map [examples](examples) bevat uitvoerbare programma's voor deze scenario's. Ze lezen de omgevingsvariabelen
`DNA_*`, dus laat ze naar OTE wijzen:

```bash
export DNA_RESELLER_ID=... DNA_API_KEY=... DNA_ENV=ote
```

<a id="configuration"></a>

## Configuratie

Er zijn vier manieren om een client te maken:

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

| Buildermethode | Standaard | Betekenis |
|---|---|---|
| `credentials(resellerId, apiKey)` | (verplicht) | De inloggegevens. |
| `environment(...)` | `PRODUCTION` | `PRODUCTION` (`https://api.domainresellerapi.com`) of `OTE` (`https://ote.domainresellerapi.com`). |
| `baseUri(...)` | geen | Overschrijft het adres van de omgeving, bijvoorbeeld voor een proxy. |
| `timeout(...)` | 60 s | Tijdslimiet van één poging. |
| `maxRetries(...)` | 2 | Hoe vaak een mislukt request wordt herhaald (zie [retries](#safety-retries-and-rate-limits)). 0 zet retries uit. |
| `maxRetryDelay(...)` | 30 s | De langste wachttijd voor een retry. |
| `paceRequests(...)` | `true` | Spreidt requests zodat je onder de rate limits van de gateway blijft. |
| `readOnly(...)` | `false` | Blokkeert elk request dat geen leesactie is, voordat het wordt verstuurd. |
| `userAgent(...)` | `DomainNameAPI-Java/<version>` | De `User-Agent`-header. |
| `app(name, version)` | `java`, de SDK-versie | De applicatie die de SDK gebruikt, verstuurd als `X-App` en `X-Version` (zie [hieronder](#uw-applicatie-identificeren)). |
| `httpClient(...)` | die van de SDK zelf | Je eigen `java.net.http.HttpClient` (proxy, TLS, executor). |
| `executor(...)` | een gedeelde daemon-pool | Waar de `sendAsync`-methoden draaien. |

`fromEnvironment()` leest:

| Variabele | Betekenis |
|---|---|
| `DNA_RESELLER_ID`, `DNA_API_KEY` | De inloggegevens. |
| `DNA_ENV` | `production` of `ote` (ook `prod`, `live`, `test`, `sandbox`). |
| `DNA_TEST_MODE` | `true`, `1`, `on` of `yes` kiest OTE. Wordt gebruikt als `DNA_ENV` niet is ingesteld. |
| `DNA_BASE_URL` | Optionele overschrijving van het basisadres. |
| `DNA_READ_ONLY` | `true` zet de read-only modus aan. |
| `DNA_APP`, `DNA_APP_VERSION` | De applicatie die de SDK gebruikt en haar versie (`X-App`, `X-Version`). |

### Uw applicatie identificeren

Elk verzoek bevat twee headers die de API vertellen welke integratie het verstuurde: `X-App`, de naam van de applicatie
in kleine letters waarvan alleen `a-z`, `0-9`, `.`, `_` en `-` overblijven, en `X-Version`, haar versie met alleen
cijfers en punten. Standaard zijn dat `java` en de SDK-versie. Geef uw paneel of module op met `app(name, version)` of
`DNA_APP`/`DNA_APP_VERSION`; een lege of ongeldige naam wordt `java`, een lege versie de SDK-versie. Er wordt verder
niets verstuurd en de `User-Agent` blijft gelijk.

```java
DomainNameApiClient client = DomainNameApiClient.builder()
        .credentials(resellerId, apiKey)
        .app("WiseCP", "V2.0.20-beta")   // sends X-App: wisecp, X-Version: 2.0.20
        .build();
```

De client is thread-safe. Maak er één per set inloggegevens en hergebruik hem zolang de applicatie draait.

<a id="usage"></a>

## Gebruik

Elk deel van de API heeft een eigen client. Methoden nemen eenvoudige argumenten; voor meer opties heeft elke methode ook een
overload (of een zustermethode) die het requestobject neemt. Methoden die je saldo belasten, vermelden dat in hun Javadoc.

<a id="account"></a>

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

<a id="domains"></a>

### Domeinen

**Zoeken**

```java
DomainSearchResult one = client.domains().search("example.com", "USD");
boolean available = one.info() != null && one.info().isAvailable();

BulkSearchResult many = client.domains().bulkSearch(List.of("example.com", "example.net", "example.com.tr"));
for (DomainAvailability domain : many.available()) {
    System.out.println(domain.domainName() + ": " + domain.price() + " " + domain.currency());
}
```

**Lijst en details**

```java
Page<Domain> page = client.domains().list(new ListDomainsRequest().maxResultCount(50).statuses(DomainStatus.ACTIVE));

client.domains().listAll(new ListDomainsRequest().domainName("example"))
        .forEach(d -> System.out.println(d.domainName() + " " + d.status() + " " + d.expirationDate()));

DomainDetails details = client.domains().get("example.com");
System.out.println(details.authCode());
details.contact(ContactType.REGISTRANT).ifPresent(c -> System.out.println(c.email()));
```

**Registreren, verlengen, verhuizen, herstellen** (deze belasten je saldo)

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

Requests worden gecontroleerd voordat ze worden verstuurd. Een registratie zonder alle vier de contactrollen, een contact zonder e-mailadres
of een periode van 0 jaar gooit een `IllegalArgumentException`, en er bereikt niets de API.

> [!NOTE]
> De API kan een registratie, verlenging of verhuizing beantwoorden met HTTP 200 en `"success": false` (bijvoorbeeld als de
> registry het domein weigert). De SDK geeft dat antwoord ongewijzigd terug: controleer `DomainOperationResult.success()` en
> `message()`.

**Instellingen**

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

**URL-doorsturing, child nameservers en documenten**

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

<a id="contacts"></a>

### Contacten

Contact handles zijn herbruikbare contacten. Maak er één aan en gebruik hem voor veel domeinen.

```java
CreateContactResult created = client.contacts().create(contact);
String handle = created.handle();                                  // for example DNA-12345

ContactDetails details = client.contacts().get(handle);            // the contact and its verification state
HandleUpdateResult result = client.contacts().update(handle, contact);
System.out.println(result.stats().successCount() + " of " + result.stats().totalDomainCount() + " domains updated");

List<ContactVerification> verifications = client.contacts().getVerification(handle);
client.contacts().resendVerification(handle);
```

<a id="dns"></a>

### DNS

Zonerecords van domeinen op de nameservers van het platform. Records zijn publiek vindbaar zodra het domein die nameservers gebruikt,
waar `apply` voor zorgt.

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

De SDK controleert wat de API zou weigeren: een CNAME op de apex, een CAA-waarde zonder de drie onderdelen, een TTL buiten
1–86400. Zoneaanroepen worden 1 seconde uit elkaar gehouden, de limiet van de gateway.

<a id="dnssec"></a>

### DNSSEC

```java
DnssecInfo info = client.dnssec().get("example.com");   // status, dsRecords, allowedAlgorithms, ...

DsRecordData record = DsRecordData.parse("12345 13 2 49FD46E6C4B45C55D4AC69CBD3CD34AC1AFE51DE");
client.dnssec().add("example.com", record);
client.dnssec().remove("example.com", record);
client.dnssec().disable("example.com");                 // removes every DS record
```

<a id="ssl"></a>

### SSL

Een domein kan meerdere certificaten hebben. Een bestaand certificaat wordt daarom aangeduid met zijn
**certificaat-ID** (een `UUID`), nooit met de domeinnaam. `purchase` en `renew` geven het ID terug: bewaar het.
`list` vindt de ID's van de certificaten die je al hebt.

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

**Certificaten weergeven en een ID vinden**

```java
Page<CertificateListItem> page = client.ssl().list(new ListCertificatesRequest().domainName("example.com"));   // newest first

client.ssl().listAll().forEach(c ->
        System.out.println(c.certificateId() + " " + c.domainName() + " " + c.status() + " " + c.subscriptionExpiresAt()));

// The approver addresses of a certificate you already have
List<String> approverEmails = client.ssl().getApproverEmails(certificateId);
```

**Verlengen** (dit belast je saldo)

```java
// Allowed from 30 days before details.subscription().expiresAt(). The renewal is a new certificate with its own ID.
SslOperationResult renewal = client.ssl().renew(certificateId);
client.ssl().configure(new ConfigureCertificateRequest(renewal.certificateId(), newCsr, certificateContact)
        .validationMethod(ValidationMethod.EMAIL)
        .approverEmail(approvers.get(0))
        .webServerType(WebServerType.APACHE));
```

Een gekocht of verlengd certificaat moet binnen 15 dagen worden geconfigureerd; daarna wordt het geannuleerd. Om te
verlengen roep je `renew` aan en daarna `configure` met het nieuwe ID. Koop niet opnieuw met `purchase`: dat is een
losse aankoop tegen de aankoopprijs, en het oude certificaat wordt nooit als verlengd gemarkeerd. Een leeg certificaat-ID
gooit een `IllegalArgumentException` voordat er iets wordt verstuurd.

<a id="hosting"></a>

### Hosting

```java
List<HostingPackage> packages = client.hosting().listPackages();
HostingCreateResult created = client.hosting().create("example.com", "starter-linux");   // charges the balance
HostingStatus status = client.hosting().getStatus("example.com");                         // panel access once ready
HostingService hosting = client.hosting().get("example.com");
List<ServicePlan> plans = client.hosting().listPlans();
```

<a id="orders-and-products"></a>

### Bestellingen en producten

```java
Order order = client.orders().get("ORDER-CODE");

client.products().getTld("com").ifPresent(com -> System.out.println(
        com.registrationPrice(1).map(Tld.Price::price).orElse(null) + ", DNSSEC: " + com.supportsDnssec()));

client.products().listAllTlds(new ListTldsRequest().currency("USD")).forEach(tld -> System.out.println(tld.name()));

ProductPrice price = client.products().getPrice("com", OrderType.RENEW, 1);
Page<SslProduct> ssl = client.products().listSslProducts(new ListSslProductsRequest().isWildcard(true));
```

<a id="requests-and-baserequest"></a>

## Requests en `BaseRequest`

Elke operatie is een requestklasse, bijvoorbeeld `RenewDomainRequest`, `ListDomainsRequest` of `AddDnsRecordRequest`.
De clientmethoden hierboven bouwen ze voor je, en je kunt ze ook zelf maken en versturen:

```java
RenewDomainRequest request = new RenewDomainRequest("example.com", 2);
System.out.println(request + " is " + request.safety());   // "POST domains/renew is BILLABLE"

DomainOperationResult result = client.send(request);

// The same, with the HTTP details: correlation ID, soft-quota state, attempts, duration
ApiResponse<DomainOperationResult> response = client.sendWithResponse(request);
System.out.println(response.correlationId() + " quota " + response.quota());
```

Alle requests erven van een van twee basisklassen:

| Basisklasse | Voor |
|---|---|
| `BaseRequest<T>` | Elke operatie. `T` is het antwoordtype; `Void` voor operaties die niets teruggeven (vergrendelen, ontgrendelen, een DNS-record toevoegen, ...). |
| `PagedRequest<T>` | Lijstoperaties. Voegt `skipCount`, `maxResultCount` en `sorting` toe en werkt met `listAll`. |

Een request geeft met een paar overrides aan wat het is: `method()`, `path()` (onder `/api/v1/`) en `safety()`.
Queryparameters voeg je toe in `addQueryParameters`, de JSON-body komt uit `body()` (`JsonBody` bouwt er een en laat
null-waarden weg), en controles vóór het versturen horen in `validate()`. Om een endpoint aan te roepen dat de SDK nog
niet dekt, schrijf je er zelf een:

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

Jouw request krijgt alles wat de ingebouwde requests krijgen: inloggegevens, retries per veiligheidsklasse, pacing, read-only modus en
foutafhandeling. Het antwoordtype wordt gelezen uit `extends BaseRequest<DomainNotes>`; overschrijf `responseType()` alleen
voor generieke requestklassen. Overschrijf `isIdempotent()` als een schrijfactie veilig te herhalen is.

<a id="errors"></a>

## Fouten

Elk mislukt request gooit een `DomainNameApiException` (unchecked). Ongeldige argumenten gooien een
`IllegalArgumentException` voordat er iets wordt verstuurd.

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

| Methode | Betekenis |
|---|---|
| `kind()` | Een vaste classificatie (tabel hieronder). Vertak op deze waarde, niet op het bericht. |
| `code()` | De foutcode van de API (zoals `Dna.DomainService:Domain:10007`), of een van de SDK-codes hieronder. |
| `getMessage()` | Wat er gebeurde. Standaardberichten worden vervangen door de details van de API. |
| `statusCode()` | De HTTP-status, of null als er geen antwoord kwam. |
| `details()`, `validationErrors()` | Extra details en problemen per veld van de API. |
| `correlationId()` | De `X-Correlation-Id` van de gateway. Noem hem als je contact opneemt met support. |
| `hint()` | Een korte suggestie voor wat je daarna kunt doen. |
| `isRetryable()` | Of het herhalen van hetzelfde request later kan slagen. |
| `isOutcomeUnknown()` | Een request dat iets wijzigt, mislukte op het netwerk, dus het kan zijn uitgevoerd. |
| `operation()`, `safety()` | Het mislukte request (`POST domains/renew`) en zijn veiligheidsklasse. |
| `retryAfter()`, `responseBody()` | De `Retry-After` van de gateway en het ruwe antwoord. |

| `kind()` | Wanneer |
|---|---|
| `AUTHENTICATION` | Onjuiste of ontbrekende inloggegevens, of een onbekende of geschorste reseller in deze omgeving (401, redirect). |
| `FORBIDDEN` | Geauthenticeerd, maar niet toegestaan (403). |
| `NOT_FOUND` | Het domein, contact, de bestelling of een andere resource bestaat niet (404). |
| `VALIDATION` | De API weigerde de invoer of een bedrijfsregel faalde: ongeldig veld, domein vergrendeld, onvoldoende saldo (400, 422). |
| `CONFLICT` | De status van de resource staat de operatie niet toe (409). |
| `RATE_LIMITED` | Na de automatische retries nog steeds rate limited (429). |
| `SERVER` | Een fout aan de serverzijde (5xx). Vaak tijdelijk. |
| `NETWORK`, `TIMEOUT` | Geen antwoord: DNS, TLS, proxy, verbroken verbinding, of geen antwoord binnen de timeout. |
| `READ_ONLY` | Geblokkeerd door de read-only modus; er is niets verstuurd. |
| `UNKNOWN` | Al het overige, inclusief een antwoord dat de SDK niet kon lezen. |

Codes die de SDK zelf instelt:

| Code | Betekenis |
|---|---|
| `INVALID_RESELLER` | De gateway verwees door naar de suspendpagina: de reseller-ID is onbekend, inactief of geschorst **in deze omgeving**. |
| `INVALID_API_KEY` | De sleutel hoort niet bij de reseller. Productie- en OTE-sleutels verschillen. |
| `MISSING_API_KEY`, `MISSING_RESELLER_ID`, `NO_CREDENTIALS` | Een inloggegeven is leeg. |
| `SUBRESELLER_NOT_ALLOWED` | Subreseller-accounts kunnen de API niet gebruiken. Gebruik de inloggegevens van de hoofdreseller. |
| `UNAUTHORIZED` | Elke andere 401. |
| `RATE_LIMITED` | 429 na de retries. |
| `NETWORK`, `TIMEOUT` | Er kwam geen antwoord. |
| `READ_ONLY` | Geblokkeerd door de read-only modus. |
| `EMPTY_RESPONSE`, `INVALID_RESPONSE` | Een geslaagd antwoord was leeg of kon niet worden gelezen. |
| `HTTP_<status>` | De API stuurde geen foutcode. |

Als de thread wordt onderbroken terwijl een request wacht, zet de SDK de interrupt-vlag opnieuw en gooit hij een
`CancellationException` in plaats van een API-fout.

<a id="safety-retries-and-rate-limits"></a>

## Veiligheid, retries en rate limits

Elk request heeft een veiligheidsklasse, dezelfde als die de `dna` CLI gebruikt:

| `OperationSafety` | Betekent | Voorbeelden |
|---|---|---|
| `READ` | Geen neveneffecten, ook niet als de API POST of PUT gebruikt | zoeken, details, lijsten, verlengings- en verhuiscontroles, CSR-generatie |
| `WRITE` | Een gratis wijziging die kan worden teruggedraaid | DNS-records, vergrendeling, privacy, nameservers, contacten |
| `BILLABLE` | Belast je saldo | registreren, verlengen, verhuizen, herstellen, SSL-aankoop en -verlenging, hostingbestelling |
| `DESTRUCTIVE` | Verwijdert iets of is moeilijk terug te draaien | DNS-record verwijderen, DNSSEC verwijderen, certificaat intrekken |

- **Read-only modus** (`readOnly(true)` of `DNA_READ_ONLY=true`) blokkeert alles wat geen `READ` is, voordat het wordt verstuurd.
  Gebruik hem tijdens het verkennen, in rapportages en in dashboards.
- **Retries.** 429-antwoorden worden altijd herhaald, met inachtneming van `Retry-After`. Dit is veilig, zelfs voor billable requests, omdat
  de gateway ze weigert voordat er iets wordt uitgevoerd. Netwerkfouten, timeouts en 502/503/504-antwoorden worden alleen herhaald voor
  leesacties (en voor schrijfacties die veilig te herhalen zijn, zoals het vervangen van nameservers). Een registratie of verlenging wordt nooit twee keer verstuurd.
- **Pacing.** De gateway staat 5 requests per seconde per sleutel toe, 2 per seconde voor domeinzoekopdrachten en 20 per seconde voor
  DNS-zoneaanroepen. De client spreidt requests om onder deze limieten te blijven (zoneaanroepen 1 seconde uit elkaar). Domeininfo en de
  TLD-lijst hebben geen rate limit.
- **Soft quota.** Zoekopdrachten en infoaanvragen kosten 1 punt, al het andere 10. Boven 200 punten vertraagt de gateway elk
  request met maximaal 5 seconden; punten nemen met 20% per minuut af. `sendWithResponse` toont de stand in `quota()`.

<a id="asynchronous-calls"></a>

## Asynchrone aanroepen

De client is synchroon: elke methode blokkeert tot het antwoord binnen is, wat goed past bij servercode en virtual threads.
`sendAsync` en `sendWithResponseAsync` voeren een request uit op de executor van de client en geven een `CompletableFuture` terug:

```java
CompletableFuture<DomainDetails> details = client.sendAsync(new GetDomainRequest("example.com"));
details.thenAccept(d -> System.out.println(d.expirationDate()));
```

De future faalt met dezelfde exceptions die de synchrone methoden gooien (door `join()` verpakt in een
`CompletionException`). Stel je eigen executor in met `builder().executor(...)`, bijvoorbeeld
`Executors.newVirtualThreadPerTaskExecutor()` op Java 21.

<a id="testing-your-code"></a>

## Je code testen

De SDK heeft alleen een HTTP-endpoint nodig. Om code die hem gebruikt te testen zonder de API aan te roepen, laat je
`baseUri` naar een lokale server wijzen, bijvoorbeeld de ingebouwde server van de JDK:

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

Je kunt ook je eigen subklasse van `HttpClient` meegeven met `httpClient(...)`, en exceptions voor je mocks maken met
`DomainNameApiException.builder(...)`. Gebruik voor tests tegen de echte API OTE met een eigen sleutel.

<a id="project-layout"></a>

## Projectindeling

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

Elk gebiedspackage heeft dezelfde opbouw: een `XxxClient`, één requestklasse per operatie (`XxxRequest`) en de modellen
en enums van de antwoorden.

<a id="development"></a>

## Ontwikkeling

```bash
mvn verify                          # build and run the tests
mvn -Pexamples -DskipTests compile  # also compile the examples
```

Zonder lokale JDK gebruik je Docker:

```bash
docker run --rm -v "$PWD":/src -w /src maven:3.9-eclipse-temurin-17 mvn -B verify
```

De tests roepen nooit de echte API aan: ze gebruiken een nep-`HttpClient`, plus een lokale HTTP-server op 127.0.0.1 voor
het transport. CI draait ze op Java 17, 21 en 25.

`src/test/resources/openapi.json` is het API-document dat de `dna` CLI insluit. `SpecCoverageTest` controleert of elke
operatie erin precies één requestklasse heeft met de veiligheidsklasse van de CLI, en of elk request alleen
queryparameters en bodyvelden verstuurt die het document beschrijft. Als de API een operatie erbij krijgt, werk je het
document bij en faalt de test totdat het request bestaat.

<a id="upgrading-from-1x"></a>

## Upgraden van 1.x

2.0 is een nieuwe SDK. 1.x was een port van de PHP-bibliotheek en gaf ongetypeerde `Map`-envelopes terug; 2.0 heeft getypeerde
requests en modellen, exceptions en een builder.

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

De Java-baseline gaat van 11 naar 17, en Jackson is nu een dependency. 1.x stuurde foutrapporten naar Sentry; 2.0 stuurt
nergens iets heen behalve naar de API.

## Links

- [`dna` CLI en MCP-server](https://github.com/domainreseller/cli): dezelfde API vanuit de terminal en vanuit AI-agents.
- [API-documentatie](https://www.domainnameapi.com/docs/)
- [Changelog](CHANGELOG.md)

## Licentie

Apache License 2.0. Zie [LICENSE](LICENSE).
