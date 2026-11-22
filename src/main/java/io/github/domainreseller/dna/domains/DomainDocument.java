package io.github.domainreseller.dna.domains;

import java.time.OffsetDateTime;

/**
 * A registry document of a domain.
 *
 * @param name          the file name
 * @param type          what the document is
 * @param status        the review state
 * @param operationType the operation the document was requested for
 * @param uploadedDate  when the document was uploaded
 */
public record DomainDocument(
        String name,
        DocumentType type,
        DocumentStatus status,
        DocumentOperationType operationType,
        OffsetDateTime uploadedDate) {
}
