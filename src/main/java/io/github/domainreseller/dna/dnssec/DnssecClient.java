package io.github.domainreseller.dna.dnssec;

import io.github.domainreseller.dna.DomainNameApiClient;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * DNSSEC (DS records) of domains. Reach it through {@link DomainNameApiClient#dnssec()}.
 *
 * <pre>{@code
 * DsRecordData record = DsRecordData.parse("12345 13 2 49FD46E6C4B45C55D4AC69CBD3CD34AC1AFE51DE");
 * client.dnssec().add("example.com", record);
 * }</pre>
 */
public final class DnssecClient {
    private final DomainNameApiClient api;

    /**
     * Creates the client. Use {@link DomainNameApiClient#dnssec()} instead.
     *
     * @param api the client that sends the requests
     */
    public DnssecClient(DomainNameApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    /**
     * Returns the DNSSEC state, DS records and the algorithms the registry allows.
     *
     * @param domainName the domain
     * @return the DNSSEC state
     */
    public DnssecInfo get(String domainName) {
        return api.send(new GetDnssecRequest(domainName));
    }

    /**
     * Publishes a DS record, which turns DNSSEC on.
     *
     * @param domainName the domain
     * @param record     the DS record
     * @return one result per record
     */
    public List<DnssecOperationResult> add(String domainName, DsRecordData record) {
        return add(domainName, java.util.Collections.singletonList(record));
    }

    /**
     * Publishes DS records, which turns DNSSEC on.
     *
     * @param domainName the domain
     * @param records    the DS records
     * @return one result per record
     */
    public List<DnssecOperationResult> add(String domainName, Collection<DsRecordData> records) {
        return api.send(new AddDsRecordsRequest(domainName, records)).items();
    }

    /**
     * Removes one DS record.
     *
     * @param domainName the domain
     * @param record     the DS record to remove
     * @return the result
     */
    public DnssecOperationResult remove(String domainName, DsRecordData record) {
        return api.send(new RemoveDsRecordRequest(domainName, record));
    }

    /**
     * Removes every DS record, which turns DNSSEC off.
     *
     * @param domainName the domain
     * @return the result
     */
    public DnssecOperationResult disable(String domainName) {
        return api.send(new DisableDnssecRequest(domainName));
    }
}
