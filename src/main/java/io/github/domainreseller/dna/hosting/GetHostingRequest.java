package io.github.domainreseller.dna.hosting;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;

/** {@code GET hostings}: a hosting, looked up by domain name or service ID. Create it with {@link #byDomain(String)} or {@link #byServiceId(String)}. */
public final class GetHostingRequest extends BaseRequest<HostingService> {
    private final String domainName;
    private final String serviceId;

    private GetHostingRequest(String domainName, String serviceId) {
        this.domainName = domainName;
        this.serviceId = serviceId;
    }

    /**
     * Looks the hosting up by domain name.
     *
     * @param domainName the domain
     * @return the request
     */
    public static GetHostingRequest byDomain(String domainName) {
        return new GetHostingRequest(domainName, null);
    }

    /**
     * Looks the hosting up by service ID.
     *
     * @param serviceId the service ID
     * @return the request
     */
    public static GetHostingRequest byServiceId(String serviceId) {
        return new GetHostingRequest(null, serviceId);
    }

    /**
     * Returns the domain.
     *
     * @return the domain name, or null when looked up by service ID
     */
    public String domainName() {
        return domainName;
    }

    /**
     * Returns the service ID.
     *
     * @return the service ID, or null when looked up by domain
     */
    public String serviceId() {
        return serviceId;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }

    @Override
    public String path() {
        return "hostings";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("serviceId", serviceId).add("domainName", domainName);
    }

    @Override
    protected void validate() {
        if ((domainName == null || domainName.isBlank()) && (serviceId == null || serviceId.isBlank())) {
            throw new IllegalArgumentException("Give a domainName or a serviceId.");
        }
    }
}
