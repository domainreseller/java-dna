package io.github.domainreseller.dna;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/** A client wired to a {@link FakeHttpClient}, with pacing off and waits recorded instead of slept. */
public final class TestClient {
    public static final String RESELLER_ID = "00000000-0000-4000-8000-000000000001";
    public static final String API_KEY = "test-api-key";

    private final FakeHttpClient http = new FakeHttpClient();
    private final List<Duration> delays = Collections.synchronizedList(new ArrayList<>());
    private final DomainNameApiClient client;

    public TestClient() {
        this(builder -> {
        });
    }

    public TestClient(Consumer<DomainNameApiClient.Builder> configure) {
        DomainNameApiClient.Builder builder = DomainNameApiClient.builder()
                .credentials(RESELLER_ID, API_KEY)
                .environment(ApiEnvironment.OTE)
                .paceRequests(false)
                .httpClient(http)
                .sleeper(delays::add);
        configure.accept(builder);
        client = builder.build();
    }

    public DomainNameApiClient client() {
        return client;
    }

    public FakeHttpClient http() {
        return http;
    }

    /** Every wait the client asked for (retries and pacing). */
    public List<Duration> delays() {
        return new ArrayList<>(delays);
    }

    public RecordedRequest lastRequest() {
        return http.lastRequest();
    }

    public List<RecordedRequest> requests() {
        return http.requests();
    }
}
