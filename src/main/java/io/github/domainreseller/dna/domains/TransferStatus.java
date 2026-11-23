package io.github.domainreseller.dna.domains;

/**
 * The state of a pending transfer.
 *
 * @param domainName        the domain name
 * @param status            the transfer state
 * @param requestedDate     when the transfer was requested, as the registry reports it
 * @param finalDate         when the transfer completes automatically, as the registry reports it
 * @param requestedClientId the registrar that requested the transfer
 * @param actionClientId    the registrar that has to act on it
 */
public record TransferStatus(
        String domainName,
        String status,
        String requestedDate,
        String finalDate,
        String requestedClientId,
        String actionClientId) {
}
