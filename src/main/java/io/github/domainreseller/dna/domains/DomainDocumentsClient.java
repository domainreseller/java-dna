package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.DomainNameApiClient;
import io.github.domainreseller.dna.Page;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

/** Registry documents of domains (mostly .tr). Reach it through {@link DomainsClient#documents()}. */
public final class DomainDocumentsClient {
    private final DomainNameApiClient api;

    /**
     * Creates the client. Use {@code client.domains().documents()} instead.
     *
     * @param api the client that sends the requests
     */
    public DomainDocumentsClient(DomainNameApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    /**
     * Returns one page of the documents of a domain.
     *
     * @param request the domain, filters and page
     * @return the page
     */
    public Page<DomainDocument> list(ListDocumentsRequest request) {
        return api.send(request);
    }

    /**
     * Returns every document of a domain, fetching pages as the stream is consumed.
     *
     * @param domainName the domain
     * @return the documents
     */
    public Stream<DomainDocument> listAll(String domainName) {
        return api.listAll(new ListDocumentsRequest(domainName));
    }

    /**
     * Uploads documents for a domain.
     *
     * @param domainName the domain
     * @param documents  the documents; create them with {@link DocumentUpload#fromFile}
     * @return what was stored
     */
    public DocumentUploadResult upload(String domainName, Collection<DocumentUpload> documents) {
        return api.send(new UploadDocumentsRequest(domainName, documents));
    }
}
