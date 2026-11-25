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

Le SDK Java officiel de la plateforme revendeur [Domain Name API](https://www.domainnameapi.com). Il couvre les
67 opérations de l'API REST avec des clients et des modèles typés : domaines, contacts, DNS, DNSSEC, certificats SSL,
hébergement, commandes, produits et votre compte.

```java
DomainNameApiClient client = DomainNameApiClient.create(resellerId, apiKey, ApiEnvironment.OTE);

DomainSearchResult search = client.domains().search("example.com");
if (search.info() != null && search.info().isAvailable()) {
    client.domains().register("example.com", contact, List.of("tr.apiname.com", "eu.apiname.com"));
}
```

- **Tout est typé.** Chaque opération est une classe de requête et chaque réponse un record documenté par Javadoc. Pas de maps, pas de parsing JSON.
- **Vos propres requêtes.** Chaque requête étend `BaseRequest`. Créez une sous-classe pour un endpoint que le SDK ne couvre pas encore.
- **Sûr avec l'argent.** Chaque requête déclare si elle lit, écrit, débite votre solde ou supprime quelque chose. Les requêtes facturables ne sont jamais rejouées, et un mode lecture seule bloque toute modification.
- **Résilience intégrée.** Les limites de débit sont respectées automatiquement, les réponses 429 sont rejouées, et les erreurs réseau sont rejouées pour les lectures.
- **Des erreurs claires.** Une seule exception non vérifiée (unchecked) avec une nature stable, le code d'erreur de l'API, les erreurs de validation par champ, l'ID de corrélation et une piste de résolution.
- **Java moderne.** Java 17+, records, pagination paresseuse avec `Stream`, variantes `CompletableFuture`, un client thread-safe basé sur `java.net.http`.
- **Confidentiel.** Aucune télémétrie. Rien ne quitte votre processus, à part les requêtes que vous envoyez à l'API.

C'est la version Java de [`dna`, la CLI Domain Name API](https://github.com/domainreseller/cli) : le même document
d'API, les mêmes classes de sécurité, les mêmes règles de nouvelle tentative et les mêmes codes d'erreur.

## Sommaire

- [Installation](#install)
- [Identifiants](#credentials)
- [Démarrage rapide](#quick-start)
- [Configuration](#configuration)
- [Utilisation](#usage) : [compte](#account) · [domaines](#domains) · [contacts](#contacts) · [DNS](#dns) · [DNSSEC](#dnssec) · [SSL](#ssl) · [hébergement](#hosting) · [commandes et produits](#orders-and-products)
- [Requêtes et `BaseRequest`](#requests-and-baserequest)
- [Erreurs](#errors)
- [Sécurité, nouvelles tentatives et limites de débit](#safety-retries-and-rate-limits)
- [Appels asynchrones](#asynchronous-calls)
- [Tester votre code](#testing-your-code)
- [Structure du projet](#project-layout)
- [Développement](#development)
- [Migrer depuis la 1.x](#upgrading-from-1x)

<a id="install"></a>

## Installation

> [!IMPORTANT]
> **Utilisez toujours la [dernière version publiée](https://github.com/domainreseller/java-dna/releases/latest).** Les paquets publiés sont versionnés et testés.
> Le bouton vert **Code → Download ZIP** télécharge à la place la branche de développement.

Coordonnées : `io.github.domainreseller:domainnameapi:2.1.0`.

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

Tant que l'artefact n'est pas disponible dans un dépôt public, installez-le dans votre dépôt Maven local depuis les
sources de la version publiée (`mvn -DskipTests install`) et ajoutez `mavenLocal()` aux dépôts de votre build Gradle.

**Prérequis :** Java 17 ou version ultérieure. La seule dépendance est [Jackson](https://github.com/FasterXML/jackson-databind)
(`jackson-databind`) pour le JSON ; le HTTP passe par le `java.net.http.HttpClient` du JDK.

<a id="credentials"></a>

## Identifiants

L'API authentifie chaque requête avec deux valeurs, que le SDK envoie pour vous :

| Valeur | En-tête | Où la trouver |
|---|---|---|
| Reseller ID (un UUID) | `__reseller` | Panneau revendeur → **API settings** |
| Clé API | `X-API-KEY` | Panneau revendeur → **API settings** |

> [!NOTE]
> Il s'agit d'**identifiants API** : vos identifiants de connexion au panneau ne fonctionnent pas ici. **La production et l'OTE (l'environnement de test)
> utilisent des clés API différentes.** Utiliser l'une à la place de l'autre échoue avec `INVALID_API_KEY` ou `INVALID_RESELLER`.

<a id="quick-start"></a>

## Démarrage rapide

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

Le dossier [examples](examples) contient des programmes exécutables pour ces scénarios. Ils lisent les variables
d'environnement `DNA_*` ; faites-les donc pointer vers l'OTE :

```bash
export DNA_RESELLER_ID=... DNA_API_KEY=... DNA_ENV=ote
```

<a id="configuration"></a>

## Configuration

Il y a quatre façons de créer un client :

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

| Méthode du builder | Par défaut | Signification |
|---|---|---|
| `credentials(resellerId, apiKey)` | (obligatoire) | Les identifiants. |
| `environment(...)` | `PRODUCTION` | `PRODUCTION` (`https://api.domainresellerapi.com`) ou `OTE` (`https://ote.domainresellerapi.com`). |
| `baseUri(...)` | aucune | Remplace l'adresse de l'environnement, par exemple pour un proxy. |
| `timeout(...)` | 60 s | Délai maximal d'une tentative. |
| `maxRetries(...)` | 2 | Combien de fois une requête échouée est rejouée (voir [nouvelles tentatives](#safety-retries-and-rate-limits)). 0 désactive les nouvelles tentatives. |
| `maxRetryDelay(...)` | 30 s | L'attente la plus longue avant une nouvelle tentative. |
| `paceRequests(...)` | `true` | Espace les requêtes pour rester sous les limites de débit de la passerelle. |
| `readOnly(...)` | `false` | Bloque, avant envoi, toute requête qui n'est pas une lecture. |
| `userAgent(...)` | `DomainNameAPI-Java/<version>` | L'en-tête `User-Agent`. |
| `app(name, version)` | `java`, la version du SDK | L'application qui utilise le SDK, envoyée dans `X-App` et `X-Version` (voir [ci-dessous](#identifier-votre-application)). |
| `httpClient(...)` | celui du SDK | Votre propre `java.net.http.HttpClient` (proxy, TLS, executor). |
| `executor(...)` | un pool de threads démons partagé | L'endroit où s'exécutent les méthodes `sendAsync`. |

`fromEnvironment()` lit :

| Variable | Signification |
|---|---|
| `DNA_RESELLER_ID`, `DNA_API_KEY` | Les identifiants. |
| `DNA_ENV` | `production` ou `ote` (aussi `prod`, `live`, `test`, `sandbox`). |
| `DNA_TEST_MODE` | `true`, `1`, `on` ou `yes` sélectionne l'OTE. Utilisée quand `DNA_ENV` n'est pas définie. |
| `DNA_BASE_URL` | Remplacement facultatif de l'adresse de base. |
| `DNA_READ_ONLY` | `true` active le mode lecture seule. |
| `DNA_APP`, `DNA_APP_VERSION` | L'application qui utilise le SDK et sa version (`X-App`, `X-Version`). |

### Identifier votre application

Chaque requête porte deux en-têtes qui indiquent à l'API quelle intégration l'a envoyée : `X-App`, le nom de
l'application en minuscules dont seuls `a-z`, `0-9`, `.`, `_` et `-` sont conservés, et `X-Version`, sa version réduite
aux chiffres et aux points. Par défaut, ce sont `java` et la version du SDK. Indiquez votre panneau ou votre module avec
`app(name, version)` ou `DNA_APP`/`DNA_APP_VERSION` ; un nom vide ou invalide redevient `java`, une version vide la
version du SDK. Rien d'autre n'est envoyé et le `User-Agent` ne change pas.

```java
DomainNameApiClient client = DomainNameApiClient.builder()
        .credentials(resellerId, apiKey)
        .app("WiseCP", "V2.0.20-beta")   // sends X-App: wisecp, X-Version: 2.0.20
        .build();
```

Le client est thread-safe. Créez-en un par jeu d'identifiants et réutilisez-le pendant toute la durée de vie de l'application.

<a id="usage"></a>

## Utilisation

Chaque domaine fonctionnel de l'API a son propre client. Les méthodes prennent des arguments simples ; pour plus d'options, chaque méthode a aussi une
surcharge (ou une méthode sœur) qui prend l'objet de requête. Les méthodes qui débitent votre solde l'indiquent dans leur Javadoc.

<a id="account"></a>

### Compte

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

### Domaines

**Recherche**

```java
DomainSearchResult one = client.domains().search("example.com", "USD");
boolean available = one.info() != null && one.info().isAvailable();

BulkSearchResult many = client.domains().bulkSearch(List.of("example.com", "example.net", "example.com.tr"));
for (DomainAvailability domain : many.available()) {
    System.out.println(domain.domainName() + ": " + domain.price() + " " + domain.currency());
}
```

**Liste et détails**

```java
Page<Domain> page = client.domains().list(new ListDomainsRequest().maxResultCount(50).statuses(DomainStatus.ACTIVE));

client.domains().listAll(new ListDomainsRequest().domainName("example"))
        .forEach(d -> System.out.println(d.domainName() + " " + d.status() + " " + d.expirationDate()));

DomainDetails details = client.domains().get("example.com");
System.out.println(details.authCode());
details.contact(ContactType.REGISTRANT).ifPresent(c -> System.out.println(c.email()));
```

**Enregistrer, renouveler, transférer, restaurer** (ces opérations débitent votre solde)

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

Les requêtes sont vérifiées avant d'être envoyées. Un enregistrement sans les quatre rôles de contact, un contact sans adresse e-mail
ou une durée de 0 an lève une `IllegalArgumentException`, et rien n'atteint l'API.

> [!NOTE]
> L'API peut répondre à un enregistrement, un renouvellement ou un transfert par un HTTP 200 avec `"success": false` (par exemple quand le
> registre refuse le domaine). Le SDK renvoie cette réponse telle quelle : vérifiez `DomainOperationResult.success()` et
> `message()`.

**Paramètres**

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

**Redirection d'URL, serveurs de noms enfants et documents**

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

### Contacts

Les handles de contact sont des contacts réutilisables. Créez-en un une fois et utilisez-le pour plusieurs domaines.

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

Enregistrements de zone des domaines qui utilisent les serveurs de noms de la plateforme. Les enregistrements sont résolus publiquement dès que le domaine utilise ces serveurs de noms,
ce dont `apply` se charge.

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

Le SDK vérifie ce que l'API refuserait : un CNAME à l'apex, une valeur CAA sans ses trois parties, un TTL hors de la plage
1–86400. Les appels de zone sont espacés de 1 seconde, la limite de la passerelle.

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

Un domaine peut avoir plusieurs certificats ; un certificat existant est donc désigné par son **identifiant de
certificat** (un `UUID`), jamais par son nom de domaine. `purchase` et `renew` renvoient cet identifiant :
conservez-le. `list` retrouve les identifiants des certificats que vous possédez déjà.

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

**Lister les certificats et trouver un identifiant**

```java
Page<CertificateListItem> page = client.ssl().list(new ListCertificatesRequest().domainName("example.com"));   // newest first

client.ssl().listAll().forEach(c ->
        System.out.println(c.certificateId() + " " + c.domainName() + " " + c.status() + " " + c.subscriptionExpiresAt()));

// The approver addresses of a certificate you already have
List<String> approverEmails = client.ssl().getApproverEmails(certificateId);
```

**Renouveler** (cette opération débite votre solde)

```java
// Allowed from 30 days before details.subscription().expiresAt(). The renewal is a new certificate with its own ID.
SslOperationResult renewal = client.ssl().renew(certificateId);
client.ssl().configure(new ConfigureCertificateRequest(renewal.certificateId(), newCsr, certificateContact)
        .validationMethod(ValidationMethod.EMAIL)
        .approverEmail(approvers.get(0))
        .webServerType(WebServerType.APACHE));
```

Un certificat acheté ou renouvelé doit être configuré dans les 15 jours ; passé ce délai, il est annulé. Pour
renouveler, appelez `renew`, puis `configure` avec le nouvel identifiant. N'achetez pas de nouveau avec `purchase` :
il s'agirait d'un achat distinct au prix d'achat, et l'ancien certificat ne serait jamais marqué comme renouvelé.
Un identifiant de certificat vide lève une `IllegalArgumentException` avant tout envoi.

<a id="hosting"></a>

### Hébergement

```java
List<HostingPackage> packages = client.hosting().listPackages();
HostingCreateResult created = client.hosting().create("example.com", "starter-linux");   // charges the balance
HostingStatus status = client.hosting().getStatus("example.com");                         // panel access once ready
HostingService hosting = client.hosting().get("example.com");
List<ServicePlan> plans = client.hosting().listPlans();
```

<a id="orders-and-products"></a>

### Commandes et produits

```java
Order order = client.orders().get("ORDER-CODE");

client.products().getTld("com").ifPresent(com -> System.out.println(
        com.registrationPrice(1).map(Tld.Price::price).orElse(null) + ", DNSSEC: " + com.supportsDnssec()));

client.products().listAllTlds(new ListTldsRequest().currency("USD")).forEach(tld -> System.out.println(tld.name()));

ProductPrice price = client.products().getPrice("com", OrderType.RENEW, 1);
Page<SslProduct> ssl = client.products().listSslProducts(new ListSslProductsRequest().isWildcard(true));
```

<a id="requests-and-baserequest"></a>

## Requêtes et `BaseRequest`

Chaque opération est une classe de requête, par exemple `RenewDomainRequest`, `ListDomainsRequest` ou `AddDnsRecordRequest`.
Les méthodes du client ci-dessus les construisent pour vous, et vous pouvez aussi les créer et les envoyer vous-même :

```java
RenewDomainRequest request = new RenewDomainRequest("example.com", 2);
System.out.println(request + " is " + request.safety());   // "POST domains/renew is BILLABLE"

DomainOperationResult result = client.send(request);

// The same, with the HTTP details: correlation ID, soft-quota state, attempts, duration
ApiResponse<DomainOperationResult> response = client.sendWithResponse(request);
System.out.println(response.correlationId() + " quota " + response.quota());
```

Toutes les requêtes étendent l'une de ces deux classes de base :

| Classe de base | Pour |
|---|---|
| `BaseRequest<T>` | Toutes les opérations. `T` est le type de la réponse ; `Void` pour les opérations qui ne renvoient rien (verrouiller, déverrouiller, ajouter un enregistrement DNS, ...). |
| `PagedRequest<T>` | Les opérations de liste. Ajoute `skipCount`, `maxResultCount` et `sorting`, et fonctionne avec `listAll`. |

Une requête se décrit avec quelques redéfinitions (overrides) : `method()`, `path()` (sous `/api/v1/`) et `safety()`. Les
paramètres de requête sont ajoutés dans `addQueryParameters`, le corps JSON provient de `body()` (`JsonBody` en construit un en omettant
les valeurs null), et les vérifications avant envoi vont dans `validate()`. Pour appeler un endpoint que le SDK ne couvre pas encore, écrivez votre propre requête :

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

Votre requête bénéficie de tout ce que reçoivent les requêtes intégrées : identifiants, nouvelles tentatives selon la classe de sécurité, espacement, mode lecture seule et
gestion des erreurs. Le type de la réponse est déduit de `extends BaseRequest<DomainNotes>` ; ne redéfinissez `responseType()` que pour
les classes de requête génériques. Redéfinissez `isIdempotent()` quand une écriture peut être répétée sans risque.

<a id="errors"></a>

## Erreurs

Toute requête échouée lève une `DomainNameApiException` (non vérifiée). Les arguments invalides lèvent une
`IllegalArgumentException` avant que rien ne soit envoyé.

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

| Méthode | Signification |
|---|---|
| `kind()` | Une classification stable (tableau ci-dessous). Branchez votre logique dessus, pas sur le message. |
| `code()` | Le code d'erreur de l'API (comme `Dna.DomainService:Domain:10007`), ou l'un des codes du SDK ci-dessous. |
| `getMessage()` | Ce qui s'est passé. Les messages génériques sont remplacés par les détails de l'API. |
| `statusCode()` | Le statut HTTP, ou null si aucune réponse n'est arrivée. |
| `details()`, `validationErrors()` | Détails supplémentaires et problèmes par champ renvoyés par l'API. |
| `correlationId()` | Le `X-Correlation-Id` de la passerelle. Indiquez-le en contactant le support. |
| `hint()` | Une courte suggestion sur la suite à donner. |
| `isRetryable()` | Indique si rejouer la même requête plus tard peut réussir. |
| `isOutcomeUnknown()` | Une requête qui modifie quelque chose a échoué sur le réseau, elle a donc peut-être été exécutée. |
| `operation()`, `safety()` | La requête en échec (`POST domains/renew`) et sa classe de sécurité. |
| `retryAfter()`, `responseBody()` | Le `Retry-After` de la passerelle et la réponse brute. |

| `kind()` | Quand |
|---|---|
| `AUTHENTICATION` | Identifiants erronés ou absents, ou revendeur inconnu ou suspendu dans cet environnement (401, redirection). |
| `FORBIDDEN` | Authentifié mais non autorisé (403). |
| `NOT_FOUND` | Le domaine, le contact, la commande ou une autre ressource n'existe pas (404). |
| `VALIDATION` | L'API a refusé l'entrée ou une règle métier a échoué : champ invalide, domaine verrouillé, solde insuffisant (400, 422). |
| `CONFLICT` | L'état de la ressource n'autorise pas l'opération (409). |
| `RATE_LIMITED` | Toujours limité en débit après les nouvelles tentatives automatiques (429). |
| `SERVER` | Une défaillance côté serveur (5xx). Souvent temporaire. |
| `NETWORK`, `TIMEOUT` | Pas de réponse : DNS, TLS, proxy, connexion réinitialisée, ou aucune réponse dans le délai imparti. |
| `READ_ONLY` | Bloquée par le mode lecture seule ; rien n'a été envoyé. |
| `UNKNOWN` | Tout le reste, y compris une réponse que le SDK n'a pas pu lire. |

Codes définis par le SDK lui-même :

| Code | Signification |
|---|---|
| `INVALID_RESELLER` | La passerelle a redirigé vers sa page de suspension : le Reseller ID est inconnu, inactif ou suspendu **dans cet environnement**. |
| `INVALID_API_KEY` | La clé ne correspond pas au revendeur. Les clés de production et d'OTE diffèrent. |
| `MISSING_API_KEY`, `MISSING_RESELLER_ID`, `NO_CREDENTIALS` | Un identifiant est vide. |
| `SUBRESELLER_NOT_ALLOWED` | Les comptes de sous-revendeur ne peuvent pas utiliser l'API. Utilisez les identifiants du revendeur principal. |
| `UNAUTHORIZED` | Toute autre 401. |
| `RATE_LIMITED` | 429 après les nouvelles tentatives. |
| `NETWORK`, `TIMEOUT` | Aucune réponse n'est arrivée. |
| `READ_ONLY` | Bloquée par le mode lecture seule. |
| `EMPTY_RESPONSE`, `INVALID_RESPONSE` | Une réponse réussie était vide ou illisible. |
| `HTTP_<status>` | L'API n'a envoyé aucun code d'erreur. |

Si le thread est interrompu pendant qu'une requête attend, le SDK repositionne le drapeau d'interruption et lève une
`CancellationException` au lieu d'une erreur d'API.

<a id="safety-retries-and-rate-limits"></a>

## Sécurité, nouvelles tentatives et limites de débit

Chaque requête a une classe de sécurité, la même que celle de la CLI `dna` :

| `OperationSafety` | Signification | Exemples |
|---|---|---|
| `READ` | Aucun effet de bord, même quand l'API utilise POST ou PUT | recherche, détails, listes, vérifications de renouvellement et de transfert, génération de CSR |
| `WRITE` | Une modification gratuite et réversible | enregistrements DNS, verrouillage, confidentialité, serveurs de noms, contacts |
| `BILLABLE` | Débite votre solde | enregistrement, renouvellement, transfert, restauration, achat et renouvellement de SSL, commande d'hébergement |
| `DESTRUCTIVE` | Supprime quelque chose ou est difficile à annuler | suppression d'un enregistrement DNS, retrait de DNSSEC, révocation d'un certificat |

- **Le mode lecture seule** (`readOnly(true)` ou `DNA_READ_ONLY=true`) bloque, avant envoi, tout ce qui n'est pas `READ`.
  Utilisez-le pour explorer, dans les rapports et dans les tableaux de bord.
- **Nouvelles tentatives.** Les réponses 429 sont toujours rejouées, en respectant `Retry-After`. C'est sans risque, même pour les requêtes facturables, car
  la passerelle les refuse avant que quoi que ce soit ne s'exécute. Les erreurs réseau, les timeouts et les réponses 502/503/504 ne sont rejoués que pour
  les lectures (et pour les écritures qui peuvent être répétées sans risque, comme le remplacement des serveurs de noms). Un enregistrement ou un renouvellement n'est jamais envoyé deux fois.
- **Espacement.** La passerelle autorise 5 requêtes par seconde et par clé, 2 par seconde pour les recherches de domaines et 20 par seconde pour
  les appels de zone DNS. Le client espace les requêtes pour rester sous ces limites (appels de zone espacés de 1 seconde). Les informations de domaine et la liste
  des TLD ne sont pas limitées en débit.
- **Quota souple.** Les recherches et les consultations d'informations coûtent 1 point, tout le reste 10. Au-delà de 200 points, la passerelle ralentit chaque
  requête jusqu'à 5 secondes ; les points décroissent de 20 % par minute. `sendWithResponse` expose cet état dans `quota()`.

<a id="asynchronous-calls"></a>

## Appels asynchrones

Le client est synchrone : chaque méthode bloque jusqu'à l'arrivée de la réponse, ce qui convient au code serveur et aux threads virtuels.
`sendAsync` et `sendWithResponseAsync` exécutent une requête sur l'executor du client et renvoient un `CompletableFuture` :

```java
CompletableFuture<DomainDetails> details = client.sendAsync(new GetDomainRequest("example.com"));
details.thenAccept(d -> System.out.println(d.expirationDate()));
```

Le future échoue avec les mêmes exceptions que celles levées par les méthodes synchrones (enveloppées dans une `CompletionException` par
`join()`). Définissez votre propre executor avec `builder().executor(...)`, par exemple `Executors.newVirtualThreadPerTaskExecutor()`
sur Java 21.

<a id="testing-your-code"></a>

## Tester votre code

Le SDK n'a besoin que d'un endpoint HTTP. Pour tester le code qui l'utilise sans appeler l'API, faites pointer `baseUri` vers un serveur
local, par exemple celui intégré au JDK :

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

Vous pouvez aussi passer votre propre sous-classe de `HttpClient` avec `httpClient(...)`, et créer des exceptions pour vos mocks avec
`DomainNameApiException.builder(...)`. Pour des tests contre la vraie API, utilisez l'OTE avec sa propre clé.

<a id="project-layout"></a>

## Structure du projet

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

Chaque package de domaine fonctionnel a la même forme : un `XxxClient`, une classe de requête par opération (`XxxRequest`) et les modèles
et enums de ses réponses.

<a id="development"></a>

## Développement

```bash
mvn verify                          # build and run the tests
mvn -Pexamples -DskipTests compile  # also compile the examples
```

Sans JDK local, utilisez Docker :

```bash
docker run --rm -v "$PWD":/src -w /src maven:3.9-eclipse-temurin-17 mvn -B verify
```

Les tests n'appellent jamais la vraie API : ils utilisent un faux `HttpClient`, ainsi qu'un serveur HTTP local sur 127.0.0.1 pour la
couche transport. La CI les exécute sur Java 17, 21 et 25.

`src/test/resources/openapi.json` est le document d'API que la CLI `dna` embarque. `SpecCoverageTest` vérifie que chaque
opération qu'il contient a exactement une classe de requête avec la classe de sécurité de la CLI, et que chaque requête n'envoie que des paramètres
de requête et des propriétés de corps décrits par le document. Quand l'API gagne une opération, mettez à jour le document : le
test échoue tant que la requête n'existe pas.

<a id="upgrading-from-1x"></a>

## Migrer depuis la 1.x

La 2.0 est un nouveau SDK. La 1.x était un portage de la bibliothèque PHP et renvoyait des enveloppes `Map` non typées ; la 2.0 propose des requêtes
et des modèles typés, des exceptions et un builder.

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

La version minimale de Java passe de 11 à 17, et Jackson devient une dépendance. La 1.x envoyait des rapports d'erreurs à Sentry ; la 2.0
n'envoie rien nulle part, sauf à l'API.

## Liens

- [CLI `dna` et serveur MCP](https://github.com/domainreseller/cli) : la même API depuis le terminal et depuis des agents IA.
- [Documentation de l'API](https://www.domainnameapi.com/docs/)
- [Changelog](CHANGELOG.md)

## Licence

Apache License 2.0. Voir [LICENSE](LICENSE).
