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

[Domain Name API](https://www.domainnameapi.com) reseller platforması üçün rəsmi Java SDK. REST API-nin bütün
67 əməliyyatını tipli klientlər və modellərlə əhatə edir: domenlər, əlaqələr, DNS, DNSSEC, SSL sertifikatları,
hostinq, sifarişlər, məhsullar və hesabınız.

```java
DomainNameApiClient client = DomainNameApiClient.create(resellerId, apiKey, ApiEnvironment.OTE);

DomainSearchResult search = client.domains().search("example.com");
if (search.info() != null && search.info().isAvailable()) {
    client.domains().register("example.com", contact, List.of("tr.apiname.com", "eu.apiname.com"));
}
```

- **Hər şey tiplidir.** Hər əməliyyat bir sorğu sinfidir, hər cavab isə Javadoc sənədləşməsi olan record-dur. Map-lər və JSON parsinq yoxdur.
- **Öz sorğularınız.** Hər sorğu `BaseRequest`-dən törəyir. SDK-nın hələ əhatə etmədiyi endpoint üçün ondan alt sinif yaradın.
- **Pulla təhlükəsiz işləyir.** Hər sorğu bildirir ki, oxuyur, yazır, balansınızdan ödəniş alır, yoxsa nəyisə silir. Ödənişli sorğular heç vaxt təkrarlanmır, yalnız oxuma rejimi isə hər dəyişikliyi bloklayır.
- **Daxili davamlılıq.** Sorğu limitlərinə avtomatik əməl edilir, 429 cavabları təkrarlanır, oxuma sorğularında isə şəbəkə xətaları təkrarlanır.
- **Aydın xətalar.** Sabit növü, API-nin xəta kodu, sahə səviyyəsində yoxlama xətaları, korrelyasiya ID-si və məsləhəti olan tək bir unchecked istisna.
- **Müasir Java.** Java 17+, record-lar, tənbəl (lazy) `Stream` səhifələməsi, `CompletableFuture` variantları, `java.net.http` üzərində thread-safe klient.
- **Məxfi.** Telemetriya yoxdur. API-yə göndərdiyiniz sorğulardan başqa heç nə prosesinizdən kənara çıxmır.

O, [`dna`, Domain Name API CLI-nin](https://github.com/domainreseller/cli) Java qarşılığıdır: eyni API
sənədi, eyni təhlükəsizlik sinifləri, təkrar qaydaları və xəta kodları.

## Məzmun

- [Quraşdırma](#install)
- [Giriş məlumatları](#credentials)
- [Sürətli başlanğıc](#quick-start)
- [Konfiqurasiya](#configuration)
- [İstifadə](#usage): [hesab](#account) · [domenlər](#domains) · [əlaqələr](#contacts) · [DNS](#dns) · [DNSSEC](#dnssec) · [SSL](#ssl) · [hostinq](#hosting) · [sifarişlər və məhsullar](#orders-and-products)
- [Sorğular və `BaseRequest`](#requests-and-baserequest)
- [Xətalar](#errors)
- [Təhlükəsizlik, təkrarlar və sorğu limitləri](#safety-retries-and-rate-limits)
- [Asinxron çağırışlar](#asynchronous-calls)
- [Kodunuzu test etmək](#testing-your-code)
- [Layihənin strukturu](#project-layout)
- [İnkişaf](#development)
- [1.x versiyasından keçid](#upgrading-from-1x)

<a id="install"></a>

## Quraşdırma

> [!IMPORTANT]
> **Həmişə [son buraxılışdan](https://github.com/domainreseller/java-dna/releases/latest) istifadə edin.** Buraxılış paketləri versiyalaşdırılır və test edilir.
> Yaşıl **Code → Download ZIP** düyməsi isə inkişaf branch-ini yükləyir.

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

Artefakt ictimai repozitoriyada əlçatan olana qədər onu buraxılışın mənbə kodundan lokal Maven repozitoriyanıza
quraşdırın (`mvn -DskipTests install`) və Gradle repozitoriyalarınıza `mavenLocal()` əlavə edin.

**Tələblər:** Java 17 və ya daha yeni versiya. Yeganə asılılıq JSON üçün [Jackson](https://github.com/FasterXML/jackson-databind)
(`jackson-databind`) kitabxanasıdır; HTTP JDK-nın `java.net.http.HttpClient` sinfi üzərindən işləyir.

<a id="credentials"></a>

## Giriş məlumatları

API hər sorğunu iki dəyərlə autentifikasiya edir, SDK onları sizin yerinizə göndərir:

| Dəyər | Header | Harada tapılır |
|---|---|---|
| Reseller ID (UUID) | `__reseller` | Reseller paneli → **API settings** |
| API açarı | `X-API-KEY` | Reseller paneli → **API settings** |

> [!NOTE]
> Bunlar **API giriş məlumatlarıdır**: panelə giriş məlumatlarınız burada işləmir. **Canlı mühit və OTE (test mühiti)
> fərqli API açarlarından istifadə edir.** Birini digərində işlətmək `INVALID_API_KEY` və ya `INVALID_RESELLER` xətası ilə nəticələnir.

<a id="quick-start"></a>

## Sürətli başlanğıc

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

[examples](examples) qovluğunda bu ssenarilər üçün işə salına bilən proqramlar var. Onlar `DNA_*` mühit
dəyişənlərini oxuyur, ona görə onları OTE-yə yönəldin:

```bash
export DNA_RESELLER_ID=... DNA_API_KEY=... DNA_ENV=ote
```

<a id="configuration"></a>

## Konfiqurasiya

Klient yaratmağın dörd yolu var:

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

| Builder metodu | Defolt | Mənası |
|---|---|---|
| `credentials(resellerId, apiKey)` | (məcburi) | Giriş məlumatları. |
| `environment(...)` | `PRODUCTION` | `PRODUCTION` (`https://api.domainresellerapi.com`) və ya `OTE` (`https://ote.domainresellerapi.com`). |
| `baseUri(...)` | yoxdur | Mühitin ünvanını əvəz edir, məsələn proxy üçün. |
| `timeout(...)` | 60 s | Bir cəhdin vaxt limiti. |
| `maxRetries(...)` | 2 | Uğursuz sorğunun neçə dəfə təkrarlanacağı ([təkrarlara](#safety-retries-and-rate-limits) baxın). 0 təkrarları söndürür. |
| `maxRetryDelay(...)` | 30 s | Təkrardan əvvəl ən uzun gözləmə. |
| `paceRequests(...)` | `true` | Gateway-in sorğu limitlərinin altında qalmaq üçün sorğular arasında fasilə qoyur. |
| `readOnly(...)` | `false` | Oxuma olmayan hər sorğunu göndərilməzdən əvvəl bloklayır. |
| `userAgent(...)` | `DomainNameAPI-Java/<version>` | `User-Agent` header-i. |
| `app(name, version)` | `java`, SDK versiyası | SDK-dan istifadə edən tətbiq; `X-App` və `X-Version` kimi göndərilir ([aşağıya](#tətbiqinizi-tanıtmaq) baxın). |
| `httpClient(...)` | SDK-nın özününkü | Öz `java.net.http.HttpClient` obyektiniz (proxy, TLS, executor). |
| `executor(...)` | paylaşılan daemon pool | `sendAsync` metodlarının işlədiyi yer. |

`fromEnvironment()` bunları oxuyur:

| Dəyişən | Mənası |
|---|---|
| `DNA_RESELLER_ID`, `DNA_API_KEY` | Giriş məlumatları. |
| `DNA_ENV` | `production` və ya `ote` (həmçinin `prod`, `live`, `test`, `sandbox`). |
| `DNA_TEST_MODE` | `true`, `1`, `on` və ya `yes` OTE-ni seçir. `DNA_ENV` təyin edilməyibsə istifadə olunur. |
| `DNA_BASE_URL` | İstəyə bağlı baza ünvanı əvəzləməsi. |
| `DNA_READ_ONLY` | `true` yalnız oxuma rejimini aktiv edir. |
| `DNA_APP`, `DNA_APP_VERSION` | SDK-dan istifadə edən tətbiq və onun versiyası (`X-App`, `X-Version`). |

### Tətbiqinizi tanıtmaq

Hər sorğu API-yə onu hansı inteqrasiyanın göndərdiyini bildirən iki header daşıyır: `X-App`, kiçik hərflərə çevrilmiş və
yalnız `a-z`, `0-9`, `.`, `_` və `-` simvolları saxlanılmış tətbiq adı, və `X-Version`, yalnız rəqəm və nöqtələrdən
ibarət versiyası. Defolt olaraq bunlar `java` və SDK versiyasıdır. Panelinizi və ya modulunuzu `app(name, version)` və
ya `DNA_APP`/`DNA_APP_VERSION` ilə göstərin; boş və ya yanlış ad `java`-ya, boş versiya isə SDK versiyasına qayıdır.
Başqa heç nə göndərilmir və `User-Agent` dəyişmir.

```java
DomainNameApiClient client = DomainNameApiClient.builder()
        .credentials(resellerId, apiKey)
        .app("WiseCP", "V2.0.20-beta")   // sends X-App: wisecp, X-Version: 2.0.20
        .build();
```

Klient thread-safe-dir. Hər giriş məlumatı dəsti üçün bir dənə yaradın və tətbiqin bütün işləmə müddətində təkrar istifadə edin.

<a id="usage"></a>

## İstifadə

API-nin hər sahəsinin öz klienti var. Metodlar sadə arqumentlər qəbul edir; daha çox seçim üçün hər metodun sorğu obyektini
qəbul edən overload-u (və ya qardaş metodu) də var. Balansınızdan ödəniş alan metodlar bunu Javadoc-larında bildirir.

<a id="account"></a>

### Hesab

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

### Domenlər

**Axtarış**

```java
DomainSearchResult one = client.domains().search("example.com", "USD");
boolean available = one.info() != null && one.info().isAvailable();

BulkSearchResult many = client.domains().bulkSearch(List.of("example.com", "example.net", "example.com.tr"));
for (DomainAvailability domain : many.available()) {
    System.out.println(domain.domainName() + ": " + domain.price() + " " + domain.currency());
}
```

**Siyahı və məlumat**

```java
Page<Domain> page = client.domains().list(new ListDomainsRequest().maxResultCount(50).statuses(DomainStatus.ACTIVE));

client.domains().listAll(new ListDomainsRequest().domainName("example"))
        .forEach(d -> System.out.println(d.domainName() + " " + d.status() + " " + d.expirationDate()));

DomainDetails details = client.domains().get("example.com");
System.out.println(details.authCode());
details.contact(ContactType.REGISTRANT).ifPresent(c -> System.out.println(c.email()));
```

**Qeydiyyat, yenilənmə, transfer, bərpa** (bunlar balansınızdan ödəniş alır)

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

Sorğular göndərilməzdən əvvəl yoxlanılır. Dörd əlaqə rolunun hamısı olmayan qeydiyyat, e-poçt ünvanı olmayan əlaqə
və ya 0 illik müddət `IllegalArgumentException` atır və API-yə heç nə çatmır.

> [!NOTE]
> API qeydiyyat, yenilənmə və ya transfer sorğusuna HTTP 200 və `"success": false` ilə cavab verə bilər (məsələn,
> reyestr domeni rədd etdikdə). SDK bu cavabı olduğu kimi qaytarır: `DomainOperationResult.success()` və
> `message()` dəyərlərini yoxlayın.

**Parametrlər**

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

**URL yönləndirmə, child nameserver-lər və sənədlər**

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

### Əlaqələr

Əlaqə handle-ları təkrar istifadə edilə bilən əlaqələrdir. Birini bir dəfə yaradın və çox domen üçün istifadə edin.

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

Platforma nameserver-lərindəki domenlərin zona qeydləri. Domen həmin nameserver-lərdən istifadə etməyə başladıqdan sonra
qeydlər hamıya açıq şəkildə həll olunur; bunu `apply` həyata keçirir.

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

SDK API-nin rədd edəcəyi halları əvvəlcədən yoxlayır: apex-də CNAME, üç hissəsi olmayan CAA dəyəri, 1–86400 aralığından
kənar TTL. Zona çağırışları bir-birindən 1 saniyə aralı göndərilir, bu gateway-in limitidir.

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

Bir domenin bir neçə sertifikatı ola bilər, buna görə mövcud sertifikat domen adı ilə deyil, **sertifikat ID-si**
(bir `UUID`) ilə göstərilir. `purchase` və `renew` bu ID-ni qaytarır: onu saxlayın. `list` artıq sahib olduğunuz
sertifikatların ID-lərini tapır.

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

**Sertifikatların siyahısı və ID-nin tapılması**

```java
Page<CertificateListItem> page = client.ssl().list(new ListCertificatesRequest().domainName("example.com"));   // newest first

client.ssl().listAll().forEach(c ->
        System.out.println(c.certificateId() + " " + c.domainName() + " " + c.status() + " " + c.subscriptionExpiresAt()));

// The approver addresses of a certificate you already have
List<String> approverEmails = client.ssl().getApproverEmails(certificateId);
```

**Yenilənmə** (bu əməliyyat balansınızdan ödəniş alır)

```java
// Allowed from 30 days before details.subscription().expiresAt(). The renewal is a new certificate with its own ID.
SslOperationResult renewal = client.ssl().renew(certificateId);
client.ssl().configure(new ConfigureCertificateRequest(renewal.certificateId(), newCsr, certificateContact)
        .validationMethod(ValidationMethod.EMAIL)
        .approverEmail(approvers.get(0))
        .webServerType(WebServerType.APACHE));
```

Alınmış və ya yenilənmiş sertifikat 15 gün ərzində konfiqurasiya edilməlidir; əks halda ləğv olunur. Yeniləmək üçün
əvvəlcə `renew`, sonra yeni ID ilə `configure` çağırın. `purchase` ilə yenidən almayın: bu, alış qiyməti ilə ayrıca
bir alış olar və köhnə sertifikat heç vaxt yenilənmiş kimi işarələnməz. Boş sertifikat ID-si heç nə göndərilməzdən
əvvəl `IllegalArgumentException` atır.

<a id="hosting"></a>

### Hostinq

```java
List<HostingPackage> packages = client.hosting().listPackages();
HostingCreateResult created = client.hosting().create("example.com", "starter-linux");   // charges the balance
HostingStatus status = client.hosting().getStatus("example.com");                         // panel access once ready
HostingService hosting = client.hosting().get("example.com");
List<ServicePlan> plans = client.hosting().listPlans();
```

<a id="orders-and-products"></a>

### Sifarişlər və məhsullar

```java
Order order = client.orders().get("ORDER-CODE");

client.products().getTld("com").ifPresent(com -> System.out.println(
        com.registrationPrice(1).map(Tld.Price::price).orElse(null) + ", DNSSEC: " + com.supportsDnssec()));

client.products().listAllTlds(new ListTldsRequest().currency("USD")).forEach(tld -> System.out.println(tld.name()));

ProductPrice price = client.products().getPrice("com", OrderType.RENEW, 1);
Page<SslProduct> ssl = client.products().listSslProducts(new ListSslProductsRequest().isWildcard(true));
```

<a id="requests-and-baserequest"></a>

## Sorğular və `BaseRequest`

Hər əməliyyat bir sorğu sinfidir, məsələn `RenewDomainRequest`, `ListDomainsRequest` və ya `AddDnsRecordRequest`.
Yuxarıdakı klient metodları onları sizin üçün qurur, siz də onları özünüz yarada və göndərə bilərsiniz:

```java
RenewDomainRequest request = new RenewDomainRequest("example.com", 2);
System.out.println(request + " is " + request.safety());   // "POST domains/renew is BILLABLE"

DomainOperationResult result = client.send(request);

// The same, with the HTTP details: correlation ID, soft-quota state, attempts, duration
ApiResponse<DomainOperationResult> response = client.sendWithResponse(request);
System.out.println(response.correlationId() + " quota " + response.quota());
```

Bütün sorğular iki baza sinfindən birindən törəyir:

| Baza sinif | Nə üçün |
|---|---|
| `BaseRequest<T>` | Hər əməliyyat. `T` cavab tipidir; heç nə qaytarmayan əməliyyatlar (kilid, kilidi açmaq, DNS qeydi əlavə etmək, ...) üçün `Void`. |
| `PagedRequest<T>` | Siyahı əməliyyatları. `skipCount`, `maxResultCount` və `sorting` əlavə edir və `listAll` ilə işləyir. |

Sorğu özü haqqında bir neçə override ilə məlumat verir: `method()`, `path()` (`/api/v1/` altında) və `safety()`. Query
parametrləri `addQueryParameters` içində əlavə olunur, JSON gövdəsi `body()`-dən gəlir (`JsonBody` onu qurur və null
dəyərləri buraxır), göndərmədən əvvəlki yoxlamalar isə `validate()` içinə yazılır. SDK-nın hələ əhatə etmədiyi endpoint-i çağırmaq üçün özünüz yazın:

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

Sizin sorğunuz daxili sorğuların aldığı hər şeyi alır: giriş məlumatları, təhlükəsizlik sinfinə görə təkrarlar, sorğu tempi,
yalnız oxuma rejimi və xəta emalı. Cavab tipi `extends BaseRequest<DomainNotes>` ifadəsindən oxunur; `responseType()`-ı
yalnız generic sorğu sinifləri üçün override edin. Yazma sorğusunun təkrarlanması təhlükəsizdirsə `isIdempotent()` override edin.

<a id="errors"></a>

## Xətalar

Hər uğursuz sorğu `DomainNameApiException` (unchecked) atır. Yanlış arqumentlər heç nə göndərilməmişdən əvvəl
`IllegalArgumentException` atır.

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

| Metod | Mənası |
|---|---|
| `kind()` | Sabit təsnifat (aşağıdakı cədvəl). Mesaja görə deyil, buna görə şaxələnin. |
| `code()` | API-nin xəta kodu (məsələn `Dna.DomainService:Domain:10007`) və ya aşağıdakı SDK kodlarından biri. |
| `getMessage()` | Nə baş verdi. Şablon mesajlar API-nin təfərrüatları ilə əvəz olunur. |
| `statusCode()` | HTTP statusu, cavab gəlmədikdə isə null. |
| `details()`, `validationErrors()` | API-dən gələn əlavə təfərrüat və sahə səviyyəsində problemlər. |
| `correlationId()` | Gateway-in `X-Correlation-Id` dəyəri. Dəstəyə müraciət edəndə onu göstərin. |
| `hint()` | Növbəti addım üçün qısa təklif. |
| `isRetryable()` | Eyni sorğunu sonra təkrarlamağın uğurlu ola bilib-bilməməsi. |
| `isOutcomeUnknown()` | Nəyisə dəyişən sorğu şəbəkədə uğursuz oldu, ona görə icra olunmuş ola bilər. |
| `operation()`, `safety()` | Uğursuz sorğu (`POST domains/renew`) və onun təhlükəsizlik sinfi. |
| `retryAfter()`, `responseBody()` | Gateway-in `Retry-After` dəyəri və xam cavab. |

| `kind()` | Nə vaxt |
|---|---|
| `AUTHENTICATION` | Yanlış və ya çatışmayan giriş məlumatları, ya da bu mühitdə naməlum və ya dayandırılmış reseller (401, yönləndirmə). |
| `FORBIDDEN` | Autentifikasiya olunub, amma icazə yoxdur (403). |
| `NOT_FOUND` | Domen, əlaqə, sifariş və ya digər resurs mövcud deyil (404). |
| `VALIDATION` | API girişi rədd etdi və ya biznes qaydası pozuldu: yanlış sahə, domen kilidlidir, balans çatmır (400, 422). |
| `CONFLICT` | Resursun vəziyyəti əməliyyata icazə vermir (409). |
| `RATE_LIMITED` | Avtomatik təkrarlardan sonra da sorğu limiti aşılıb (429). |
| `SERVER` | Server tərəfində xəta (5xx). Çox vaxt müvəqqətidir. |
| `NETWORK`, `TIMEOUT` | Cavab yoxdur: DNS, TLS, proxy, bağlantının kəsilməsi və ya timeout daxilində cavab gəlməməsi. |
| `READ_ONLY` | Yalnız oxuma rejimi tərəfindən bloklandı; heç nə göndərilmədi. |
| `UNKNOWN` | Qalan hər şey, o cümlədən SDK-nın oxuya bilmədiyi cavab. |

SDK-nın özünün təyin etdiyi kodlar:

| Kod | Mənası |
|---|---|
| `INVALID_RESELLER` | Gateway öz dayandırma səhifəsinə yönləndirdi: reseller ID **bu mühitdə** naməlum, qeyri-aktiv və ya dayandırılmışdır. |
| `INVALID_API_KEY` | Açar reseller ilə uyğun gəlmir. Canlı mühit və OTE açarları fərqlidir. |
| `MISSING_API_KEY`, `MISSING_RESELLER_ID`, `NO_CREDENTIALS` | Giriş məlumatlarından biri boşdur. |
| `SUBRESELLER_NOT_ALLOWED` | Alt reseller hesabları API-dən istifadə edə bilməz. Əsas reseller-in giriş məlumatlarından istifadə edin. |
| `UNAUTHORIZED` | Digər bütün 401 cavabları. |
| `RATE_LIMITED` | Təkrarlardan sonra 429. |
| `NETWORK`, `TIMEOUT` | Cavab gəlmədi. |
| `READ_ONLY` | Yalnız oxuma rejimi tərəfindən bloklandı. |
| `EMPTY_RESPONSE`, `INVALID_RESPONSE` | Uğurlu cavab boş idi və ya oxunmadı. |
| `HTTP_<status>` | API xəta kodu göndərmədi. |

Sorğu gözləyərkən thread kəsilərsə (interrupt), SDK interrupt bayrağını yenidən qoyur və API xətası əvəzinə
`CancellationException` atır.

<a id="safety-retries-and-rate-limits"></a>

## Təhlükəsizlik, təkrarlar və sorğu limitləri

Hər sorğunun təhlükəsizlik sinfi var, `dna` CLI-nin istifadə etdiyi ilə eynidir:

| `OperationSafety` | Mənası | Nümunələr |
|---|---|---|
| `READ` | Yan təsiri yoxdur, API POST və ya PUT istifadə etsə belə | axtarış, məlumat, siyahılar, yenilənmə və transfer yoxlamaları, CSR yaradılması |
| `WRITE` | Geri qaytarıla bilən pulsuz dəyişiklik | DNS qeydləri, kilid, məxfilik, nameserver-lər, əlaqələr |
| `BILLABLE` | Balansınızdan ödəniş alır | qeydiyyat, yenilənmə, transfer, bərpa, SSL alışı və yenilənməsi, hostinq sifarişi |
| `DESTRUCTIVE` | Nəyisə silir və ya geri qaytarmaq çətindir | DNS qeydinin silinməsi, DNSSEC-in götürülməsi, sertifikatın ləğvi |

- **Yalnız oxuma rejimi** (`readOnly(true)` və ya `DNA_READ_ONLY=true`) `READ` olmayan hər şeyi göndərilməzdən əvvəl bloklayır.
  Onu araşdırma zamanı, hesabatlarda və dashboard-larda istifadə edin.
- **Təkrarlar.** 429 cavabları həmişə təkrarlanır, `Retry-After` nəzərə alınır. Bu, ödənişli sorğular üçün də təhlükəsizdir,
  çünki gateway onları heç nə icra olunmamış rədd edir. Şəbəkə xətaları, timeout-lar və 502/503/504 cavabları yalnız
  oxuma sorğuları üçün (və təkrarlanması təhlükəsiz olan yazma sorğuları, məsələn nameserver-lərin əvəzlənməsi üçün) təkrarlanır.
  Qeydiyyat və ya yenilənmə heç vaxt iki dəfə göndərilmir.
- **Templəmə.** Gateway hər açar üçün saniyədə 5 sorğuya, domen axtarışları üçün saniyədə 2, DNS zona çağırışları üçün
  saniyədə 20 sorğuya icazə verir. Klient bu limitlərin altında qalmaq üçün sorğular arasında fasilə qoyur (zona çağırışları 1 saniyə aralı). Domen məlumatı və
  TLD siyahısı üçün sorğu limiti yoxdur.
- **Yumşaq kvota.** Axtarışlar və məlumat sorğuları 1 xal, qalan hər şey 10 xal çəkir. 200 xaldan sonra gateway hər
  sorğunu 5 saniyəyə qədər ləngidir; xallar dəqiqədə 20% azalır. `sendWithResponse` vəziyyəti `quota()` içində göstərir.

<a id="asynchronous-calls"></a>

## Asinxron çağırışlar

Klient sinxrondur: hər metod cavab gələnə qədər bloklanır; bu, server kodu və virtual thread-lər üçün uyğundur.
`sendAsync` və `sendWithResponseAsync` sorğunu klientin executor-unda icra edir və `CompletableFuture` qaytarır:

```java
CompletableFuture<DomainDetails> details = client.sendAsync(new GetDomainRequest("example.com"));
details.thenAccept(d -> System.out.println(d.expirationDate()));
```

Future sinxron metodların atdığı eyni istisnalarla uğursuz olur (`join()` onları `CompletionException` içinə bükür).
Öz executor-unuzu `builder().executor(...)` ilə təyin edin, məsələn Java 21-də `Executors.newVirtualThreadPerTaskExecutor()`.

<a id="testing-your-code"></a>

## Kodunuzu test etmək

SDK-ya yalnız HTTP endpoint lazımdır. SDK-dan istifadə edən kodu API-ni çağırmadan test etmək üçün `baseUri`-ni lokal
serverə, məsələn JDK-nın daxili serverinə yönəldin:

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

Həmçinin `httpClient(...)` ilə öz `HttpClient` alt sinfinizi ötürə, mock-larınız üçün istisnaları isə
`DomainNameApiException.builder(...)` ilə yarada bilərsiniz. Real API-yə qarşı testlər üçün öz açarı olan OTE-dən istifadə edin.

<a id="project-layout"></a>

## Layihənin strukturu

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

Hər sahə paketi eyni formadadır: bir `XxxClient`, hər əməliyyat üçün bir sorğu sinfi (`XxxRequest`) və cavablarının
modelləri ilə enum-ları.

<a id="development"></a>

## İnkişaf

```bash
mvn verify                          # build and run the tests
mvn -Pexamples -DskipTests compile  # also compile the examples
```

Lokal JDK yoxdursa, Docker istifadə edin:

```bash
docker run --rm -v "$PWD":/src -w /src maven:3.9-eclipse-temurin-17 mvn -B verify
```

Testlər heç vaxt real API-ni çağırmır: saxta `HttpClient`, transport üçün isə 127.0.0.1 üzərində lokal HTTP server
istifadə edirlər. CI onları Java 17, 21 və 25-də işə salır.

`src/test/resources/openapi.json` `dna` CLI-nin daxilinə yerləşdirdiyi API sənədidir. `SpecCoverageTest` yoxlayır ki,
ondakı hər əməliyyatın CLI-nin təhlükəsizlik sinfi ilə tam bir sorğu sinfi var və hər sorğu yalnız sənəddə təsvir olunan
query parametrlərini və gövdə xassələrini göndərir. API yeni əməliyyat qazandıqda sənədi yeniləyin; sorğu yaradılana
qədər test uğursuz olacaq.

<a id="upgrading-from-1x"></a>

## 1.x versiyasından keçid

2.0 yeni SDK-dır. 1.x PHP kitabxanasının port-u idi və tipsiz `Map` zərfləri qaytarırdı; 2.0-da tipli sorğular
və modellər, istisnalar və builder var.

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

Java baza versiyası 11-dən 17-yə qalxır və Jackson indi asılılıqdır. 1.x xəta hesabatlarını Sentry-yə göndərirdi;
2.0 API-dən başqa heç yerə heç nə göndərmir.

## Keçidlər

- [`dna` CLI və MCP serveri](https://github.com/domainreseller/cli): eyni API terminaldan və AI agentlərindən.
- [API sənədləri](https://www.domainnameapi.com/docs/)
- [Dəyişikliklər jurnalı](CHANGELOG.md)

## Lisenziya

Apache License 2.0. [LICENSE](LICENSE) faylına baxın.
