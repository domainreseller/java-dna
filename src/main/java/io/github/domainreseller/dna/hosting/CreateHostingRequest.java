package io.github.domainreseller.dna.hosting;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;

/** {@code POST hostings/create}: orders a hosting for a domain. <b>Charges your balance.</b> */
public final class CreateHostingRequest extends BaseRequest<HostingCreateResult> {
    private final String domainName;
    private final String servicePlan;

    /**
     * Creates the request.
     *
     * @param domainName  the domain
     * @param servicePlan the service plan, from {@link HostingClient#listPackages()}
     */
    public CreateHostingRequest(String domainName, String servicePlan) {
        this.domainName = domainName;
        this.servicePlan = servicePlan;
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
     * Returns the service plan.
     *
     * @return the plan name
     */
    public String servicePlan() {
        return servicePlan;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "hostings/create";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.BILLABLE;
    }

    @Override
    protected Object body() {
        return JsonBody.create().add("domainName", domainName).add("servicePlan", servicePlan);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        require(servicePlan, "servicePlan");
    }
}
