import io.github.domainreseller.dna.DomainNameApiClient;
import io.github.domainreseller.dna.dns.DnsRecordSet;
import io.github.domainreseller.dna.dns.RecordType;
import java.util.List;

/** Adds, replaces and deletes DNS records, then switches the domain to the platform nameservers. */
public final class DnsExamples {
    private DnsExamples() {
    }

    /**
     * Runs the example.
     *
     * @param args the domain
     */
    public static void main(String[] args) {
        DomainNameApiClient client = DomainNameApiClient.fromEnvironment();
        String domain = args.length > 0 ? args[0] : "example.com";

        client.dns().add(domain, "www", RecordType.A, "192.0.2.10");
        client.dns().add(domain, "@", RecordType.of("txt"), "v=spf1 -all");
        client.dns().add(domain, "@", RecordType.MX, List.of("10 mx1.example.net", "20 mx2.example.net"), 3600);
        client.dns().replace(domain, "www", RecordType.A, List.of("192.0.2.20"), 300);
        client.dns().delete(domain, "www", RecordType.A, "192.0.2.20");

        for (DnsRecordSet set : client.dns().list(domain)) {
            System.out.println(set.name() + " " + set.type() + " " + set.ttl() + " " + set.values());
        }

        System.out.println("now served by " + client.dns().apply(domain).systemNameServers());
    }
}
