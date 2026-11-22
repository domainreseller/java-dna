package io.github.domainreseller.dna.internal;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.domainreseller.dna.ApiErrorKind;
import io.github.domainreseller.dna.ApiValidationError;
import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.DomainNameApiException;
import io.github.domainreseller.dna.OperationSafety;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/** Turns a failed answer into a {@link DomainNameApiException} with a stable kind, code and hint. Not part of the public API. */
public final class ErrorParser {
    // The generic .NET exception text the API sometimes sends instead of a useful message.
    private static final Pattern BOILERPLATE =
            Pattern.compile("exception of type '[^']*' was thrown", Pattern.CASE_INSENSITIVE);

    private ErrorParser() {
    }

    /**
     * Builds the exception for a non-2xx answer (or a redirect to the gateway's error page).
     *
     * @param request    the request
     * @param status     the HTTP status
     * @param headers    the response headers, names compared ignoring case
     * @param body       the response body
     * @param redirected whether the answer came from the gateway's error page after a redirect
     * @param clock      the clock, for {@code Retry-After} dates
     * @return the exception
     */
    public static DomainNameApiException fromResponse(BaseRequest<?> request, int status, Map<String, String> headers,
                                                      String body, boolean redirected, Clock clock) {
        String correlationId = blankToNull(headers.get("X-Correlation-Id"));
        String text = body == null ? "" : body;

        // The multi-reseller middleware redirects to /Error/Suspend when the __reseller value is unknown, inactive
        // or suspended in this environment.
        if (status == 301 || status == 302 || redirected) {
            return create(request, ApiErrorKind.AUTHENTICATION, "INVALID_RESELLER",
                    "Authentication failed: the reseller ID is unknown, inactive or suspended in this environment.",
                    status, correlationId, text).build();
        }

        if (status == 429) {
            Duration retryAfter = retryAfter(headers, clock);
            String message = "Rate limit exceeded.";
            if (retryAfter != null) {
                message = "Rate limit exceeded; retry after " + seconds(retryAfter) + " s.";
            }
            return create(request, ApiErrorKind.RATE_LIMITED, "RATE_LIMITED", message, status, correlationId, text)
                    .retryAfter(retryAfter)
                    .build();
        }

        String code = "";
        String message = "";
        String details = "";
        List<ApiValidationError> validationErrors = new ArrayList<>();
        String trimmed = text.trim();
        JsonNode root = trimmed.startsWith("{") ? readObject(trimmed) : null;
        if (root != null) {
            // ABP wraps errors as {"error": {...}}; some endpoints answer with a flat object.
            JsonNode error = property(root, "error");
            if (error == null || !error.isObject()) {
                error = root;
            }
            code = string(error, "code");
            message = string(error, "message");
            details = string(error, "details");
            JsonNode list = property(error, "validationErrors");
            if (list != null && list.isArray()) {
                for (JsonNode item : list) {
                    if (item.isTextual()) {
                        validationErrors.add(new ApiValidationError(item.asText(), List.of()));
                    } else if (item.isObject()) {
                        List<String> members = new ArrayList<>();
                        JsonNode m = property(item, "members");
                        if (m != null && m.isArray()) {
                            for (JsonNode member : m) {
                                if (member.isTextual()) {
                                    members.add(member.asText());
                                }
                            }
                        }
                        validationErrors.add(new ApiValidationError(string(item, "message"), members));
                    }
                }
            }
        } else if (!trimmed.isEmpty()) {
            message = trimmed;
        }

        if (status == 401 && code.isEmpty()) {
            String lower = message.toLowerCase(Locale.ROOT);
            if (lower.contains("api key is missing")) {
                code = "MISSING_API_KEY";
            } else if (lower.contains("resellerid is missing")) {
                code = "MISSING_RESELLER_ID";
            } else if (lower.contains("invalid api key")) {
                code = "INVALID_API_KEY";
            } else if (lower.contains("authorisation") || lower.contains("authorization")) {
                code = "SUBRESELLER_NOT_ALLOWED";
            } else {
                code = "UNAUTHORIZED";
            }
        }

        // Replace boilerplate or an echoed error code with the details field.
        if (BOILERPLATE.matcher(message).find() || (!message.isEmpty() && message.equals(code))) {
            String d = details.trim();
            if (d.startsWith("Dna ")) {
                d = d.substring(4).trim();
            }
            message = !d.isEmpty() && !BOILERPLATE.matcher(d).find() && !d.equals(code)
                    ? d
                    : "The operation could not be completed (error " + code + ").";
        }

        if (code.isEmpty()) {
            code = "HTTP_" + status;
        }
        if (message.isBlank()) {
            message = "HTTP " + status + " " + reasonPhrase(status) + ".";
        }

        return create(request, kindOf(status), code, message, status, correlationId, text)
                .details(details.isEmpty() ? null : details)
                .validationErrors(validationErrors)
                .hint(hintFor(kindOf(status), code, validationErrors.size(), correlationId, request.safety()))
                .build();
    }

    /**
     * Builds the exception for a request that got no answer.
     *
     * @param request   the request
     * @param cause     the network exception
     * @param timedOut  whether the time limit was reached
     * @param timeout   the time limit
     * @return the exception
     */
    public static DomainNameApiException network(BaseRequest<?> request, Throwable cause, boolean timedOut, Duration timeout) {
        String message = timedOut
                ? "No answer from the API within " + seconds(timeout) + " s."
                : "The request could not be completed: " + describe(cause);
        return create(request, timedOut ? ApiErrorKind.TIMEOUT : ApiErrorKind.NETWORK, timedOut ? "TIMEOUT" : "NETWORK",
                message, null, null, null)
                .cause(cause)
                .build();
    }

    /**
     * Returns the kind for an HTTP status.
     *
     * @param status the status
     * @return the kind
     */
    public static ApiErrorKind kindOf(int status) {
        switch (status) {
            case 301:
            case 302:
            case 401:
                return ApiErrorKind.AUTHENTICATION;
            case 403:
                return ApiErrorKind.FORBIDDEN;
            case 404:
                return ApiErrorKind.NOT_FOUND;
            case 409:
                return ApiErrorKind.CONFLICT;
            case 429:
                return ApiErrorKind.RATE_LIMITED;
            case 400:
            case 422:
                return ApiErrorKind.VALIDATION;
            default:
                return status >= 500 ? ApiErrorKind.SERVER : ApiErrorKind.UNKNOWN;
        }
    }

    /**
     * Reads {@code Retry-After} as seconds or as an HTTP date.
     *
     * @param headers the response headers
     * @param clock   the clock, for dates
     * @return the wait, or null when the header is missing or unreadable
     */
    public static Duration retryAfter(Map<String, String> headers, Clock clock) {
        String value = headers.get("Retry-After");
        if (value == null || value.isBlank()) {
            return null;
        }
        value = value.trim();
        try {
            BigDecimal seconds = new BigDecimal(value);
            if (seconds.signum() >= 0) {
                return Duration.ofMillis(seconds.movePointRight(3).longValue());
            }
            return null;
        } catch (NumberFormatException notNumber) {
            try {
                ZonedDateTime date = ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME);
                Duration wait = Duration.between(clock.instant(), date.toInstant());
                return wait.isNegative() ? Duration.ZERO : wait;
            } catch (DateTimeParseException notDate) {
                return null;
            }
        }
    }

    /**
     * Returns a short suggestion on what to do next, or null.
     *
     * @param kind                 the kind
     * @param code                 the code
     * @param validationErrorCount the number of field-level problems
     * @param correlationId        the correlation ID, or null
     * @param safety               the safety class of the request, or null
     * @return the hint, or null
     */
    public static String hintFor(ApiErrorKind kind, String code, int validationErrorCount, String correlationId,
                                 OperationSafety safety) {
        switch (code) {
            case "INVALID_RESELLER":
                return "The reseller ID is unknown, inactive or suspended in this environment. Check the reseller ID "
                        + "(a UUID from the panel's API settings) and the environment (PRODUCTION or OTE).";
            case "INVALID_API_KEY":
                return "Production and OTE use different API keys. Make sure the key belongs to the selected environment.";
            case "MISSING_API_KEY":
            case "MISSING_RESELLER_ID":
            case "NO_CREDENTIALS":
                return "Set the reseller ID and API key on DomainNameApiClient.builder(), or the DNA_RESELLER_ID and "
                        + "DNA_API_KEY environment variables.";
            case "SUBRESELLER_NOT_ALLOWED":
                return "Sub-reseller accounts cannot use the API. Use the main reseller's credentials.";
            case "RATE_LIMITED":
                return "The gateway allows 5 requests/second per key (2/second for domain searches). Slow down, or "
                        + "keep paceRequests on.";
            case "READ_ONLY":
                return "The client is read-only (readOnly(true) on the builder, or DNA_READ_ONLY is set). Turn it off "
                        + "to send changes.";
            default:
                break;
        }
        switch (kind) {
            case NOT_FOUND:
                return "Check the spelling and the environment: production and OTE have separate data.";
            case VALIDATION:
                return validationErrorCount > 0 ? "Fix the listed fields and send the request again." : null;
            case SERVER:
                return correlationId == null ? null
                        : "Server-side failure. If it persists, contact support and quote correlation ID " + correlationId + ".";
            case NETWORK:
            case TIMEOUT:
                if (safety != null && safety != OperationSafety.READ) {
                    return "The request may have reached the API. Check the current state before sending it again.";
                }
                return "Check the network, proxy and firewall. Reads are retried automatically.";
            default:
                return null;
        }
    }

    /**
     * Starts an exception with the request, status, correlation ID, body and hint filled in.
     *
     * @param request       the request
     * @param kind          the kind
     * @param code          the code
     * @param message       the message
     * @param status        the HTTP status, or null
     * @param correlationId the correlation ID, or null
     * @param body          the response body, or null
     * @return the builder
     */
    public static DomainNameApiException.Builder create(BaseRequest<?> request, ApiErrorKind kind, String code,
                                                        String message, Integer status, String correlationId, String body) {
        return DomainNameApiException.builder(kind, code, message)
                .request(request)
                .statusCode(status)
                .correlationId(correlationId)
                .responseBody(body == null || body.isEmpty() ? null : body)
                .hint(hintFor(kind, code, 0, correlationId, request.safety()));
    }

    private static JsonNode readObject(String text) {
        try {
            JsonNode node = Json.MAPPER.readTree(text);
            return node != null && node.isObject() ? node : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static JsonNode property(JsonNode node, String name) {
        for (Map.Entry<String, JsonNode> field : node.properties()) {
            if (field.getKey().equalsIgnoreCase(name)) {
                return field.getValue();
            }
        }
        return null;
    }

    private static String string(JsonNode node, String name) {
        JsonNode value = property(node, name);
        if (value == null || !value.isValueNode() || value.isNull()) {
            return "";
        }
        return value.asText();
    }

    private static String blankToNull(String value) {
        return value == null || value.isEmpty() ? null : value;
    }

    private static String seconds(Duration duration) {
        BigDecimal seconds = BigDecimal.valueOf(duration.toMillis()).movePointLeft(3).stripTrailingZeros();
        return seconds.scale() > 2 ? seconds.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString() : seconds.toPlainString();
    }

    private static String describe(Throwable cause) {
        String message = cause.getMessage();
        return message == null || message.isBlank() ? cause.getClass().getSimpleName() : message;
    }

    private static String reasonPhrase(int status) {
        switch (status) {
            case 400: return "Bad Request";
            case 401: return "Unauthorized";
            case 403: return "Forbidden";
            case 404: return "Not Found";
            case 405: return "Method Not Allowed";
            case 409: return "Conflict";
            case 422: return "Unprocessable Entity";
            case 500: return "Internal Server Error";
            case 501: return "Not Implemented";
            case 502: return "Bad Gateway";
            case 503: return "Service Unavailable";
            case 504: return "Gateway Timeout";
            default: return "Unexpected Status";
        }
    }
}
