package io.github.domainreseller.dna.dns;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;
import io.github.domainreseller.dna.internal.Values;
import java.util.Collection;
import java.util.List;

/**
 * {@code PUT domains/zones}: replaces every value of a record set (and its TTL). Set {@link #newName(String)} to
 * rename it. Use {@link AddDnsRecordRequest} to append a value instead. Replacing twice is harmless, so the request
 * is retried after network errors.
 */
public final class ReplaceDnsRecordSetRequest extends BaseRequest<Void> {
    private final String domainName;
    private final String recordName;
    private final RecordType type;
    private final List<String> contents;
    private int ttl = 3600;
    private String newName;

    /**
     * Creates the request with a TTL of 3600 seconds.
     *
     * @param domainName the domain
     * @param recordName the name of the existing record set
     * @param type       the record type
     * @param contents   the new values
     */
    public ReplaceDnsRecordSetRequest(String domainName, String recordName, RecordType type, Collection<String> contents) {
        this.domainName = domainName;
        this.recordName = recordName;
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
     * Returns the name of the existing record set.
     *
     * @return the name
     */
    public String recordName() {
        return recordName;
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
     * Returns the new values.
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
    public ReplaceDnsRecordSetRequest ttl(int ttl) {
        this.ttl = ttl;
        return this;
    }

    /**
     * Returns the new name of the record set.
     *
     * @return the new name, or null to keep the current one
     */
    public String newName() {
        return newName;
    }

    /**
     * Renames the record set.
     *
     * @param newName the new name, or null to keep the current one
     * @return this request
     */
    public ReplaceDnsRecordSetRequest newName(String newName) {
        this.newName = newName;
        return this;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.PUT;
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
    public boolean isIdempotent() {
        return true;
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("domainName", domainName).add("recordName", recordName);
    }

    @Override
    protected Object body() {
        return DnsValidation.zoneBody(newName != null ? newName : recordName, type, ttl, contents);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        require(recordName, "recordName");
        require(type, "type");
        require(contents, "contents");
        DnsValidation.check(newName != null ? newName : recordName, type, ttl, contents);
    }
}
