package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.DomainNameApiClient;
import java.util.Objects;

/** URL forwarding of domains. Reach it through {@link DomainsClient#forwarding()}. */
public final class DomainForwardingClient {
    private final DomainNameApiClient api;

    /**
     * Creates the client. Use {@code client.domains().forwarding()} instead.
     *
     * @param api the client that sends the requests
     */
    public DomainForwardingClient(DomainNameApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    /**
     * Returns the URL forwarding of a domain.
     *
     * @param domainName the domain
     * @return the forwarding
     * @throws io.github.domainreseller.dna.DomainNameApiException with kind {@code NOT_FOUND} when no forwarding is
     *                                                             set up
     */
    public DomainForwarding get(String domainName) {
        return api.send(new GetForwardingRequest(domainName));
    }

    /**
     * Forwards a domain to a URL with a standard (HTTP 301) redirect.
     *
     * @param domainName      the domain
     * @param redirectAddress the target URL, for example {@code https://www.example.net}
     */
    public void set(String domainName, String redirectAddress) {
        set(domainName, redirectAddress, ForwardType.STANDARD);
    }

    /**
     * Forwards a domain to a URL.
     *
     * @param domainName      the domain
     * @param redirectAddress the target URL
     * @param forwardType     {@link ForwardType#STANDARD} (HTTP 301) or {@link ForwardType#FRAME} (masked)
     */
    public void set(String domainName, String redirectAddress, ForwardType forwardType) {
        api.send(new SetForwardingRequest(domainName, redirectAddress, forwardType));
    }

    /**
     * Removes the URL forwarding of a domain.
     *
     * @param domainName the domain
     */
    public void delete(String domainName) {
        api.send(new DeleteForwardingRequest(domainName));
    }
}
