package io.github.domainreseller.dna.dns;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;
import io.github.domainreseller.dna.internal.Values;
import java.util.Collection;
import java.util.List;

/**
 * {@code POST domains/zones}: adds values to a record set and creates the set if needed. Existing values of the set
 * are kept. Records resolve publicly once the domain uses the platform nameservers (see {@link ApplyDnsZoneRequest}).
 */
public final class AddDnsRecordRequest extends BaseRequest<Void> {
    private final String domainName;
    private final String name;
    private final RecordType type;
    private final List<String> contents;
    private int ttl = 3600;

    /**
     * Creates the request with a TTL of 3600 seconds.
     *
     * @param domainName the domain
     * @param name       the record name: {@code @} for the apex or a relative name such as {@code www}
     * @param type       the record type
     * @param contents   the values to add
     */
    public AddDnsRecordRequest(String domainName, String name, RecordType type, Collection<String> contents) {
        this.domainName = domainName;
        this.name = name;
        this.type = type;
        this.contents = Values.list(contents);
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
     * Returns the record name.
     *
     * @return the name
     */
    public String name() {
        return name;
    }

    /**
     * Returns the record type.
     *
     * @return the type
     */
    public RecordType type() {
        return type;
    }

    /**
     * Returns the values to add.
     *
     * @return the values
     */
    public List<String> contents() {
        return contents;
    }

    /**
     * Returns the time to live.
     *
     * @return the TTL in seconds
     */
    public int ttl() {
        return ttl;
    }

    /**
     * Sets the time to live. 3600 seconds by default; the API accepts 1 to 86400.
     *
     * @param ttl the TTL in seconds
     * @return this request
     */
    public AddDnsRecordRequest ttl(int ttl) {
        this.ttl = ttl;
        return this;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "domains/zones";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.WRITE;
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("domainName", domainName);
    }

    @Override
    protected Object body() {
        return DnsValidation.zoneBody(name, type, ttl, contents);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        require(type, "type");
        require(contents, "contents");
        DnsValidation.check(name, type, ttl, contents);
    }
}
