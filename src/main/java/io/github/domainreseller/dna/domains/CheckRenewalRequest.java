package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;

/**
 * {@code POST domains/renew/check}: whether a domain can be renewed, and its premium price if any. Charges nothing.
 */
public final class CheckRenewalRequest extends BaseRequest<RenewalCheck> {
    private final String domainName;
    private final int period;

    /**
     * Creates the request for one year.
     *
     * @param domainName the domain
     */
    public CheckRenewalRequest(String domainName) {
        this(domainName, 1);
    }

    /**
     * Creates the request.
     *
     * @param domainName the domain
     * @param period     the renewal period in years to check
     */
    public CheckRenewalRequest(String domainName, int period) {
        this.domainName = domainName;
        this.period = period;
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
     * Returns the period.
     *
     * @return the renewal period in years to check
     */
    public int period() {
        return period;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "domains/renew/check";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }

    @Override
    protected Object body() {
        return JsonBody.create().add("domainName", domainName).add("period", period);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        if (period < 1) {
            throw new IllegalArgumentException("period must be at least 1 year.");
        }
    }
}
