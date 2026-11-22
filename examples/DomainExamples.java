import io.github.domainreseller.dna.DomainNameApiClient;
import io.github.domainreseller.dna.contacts.ContactInfo;
import io.github.domainreseller.dna.contacts.ContactType;
import io.github.domainreseller.dna.domains.DomainDetails;
import io.github.domainreseller.dna.domains.DomainOperationResult;
import io.github.domainreseller.dna.domains.ListDomainsRequest;
import io.github.domainreseller.dna.domains.DomainStatus;
import io.github.domainreseller.dna.domains.RenewalCheck;
import java.util.List;

/**
 * Registers a domain on OTE, renews it, changes its settings and reads its details. Uses the
 * {@code DNA_*} environment variables; run it with {@code DNA_ENV=ote}.
 */
public final class DomainExamples {
    private DomainExamples() {
    }

    /**
     * Runs the example.
     *
     * @param args the domain to register, for example {@code my-test-domain-123.com}
     */
    public static void main(String[] args) {
        DomainNameApiClient client = DomainNameApiClient.fromEnvironment();
        String domain = args.length > 0 ? args[0] : "my-test-domain-123.com";

        ContactInfo contact = new ContactInfo()
                .firstName("Ada").lastName("Lovelace").email("ada@example.com")
                .address("1 Example Street").city("Istanbul").state("Istanbul").postalCode("34000")
                .country("TR").phoneCountryCode("90").phone("5551234567");

        // Charges the balance (OTE has its own test balance).
        DomainOperationResult registered = client.domains().register(domain, contact, List.of("tr.apiname.com", "eu.apiname.com"));
        System.out.println("registered: " + registered.success() + " " + registered.message());

        RenewalCheck check = client.domains().checkRenewal(domain);
        if (check.isRenewable()) {
            client.domains().renew(domain, 1);
        }

        client.domains().lock(domain);
        client.domains().setPrivacyProtection(domain, true);
        client.domains().forwarding().set(domain, "https://www.example.net");

        DomainDetails details = client.domains().get(domain);
        System.out.println(details.domainName() + " expires " + details.expirationDate() + ", auth code " + details.authCode());
        details.contact(ContactType.REGISTRANT).ifPresent(c -> System.out.println("registrant: " + c.email()));

        client.domains().listAll(new ListDomainsRequest().statuses(DomainStatus.ACTIVE))
                .forEach(d -> System.out.println(d.domainName()));
    }
}
