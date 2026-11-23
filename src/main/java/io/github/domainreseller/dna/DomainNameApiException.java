package io.github.domainreseller.dna;

import io.github.domainreseller.dna.internal.Values;
import java.time.Duration;
import java.util.List;

/**
 * Thrown for every failed request: API errors (non-2xx answers), network failures, timeouts and read-only blocks.
 * Invalid arguments throw {@link IllegalArgumentException} instead, before anything is sent.
 *
 * <pre>{@code
 * try {
 *     client.domains().renew("example.com", 1);
 * } catch (DomainNameApiException e) {
 *     if (e.kind() == ApiErrorKind.VALIDATION) {
 *         System.out.println(e.code() + ": " + e.getMessage());
 *     } else if (e.isOutcomeUnknown()) {
 *         // the renewal may have run: check the domain before sending it again
 *     } else {
 *         throw e;
 *     }
 * }
 * }</pre>
 */
public class DomainNameApiException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final ApiErrorKind kind;
    private final String code;
    private final Integer statusCode;
    private final String details;
    private final transient List<ApiValidationError> validationErrors;
    private final String correlationId;
    private final Duration retryAfter;
    private final String responseBody;
    private final String operation;
    private final OperationSafety safety;
    private final String hint;

    /**
     * Creates an exception with only a kind, a code and a message. {@link #builder} sets the other details.
     *
     * @param kind    the classification
     * @param code    the API's error code, or one of the SDK's codes such as {@code INVALID_API_KEY}
     * @param message what happened
     */
    public DomainNameApiException(ApiErrorKind kind, String code, String message) {
        this(builder(kind, code, message));
    }

    /**
     * Creates an exception from a builder.
     *
     * @param builder the details
     */
    protected DomainNameApiException(Builder builder) {
        super(builder.message, builder.cause);
        this.kind = builder.kind == null ? ApiErrorKind.UNKNOWN : builder.kind;
        this.code = builder.code == null ? "" : builder.code;
        this.statusCode = builder.statusCode;
        this.details = builder.details;
        this.validationErrors = Values.list(builder.validationErrors);
        this.correlationId = builder.correlationId;
        this.retryAfter = builder.retryAfter;
        this.responseBody = builder.responseBody;
        this.operation = builder.operation;
        this.safety = builder.safety;
        this.hint = builder.hint;
    }

    /**
     * Starts building an exception. The SDK uses it, and so can tests of code that uses the SDK.
     *
     * @param kind    the classification
     * @param code    the error code
     * @param message what happened
     * @return a builder
     */
    public static Builder builder(ApiErrorKind kind, String code, String message) {
        return new Builder(kind, code, message);
    }

    /**
     * Returns the classification of the failure.
     *
     * @return the kind
     */
    public ApiErrorKind kind() {
        return kind;
    }

    /**
     * Returns the API's error code when it sends one (for example {@code Dna.DomainService:Domain:10007}). These
     * come from the platform's error catalogue and identify the exact reason. Otherwise one of the SDK's codes:
     * {@code INVALID_RESELLER}, {@code INVALID_API_KEY}, {@code MISSING_API_KEY}, {@code MISSING_RESELLER_ID},
     * {@code SUBRESELLER_NOT_ALLOWED}, {@code UNAUTHORIZED}, {@code NO_CREDENTIALS}, {@code RATE_LIMITED},
     * {@code NETWORK}, {@code TIMEOUT}, {@code READ_ONLY}, {@code EMPTY_RESPONSE}, {@code INVALID_RESPONSE} or
     * {@code HTTP_<status>}.
     *
     * @return the code; never null
     */
    public String code() {
        return code;
    }

    /**
     * Returns the HTTP status.
     *
     * @return the status, or null when no answer was received
     */
    public Integer statusCode() {
        return statusCode;
    }

    /**
     * Returns additional detail the API sent with the error.
     *
     * @return the details, or null
     */
    public String details() {
        return details;
    }

    /**
     * Returns the field-level problems the API reported.
     *
     * @return the validation errors; never null
     */
    public List<ApiValidationError> validationErrors() {
        return validationErrors == null ? List.of() : validationErrors;
    }

    /**
     * Returns the gateway's correlation ID. Quote it when contacting support.
     *
     * @return the correlation ID, or null
     */
    public String correlationId() {
        return correlationId;
    }

    /**
     * Returns how long the gateway asked to wait before retrying (rate limits).
     *
     * @return the wait, or null
     */
    public Duration retryAfter() {
        return retryAfter;
    }

    /**
     * Returns the raw response body.
     *
     * @return the body, or null when there was none
     */
    public String responseBody() {
        return responseBody;
    }

    /**
     * Returns the request that failed, for example {@code POST domains/renew}.
     *
     * @return the operation, or null
     */
    public String operation() {
        return operation;
    }

    /**
     * Returns what the failed request could do to your account.
     *
     * @return the safety class, or null
     */
    public OperationSafety safety() {
        return safety;
    }

    /**
     * Returns a short suggestion on what to do next.
     *
     * @return the hint, or null
     */
    public String hint() {
        return hint;
    }

    /**
     * Returns whether repeating the same request later can succeed: rate limits, network failures, timeouts and
     * server errors other than 501.
     *
     * @return whether the failure is temporary
     */
    public boolean isRetryable() {
        switch (kind) {
            case RATE_LIMITED:
            case NETWORK:
            case TIMEOUT:
                return true;
            case SERVER:
                return statusCode == null || statusCode != 501;
            default:
                return false;
        }
    }

    /**
     * Returns true when the outcome is unknown: a request that changes something failed on the network or timed
     * out, so the API may have carried it out. Check the state (for example with {@code domains().get(...)} or the
     * account transactions) before sending it again.
     *
     * @return whether the request may have been carried out
     */
    public boolean isOutcomeUnknown() {
        return (kind == ApiErrorKind.NETWORK || kind == ApiErrorKind.TIMEOUT)
                && safety != null && safety != OperationSafety.READ;
    }

    /**
     * Returns the class name, code, message, status, validation errors, hint and correlation ID.
     *
     * @return a readable description
     */
    @Override
    public String toString() {
        StringBuilder text = new StringBuilder(getClass().getName()).append(": [").append(code).append("] ")
                .append(getMessage());
        if (statusCode != null) {
            text.append(" (HTTP ").append(statusCode).append(')');
        }
        for (ApiValidationError error : validationErrors()) {
            text.append(System.lineSeparator()).append("  - ").append(error);
        }
        if (hint != null) {
            text.append(System.lineSeparator()).append("Hint: ").append(hint);
        }
        if (correlationId != null) {
            text.append(System.lineSeparator()).append("Correlation ID: ").append(correlationId);
        }
        return text.toString();
    }

    /** Collects the details of a {@link DomainNameApiException}. */
    public static final class Builder {
        private final ApiErrorKind kind;
        private final String code;
        private final String message;
        private Integer statusCode;
        private String details;
        private List<ApiValidationError> validationErrors;
        private String correlationId;
        private Duration retryAfter;
        private String responseBody;
        private String operation;
        private OperationSafety safety;
        private String hint;
        private Throwable cause;

        private Builder(ApiErrorKind kind, String code, String message) {
            this.kind = kind;
            this.code = code;
            this.message = message;
        }

        /**
         * Sets the HTTP status.
         *
         * @param statusCode the status, or null when no answer was received
         * @return this builder
         */
        public Builder statusCode(Integer statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        /**
         * Sets the additional detail the API sent.
         *
         * @param details the details
         * @return this builder
         */
        public Builder details(String details) {
            this.details = details;
            return this;
        }

        /**
         * Sets the field-level problems.
         *
         * @param validationErrors the validation errors
         * @return this builder
         */
        public Builder validationErrors(List<ApiValidationError> validationErrors) {
            this.validationErrors = validationErrors;
            return this;
        }

        /**
         * Sets the correlation ID.
         *
         * @param correlationId the correlation ID
         * @return this builder
         */
        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        /**
         * Sets the wait the gateway asked for.
         *
         * @param retryAfter the wait
         * @return this builder
         */
        public Builder retryAfter(Duration retryAfter) {
            this.retryAfter = retryAfter;
            return this;
        }

        /**
         * Sets the raw response body.
         *
         * @param responseBody the body
         * @return this builder
         */
        public Builder responseBody(String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        /**
         * Sets the request that failed and its safety class from a request.
         *
         * @param request the request
         * @return this builder
         */
        public Builder request(BaseRequest<?> request) {
            this.operation = request.toString();
            this.safety = request.safety();
            return this;
        }

        /**
         * Sets the request that failed, for example {@code POST domains/renew}.
         *
         * @param operation the operation
         * @return this builder
         */
        public Builder operation(String operation) {
            this.operation = operation;
            return this;
        }

        /**
         * Sets the safety class of the request that failed.
         *
         * @param safety the safety class
         * @return this builder
         */
        public Builder safety(OperationSafety safety) {
            this.safety = safety;
            return this;
        }

        /**
         * Sets the suggestion on what to do next.
         *
         * @param hint the hint
         * @return this builder
         */
        public Builder hint(String hint) {
            this.hint = hint;
            return this;
        }

        /**
         * Sets the underlying exception.
         *
         * @param cause the cause
         * @return this builder
         */
        public Builder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        /**
         * Creates the exception.
         *
         * @return the exception
         */
        public DomainNameApiException build() {
            return new DomainNameApiException(this);
        }
    }
}
