package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.internal.Values;
import java.util.Collection;
import java.util.List;

/** {@code POST domains/documents}: uploads registry documents for a domain. */
public final class UploadDocumentsRequest extends BaseRequest<DocumentUploadResult> {
    private final String domainName;
    private final List<DocumentUpload> documents;

    /**
     * Creates the request.
     *
     * @param domainName the domain
     * @param documents  the documents; create them with {@link DocumentUpload#fromFile}
     */
    public UploadDocumentsRequest(String domainName, Collection<DocumentUpload> documents) {
        this.domainName = domainName;
        this.documents = Values.list(documents);
    }

    /**
     * Returns the domain.
     *
     * @return the domain name
     */
    public String domainName() {
        return domainName;
    }

    /**
     * Returns the documents.
     *
     * @return the documents
     */
    public List<DocumentUpload> documents() {
        return documents;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "domains/documents";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.WRITE;
    }

    @Override
    protected Object body() {
        return JsonBody.create().add("domainName", domainName).add("documents", documents);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        require(documents, "documents");
        for (DocumentUpload document : documents) {
            if (document == null || document.fileName() == null || document.fileName().isBlank()
                    || document.fileContent() == null || document.fileContent().isEmpty()) {
                throw new IllegalArgumentException("Every document needs a file name and content.");
            }
        }
    }
}
