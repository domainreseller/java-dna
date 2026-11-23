package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.DomainNameApiClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Child nameservers (glue records) such as ns1.example.com for example.com. Reach it through
 * {@link DomainsClient#childNameServers()}. The current ones are listed in {@link DomainDetails#hosts()}.
 */
public final class ChildNameServersClient {
    private final DomainNameApiClient api;

    /**
     * Creates the client. Use {@code client.domains().childNameServers()} instead.
     *
     * @param api the client that sends the requests
     */
    public ChildNameServersClient(DomainNameApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    /**
     * Creates a child nameserver. The IP version of each address is detected.
     *
     * @param domainName  the parent domain, for example {@code example.com}
     * @param hostName    the nameserver, for example {@code ns1.example.com}
     * @param ipAddresses its IP addresses, for example {@code 192.0.2.10} and {@code 2001:db8::10}
     * @throws IllegalArgumentException when an address is not a valid IP address; nothing is sent
     */
    public void add(String domainName, String hostName, String... ipAddresses) {
        add(domainName, hostName, Arrays.asList(ipAddresses));
    }

    /**
     * Creates a child nameserver. The IP version of each address is detected.
     *
     * @param domainName  the parent domain
     * @param hostName    the nameserver
     * @param ipAddresses its IP addresses
     * @throws IllegalArgumentException when an address is not a valid IP address; nothing is sent
     */
    public void add(String domainName, String hostName, Collection<String> ipAddresses) {
        api.send(new AddChildNameServerRequest(domainName, hostName, parse(ipAddresses)));
    }

    /**
     * Replaces the IP addresses of a child nameserver.
     *
     * @param domainName  the parent domain
     * @param hostName    the nameserver
     * @param ipAddresses the new IP addresses
     */
    public void update(String domainName, String hostName, Collection<String> ipAddresses) {
        update(domainName, hostName, ipAddresses, null);
    }

    /**
     * Replaces the IP addresses of a child nameserver and renames it.
     *
     * @param domainName  the parent domain
     * @param hostName    the current nameserver host name
     * @param ipAddresses the new IP addresses
     * @param newHostName the new host name, or null to keep it
     */
    public void update(String domainName, String hostName, Collection<String> ipAddresses, String newHostName) {
        api.send(new UpdateChildNameServerRequest(domainName, hostName, parse(ipAddresses)).newHostName(newHostName));
    }

    /**
     * Deletes a child nameserver.
     *
     * @param domainName the parent domain
     * @param hostName   the nameserver
     */
    public void delete(String domainName, String hostName) {
        api.send(new DeleteChildNameServerRequest(domainName, hostName));
    }

    private static List<HostIpAddress> parse(Collection<String> ipAddresses) {
        List<HostIpAddress> parsed = new ArrayList<>();
        if (ipAddresses != null) {
            for (String address : ipAddresses) {
                parsed.add(HostIpAddress.parse(address));
            }
        }
        return parsed;
    }
}
