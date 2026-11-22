package io.github.domainreseller.dna.contacts;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

/**
 * The verification state of one contact field (an e-mail address or a phone number).
 *
 * @param ownerName            the contact's name
 * @param fieldValue           the verified value, for example the e-mail address
 * @param fieldType            what is verified, for example {@code Email}
 * @param tokenExpireDate      when the verification link expires
 * @param resendDate           when the notice was last sent again
 * @param resendRetryCount     how often the notice was sent again
 * @param lastAttemptDate      when the contact last tried to verify
 * @param attemptNumber        how often the contact tried to verify
 * @param operationStatus      the state of the verification process
 * @param createdDate          when the verification was started
 * @param userVerificationDate when the contact verified, or null
 * @param isUserVerified       whether the contact has verified
 */
public record ContactVerification(
        String ownerName,
        String fieldValue,
        String fieldType,
        OffsetDateTime tokenExpireDate,
        OffsetDateTime resendDate,
        int resendRetryCount,
        OffsetDateTime lastAttemptDate,
        int attemptNumber,
        String operationStatus,
        OffsetDateTime createdDate,
        OffsetDateTime userVerificationDate,
        @JsonProperty("isUserVerified") boolean isUserVerified) {
}
