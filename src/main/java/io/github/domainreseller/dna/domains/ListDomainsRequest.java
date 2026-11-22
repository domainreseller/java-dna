package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.PagedRequest;
import io.github.domainreseller.dna.QueryParameters;
import io.github.domainreseller.dna.internal.Values;
import java.util.Arrays;
import java.util.List;

/**
 * {@code GET domains}: pages through the domains in your account.
 *
 * <pre>{@code
 * client.domains().listAll(new ListDomainsRequest().statuses(DomainStatus.ACTIVE))
 *         .forEach(d -> System.out.println(d.domainName()));
 * }</pre>
 */
public final class ListDomainsRequest extends PagedRequest<Domain> {
    private String domainName;
    private List<DomainStatus> statuses = List.of();

    /** Creates a request for all domains. */
    public ListDomainsRequest() {
    }

    /**
     * Returns the name filter.
     *
     * @return the text, or null
     */
    public String domainName() {
        return domainName;
    }

    /**
     * Lists only domains whose name contains this text.
     *
     * @param domainName the text
     * @return this request
     */
    public ListDomainsRequest domainName(String domainName) {
        this.domainName = domainName;
        return this;
    }

    /**
     * Returns the state filter.
     *
     * @return the states; empty for all
     */
    public List<DomainStatus> statuses() {
        return statuses;
    }

    /**
     * Lists only domains in these states.
     *
     * @param statuses the states
     * @return this request
     */
    public ListDomainsRequest statuses(DomainStatus... statuses) {
        this.statuses = Values.list(Arrays.asList(statuses));
        return this;
    }

    @Override
    public ListDomainsRequest skipCount(Integer skipCount) {
        super.skipCount(skipCount);
        return this;
    }

    @Override
    public ListDomainsRequest maxResultCount(Integer maxResultCount) {
        super.maxResultCount(maxResultCount);
        return this;
    }

    @Override
    public ListDomainsRequest sorting(String sorting) {
        super.sorting(sorting);
        return this;
    }

    @Override
    public String path() {
        return "domains";
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("DomainName", domainName).addCommaSeparated("StatusIds", statuses);
        super.addQueryParameters(query);
    }
}
