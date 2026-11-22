package io.github.domainreseller.dna.internal;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Spaces requests per rate-limit class so a client stays under the gateway's per-key limits: 5 requests/second in
 * general, 2/second for domain searches (which also count towards the general budget) and a separate budget for DNS
 * zone calls, which are kept 1 second apart. Domain info and TLD list calls are not rate limited.
 * Not part of the public API.
 */
public final class RequestPacer {
    static final Duration GENERAL_GAP = Duration.ofMillis(200);
    static final Duration SEARCH_GAP = Duration.ofMillis(500);
    static final Duration ZONE_GAP = Duration.ofSeconds(1);

    private final Clock clock;
    private final Sleeper sleeper;
    private final Map<String, Instant> next = new HashMap<>();

    /**
     * Creates a pacer.
     *
     * @param clock   the clock
     * @param sleeper waits until a slot is free
     */
    public RequestPacer(Clock clock, Sleeper sleeper) {
        this.clock = clock;
        this.sleeper = sleeper;
    }

    /**
     * The rate-limit class of a path and the minimum gap between its requests.
     *
     * @param name the class: {@code general}, {@code search}, {@code zones}, or empty when not limited
     * @param gap  the minimum gap
     */
    public record RateClass(String name, Duration gap) {
    }

    /**
     * Returns the rate-limit class of a path below {@code /api/v1/}.
     *
     * @param path the path, with or without a query string
     * @return the class and its gap
     */
    public static RateClass classify(String path) {
        String p = path == null ? "" : path.toLowerCase(Locale.ROOT);
        while (p.startsWith("/")) {
            p = p.substring(1);
        }
        int q = p.indexOf('?');
        if (q >= 0) {
            p = p.substring(0, q);
        }
        if (p.startsWith("domains/zones")) {
            return new RateClass("zones", ZONE_GAP);
        }
        if (p.equals("domains/search") || p.equals("domains/bulk-search")) {
            return new RateClass("search", SEARCH_GAP);
        }
        if (p.startsWith("domains/info") || p.startsWith("products/tlds")) {
            return new RateClass("", Duration.ZERO);
        }
        return new RateClass("general", GENERAL_GAP);
    }

    /**
     * Waits until a request to the path may be sent.
     *
     * @param path the path below {@code /api/v1/}
     * @throws InterruptedException when the thread is interrupted while waiting
     */
    public void await(String path) throws InterruptedException {
        RateClass rateClass = classify(path);
        if (rateClass.gap().isZero()) {
            return;
        }
        Instant now;
        Instant slot;
        synchronized (next) {
            now = clock.instant();
            Instant previous = next.getOrDefault(rateClass.name(), Instant.MIN);
            slot = previous.isAfter(now) ? previous : now;
            next.put(rateClass.name(), slot.plus(rateClass.gap()));
            if (rateClass.name().equals("search")) {
                // Searches also count towards the general budget; zone calls have their own.
                Instant general = next.getOrDefault("general", Instant.MIN);
                if (general.isBefore(slot.plus(GENERAL_GAP))) {
                    next.put("general", slot.plus(GENERAL_GAP));
                }
            }
        }
        Duration wait = Duration.between(now, slot);
        if (!wait.isZero() && !wait.isNegative()) {
            sleeper.sleep(wait);
        }
    }
}
