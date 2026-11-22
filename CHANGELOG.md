# Changelog

All notable changes to this project are documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2026-11-22

A new SDK, modelled on the [`dna` CLI](https://github.com/domainreseller/cli) and built from the same API document.
1.x was a port of the PHP library; 2.0 is not compatible with it. See "Upgrading from 1.x" in the README.

### Added

- Typed clients for all 67 operations of the REST API: `account()`, `contacts()`, `domains()` (with `forwarding()`,
  `childNameServers()` and `documents()`), `dns()`, `dnssec()`, `ssl()`, `hosting()`, `orders()` and `products()`.
  DNSSEC, SSL certificates (purchase, configure, list, renew, reissue, revoke), hosting, orders, products, contact
  handles, registry documents, auth codes, restores, the reseller profile and the transaction ledger are new.
- One request class per operation, all extending `BaseRequest<T>` or `PagedRequest<T>`. Subclass them for endpoints
  the SDK does not cover; `DomainNameApiClient.send` sends any request, `sendWithResponse` also returns the
  correlation ID, the soft-quota state, the number of attempts and the duration.
- Records with Javadoc for every answer, enums for coded values (`DomainStatus`, `TransactionType`,
  `DnssecAlgorithm`, ...) with an `UNKNOWN` fallback, and forgiving text values (`RecordType`, `ContactType`,
  `ForwardType`, ...). Reading is lenient: numbers and booleans as text, dates with or without an offset, empty values.
- Lazy `Stream` paging with the `listAll` methods; `sendAsync` and `sendWithResponseAsync` return `CompletableFuture`s.
- Every request declares an `OperationSafety` (`READ`, `WRITE`, `BILLABLE`, `DESTRUCTIVE`), the same classes the CLI
  uses. Read-only mode blocks everything but reads before it is sent.
- Retries like the CLI: 429 always (honouring `Retry-After`), network errors and 502/503/504 only for reads and
  idempotent writes, so a billable request is never sent twice. Client-side pacing keeps requests under the gateway's
  rate limits.
- `DomainNameApiException` (unchecked) with a stable `kind()`, the API's error `code()`, validation errors,
  correlation ID, hint, `isRetryable()` and `isOutcomeUnknown()`.
- `DomainNameApiClient.builder()`, `create(...)` and `fromEnvironment()`, which reads the CLI's variables
  (`DNA_RESELLER_ID`, `DNA_API_KEY`, `DNA_ENV`, `DNA_TEST_MODE`, `DNA_BASE_URL`, `DNA_READ_ONLY`). The client is
  thread-safe and accepts your own `java.net.http.HttpClient` and executor.
- Checks before sending: missing fields, incomplete contacts, missing contact roles, invalid IP addresses, CNAME at the
  apex, CAA values, TTL ranges and empty certificate IDs throw `IllegalArgumentException` without calling the API.
- Helpers: `DomainContact.forAllRoles`, `ContactHandle.forAllRoles`, `HostIpAddress.parse`, `DsRecordData.parse`,
  `DocumentUpload.fromFile`, `CertificateContent.fullChain`, `Tld.registrationPrice`, `ProductsClient.getTld`.
- SSL certificates are addressed by their certificate ID (`UUID`), as the API requires since 2026-11-20.
  `ssl().renew(certificateId)` returns the ID of the new certificate, which is then configured with
  `ssl().configure(...)`; `ssl().list(...)` finds the IDs of existing certificates.
- JUnit 5 tests with a fake `HttpClient` and a loopback HTTP server, including a check that every operation in the
  API document has one request class with the CLI's safety class and sends only the parameters the document describes.
- `AGENTS.md` with the rules for contributors and AI coding agents.

### Changed

- Java 17 is the minimum (was 11); CI runs on Java 17, 21 and 25.
- Jackson (`jackson-databind`) is now the only runtime dependency; the built-in JSON codec is gone.
- User-Agent is `DomainNameAPI-Java/2.0.0 (+https://github.com/domainreseller/java-dna)`.

### Removed

- The php-dna compatible API (`DomainNameApi` with `Map` envelopes, `HttpTransport`, `Php`, `DNARest`) and the
  php-dna conformance fixtures.
- Sentry error reporting. The SDK sends nothing anywhere except to the API.

## [1.0.1] - 2026-11-14

### Changed

- Error reporting now has its own Sentry project: the built-in default DSN points to
  DomainNameAPI's java-dna project, so telemetry is active by default as documented
  (in 1.0.0 the default DSN was empty and nothing was sent).
- Turn it off with `setErrorReportingEnabled(false)`; `setTelemetryDsn(...)` still overrides
  the default DSN.
- User-Agent is now `DomainNameAPI-Java/1.0.1`.

## [1.0.0] - Unreleased

First release: a faithful Java port of [php-dna](https://github.com/domainreseller/php-dna)
(4.0.4 plus the fixes of php-dna commit f946d69).

### Added

- `DomainNameApi` client with all 45 php-dna methods: account and balance, availability
  check, domain and TLD lists, domain details, sync, contacts, nameservers, theft protection
  lock, privacy protection, renewal, registration (including .tr TRABIS attributes), child
  nameservers, transfers (start, check, cancel, approve, reject), DNS zone records (record and
  record-set level, value-level add/replace/remove, apply) and URL forwarding.
- Overloads for every php-dna default parameter; constructor accepts the test-mode flag as a
  boolean or as a string (`"true"`, `"1"`, `"on"`, `"yes"`).
- Return values identical to php-dna (`LinkedHashMap` / `ArrayList`): same keys, string flags,
  formatted money strings and OK/ERROR envelopes. Errors are returned, never thrown.
- HTTP layer on `java.net.http` with the `DomainNameAPI-Java/1.0.0` User-Agent, 30 s / 10 s
  timeouts, optional TLS verification bypass, PHP-compatible query strings and JSON bodies,
  one-second pacing of DNS zone calls, and a pluggable `HttpTransport`.
- php-dna error mapping: `API_<status>` codes with the gateway's own code as `GatewayCode`,
  credential redirects, empty bodies, ABP boilerplate messages (never replaced by the raw body),
  flattened validation errors, and transport failures as errors with `CURL_<errno>` codes
  (`CURL_28` timeout).
- Refusals are errors, not false successes: `success:false` from register, transfer and renew
  returns `REGISTER_DOMAIN` / `TRANSFER_DOMAIN` / `RENEW_DOMAIN`; a failed automatic zone apply
  returns the apply error; `getCurrentBalance` returns `BALANCE` for currencies the account does
  not report.
- Transfers never send contacts (`contacts: []`); the API has no contacts parameter.
- Forwarding: only "Redirect information not found" means "not set"; `deleteForwarding` reports
  `Removed: true/false`.
- `modifyPrivacyProtectionStatus` accepts the status as text (`"1"`, `"true"`, `"on"`, `"yes"`);
  the reason is not sent.
- Contact types are matched case-insensitively and unknown types are kept; phone numbers with
  their own `+` country code keep it; a missing State falls back to the city.
- Sentry telemetry ported from php-dna (credential masking, ignored errors, performance
  sampling, minimal mode, `application=DomainNameAPI-Java`); the built-in DSN is empty until the
  Java Sentry project exists, and `setTelemetryDsn` overrides it.
- Built-in JSON codec (`Json`) — no runtime dependencies.
- Conformance test running all 134 php-dna fixtures, plus unit tests for the HTTP layer, error
  mapping, telemetry and utilities.
- Runnable examples for every php-dna example plus DNS zone, forwarding, transfer approval,
  `isTrTLD` and `validateContact`.
- README in English, Turkish, German, French, Italian, Dutch, Azerbaijani, Arabic and
  Simplified Chinese.

[2.0.0]: https://github.com/domainreseller/java-dna/releases/tag/V2.0.0
[1.0.1]: https://github.com/domainreseller/java-dna/releases/tag/V1.0.1
[1.0.0]: https://github.com/domainreseller/java-dna/releases/tag/V1.0.0
