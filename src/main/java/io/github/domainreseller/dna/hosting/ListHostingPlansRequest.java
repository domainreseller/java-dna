package io.github.domainreseller.dna.hosting;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;
import java.util.List;
import java.util.UUID;

/** {@code GET hostings/plans}: the hosting service plans and their limits. */
public final class ListHostingPlansRequest extends BaseRequest<List<ServicePlan>> {
    private final UUID hostingId;

    /** Creates the request for all plans. */
    public ListHostingPlansRequest() {
        this(null);
    }

    /**
     * Creates the request for the plans available to one hosting.
     *
     * @param hostingId the hosting, or null for all plans
     */
    public ListHostingPlansRequest(UUID hostingId) {
        this.hostingId = hostingId;
    }

    /**
     * Returns the hosting filter.
     *
     * @return the hosting ID, or null
     */
    public UUID hostingId() {
        return hostingId;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }

    @Override
    public String path() {
        return "hostings/plans";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("hostingId", hostingId);
    }
}
