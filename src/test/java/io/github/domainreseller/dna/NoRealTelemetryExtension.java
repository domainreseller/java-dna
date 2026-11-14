package io.github.domainreseller.dna;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Registered for every test class through
 * {@code META-INF/services/org.junit.jupiter.api.extension.Extension} (auto-detection is
 * switched on in {@code junit-platform.properties}). It blanks the DSN new clients start
 * with, so no test can ever send an event to DomainNameAPI's real Sentry project. Tests
 * that exercise sending set a fake DSN and a fake sender explicitly.
 */
public final class NoRealTelemetryExtension implements BeforeAllCallback, BeforeEachCallback {

    static {
        Telemetry.initialDsn = "";
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        Telemetry.initialDsn = "";
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        Telemetry.initialDsn = "";
    }
}
