package io.github.domainreseller.dna.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 * Whether a domain can be registered, and at what price.
 *
 * @param domainName         the domain name
 * @param tld                the TLD, for example {@code COM}
 * @param status             the availability as the API reports it: {@code AVAILABLE} or {@code NOTAVAILABLE}
 * @param reason             why the domain is not available, for example {@code Domain exists}
 * @param isPremium          whether the domain is a premium name with a registry-set price, or null when unknown
 * @param isDocumentRequired whether the registry requires documents for this domain (common for .tr)
 * @param price              the registration price for {@code period} years
 * @param currency           the currency of {@code price}
 * @param period             the period in years the price is for
 */
public record DomainAvailability(
        String domainName,
        String tld,
        String status,
        String reason,
        @JsonProperty("isPremium") Boolean isPremium,
        @JsonProperty("isDocumentRequired") boolean isDocumentRequired,
        BigDecimal price,
        String currency,
        Integer period) {
    /**
     * Returns whether the domain can be registered.
     *
     * @return true when {@link #status()} is {@code AVAILABLE}
     */
    public boolean isAvailable() {
        return "AVAILABLE".equalsIgnoreCase(status);
    }
}
