package io.github.domainreseller.dna;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import io.github.domainreseller.dna.accounts.AccountClient;
import io.github.domainreseller.dna.contacts.ContactsClient;
import io.github.domainreseller.dna.dns.DnsClient;
import io.github.domainreseller.dna.dnssec.DnssecClient;
import io.github.domainreseller.dna.domains.DomainsClient;
import io.github.domainreseller.dna.hosting.HostingClient;
import io.github.domainreseller.dna.internal.AsyncExecutor;
import io.github.domainreseller.dna.internal.ErrorParser;
import io.github.domainreseller.dna.internal.Json;
import io.github.domainreseller.dna.internal.RequestPacer;
import io.github.domainreseller.dna.internal.Sleeper;
import io.github.domainreseller.dna.internal.Version;
import io.github.domainreseller.dna.orders.OrdersClient;
import io.github.domainreseller.dna.products.ProductsClient;
import io.github.domainreseller.dna.ssl.SslClient;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeMap;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The entry point of the SDK. Each area of the API has its own client: {@link #domains()}, {@link #contacts()},
 * {@link #dns()}, {@link #dnssec()}, {@link #ssl()}, {@link #hosting()}, {@link #orders()}, {@link #products()} and
 * {@link #account()}. Any request, including your own {@link BaseRequest} subclasses, can also be sent with
 * {@link #send(BaseRequest)}.
 *
 * <pre>{@code
 * DomainNameApiClient client = DomainNameApiClient.create(resellerId, apiKey, ApiEnvironment.OTE);
 *
 * ResellerAccount account = client.account().get();
 * DomainSearchResult search = client.domains().search("example.com");
 * client.domains().listAll().forEach(domain ->
 *         System.out.println(domain.domainName() + " expires " + domain.expirationDate()));
 * }</pre>
 *
 * <p>The client is thread-safe and meant to be reused: create one per set of credentials and keep it for the
 * lifetime of the application. Failed requests throw {@link DomainNameApiException}. Rate limits are respected
 * automatically, 429 answers are retried, and network errors are retried only for reads, so nothing is charged
 * twice. The SDK sends nothing anywhere except your requests to the API.
 */
public final class DomainNameApiClient {
    /** The SDK version, for example {@code 2.0.0}. */
    public static final String VERSION = Version.read();

    /** The default {@code User-Agent} header. */
    public static final String DEFAULT_USER_AGENT =
            "DomainNameAPI-Java/" + VERSION + " (+https://github.com/domainreseller/java-dna)";

    private static final String API_PREFIX = "api/v1/";

    private final HttpClient http;
    private final String resellerId;
    private final String apiKey;
    private final ApiEnvironment environment;
    private final URI baseUri;
    private final Duration timeout;
    private final int maxRetries;
    private final Duration maxRetryDelay;
    private final boolean paceRequests;
    private final boolean readOnly;
    private final String userAgent;
    private final Executor executor;
    private final Clock clock;
    private final Sleeper sleeper;
    private final RequestPacer pacer;

    private final AccountClient account;
    private final ContactsClient contacts;
    private final DomainsClient domains;
    private final DnsClient dns;
    private final DnssecClient dnssec;
    private final SslClient ssl;
    private final HostingClient hosting;
    private final OrdersClient orders;
    private final ProductsClient products;

    private DomainNameApiClient(Builder builder) {
        this.resellerId = builder.resellerId == null ? "" : builder.resellerId.trim();
        this.apiKey = builder.apiKey == null ? "" : builder.apiKey.trim();
        this.environment = builder.environment;
        this.baseUri = resolveBaseUri(builder.baseUri != null ? builder.baseUri : builder.environment.baseUri());
        this.timeout = builder.timeout;
        this.maxRetries = builder.maxRetries;
        this.maxRetryDelay = builder.maxRetryDelay;
        this.paceRequests = builder.paceRequests;
        this.readOnly = builder.readOnly;
        this.userAgent = builder.userAgent == null || builder.userAgent.isBlank()
                ? DEFAULT_USER_AGENT : builder.userAgent.trim();
        this.executor = builder.executor;
        this.clock = builder.clock;
        this.sleeper = builder.sleeper;
        this.pacer = new RequestPacer(clock, sleeper);
        this.http = builder.httpClient != null ? builder.httpClient : HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(builder.timeout)
                .build();

        this.account = new AccountClient(this);
        this.contacts = new ContactsClient(this);
        this.domains = new DomainsClient(this);
        this.dns = new DnsClient(this);
        this.dnssec = new DnssecClient(this);
        this.ssl = new SslClient(this);
        this.hosting = new HostingClient(this);
        this.orders = new OrdersClient(this);
        this.products = new ProductsClient(this);
    }

    /**
     * Creates a client for the production environment with default settings.
     *
     * @param resellerId your reseller ID (a UUID) from the reseller panel's API settings
     * @param apiKey     your API key from the reseller panel's API settings
     * @return the client
     */
    public static DomainNameApiClient create(String resellerId, String apiKey) {
        return builder().credentials(resellerId, apiKey).build();
    }

    /**
     * Creates a client with default settings.
     *
     * @param resellerId  your reseller ID (a UUID) from the reseller panel's API settings
     * @param apiKey      your API key from the reseller panel's API settings; production and OTE use different keys
     * @param environment {@link ApiEnvironment#PRODUCTION} or the {@link ApiEnvironment#OTE} test environment
     * @return the client
     */
    public static DomainNameApiClient create(String resellerId, String apiKey, ApiEnvironment environment) {
        return builder().credentials(resellerId, apiKey).environment(environment).build();
    }

    /**
     * Creates a client from the {@code DNA_RESELLER_ID}, {@code DNA_API_KEY}, {@code DNA_ENV} (or
     * {@code DNA_TEST_MODE}), {@code DNA_BASE_URL} and {@code DNA_READ_ONLY} environment variables, the same ones
     * the {@code dna} CLI reads. Use {@link Builder#fromEnvironment()} to change other settings as well.
     *
     * @return the client
     * @throws IllegalArgumentException when {@code DNA_ENV} or {@code DNA_BASE_URL} cannot be read
     */
    public static DomainNameApiClient fromEnvironment() {
        return Builder.fromEnvironment().build();
    }

    /**
     * Starts building a client. Only the credentials are required; everything else has a sensible default.
     *
     * @return a builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the environment this client talks to.
     *
     * @return the environment
     */
    public ApiEnvironment environment() {
        return environment;
    }

    /**
     * Returns the base address requests are sent to, for example {@code https://api.domainresellerapi.com/}.
     *
     * @return the base address, ending with {@code /}
     */
    public URI baseUri() {
        return baseUri;
    }

    /**
     * Returns whether the client blocks every request that is not a read.
     *
     * @return whether read-only mode is on
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Returns the {@code User-Agent} header the client sends.
     *
     * @return the user agent
     */
    public String userAgent() {
        return userAgent;
    }

    /**
     * Your account: balances, profile and the transaction ledger.
     *
     * @return the account client
     */
    public AccountClient account() {
        return account;
    }

    /**
     * Reusable contact handles (registrant, admin, tech, billing) and their verification.
     *
     * @return the contacts client
     */
    public ContactsClient contacts() {
        return contacts;
    }

    /**
     * Search, register, renew, transfer and manage domains, with URL forwarding, child nameservers and documents.
     *
     * @return the domains client
     */
    public DomainsClient domains() {
        return domains;
    }

    /**
     * DNS zone records of domains that use the platform nameservers.
     *
     * @return the DNS client
     */
    public DnsClient dns() {
        return dns;
    }

    /**
     * DNSSEC (DS records) of domains.
     *
     * @return the DNSSEC client
     */
    public DnssecClient dnssec() {
        return dnssec;
    }

    /**
     * SSL certificates: buy, configure, issue, list, renew, reissue and revoke.
     *
     * @return the SSL client
     */
    public SslClient ssl() {
        return ssl;
    }

    /**
     * Web hosting packages and services.
     *
     * @return the hosting client
     */
    public HostingClient hosting() {
        return hosting;
    }

    /**
     * Orders.
     *
     * @return the orders client
     */
    public OrdersClient orders() {
        return orders;
    }

    /**
     * The product catalogue and prices: TLDs, SSL certificates and other products.
     *
     * @return the products client
     */
    public ProductsClient products() {
        return products;
    }

    /**
     * Sends a request and returns the decoded answer.
     *
     * @param request the request
     * @param <T>     the type of the answer
     * @return the answer, or null for requests of type {@code BaseRequest<Void>}
     * @throws DomainNameApiException   when the API refused the request or it failed on the network
     * @throws IllegalArgumentException when the request is missing a required value; nothing was sent
     * @throws CancellationException    when the thread was interrupted; the interrupt flag is set again
     */
    public <T> T send(BaseRequest<T> request) {
        return sendWithResponse(request).value();
    }

    /**
     * Sends a request and returns the decoded answer together with the HTTP details: correlation ID, soft-quota
     * state, number of attempts and duration.
     *
     * @param request the request
     * @param <T>     the type of the answer
     * @return the answer and the HTTP details
     * @throws DomainNameApiException   when the API refused the request or it failed on the network
     * @throws IllegalArgumentException when the request is missing a required value; nothing was sent
     * @throws CancellationException    when the thread was interrupted; the interrupt flag is set again
     */
    public <T> ApiResponse<T> sendWithResponse(BaseRequest<T> request) {
        Objects.requireNonNull(request, "request");
        Exchange exchange = exchange(request);
        T value = decode(request, exchange);
        return new ApiResponse<>(value, exchange.status, exchange.headers, exchange.attempts, exchange.duration,
                quota(exchange.headers));
    }

    /**
     * Sends a request on the client's executor. The future fails with the same exceptions {@link #send} throws.
     *
     * @param request the request
     * @param <T>     the type of the answer
     * @return a future answer
     */
    public <T> CompletableFuture<T> sendAsync(BaseRequest<T> request) {
        Objects.requireNonNull(request, "request");
        return CompletableFuture.supplyAsync(() -> send(request), executor());
    }

    /**
     * Sends a request on the client's executor and returns the answer with the HTTP details.
     *
     * @param request the request
     * @param <T>     the type of the answer
     * @return a future answer with the HTTP details
     */
    public <T> CompletableFuture<ApiResponse<T>> sendWithResponseAsync(BaseRequest<T> request) {
        Objects.requireNonNull(request, "request");
        return CompletableFuture.supplyAsync(() -> sendWithResponse(request), executor());
    }

    /**
     * Pages through a list request and returns every item as a lazy stream: a page is fetched only when the stream
     * needs it. The request's own {@link PagedRequest#skipCount()} is the starting point and its
     * {@link PagedRequest#maxResultCount()} the page size ({@link PagedRequest#DEFAULT_PAGE_SIZE} when not set).
     * The request object is not changed.
     *
     * @param request the list request with its filters
     * @param <T>     the type of the items
     * @return the items of all pages
     */
    public <T> Stream<T> listAll(PagedRequest<T> request) {
        Objects.requireNonNull(request, "request");
        Iterator<T> iterator = new PageIterator<>(this, request);
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.NONNULL), false);
    }

    /**
     * Returns the environment and base address, for debugging. Never shows the credentials.
     *
     * @return a readable description
     */
    @Override
    public String toString() {
        return "DomainNameApiClient[" + environment + ", " + baseUri + (readOnly ? ", read-only" : "") + "]";
    }

    // ---------------------------------------------------------------------------------------------- transport

    private Exchange exchange(BaseRequest<?> request) {
        request.validate();

        if (readOnly && request.safety() != OperationSafety.READ) {
            throw ErrorParser.create(request, ApiErrorKind.READ_ONLY, "READ_ONLY",
                    request + " is a " + request.safety().name().toLowerCase(Locale.ROOT)
                            + " operation and the client is read-only; nothing was sent.",
                    null, null, null).build();
        }
        if (resellerId.isEmpty() || apiKey.isEmpty()) {
            throw ErrorParser.create(request, ApiErrorKind.AUTHENTICATION, "NO_CREDENTIALS",
                    "The reseller ID and API key are required; nothing was sent.", null, null, null).build();
        }

        QueryParameters query = new QueryParameters();
        request.addQueryParameters(query);
        URI uri = buildUri(request.path(), query);
        Object body = request.body();
        String json = body == null ? null : Json.write(body);
        long started = System.nanoTime();

        try {
            for (int attempt = 1; ; attempt++) {
                if (paceRequests) {
                    pacer.await(request.path());
                }

                HttpResponse<String> response;
                try {
                    response = http.send(buildMessage(request.method(), uri, json),
                            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    boolean timedOut = e instanceof HttpTimeoutException;
                    if (request.isIdempotent() && attempt <= maxRetries) {
                        sleeper.sleep(backoff(attempt));
                        continue;
                    }
                    throw ErrorParser.network(request, e, timedOut, timeout);
                }

                int status = response.statusCode();
                Map<String, String> headers = readHeaders(response);
                boolean redirected = redirectedToErrorPage(uri, response);
                String text = response.body() == null ? "" : response.body();

                if (status >= 200 && status < 300 && !redirected) {
                    return new Exchange(status, headers, text, attempt, Duration.ofNanos(System.nanoTime() - started));
                }

                boolean retry = status == 429 || (request.isIdempotent() && (status == 502 || status == 503 || status == 504));
                if (retry && !redirected && attempt <= maxRetries) {
                    Duration retryAfter = ErrorParser.retryAfter(headers, clock);
                    Duration wait = retryAfter == null ? backoff(attempt) : retryAfter.plusMillis(50);
                    if (wait.compareTo(maxRetryDelay) <= 0) {
                        sleeper.sleep(wait);
                        continue;
                    }
                }

                throw ErrorParser.fromResponse(request, status, headers, text, redirected, clock);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            CancellationException cancelled = new CancellationException(request + " was interrupted.");
            cancelled.initCause(e);
            throw cancelled;
        }
    }

    private <T> T decode(BaseRequest<T> request, Exchange exchange) {
        JavaType type = Json.responseType(request.getClass(), BaseRequest.class, request.responseType());
        if (type.getRawClass() == Void.class) {
            return null;
        }
        String trimmed = exchange.body.trim();
        if (trimmed.isEmpty() || trimmed.equals("null")) {
            throw ErrorParser.create(request, ApiErrorKind.UNKNOWN, "EMPTY_RESPONSE",
                    "The API answered " + request + " with an empty body.",
                    exchange.status, exchange.headers.get("X-Correlation-Id"), exchange.body).build();
        }
        try {
            return Json.MAPPER.readValue(trimmed, type);
        } catch (JsonProcessingException e) {
            throw ErrorParser.create(request, ApiErrorKind.UNKNOWN, "INVALID_RESPONSE",
                    "The answer to " + request + " could not be read as " + type.getRawClass().getSimpleName()
                            + ": " + e.getOriginalMessage(),
                    exchange.status, exchange.headers.get("X-Correlation-Id"), exchange.body).cause(e).build();
        }
    }

    private URI buildUri(String path, QueryParameters query) {
        String relative = path == null ? "" : path;
        while (relative.startsWith("/")) {
            relative = relative.substring(1);
        }
        String text = baseUri + API_PREFIX + relative;
        if (query.size() > 0) {
            text += "?" + query;
        }
        try {
            return new URI(text);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("The request path is not a valid URI: " + text, e);
        }
    }

    private HttpRequest buildMessage(HttpMethod method, URI uri, String json) {
        HttpRequest.Builder message = HttpRequest.newBuilder(uri)
                .timeout(timeout)
                .header("Accept", "application/json")
                .header("User-Agent", userAgent)
                .header("X-API-KEY", apiKey)
                .header("__reseller", resellerId);
        HttpRequest.BodyPublisher publisher = json == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8);
        if (json != null) {
            message.header("Content-Type", "application/json; charset=utf-8");
        }
        return message.method(method.name(), publisher).build();
    }

    private static Map<String, String> readHeaders(HttpResponse<?> response) {
        Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Map.Entry<String, List<String>> header : response.headers().map().entrySet()) {
            headers.put(header.getKey(), String.join(", ", header.getValue()));
        }
        return headers;
    }

    /**
     * A custom HttpClient may follow redirects. The gateway redirects unknown or suspended resellers to
     * /Error/Suspend, which then answers 200 with an HTML page; treat that as the redirect it was.
     */
    private static boolean redirectedToErrorPage(URI requested, HttpResponse<?> response) {
        URI last = response.uri();
        if (last == null || last.getPath() == null) {
            return false;
        }
        return !last.getPath().equals(requested.getPath())
                && last.getPath().toLowerCase(Locale.ROOT).contains("/error/");
    }

    private static Duration backoff(int attempt) {
        int jitter = ThreadLocalRandom.current().nextInt(250);
        return Duration.ofMillis((500L << Math.min(attempt - 1, 6)) + jitter);
    }

    private static ApiQuota quota(Map<String, String> headers) {
        if (!headers.containsKey("X-Quota-Limit") && !headers.containsKey("X-Quota-Used")) {
            return null;
        }
        Function<String, Double> number = name -> {
            String value = headers.get(name);
            try {
                return value == null ? 0d : Double.parseDouble(value.trim());
            } catch (NumberFormatException e) {
                return 0d;
            }
        };
        return new ApiQuota(headers.get("X-Quota-Scope"), number.apply("X-Quota-Cost"), number.apply("X-Quota-Used"),
                number.apply("X-Quota-Limit"), number.apply("X-Quota-Delay"), number.apply("X-Quota-Reset"));
    }

    private Executor executor() {
        return executor != null ? executor : AsyncExecutor.get();
    }

    private static URI resolveBaseUri(URI address) {
        String path = address.getPath() == null || address.getPath().isEmpty() ? "/" : address.getPath();
        if (!path.endsWith("/")) {
            path += "/";
        }
        try {
            return new URI(address.getScheme(), address.getAuthority(), path, null, null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Not a valid base address: " + address, e);
        }
    }

    private static final class Exchange {
        final int status;
        final Map<String, String> headers;
        final String body;
        final int attempts;
        final Duration duration;

        Exchange(int status, Map<String, String> headers, String body, int attempts, Duration duration) {
            this.status = status;
            this.headers = headers;
            this.body = body;
            this.attempts = attempts;
            this.duration = duration;
        }
    }

    /** Fetches the pages of a list request as the items are consumed. */
    private static final class PageIterator<T> implements Iterator<T> {
        private final DomainNameApiClient client;
        private final PagedRequest<T> request;
        private final int pageSize;
        private final Deque<T> buffer = new ArrayDeque<>();
        private int skip;
        private boolean done;

        PageIterator(DomainNameApiClient client, PagedRequest<T> request) {
            this.client = client;
            this.request = request;
            this.pageSize = request.maxResultCount() != null ? request.maxResultCount() : PagedRequest.DEFAULT_PAGE_SIZE;
            this.skip = request.skipCount() != null ? request.skipCount() : 0;
        }

        @Override
        public boolean hasNext() {
            while (buffer.isEmpty() && !done) {
                Page<T> page = client.send(request.forPage(skip, pageSize));
                for (T item : page.items()) {
                    if (item != null) {
                        buffer.add(item);
                    }
                }
                skip += page.items().size();
                if (page.items().isEmpty() || skip >= page.totalCount()) {
                    done = true;
                }
            }
            return !buffer.isEmpty();
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return buffer.poll();
        }
    }

    /**
     * Settings of a {@link DomainNameApiClient}. Only the credentials are required; everything else has a sensible
     * default. A builder is not thread-safe; the client it builds is.
     *
     * <pre>{@code
     * DomainNameApiClient client = DomainNameApiClient.builder()
     *         .credentials(resellerId, apiKey)
     *         .environment(ApiEnvironment.OTE)
     *         .timeout(Duration.ofSeconds(30))
     *         .readOnly(true)
     *         .build();
     * }</pre>
     */
    public static final class Builder {
        private String resellerId = "";
        private String apiKey = "";
        private ApiEnvironment environment = ApiEnvironment.PRODUCTION;
        private URI baseUri;
        private Duration timeout = Duration.ofSeconds(60);
        private int maxRetries = 2;
        private Duration maxRetryDelay = Duration.ofSeconds(30);
        private boolean paceRequests = true;
        private boolean readOnly;
        private String userAgent;
        private HttpClient httpClient;
        private Executor executor;
        private Clock clock = Clock.systemUTC();
        private Sleeper sleeper = Sleeper.SYSTEM;

        private Builder() {
        }

        /**
         * Returns a builder filled from the environment variables the DomainNameAPI tools share:
         * {@code DNA_RESELLER_ID} and {@code DNA_API_KEY} (the credentials), {@code DNA_ENV} ({@code production} or
         * {@code ote}) or {@code DNA_TEST_MODE} ({@code true} for OTE), {@code DNA_BASE_URL} (an optional base address
         * override) and {@code DNA_READ_ONLY} ({@code true} blocks everything but reads).
         *
         * @return the builder
         * @throws IllegalArgumentException when {@code DNA_ENV} or {@code DNA_BASE_URL} cannot be read
         */
        public static Builder fromEnvironment() {
            return fromEnvironment(System::getenv);
        }

        static Builder fromEnvironment(Function<String, String> variables) {
            Function<String, String> variable = name -> {
                String value = variables.apply(name);
                return value == null ? "" : value.trim();
            };
            Builder builder = new Builder().credentials(variable.apply("DNA_RESELLER_ID"), variable.apply("DNA_API_KEY"));

            String env = variable.apply("DNA_ENV");
            if (env.isEmpty()) {
                env = variable.apply("DNA_TEST_MODE");
            }
            String name = env;
            builder.environment(ApiEnvironment.parse(env).orElseThrow(() ->
                    new IllegalArgumentException("DNA_ENV must be production or ote, not '" + name + "'.")));

            String baseUrl = variable.apply("DNA_BASE_URL");
            if (!baseUrl.isEmpty()) {
                URI uri;
                try {
                    uri = new URI(baseUrl);
                } catch (URISyntaxException e) {
                    uri = null;
                }
                if (uri == null || !uri.isAbsolute() || uri.getHost() == null) {
                    throw new IllegalArgumentException("DNA_BASE_URL is not an absolute URL: '" + baseUrl + "'.");
                }
                builder.baseUri(uri);
            }

            String readOnly = variable.apply("DNA_READ_ONLY").toLowerCase(Locale.ROOT);
            builder.readOnly(readOnly.equals("true") || readOnly.equals("1") || readOnly.equals("on") || readOnly.equals("yes"));
            return builder;
        }

        /**
         * Sets the credentials. Both are sent with every request ({@code __reseller} and {@code X-API-KEY} headers).
         *
         * @param resellerId your reseller ID (a UUID), from the reseller panel's API settings
         * @param apiKey     your API key, from the reseller panel's API settings; production and OTE use different keys
         * @return this builder
         */
        public Builder credentials(String resellerId, String apiKey) {
            return resellerId(resellerId).apiKey(apiKey);
        }

        /**
         * Sets the reseller ID.
         *
         * @param resellerId your reseller ID (a UUID), from the reseller panel's API settings
         * @return this builder
         */
        public Builder resellerId(String resellerId) {
            this.resellerId = resellerId == null ? "" : resellerId;
            return this;
        }

        /**
         * Sets the API key.
         *
         * @param apiKey your API key, from the reseller panel's API settings
         * @return this builder
         */
        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey == null ? "" : apiKey;
            return this;
        }

        /**
         * Chooses the deployment. {@link ApiEnvironment#PRODUCTION} by default.
         *
         * @param environment the environment
         * @return this builder
         */
        public Builder environment(ApiEnvironment environment) {
            this.environment = Objects.requireNonNull(environment, "environment");
            return this;
        }

        /**
         * Overrides the base address chosen by the environment, for example for a proxy or a local test server.
         * Only the scheme, host, port and path prefix are used.
         *
         * @param baseUri the base address, or null to use the environment's
         * @return this builder
         */
        public Builder baseUri(URI baseUri) {
            if (baseUri != null && (!baseUri.isAbsolute() || baseUri.getHost() == null)) {
                throw new IllegalArgumentException("The base address must be an absolute URL, not '" + baseUri + "'.");
            }
            this.baseUri = baseUri;
            return this;
        }

        /**
         * Sets the time limit of one attempt. 60 seconds by default.
         *
         * @param timeout the time limit; must be positive
         * @return this builder
         */
        public Builder timeout(Duration timeout) {
            if (timeout == null || timeout.isZero() || timeout.isNegative()) {
                throw new IllegalArgumentException("timeout must be positive.");
            }
            this.timeout = timeout;
            return this;
        }

        /**
         * Sets how often a failed request is repeated. 2 by default; 0 turns retries off. 429 answers are always
         * retried (the gateway refused them before anything ran). Network errors and 502/503/504 answers are retried
         * only for reads and idempotent writes, so nothing is charged twice.
         *
         * @param maxRetries the number of retries; cannot be negative
         * @return this builder
         */
        public Builder maxRetries(int maxRetries) {
            if (maxRetries < 0) {
                throw new IllegalArgumentException("maxRetries cannot be negative.");
            }
            this.maxRetries = maxRetries;
            return this;
        }

        /**
         * Sets the longest wait before a retry. A {@code Retry-After} longer than this is not waited for.
         * 30 seconds by default.
         *
         * @param maxRetryDelay the longest wait; cannot be negative
         * @return this builder
         */
        public Builder maxRetryDelay(Duration maxRetryDelay) {
            if (maxRetryDelay == null || maxRetryDelay.isNegative()) {
                throw new IllegalArgumentException("maxRetryDelay cannot be negative.");
            }
            this.maxRetryDelay = maxRetryDelay;
            return this;
        }

        /**
         * Spaces requests to stay under the gateway's per-key rate limits: 5 requests/second in general,
         * 2/second for domain searches and 1/second for DNS zone calls. On by default.
         *
         * @param paceRequests whether to pace requests
         * @return this builder
         */
        public Builder paceRequests(boolean paceRequests) {
            this.paceRequests = paceRequests;
            return this;
        }

        /**
         * Blocks every request that is not a read ({@link OperationSafety#READ}) before it is sent. Useful while
         * exploring the API or in reporting tools. Off by default.
         *
         * @param readOnly whether to block changes
         * @return this builder
         */
        public Builder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        /**
         * Sets the {@code User-Agent} header. Defaults to {@link DomainNameApiClient#DEFAULT_USER_AGENT}. The API sits
         * behind Cloudflare, which rejects empty or generic agents.
         *
         * @param userAgent the user agent, or null for the default
         * @return this builder
         */
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        /**
         * Uses your own {@link HttpClient}, for example one with a proxy or custom TLS settings. The SDK's own client
         * follows no redirects; a client that does is still handled. The time limit of each attempt is set on the
         * request, so the client's settings for it do not matter.
         *
         * @param httpClient the HTTP client, or null for the SDK's own
         * @return this builder
         */
        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        /**
         * Sets the executor the {@code sendAsync} methods run on. By default a shared pool of daemon threads.
         *
         * @param executor the executor, or null for the default
         * @return this builder
         */
        public Builder executor(Executor executor) {
            this.executor = executor;
            return this;
        }

        // Test seams: replace the clock and the waiting used by pacing and retries.
        Builder clock(Clock clock) {
            this.clock = Objects.requireNonNull(clock);
            return this;
        }

        Builder sleeper(Sleeper sleeper) {
            this.sleeper = Objects.requireNonNull(sleeper);
            return this;
        }

        /**
         * Creates the client. The settings are copied; later changes to this builder have no effect on it.
         *
         * @return the client
         */
        public DomainNameApiClient build() {
            return new DomainNameApiClient(this);
        }
    }
}
