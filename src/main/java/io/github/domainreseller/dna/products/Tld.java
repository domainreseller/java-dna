package io.github.domainreseller.dna.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.domainreseller.dna.internal.Values;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * A TLD with its prices, periods, rules and the attributes a registration needs.
 *
 * @param name                         the TLD without a leading dot, for example {@code com} or {@code com.tr}
 * @param minRegistrationPeriod        the shortest registration, in years
 * @param maxRegistrationPeriod        the longest registration, in years
 * @param registrationPeriods          the registration periods offered, in years; never null
 * @param renewalPeriods               the renewal periods offered, in years; never null
 * @param prices                       prices per price group: register, renew, transfer, restore; never null
 * @param attributes                   attributes the registry needs at registration, passed as {@code tldAttributes};
 *                                     never null
 * @param supportsTransfer             whether domains can be transferred in
 * @param supportsTransferCheck        whether a transfer can be checked before it is sent
 * @param requiresAuthCode             whether transfers need an auth code
 * @param supportsTransferLock         whether the transfer lock can be set
 * @param supportsIdn                  whether internationalised names are supported
 * @param requiresDocument             whether the registry asks for documents
 * @param supportsTrustee              whether a trustee (local presence) service is offered
 * @param requiresTrustee              whether a trustee is required
 * @param isSaleAllowed                whether the TLD is on sale
 * @param supportsHandleUpdate         whether contacts can be changed through handles
 * @param supportsPrivacy              whether WHOIS privacy is offered
 * @param supportsDnssec               whether DNSSEC is supported
 * @param supportsIpv6                 whether IPv6 glue records are accepted
 * @param supportsExplicitRenewal      whether the registry supports explicit renewals
 * @param allowsGlueRecords            whether glue records are allowed
 * @param acceptsHostObjects           whether the registry uses host objects for nameservers
 * @param hasRegistryDnsCheck          whether the registry checks the nameservers' DNS
 * @param isAvailableInTestEnvironment whether the TLD can be used in the OTE test environment
 * @param addGracePeriod               add grace period, in days
 * @param autoRenewGracePeriod         auto-renew grace period, in days
 * @param finalizationPeriod           finalisation period, in days
 * @param failurePeriod                failure period, in days
 * @param paymentPeriod                payment period, in days
 * @param deletionRestorablePeriod     how long a deleted domain can be restored, in days
 * @param deletionHoldPeriod           how long a deleted domain is held, in days
 * @param transferPeriod               transfer period, in days
 * @param registerPeriod               register period, in days
 * @param renewalPeriod                renewal period, in days
 */
public record Tld(
        String name,
        int minRegistrationPeriod,
        int maxRegistrationPeriod,
        List<Integer> registrationPeriods,
        List<Integer> renewalPeriods,
        List<PriceGroup> prices,
        List<Attribute> attributes,
        @JsonProperty("isSupportTransfer") boolean supportsTransfer,
        @JsonProperty("isTransferCheckSupported") boolean supportsTransferCheck,
        @JsonProperty("isRequiredAuthCode") boolean requiresAuthCode,
        @JsonProperty("isTransferLockPossible") boolean supportsTransferLock,
        @JsonProperty("isSupportIdn") boolean supportsIdn,
        @JsonProperty("isRequireDocument") boolean requiresDocument,
        @JsonProperty("isSupportTrustee") boolean supportsTrustee,
        @JsonProperty("isTrusteeRequired") boolean requiresTrustee,
        @JsonProperty("isSaleAllowed") boolean isSaleAllowed,
        @JsonProperty("isHandleUpdateAvailable") boolean supportsHandleUpdate,
        @JsonProperty("isPrivacyAvailable") boolean supportsPrivacy,
        @JsonProperty("isDnssecAvailable") boolean supportsDnssec,
        @JsonProperty("isIpv6Available") boolean supportsIpv6,
        @JsonProperty("isExplicitRenewalSupported") boolean supportsExplicitRenewal,
        @JsonProperty("isGlueRecordAllowed") boolean allowsGlueRecords,
        @JsonProperty("isHostObjectAccepted") boolean acceptsHostObjects,
        @JsonProperty("registryDnsCheck") boolean hasRegistryDnsCheck,
        @JsonProperty("availableInTestEnvironment") boolean isAvailableInTestEnvironment,
        int addGracePeriod,
        int autoRenewGracePeriod,
        int finalizationPeriod,
        int failurePeriod,
        int paymentPeriod,
        int deletionRestorablePeriod,
        int deletionHoldPeriod,
        int transferPeriod,
        int registerPeriod,
        int renewalPeriod) {
    /**
     * Creates a TLD. Null lists become empty lists.
     *
     * @param name                         the TLD
     * @param minRegistrationPeriod        the shortest registration
     * @param maxRegistrationPeriod        the longest registration
     * @param registrationPeriods          the registration periods
     * @param renewalPeriods               the renewal periods
     * @param prices                       the prices per price group
     * @param attributes                   the registration attributes
     * @param supportsTransfer             whether transfers in are possible
     * @param supportsTransferCheck        whether transfers can be checked
     * @param requiresAuthCode             whether transfers need an auth code
     * @param supportsTransferLock         whether the transfer lock can be set
     * @param supportsIdn                  whether IDNs are supported
     * @param requiresDocument             whether documents are needed
     * @param supportsTrustee              whether a trustee is offered
     * @param requiresTrustee              whether a trustee is required
     * @param isSaleAllowed                whether the TLD is on sale
     * @param supportsHandleUpdate         whether handles can be updated
     * @param supportsPrivacy              whether privacy is offered
     * @param supportsDnssec               whether DNSSEC is supported
     * @param supportsIpv6                 whether IPv6 glue is accepted
     * @param supportsExplicitRenewal      whether explicit renewals are supported
     * @param allowsGlueRecords            whether glue records are allowed
     * @param acceptsHostObjects           whether host objects are used
     * @param hasRegistryDnsCheck          whether the registry checks DNS
     * @param isAvailableInTestEnvironment whether it can be used in OTE
     * @param addGracePeriod               add grace period
     * @param autoRenewGracePeriod         auto-renew grace period
     * @param finalizationPeriod           finalisation period
     * @param failurePeriod                failure period
     * @param paymentPeriod                payment period
     * @param deletionRestorablePeriod     restorable period
     * @param deletionHoldPeriod           hold period
     * @param transferPeriod               transfer period
     * @param registerPeriod               register period
     * @param renewalPeriod                renewal period
     */
    public Tld {
        registrationPeriods = Values.list(registrationPeriods);
        renewalPeriods = Values.list(renewalPeriods);
        prices = Values.list(prices);
        attributes = Values.list(attributes);
    }

    /**
     * Returns the registration price for a period from the first price group that has one.
     *
     * @param years the period in years
     * @return the price, or empty when the TLD has none for that period
     */
    public Optional<Price> registrationPrice(int years) {
        return prices.stream().flatMap(g -> g.register().stream()).filter(p -> p.period() == years).findFirst();
    }

    /**
     * Returns the renewal price for a period from the first price group that has one.
     *
     * @param years the period in years
     * @return the price, or empty when the TLD has none for that period
     */
    public Optional<Price> renewalPrice(int years) {
        return prices.stream().flatMap(g -> g.renew().stream()).filter(p -> p.period() == years).findFirst();
    }

    /**
     * The prices of a TLD in one price group.
     *
     * @param priceGroup the price group
     * @param register   registration prices per period; never null
     * @param renew      renewal prices per period; never null
     * @param transfer   transfer prices per period; never null
     * @param restore    the restore price, or null
     * @param refund     the refund amount, or null
     * @param backorder  the backorder price, or null
     */
    public record PriceGroup(
            String priceGroup,
            List<Price> register,
            List<Price> renew,
            List<Price> transfer,
            Price restore,
            Price refund,
            Price backorder) {
        /**
         * Creates a price group.
         *
         * @param priceGroup the price group
         * @param register   registration prices; null becomes an empty list
         * @param renew      renewal prices; null becomes an empty list
         * @param transfer   transfer prices; null becomes an empty list
         * @param restore    the restore price
         * @param refund     the refund amount
         * @param backorder  the backorder price
         */
        public PriceGroup {
            register = Values.list(register);
            renew = Values.list(renew);
            transfer = Values.list(transfer);
        }
    }

    /**
     * A price for a period.
     *
     * @param period   the period in years
     * @param price    the price
     * @param currency the currency
     */
    public record Price(int period, BigDecimal price, String currency) {
    }

    /**
     * An attribute a registry needs at registration (for example for .tr domains).
     *
     * @param key         the key to use in {@code tldAttributes}
     * @param description what the attribute means
     * @param isRequired  whether it must be given
     * @param type        the value type
     * @param options     the allowed values, when the attribute is a choice; never null
     */
    public record Attribute(
            String key,
            String description,
            @JsonProperty("isRequired") boolean isRequired,
            String type,
            List<Option> options) {
        /**
         * Creates an attribute.
         *
         * @param key         the key
         * @param description what it means
         * @param isRequired  whether it must be given
         * @param type        the value type
         * @param options     the allowed values; null becomes an empty list
         */
        public Attribute {
            options = Values.list(options);
        }
    }

    /**
     * An allowed value of a TLD attribute.
     *
     * @param value the value
     */
    public record Option(String value) {
    }
}
