package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.internal.Values;
import java.util.Collection;
import java.util.List;

/** {@code PUT domains/dns/host}: changes the IP addresses of a child nameserver, and optionally its name. */
public final class UpdateChildNameServerRequest extends BaseRequest<Void> {
    private final String domainName;
    private final String hostName;
    private final List<HostIpAddress> ipAddresses;
    private String newHostName;

    /**
     * Creates the request.
     *
     * @param domainName  the parent domain
     * @param hostName    the current nameserver host name
     * @param ipAddresses the new IP addresses
     */
    public UpdateChildNameServerRequest(String domainName, String hostName, Collection<HostIpAddress> ipAddresses) {
        this.domainName = domainName;
        this.hostName = hostName;
        this.ipAddresses = Values.list(ipAddresses);
    }

    /**
     * Returns the parent domain.
     *
     * @return the domain name
     */
    public String domainName() {
        return domainName;
    }

    /**
     * Returns the current host name.
     *
     * @return the host name
     */
    public String hostName() {
        return hostName;
    }

    /**
     * Returns the new IP addresses.
     *
     * @return the addresses
     */
    public List<HostIpAddress> ipAddresses() {
        return ipAddresses;
    }

    /**
     * Returns the new host name.
     *
     * @return the new name, or null to keep the current one
     */
    public String newHostName() {
        return newHostName;
    }

    /**
     * Renames the nameserver.
     *
     * @param newHostName the new name, or null to keep the current one
     * @return this request
     */
    public UpdateChildNameServerRequest newHostName(String newHostName) {
        this.newHostName = newHostName;
        return this;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.PUT;
    }

    @Override
    public String path() {
        return "domains/dns/host";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.WRITE;
    }

    @Override
    protected Object body() {
        return JsonBody.create()
                .add("domainName", domainName)
                .add("hostName", hostName)
                .add("newHostName", newHostName)
                .add("ipAddresses", ipAddresses);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        require(hostName, "hostName");
        require(ipAddresses, "ipAddresses");
    }
}
