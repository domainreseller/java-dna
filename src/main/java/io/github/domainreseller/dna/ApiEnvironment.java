package io.github.domainreseller.dna;

import java.net.URI;
import java.util.Locale;
import java.util.Optional;

/** The API deployment a client talks to. Each environment has its own data, balance and API key. */
public enum ApiEnvironment {
    /** The live environment ({@code https://api.domainresellerapi.com}): real domains and real charges. */
    PRODUCTION(URI.create("https://api.domainresellerapi.com/")),

    /**
     * The test environment ({@code https://ote.domainresellerapi.com}), also called OTE. Use your OTE API key with
     * it; nothing you do there touches live domains or your live balance.
     */
    OTE(URI.create("https://ote.domainresellerapi.com/"));

    private final URI baseUri;

    ApiEnvironment(URI baseUri) {
        this.baseUri = baseUri;
    }

    /**
     * Returns the base address of the environment.
     *
     * @return the base address, ending with {@code /}
     */
    public URI baseUri() {
        return baseUri;
    }

    /**
     * Reads an environment name. Accepts {@code production}, {@code prod}, {@code live} and {@code ote},
     * {@code test}, {@code testing}, {@code sandbox}, as well as the test-mode flags other DomainNameAPI tools use
     * ({@code true}, {@code 1}, {@code on}, {@code yes} mean OTE; {@code false}, {@code 0}, {@code off}, {@code no}
     * mean production). A null or empty value means production. Case and surrounding spaces are ignored.
     *
     * @param value the text to read
     * @return the environment, or empty when the text was not recognised
     */
    public static Optional<ApiEnvironment> parse(String value) {
        switch (value == null ? "" : value.trim().toLowerCase(Locale.ROOT)) {
            case "":
            case "production":
            case "prod":
            case "live":
            case "false":
            case "0":
            case "off":
            case "no":
                return Optional.of(PRODUCTION);
            case "ote":
            case "test":
            case "testing":
            case "sandbox":
            case "true":
            case "1":
            case "on":
            case "yes":
                return Optional.of(OTE);
            default:
                return Optional.empty();
        }
    }
}
