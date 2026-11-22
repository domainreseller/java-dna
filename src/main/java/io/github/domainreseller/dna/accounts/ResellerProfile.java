package io.github.domainreseller.dna.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

/**
 * Your reseller profile: who the account is, its currency and its state.
 *
 * @param id                 your reseller ID
 * @param resellerAutoId     the numeric ID of your reseller account
 * @param name               your reseller name
 * @param companyName        your company name
 * @param resellerGroupName  the price group your account belongs to
 * @param accountCurrency    the currency your account is billed in
 * @param status             the state of the account
 * @param isSubReseller      whether the account is a sub-reseller of another reseller
 * @param isBalanceSuspended whether the balance is suspended; a suspended balance cannot be charged
 * @param isMcpEnabled       whether MCP access is enabled for the account
 */
public record ResellerProfile(
        UUID id,
        int resellerAutoId,
        String name,
        String companyName,
        String resellerGroupName,
        AccountCurrency accountCurrency,
        ResellerStatus status,
        @JsonProperty("isSubReseller") boolean isSubReseller,
        @JsonProperty("isBalanceSuspended") boolean isBalanceSuspended,
        @JsonProperty("isMcpEnabled") boolean isMcpEnabled) {
}
