package io.github.domainreseller.dna.domains;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * The result of a registration, renewal or transfer. An HTTP 200 answer can still report
 * {@code success == false} with a {@link #message()} (for example when the registry refused the domain): check
 * {@link #success()} before treating the operation as done.
 *
 * @param success        whether the registry accepted the operation
 * @param message        the API's message
 * @param domainId       the domain's ID
 * @param status         the domain's state after the operation
 * @param expirationDate the expiration date after the operation
 */
public record DomainOperationResult(
        boolean success,
        String message,
        UUID domainId,
        String status,
        OffsetDateTime expirationDate) {
}
