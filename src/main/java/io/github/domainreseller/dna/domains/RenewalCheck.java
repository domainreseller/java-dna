package io.github.domainreseller.dna.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 * Whether a domain can be renewed, and at what price if it is premium.
 *
 * @param isRenewable     whether the domain can be renewed
 * @param hasRenewalLock  whether a renewal lock prevents renewing
 * @param errorMessageKey the reason when it cannot be renewed
 * @param isPremium       whether the domain is a premium name
 * @param premiumPrice    the premium renewal price
 * @param premiumCurrency the currency of {@code premiumPrice}
 */
public record RenewalCheck(
        @JsonProperty("renewabilityStatus") boolean isRenewable,
        @JsonProperty("renewalLock") boolean hasRenewalLock,
        String errorMessageKey,
        @JsonProperty("isPremium") boolean isPremium,
        BigDecimal premiumPrice,
        String premiumCurrency) {
}
