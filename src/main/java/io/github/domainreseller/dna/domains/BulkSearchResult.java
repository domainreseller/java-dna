package io.github.domainreseller.dna.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.domainreseller.dna.internal.Values;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The result of searching several domains at once.
 *
 * @param success          whether the registry answered the search
 * @param code             the registry's result code
 * @param operationMessage the registry's message
 * @param reason           why the search failed, if it did
 * @param results          one entry per domain; never null
 */
public record BulkSearchResult(
        boolean success,
        String code,
        String operationMessage,
        String reason,
        @JsonProperty("infos") List<DomainAvailability> results) {
    /**
     * Creates the result.
     *
     * @param success          whether the registry answered the search
     * @param code             the registry's result code
     * @param operationMessage the registry's message
     * @param reason           why the search failed
     * @param results          one entry per domain; null becomes an empty list
     */
    public BulkSearchResult {
        results = Values.list(results);
    }

    /**
     * Returns the domains that can be registered.
     *
     * @return the available domains
     */
    public List<DomainAvailability> available() {
        return results.stream().filter(DomainAvailability::isAvailable).collect(Collectors.toUnmodifiableList());
    }
}
