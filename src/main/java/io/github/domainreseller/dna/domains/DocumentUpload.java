package io.github.domainreseller.dna.domains;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;

/**
 * A document to upload for a domain. Create one with {@link #fromFile(DocumentType, Path)} or
 * {@link #fromBytes(DocumentType, String, byte[])}.
 *
 * @param documentType what the document is
 * @param fileName     the file name, including its extension (for example {@code id.pdf})
 * @param fileContent  the file content, Base64-encoded
 */
public record DocumentUpload(DocumentType documentType, String fileName, String fileContent) {
    /**
     * Creates an upload from bytes.
     *
     * @param documentType what the document is
     * @param fileName     the file name, including its extension
     * @param content      the file content
     * @return the upload
     */
    public static DocumentUpload fromBytes(DocumentType documentType, String fileName, byte[] content) {
        Objects.requireNonNull(content, "content");
        return new DocumentUpload(documentType, fileName, Base64.getEncoder().encodeToString(content));
    }

    /**
     * Creates an upload from a file on disk.
     *
     * @param documentType what the document is
     * @param path         the file
     * @return the upload
     * @throws UncheckedIOException when the file cannot be read
     */
    public static DocumentUpload fromFile(DocumentType documentType, Path path) {
        try {
            return fromBytes(documentType, path.getFileName().toString(), Files.readAllBytes(path));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + path, e);
        }
    }
}
