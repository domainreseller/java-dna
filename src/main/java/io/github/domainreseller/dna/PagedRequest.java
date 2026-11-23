package io.github.domainreseller.dna;

/**
 * Base class of list requests that return one page at a time. Use {@link #skipCount(Integer)} and
 * {@link #maxResultCount(Integer)} to choose the page, or let the client page for you with
 * {@link DomainNameApiClient#listAll(PagedRequest)} and the {@code listAll} methods of the area clients.
 *
 * <p>Subclasses override the three setters with their own return type, so calls can be chained:
 * {@code new ListDomainsRequest().maxResultCount(50).domainName("example")}.
 *
 * @param <T> the type of the listed items
 */
public abstract class PagedRequest<T> extends BaseRequest<Page<T>> implements Cloneable {
    /** The page size {@code listAll} uses when {@link #maxResultCount()} is not set. */
    public static final int DEFAULT_PAGE_SIZE = 100;

    private Integer skipCount;
    private Integer maxResultCount;
    private String sorting;

    /** Creates a request for the first page. */
    protected PagedRequest() {
    }

    /**
     * Returns how many items to skip.
     *
     * @return the number of items to skip, or null for the API default (0)
     */
    public Integer skipCount() {
        return skipCount;
    }

    /**
     * Sets how many items to skip. The API starts at 0.
     *
     * @param skipCount the number of items to skip, or null for the API default
     * @return this request
     */
    public PagedRequest<T> skipCount(Integer skipCount) {
        this.skipCount = skipCount;
        return this;
    }

    /**
     * Returns the page size.
     *
     * @return the page size, or null for the API default (10)
     */
    public Integer maxResultCount() {
        return maxResultCount;
    }

    /**
     * Sets the page size. The API uses 10 when it is not set.
     *
     * @param maxResultCount the page size, or null for the API default
     * @return this request
     */
    public PagedRequest<T> maxResultCount(Integer maxResultCount) {
        this.maxResultCount = maxResultCount;
        return this;
    }

    /**
     * Returns the sort expression.
     *
     * @return the sort expression, or null for the API's order
     */
    public String sorting() {
        return sorting;
    }

    /**
     * Sets the sort expression, for example {@code expirationDate desc}.
     *
     * @param sorting the sort expression, or null for the API's order
     * @return this request
     */
    public PagedRequest<T> sorting(String sorting) {
        this.sorting = sorting;
        return this;
    }

    /**
     * Returns {@link HttpMethod#GET}: lists are read with GET.
     *
     * @return {@link HttpMethod#GET}
     */
    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }

    /**
     * Returns {@link OperationSafety#READ}: listing changes nothing.
     *
     * @return {@link OperationSafety#READ}
     */
    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }

    /**
     * Adds {@code Sorting}, {@code SkipCount} and {@code MaxResultCount}. Subclasses add their filters and call this
     * method.
     *
     * @param query the query string being built
     */
    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("Sorting", sorting)
                .add("SkipCount", skipCount)
                .add("MaxResultCount", maxResultCount);
    }

    /** Checks that the skip count is not negative and the page size is at least 1. */
    @Override
    protected void validate() {
        if (skipCount != null && skipCount < 0) {
            throw new IllegalArgumentException("skipCount cannot be negative.");
        }
        if (maxResultCount != null && maxResultCount < 1) {
            throw new IllegalArgumentException("maxResultCount must be at least 1.");
        }
    }

    /** Returns a copy of this request for another page; the original is not changed. */
    @SuppressWarnings("unchecked")
    PagedRequest<T> forPage(int skip, int pageSize) {
        PagedRequest<T> copy;
        try {
            copy = (PagedRequest<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
        copy.skipCount = skip;
        copy.maxResultCount = pageSize;
        return copy;
    }
}
