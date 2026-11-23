package io.github.domainreseller.dna.dns;

import io.github.domainreseller.dna.internal.Values;
import java.util.List;
import java.util.stream.Collectors;

/**
 * All values of one name and type in a DNS zone, for example every A record of {@code www}.
 *
 * @param name       the record name: {@code @} or empty for the apex, a relative name such as {@code www}, or a full
 *                   name, as the API returns it
 * @param type       the record type
 * @param ttl        time to live in seconds
 * @param changeType a pending change, if the zone has not been published yet
 * @param records    the values; never null
 */
public record DnsRecordSet(String name, RecordType type, int ttl, String changeType, List<DnsRecord> records) {
    /**
     * Creates a record set.
     *
     * @param name       the record name
     * @param type       the record type
     * @param ttl        time to live in seconds
     * @param changeType a pending change
     * @param records    the values; null becomes an empty list
     */
    public DnsRecordSet {
        records = Values.list(records);
    }

    /**
     * Returns the contents of the enabled records.
     *
     * @return the values
     */
    public List<String> values() {
        return records.stream()
                .filter(r -> !r.isDisabled() && r.content() != null)
                .map(DnsRecord::content)
                .collect(Collectors.toUnmodifiableList());
    }
}
