package io.github.domainreseller.dna.dns;

import io.github.domainreseller.dna.DomainNameApiClient;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * DNS zone records of domains. Records resolve publicly once the domain uses the platform nameservers;
 * {@link #apply(String)} switches it to them. Zone calls are kept 1 second apart (the gateway's limit). Reach it
 * through {@link DomainNameApiClient#dns()}.
 *
 * <pre>{@code
 * client.dns().add("example.com", "www", RecordType.A, "192.0.2.10");
 * client.dns().add("example.com", "@", RecordType.of("txt"), "v=spf1 -all");
 * client.dns().apply("example.com");
 * }</pre>
 */
public final class DnsClient {
    private final DomainNameApiClient api;

    /**
     * Creates the client. Use {@link DomainNameApiClient#dns()} instead.
     *
     * @param api the client that sends the requests
     */
    public DnsClient(DomainNameApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    /**
     * Returns the record sets of a domain.
     *
     * @param domainName the domain
     * @return the record sets
     */
    public List<DnsRecordSet> list(String domainName) {
        return api.send(new ListDnsRecordsRequest(domainName));
    }

    /**
     * Adds a value to a record set with a TTL of 3600 seconds, creating the set if needed. Existing values are kept.
     *
     * @param domainName the domain
     * @param name       the record name: {@code @} for the apex or a relative name such as {@code www}
     * @param type       the record type
     * @param value      the value, for example {@code 192.0.2.10}
     */
    public void add(String domainName, String name, RecordType type, String value) {
        add(domainName, name, type, Collections.singletonList(value), 3600);
    }

    /**
     * Adds a value to a record set, creating the set if needed. Existing values are kept.
     *
     * @param domainName the domain
     * @param name       the record name
     * @param type       the record type
     * @param value      the value
     * @param ttl        time to live in seconds (1 to 86400)
     */
    public void add(String domainName, String name, RecordType type, String value, int ttl) {
        add(domainName, name, type, Collections.singletonList(value), ttl);
    }

    /**
     * Adds values to a record set, creating the set if needed. Existing values are kept.
     *
     * @param domainName the domain
     * @param name       the record name
     * @param type       the record type
     * @param values     the values
     * @param ttl        time to live in seconds (1 to 86400)
     */
    public void add(String domainName, String name, RecordType type, Collection<String> values, int ttl) {
        api.send(new AddDnsRecordRequest(domainName, name, type, values).ttl(ttl));
    }

    /**
     * Replaces every value of a record set and its TTL.
     *
     * @param domainName the domain
     * @param name       the record name
     * @param type       the record type
     * @param values     the new values
     * @param ttl        time to live in seconds (1 to 86400)
     */
    public void replace(String domainName, String name, RecordType type, Collection<String> values, int ttl) {
        replace(domainName, name, type, values, ttl, null);
    }

    /**
     * Replaces every value of a record set and its TTL, and renames it.
     *
     * @param domainName the domain
     * @param name       the current record name
     * @param type       the record type
     * @param values     the new values
     * @param ttl        time to live in seconds (1 to 86400)
     * @param newName    the new record name, or null to keep it
     */
    public void replace(String domainName, String name, RecordType type, Collection<String> values, int ttl, String newName) {
        api.send(new ReplaceDnsRecordSetRequest(domainName, name, type, values).ttl(ttl).newName(newName));
    }

    /**
     * Removes one value from a record set.
     *
     * @param domainName the domain
     * @param name       the record name
     * @param type       the record type
     * @param value      the value to remove
     */
    public void delete(String domainName, String name, RecordType type, String value) {
        api.send(new DeleteDnsRecordRequest(domainName, name, type, value));
    }

    /**
     * Switches the domain to the platform nameservers so its zone is served.
     *
     * @param domainName the domain
     * @return the nameservers before and after
     */
    public ZoneApplyResult apply(String domainName) {
        return api.send(new ApplyDnsZoneRequest(domainName));
    }
}
