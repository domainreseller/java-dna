package io.github.domainreseller.dna.hosting;

import io.github.domainreseller.dna.DomainNameApiClient;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Web hosting packages and services. Reach it through {@link DomainNameApiClient#hosting()}. */
public final class HostingClient {
    private final DomainNameApiClient api;

    /**
     * Creates the client. Use {@link DomainNameApiClient#hosting()} instead.
     *
     * @param api the client that sends the requests
     */
    public HostingClient(DomainNameApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    /**
     * Returns the hosting packages and their service plans.
     *
     * @return the packages
     */
    public List<HostingPackage> listPackages() {
        return api.send(new ListHostingPackagesRequest());
    }

    /**
     * Returns all service plans and their limits.
     *
     * @return the plans
     */
    public List<ServicePlan> listPlans() {
        return listPlans(null);
    }

    /**
     * Returns the service plans available to a hosting, and their limits.
     *
     * @param hostingId the hosting, or null for all plans
     * @return the plans
     */
    public List<ServicePlan> listPlans(UUID hostingId) {
        return api.send(new ListHostingPlansRequest(hostingId));
    }

    /**
     * Orders a hosting for a domain. <b>Charges your balance.</b> Poll {@link #getStatus(String)} until it is ready.
     *
     * @param domainName  the domain
     * @param servicePlan the service plan
     * @return the order, with a lookup token
     */
    public HostingCreateResult create(String domainName, String servicePlan) {
        return api.send(new CreateHostingRequest(domainName, servicePlan));
    }

    /**
     * Returns the hosting of a domain.
     *
     * @param domainName the domain
     * @return the hosting
     */
    public HostingService get(String domainName) {
        return api.send(GetHostingRequest.byDomain(domainName));
    }

    /**
     * Returns a hosting by its service ID.
     *
     * @param serviceId the service ID
     * @return the hosting
     */
    public HostingService getByServiceId(String serviceId) {
        return api.send(GetHostingRequest.byServiceId(serviceId));
    }

    /**
     * Returns a hosting by the token returned when it was ordered.
     *
     * @param token the token
     * @return the hosting
     */
    public HostingService getByToken(String token) {
        return api.send(new GetHostingByTokenRequest(token));
    }

    /**
     * Returns the provisioning state of a domain's hosting, and the panel access once ready.
     *
     * @param domainName the domain
     * @return the state
     */
    public HostingStatus getStatus(String domainName) {
        return api.send(GetHostingStatusRequest.byDomain(domainName));
    }

    /**
     * Returns the provisioning state of a hosting by its service ID.
     *
     * @param serviceId the service ID
     * @return the state
     */
    public HostingStatus getStatusByServiceId(String serviceId) {
        return api.send(GetHostingStatusRequest.byServiceId(serviceId));
    }
}
