package io.github.domainreseller.dna.contacts;

import io.github.domainreseller.dna.internal.Values;
import java.util.List;

/**
 * The result of updating a contact handle, including the domains that use it.
 *
 * @param handle               the contact handle
 * @param contactUpdateSuccess whether the contact itself was updated
 * @param stats                counts of the affected domains
 * @param affectedDomains      what happened to each domain that uses the handle; never null
 */
public record HandleUpdateResult(
        String handle,
        boolean contactUpdateSuccess,
        Stats stats,
        List<DomainUpdate> affectedDomains) {
    /**
     * Creates the result.
     *
     * @param handle               the contact handle
     * @param contactUpdateSuccess whether the contact itself was updated
     * @param stats                counts of the affected domains
     * @param affectedDomains      what happened to each domain; null becomes an empty list
     */
    public HandleUpdateResult {
        affectedDomains = Values.list(affectedDomains);
    }

    /**
     * Counts of the domains affected by a contact update.
     *
     * @param totalDomainCount     domains that use the handle
     * @param successCount         domains updated
     * @param pendingDocumentCount domains waiting for registry documents
     * @param failedCount          domains that could not be updated
     * @param splitCount           domains that received a new handle because the change could not apply to them
     */
    public record Stats(int totalDomainCount, int successCount, int pendingDocumentCount, int failedCount, int splitCount) {
    }

    /**
     * What happened to one domain when its contact handle was updated.
     *
     * @param domainName the domain
     * @param status     the outcome for this domain
     * @param newHandle  the new handle, when the domain was given one
     * @param reason     why the domain got this outcome
     * @param message    the API's message
     */
    public record DomainUpdate(String domainName, String status, String newHandle, String reason, String message) {
    }
}
