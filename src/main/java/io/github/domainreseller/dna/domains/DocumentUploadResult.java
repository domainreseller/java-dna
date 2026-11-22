package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.internal.Values;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * The result of uploading registry documents.
 *
 * @param uploaded           the documents that were stored; never null
 * @param duplicates         file names that were already uploaded; never null
 * @param failed             file names that could not be stored; never null
 * @param totalDocumentCount the number of documents the domain has now
 * @param totalDocumentSize  the total size of the domain's documents in bytes
 */
public record DocumentUploadResult(
        List<UploadedDocument> uploaded,
        List<String> duplicates,
        List<String> failed,
        int totalDocumentCount,
        long totalDocumentSize) {
    /**
     * Creates the result.
     *
     * @param uploaded           the stored documents; null becomes an empty list
     * @param duplicates         the duplicate file names; null becomes an empty list
     * @param failed             the failed file names; null becomes an empty list
     * @param totalDocumentCount the number of documents
     * @param totalDocumentSize  the total size in bytes
     */
    public DocumentUploadResult {
        uploaded = Values.list(uploaded);
        duplicates = Values.list(duplicates);
        failed = Values.list(failed);
    }

    /**
     * A document that was stored.
     *
     * @param name         the file name
     * @param size         the size in bytes
     * @param mimeType     the detected MIME type
     * @param type         what the document is
     * @param uploadedDate when it was uploaded
     */
    public record UploadedDocument(String name, long size, String mimeType, DocumentType type, OffsetDateTime uploadedDate) {
    }
}
