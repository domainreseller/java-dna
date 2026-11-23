package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.contacts.ContactHandle;
import io.github.domainreseller.dna.internal.Values;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * {@code POST domains/register}: registers a domain using existing contact handles. <b>Charges your balance.</b>
 * Create handles with {@code client.contacts().create(...)}.
 */
public final class RegisterDomainWithHandlesRequest extends BaseRequest<DomainOperationResult> {
    private final String domainName;
    private int period = 1;
    private List<String> nameServers = List.of();
    private List<ContactHandle> handles = List.of();
    private Map<String, String> tldAttributes;

    /**
     * Creates the request.
     *
     * @param domainName the domain to register
     */
    public RegisterDomainWithHandlesRequest(String domainName) {
        this.domainName = domainName;
    }

    /**
     * Returns the domain to register.
     *
     * @return the domain name
     */
    public String domainName() {
        return domainName;
    }

    /**
     * Returns the registration period.
     *
     * @return the period in years
     */
    public int period() {
        return period;
    }

    /**
     * Sets the registration period. 1 year by default.
     *
     * @param period the period in years
     * @return this request
     */
    public RegisterDomainWithHandlesRequest period(int period) {
        this.period = period;
        return this;
    }

    /**
     * Returns the nameservers.
     *
     * @return the nameservers
     */
    public List<String> nameServers() {
        return nameServers;
    }

    /**
     * Sets the nameservers. Most registries need at least two.
     *
     * @param nameServers the nameservers
     * @return this request
     */
    public RegisterDomainWithHandlesRequest nameServers(String... nameServers) {
        return nameServers(Arrays.asList(nameServers));
    }

    /**
     * Sets the nameservers. Most registries need at least two.
     *
     * @param nameServers the nameservers
     * @return this request
     */
    public RegisterDomainWithHandlesRequest nameServers(Collection<String> nameServers) {
        this.nameServers = Values.list(nameServers);
        return this;
    }

    /**
     * Returns the contact handles.
     *
     * @return the handles, one per role
     */
    public List<ContactHandle> handles() {
        return handles;
    }

    /**
     * Sets the contact handles, one per role. {@link ContactHandle#forAllRoles} uses one handle for all four.
     *
     * @param handles the handles
     * @return this request
     */
    public RegisterDomainWithHandlesRequest handles(Collection<ContactHandle> handles) {
        this.handles = Values.list(handles);
        return this;
    }

    /**
     * Returns the registry-specific attributes.
     *
     * @return the attributes, or null
     */
    public Map<String, String> tldAttributes() {
        return tldAttributes;
    }

    /**
     * Sets registry-specific attributes, such as the ones .tr domains need.
     *
     * @param tldAttributes the attributes, or null
     * @return this request
     */
    public RegisterDomainWithHandlesRequest tldAttributes(Map<String, String> tldAttributes) {
        this.tldAttributes = tldAttributes == null ? null : Values.map(tldAttributes);
        return this;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "domains/register";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.BILLABLE;
    }

    @Override
    protected Object body() {
        return JsonBody.create()
                .add("domainName", domainName)
                .add("period", period)
                .add("nameServers", nameServers)
                .add("handles", handles)
                .add("tldAttributes", tldAttributes);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        if (period < 1) {
            throw new IllegalArgumentException("period must be at least 1 year.");
        }
        require(nameServers, "nameServers");
        require(handles, "handles");
        for (ContactHandle handle : handles) {
            if (handle == null || handle.handle() == null || handle.handle().isBlank()) {
                throw new IllegalArgumentException("Every entry in handles needs a handle.");
            }
        }
    }
}
