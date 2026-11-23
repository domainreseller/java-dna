package io.github.domainreseller.dna;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.domainreseller.dna.dns.RecordType;
import io.github.domainreseller.dna.internal.RequestPacer;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PacingTest {
    /** A clock that only moves when the pacer sleeps or the test advances it. */
    static final class ManualClock extends Clock {
        Instant now = Instant.parse("2026-11-22T10:00:00Z");
        final List<Duration> sleeps = new ArrayList<>();

        void sleep(Duration wait) {
            sleeps.add(wait);
            now = now.plus(wait);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return now;
        }
    }

    @ParameterizedTest
    @CsvSource({
            "domains/zones, zones, 1000",
            "/domains/zones/apply, zones, 1000",
            "domains/search, search, 500",
            "domains/bulk-search, search, 500",
            "domains/info?DomainName=x, '', 0",
            "products/tlds, '', 0",
            "domains/renew, general, 200",
            "deposit/accounts/me, general, 200",
    })
    void classifiesPaths(String path, String name, long gapMillis) {
        RequestPacer.RateClass rateClass = RequestPacer.classify(path);
        assertEquals(name, rateClass.name());
        assertEquals(Duration.ofMillis(gapMillis), rateClass.gap());
    }

    @Test
    void spacesCallsOfTheSameClass() throws InterruptedException {
        ManualClock clock = new ManualClock();
        RequestPacer pacer = new RequestPacer(clock, clock::sleep);

        pacer.await("domains/zones");
        pacer.await("domains/zones");
        pacer.await("domains/zones");

        assertEquals(List.of(Duration.ofSeconds(1), Duration.ofSeconds(1)), clock.sleeps);
    }

    @Test
    void searchesAlsoUseTheGeneralBudget() throws InterruptedException {
        ManualClock clock = new ManualClock();
        RequestPacer pacer = new RequestPacer(clock, clock::sleep);

        pacer.await("domains/search");
        pacer.await("domains/renew");

        assertEquals(List.of(Duration.ofMillis(200)), clock.sleeps);
    }

    @Test
    void doesNotWaitWhenEnoughTimePassed() throws InterruptedException {
        ManualClock clock = new ManualClock();
        RequestPacer pacer = new RequestPacer(clock, clock::sleep);

        pacer.await("domains/renew");
        clock.now = clock.now.plusSeconds(1);
        pacer.await("domains/renew");
        pacer.await("domains/info");

        assertTrue(clock.sleeps.isEmpty());
    }

    @Test
    void theClientPacesZoneCalls() {
        ManualClock clock = new ManualClock();
        TestClient t = new TestClient(b -> b.paceRequests(true).clock(clock).sleeper(clock::sleep));
        t.http().respond(200, "").respond(200, "");

        t.client().dns().add("example.com", "www", RecordType.A, "192.0.2.1");
        t.client().dns().add("example.com", "www", RecordType.A, "192.0.2.2");

        assertEquals(List.of(Duration.ofSeconds(1)), clock.sleeps);
    }
}
