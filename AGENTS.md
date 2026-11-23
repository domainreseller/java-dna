# AGENTS.md

Guidance for people and AI coding agents working on this repository.

## What this is

`java-dna` is the Java SDK for the Domain Name API reseller REST API (`io.github.domainreseller:domainnameapi`,
package `io.github.domainreseller.dna`). It mirrors the [`dna` CLI](https://github.com/domainreseller/cli): the same
operations, the same safety classes, environment variables, retry rules and error codes. It has nothing to do with
php-dna or the SOAP API.

## Commands

```bash
mvn verify                          # library and tests (Java 17+); no test touches the real API
mvn -Pexamples -DskipTests compile  # also compiles examples/
# without a local JDK:
docker run --rm -v "$PWD":/src -w /src maven:3.9-eclipse-temurin-17 mvn -B verify
```

The build must stay free of compiler warnings: `-Xlint:all` everywhere and `-Xdoclint:all/protected` for the library.

## Layout

- `DomainNameApiClient`: the entry point and its `Builder`. Sending, retries, pacing, error handling.
- Root package: `BaseRequest`, `PagedRequest`, `QueryParameters`, `JsonBody`, `Page`, `ApiResponse`,
  `DomainNameApiException`, `ApiErrorKind`, `OperationSafety`, shared enums.
- `internal/`: JSON settings, error parsing, the rate-limit pacer. Not public API.
- One package per API area (`accounts`, `contacts`, `domains`, `dns`, `dnssec`, `ssl`, `hosting`, `orders`,
  `products`), each with an `XxxClient`, one request class per operation and the models (records) and enums.
- `src/test/resources/openapi.json`: the API document, the same one the `dna` CLI embeds.
- `src/test/.../FakeHttpClient`, `TestClient`: the fake HTTP layer every test uses.

## Rules

- **New API operation:** update `src/test/resources/openapi.json` from the CLI repository, add a request class in the
  area package, models, a method on the area client, and an entry in `SpecCoverageTest.REQUESTS` (every optional value
  set). `SpecCoverageTest` fails until all of this is done, and also when a request sends a parameter or body property
  the document does not have.
- **Safety class** (`OperationSafety`) is a product decision and must match the CLI's registry: `BILLABLE` for anything
  that charges the balance, `DESTRUCTIVE` for deletes and hard-to-undo changes, `WRITE` for free, reversible changes,
  `READ` only for operations without side effects (even when the HTTP method is POST or PUT).
- **Never retry billable requests** after network errors or 5xx answers. Only 429 is safe to retry for them. Override
  `isIdempotent()` only when sending a request twice is harmless.
- **No telemetry.** Nothing may leave the user's process except the API requests they make.
- **One runtime dependency:** Jackson databind. HTTP goes through `java.net.http`.
- **Models are forgiving:** response records accept missing, null and loosely typed values; lists are never null.
  Requests validate before sending and throw `IllegalArgumentException`.
- **Tests never call the real API.** Use `TestClient` and `FakeHttpClient` (or a loopback server, as `TransportTest`).
- **Public API:** every public and protected member has Javadoc. Error codes, `ApiErrorKind` values and safety classes
  are a contract.
- **README translations:** `README.md` is the source; keep the eight `README-XX.md` translations in sync.
- **Style:** English for code, comments and docs; messages say what happened and what to do next.
- **Commits:** imperative subject line. Do not add `Co-Authored-By` trailers for AI tools.
