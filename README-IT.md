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

L'SDK Java ufficiale per la piattaforma reseller di [Domain Name API](https://www.domainnameapi.com). Copre tutte le
67 operazioni dell'API REST con client e modelli tipizzati: domini, contatti, DNS, DNSSEC, certificati SSL,
hosting, ordini, prodotti e il tuo account.

```java
DomainNameApiClient client = DomainNameApiClient.create(resellerId, apiKey, ApiEnvironment.OTE);

DomainSearchResult search = client.domains().search("example.com");
if (search.info() != null && search.info().isAvailable()) {
    client.domains().register("example.com", contact, List.of("tr.apiname.com", "eu.apiname.com"));
}
```

- **Tutto tipizzato.** Ogni operazione è una classe di richiesta e ogni risposta un record con Javadoc. Niente mappe, niente parsing JSON.
- **Le tue richieste.** Ogni richiesta estende `BaseRequest`. Creane una sottoclasse per un endpoint che l'SDK non copre ancora.
- **Sicuro con il denaro.** Ogni richiesta dichiara se legge, scrive, addebita il tuo saldo o elimina qualcosa. Le richieste a pagamento non vengono mai ripetute e la modalità di sola lettura blocca qualsiasi modifica.
- **Resilienza integrata.** I limiti di richieste vengono rispettati automaticamente, le risposte 429 vengono ritentate e gli errori di rete vengono ritentati per le letture.
- **Errori chiari.** Una sola eccezione unchecked con un tipo stabile, il codice di errore dell'API, gli errori di validazione per campo, il correlation ID e un suggerimento.
- **Java moderno.** Java 17+, record, paginazione lazy con `Stream`, varianti `CompletableFuture`, un client thread-safe basato su `java.net.http`.
- **Riservato.** Nessuna telemetria. Nulla lascia il tuo processo, tranne le richieste che invii all'API.

È la controparte Java di [`dna`, la CLI di Domain Name API](https://github.com/domainreseller/cli): stesso documento
API, stesse classi di sicurezza, stesse regole di retry e stessi codici di errore.

## Indice

- [Installazione](#install)
- [Credenziali](#credentials)
- [Avvio rapido](#quick-start)
- [Configurazione](#configuration)
- [Utilizzo](#usage): [account](#account) · [domini](#domains) · [contatti](#contacts) · [DNS](#dns) · [DNSSEC](#dnssec) · [SSL](#ssl) · [hosting](#hosting) · [ordini e prodotti](#orders-and-products)
- [Richieste e `BaseRequest`](#requests-and-baserequest)
- [Errori](#errors)
- [Sicurezza, retry e limiti di richieste](#safety-retries-and-rate-limits)
- [Chiamate asincrone](#asynchronous-calls)
- [Testare il tuo codice](#testing-your-code)
- [Struttura del progetto](#project-layout)
- [Sviluppo](#development)
- [Passare dalla 1.x](#upgrading-from-1x)

<a id="install"></a>

## Installazione

> [!IMPORTANT]
> **Usa sempre l'[ultima release](https://github.com/domainreseller/java-dna/releases/latest).** I pacchetti delle release sono versionati e testati.
> Il pulsante verde **Code → Download ZIP** scarica invece il branch di sviluppo.

Coordinate: `io.github.domainreseller:domainnameapi:2.1.0`.

```xml
<dependency>
    <groupId>io.github.domainreseller</groupId>
    <artifactId>domainnameapi</artifactId>
    <version>2.1.0</version>
</dependency>
```

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.github.domainreseller:domainnameapi:2.1.0")
}
```

Finché l'artefatto non è disponibile in un repository pubblico, installalo nel tuo repository Maven locale a partire dai
sorgenti della release (`mvn -DskipTests install`) e aggiungi `mavenLocal()` ai repository di Gradle.

**Requisiti:** Java 17 o successivo. L'unica dipendenza è [Jackson](https://github.com/FasterXML/jackson-databind)
(`jackson-databind`) per il JSON; l'HTTP passa per `java.net.http.HttpClient` del JDK.

<a id="credentials"></a>

## Credenziali

L'API autentica ogni richiesta con due valori, che l'SDK invia per te:

| Valore | Header | Dove trovarlo |
|---|---|---|
| Reseller ID (un UUID) | `__reseller` | Pannello reseller → **API settings** |
| API key | `X-API-KEY` | Pannello reseller → **API settings** |

> [!NOTE]
> Queste sono **credenziali API**: il login del pannello qui non funziona. **Produzione e OTE (l'ambiente di test) usano
> chiavi API diverse.** Usare l'una con l'altro fallisce con `INVALID_API_KEY` o `INVALID_RESELLER`.

<a id="quick-start"></a>

## Avvio rapido

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

La cartella [examples](examples) contiene programmi eseguibili per questi scenari. Leggono le variabili d'ambiente
`DNA_*`, quindi puntale su OTE:

```bash
export DNA_RESELLER_ID=... DNA_API_KEY=... DNA_ENV=ote
```

<a id="configuration"></a>

## Configurazione

Ci sono quattro modi per creare un client:

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

| Metodo del builder | Predefinito | Significato |
|---|---|---|
| `credentials(resellerId, apiKey)` | (obbligatorio) | Le credenziali. |
| `environment(...)` | `PRODUCTION` | `PRODUCTION` (`https://api.domainresellerapi.com`) oppure `OTE` (`https://ote.domainresellerapi.com`). |
| `baseUri(...)` | nessuno | Sostituisce l'indirizzo dell'ambiente, ad esempio per un proxy. |
| `timeout(...)` | 60 s | Tempo massimo di un singolo tentativo. |
| `maxRetries(...)` | 2 | Quante volte una richiesta fallita viene ripetuta (vedi [retry](#safety-retries-and-rate-limits)). 0 disattiva i retry. |
| `maxRetryDelay(...)` | 30 s | L'attesa massima prima di un retry. |
| `paceRequests(...)` | `true` | Distanzia le richieste per restare entro i limiti di richieste del gateway. |
| `readOnly(...)` | `false` | Blocca, prima dell'invio, ogni richiesta che non sia una lettura. |
| `userAgent(...)` | `DomainNameAPI-Java/<version>` | L'header `User-Agent`. |
| `app(name, version)` | `java`, la versione dell'SDK | L'applicazione che usa l'SDK, inviata come `X-App` e `X-Version` (vedi [sotto](#identificare-la-tua-applicazione)). |
| `httpClient(...)` | quello dell'SDK | Il tuo `java.net.http.HttpClient` (proxy, TLS, executor). |
| `executor(...)` | un pool daemon condiviso | Dove vengono eseguiti i metodi `sendAsync`. |

`fromEnvironment()` legge:

| Variabile | Significato |
|---|---|
| `DNA_RESELLER_ID`, `DNA_API_KEY` | Le credenziali. |
| `DNA_ENV` | `production` oppure `ote` (anche `prod`, `live`, `test`, `sandbox`). |
| `DNA_TEST_MODE` | `true`, `1`, `on` o `yes` seleziona OTE. Usata quando `DNA_ENV` non è impostata. |
| `DNA_BASE_URL` | Sostituzione facoltativa dell'indirizzo base. |
| `DNA_READ_ONLY` | `true` attiva la modalità di sola lettura. |
| `DNA_APP`, `DNA_APP_VERSION` | L'applicazione che usa l'SDK e la sua versione (`X-App`, `X-Version`). |

### Identificare la tua applicazione

Ogni richiesta porta due header che dicono all'API quale integrazione l'ha inviata: `X-App`, il nome dell'applicazione
in minuscolo di cui restano solo `a-z`, `0-9`, `.`, `_` e `-`, e `X-Version`, la sua versione fatta solo di cifre e
punti. Per impostazione predefinita sono `java` e la versione dell'SDK. Indica il tuo pannello o modulo con `app(name,
version)` oppure `DNA_APP`/`DNA_APP_VERSION`; un nome vuoto o non valido torna a `java`, una versione vuota alla
versione dell'SDK. Non viene inviato nient'altro e lo `User-Agent` non cambia.

```java
DomainNameApiClient client = DomainNameApiClient.builder()
        .credentials(resellerId, apiKey)
        .app("WiseCP", "V2.0.20-beta")   // sends X-App: wisecp, X-Version: 2.0.20
        .build();
```

Il client è thread-safe. Creane uno per ogni set di credenziali e riusalo per tutta la vita dell'applicazione.

<a id="usage"></a>

## Utilizzo

Ogni area dell'API ha un proprio client. I metodi accettano argomenti semplici; per più opzioni, ogni metodo ha anche un
overload (o un metodo affine) che accetta l'oggetto richiesta. I metodi che addebitano il tuo saldo lo indicano nel loro Javadoc.

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

### Domini

**Ricerca**

```java
DomainSearchResult one = client.domains().search("example.com", "USD");
boolean available = one.info() != null && one.info().isAvailable();

BulkSearchResult many = client.domains().bulkSearch(List.of("example.com", "example.net", "example.com.tr"));
for (DomainAvailability domain : many.available()) {
    System.out.println(domain.domainName() + ": " + domain.price() + " " + domain.currency());
}
```

**Elenco e dettagli**

```java
Page<Domain> page = client.domains().list(new ListDomainsRequest().maxResultCount(50).statuses(DomainStatus.ACTIVE));

client.domains().listAll(new ListDomainsRequest().domainName("example"))
        .forEach(d -> System.out.println(d.domainName() + " " + d.status() + " " + d.expirationDate()));

DomainDetails details = client.domains().get("example.com");
System.out.println(details.authCode());
details.contact(ContactType.REGISTRANT).ifPresent(c -> System.out.println(c.email()));
```

**Registrazione, rinnovo, trasferimento, ripristino** (addebitano il tuo saldo)

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

Le richieste vengono controllate prima dell'invio. Una registrazione senza tutti e quattro i ruoli di contatto, un contatto senza indirizzo
e-mail o un periodo di 0 anni lanciano un `IllegalArgumentException` e nulla raggiunge l'API.

> [!NOTE]
> L'API può rispondere a una registrazione, a un rinnovo o a un trasferimento con HTTP 200 e `"success": false` (ad esempio quando il
> registry rifiuta il dominio). L'SDK restituisce quella risposta così com'è: controlla `DomainOperationResult.success()` e
> `message()`.

**Impostazioni**

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

**Inoltro URL, nameserver figli e documenti**

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

### Contatti

Gli handle di contatto sono contatti riutilizzabili. Creane uno una volta e usalo per molti domini.

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

Record di zona dei domini sui nameserver della piattaforma. I record si risolvono pubblicamente quando il dominio usa quei nameserver,
cosa di cui si occupa `apply`.

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

L'SDK controlla ciò che l'API rifiuterebbe: un CNAME all'apex, un valore CAA senza le sue tre parti, un TTL fuori dall'intervallo
1–86400. Le chiamate alla zona sono distanziate di 1 secondo, il limite del gateway.

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

Un dominio può avere più certificati, quindi un certificato esistente si indica con il suo **ID del certificato**
(un `UUID`), mai con il nome di dominio. `purchase` e `renew` restituiscono l'ID: conservalo. `list` trova
gli ID dei certificati che hai già.

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

**Elencare i certificati e trovare un ID**

```java
Page<CertificateListItem> page = client.ssl().list(new ListCertificatesRequest().domainName("example.com"));   // newest first

client.ssl().listAll().forEach(c ->
        System.out.println(c.certificateId() + " " + c.domainName() + " " + c.status() + " " + c.subscriptionExpiresAt()));

// The approver addresses of a certificate you already have
List<String> approverEmails = client.ssl().getApproverEmails(certificateId);
```

**Rinnovo** (addebita il tuo saldo)

```java
// Allowed from 30 days before details.subscription().expiresAt(). The renewal is a new certificate with its own ID.
SslOperationResult renewal = client.ssl().renew(certificateId);
client.ssl().configure(new ConfigureCertificateRequest(renewal.certificateId(), newCsr, certificateContact)
        .validationMethod(ValidationMethod.EMAIL)
        .approverEmail(approvers.get(0))
        .webServerType(WebServerType.APACHE));
```

Un certificato acquistato o rinnovato va configurato entro 15 giorni; dopo viene annullato. Per rinnovare, chiama
`renew` e poi `configure` con il nuovo ID. Non acquistare di nuovo con `purchase`: sarebbe un acquisto separato al
prezzo di acquisto e il vecchio certificato non verrebbe mai contrassegnato come rinnovato. Un ID del certificato vuoto
lancia un `IllegalArgumentException` prima che venga inviato qualcosa.

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

### Ordini e prodotti

```java
Order order = client.orders().get("ORDER-CODE");

client.products().getTld("com").ifPresent(com -> System.out.println(
        com.registrationPrice(1).map(Tld.Price::price).orElse(null) + ", DNSSEC: " + com.supportsDnssec()));

client.products().listAllTlds(new ListTldsRequest().currency("USD")).forEach(tld -> System.out.println(tld.name()));

ProductPrice price = client.products().getPrice("com", OrderType.RENEW, 1);
Page<SslProduct> ssl = client.products().listSslProducts(new ListSslProductsRequest().isWildcard(true));
```

<a id="requests-and-baserequest"></a>

## Richieste e `BaseRequest`

Ogni operazione è una classe di richiesta, ad esempio `RenewDomainRequest`, `ListDomainsRequest` o `AddDnsRecordRequest`.
I metodi del client qui sopra le costruiscono per te, ma puoi anche crearle e inviarle tu stesso:

```java
RenewDomainRequest request = new RenewDomainRequest("example.com", 2);
System.out.println(request + " is " + request.safety());   // "POST domains/renew is BILLABLE"

DomainOperationResult result = client.send(request);

// The same, with the HTTP details: correlation ID, soft-quota state, attempts, duration
ApiResponse<DomainOperationResult> response = client.sendWithResponse(request);
System.out.println(response.correlationId() + " quota " + response.quota());
```

Tutte le richieste estendono una di due classi base:

| Classe base | Per |
|---|---|
| `BaseRequest<T>` | Ogni operazione. `T` è il tipo della risposta; `Void` per le operazioni che non restituiscono nulla (blocco, sblocco, aggiunta di un record DNS, ...). |
| `PagedRequest<T>` | Operazioni di elenco. Aggiunge `skipCount`, `maxResultCount` e `sorting`, e funziona con `listAll`. |

Una richiesta dichiara cosa è con alcuni override: `method()`, `path()` (sotto `/api/v1/`) e `safety()`. I parametri
di query si aggiungono in `addQueryParameters`, il corpo JSON viene da `body()` (`JsonBody` ne costruisce uno e omette
i valori null) e i controlli prima dell'invio vanno in `validate()`. Per chiamare un endpoint che l'SDK non copre ancora, scrivi la tua richiesta:

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

La tua richiesta ottiene tutto ciò che ottengono quelle integrate: credenziali, retry per classe di sicurezza, distanziamento, modalità di sola lettura e
gestione degli errori. Il tipo della risposta viene letto da `extends BaseRequest<DomainNotes>`; sovrascrivi `responseType()` solo per
le classi di richiesta generiche. Sovrascrivi `isIdempotent()` quando una scrittura può essere ripetuta senza rischi.

<a id="errors"></a>

## Errori

Ogni richiesta fallita lancia una `DomainNameApiException` (unchecked). Gli argomenti non validi lanciano
`IllegalArgumentException` prima che venga inviato qualsiasi dato.

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

| Metodo | Significato |
|---|---|
| `kind()` | Una classificazione stabile (tabella sotto). Ramifica su questa, non sul messaggio. |
| `code()` | Il codice di errore dell'API (come `Dna.DomainService:Domain:10007`) o uno dei codici dell'SDK qui sotto. |
| `getMessage()` | Cosa è successo. I messaggi generici vengono sostituiti con i dettagli dell'API. |
| `statusCode()` | Lo stato HTTP, oppure null se non è arrivata alcuna risposta. |
| `details()`, `validationErrors()` | Dettagli aggiuntivi e problemi a livello di campo restituiti dall'API. |
| `correlationId()` | L'`X-Correlation-Id` del gateway. Citalo quando contatti il supporto. |
| `hint()` | Un breve suggerimento su cosa fare dopo. |
| `isRetryable()` | Indica se ripetere la stessa richiesta più tardi può riuscire. |
| `isOutcomeUnknown()` | Una richiesta che modifica qualcosa è fallita in rete, quindi potrebbe essere stata eseguita. |
| `operation()`, `safety()` | La richiesta fallita (`POST domains/renew`) e la sua classe di sicurezza. |
| `retryAfter()`, `responseBody()` | Il `Retry-After` del gateway e la risposta grezza. |

| `kind()` | Quando |
|---|---|
| `AUTHENTICATION` | Credenziali errate o mancanti, oppure reseller sconosciuto o sospeso in questo ambiente (401, redirect). |
| `FORBIDDEN` | Autenticato ma non autorizzato (403). |
| `NOT_FOUND` | Il dominio, il contatto, l'ordine o un'altra risorsa non esiste (404). |
| `VALIDATION` | L'API ha rifiutato l'input o una regola di business è fallita: campo errato, dominio bloccato, saldo insufficiente (400, 422). |
| `CONFLICT` | Lo stato della risorsa non consente l'operazione (409). |
| `RATE_LIMITED` | Limite di richieste ancora superato dopo i retry automatici (429). |
| `SERVER` | Un errore lato server (5xx). Spesso temporaneo. |
| `NETWORK`, `TIMEOUT` | Nessuna risposta: DNS, TLS, proxy, connessione interrotta, o nessuna risposta entro il timeout. |
| `READ_ONLY` | Bloccata dalla modalità di sola lettura; non è stato inviato nulla. |
| `UNKNOWN` | Qualsiasi altro caso, inclusa una risposta che l'SDK non ha saputo leggere. |

Codici impostati dall'SDK stesso:

| Codice | Significato |
|---|---|
| `INVALID_RESELLER` | Il gateway ha reindirizzato alla sua pagina di sospensione: il reseller ID è sconosciuto, inattivo o sospeso **in questo ambiente**. |
| `INVALID_API_KEY` | La chiave non corrisponde al reseller. Le chiavi di produzione e OTE sono diverse. |
| `MISSING_API_KEY`, `MISSING_RESELLER_ID`, `NO_CREDENTIALS` | Una credenziale è vuota. |
| `SUBRESELLER_NOT_ALLOWED` | Gli account sub-reseller non possono usare l'API. Usa le credenziali del reseller principale. |
| `UNAUTHORIZED` | Qualsiasi altro 401. |
| `RATE_LIMITED` | 429 dopo i retry. |
| `NETWORK`, `TIMEOUT` | Non è arrivata alcuna risposta. |
| `READ_ONLY` | Bloccata dalla modalità di sola lettura. |
| `EMPTY_RESPONSE`, `INVALID_RESPONSE` | Una risposta riuscita era vuota o non è stato possibile leggerla. |
| `HTTP_<status>` | L'API non ha inviato alcun codice di errore. |

Se il thread viene interrotto mentre una richiesta è in attesa, l'SDK reimposta il flag di interruzione e lancia una
`CancellationException` invece di un errore dell'API.

<a id="safety-retries-and-rate-limits"></a>

## Sicurezza, retry e limiti di richieste

Ogni richiesta ha una classe di sicurezza, la stessa usata dalla CLI `dna`:

| `OperationSafety` | Significato | Esempi |
|---|---|---|
| `READ` | Nessun effetto collaterale, anche quando l'API usa POST o PUT | ricerca, dettagli, elenchi, verifiche di rinnovo e trasferimento, generazione della CSR |
| `WRITE` | Una modifica gratuita e reversibile | record DNS, blocco, privacy, nameserver, contatti |
| `BILLABLE` | Addebita il tuo saldo | registrazione, rinnovo, trasferimento, ripristino, acquisto e rinnovo SSL, ordine di hosting |
| `DESTRUCTIVE` | Elimina qualcosa o è difficile da annullare | eliminazione di record DNS, rimozione DNSSEC, revoca del certificato |

- **Modalità di sola lettura** (`readOnly(true)` o `DNA_READ_ONLY=true`) blocca, prima dell'invio, tutto ciò che non è `READ`.
  Usala mentre esplori, nei report e nelle dashboard.
- **Retry.** Le risposte 429 vengono sempre ritentate, rispettando `Retry-After`. È sicuro anche per le richieste a pagamento, perché
  il gateway le rifiuta prima che venga eseguito qualsiasi cosa. Errori di rete, timeout e risposte 502/503/504 vengono ritentati solo per
  le letture (e per le scritture che si possono ripetere senza rischi, come la sostituzione dei nameserver). Una registrazione o un rinnovo non viene mai inviato due volte.
- **Distanziamento.** Il gateway consente 5 richieste al secondo per chiave, 2 al secondo per le ricerche di domini e 20 al secondo per
  le chiamate alla zona DNS. Il client distanzia le richieste per restare entro questi limiti (chiamate alla zona a 1 secondo di distanza). Le informazioni sul dominio e
  l'elenco dei TLD non hanno limiti di richieste.
- **Soft quota.** Le ricerche e le consultazioni di informazioni costano 1 punto, tutto il resto 10. Oltre i 200 punti il gateway rallenta ogni
  richiesta fino a 5 secondi; i punti decadono del 20% al minuto. `sendWithResponse` mostra lo stato in `quota()`.

<a id="asynchronous-calls"></a>

## Chiamate asincrone

Il client è sincrono: ogni metodo si blocca finché non arriva la risposta, il che si adatta al codice lato server e ai virtual thread.
`sendAsync` e `sendWithResponseAsync` eseguono una richiesta sull'executor del client e restituiscono un `CompletableFuture`:

```java
CompletableFuture<DomainDetails> details = client.sendAsync(new GetDomainRequest("example.com"));
details.thenAccept(d -> System.out.println(d.expirationDate()));
```

Il future fallisce con le stesse eccezioni lanciate dai metodi sincroni (racchiuse in una `CompletionException` da
`join()`). Imposta il tuo executor con `builder().executor(...)`, ad esempio `Executors.newVirtualThreadPerTaskExecutor()`
su Java 21.

<a id="testing-your-code"></a>

## Testare il tuo codice

All'SDK serve solo un endpoint HTTP. Per testare il codice che lo usa senza chiamare l'API, punta `baseUri` su un server
locale, ad esempio quello integrato nel JDK:

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

Puoi anche passare una tua sottoclasse di `HttpClient` con `httpClient(...)` e creare eccezioni per i tuoi mock con
`DomainNameApiException.builder(...)`. Per i test sull'API reale, usa OTE con la sua chiave.

<a id="project-layout"></a>

## Struttura del progetto

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

Ogni package di area ha la stessa forma: un `XxxClient`, una classe di richiesta per operazione (`XxxRequest`) e i modelli
e gli enum delle sue risposte.

<a id="development"></a>

## Sviluppo

```bash
mvn verify                          # build and run the tests
mvn -Pexamples -DskipTests compile  # also compile the examples
```

Senza un JDK locale, usa Docker:

```bash
docker run --rm -v "$PWD":/src -w /src maven:3.9-eclipse-temurin-17 mvn -B verify
```

I test non chiamano mai l'API reale: usano un `HttpClient` finto, più un server HTTP locale su 127.0.0.1 per il
trasporto. La CI li esegue su Java 17, 21 e 25.

`src/test/resources/openapi.json` è il documento API che la CLI `dna` incorpora. `SpecCoverageTest` verifica che ogni
operazione al suo interno abbia esattamente una classe di richiesta con la classe di sicurezza della CLI, e che ogni richiesta invii solo parametri
di query e proprietà del corpo descritti nel documento. Quando l'API aggiunge un'operazione, aggiorna il documento e il
test fallisce finché la richiesta non esiste.

<a id="upgrading-from-1x"></a>

## Passare dalla 1.x

La 2.0 è un nuovo SDK. La 1.x era un port della libreria PHP e restituiva envelope `Map` non tipizzati; la 2.0 ha richieste
e modelli tipizzati, eccezioni e un builder.

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

La baseline Java passa dalla 11 alla 17 e Jackson è ora una dipendenza. La 1.x inviava report di errore a Sentry; la 2.0
non invia nulla a nessuno, tranne che all'API.

## Link

- [CLI `dna` e server MCP](https://github.com/domainreseller/cli): la stessa API dal terminale e dagli agenti AI.
- [Documentazione dell'API](https://www.domainnameapi.com/docs/)
- [Changelog](CHANGELOG.md)

## Licenza

Apache License 2.0. Vedi [LICENSE](LICENSE).
