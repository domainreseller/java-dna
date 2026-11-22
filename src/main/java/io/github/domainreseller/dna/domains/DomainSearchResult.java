package io.github.domainreseller.dna.domains;

/**
 * The result of searching one domain.
 *
 * @param success          whether the registry answered the search
 * @param code             the registry's result code
 * @param operationMessage the registry's message
 * @param reason           why the search failed, if it did
 * @param info             availability and price
 */
public record DomainSearchResult(
        boolean success,
        String code,
        String operationMessage,
        String reason,
        DomainAvailability info) {
}
