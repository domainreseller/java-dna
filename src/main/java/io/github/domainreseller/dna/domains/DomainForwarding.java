package io.github.domainreseller.dna.domains;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The URL forwarding of a domain.
 *
 * @param redirectAddress the URL the domain forwards to
 * @param forwardType     how the domain forwards: Standard (HTTP 301) or Frame (masked)
 */
public record DomainForwarding(
        @JsonProperty("redirectAlias") String redirectAddress,
        @JsonProperty("redirectType") ForwardType forwardType) {
}
