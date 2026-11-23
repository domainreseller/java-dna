package io.github.domainreseller.dna.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.domainreseller.dna.internal.Json;
import io.github.domainreseller.dna.internal.Values;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Whether a domain can be transferred in with a given auth code.
 *
 * @param isTransferable         whether the domain can be transferred
 * @param isAuthCodeRequired     whether the registry needs an auth code
 * @param isAuthCodeValid        whether the given auth code is valid
 * @param isUserApprovalRequired whether the registrant has to approve the transfer
 * @param hasTransferLock        whether a transfer lock is on at the current registrar
 * @param domainStatuses         the registry statuses of the domain; never null
 * @param messageKey             a key identifying the message
 * @param message                the API's message
 * @param isPremium              whether the domain is a premium name
 * @param premiumPrice           the premium transfer price
 * @param premiumCurrency        the currency of {@code premiumPrice}
 * @param additionalAttributes   registry-specific attributes; never null
 */
public record TransferCheck(
        @JsonProperty("transferAvailabilityStatus") boolean isTransferable,
        @JsonProperty("authCodeIsRequired") boolean isAuthCodeRequired,
        @JsonProperty("authCodeIsValid") boolean isAuthCodeValid,
        @JsonProperty("userTransferRequired") boolean isUserApprovalRequired,
        @JsonProperty("transferLock") boolean hasTransferLock,
        List<String> domainStatuses,
        String messageKey,
        String message,
        @JsonProperty("isPremium") boolean isPremium,
        BigDecimal premiumPrice,
        String premiumCurrency,
        @JsonDeserialize(using = Json.LenientStringMapDeserializer.class) Map<String, String> additionalAttributes) {
    /**
     * Creates the check.
     *
     * @param isTransferable         whether the domain can be transferred
     * @param isAuthCodeRequired     whether an auth code is needed
     * @param isAuthCodeValid        whether the auth code is valid
     * @param isUserApprovalRequired whether the registrant has to approve
     * @param hasTransferLock        whether a transfer lock is on
     * @param domainStatuses         the registry statuses; null becomes an empty list
     * @param messageKey             a key identifying the message
     * @param message                the API's message
     * @param isPremium              whether it is a premium name
     * @param premiumPrice           the premium transfer price
     * @param premiumCurrency        the currency of the premium price
     * @param additionalAttributes   registry attributes; null becomes an empty map
     */
    public TransferCheck {
        domainStatuses = Values.list(domainStatuses);
        additionalAttributes = Values.map(additionalAttributes);
    }
}
