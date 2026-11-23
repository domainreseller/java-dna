package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.internal.Values;
import java.util.Collection;
import java.util.List;

/**
 * {@code PUT domains/dns/name-server}: replaces the nameservers of a domain. On a DNSSEC-signed domain the change is
 * refused unless {@link #dnssecMode(DnssecMode)} says what to do with the DS records. Replacing the list twice is
 * harmless, so the request is retried after network errors.
 */
public final class SetNameServersRequest extends BaseRequest<Void> {
    private final String domainName;
    private final List<String> nameServers;
    private DnssecMode dnssecMode;

    /**
     * Creates the request.
     *
     * @param domainName  the domain
     * @param nameServers the complete list of nameservers
     */
    public SetNameServersRequest(String domainName, Collection<String> nameServers) {
        this.domainName = domainName;
        this.nameServers = Values.list(nameServers);
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
     * Returns the nameservers.
     *
     * @return the complete list of nameservers
     */
    public List<String> nameServers() {
        return nameServers;
    }

    /**
     * Returns what to do with DNSSEC on a signed domain.
     *
     * @return the mode, or null for the API default ({@link DnssecMode#BLOCK})
     */
    public DnssecMode dnssecMode() {
        return dnssecMode;
    }

    /**
     * Sets what to do with DNSSEC on a signed domain.
     *
     * @param dnssecMode the mode, or null for the API default ({@link DnssecMode#BLOCK})
     * @return this request
     */
    public SetNameServersRequest dnssecMode(DnssecMode dnssecMode) {
        this.dnssecMode = dnssecMode;
        return this;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.PUT;
    }

    @Override
    public String path() {
        return "domains/dns/name-server";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.WRITE;
    }

    @Override
    public boolean isIdempotent() {
        return true;
    }

    @Override
    protected Object body() {
        return JsonBody.create()
                .add("domainName", domainName)
                .add("nameServers", nameServers)
                .add("dnssecMode", dnssecMode);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        require(nameServers, "nameServers");
    }
}
