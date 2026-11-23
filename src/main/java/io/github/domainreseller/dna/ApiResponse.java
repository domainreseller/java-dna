package io.github.domainreseller.dna;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * A decoded answer together with the details of the HTTP exchange: status, correlation ID, quota, attempts and
 * timing. Returned by {@link DomainNameApiClient#sendWithResponse(BaseRequest)}.
 *
 * @param <T> the type of the decoded answer
 */
public final class ApiResponse<T> {
    private final T value;
    private final int statusCode;
    private final Map<String, String> headers;
    private final int attempts;
    private final Duration duration;
    private final String correlationId;
    private final ApiQuota quota;

    /**
     * Creates a response. The SDK creates these; the constructor is public for tests of code that uses the SDK.
     *
     * @param value      the decoded answer, or null for operations without one
     * @param statusCode the HTTP status
     * @param headers    the response headers; names are compared ignoring case
     * @param attempts   how many times the request was sent
     * @param duration   the total time including retries and pacing
     * @param quota      the soft-quota state, or null when the gateway sent none
     */
    public ApiResponse(T value, int statusCode, Map<String, String> headers, int attempts, Duration duration, ApiQuota quota) {
        this.value = value;
        this.statusCode = statusCode;
        TreeMap<String, String> copy = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        if (headers != null) {
            copy.putAll(headers);
        }
        this.headers = Collections.unmodifiableMap(copy);
        this.attempts = attempts;
        this.duration = duration == null ? Duration.ZERO : duration;
        String id = copy.get("X-Correlation-Id");
        this.correlationId = id == null || id.isEmpty() ? null : id;
        this.quota = quota;
    }

    /**
     * Returns the decoded answer.
     *
     * @return the answer, or null for operations that return nothing
     */
    public T value() {
        return value;
    }

    /**
     * Returns the HTTP status code.
     *
     * @return the status code
     */
    public int statusCode() {
        return statusCode;
    }

    /**
     * Returns the gateway's {@code X-Correlation-Id}. Quote it when contacting support.
     *
     * @return the correlation ID, or null when the gateway sent none
     */
    public String correlationId() {
        return correlationId;
    }

    /**
     * Returns the soft-quota state after this request.
     *
     * @return the quota, or null when the gateway sent none
     */
    public ApiQuota quota() {
        return quota;
    }

    /**
     * Returns how many times the request was sent.
     *
     * @return the number of attempts; 1 when it was not retried
     */
    public int attempts() {
        return attempts;
    }

    /**
     * Returns the total time including retries and pacing.
     *
     * @return the duration
     */
    public Duration duration() {
        return duration;
    }

    /**
     * Returns the response headers. Names are compared ignoring case; repeated headers are joined with
     * {@code ", "}.
     *
     * @return the headers
     */
    public Map<String, String> headers() {
        return headers;
    }

    /**
     * Returns a short description such as {@code 200 (attempts: 1, correlation ID: abc)}.
     *
     * @return a readable description
     */
    @Override
    public String toString() {
        return statusCode + " (attempts: " + attempts + ", correlation ID: " + correlationId + ")";
    }
}
