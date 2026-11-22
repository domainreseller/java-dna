package io.github.domainreseller.dna.dnssec;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.ListResult;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.internal.Values;
import java.util.Collection;
import java.util.List;

/** {@code POST domains/dns/dnssec}: publishes DS records at the registry, which turns DNSSEC on. */
public final class AddDsRecordsRequest extends BaseRequest<ListResult<DnssecOperationResult>> {
    private final String domainName;
    private final List<DsRecordData> records;

    /**
     * Creates the request.
     *
     * @param domainName the domain
     * @param records    the DS records
     */
    public AddDsRecordsRequest(String domainName, Collection<DsRecordData> records) {
        this.domainName = domainName;
        this.records = Values.list(records);
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
     * Returns the DS records.
     *
     * @return the records
     */
    public List<DsRecordData> records() {
        return records;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "domains/dns/dnssec";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.WRITE;
    }

    @Override
    protected Object body() {
        return JsonBody.create().add("domainName", domainName).add("records", records);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        require(records, "records");
        for (DsRecordData record : records) {
            if (record == null || record.digest() == null || record.digest().isBlank()) {
                throw new IllegalArgumentException("Every DS record needs a digest.");
            }
        }
    }
}
