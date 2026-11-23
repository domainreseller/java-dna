package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.internal.Values;
import java.util.Collection;
import java.util.List;

/** {@code POST domains/dns/host}: creates a child nameserver (glue record) such as ns1.example.com. */
public final class AddChildNameServerRequest extends BaseRequest<Void> {
    private final String domainName;
    private final String hostName;
    private final List<HostIpAddress> ipAddresses;

    /**
     * Creates the request.
     *
     * @param domainName  the parent domain, for example {@code example.com}
     * @param hostName    the nameserver, for example {@code ns1.example.com}
     * @param ipAddresses its IP addresses; {@link HostIpAddress#parse} detects the IP version
     */
    public AddChildNameServerRequest(String domainName, String hostName, Collection<HostIpAddress> ipAddresses) {
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
     * Returns the nameserver host name.
     *
     * @return the host name
     */
    public String hostName() {
        return hostName;
    }

    /**
     * Returns the IP addresses.
     *
     * @return the addresses
     */
    public List<HostIpAddress> ipAddresses() {
        return ipAddresses;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
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
        return JsonBody.create().add("domainName", domainName).add("hostName", hostName).add("ipAddresses", ipAddresses);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        require(hostName, "hostName");
        require(ipAddresses, "ipAddresses");
    }
}
