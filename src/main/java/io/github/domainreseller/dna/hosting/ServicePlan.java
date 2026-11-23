package io.github.domainreseller.dna.hosting;

/**
 * A hosting service plan and its limits. Null limits are unlimited or not reported.
 *
 * @param name             the plan name
 * @param externalId       the plan's ID at the hosting platform
 * @param diskQuotaMb      disk quota in MB
 * @param bandwidthLimitMb monthly bandwidth in MB
 * @param maxDomains       domains
 * @param maxSubDomains    subdomains
 * @param maxDatabases     databases
 * @param maxFtpAccounts   FTP accounts
 * @param maxEmailAccounts e-mail accounts
 * @param hasDedicatedIp   whether the plan has a dedicated IP address
 * @param hasShellAccess   whether the plan has shell access
 */
public record ServicePlan(
        String name,
        String externalId,
        Long diskQuotaMb,
        Long bandwidthLimitMb,
        Integer maxDomains,
        Integer maxSubDomains,
        Integer maxDatabases,
        Integer maxFtpAccounts,
        Integer maxEmailAccounts,
        boolean hasDedicatedIp,
        boolean hasShellAccess) {
}
