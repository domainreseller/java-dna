package io.github.domainreseller.dna.dnssec;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;

/** {@code DELETE domains/dns/dnssec}: removes one DS record. */
public final class RemoveDsRecordRequest extends BaseRequest<DnssecOperationResult> {
    private final String domainName;
    private final DsRecordData record;

    /**
     * Creates the request.
     *
     * @param domainName the domain
     * @param record     the DS record to remove
     */
    public RemoveDsRecordRequest(String domainName, DsRecordData record) {
        this.domainName = domainName;
        this.record = record;
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
     * Returns the DS record to remove.
     *
     * @return the record
     */
    public DsRecordData record() {
        return record;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.DELETE;
    }

    @Override
    public String path() {
        return "domains/dns/dnssec";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.DESTRUCTIVE;
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("DomainName", domainName);
        if (record != null) {
            query.add("KeyTag", record.keyTag())
                    .add("Algorithm", record.algorithm())
                    .add("DigestType", record.digestType())
                    .add("Digest", record.digest());
        }
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        require(record, "record");
        require(record.digest(), "record.digest");
    }
}
