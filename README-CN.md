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

[Domain Name API](https://www.domainnameapi.com) 经销商平台的官方 Java SDK。它用带类型的客户端和模型覆盖了 REST API 的全部
67 个操作：域名、联系人、DNS、DNSSEC、SSL 证书、主机、订单、产品和你的账户。

```java
DomainNameApiClient client = DomainNameApiClient.create(resellerId, apiKey, ApiEnvironment.OTE);

DomainSearchResult search = client.domains().search("example.com");
if (search.info() != null && search.info().isAvailable()) {
    client.domains().register("example.com", contact, List.of("tr.apiname.com", "eu.apiname.com"));
}
```

- **处处有类型。** 每个操作都是一个请求类，每个响应都是一个带 Javadoc 的 record。没有 Map，也不用解析 JSON。
- **自定义请求。** 每个请求都继承自 `BaseRequest`。SDK 尚未覆盖的端点，可以通过子类化来调用。
- **涉及资金时更安全。** 每个请求都会声明自己是读取、写入、扣除余额还是删除数据。需付费的请求绝不会重试，只读模式会阻止一切更改。
- **内置容错。** 自动遵守速率限制，429 响应会重试，读取请求遇到网络错误也会重试。
- **清晰的错误。** 只有一种非受检异常，带有稳定的错误类别、API 的错误代码、字段级验证错误、关联 ID 和提示。
- **现代 Java。** Java 17+、record、惰性 `Stream` 分页、`CompletableFuture` 变体方法，以及基于 `java.net.http` 的线程安全客户端。
- **注重隐私。** 没有遥测。除了你发往 API 的请求之外，不会有任何数据离开你的进程。

它是 [`dna`（Domain Name API CLI）](https://github.com/domainreseller/cli) 的 Java 对应版本：使用同一份 API
文档、同样的安全类别、重试规则和错误代码。

## 目录

- [安装](#install)
- [凭据](#credentials)
- [快速开始](#quick-start)
- [配置](#configuration)
- [用法](#usage)：[账户](#account) · [域名](#domains) · [联系人](#contacts) · [DNS](#dns) · [DNSSEC](#dnssec) · [SSL](#ssl) · [主机](#hosting) · [订单与产品](#orders-and-products)
- [请求与 `BaseRequest`](#requests-and-baserequest)
- [错误](#errors)
- [安全、重试与速率限制](#safety-retries-and-rate-limits)
- [异步调用](#asynchronous-calls)
- [测试你的代码](#testing-your-code)
- [项目结构](#project-layout)
- [开发](#development)
- [从 1.x 升级](#upgrading-from-1x)

<a id="install"></a>

## 安装

> [!IMPORTANT]
> **请始终使用[最新版本](https://github.com/domainreseller/java-dna/releases/latest)。** 发布包均带有版本号并经过测试。
> 绿色的 **Code → Download ZIP** 按钮下载的是开发分支。

坐标：`io.github.domainreseller:domainnameapi:2.0.0`。

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

在该构件可从公共仓库获取之前，请从发布版源码将其安装到本地 Maven 仓库（`mvn -DskipTests install`），
并在 Gradle 的 repositories 中添加 `mavenLocal()`。

**要求：** Java 17 或更高版本。唯一的依赖是用于处理 JSON 的 [Jackson](https://github.com/FasterXML/jackson-databind)
（`jackson-databind`）；HTTP 通过 JDK 自带的 `java.net.http.HttpClient` 完成。

<a id="credentials"></a>

## 凭据

API 用两个值验证每个请求，SDK 会替你发送它们：

| 值 | 请求头 | 在哪里找到 |
|---|---|---|
| Reseller ID（一个 UUID） | `__reseller` | 经销商面板 → **API 设置** |
| API Key | `X-API-KEY` | 经销商面板 → **API 设置** |

> [!NOTE]
> 这些是 **API 凭据**：你的面板登录信息在这里无法使用。**正式环境和 OTE（测试环境）使用
> 不同的 API Key。** 混用会失败，并返回 `INVALID_API_KEY` 或 `INVALID_RESELLER`。

<a id="quick-start"></a>

## 快速开始

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

[examples](examples) 文件夹中有针对这些场景的可运行程序。它们读取 `DNA_*` 环境变量，
因此请将它们指向 OTE：

```bash
export DNA_RESELLER_ID=... DNA_API_KEY=... DNA_ENV=ote
```

<a id="configuration"></a>

## 配置

创建客户端有四种方式：

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

| Builder 方法 | 默认值 | 含义 |
|---|---|---|
| `credentials(resellerId, apiKey)` | （必填） | 凭据。 |
| `environment(...)` | `PRODUCTION` | `PRODUCTION`（`https://api.domainresellerapi.com`）或 `OTE`（`https://ote.domainresellerapi.com`）。 |
| `baseUri(...)` | 无 | 覆盖环境的地址，例如用于代理。 |
| `timeout(...)` | 60 秒 | 单次尝试的时间限制。 |
| `maxRetries(...)` | 2 | 失败的请求重复多少次（见[重试](#safety-retries-and-rate-limits)）。0 表示关闭重试。 |
| `maxRetryDelay(...)` | 30 秒 | 重试前的最长等待时间。 |
| `paceRequests(...)` | `true` | 控制请求间隔，使其保持在网关的速率限制之内。 |
| `readOnly(...)` | `false` | 在发送之前阻止所有非读取请求。 |
| `userAgent(...)` | `DomainNameAPI-Java/<version>` | `User-Agent` 请求头。 |
| `app(name, version)` | `java`、SDK 版本 | 使用 SDK 的应用，以 `X-App` 和 `X-Version` 发送（见[下文](#标识您的应用)）。 |
| `httpClient(...)` | SDK 自带的客户端 | 你自己的 `java.net.http.HttpClient`（代理、TLS、执行器）。 |
| `executor(...)` | 一个共享的守护线程池 | `sendAsync` 系列方法在其中运行。 |

`fromEnvironment()` 会读取：

| 变量 | 含义 |
|---|---|
| `DNA_RESELLER_ID`、`DNA_API_KEY` | 凭据。 |
| `DNA_ENV` | `production` 或 `ote`（也可用 `prod`、`live`、`test`、`sandbox`）。 |
| `DNA_TEST_MODE` | `true`、`1`、`on` 或 `yes` 表示选择 OTE。未设置 `DNA_ENV` 时使用。 |
| `DNA_BASE_URL` | 可选，覆盖基础地址。 |
| `DNA_READ_ONLY` | `true` 表示开启只读模式。 |
| `DNA_APP`, `DNA_APP_VERSION` | 使用 SDK 的应用及其版本（`X-App`、`X-Version`）。 |

### 标识您的应用

每个请求都带有两个请求头，告诉 API 是哪个集成发出的：`X-App` 是转为小写、只保留 `a-z`、`0-9`、`.`、`_` 和 `-` 的应用名称；`X-Version` 是只含数字和点的应用版本。默认值为 `java` 和 SDK 版本。用 `app(name, version)` 或 `DNA_APP`/`DNA_APP_VERSION` 指明您的面板或模块；名称为空或无效时回退为 `java`，版本为空时回退为 SDK 版本。除此之外不发送任何内容，`User-Agent` 也保持不变。

```java
DomainNameApiClient client = DomainNameApiClient.builder()
        .credentials(resellerId, apiKey)
        .app("WiseCP", "V2.0.20-beta")   // sends X-App: wisecp, X-Version: 2.0.20
        .build();
```

客户端是线程安全的。每组凭据创建一个客户端，并在应用的整个生命周期内复用。

<a id="usage"></a>

## 用法

API 的每个领域都有各自的客户端。方法接受简单的参数；如需更多选项，每个方法还有一个接受请求对象的
重载（或同类方法）。会扣除余额的方法会在其 Javadoc 中注明。

<a id="account"></a>

### 账户

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

### 域名

**搜索**

```java
DomainSearchResult one = client.domains().search("example.com", "USD");
boolean available = one.info() != null && one.info().isAvailable();

BulkSearchResult many = client.domains().bulkSearch(List.of("example.com", "example.net", "example.com.tr"));
for (DomainAvailability domain : many.available()) {
    System.out.println(domain.domainName() + ": " + domain.price() + " " + domain.currency());
}
```

**列表与详情**

```java
Page<Domain> page = client.domains().list(new ListDomainsRequest().maxResultCount(50).statuses(DomainStatus.ACTIVE));

client.domains().listAll(new ListDomainsRequest().domainName("example"))
        .forEach(d -> System.out.println(d.domainName() + " " + d.status() + " " + d.expirationDate()));

DomainDetails details = client.domains().get("example.com");
System.out.println(details.authCode());
details.contact(ContactType.REGISTRANT).ifPresent(c -> System.out.println(c.email()));
```

**注册、续费、转移、恢复**（这些操作会扣除你的余额）

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

请求在发送之前会先经过检查。缺少四种联系人角色之一的注册、没有电子邮件地址的联系人，或注册期限为 0 年，
都会抛出 `IllegalArgumentException`，并且不会向 API 发送任何内容。

> [!NOTE]
> 对于注册、续费或转移，API 可能返回 HTTP 200 且 `"success": false`（例如注册局拒绝该域名时）。
> SDK 会原样返回该响应：请检查 `DomainOperationResult.success()` 和 `message()`。

**设置**

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

**URL 转发、子名称服务器与文档**

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

### 联系人

联系人句柄（handle）是可重复使用的联系人。创建一次，即可用于多个域名。

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

使用平台名称服务器的域名的区域记录。只有当域名使用这些名称服务器后，记录才会在公网上解析，
这一步由 `apply` 负责。

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

SDK 会提前检查 API 会拒绝的情况：根域上的 CNAME、缺少三个部分的 CAA 值、超出
1–86400 范围的 TTL。区域调用之间会保持 1 秒间隔，这是网关的限制。

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

一个域名可以有多张证书，因此已有的证书通过**证书 ID**（一个 `UUID`）来指定，而不是域名。
`purchase` 和 `renew` 会返回该 ID：请保存它。`list` 可以查到你已有证书的 ID。

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

**列出证书并查找 ID**

```java
Page<CertificateListItem> page = client.ssl().list(new ListCertificatesRequest().domainName("example.com"));   // newest first

client.ssl().listAll().forEach(c ->
        System.out.println(c.certificateId() + " " + c.domainName() + " " + c.status() + " " + c.subscriptionExpiresAt()));

// The approver addresses of a certificate you already have
List<String> approverEmails = client.ssl().getApproverEmails(certificateId);
```

**续费**（此操作会扣除你的余额）

```java
// Allowed from 30 days before details.subscription().expiresAt(). The renewal is a new certificate with its own ID.
SslOperationResult renewal = client.ssl().renew(certificateId);
client.ssl().configure(new ConfigureCertificateRequest(renewal.certificateId(), newCsr, certificateContact)
        .validationMethod(ValidationMethod.EMAIL)
        .approverEmail(approvers.get(0))
        .webServerType(WebServerType.APACHE));
```

购买或续费的证书必须在 15 天内完成配置，否则会被取消。续费时先调用 `renew`，再用新的 ID 调用 `configure`。
不要用 `purchase` 再买一次：那是按购买价格进行的另一笔购买，旧证书也永远不会被标记为已续费。
空的证书 ID 会在发送任何内容之前抛出 `IllegalArgumentException`。

<a id="hosting"></a>

### 主机

```java
List<HostingPackage> packages = client.hosting().listPackages();
HostingCreateResult created = client.hosting().create("example.com", "starter-linux");   // charges the balance
HostingStatus status = client.hosting().getStatus("example.com");                         // panel access once ready
HostingService hosting = client.hosting().get("example.com");
List<ServicePlan> plans = client.hosting().listPlans();
```

<a id="orders-and-products"></a>

### 订单与产品

```java
Order order = client.orders().get("ORDER-CODE");

client.products().getTld("com").ifPresent(com -> System.out.println(
        com.registrationPrice(1).map(Tld.Price::price).orElse(null) + ", DNSSEC: " + com.supportsDnssec()));

client.products().listAllTlds(new ListTldsRequest().currency("USD")).forEach(tld -> System.out.println(tld.name()));

ProductPrice price = client.products().getPrice("com", OrderType.RENEW, 1);
Page<SslProduct> ssl = client.products().listSslProducts(new ListSslProductsRequest().isWildcard(true));
```

<a id="requests-and-baserequest"></a>

## 请求与 `BaseRequest`

每个操作都是一个请求类，例如 `RenewDomainRequest`、`ListDomainsRequest` 或 `AddDnsRecordRequest`。
上面的客户端方法会替你构建它们，你也可以自己创建并发送：

```java
RenewDomainRequest request = new RenewDomainRequest("example.com", 2);
System.out.println(request + " is " + request.safety());   // "POST domains/renew is BILLABLE"

DomainOperationResult result = client.send(request);

// The same, with the HTTP details: correlation ID, soft-quota state, attempts, duration
ApiResponse<DomainOperationResult> response = client.sendWithResponse(request);
System.out.println(response.correlationId() + " quota " + response.quota());
```

所有请求都继承自两个基类之一：

| 基类 | 用途 |
|---|---|
| `BaseRequest<T>` | 所有操作。`T` 是响应类型；不返回数据的操作（锁定、解锁、添加 DNS 记录等）使用 `Void`。 |
| `PagedRequest<T>` | 列表操作。增加 `skipCount`、`maxResultCount` 和 `sorting`，并可配合 `listAll` 使用。 |

请求通过几个重写方法来描述自己：`method()`、`path()`（位于 `/api/v1/` 之下）和 `safety()`。查询
参数在 `addQueryParameters` 中添加，JSON 请求体来自 `body()`（`JsonBody` 可以构建请求体并省略
null 值），发送前的检查写在 `validate()` 中。要调用 SDK 尚未覆盖的端点，请编写你自己的请求：

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

你的请求可以获得内置请求的一切能力：凭据、按安全类别执行的重试、请求间隔控制、只读模式和
错误处理。响应类型从 `extends BaseRequest<DomainNotes>` 中读取；只有泛型请求类才需要重写 `responseType()`。
当某个写入操作可以安全地重复执行时，请重写 `isIdempotent()`。

<a id="errors"></a>

## 错误

每个失败的请求都会抛出 `DomainNameApiException`（非受检异常）。无效参数会在发送任何内容之前抛出
`IllegalArgumentException`。

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

| 方法 | 含义 |
|---|---|
| `kind()` | 稳定的分类（见下表）。请根据它分支处理，而不是根据消息。 |
| `code()` | API 的错误代码（如 `Dna.DomainService:Domain:10007`），或下面列出的 SDK 自身的代码之一。 |
| `getMessage()` | 发生了什么。模板式的消息会被替换为 API 给出的详情。 |
| `statusCode()` | HTTP 状态码；没有收到响应时为 null。 |
| `details()`、`validationErrors()` | 来自 API 的附加详情和字段级问题。 |
| `correlationId()` | 网关的 `X-Correlation-Id`。联系支持人员时请提供它。 |
| `hint()` | 关于下一步该怎么做的简短建议。 |
| `isRetryable()` | 稍后重复同一请求是否可能成功。 |
| `isOutcomeUnknown()` | 一个会更改数据的请求在网络上失败，因此它可能已经执行。 |
| `operation()`、`safety()` | 失败的请求（`POST domains/renew`）及其安全类别。 |
| `retryAfter()`、`responseBody()` | 网关的 `Retry-After` 和原始响应。 |

| `kind()` | 何时出现 |
|---|---|
| `AUTHENTICATION` | 凭据错误或缺失，或者该经销商在此环境中未知或已被停用（401、重定向）。 |
| `FORBIDDEN` | 已通过身份验证，但无权执行（403）。 |
| `NOT_FOUND` | 域名、联系人、订单或其他资源不存在（404）。 |
| `VALIDATION` | API 拒绝了输入，或业务规则未通过：字段错误、域名已锁定、余额不足（400、422）。 |
| `CONFLICT` | 资源的状态不允许该操作（409）。 |
| `RATE_LIMITED` | 自动重试后仍受速率限制（429）。 |
| `SERVER` | 服务器端故障（5xx）。通常是暂时的。 |
| `NETWORK`、`TIMEOUT` | 没有响应：DNS、TLS、代理、连接被重置，或在超时时间内没有响应。 |
| `READ_ONLY` | 被只读模式阻止；未发送任何内容。 |
| `UNKNOWN` | 其他任何情况，包括 SDK 无法读取的响应。 |

SDK 自行设置的代码：

| 代码 | 含义 |
|---|---|
| `INVALID_RESELLER` | 网关重定向到了它的停用页面：该 Reseller ID 在**此环境中**未知、未激活或已被停用。 |
| `INVALID_API_KEY` | 该密钥与经销商不匹配。正式环境和 OTE 的密钥不同。 |
| `MISSING_API_KEY`、`MISSING_RESELLER_ID`、`NO_CREDENTIALS` | 某个凭据为空。 |
| `SUBRESELLER_NOT_ALLOWED` | 子经销商账户不能使用 API。请使用主经销商的凭据。 |
| `UNAUTHORIZED` | 其他任何 401。 |
| `RATE_LIMITED` | 重试之后仍返回 429。 |
| `NETWORK`、`TIMEOUT` | 没有收到响应。 |
| `READ_ONLY` | 被只读模式阻止。 |
| `EMPTY_RESPONSE`、`INVALID_RESPONSE` | 成功的响应为空或无法读取。 |
| `HTTP_<status>` | API 没有返回错误代码。 |

如果线程在请求等待期间被中断，SDK 会重新设置中断标志，并抛出 `CancellationException`，
而不是 API 错误。

<a id="safety-retries-and-rate-limits"></a>

## 安全、重试与速率限制

每个请求都有一个安全类别，与 `dna` CLI 使用的相同：

| `OperationSafety` | 含义 | 示例 |
|---|---|---|
| `READ` | 没有副作用，即使 API 使用 POST 或 PUT | 搜索、详情、列表、续费和转移检查、CSR 生成 |
| `WRITE` | 免费且可撤销的更改 | DNS 记录、锁定、隐私保护、名称服务器、联系人 |
| `BILLABLE` | 扣除你的余额 | 注册、续费、转移、恢复、购买和续费 SSL、订购主机 |
| `DESTRUCTIVE` | 删除数据或难以撤销 | 删除 DNS 记录、移除 DNSSEC、吊销证书 |

- **只读模式**（`readOnly(true)` 或 `DNA_READ_ONLY=true`）会在发送之前阻止一切不属于 `READ` 的请求。
  浏览探索、生成报表和搭建仪表板时可以使用。
- **重试。** 429 响应总是会重试，并遵循 `Retry-After`。即使对需付费的请求，这样做也是安全的，因为
  网关会在执行任何操作之前拒绝它们。网络错误、超时和 502/503/504 响应只会对读取请求重试
  （以及可以安全重复的写入请求，例如替换名称服务器）。注册或续费绝不会被发送两次。
- **请求间隔控制。** 网关允许每个密钥每秒 5 个请求，域名搜索每秒 2 个，DNS 区域调用每秒 20 个。
  客户端会控制请求间隔，使其保持在这些限制之内（区域调用间隔 1 秒）。域名信息和
  TLD 列表不受速率限制。
- **软配额。** 搜索和信息查询消耗 1 点，其他一切消耗 10 点。超过 200 点后，网关会让每个
  请求最多延迟 5 秒；点数每分钟衰减 20%。`sendWithResponse` 会在 `quota()` 中显示当前状态。

<a id="asynchronous-calls"></a>

## 异步调用

客户端是同步的：每个方法都会阻塞直到收到响应，这适合服务器代码和虚拟线程。
`sendAsync` 和 `sendWithResponseAsync` 会在客户端的执行器上运行请求，并返回一个 `CompletableFuture`：

```java
CompletableFuture<DomainDetails> details = client.sendAsync(new GetDomainRequest("example.com"));
details.thenAccept(d -> System.out.println(d.expirationDate()));
```

该 future 失败时抛出的异常与同步方法相同（通过 `join()` 获取时会被包装在 `CompletionException` 中）。
可以用 `builder().executor(...)` 设置你自己的执行器，例如在 Java 21 上使用 `Executors.newVirtualThreadPerTaskExecutor()`。

<a id="testing-your-code"></a>

## 测试你的代码

SDK 只需要一个 HTTP 端点。要在不调用 API 的情况下测试使用该 SDK 的代码，请将 `baseUri` 指向一个本地
服务器，例如 JDK 内置的服务器：

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

你也可以通过 `httpClient(...)` 传入你自己的 `HttpClient` 子类，并用 `DomainNameApiException.builder(...)`
为你的模拟对象创建异常。要针对真实 API 测试，请使用 OTE 及其专用的密钥。

<a id="project-layout"></a>

## 项目结构

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

每个领域的包结构相同：一个 `XxxClient`、每个操作对应一个请求类（`XxxRequest`），以及其响应所用的模型
和枚举。

<a id="development"></a>

## 开发

```bash
mvn verify                          # build and run the tests
mvn -Pexamples -DskipTests compile  # also compile the examples
```

如果本地没有 JDK，可以使用 Docker：

```bash
docker run --rm -v "$PWD":/src -w /src maven:3.9-eclipse-temurin-17 mvn -B verify
```

测试从不调用真实 API：它们使用一个伪造的 `HttpClient`，并为传输层使用一个运行在 127.0.0.1 上的本地 HTTP 服务器。
CI 会在 Java 17、21 和 25 上运行这些测试。

`src/test/resources/openapi.json` 是 `dna` CLI 内嵌的 API 文档。`SpecCoverageTest` 会检查其中的每个
操作都有且只有一个请求类，并且安全类别与 CLI 一致；同时检查每个请求只发送文档中描述的查询
参数和请求体属性。当 API 新增操作时，请更新该文档；在对应的请求类创建出来之前，测试都会失败。

<a id="upgrading-from-1x"></a>

## 从 1.x 升级

2.0 是全新的 SDK。1.x 是 PHP 库的移植版本，返回无类型的 `Map` 信封；2.0 提供带类型的请求
和模型、异常以及 builder。

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

Java 基线版本从 11 提升到 17，并且 Jackson 现在是一个依赖。1.x 会向 Sentry 发送错误报告；2.0 除了 API 之外，
不会向任何地方发送任何内容。

## 链接

- [`dna` CLI 和 MCP 服务器](https://github.com/domainreseller/cli)：在终端和 AI 智能体中使用同一套 API。
- [API 文档](https://www.domainnameapi.com/docs/)
- [更新日志](CHANGELOG.md)

## 许可证

Apache License 2.0。参见 [LICENSE](LICENSE)。
