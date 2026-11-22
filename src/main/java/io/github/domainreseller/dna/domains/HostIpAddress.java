package io.github.domainreseller.dna.domains;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * An IP address of a child nameserver.
 *
 * @param ipAddress the address, for example {@code 192.0.2.10} or {@code 2001:db8::10}
 * @param ipVersion the IP version
 */
public record HostIpAddress(String ipAddress, IpVersion ipVersion) {
    private static final Pattern IPV4 =
            Pattern.compile("^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$");
    private static final Pattern IPV6_CHARACTERS = Pattern.compile("^[0-9A-Fa-f:.]+$");

    /**
     * Reads an address and detects its IP version. No DNS lookup is made.
     *
     * @param ipAddress the address, for example {@code 192.0.2.10} or {@code 2001:db8::10}
     * @return the address with its version
     * @throws IllegalArgumentException when the text is not an IP address
     */
    public static HostIpAddress parse(String ipAddress) {
        String text = ipAddress == null ? "" : ipAddress.trim();
        if (IPV4.matcher(text).matches()) {
            return new HostIpAddress(text, IpVersion.V4);
        }
        if (text.contains(":") && IPV6_CHARACTERS.matcher(text).matches()) {
            try {
                // A text with ':' is parsed as an IPv6 literal (also ::ffff:192.0.2.1); it is never looked up.
                InetAddress.getByName(text);
                return new HostIpAddress(text.toLowerCase(Locale.ROOT), IpVersion.V6);
            } catch (UnknownHostException e) {
                // not an address
            }
        }
        throw new IllegalArgumentException("'" + ipAddress + "' is not an IP address.");
    }
}
