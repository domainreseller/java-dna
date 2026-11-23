package io.github.domainreseller.dna.domains;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A nameserver of a domain.
 *
 * @param name              the host name
 * @param isChildNameServer whether it is a child nameserver of the domain itself (such as ns1.example.com for
 *                          example.com)
 * @param isGlueRecord      whether the registry holds a glue record for it
 * @param isLinked          whether it is linked to a host object
 */
public record DomainNameServer(
        String name,
        @JsonProperty("isChildnameServer") boolean isChildNameServer,
        @JsonProperty("isGlueRecord") boolean isGlueRecord,
        @JsonProperty("isLinked") boolean isLinked) {
}
