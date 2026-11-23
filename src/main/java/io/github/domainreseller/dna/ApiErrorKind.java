package io.github.domainreseller.dna;

/** A coarse, stable classification of failures. Branch on it instead of parsing messages. */
public enum ApiErrorKind {
    /** Anything that fits no other kind, including an answer the SDK could not read. */
    UNKNOWN,

    /** Missing or wrong credentials, or an unknown, inactive or suspended reseller (HTTP 401, or a redirect). */
    AUTHENTICATION,

    /** Authenticated but not allowed (HTTP 403). */
    FORBIDDEN,

    /** The domain, contact, order or other resource does not exist (HTTP 404). */
    NOT_FOUND,

    /**
     * The API refused the input or a business rule failed: bad field, domain locked, not renewable, insufficient
     * balance (HTTP 400/422).
     */
    VALIDATION,

    /** The resource is in a state that does not allow the operation (HTTP 409). */
    CONFLICT,

    /** Still rate limited after the automatic retries (HTTP 429). */
    RATE_LIMITED,

    /** A server-side failure (HTTP 5xx). Often temporary. */
    SERVER,

    /** The request could not be completed on the network: DNS, TLS, proxy, connection reset. */
    NETWORK,

    /** No answer within the client's timeout. */
    TIMEOUT,

    /** Blocked by the client's read-only mode; nothing was sent. */
    READ_ONLY
}
