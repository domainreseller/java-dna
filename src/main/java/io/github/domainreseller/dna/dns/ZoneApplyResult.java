package io.github.domainreseller.dna.dns;

import io.github.domainreseller.dna.internal.Values;
import java.util.List;

/**
 * The result of switching a domain to the platform nameservers.
 *
 * @param nameServersChanged  whether the nameservers had to be changed
 * @param previousNameServers the nameservers before; never null
 * @param currentNameServers  the nameservers now; never null
 * @param systemNameServers   the platform nameservers that serve the zone; never null
 */
public record ZoneApplyResult(
        boolean nameServersChanged,
        List<String> previousNameServers,
        List<String> currentNameServers,
        List<String> systemNameServers) {
    /**
     * Creates the result.
     *
     * @param nameServersChanged  whether the nameservers had to be changed
     * @param previousNameServers the nameservers before; null becomes an empty list
     * @param currentNameServers  the nameservers now; null becomes an empty list
     * @param systemNameServers   the platform nameservers; null becomes an empty list
     */
    public ZoneApplyResult {
        previousNameServers = Values.list(previousNameServers);
        currentNameServers = Values.list(currentNameServers);
        systemNameServers = Values.list(systemNameServers);
    }
}
