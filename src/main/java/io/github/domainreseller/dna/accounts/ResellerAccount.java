package io.github.domainreseller.dna.accounts;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Your reseller account and its balances.
 *
 * @param resellerId        your reseller ID
 * @param resellerName      your reseller name
 * @param resellerGroupName the price group your account belongs to
 * @param tryBalance        balance in Turkish lira
 * @param usdBalance        balance in US dollars
 */
public record ResellerAccount(
        UUID resellerId,
        String resellerName,
        String resellerGroupName,
        BigDecimal tryBalance,
        BigDecimal usdBalance) {
}
