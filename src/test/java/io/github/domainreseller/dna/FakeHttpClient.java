package io.github.domainreseller.dna;

import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;

/**
 * Stands in for the network: records every request and answers with queued responses. No test talks to the real
 * API; a request without a queued response fails the test.
 */
public final class FakeHttpClient extends HttpClient {
    /** Produces the answer to one request. */
    @FunctionalInterface
    public interface Responder {
        HttpResponse<String> respond(HttpRequest request) throws IOException, InterruptedException;
    }

    private final Deque<Responder> responses = new ArrayDeque<>();
    private final List<RecordedRequest> requests = new ArrayList<>();

    public synchronized List<RecordedRequest> requests() {
        return new ArrayList<>(requests);
    }

    public synchronized RecordedRequest lastRequest() {
        if (requests.isEmpty()) {
            throw new AssertionError("No request was sent.");
        }
        return requests.get(requests.size() - 1);
    }

    /** Queues a response; headers are given as name/value pairs. */
    public FakeHttpClient respond(int status, String body, String... headers) {
        return enqueue(request -> new FakeResponse(request, request.uri(), status, body, headers));
    }

    /** Queues a 200 response with a JSON body. */
    public FakeHttpClient respondJson(String json) {
        return respond(200, json);
    }

    /** Queues a network failure. */
    public FakeHttpClient fail(IOException exception) {
        return enqueue(request -> {
            throw exception;
        });
    }

    public synchronized FakeHttpClient enqueue(Responder responder) {
        responses.add(responder);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> handler)
            throws IOException, InterruptedException {
        Responder responder;
        synchronized (this) {
            requests.add(new RecordedRequest(request.method(), request.uri(), headers(request), readBody(request)));
            responder = responses.poll();
        }
        if (responder == null) {
            throw new AssertionError("No response queued for " + request.method() + " " + request.uri());
        }
        return (HttpResponse<T>) responder.respond(request);
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> handler) {
        throw new UnsupportedOperationException("The SDK sends synchronously.");
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> handler,
                                                            HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
        throw new UnsupportedOperationException("The SDK sends synchronously.");
    }

    @Override
    public Optional<CookieHandler> cookieHandler() {
        return Optional.empty();
    }

    @Override
    public Optional<Duration> connectTimeout() {
        return Optional.empty();
    }

    @Override
    public Redirect followRedirects() {
        return Redirect.NEVER;
    }

    @Override
    public Optional<ProxySelector> proxy() {
        return Optional.empty();
    }

    @Override
    public SSLContext sslContext() {
        return null;
    }

    @Override
    public SSLParameters sslParameters() {
        return null;
    }

    @Override
    public Optional<Authenticator> authenticator() {
        return Optional.empty();
    }

    @Override
    public Version version() {
        return Version.HTTP_1_1;
    }

    @Override
    public Optional<Executor> executor() {
        return Optional.empty();
    }

    private static Map<String, String> headers(HttpRequest request) {
        Map<String, String> headers = new java.util.TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        request.headers().map().forEach((name, values) -> headers.put(name, String.join(", ", values)));
        return headers;
    }

    private static String readBody(HttpRequest request) {
        Optional<HttpRequest.BodyPublisher> publisher = request.bodyPublisher();
        if (publisher.isEmpty() || publisher.get().contentLength() == 0) {
            return null;
        }
        CompletableFuture<String> body = new CompletableFuture<>();
        publisher.get().subscribe(new Flow.Subscriber<>() {
            private final java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(ByteBuffer item) {
                byte[] chunk = new byte[item.remaining()];
                item.get(chunk);
                bytes.write(chunk, 0, chunk.length);
            }

            @Override
            public void onError(Throwable throwable) {
                body.completeExceptionally(throwable);
            }

            @Override
            public void onComplete() {
                body.complete(bytes.toString(StandardCharsets.UTF_8));
            }
        });
        try {
            return body.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new AssertionError("Could not read the request body", e);
        }
    }

    /** A canned response. */
    public static final class FakeResponse implements HttpResponse<String> {
        private final HttpRequest request;
        private final URI uri;
        private final int status;
        private final String body;
        private final HttpHeaders headers;

        public FakeResponse(HttpRequest request, URI uri, int status, String body, String... headerPairs) {
            this.request = request;
            this.uri = uri;
            this.status = status;
            this.body = body == null ? "" : body;
            Map<String, List<String>> map = new LinkedHashMap<>();
            for (int i = 0; i + 1 < headerPairs.length; i += 2) {
                map.computeIfAbsent(headerPairs[i], k -> new ArrayList<>()).add(headerPairs[i + 1]);
            }
            this.headers = HttpHeaders.of(map, (a, b) -> true);
        }

        @Override
        public int statusCode() {
            return status;
        }

        @Override
        public HttpRequest request() {
            return request;
        }

        @Override
        public Optional<HttpResponse<String>> previousResponse() {
            return Optional.empty();
        }

        @Override
        public HttpHeaders headers() {
            return headers;
        }

        @Override
        public String body() {
            return body;
        }

        @Override
        public Optional<SSLSession> sslSession() {
            return Optional.empty();
        }

        @Override
        public URI uri() {
            return uri;
        }

        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_1_1;
        }
    }
}
