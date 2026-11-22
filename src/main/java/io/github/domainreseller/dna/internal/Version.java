package io.github.domainreseller.dna.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Reads the SDK version that Maven writes into {@code version.properties}. Not part of the public API. */
public final class Version {
    private Version() {
    }

    /**
     * Returns the SDK version, for example {@code 2.0.0}.
     *
     * @return the version, or {@code 0.0.0} when it cannot be read
     */
    public static String read() {
        try (InputStream in = Version.class.getResourceAsStream("/io/github/domainreseller/dna/version.properties")) {
            if (in != null) {
                Properties properties = new Properties();
                properties.load(in);
                String version = properties.getProperty("version", "").trim();
                if (!version.isEmpty() && !version.startsWith("$")) {
                    return version;
                }
            }
        } catch (IOException ignored) {
            // fall through
        }
        return "0.0.0";
    }
}
