package io.github.domainreseller.dna.ssl;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The subject of a decoded CSR.
 *
 * @param domainName       the common name (domain)
 * @param country          two-letter country code
 * @param state            state or province
 * @param locality         city
 * @param organization     organisation
 * @param organizationUnit organisational unit
 * @param alternativeNames the subject alternative names
 * @param isWildcard       whether the CSR is for a wildcard name
 * @param isSan            whether the CSR has subject alternative names
 */
public record CsrDetails(
        String domainName,
        String country,
        String state,
        String locality,
        String organization,
        String organizationUnit,
        String alternativeNames,
        @JsonProperty("isWildcard") boolean isWildcard,
        @JsonProperty("isSan") boolean isSan) {
}
