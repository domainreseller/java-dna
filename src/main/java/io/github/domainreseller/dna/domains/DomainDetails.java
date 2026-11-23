package io.github.domainreseller.dna.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.domainreseller.dna.contacts.Contact;
import io.github.domainreseller.dna.contacts.ContactType;
import io.github.domainreseller.dna.internal.Json;
import io.github.domainreseller.dna.internal.Values;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * The full details of a domain: dates, state, lock and privacy, nameservers, child nameservers, contacts, auth code
 * and registry attributes.
 *
 * @param domainName           the domain name
 * @param domainNameIdn        the Unicode form of an internationalised domain name
 * @param isIdn                whether the domain is an internationalised domain name
 * @param status               the state, as text (for example {@code Active})
 * @param statusReason         why the domain is in its state, when the API says
 * @param statusCode           the registry's status code
 * @param rgpStatus            the redemption grace period status
 * @param startDate            when the domain was registered
 * @param expirationDate       when the domain expires
 * @param remainingDays        days until the expiration date
 * @param transferDate         when the domain was transferred in, or null
 * @param updatedDate          when the domain was last changed
 * @param resellerId           the reseller that holds the domain
 * @param renewalMode          how the domain is renewed
 * @param authCode             the transfer (EPP) code
 * @param isLocked             whether the transfer lock is on
 * @param isPrivacyProtected   whether WHOIS privacy protection is on
 * @param isForwarded          whether URL forwarding is set up
 * @param useTrusteeContact    whether a trustee contact is used
 * @param objectId             the registry object ID
 * @param nameServers          the nameservers; never null
 * @param hosts                the child nameservers (glue records) of the domain; never null
 * @param contacts             the contacts, one per role; never null
 * @param additionalAttributes registry-specific attributes, such as those of .tr domains; never null
 */
public record DomainDetails(
        String domainName,
        String domainNameIdn,
        @JsonProperty("isIdnName") boolean isIdn,
        String status,
        String statusReason,
        String statusCode,
        @JsonProperty("rgpStatues") String rgpStatus,
        OffsetDateTime startDate,
        OffsetDateTime expirationDate,
        @JsonProperty("remainingDay") int remainingDays,
        OffsetDateTime transferDate,
        OffsetDateTime updatedDate,
        UUID resellerId,
        String renewalMode,
        String authCode,
        @JsonProperty("lockStatus") boolean isLocked,
        @JsonProperty("privacyProtectionStatus") boolean isPrivacyProtected,
        @JsonProperty("isForwarded") boolean isForwarded,
        boolean useTrusteeContact,
        String objectId,
        @JsonProperty("nameservers") List<String> nameServers,
        List<DomainHost> hosts,
        List<Contact> contacts,
        @JsonDeserialize(using = Json.LenientStringMapDeserializer.class) Map<String, String> additionalAttributes) {
    /**
     * Creates the details.
     *
     * @param domainName           the domain name
     * @param domainNameIdn        the Unicode form
     * @param isIdn                whether it is an IDN
     * @param status               the state
     * @param statusReason         why it is in its state
     * @param statusCode           the registry's status code
     * @param rgpStatus            the redemption grace period status
     * @param startDate            when it was registered
     * @param expirationDate       when it expires
     * @param remainingDays        days until it expires
     * @param transferDate         when it was transferred in
     * @param updatedDate          when it was last changed
     * @param resellerId           the reseller that holds it
     * @param renewalMode          how it is renewed
     * @param authCode             the transfer code
     * @param isLocked             whether the transfer lock is on
     * @param isPrivacyProtected   whether privacy protection is on
     * @param isForwarded          whether forwarding is set up
     * @param useTrusteeContact    whether a trustee contact is used
     * @param objectId             the registry object ID
     * @param nameServers          the nameservers; null becomes an empty list
     * @param hosts                the child nameservers; null becomes an empty list
     * @param contacts             the contacts; null becomes an empty list
     * @param additionalAttributes registry attributes; null becomes an empty map
     */
    public DomainDetails {
        nameServers = Values.list(nameServers);
        hosts = Values.list(hosts);
        contacts = Values.list(contacts);
        additionalAttributes = Values.map(additionalAttributes);
    }

    /**
     * Returns the contact with the given role.
     *
     * @param role the role, for example {@link ContactType#REGISTRANT}
     * @return the contact, or empty when the domain has none with that role
     */
    public Optional<Contact> contact(ContactType role) {
        return contacts.stream().filter(c -> role.equals(c.contactType())).findFirst();
    }
}
