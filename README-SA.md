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

<div dir="rtl">

# حزمة DomainNameAPI لـ Java (java-dna)

</div>

<p>
  <a href="https://github.com/domainreseller/java-dna/actions/workflows/ci.yml"><img src="https://img.shields.io/github/actions/workflow/status/domainreseller/java-dna/ci.yml?branch=main&label=CI&style=flat-square" alt="CI"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-Apache--2.0-0B8DB5?style=flat-square" alt="Apache-2.0"></a>
  <img src="https://img.shields.io/badge/Java-17%2B-0B8DB5?style=flat-square" alt="Java 17+">
</p>

<div dir="rtl">

حزمة SDK الرسمية لـ Java الخاصة بمنصة الموزّعين [Domain Name API](https://www.domainnameapi.com). تغطي جميع عمليات REST API
البالغ عددها 67 عملية بعملاء ونماذج مكتوبة بأنواع صريحة: النطاقات، وجهات الاتصال، وDNS، وDNSSEC، وشهادات SSL،
والاستضافة، والطلبات، والمنتجات، وحسابك.

</div>

```java
DomainNameApiClient client = DomainNameApiClient.create(resellerId, apiKey, ApiEnvironment.OTE);

DomainSearchResult search = client.domains().search("example.com");
if (search.info() != null && search.info().isAvailable()) {
    client.domains().register("example.com", contact, List.of("tr.apiname.com", "eu.apiname.com"));
}
```

<div dir="rtl">

- **كل شيء مكتوب بأنواع صريحة.** كل عملية هي فئة طلب، وكل إجابة هي سجل (record) مزوّد بتوثيق Javadoc. لا خرائط (maps) ولا تحليل JSON.
- **طلباتك الخاصة.** كل طلب يشتق من `BaseRequest`. ورّث منه لنقطة نهاية لا تغطيها الحزمة بعد.
- **آمنة مع الأموال.** يعلن كل طلب ما إذا كان يقرأ، أو يكتب، أو يخصم من رصيدك، أو يحذف شيئًا. لا يُعاد إرسال الطلبات المدفوعة أبدًا، ووضع القراءة فقط يمنع أي تغيير.
- **مرونة مدمجة.** تُراعى حدود معدل الطلبات تلقائيًا، وتُعاد محاولة إجابات 429، وتُعاد محاولة أخطاء الشبكة في عمليات القراءة.
- **أخطاء واضحة.** استثناء واحد غير مُتحقَّق منه (unchecked) مع تصنيف ثابت، ورمز الخطأ من API، وأخطاء التحقق على مستوى الحقول، ومعرّف الارتباط (correlation ID) وتلميح.
- **Java حديثة.** Java 17 فما فوق، والسجلات (records)، وترقيم صفحات كسول عبر `Stream`، ونسخ تعتمد `CompletableFuture`، وعميل آمن للاستخدام من عدة خيوط مبني على `java.net.http`.
- **خصوصية تامة.** لا قياس عن بُعد (telemetry). لا يغادر عمليتك شيء سوى الطلبات التي ترسلها إلى API.

وهي النظير في Java لـ [`dna`، أداة CLI الخاصة بـ Domain Name API](https://github.com/domainreseller/cli): وثيقة API نفسها،
وفئات الأمان نفسها، وقواعد إعادة المحاولة ورموز الأخطاء نفسها.

## المحتويات

- [التثبيت](#install)
- [بيانات الاعتماد](#credentials)
- [البدء السريع](#quick-start)
- [الإعداد](#configuration)
- [الاستخدام](#usage): [الحساب](#account) · [النطاقات](#domains) · [جهات الاتصال](#contacts) · [DNS](#dns) · [DNSSEC](#dnssec) · [SSL](#ssl) · [الاستضافة](#hosting) · [الطلبات والمنتجات](#orders-and-products)
- [الطلبات و`BaseRequest`](#requests-and-baserequest)
- [الأخطاء](#errors)
- [الأمان وإعادة المحاولة وحدود معدل الطلبات](#safety-retries-and-rate-limits)
- [الاستدعاءات غير المتزامنة](#asynchronous-calls)
- [اختبار شيفرتك](#testing-your-code)
- [بنية المشروع](#project-layout)
- [التطوير](#development)
- [الترقية من 1.x](#upgrading-from-1x)

<a id="install"></a>

## التثبيت

> [!IMPORTANT]
> **استخدم دائمًا [أحدث إصدار](https://github.com/domainreseller/java-dna/releases/latest).** حزم الإصدارات مرقّمة بإصدارات ومُختبرة.
> أما زر **Code → Download ZIP** الأخضر فينزّل فرع التطوير بدلًا منها.

الإحداثيات (coordinates): ‏`io.github.domainreseller:domainnameapi:2.0.0`.

</div>

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

<div dir="rtl">

إلى أن تتوفر الحزمة في مستودع عام، ثبّتها في مستودع Maven المحلي لديك من المصدر البرمجي لأحد الإصدارات
(`mvn -DskipTests install`)، وأضف `mavenLocal()` إلى مستودعات Gradle لديك.

**المتطلبات:** Java 17 أو أحدث. التبعية الوحيدة هي [Jackson](https://github.com/FasterXML/jackson-databind)
(`jackson-databind`) لمعالجة JSON؛ أما HTTP فيمر عبر `java.net.http.HttpClient` المدمج في JDK.

<a id="credentials"></a>

## بيانات الاعتماد

تصادق API على كل طلب بقيمتين، وترسلهما الحزمة نيابةً عنك:

| القيمة | الترويسة (Header) | أين تجدها |
|---|---|---|
| Reseller ID (معرّف UUID) | `__reseller` | لوحة الموزّع ← **API settings** |
| API key | `X-API-KEY` | لوحة الموزّع ← **API settings** |

> [!NOTE]
> هذه **بيانات اعتماد API**: بيانات تسجيل الدخول إلى اللوحة لا تعمل هنا. **تستخدم بيئة الإنتاج وبيئة OTE (بيئة الاختبار)
> مفاتيح API مختلفة.** استخدام مفتاح إحداهما مع الأخرى يفشل بالخطأ `INVALID_API_KEY` أو `INVALID_RESELLER`.

<a id="quick-start"></a>

## البدء السريع

</div>

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

<div dir="rtl">

يحتوي مجلد [الأمثلة](examples) على برامج قابلة للتشغيل لهذه السيناريوهات. وهي تقرأ متغيرات البيئة `DNA_*`،
لذا وجّهها إلى OTE:

</div>

```bash
export DNA_RESELLER_ID=... DNA_API_KEY=... DNA_ENV=ote
```

<div dir="rtl">

<a id="configuration"></a>

## الإعداد

هناك أربع طرق لإنشاء عميل:

</div>

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

<div dir="rtl">

| دالة الباني (Builder) | الافتراضي | المعنى |
|---|---|---|
| `credentials(resellerId, apiKey)` | (مطلوب) | بيانات الاعتماد. |
| `environment(...)` | `PRODUCTION` | `PRODUCTION` (‏`https://api.domainresellerapi.com`) أو `OTE` (‏`https://ote.domainresellerapi.com`). |
| `baseUri(...)` | لا شيء | يتجاوز عنوان البيئة، مثلًا عند استخدام وكيل (proxy). |
| `timeout(...)` | 60 ثانية | المهلة الزمنية لمحاولة واحدة. |
| `maxRetries(...)` | 2 | عدد مرات تكرار الطلب الفاشل (انظر [إعادة المحاولة](#safety-retries-and-rate-limits)). القيمة 0 توقف إعادة المحاولة. |
| `maxRetryDelay(...)` | 30 ثانية | أطول انتظار قبل إعادة المحاولة. |
| `paceRequests(...)` | `true` | يوزّع الطلبات زمنيًا لتبقى ضمن حدود معدل الطلبات في البوابة (gateway). |
| `readOnly(...)` | `false` | يمنع كل طلب ليس قراءةً، قبل إرساله. |
| `userAgent(...)` | `DomainNameAPI-Java/<version>` | ترويسة `User-Agent`. |
| `app(name, version)` | `java`، إصدار الحزمة | التطبيق الذي يستخدم الحزمة، يُرسل في `X-App` و`X-Version` (انظر [أدناه](#تعريف-تطبيقك)). |
| `httpClient(...)` | العميل الخاص بالحزمة | `java.net.http.HttpClient` الخاص بك (وكيل، TLS، منفّذ). |
| `executor(...)` | مجمّع خيوط خفية (daemon) مشترك | المكان الذي تعمل فيه دوال `sendAsync`. |

تقرأ `fromEnvironment()` ما يلي:

| المتغير | المعنى |
|---|---|
| `DNA_RESELLER_ID`, `DNA_API_KEY` | بيانات الاعتماد. |
| `DNA_ENV` | `production` أو `ote` (وأيضًا `prod` و`live` و`test` و`sandbox`). |
| `DNA_TEST_MODE` | القيم `true` أو `1` أو `on` أو `yes` تختار OTE. تُستخدم عند عدم ضبط `DNA_ENV`. |
| `DNA_BASE_URL` | تجاوز اختياري للعنوان الأساسي. |
| `DNA_READ_ONLY` | القيمة `true` تفعّل وضع القراءة فقط. |
| `DNA_APP`, `DNA_APP_VERSION` | التطبيق الذي يستخدم الحزمة وإصداره (`X-App`، `X-Version`). |

### تعريف تطبيقك

يحمل كل طلب ترويستين تُخبران الـ API بالتكامل الذي أرسله: `X-App`، اسم التطبيق بأحرف صغيرة مع الإبقاء على `a-z` و`0-9`
و`.` و`_` و`-` فقط، و`X-Version`، إصداره بالأرقام والنقاط فقط. القيمتان الافتراضيتان هما `java` وإصدار الحزمة. حدّد
لوحتك أو وحدتك عبر `app(name, version)` أو `DNA_APP`/`DNA_APP_VERSION`؛ الاسم الفارغ أو غير الصالح يعود إلى `java`،
والإصدار الفارغ يعود إلى إصدار الحزمة. لا يُرسل أي شيء آخر، ولا تتغير ترويسة `User-Agent`.

```java
DomainNameApiClient client = DomainNameApiClient.builder()
        .credentials(resellerId, apiKey)
        .app("WiseCP", "V2.0.20-beta")   // sends X-App: wisecp, X-Version: 2.0.20
        .build();
```

العميل آمن للاستخدام من عدة خيوط (thread-safe). أنشئ عميلًا واحدًا لكل مجموعة بيانات اعتماد، وأعد استخدامه طوال عمر التطبيق.

<a id="usage"></a>

## الاستخدام

لكل مجال في API عميله الخاص. تأخذ الدوال وسائط بسيطة؛ ولخيارات أكثر، لكل دالة أيضًا
نسخة محمّلة (overload) أو دالة شقيقة تأخذ كائن الطلب. وتذكر الدوال التي تخصم من رصيدك ذلك في توثيق Javadoc الخاص بها.

<a id="account"></a>

### الحساب

</div>

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

<div dir="rtl">

<a id="domains"></a>

### النطاقات

**البحث**

</div>

```java
DomainSearchResult one = client.domains().search("example.com", "USD");
boolean available = one.info() != null && one.info().isAvailable();

BulkSearchResult many = client.domains().bulkSearch(List.of("example.com", "example.net", "example.com.tr"));
for (DomainAvailability domain : many.available()) {
    System.out.println(domain.domainName() + ": " + domain.price() + " " + domain.currency());
}
```

<div dir="rtl">

**القوائم والتفاصيل**

</div>

```java
Page<Domain> page = client.domains().list(new ListDomainsRequest().maxResultCount(50).statuses(DomainStatus.ACTIVE));

client.domains().listAll(new ListDomainsRequest().domainName("example"))
        .forEach(d -> System.out.println(d.domainName() + " " + d.status() + " " + d.expirationDate()));

DomainDetails details = client.domains().get("example.com");
System.out.println(details.authCode());
details.contact(ContactType.REGISTRANT).ifPresent(c -> System.out.println(c.email()));
```

<div dir="rtl">

**التسجيل والتجديد والنقل والاستعادة** (تخصم من رصيدك)

</div>

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

<div dir="rtl">

تُفحص الطلبات قبل إرسالها. التسجيل دون أدوار جهات الاتصال الأربعة جميعها، أو جهة اتصال بلا عنوان بريد إلكتروني،
أو مدة 0 سنة، يُطلق `IllegalArgumentException`، ولا يصل شيء إلى API.

> [!NOTE]
> قد تجيب API عن طلب تسجيل أو تجديد أو نقل بحالة HTTP 200 مع `"success": false` (مثلًا عندما ترفض
> جهة التسجيل (registry) النطاق). تُعيد الحزمة هذه الإجابة كما هي: تحقّق من `DomainOperationResult.success()`
> و`message()`.

**الإعدادات**

</div>

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

<div dir="rtl">

**إعادة توجيه URL وخوادم الأسماء الفرعية والمستندات**

</div>

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

<div dir="rtl">

<a id="contacts"></a>

### جهات الاتصال

معرّفات جهات الاتصال (handles) هي جهات اتصال قابلة لإعادة الاستخدام. أنشئ واحدة مرة واحدة واستخدمها لنطاقات كثيرة.

</div>

```java
CreateContactResult created = client.contacts().create(contact);
String handle = created.handle();                                  // for example DNA-12345

ContactDetails details = client.contacts().get(handle);            // the contact and its verification state
HandleUpdateResult result = client.contacts().update(handle, contact);
System.out.println(result.stats().successCount() + " of " + result.stats().totalDomainCount() + " domains updated");

List<ContactVerification> verifications = client.contacts().getVerification(handle);
client.contacts().resendVerification(handle);
```

<div dir="rtl">

<a id="dns"></a>

### DNS

سجلات المنطقة (zone) للنطاقات الموجودة على خوادم أسماء المنصة. تُحَلّ السجلات علنًا بمجرد أن يستخدم النطاق تلك الخوادم،
وهذا ما تتولاه `apply`.

</div>

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

<div dir="rtl">

تفحص الحزمة ما سترفضه API: سجل CNAME عند الجذر (apex)، أو قيمة CAA ناقصة الأجزاء الثلاثة، أو TTL خارج النطاق
1–86400. تُباعَد استدعاءات المنطقة بثانية واحدة، وهي حدّ البوابة.

<a id="dnssec"></a>

### DNSSEC

</div>

```java
DnssecInfo info = client.dnssec().get("example.com");   // status, dsRecords, allowedAlgorithms, ...

DsRecordData record = DsRecordData.parse("12345 13 2 49FD46E6C4B45C55D4AC69CBD3CD34AC1AFE51DE");
client.dnssec().add("example.com", record);
client.dnssec().remove("example.com", record);
client.dnssec().disable("example.com");                 // removes every DS record
```

<div dir="rtl">

<a id="ssl"></a>

### SSL

يمكن أن يكون للنطاق الواحد عدة شهادات، ولذلك تُحدَّد الشهادة القائمة عبر **معرّف الشهادة** (من النوع `UUID`)
لا عبر اسم النطاق أبدًا. تعيد `purchase` و`renew` هذا المعرّف: احتفظ به. وتعثر `list` على معرّفات الشهادات
التي تملكها بالفعل.

</div>

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

<div dir="rtl">

**عرض الشهادات والعثور على المعرّف**

</div>

```java
Page<CertificateListItem> page = client.ssl().list(new ListCertificatesRequest().domainName("example.com"));   // newest first

client.ssl().listAll().forEach(c ->
        System.out.println(c.certificateId() + " " + c.domainName() + " " + c.status() + " " + c.subscriptionExpiresAt()));

// The approver addresses of a certificate you already have
List<String> approverEmails = client.ssl().getApproverEmails(certificateId);
```

<div dir="rtl">

**التجديد** (يخصم من رصيدك)

</div>

```java
// Allowed from 30 days before details.subscription().expiresAt(). The renewal is a new certificate with its own ID.
SslOperationResult renewal = client.ssl().renew(certificateId);
client.ssl().configure(new ConfigureCertificateRequest(renewal.certificateId(), newCsr, certificateContact)
        .validationMethod(ValidationMethod.EMAIL)
        .approverEmail(approvers.get(0))
        .webServerType(WebServerType.APACHE));
```

<div dir="rtl">

يجب إعداد الشهادة المشتراة أو المجدَّدة خلال 15 يومًا، وإلا أُلغيت. للتجديد استدعِ `renew` ثم `configure`
بالمعرّف الجديد. لا تشترِ مجددًا عبر `purchase`: فذلك شراء مستقل بسعر الشراء، ولن توسَم الشهادة القديمة بأنها
مجدَّدة أبدًا. معرّف الشهادة الفارغ يُطلق `IllegalArgumentException` قبل إرسال أي شيء.

<a id="hosting"></a>

### الاستضافة

</div>

```java
List<HostingPackage> packages = client.hosting().listPackages();
HostingCreateResult created = client.hosting().create("example.com", "starter-linux");   // charges the balance
HostingStatus status = client.hosting().getStatus("example.com");                         // panel access once ready
HostingService hosting = client.hosting().get("example.com");
List<ServicePlan> plans = client.hosting().listPlans();
```

<div dir="rtl">

<a id="orders-and-products"></a>

### الطلبات والمنتجات

</div>

```java
Order order = client.orders().get("ORDER-CODE");

client.products().getTld("com").ifPresent(com -> System.out.println(
        com.registrationPrice(1).map(Tld.Price::price).orElse(null) + ", DNSSEC: " + com.supportsDnssec()));

client.products().listAllTlds(new ListTldsRequest().currency("USD")).forEach(tld -> System.out.println(tld.name()));

ProductPrice price = client.products().getPrice("com", OrderType.RENEW, 1);
Page<SslProduct> ssl = client.products().listSslProducts(new ListSslProductsRequest().isWildcard(true));
```

<div dir="rtl">

<a id="requests-and-baserequest"></a>

## الطلبات و`BaseRequest`

كل عملية هي فئة طلب، مثل `RenewDomainRequest` أو `ListDomainsRequest` أو `AddDnsRecordRequest`.
تبنيها لك دوال العميل المذكورة أعلاه، ويمكنك أيضًا إنشاؤها وإرسالها بنفسك:

</div>

```java
RenewDomainRequest request = new RenewDomainRequest("example.com", 2);
System.out.println(request + " is " + request.safety());   // "POST domains/renew is BILLABLE"

DomainOperationResult result = client.send(request);

// The same, with the HTTP details: correlation ID, soft-quota state, attempts, duration
ApiResponse<DomainOperationResult> response = client.sendWithResponse(request);
System.out.println(response.correlationId() + " quota " + response.quota());
```

<div dir="rtl">

تشتق جميع الطلبات من إحدى فئتين أساسيتين:

| الفئة الأساسية | الغرض |
|---|---|
| `BaseRequest<T>` | كل العمليات. `T` هو نوع الإجابة؛ و`Void` للعمليات التي لا تُعيد شيئًا (قفل، فك القفل، إضافة سجل DNS، ...). |
| `PagedRequest<T>` | عمليات القوائم. تضيف `skipCount` و`maxResultCount` و`sorting`، وتعمل مع `listAll`. |

يصف الطلبُ نفسه عبر بضع دوال مُعاد تعريفها (overrides): `method()`، و`path()` (تحت `/api/v1/`)، و`safety()`. تُضاف
معاملات الاستعلام في `addQueryParameters`، ويأتي جسم JSON من `body()` (يبني `JsonBody` جسمًا مع حذف القيم
null)، وتوضع الفحوص التي تسبق الإرسال في `validate()`. لاستدعاء نقطة نهاية لا تغطيها الحزمة بعد، اكتب طلبك الخاص:

</div>

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

<div dir="rtl">

يحصل طلبك على كل ما تحصل عليه الطلبات المدمجة: بيانات الاعتماد، وإعادة المحاولة بحسب فئة الأمان، وتنظيم وتيرة الطلبات، ووضع القراءة فقط،
ومعالجة الأخطاء. يُستنتج نوع الإجابة من `extends BaseRequest<DomainNotes>`؛ ولا تُعِد تعريف `responseType()` إلا لفئات
الطلبات العامة (generic). وأعد تعريف `isIdempotent()` عندما يكون تكرار عملية الكتابة آمنًا.

<a id="errors"></a>

## الأخطاء

كل طلب فاشل يُطلق `DomainNameApiException` (غير مُتحقَّق منه، unchecked). أما الوسائط غير الصالحة فتُطلق
`IllegalArgumentException` قبل إرسال أي شيء.

</div>

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

<div dir="rtl">

| الدالة | المعنى |
|---|---|
| `kind()` | تصنيف ثابت (الجدول أدناه). اتخذ قراراتك بناءً عليه، لا على الرسالة. |
| `code()` | رمز الخطأ من API (مثل `Dna.DomainService:Domain:10007`)، أو أحد رموز الحزمة أدناه. |
| `getMessage()` | ما الذي حدث. تُستبدل الرسائل الجاهزة العامة بتفاصيل API. |
| `statusCode()` | حالة HTTP، أو null إن لم تصل أي إجابة. |
| `details()`, `validationErrors()` | تفاصيل إضافية ومشكلات على مستوى الحقول من API. |
| `correlationId()` | قيمة `X-Correlation-Id` من البوابة. اذكرها عند التواصل مع الدعم. |
| `hint()` | اقتراح قصير بما يجب فعله بعد ذلك. |
| `isRetryable()` | هل يمكن أن ينجح تكرار الطلب نفسه لاحقًا. |
| `isOutcomeUnknown()` | فشل طلب يُغيّر شيئًا بسبب الشبكة، فقد يكون قد نُفّذ. |
| `operation()`, `safety()` | الطلب الفاشل (`POST domains/renew`) وفئة الأمان الخاصة به. |
| `retryAfter()`, `responseBody()` | قيمة `Retry-After` من البوابة والإجابة الخام. |

| `kind()` | متى |
|---|---|
| `AUTHENTICATION` | بيانات اعتماد خاطئة أو مفقودة، أو موزّع غير معروف أو موقوف في هذه البيئة (401، إعادة توجيه). |
| `FORBIDDEN` | مصادَق عليه لكن غير مسموح له (403). |
| `NOT_FOUND` | النطاق أو جهة الاتصال أو الطلب أو أي مورد آخر غير موجود (404). |
| `VALIDATION` | رفضت API المدخلات أو فشلت قاعدة عمل: حقل غير صالح، أو نطاق مقفل، أو رصيد غير كافٍ (400، 422). |
| `CONFLICT` | حالة المورد لا تسمح بالعملية (409). |
| `RATE_LIMITED` | ما زال الطلب محدودًا بمعدل الطلبات بعد إعادة المحاولة التلقائية (429). |
| `SERVER` | فشل من جهة الخادم (5xx). غالبًا مؤقت. |
| `NETWORK`, `TIMEOUT` | لا إجابة: DNS أو TLS أو وكيل (proxy) أو إعادة ضبط الاتصال، أو لا إجابة خلال المهلة الزمنية. |
| `READ_ONLY` | مُنع بسبب وضع القراءة فقط؛ ولم يُرسَل شيء. |
| `UNKNOWN` | أي حالة أخرى، بما فيها إجابة لم تستطع الحزمة قراءتها. |

الرموز التي تضبطها الحزمة بنفسها:

| الرمز | المعنى |
|---|---|
| `INVALID_RESELLER` | أعادت البوابة التوجيه إلى صفحة الإيقاف: معرّف الموزّع غير معروف أو غير نشط أو موقوف **في هذه البيئة**. |
| `INVALID_API_KEY` | المفتاح لا يطابق الموزّع. مفاتيح الإنتاج وOTE مختلفة. |
| `MISSING_API_KEY`, `MISSING_RESELLER_ID`, `NO_CREDENTIALS` | إحدى بيانات الاعتماد فارغة. |
| `SUBRESELLER_NOT_ALLOWED` | لا يمكن لحسابات الموزّعين الفرعيين استخدام API. استخدم بيانات اعتماد الموزّع الرئيسي. |
| `UNAUTHORIZED` | أي 401 آخر. |
| `RATE_LIMITED` | 429 بعد إعادة المحاولة. |
| `NETWORK`, `TIMEOUT` | لم تصل أي إجابة. |
| `READ_ONLY` | مُنع بسبب وضع القراءة فقط. |
| `EMPTY_RESPONSE`, `INVALID_RESPONSE` | كانت الإجابة الناجحة فارغة أو تعذّرت قراءتها. |
| `HTTP_<status>` | لم ترسل API رمز خطأ. |

عند مقاطعة الخيط (thread) أثناء انتظار أحد الطلبات، تعيد الحزمة ضبط علامة المقاطعة (interrupt flag) وتُطلق
`CancellationException` بدلًا من خطأ API.

<a id="safety-retries-and-rate-limits"></a>

## الأمان وإعادة المحاولة وحدود معدل الطلبات

لكل طلب فئة أمان، وهي نفسها التي تستخدمها أداة `dna` CLI:

| `OperationSafety` | تعني | أمثلة |
|---|---|---|
| `READ` | بلا آثار جانبية، حتى لو استخدمت API الأسلوب POST أو PUT | البحث، التفاصيل، القوائم، فحوص التجديد والنقل، إنشاء CSR |
| `WRITE` | تغيير مجاني يمكن التراجع عنه | سجلات DNS، القفل، الخصوصية، خوادم الأسماء، جهات الاتصال |
| `BILLABLE` | يخصم من رصيدك | التسجيل، التجديد، النقل، الاستعادة، شراء SSL وتجديده، طلب استضافة |
| `DESTRUCTIVE` | يحذف شيئًا أو يصعب التراجع عنه | حذف سجل DNS، إزالة DNSSEC، إلغاء شهادة |

- **وضع القراءة فقط** (‏`readOnly(true)` أو `DNA_READ_ONLY=true`) يمنع كل ما ليس `READ` قبل إرساله.
  استخدمه أثناء الاستكشاف، وفي التقارير ولوحات المعلومات.
- **إعادة المحاولة.** تُعاد محاولة إجابات 429 دائمًا، مع احترام `Retry-After`. وهذا آمن حتى للطلبات المدفوعة، لأن
  البوابة ترفضها قبل تنفيذ أي شيء. أما أخطاء الشبكة والمهلات وإجابات 502/503/504 فتُعاد محاولتها لعمليات القراءة فقط
  (ولعمليات الكتابة التي يكون تكرارها آمنًا، مثل استبدال خوادم الأسماء). لا يُرسَل التسجيل أو التجديد مرتين أبدًا.
- **تنظيم وتيرة الطلبات.** تسمح البوابة بـ 5 طلبات في الثانية لكل مفتاح، و2 في الثانية لعمليات البحث عن النطاقات، و20 في الثانية
  لاستدعاءات منطقة DNS. يباعد العميل بين الطلبات ليبقى ضمن هذه الحدود (وبين استدعاءات المنطقة بثانية واحدة). معلومات النطاق وقائمة
  TLD غير محدودتين بمعدل الطلبات.
- **الحصة المرنة (soft quota).** تكلّف عمليات البحث والاستعلام عن المعلومات نقطة واحدة، وكل ما عداها 10 نقاط. بعد تجاوز 200 نقطة تبطئ البوابة كل
  طلب بما يصل إلى 5 ثوانٍ؛ وتتناقص النقاط بنسبة 20% في الدقيقة. تعرض `sendWithResponse` الحالة في `quota()`.

<a id="asynchronous-calls"></a>

## الاستدعاءات غير المتزامنة

العميل متزامن: كل دالة تنتظر (blocking) حتى تصل الإجابة، وهو ما يناسب شيفرة الخوادم والخيوط الافتراضية (virtual threads).
أما `sendAsync` و`sendWithResponseAsync` فتشغّلان الطلب على منفّذ (executor) العميل وتُعيدان `CompletableFuture`:

</div>

```java
CompletableFuture<DomainDetails> details = client.sendAsync(new GetDomainRequest("example.com"));
details.thenAccept(d -> System.out.println(d.expirationDate()));
```

<div dir="rtl">

يفشل الـ future بالاستثناءات نفسها التي تُطلقها الدوال المتزامنة (مغلّفةً في `CompletionException` عند استدعاء
`join()`). اضبط منفّذك الخاص عبر `builder().executor(...)`، مثل `Executors.newVirtualThreadPerTaskExecutor()`
على Java 21.

<a id="testing-your-code"></a>

## اختبار شيفرتك

لا تحتاج الحزمة إلا إلى نقطة نهاية HTTP. لاختبار الشيفرة التي تستخدمها دون استدعاء API، وجّه `baseUri` إلى خادم
محلي، مثل الخادم المدمج في JDK:

</div>

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

<div dir="rtl">

يمكنك أيضًا تمرير فئة فرعية خاصة بك من `HttpClient` عبر `httpClient(...)`، وإنشاء استثناءات لكائناتك الوهمية (mocks) عبر
`DomainNameApiException.builder(...)`. للاختبار على API الحقيقية، استخدم OTE بمفتاحها الخاص.

<a id="project-layout"></a>

## بنية المشروع

</div>

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

<div dir="rtl">

لكل حزمة (package) مجال الشكل نفسه: `XxxClient`، وفئة طلب واحدة لكل عملية (`XxxRequest`)، والنماذج
والقيم المُعدَّدة (enums) الخاصة بإجاباتها.

<a id="development"></a>

## التطوير

</div>

```bash
mvn verify                          # build and run the tests
mvn -Pexamples -DskipTests compile  # also compile the examples
```

<div dir="rtl">

إن لم يكن لديك JDK محلي، فاستخدم Docker:

</div>

```bash
docker run --rm -v "$PWD":/src -w /src maven:3.9-eclipse-temurin-17 mvn -B verify
```

<div dir="rtl">

لا تستدعي الاختبارات API الحقيقية أبدًا: فهي تستخدم `HttpClient` وهميًا، إضافةً إلى خادم HTTP محلي على 127.0.0.1
لاختبار طبقة النقل. يشغّلها CI على Java 17 و21 و25.

الملف `src/test/resources/openapi.json` هو وثيقة API التي تضمّنها أداة `dna` CLI. يتحقق `SpecCoverageTest` من أن لكل
عملية فيه فئة طلب واحدة بالضبط بفئة الأمان نفسها في CLI، ومن أن كل طلب لا يرسل إلا معاملات استعلام وخصائص
جسم تصفها الوثيقة. عندما تضيف API عملية جديدة، حدّث الوثيقة، وسيفشل الاختبار إلى أن يوجد الطلب.

<a id="upgrading-from-1x"></a>

## الترقية من 1.x

الإصدار 2.0 حزمة SDK جديدة. كان الإصدار 1.x نسخة منقولة من مكتبة PHP ويُعيد أغلفة `Map` غير مكتوبة بأنواع؛ أما 2.0 فيعتمد
طلبات ونماذج مكتوبة بأنواع صريحة، واستثناءات، وبانيًا (builder).

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

ينتقل الحد الأدنى لإصدار Java من 11 إلى 17، وأصبحت Jackson تبعيةً. كان الإصدار 1.x يرسل تقارير الأخطاء إلى Sentry؛ أما 2.0
فلا يرسل شيئًا إلى أي جهة سوى API.

## الروابط

- [`dna` CLI وخادم MCP](https://github.com/domainreseller/cli): API نفسها من الطرفية ومن وكلاء الذكاء الاصطناعي.
- [توثيق API](https://www.domainnameapi.com/docs/)
- [سجل التغييرات](CHANGELOG.md)

## الترخيص

Apache License 2.0. راجع [LICENSE](LICENSE).

</div>
