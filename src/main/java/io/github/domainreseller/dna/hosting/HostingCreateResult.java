package io.github.domainreseller.dna.hosting;

/**
 * The result of ordering a hosting. Provisioning continues in the background; poll its status.
 *
 * @param serviceId    the service ID
 * @param status       the state
 * @param panelAddress the control panel address, once ready
 * @param username     the control panel user name, once ready
 * @param password     the control panel password, once ready
 * @param token        a token to look the hosting up with {@link HostingClient#getByToken(String)}
 */
public record HostingCreateResult(
        String serviceId,
        String status,
        String panelAddress,
        String username,
        String password,
        String token) {
    /**
     * Returns the result without the password, so it does not end up in logs.
     *
     * @return a short description
     */
    @Override
    public String toString() {
        return "HostingCreateResult[serviceId=" + serviceId + ", status=" + status + ", panelAddress=" + panelAddress
                + ", username=" + username + (password == null ? "" : ", password=***") + "]";
    }
}
