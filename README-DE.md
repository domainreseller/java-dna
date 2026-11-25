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

Das offizielle Java SDK für die Reseller-Plattform von [Domain Name API](https://www.domainnameapi.com). Es deckt alle
67 Operationen der REST-API mit typisierten Clients und Modellen ab: Domains, Kontakte, DNS, DNSSEC, SSL-Zertifikate,
Hosting, Bestellungen, Produkte und Ihr Konto.

```java
DomainNameApiClient client = DomainNameApiClient.create(resellerId, apiKey, ApiEnvironment.OTE);

DomainSearchResult search = client.domains().search("example.com");
if (search.info() != null && search.info().isAvailable()) {
    client.domains().register("example.com", contact, List.of("tr.apiname.com", "eu.apiname.com"));
}
```

- **Alles typisiert.** Jede Operation ist eine Request-Klasse und jede Antwort ein Record mit Javadoc. Keine Maps, kein JSON-Parsing.
- **Ihre eigenen Requests.** Jeder Request erbt von `BaseRequest`. Leiten Sie davon ab, wenn ein Endpunkt vom SDK noch nicht abgedeckt wird.
- **Sicher im Umgang mit Geld.** Jeder Request gibt an, ob er liest, schreibt, Ihr Guthaben belastet oder etwas löscht. Kostenpflichtige Requests werden nie wiederholt, und ein Nur-Lese-Modus blockiert jede Änderung.
- **Eingebaute Ausfallsicherheit.** Rate-Limits werden automatisch eingehalten, 429-Antworten werden wiederholt, und Netzwerkfehler werden bei Lesezugriffen wiederholt.
- **Klare Fehler.** Eine ungeprüfte Exception mit stabiler Art, dem Fehlercode der API, Validierungsfehlern auf Feldebene, der Correlation-ID und einem Hinweis.
- **Modernes Java.** Java 17+, Records, verzögerte Paginierung mit `Stream`, `CompletableFuture`-Varianten, ein threadsicherer Client auf Basis von `java.net.http`.
- **Privat.** Keine Telemetrie. Nichts verlässt Ihren Prozess außer den Requests, die Sie an die API senden.

Es ist das Java-Pendant zu [`dna`, der Domain Name API CLI](https://github.com/domainreseller/cli): dasselbe API-Dokument,
dieselben Sicherheitsklassen, Wiederholungsregeln und Fehlercodes.

## Inhalt

- [Installation](#install)
- [Zugangsdaten](#credentials)
- [Schnellstart](#quick-start)
- [Konfiguration](#configuration)
- [Verwendung](#usage): [Konto](#account) · [Domains](#domains) · [Kontakte](#contacts) · [DNS](#dns) · [DNSSEC](#dnssec) · [SSL](#ssl) · [Hosting](#hosting) · [Bestellungen und Produkte](#orders-and-products)
- [Requests und `BaseRequest`](#requests-and-baserequest)
- [Fehler](#errors)
- [Sicherheit, Wiederholungen und Rate-Limits](#safety-retries-and-rate-limits)
- [Asynchrone Aufrufe](#asynchronous-calls)
- [Ihren Code testen](#testing-your-code)
- [Projektstruktur](#project-layout)
- [Entwicklung](#development)
- [Umstieg von 1.x](#upgrading-from-1x)

<a id="install"></a>

## Installation

> [!IMPORTANT]
> **Verwenden Sie immer das [neueste Release](https://github.com/domainreseller/java-dna/releases/latest).** Release-Pakete sind versioniert und getestet.
> Der grüne Button **Code → Download ZIP** lädt stattdessen den Entwicklungszweig herunter.

Koordinaten: `io.github.domainreseller:domainnameapi:2.0.0`.

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

Solange das Artefakt nicht in einem öffentlichen Repository verfügbar ist, installieren Sie es aus dem Quellcode eines
Releases in Ihr lokales Maven-Repository (`mvn -DskipTests install`) und fügen Sie `mavenLocal()` zu Ihren Gradle-Repositories hinzu.

**Voraussetzungen:** Java 17 oder neuer. Die einzige Abhängigkeit ist [Jackson](https://github.com/FasterXML/jackson-databind)
(`jackson-databind`) für JSON; HTTP läuft über den `java.net.http.HttpClient` des JDK.

<a id="credentials"></a>

## Zugangsdaten

Die API authentifiziert jeden Request mit zwei Werten, die das SDK für Sie sendet:

| Wert | Header | Fundort |
|---|---|---|
| Reseller-ID (eine UUID) | `__reseller` | Reseller-Panel → **API-Einstellungen** |
| API-Key | `X-API-KEY` | Reseller-Panel → **API-Einstellungen** |

> [!NOTE]
> Dies sind **API-Zugangsdaten**: Ihre Panel-Anmeldung funktioniert hier nicht. **Produktion und OTE (die Testumgebung) verwenden
> unterschiedliche API-Keys.** Wird der eine mit der jeweils anderen Umgebung verwendet, schlägt der Aufruf mit `INVALID_API_KEY` oder `INVALID_RESELLER` fehl.

<a id="quick-start"></a>

## Schnellstart

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

Der Ordner [examples](examples) enthält lauffähige Programme für diese Szenarien. Sie lesen die Umgebungsvariablen
`DNA_*`, richten Sie sie also auf OTE aus:

```bash
export DNA_RESELLER_ID=... DNA_API_KEY=... DNA_ENV=ote
```

<a id="configuration"></a>

## Konfiguration

Es gibt vier Wege, einen Client zu erstellen:

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

| Builder-Methode | Standard | Bedeutung |
|---|---|---|
| `credentials(resellerId, apiKey)` | (erforderlich) | Die Zugangsdaten. |
| `environment(...)` | `PRODUCTION` | `PRODUCTION` (`https://api.domainresellerapi.com`) oder `OTE` (`https://ote.domainresellerapi.com`). |
| `baseUri(...)` | keine | Überschreibt die Adresse der Umgebung, zum Beispiel für einen Proxy. |
| `timeout(...)` | 60 s | Zeitlimit eines einzelnen Versuchs. |
| `maxRetries(...)` | 2 | Wie oft ein fehlgeschlagener Request wiederholt wird (siehe [Wiederholungen](#safety-retries-and-rate-limits)). 0 schaltet Wiederholungen aus. |
| `maxRetryDelay(...)` | 30 s | Die längste Wartezeit vor einer Wiederholung. |
| `paceRequests(...)` | `true` | Verteilt Requests zeitlich, um unter den Rate-Limits des Gateways zu bleiben. |
| `readOnly(...)` | `false` | Blockiert vor dem Senden jeden Request, der kein Lesezugriff ist. |
| `userAgent(...)` | `DomainNameAPI-Java/<version>` | Der `User-Agent`-Header. |
| `app(name, version)` | `java`, die SDK-Version | Die Anwendung, die das SDK nutzt, gesendet als `X-App` und `X-Version` (siehe [unten](#ihre-anwendung-angeben)). |
| `httpClient(...)` | der eigene des SDK | Ihr eigener `java.net.http.HttpClient` (Proxy, TLS, Executor). |
| `executor(...)` | ein gemeinsamer Daemon-Pool | Wo die `sendAsync`-Methoden ausgeführt werden. |

`fromEnvironment()` liest:

| Variable | Bedeutung |
|---|---|
| `DNA_RESELLER_ID`, `DNA_API_KEY` | Die Zugangsdaten. |
| `DNA_ENV` | `production` oder `ote` (auch `prod`, `live`, `test`, `sandbox`). |
| `DNA_TEST_MODE` | `true`, `1`, `on` oder `yes` wählt OTE. Wird verwendet, wenn `DNA_ENV` nicht gesetzt ist. |
| `DNA_BASE_URL` | Optionale Überschreibung der Basisadresse. |
| `DNA_READ_ONLY` | `true` schaltet den Nur-Lese-Modus ein. |
| `DNA_APP`, `DNA_APP_VERSION` | Die Anwendung, die das SDK nutzt, und ihre Version (`X-App`, `X-Version`). |

### Ihre Anwendung angeben

Jede Anfrage trägt zwei Header, die der API sagen, welche Integration sie gesendet hat: `X-App`, der Anwendungsname in
Kleinbuchstaben, von dem nur `a-z`, `0-9`, `.`, `_` und `-` übrig bleiben, und `X-Version`, ihre Version aus Ziffern und
Punkten. Standardmäßig sind das `java` und die SDK-Version. Geben Sie Ihr Panel oder Modul mit `app(name, version)` oder
`DNA_APP`/`DNA_APP_VERSION` an; ein leerer oder ungültiger Name ergibt `java`, eine leere Version die SDK-Version. Sonst
wird nichts gesendet, und der `User-Agent` bleibt unverändert.

```java
DomainNameApiClient client = DomainNameApiClient.builder()
        .credentials(resellerId, apiKey)
        .app("WiseCP", "V2.0.20-beta")   // sends X-App: wisecp, X-Version: 2.0.20
        .build();
```

Der Client ist threadsicher. Erstellen Sie pro Satz Zugangsdaten einen Client und verwenden Sie ihn für die Lebensdauer der Anwendung weiter.

<a id="usage"></a>

## Verwendung

Jeder Bereich der API hat einen eigenen Client. Die Methoden nehmen einfache Argumente entgegen; für weitere Optionen hat jede Methode auch
eine Überladung (oder eine Schwestermethode), die das Request-Objekt annimmt. Methoden, die Ihr Guthaben belasten, weisen in ihrem Javadoc darauf hin.

<a id="account"></a>

### Konto

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

### Domains

**Suche**

```java
DomainSearchResult one = client.domains().search("example.com", "USD");
boolean available = one.info() != null && one.info().isAvailable();

BulkSearchResult many = client.domains().bulkSearch(List.of("example.com", "example.net", "example.com.tr"));
for (DomainAvailability domain : many.available()) {
    System.out.println(domain.domainName() + ": " + domain.price() + " " + domain.currency());
}
```

**Liste und Details**

```java
Page<Domain> page = client.domains().list(new ListDomainsRequest().maxResultCount(50).statuses(DomainStatus.ACTIVE));

client.domains().listAll(new ListDomainsRequest().domainName("example"))
        .forEach(d -> System.out.println(d.domainName() + " " + d.status() + " " + d.expirationDate()));

DomainDetails details = client.domains().get("example.com");
System.out.println(details.authCode());
details.contact(ContactType.REGISTRANT).ifPresent(c -> System.out.println(c.email()));
```

**Registrieren, verlängern, transferieren, wiederherstellen** (diese belasten Ihr Guthaben)

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

Requests werden vor dem Senden geprüft. Eine Registrierung ohne alle vier Kontaktrollen, ein Kontakt ohne E-Mail-Adresse
oder eine Laufzeit von 0 Jahren löst eine `IllegalArgumentException` aus, und nichts erreicht die API.

> [!NOTE]
> Die API kann eine Registrierung, Verlängerung oder einen Transfer mit HTTP 200 und `"success": false` beantworten (zum Beispiel, wenn die
> Registry die Domain ablehnt). Das SDK gibt diese Antwort unverändert zurück: Prüfen Sie `DomainOperationResult.success()` und
> `message()`.

**Einstellungen**

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

**URL-Weiterleitung, Child-Nameserver und Dokumente**

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

### Kontakte

Kontakt-Handles sind wiederverwendbare Kontakte. Legen Sie einen einmal an und verwenden Sie ihn für viele Domains.

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

Zoneneinträge von Domains auf den Nameservern der Plattform. Die Einträge sind öffentlich auflösbar, sobald die Domain diese Nameserver verwendet,
worum sich `apply` kümmert.

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

Das SDK prüft, was die API ablehnen würde: einen CNAME am Apex, einen CAA-Wert ohne seine drei Bestandteile, eine TTL außerhalb von
1–86400. Zonenaufrufe werden im Abstand von 1 Sekunde gesendet, dem Limit des Gateways.

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

Eine Domain kann mehrere Zertifikate haben. Ein bestehendes Zertifikat wird deshalb über seine **Zertifikats-ID**
(eine `UUID`) angesprochen, nie über den Domainnamen. `purchase` und `renew` geben die ID zurück: Speichern Sie sie.
`list` findet die IDs der Zertifikate, die Sie bereits haben.

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

**Zertifikate auflisten und eine ID finden**

```java
Page<CertificateListItem> page = client.ssl().list(new ListCertificatesRequest().domainName("example.com"));   // newest first

client.ssl().listAll().forEach(c ->
        System.out.println(c.certificateId() + " " + c.domainName() + " " + c.status() + " " + c.subscriptionExpiresAt()));

// The approver addresses of a certificate you already have
List<String> approverEmails = client.ssl().getApproverEmails(certificateId);
```

**Verlängern** (dies belastet Ihr Guthaben)

```java
// Allowed from 30 days before details.subscription().expiresAt(). The renewal is a new certificate with its own ID.
SslOperationResult renewal = client.ssl().renew(certificateId);
client.ssl().configure(new ConfigureCertificateRequest(renewal.certificateId(), newCsr, certificateContact)
        .validationMethod(ValidationMethod.EMAIL)
        .approverEmail(approvers.get(0))
        .webServerType(WebServerType.APACHE));
```

Ein gekauftes oder verlängertes Zertifikat muss innerhalb von 15 Tagen konfiguriert werden; danach wird es storniert.
Zum Verlängern rufen Sie `renew` und anschließend `configure` mit der neuen ID auf. Kaufen Sie nicht erneut
mit `purchase`: Das ist ein eigenständiger Kauf zum Kaufpreis, und das alte Zertifikat wird nie als verlängert
markiert. Eine leere Zertifikats-ID löst eine `IllegalArgumentException` aus, bevor etwas gesendet wird.

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

### Bestellungen und Produkte

```java
Order order = client.orders().get("ORDER-CODE");

client.products().getTld("com").ifPresent(com -> System.out.println(
        com.registrationPrice(1).map(Tld.Price::price).orElse(null) + ", DNSSEC: " + com.supportsDnssec()));

client.products().listAllTlds(new ListTldsRequest().currency("USD")).forEach(tld -> System.out.println(tld.name()));

ProductPrice price = client.products().getPrice("com", OrderType.RENEW, 1);
Page<SslProduct> ssl = client.products().listSslProducts(new ListSslProductsRequest().isWildcard(true));
```

<a id="requests-and-baserequest"></a>

## Requests und `BaseRequest`

Jede Operation ist eine Request-Klasse, zum Beispiel `RenewDomainRequest`, `ListDomainsRequest` oder `AddDnsRecordRequest`.
Die oben gezeigten Client-Methoden erstellen sie für Sie, und Sie können sie auch selbst erstellen und senden:

```java
RenewDomainRequest request = new RenewDomainRequest("example.com", 2);
System.out.println(request + " is " + request.safety());   // "POST domains/renew is BILLABLE"

DomainOperationResult result = client.send(request);

// The same, with the HTTP details: correlation ID, soft-quota state, attempts, duration
ApiResponse<DomainOperationResult> response = client.sendWithResponse(request);
System.out.println(response.correlationId() + " quota " + response.quota());
```

Alle Requests erben von einer von zwei Basisklassen:

| Basisklasse | Wofür |
|---|---|
| `BaseRequest<T>` | Jede Operation. `T` ist der Antworttyp; `Void` für Operationen, die nichts zurückgeben (sperren, entsperren, einen DNS-Eintrag hinzufügen, ...). |
| `PagedRequest<T>` | Listenoperationen. Ergänzt `skipCount`, `maxResultCount` und `sorting` und funktioniert mit `listAll`. |

Ein Request beschreibt sich selbst über einige Overrides: `method()`, `path()` (unterhalb von `/api/v1/`) und `safety()`. Query-Parameter
werden in `addQueryParameters` hinzugefügt, der JSON-Body kommt aus `body()` (`JsonBody` erstellt einen und lässt Nullwerte
weg), und Prüfungen vor dem Senden gehören in `validate()`. Um einen Endpunkt aufzurufen, den das SDK noch nicht abdeckt, schreiben Sie einen eigenen Request:

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

Ihr Request erhält alles, was die eingebauten erhalten: Zugangsdaten, Wiederholungen nach Sicherheitsklasse, Pacing, Nur-Lese-Modus und
Fehlerbehandlung. Der Antworttyp wird aus `extends BaseRequest<DomainNotes>` gelesen; überschreiben Sie `responseType()` nur bei
generischen Request-Klassen. Überschreiben Sie `isIdempotent()`, wenn ein Schreibzugriff sicher wiederholt werden kann.

<a id="errors"></a>

## Fehler

Jeder fehlgeschlagene Request löst eine `DomainNameApiException` (ungeprüft) aus. Ungültige Argumente lösen eine
`IllegalArgumentException` aus, bevor etwas gesendet wird.

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

| Methode | Bedeutung |
|---|---|
| `kind()` | Eine stabile Klassifizierung (Tabelle unten). Verzweigen Sie danach, nicht nach der Meldung. |
| `code()` | Der Fehlercode der API (etwa `Dna.DomainService:Domain:10007`) oder einer der unten genannten Codes des SDK. |
| `getMessage()` | Was passiert ist. Standardmeldungen werden durch die Details der API ersetzt. |
| `statusCode()` | Der HTTP-Status, oder null, wenn keine Antwort eingetroffen ist. |
| `details()`, `validationErrors()` | Zusätzliche Details und Probleme auf Feldebene von der API. |
| `correlationId()` | Die `X-Correlation-Id` des Gateways. Nennen Sie sie, wenn Sie den Support kontaktieren. |
| `hint()` | Ein kurzer Vorschlag, was als Nächstes zu tun ist. |
| `isRetryable()` | Ob die spätere Wiederholung desselben Requests erfolgreich sein kann. |
| `isOutcomeUnknown()` | Ein Request, der etwas ändert, ist im Netzwerk fehlgeschlagen und wurde möglicherweise ausgeführt. |
| `operation()`, `safety()` | Der fehlgeschlagene Request (`POST domains/renew`) und seine Sicherheitsklasse. |
| `retryAfter()`, `responseBody()` | Das `Retry-After` des Gateways und die rohe Antwort. |

| `kind()` | Wann |
|---|---|
| `AUTHENTICATION` | Falsche oder fehlende Zugangsdaten oder ein unbekannter oder gesperrter Reseller in dieser Umgebung (401, Weiterleitung). |
| `FORBIDDEN` | Authentifiziert, aber nicht berechtigt (403). |
| `NOT_FOUND` | Die Domain, der Kontakt, die Bestellung oder eine andere Ressource existiert nicht (404). |
| `VALIDATION` | Die API hat die Eingabe abgelehnt oder eine Geschäftsregel ist fehlgeschlagen: ungültiges Feld, Domain gesperrt, unzureichendes Guthaben (400, 422). |
| `CONFLICT` | Der Zustand der Ressource lässt die Operation nicht zu (409). |
| `RATE_LIMITED` | Auch nach den automatischen Wiederholungen weiterhin durch das Rate-Limit begrenzt (429). |
| `SERVER` | Ein serverseitiger Fehler (5xx). Oft vorübergehend. |
| `NETWORK`, `TIMEOUT` | Keine Antwort: DNS, TLS, Proxy, zurückgesetzte Verbindung oder keine Antwort innerhalb des Timeouts. |
| `READ_ONLY` | Vom Nur-Lese-Modus blockiert; nichts wurde gesendet. |
| `UNKNOWN` | Alles andere, einschließlich einer Antwort, die das SDK nicht lesen konnte. |

Codes, die das SDK selbst setzt:

| Code | Bedeutung |
|---|---|
| `INVALID_RESELLER` | Das Gateway hat auf seine Sperrseite weitergeleitet: Die Reseller-ID ist **in dieser Umgebung** unbekannt, inaktiv oder gesperrt. |
| `INVALID_API_KEY` | Der Key passt nicht zum Reseller. Keys für Produktion und OTE unterscheiden sich. |
| `MISSING_API_KEY`, `MISSING_RESELLER_ID`, `NO_CREDENTIALS` | Ein Zugangswert ist leer. |
| `SUBRESELLER_NOT_ALLOWED` | Sub-Reseller-Konten können die API nicht verwenden. Verwenden Sie die Zugangsdaten des Haupt-Resellers. |
| `UNAUTHORIZED` | Jedes andere 401. |
| `RATE_LIMITED` | 429 nach den Wiederholungen. |
| `NETWORK`, `TIMEOUT` | Es ist keine Antwort eingetroffen. |
| `READ_ONLY` | Vom Nur-Lese-Modus blockiert. |
| `EMPTY_RESPONSE`, `INVALID_RESPONSE` | Eine erfolgreiche Antwort war leer oder konnte nicht gelesen werden. |
| `HTTP_<status>` | Die API hat keinen Fehlercode gesendet. |

Wird der Thread unterbrochen, während ein Request wartet, setzt das SDK das Interrupt-Flag erneut und löst statt eines
API-Fehlers eine `CancellationException` aus.

<a id="safety-retries-and-rate-limits"></a>

## Sicherheit, Wiederholungen und Rate-Limits

Jeder Request hat eine Sicherheitsklasse, dieselbe, die die `dna` CLI verwendet:

| `OperationSafety` | Bedeutet | Beispiele |
|---|---|---|
| `READ` | Keine Seiteneffekte, auch wenn die API POST oder PUT verwendet | Suche, Details, Listen, Verlängerungs- und Transferprüfungen, CSR-Erzeugung |
| `WRITE` | Eine kostenlose Änderung, die sich rückgängig machen lässt | DNS-Einträge, Sperre, Datenschutz, Nameserver, Kontakte |
| `BILLABLE` | Belastet Ihr Guthaben | Registrierung, Verlängerung, Transfer, Wiederherstellung, SSL-Kauf und -Verlängerung, Hosting-Bestellung |
| `DESTRUCTIVE` | Löscht etwas oder lässt sich kaum rückgängig machen | Löschen von DNS-Einträgen, Entfernen von DNSSEC, Widerruf von Zertifikaten |

- **Nur-Lese-Modus** (`readOnly(true)` oder `DNA_READ_ONLY=true`) blockiert vor dem Senden alles, was kein `READ` ist.
  Verwenden Sie ihn beim Erkunden, in Berichten und in Dashboards.
- **Wiederholungen.** 429-Antworten werden immer wiederholt, wobei `Retry-After` beachtet wird. Das ist auch bei kostenpflichtigen Requests sicher, weil
  das Gateway sie ablehnt, bevor etwas ausgeführt wird. Netzwerkfehler, Timeouts und 502/503/504-Antworten werden nur bei
  Lesezugriffen wiederholt (und bei Schreibzugriffen, die sich sicher wiederholen lassen, etwa dem Ersetzen von Nameservern). Eine Registrierung oder Verlängerung wird nie zweimal gesendet.
- **Pacing.** Das Gateway erlaubt 5 Requests pro Sekunde und Key, 2 pro Sekunde für Domainsuchen und 20 pro Sekunde für
  DNS-Zonenaufrufe. Der Client verteilt Requests zeitlich, um unter diesen Limits zu bleiben (Zonenaufrufe im Abstand von 1 Sekunde). Domain-Infos und die
  TLD-Liste unterliegen keinem Rate-Limit.
- **Soft-Quota.** Suchen und Info-Abfragen kosten 1 Punkt, alles andere 10. Ab 200 Punkten verlangsamt das Gateway jeden
  Request um bis zu 5 Sekunden; die Punkte verfallen um 20 % pro Minute. `sendWithResponse` zeigt den Zustand in `quota()`.

<a id="asynchronous-calls"></a>

## Asynchrone Aufrufe

Der Client ist synchron: Jede Methode blockiert, bis die Antwort eintrifft, was zu Servercode und virtuellen Threads passt.
`sendAsync` und `sendWithResponseAsync` führen einen Request auf dem Executor des Clients aus und geben eine `CompletableFuture` zurück:

```java
CompletableFuture<DomainDetails> details = client.sendAsync(new GetDomainRequest("example.com"));
details.thenAccept(d -> System.out.println(d.expirationDate()));
```

Die Future schlägt mit denselben Exceptions fehl, die die synchronen Methoden auslösen (von `join()` in eine
`CompletionException` verpackt). Legen Sie mit `builder().executor(...)` einen eigenen Executor fest, zum Beispiel
`Executors.newVirtualThreadPerTaskExecutor()` unter Java 21.

<a id="testing-your-code"></a>

## Ihren Code testen

Das SDK benötigt nur einen HTTP-Endpunkt. Um Code, der es verwendet, zu testen, ohne die API aufzurufen, richten Sie `baseUri` auf einen lokalen
Server, zum Beispiel den im JDK eingebauten:

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

Sie können auch mit `httpClient(...)` Ihre eigene `HttpClient`-Unterklasse übergeben und Exceptions für Ihre Mocks mit
`DomainNameApiException.builder(...)` erstellen. Für Tests gegen die echte API verwenden Sie OTE mit dem zugehörigen eigenen Key.

<a id="project-layout"></a>

## Projektstruktur

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

Jedes Bereichspaket hat denselben Aufbau: einen `XxxClient`, eine Request-Klasse pro Operation (`XxxRequest`) sowie die Modelle
und Enums seiner Antworten.

<a id="development"></a>

## Entwicklung

```bash
mvn verify                          # build and run the tests
mvn -Pexamples -DskipTests compile  # also compile the examples
```

Ohne lokales JDK verwenden Sie Docker:

```bash
docker run --rm -v "$PWD":/src -w /src maven:3.9-eclipse-temurin-17 mvn -B verify
```

Die Tests rufen nie die echte API auf: Sie verwenden einen gefälschten `HttpClient` sowie für den Transport einen lokalen
HTTP-Server auf 127.0.0.1. Die CI führt sie auf Java 17, 21 und 25 aus.

`src/test/resources/openapi.json` ist das API-Dokument, das die `dna` CLI einbettet. `SpecCoverageTest` prüft, dass jede
darin enthaltene Operation genau eine Request-Klasse mit der Sicherheitsklasse der CLI hat und dass jeder Request nur Query-Parameter
und Body-Eigenschaften sendet, die das Dokument beschreibt. Wenn die API eine Operation erhält, aktualisieren Sie das Dokument, und der
Test schlägt so lange fehl, bis der Request existiert.

<a id="upgrading-from-1x"></a>

## Umstieg von 1.x

2.0 ist ein neues SDK. 1.x war eine Portierung der PHP-Bibliothek und gab untypisierte `Map`-Umschläge zurück; 2.0 hat typisierte Requests und
Modelle, Exceptions und einen Builder.

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

Die Java-Mindestversion steigt von 11 auf 17, und Jackson ist jetzt eine Abhängigkeit. 1.x hat Fehlerberichte an Sentry gesendet; 2.0 sendet
nirgendwohin etwas, außer an die API.

## Links

- [`dna` CLI und MCP-Server](https://github.com/domainreseller/cli): dieselbe API vom Terminal und von KI-Agenten aus.
- [API-Dokumentation](https://www.domainnameapi.com/docs/)
- [Changelog](CHANGELOG.md)

## Lizenz

Apache License 2.0. Siehe [LICENSE](LICENSE).
