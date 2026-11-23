package io.github.domainreseller.dna.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The executor the asynchronous methods use when none is configured: a cached pool of daemon threads, created on
 * first use and shared by all clients. Requests block while waiting for the API, so the common fork-join pool is
 * not used. Not part of the public API.
 */
public final class AsyncExecutor {
    private AsyncExecutor() {
    }

    /**
     * Returns the shared executor.
     *
     * @return the executor
     */
    public static ExecutorService get() {
        return Holder.EXECUTOR;
    }

    private static final class Holder {
        private static final AtomicInteger COUNTER = new AtomicInteger();
        static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(task -> {
            Thread thread = new Thread(task, "domainnameapi-async-" + COUNTER.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        });
    }
}
