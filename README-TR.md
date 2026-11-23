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

[Domain Name API](https://www.domainnameapi.com) bayi platformunun resmi Java SDK'sı. REST API'nin 67 işleminin
tamamını tipli istemciler ve modellerle kapsar: alan adları, iletişim bilgileri, DNS, DNSSEC, SSL sertifikaları,
hosting, siparişler, ürünler ve hesabınız.

```java
DomainNameApiClient client = DomainNameApiClient.create(resellerId, apiKey, ApiEnvironment.OTE);

DomainSearchResult search = client.domains().search("example.com");
if (search.info() != null && search.info().isAvailable()) {
    client.domains().register("example.com", contact, List.of("tr.apiname.com", "eu.apiname.com"));
}
```

- **Her şey tipli.** Her işlem bir istek sınıfı, her yanıt Javadoc'lu bir record'dur. Map yok, JSON ayrıştırma yok.
- **Kendi istekleriniz.** Her istek `BaseRequest`'ten türer. SDK'nın henüz kapsamadığı bir uç nokta için ondan kendi sınıfınızı türetin.
- **Para harcayan işlemlerde güvenli.** Her istek, okuma mı yaptığını, yazma mı yaptığını, bakiyenizden ücret mi aldığını yoksa bir şey mi sildiğini bildirir. Ücretli istekler asla yeniden denenmez, salt-okunur mod ise her değişikliği engeller.
- **Yerleşik dayanıklılık.** Hız sınırlarına otomatik uyulur, 429 yanıtları yeniden denenir, okumalarda ağ hataları yeniden denenir.
- **Net hatalar.** Sabit bir türü, API'nin hata kodunu, alan düzeyinde doğrulama hatalarını, correlation ID'yi ve bir ipucunu taşıyan tek bir unchecked istisna.
- **Modern Java.** Java 17+, record'lar, tembel (lazy) `Stream` sayfalama, `CompletableFuture` varyantları, `java.net.http` üzerinde thread-safe bir istemci.
- **Gizlilik.** Telemetri yok. API'ye gönderdiğiniz istekler dışında sürecinizden hiçbir şey dışarı çıkmaz.

[Domain Name API CLI'si `dna`](https://github.com/domainreseller/cli)'nın Java karşılığıdır: aynı API
dokümanı, aynı güvenlik sınıfları, yeniden deneme kuralları ve hata kodları.

## İçindekiler

- [Kurulum](#install)
- [Kimlik bilgileri](#credentials)
- [Hızlı başlangıç](#quick-start)
- [Yapılandırma](#configuration)
- [Kullanım](#usage): [hesap](#account) · [alan adları](#domains) · [iletişim bilgileri](#contacts) · [DNS](#dns) · [DNSSEC](#dnssec) · [SSL](#ssl) · [hosting](#hosting) · [siparişler ve ürünler](#orders-and-products)
- [İstekler ve `BaseRequest`](#requests-and-baserequest)
- [Hatalar](#errors)
- [Güvenlik, yeniden denemeler ve hız sınırları](#safety-retries-and-rate-limits)
- [Asenkron çağrılar](#asynchronous-calls)
- [Kodunuzu test etme](#testing-your-code)
- [Proje yapısı](#project-layout)
- [Geliştirme](#development)
- [1.x'ten yükseltme](#upgrading-from-1x)

<a id="install"></a>

## Kurulum

> [!IMPORTANT]
> **Her zaman [en son sürümü](https://github.com/domainreseller/java-dna/releases/latest) kullanın.** Yayınlanan paketler sürüm numaralıdır ve test edilmiştir.
> Yeşil **Code → Download ZIP** düğmesi ise geliştirme dalını indirir.

Koordinatlar: `io.github.domainreseller:domainnameapi:2.0.0`.

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

Artifact herkese açık bir depodan edinilebilir olana kadar, onu sürüm kaynağından yerel Maven deponuza kurun
(`mvn -DskipTests install`) ve Gradle depolarınıza `mavenLocal()` ekleyin.

**Gereksinimler:** Java 17 veya üstü. Tek bağımlılık JSON için [Jackson](https://github.com/FasterXML/jackson-databind)'dır
(`jackson-databind`); HTTP, JDK'nın `java.net.http.HttpClient` sınıfı üzerinden yapılır.

<a id="credentials"></a>

## Kimlik bilgileri

API her isteği iki değerle doğrular; SDK bunları sizin yerinize gönderir:

| Değer | Başlık | Nerede bulunur |
|---|---|---|
| Reseller ID (bir UUID) | `__reseller` | Bayi paneli → **API ayarları** |
| API anahtarı | `X-API-KEY` | Bayi paneli → **API ayarları** |

> [!NOTE]
> Bunlar **API kimlik bilgileridir**: panel girişiniz burada çalışmaz. **Canlı ortam ve OTE (test ortamı) farklı
> API anahtarları kullanır.** Birini diğerinde kullanmak `INVALID_API_KEY` veya `INVALID_RESELLER` hatasıyla sonuçlanır.

<a id="quick-start"></a>

## Hızlı başlangıç

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

[examples](examples) klasöründe bu senaryolar için çalıştırılabilir programlar vardır. `DNA_*` ortam değişkenlerini
okurlar; bu yüzden onları OTE'ye yönlendirin:

```bash
export DNA_RESELLER_ID=... DNA_API_KEY=... DNA_ENV=ote
```

<a id="configuration"></a>

## Yapılandırma

Bir istemci oluşturmanın dört yolu vardır:

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

| Builder metodu | Varsayılan | Anlamı |
|---|---|---|
| `credentials(resellerId, apiKey)` | (zorunlu) | Kimlik bilgileri. |
| `environment(...)` | `PRODUCTION` | `PRODUCTION` (`https://api.domainresellerapi.com`) veya `OTE` (`https://ote.domainresellerapi.com`). |
| `baseUri(...)` | yok | Ortamın adresini geçersiz kılar, örneğin bir proxy için. |
| `timeout(...)` | 60 sn | Tek bir denemenin zaman sınırı. |
| `maxRetries(...)` | 2 | Başarısız bir isteğin kaç kez tekrarlanacağı ([yeniden denemeler](#safety-retries-and-rate-limits) bölümüne bakın). 0, yeniden denemeyi kapatır. |
| `maxRetryDelay(...)` | 30 sn | Bir yeniden denemeden önceki en uzun bekleme. |
| `paceRequests(...)` | `true` | İstekleri, geçidin hız sınırlarının altında kalacak şekilde aralıklar. |
| `readOnly(...)` | `false` | Okuma olmayan her isteği, gönderilmeden önce engeller. |
| `userAgent(...)` | `DomainNameAPI-Java/<version>` | `User-Agent` başlığı. |
| `httpClient(...)` | SDK'nın kendi istemcisi | Kendi `java.net.http.HttpClient` nesneniz (proxy, TLS, executor). |
| `executor(...)` | paylaşılan bir daemon havuzu | `sendAsync` metotlarının çalıştığı yer. |

`fromEnvironment()` şunları okur:

| Değişken | Anlamı |
|---|---|
| `DNA_RESELLER_ID`, `DNA_API_KEY` | Kimlik bilgileri. |
| `DNA_ENV` | `production` veya `ote` (ayrıca `prod`, `live`, `test`, `sandbox`). |
| `DNA_TEST_MODE` | `true`, `1`, `on` veya `yes` OTE'yi seçer. `DNA_ENV` ayarlı değilse kullanılır. |
| `DNA_BASE_URL` | İsteğe bağlı temel adres geçersiz kılma. |
| `DNA_READ_ONLY` | `true`, salt-okunur modu açar. |

İstemci thread-safe'tir. Her kimlik bilgisi kümesi için bir tane oluşturun ve uygulamanın ömrü boyunca yeniden kullanın.

<a id="usage"></a>

## Kullanım

API'nin her alanının kendi istemcisi vardır. Metotlar basit argümanlar alır; daha fazla seçenek için her metodun istek
nesnesini alan bir aşırı yüklemesi (veya kardeş metodu) da vardır. Bakiyenizden ücret alan metotlar bunu Javadoc'larında belirtir.

<a id="account"></a>

### Hesap

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

### Alan adları

**Arama**

```java
DomainSearchResult one = client.domains().search("example.com", "USD");
boolean available = one.info() != null && one.info().isAvailable();

BulkSearchResult many = client.domains().bulkSearch(List.of("example.com", "example.net", "example.com.tr"));
for (DomainAvailability domain : many.available()) {
    System.out.println(domain.domainName() + ": " + domain.price() + " " + domain.currency());
}
```

**Listeleme ve ayrıntılar**

```java
Page<Domain> page = client.domains().list(new ListDomainsRequest().maxResultCount(50).statuses(DomainStatus.ACTIVE));

client.domains().listAll(new ListDomainsRequest().domainName("example"))
        .forEach(d -> System.out.println(d.domainName() + " " + d.status() + " " + d.expirationDate()));

DomainDetails details = client.domains().get("example.com");
System.out.println(details.authCode());
details.contact(ContactType.REGISTRANT).ifPresent(c -> System.out.println(c.email()));
```

**Kayıt, yenileme, transfer, geri yükleme** (bunlar bakiyenizden ücret alır)

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

İstekler gönderilmeden önce denetlenir. Dört iletişim rolünün tamamını içermeyen bir kayıt, e-posta adresi olmayan bir
iletişim kişisi veya 0 yıllık bir süre `IllegalArgumentException` fırlatır ve API'ye hiçbir şey ulaşmaz.

> [!NOTE]
> API, bir kayıt, yenileme veya transfer isteğini HTTP 200 ve `"success": false` ile yanıtlayabilir (örneğin kayıt
> kuruluşu alan adını reddettiğinde). SDK bu yanıtı olduğu gibi döndürür: `DomainOperationResult.success()` ve
> `message()` değerlerini kontrol edin.

**Ayarlar**

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

**URL yönlendirme, child nameserver'lar ve belgeler**

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

### İletişim bilgileri

İletişim kişisi tanımlayıcıları (handle), yeniden kullanılabilir iletişim kayıtlarıdır. Bir kez oluşturun, birçok alan adında kullanın.

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

Platformun nameserver'larındaki alan adlarının bölge (zone) kayıtları. Alan adı bu nameserver'ları kullanmaya başlayınca
kayıtlar herkese açık olarak çözümlenir; bunu `apply` sağlar.

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

SDK, API'nin reddedeceği durumları önceden denetler: apex'te CNAME, üç parçası eksik bir CAA değeri, 1–86400 dışında
bir TTL. Bölge çağrıları, geçidin sınırı olan 1 saniye arayla yapılır.

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

Bir alan adının birden fazla sertifikası olabilir; bu nedenle mevcut bir sertifika alan adıyla değil,
**sertifika kimliği** (bir `UUID`) ile belirtilir. `purchase` ve `renew` bu kimliği döndürür: saklayın. `list`,
elinizdeki sertifikaların kimliklerini bulur.

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

**Sertifikaları listeleme ve kimlik bulma**

```java
Page<CertificateListItem> page = client.ssl().list(new ListCertificatesRequest().domainName("example.com"));   // newest first

client.ssl().listAll().forEach(c ->
        System.out.println(c.certificateId() + " " + c.domainName() + " " + c.status() + " " + c.subscriptionExpiresAt()));

// The approver addresses of a certificate you already have
List<String> approverEmails = client.ssl().getApproverEmails(certificateId);
```

**Yenileme** (bu işlem bakiyenizden ücret alır)

```java
// Allowed from 30 days before details.subscription().expiresAt(). The renewal is a new certificate with its own ID.
SslOperationResult renewal = client.ssl().renew(certificateId);
client.ssl().configure(new ConfigureCertificateRequest(renewal.certificateId(), newCsr, certificateContact)
        .validationMethod(ValidationMethod.EMAIL)
        .approverEmail(approvers.get(0))
        .webServerType(WebServerType.APACHE));
```

Satın alınan veya yenilenen bir sertifika 15 gün içinde yapılandırılmalıdır; aksi halde iptal edilir. Yenilemek için
önce `renew`, ardından yeni kimlikle `configure` çağırın. `purchase` ile yeniden satın almayın: bu, satın alma fiyatı
üzerinden ayrı bir satın alma olur ve eski sertifika hiçbir zaman yenilenmiş olarak işaretlenmez. Boş bir sertifika
kimliği, hiçbir şey gönderilmeden önce `IllegalArgumentException` fırlatır.

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

### Siparişler ve ürünler

```java
Order order = client.orders().get("ORDER-CODE");

client.products().getTld("com").ifPresent(com -> System.out.println(
        com.registrationPrice(1).map(Tld.Price::price).orElse(null) + ", DNSSEC: " + com.supportsDnssec()));

client.products().listAllTlds(new ListTldsRequest().currency("USD")).forEach(tld -> System.out.println(tld.name()));

ProductPrice price = client.products().getPrice("com", OrderType.RENEW, 1);
Page<SslProduct> ssl = client.products().listSslProducts(new ListSslProductsRequest().isWildcard(true));
```

<a id="requests-and-baserequest"></a>

## İstekler ve `BaseRequest`

Her işlem bir istek sınıfıdır; örneğin `RenewDomainRequest`, `ListDomainsRequest` veya `AddDnsRecordRequest`.
Yukarıdaki istemci metotları bunları sizin yerinize oluşturur; isterseniz bunları kendiniz de oluşturup gönderebilirsiniz:

```java
RenewDomainRequest request = new RenewDomainRequest("example.com", 2);
System.out.println(request + " is " + request.safety());   // "POST domains/renew is BILLABLE"

DomainOperationResult result = client.send(request);

// The same, with the HTTP details: correlation ID, soft-quota state, attempts, duration
ApiResponse<DomainOperationResult> response = client.sendWithResponse(request);
System.out.println(response.correlationId() + " quota " + response.quota());
```

Tüm istekler iki temel sınıftan birinden türer:

| Temel sınıf | Ne için |
|---|---|
| `BaseRequest<T>` | Her işlem. `T` yanıt tipidir; hiçbir şey döndürmeyen işlemler (kilitleme, kilit açma, DNS kaydı ekleme, ...) için `Void`. |
| `PagedRequest<T>` | Liste işlemleri. `skipCount`, `maxResultCount` ve `sorting` ekler, `listAll` ile çalışır. |

Bir istek, ne olduğunu birkaç override ile bildirir: `method()`, `path()` (`/api/v1/` altında) ve `safety()`. Sorgu
parametreleri `addQueryParameters` içinde eklenir, JSON gövdesi `body()` metodundan gelir (`JsonBody` bir gövde oluşturur
ve null değerleri dışarıda bırakır), göndermeden önceki denetimler ise `validate()` içine yazılır. SDK'nın henüz
kapsamadığı bir uç noktayı çağırmak için kendi isteğinizi yazın:

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

İsteğiniz, yerleşik isteklerin aldığı her şeyi alır: kimlik bilgileri, güvenlik sınıfına göre yeniden denemeler, hız
ayarlama, salt-okunur mod ve hata yönetimi. Yanıt tipi `extends BaseRequest<DomainNotes>` ifadesinden okunur;
`responseType()` metodunu yalnızca generic istek sınıfları için override edin. Bir yazma işlemi tekrarlanırken
güvenliyse `isIdempotent()` metodunu override edin.

<a id="errors"></a>

## Hatalar

Başarısız her istek bir `DomainNameApiException` (unchecked) fırlatır. Geçersiz argümanlar, hiçbir şey gönderilmeden
önce `IllegalArgumentException` fırlatır.

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

| Metot | Anlamı |
|---|---|
| `kind()` | Sabit bir sınıflandırma (aşağıdaki tablo). Mesaja göre değil, buna göre dallanın. |
| `code()` | API'nin hata kodu (örneğin `Dna.DomainService:Domain:10007`) veya aşağıdaki SDK kodlarından biri. |
| `getMessage()` | Ne olduğu. Kalıp mesajların yerine API'nin ayrıntıları konur. |
| `statusCode()` | HTTP durumu; yanıt gelmediyse null. |
| `details()`, `validationErrors()` | API'den gelen ek ayrıntılar ve alan düzeyindeki sorunlar. |
| `correlationId()` | Geçidin `X-Correlation-Id` değeri. Destekle iletişime geçerken bunu belirtin. |
| `hint()` | Sonra ne yapılacağına dair kısa bir öneri. |
| `isRetryable()` | Aynı isteği daha sonra tekrarlamanın başarılı olup olamayacağı. |
| `isOutcomeUnknown()` | Bir şeyi değiştiren istek ağda başarısız oldu, bu yüzden çalışmış olabilir. |
| `operation()`, `safety()` | Başarısız olan istek (`POST domains/renew`) ve onun güvenlik sınıfı. |
| `retryAfter()`, `responseBody()` | Geçidin `Retry-After` değeri ve ham yanıt. |

| `kind()` | Ne zaman |
|---|---|
| `AUTHENTICATION` | Yanlış veya eksik kimlik bilgileri ya da bu ortamda bilinmeyen veya askıya alınmış bir bayi (401, yönlendirme). |
| `FORBIDDEN` | Kimlik doğrulandı ama izin yok (403). |
| `NOT_FOUND` | Alan adı, iletişim kişisi, sipariş veya başka bir kaynak yok (404). |
| `VALIDATION` | API girdiyi reddetti ya da bir iş kuralı başarısız oldu: hatalı alan, kilitli alan adı, yetersiz bakiye (400, 422). |
| `CONFLICT` | Kaynağın durumu işleme izin vermiyor (409). |
| `RATE_LIMITED` | Otomatik yeniden denemelerden sonra hâlâ hız sınırında (429). |
| `SERVER` | Sunucu tarafı hata (5xx). Çoğu zaman geçicidir. |
| `NETWORK`, `TIMEOUT` | Yanıt yok: DNS, TLS, proxy, bağlantı sıfırlanması veya zaman aşımı içinde yanıt gelmemesi. |
| `READ_ONLY` | Salt-okunur mod tarafından engellendi; hiçbir şey gönderilmedi. |
| `UNKNOWN` | SDK'nın okuyamadığı bir yanıt dahil, geri kalan her şey. |

SDK'nın kendisinin belirlediği kodlar:

| Kod | Anlamı |
|---|---|
| `INVALID_RESELLER` | Geçit, askıya alma sayfasına yönlendirdi: reseller ID **bu ortamda** bilinmiyor, etkin değil veya askıya alınmış. |
| `INVALID_API_KEY` | Anahtar bayiyle eşleşmiyor. Canlı ortam ve OTE anahtarları farklıdır. |
| `MISSING_API_KEY`, `MISSING_RESELLER_ID`, `NO_CREDENTIALS` | Bir kimlik bilgisi boş. |
| `SUBRESELLER_NOT_ALLOWED` | Alt bayi hesapları API'yi kullanamaz. Ana bayinin kimlik bilgilerini kullanın. |
| `UNAUTHORIZED` | Diğer tüm 401 yanıtları. |
| `RATE_LIMITED` | Yeniden denemelerden sonra 429. |
| `NETWORK`, `TIMEOUT` | Yanıt gelmedi. |
| `READ_ONLY` | Salt-okunur mod tarafından engellendi. |
| `EMPTY_RESPONSE`, `INVALID_RESPONSE` | Başarılı bir yanıt boştu veya okunamadı. |
| `HTTP_<status>` | API hata kodu göndermedi. |

Bir istek beklerken thread kesintiye uğrarsa (interrupt), SDK kesinti bayrağını yeniden ayarlar ve bir API hatası yerine
`CancellationException` fırlatır.

<a id="safety-retries-and-rate-limits"></a>

## Güvenlik, yeniden denemeler ve hız sınırları

Her isteğin bir güvenlik sınıfı vardır; `dna` CLI'nin kullandığıyla aynıdır:

| `OperationSafety` | Anlamı | Örnekler |
|---|---|---|
| `READ` | API POST veya PUT kullansa bile yan etkisi yok | arama, ayrıntılar, listeler, yenileme ve transfer denetimleri, CSR üretimi |
| `WRITE` | Geri alınabilen ücretsiz bir değişiklik | DNS kayıtları, kilit, gizlilik, nameserver'lar, iletişim bilgileri |
| `BILLABLE` | Bakiyenizden ücret alır | kayıt, yenileme, transfer, geri yükleme, SSL satın alma ve yenileme, hosting siparişi |
| `DESTRUCTIVE` | Bir şeyi siler veya geri alması zordur | DNS kaydı silme, DNSSEC kaldırma, sertifika iptali |

- **Salt-okunur mod** (`readOnly(true)` veya `DNA_READ_ONLY=true`), `READ` olmayan her şeyi gönderilmeden önce engeller.
  Keşif yaparken, raporlarda ve panolarda kullanın.
- **Yeniden denemeler.** 429 yanıtları her zaman, `Retry-After` dikkate alınarak yeniden denenir. Bu, ücretli istekler
  için bile güvenlidir, çünkü geçit bunları hiçbir şey çalışmadan önce reddeder. Ağ hataları, zaman aşımları ve
  502/503/504 yanıtları yalnızca okumalarda (ve nameserver değiştirme gibi tekrarlanması güvenli yazmalarda)
  yeniden denenir. Bir kayıt veya yenileme asla iki kez gönderilmez.
- **Hız ayarlama.** Geçit, anahtar başına saniyede 5 isteğe, alan adı aramalarında saniyede 2 isteğe ve DNS bölge
  çağrılarında saniyede 20 isteğe izin verir. İstemci, istekleri bu sınırların altında kalacak şekilde aralıklar
  (bölge çağrıları 1 saniye arayla). Alan adı bilgisi ve TLD listesi hız sınırına tabi değildir.
- **Yumuşak kota.** Aramalar ve bilgi sorguları 1 puan, diğer her şey 10 puan harcar. 200 puanın üzerinde geçit her
  isteği 5 saniyeye kadar yavaşlatır; puanlar dakikada %20 azalır. `sendWithResponse`, durumu `quota()` içinde gösterir.

<a id="asynchronous-calls"></a>

## Asenkron çağrılar

İstemci senkrondur: her metot yanıt gelene kadar bekler (bloklar); bu, sunucu kodu ve sanal thread'ler için uygundur.
`sendAsync` ve `sendWithResponseAsync`, bir isteği istemcinin executor'ında çalıştırır ve bir `CompletableFuture` döndürür:

```java
CompletableFuture<DomainDetails> details = client.sendAsync(new GetDomainRequest("example.com"));
details.thenAccept(d -> System.out.println(d.expirationDate()));
```

Future, senkron metotların fırlattığı istisnaların aynısıyla başarısız olur (`join()` bunları bir `CompletionException`
içine sarar). Kendi executor'ınızı `builder().executor(...)` ile ayarlayın; örneğin Java 21'de
`Executors.newVirtualThreadPerTaskExecutor()`.

<a id="testing-your-code"></a>

## Kodunuzu test etme

SDK'nın tek ihtiyacı bir HTTP uç noktasıdır. SDK'yı kullanan kodu API'yi çağırmadan test etmek için `baseUri`'yi yerel
bir sunucuya, örneğin JDK'nın yerleşik sunucusuna yönlendirin:

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

`httpClient(...)` ile kendi `HttpClient` alt sınıfınızı da verebilir, mock'larınız için istisnaları
`DomainNameApiException.builder(...)` ile oluşturabilirsiniz. Gerçek API'ye karşı testler için, kendi anahtarıyla OTE'yi kullanın.

<a id="project-layout"></a>

## Proje yapısı

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

Her alan paketi aynı biçimdedir: bir `XxxClient`, işlem başına bir istek sınıfı (`XxxRequest`) ve yanıtlarının
modelleri ile enum'ları.

<a id="development"></a>

## Geliştirme

```bash
mvn verify                          # build and run the tests
mvn -Pexamples -DskipTests compile  # also compile the examples
```

Yerel bir JDK yoksa Docker kullanın:

```bash
docker run --rm -v "$PWD":/src -w /src maven:3.9-eclipse-temurin-17 mvn -B verify
```

Testler gerçek API'yi asla çağırmaz: sahte bir `HttpClient` ve aktarım katmanı için 127.0.0.1 üzerinde yerel bir HTTP
sunucusu kullanır. CI, testleri Java 17, 21 ve 25 üzerinde çalıştırır.

`src/test/resources/openapi.json`, `dna` CLI'nin içine gömdüğü API dokümanıdır. `SpecCoverageTest`, içindeki her
işlemin, CLI'nin güvenlik sınıfıyla birlikte tam olarak bir istek sınıfına sahip olduğunu ve her isteğin yalnızca
dokümanda tanımlanan sorgu parametrelerini ve gövde özelliklerini gönderdiğini denetler. API'ye yeni bir işlem
eklendiğinde dokümanı güncelleyin; istek yazılana kadar test başarısız olur.

<a id="upgrading-from-1x"></a>

## 1.x'ten yükseltme

2.0 yeni bir SDK'dır. 1.x, PHP kütüphanesinin bir uyarlamasıydı ve tipsiz `Map` zarfları döndürüyordu; 2.0'da tipli
istekler ve modeller, istisnalar ve bir builder vardır.

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

Java alt sürüm gereksinimi 11'den 17'ye yükselir ve Jackson artık bir bağımlılıktır. 1.x, hata raporlarını Sentry'ye
gönderiyordu; 2.0, API dışında hiçbir yere hiçbir şey göndermez.

## Bağlantılar

- [`dna` CLI ve MCP sunucusu](https://github.com/domainreseller/cli): aynı API, terminalden ve yapay zeka ajanlarından.
- [API dokümantasyonu](https://www.domainnameapi.com/docs/)
- [Değişiklik günlüğü](CHANGELOG.md)

## Lisans

Apache License 2.0. Bkz. [LICENSE](LICENSE).
