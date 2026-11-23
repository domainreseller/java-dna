package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.PagedRequest;
import io.github.domainreseller.dna.QueryParameters;
import io.github.domainreseller.dna.internal.Values;
import java.util.Arrays;
import java.util.List;

/** {@code GET domains/documents}: pages through the registry documents of a domain (mostly .tr). */
public final class ListDocumentsRequest extends PagedRequest<DomainDocument> {
    private final String domainName;
    private List<DocumentType> documentTypes = List.of();

    /**
     * Creates the request.
     *
     * @param domainName the domain
     */
    public ListDocumentsRequest(String domainName) {
        this.domainName = domainName;
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
     * Returns the document type filter.
     *
     * @return the types; empty for all
     */
    public List<DocumentType> documentTypes() {
        return documentTypes;
    }

    /**
     * Lists only these document types.
     *
     * @param documentTypes the types
     * @return this request
     */
    public ListDocumentsRequest documentTypes(DocumentType... documentTypes) {
        this.documentTypes = Values.list(Arrays.asList(documentTypes));
        return this;
    }

    @Override
    public ListDocumentsRequest skipCount(Integer skipCount) {
        super.skipCount(skipCount);
        return this;
    }

    @Override
    public ListDocumentsRequest maxResultCount(Integer maxResultCount) {
        super.maxResultCount(maxResultCount);
        return this;
    }

    @Override
    public ListDocumentsRequest sorting(String sorting) {
        super.sorting(sorting);
        return this;
    }

    @Override
    public String path() {
        return "domains/documents";
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("DomainName", domainName).addCommaSeparated("DocumentTypes", documentTypes);
        super.addQueryParameters(query);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        super.validate();
    }
}
