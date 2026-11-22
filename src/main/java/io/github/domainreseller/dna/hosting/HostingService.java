package io.github.domainreseller.dna.hosting;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * A hosting service in your account.
 *
 * @param hostingId          the hosting ID
 * @param serviceId          the service ID at the hosting platform
 * @param domainName         the domain
 * @param packageName        the package name
 * @param leaseOn            when the lease started
 * @param expiresOn          when the lease ends
 * @param status             the state
 * @param externalCustomerId the customer ID at the hosting platform
 */
public record HostingService(
        UUID hostingId,
        String serviceId,
        String domainName,
        String packageName,
        OffsetDateTime leaseOn,
        @JsonProperty("expiredOn") OffsetDateTime expiresOn,
        String status,
        String externalCustomerId) {
}
