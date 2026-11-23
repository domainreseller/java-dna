package io.github.domainreseller.dna.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.domainreseller.dna.internal.Json;
import io.github.domainreseller.dna.internal.Values;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * A domain in your account, as returned by the domain list. {@link DomainsClient#get(String)} returns the full
 * details.
 *
 * @param domainName          the domain name
 * @param domainNameIdn       the Unicode form of an internationalised domain name
 * @param isIdn               whether the domain is an internationalised domain name
 * @param status              the lifecycle state
 * @param statusCode          the registry's status code
 * @param startDate           when the domain was registered
 * @param expirationDate      when the domain expires
 * @param remainingDays       days until the expiration date
 * @param transferDate        when the domain was transferred in, or null
 * @param updatedDate         when the domain was last changed
 * @param rgpExpirationDate   end of the redemption grace period, if the domain is in it
 * @param agpExpirationDate   end of the add grace period
 * @param failureDate         the failure date the registry reports
 * @param finalizationDate    the finalisation date the registry reports
 * @param isLocked            whether the transfer lock is on
 * @param isPrivacyProtected  whether WHOIS privacy protection is on
 * @param renewalMode         how the domain is renewed
 * @param objectClass         the registry object class
 * @param nameServers         the nameservers; never null
 * @param contacts            the contacts as the list sends them (role to value); never null
 */
public record Domain(
        String domainName,
        String domainNameIdn,
        @JsonProperty("isIdnName") boolean isIdn,
        DomainStatus status,
        String statusCode,
        OffsetDateTime startDate,
        OffsetDateTime expirationDate,
        @JsonProperty("remainingDay") int remainingDays,
        OffsetDateTime transferDate,
        OffsetDateTime updatedDate,
        OffsetDateTime rgpExpirationDate,
        OffsetDateTime agpExpirationDate,
        OffsetDateTime failureDate,
        OffsetDateTime finalizationDate,
        @JsonProperty("lockStatus") boolean isLocked,
        @JsonProperty("privacyProtectionStatus") boolean isPrivacyProtected,
        String renewalMode,
        String objectClass,
        @JsonProperty("nameservers") List<DomainNameServer> nameServers,
        @JsonDeserialize(using = Json.LenientStringMapDeserializer.class) Map<String, String> contacts) {
    /**
     * Creates a domain.
     *
     * @param domainName         the domain name
     * @param domainNameIdn      the Unicode form
     * @param isIdn              whether it is an IDN
     * @param status             the lifecycle state
     * @param statusCode         the registry's status code
     * @param startDate          when it was registered
     * @param expirationDate     when it expires
     * @param remainingDays      days until it expires
     * @param transferDate       when it was transferred in
     * @param updatedDate        when it was last changed
     * @param rgpExpirationDate  end of the redemption grace period
     * @param agpExpirationDate  end of the add grace period
     * @param failureDate        the failure date
     * @param finalizationDate   the finalisation date
     * @param isLocked           whether the transfer lock is on
     * @param isPrivacyProtected whether privacy protection is on
     * @param renewalMode        how it is renewed
     * @param objectClass        the registry object class
     * @param nameServers        the nameservers; null becomes an empty list
     * @param contacts           the contacts; null becomes an empty map
     */
    public Domain {
        nameServers = Values.list(nameServers);
        contacts = Values.map(contacts);
    }
}
