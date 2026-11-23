package io.github.domainreseller.dna.hosting;

/**
 * The provisioning state of a hosting, and the panel access once it is ready.
 *
 * @param serviceId     the service ID
 * @param domainName    the domain
 * @param status        the state
 * @param statusMessage a description of the state
 * @param panelAddress  the control panel address
 * @param username      the control panel user name
 * @param password      the control panel password
 * @param token         the lookup token
 * @param failureReason why provisioning failed, if it did
 */
public record HostingStatus(
        String serviceId,
        String domainName,
        String status,
        String statusMessage,
        String panelAddress,
        String username,
        String password,
        String token,
        String failureReason) {
    /**
     * Returns the state without the password, so it does not end up in logs.
     *
     * @return a short description
     */
    @Override
    public String toString() {
        return "HostingStatus[serviceId=" + serviceId + ", domainName=" + domainName + ", status=" + status
                + ", statusMessage=" + statusMessage + (password == null ? "" : ", password=***") + "]";
    }
}
