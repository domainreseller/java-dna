package io.github.domainreseller.dna.dns;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * One value of a DNS record set.
 *
 * @param content    the value, for example {@code 192.0.2.10}
 * @param isDisabled whether the value is switched off
 */
public record DnsRecord(String content, @JsonProperty("disabled") boolean isDisabled) {
}
