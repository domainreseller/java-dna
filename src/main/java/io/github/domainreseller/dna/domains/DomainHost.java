package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.internal.Values;
import java.util.List;

/**
 * A child nameserver (glue record) of a domain, such as ns1.example.com.
 *
 * @param name        the host name
 * @param ipAddresses its IP addresses; never null
 */
public record DomainHost(String name, List<HostIpAddress> ipAddresses) {
    /**
     * Creates a host.
     *
     * @param name        the host name
     * @param ipAddresses its IP addresses; null becomes an empty list
     */
    public DomainHost {
        ipAddresses = Values.list(ipAddresses);
    }
}
