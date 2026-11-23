package io.github.domainreseller.dna;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Base class of every API request. A request knows its HTTP method, its path, what it can do to your account
 * ({@link #safety()}), its query string, its JSON body and how to check itself before it is sent.
 *
 * <p>The type parameter is the type of the decoded answer; use {@link Void} for operations that return nothing.
 * Send any request with {@link DomainNameApiClient#send(BaseRequest)}. The client adds the credentials, retries
 * according to the safety class, paces requests, honours read-only mode and turns failures into
 * {@link DomainNameApiException}.
 *
 * <p>A request for an endpoint the SDK does not cover yet:
 * <pre>{@code
 * public final class GetDomainNotesRequest extends BaseRequest<DomainNotes> {
 *     private final String domainName;
 *
 *     public GetDomainNotesRequest(String domainName) {
 *         this.domainName = domainName;
 *     }
 *
 *     @Override public HttpMethod method() { return HttpMethod.GET; }
 *     @Override public String path() { return "domains/notes"; }          // below /api/v1/
 *     @Override public OperationSafety safety() { return OperationSafety.READ; }
 *
 *     @Override protected void addQueryParameters(QueryParameters query) {
 *         query.add("DomainName", domainName);
 *     }
 *
 *     @Override protected void validate() {
 *         require(domainName, "domainName");
 *     }
 * }
 *
 * DomainNotes notes = client.send(new GetDomainNotesRequest("example.com"));
 * }</pre>
 *
 * @param <T> the type of the decoded answer, or {@link Void}
 */
public abstract class BaseRequest<T> {
    /** Creates a request. */
    protected BaseRequest() {
    }

    /**
     * Returns the HTTP method.
     *
     * @return the method
     */
    public abstract HttpMethod method();

    /**
     * Returns the path below {@code /api/v1/} with path parameters filled in and escaped, for example
     * {@code domains/info} or {@code contacts/DNA-12345}.
     *
     * @return the path
     */
    public abstract String path();

    /**
     * Returns what the operation can do to your account.
     *
     * @return the safety class
     */
    public abstract OperationSafety safety();

    /**
     * Returns whether the request may be sent again after a network error or a 502/503/504 answer. True for reads.
     * Override it for writes that are harmless to repeat, such as replacing a list. Billable requests must never
     * return true, so nothing is charged twice.
     *
     * @return whether the request is safe to repeat
     */
    public boolean isIdempotent() {
        return safety() == OperationSafety.READ;
    }

    /**
     * Adds the request's query string parameters. Does nothing by default.
     *
     * @param query the query string being built
     */
    protected void addQueryParameters(QueryParameters query) {
    }

    /**
     * Returns the object written as the JSON body, or null for a request without a body (the default).
     * {@link JsonBody} builds an object; records, maps, lists and the SDK's models work as well.
     *
     * @return the body, or null
     */
    protected Object body() {
        return null;
    }

    /**
     * Checks the request before it is sent. Throw an {@link IllegalArgumentException} for missing or invalid values;
     * nothing is sent in that case. Does nothing by default.
     */
    protected void validate() {
    }

    /**
     * Returns the type the answer is decoded into. By default it is taken from the type argument of
     * {@link BaseRequest} in the class declaration, which covers every request declared as
     * {@code class X extends BaseRequest<Answer>}. Override it only when the type cannot be read from the class,
     * for example in a generic request class.
     *
     * @return the answer type, or null to read it from the class declaration
     */
    protected Type responseType() {
        return null;
    }

    /**
     * Throws an {@link IllegalArgumentException} when the text is null or blank.
     *
     * @param value the value to check
     * @param name  the field name used in the message
     */
    protected static void require(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required.");
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} when the collection is null or empty.
     *
     * @param values the values to check
     * @param name   the field name used in the message
     */
    protected static void require(Collection<?> values, String name) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException(name + " needs at least one value.");
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} when the value is null.
     *
     * @param value the value to check
     * @param name  the field name used in the message
     */
    protected static void require(Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required.");
        }
    }

    /**
     * Returns {@code METHOD path}, for example {@code POST domains/renew}.
     *
     * @return the method and path
     */
    @Override
    public String toString() {
        return method() + " " + path();
    }
}
