package io.github.domainreseller.dna.dns;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;

/** {@code DELETE domains/zones}: removes one value from a record set. */
public final class DeleteDnsRecordRequest extends BaseRequest<Void> {
    private final String domainName;
    private final String name;
    private final RecordType type;
    private final String value;

    /**
     * Creates the request.
     *
     * @param domainName the domain
     * @param name       the record name, for example {@code www} or {@code @}
     * @param type       the record type
     * @param value      the value to remove
     */
    public DeleteDnsRecordRequest(String domainName, String name, RecordType type, String value) {
        this.domainName = domainName;
        this.name = name;
        this.type = type;
        this.value = value;
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
     * Returns the value to remove.
     *
     * @return the value
     */
    public String value() {
        return value;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.DELETE;
    }

    @Override
    public String path() {
        return "domains/zones";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.DESTRUCTIVE;
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("domainName", domainName)
                .add("Name", name)
                .add("Record", value)
                .add("RecordType", type == null ? null : type.value());
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        require(name, "name");
        require(type, "type");
        require(value, "value");
    }
}
