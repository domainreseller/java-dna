package io.github.domainreseller.dna.internal;

import java.time.Duration;

/** Waits; replaced in tests so retries and pacing do not slow them down. Not part of the public API. */
@FunctionalInterface
public interface Sleeper {
    /** Sleeps with {@link Thread#sleep(long)}. */
    Sleeper SYSTEM = wait -> Thread.sleep(Math.max(0, wait.toMillis()));

    /**
     * Waits for the given time.
     *
     * @param wait how long to wait
     * @throws InterruptedException when the thread is interrupted
     */
    void sleep(Duration wait) throws InterruptedException;
}
