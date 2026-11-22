package io.github.domainreseller.dna.domains;

/**
 * The transfer (EPP) code of a domain.
 *
 * @param domainName the domain name
 * @param authCode   the auth code
 */
public record DomainAuthCode(String domainName, String authCode) {
}
