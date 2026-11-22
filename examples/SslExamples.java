import io.github.domainreseller.dna.DomainNameApiClient;
import io.github.domainreseller.dna.ssl.CertificateContact;
import io.github.domainreseller.dna.ssl.ConfigureCertificateRequest;
import io.github.domainreseller.dna.ssl.GenerateCsrRequest;
import io.github.domainreseller.dna.ssl.GeneratedCsr;
import io.github.domainreseller.dna.ssl.ListCertificatesRequest;
import io.github.domainreseller.dna.ssl.SslOperationResult;
import io.github.domainreseller.dna.ssl.ValidationMethod;
import io.github.domainreseller.dna.ssl.WebServerType;
import java.util.List;
import java.util.UUID;

/** Buys, configures and lists SSL certificates. Certificates are addressed by their ID, never by domain name. */
public final class SslExamples {
    private SslExamples() {
    }

    /**
     * Runs the example.
     *
     * @param args the domain and the product code
     */
    public static void main(String[] args) {
        DomainNameApiClient client = DomainNameApiClient.fromEnvironment();
        String domain = args.length > 0 ? args[0] : "example.com";
        String packageCode = args.length > 1 ? args[1] : "PKG-CODE";

        List<String> approvers = client.ssl().getApproverEmails(domain, packageCode);
        SslOperationResult order = client.ssl().purchase(domain, packageCode);   // charges the balance
        UUID certificateId = order.certificateId();                                 // store it

        GeneratedCsr csr = client.ssl().generateCsr(new GenerateCsrRequest(domain)
                .country("TR").state("Istanbul").locality("Istanbul").organization("Example Ltd").organizationUnit("IT"));

        CertificateContact contact = new CertificateContact()
                .firstName("Ada").lastName("Lovelace").companyName("Example Ltd").email("ada@example.com")
                .address("1 Example Street").city("Istanbul").postalCode("34000").country("TR")
                .phoneCountryCode("90").phone("5551234567");

        client.ssl().configure(new ConfigureCertificateRequest(certificateId, csr.csr(), contact)
                .validationMethod(ValidationMethod.EMAIL)
                .approverEmail(approvers.isEmpty() ? "admin@" + domain : approvers.get(0))
                .webServerType(WebServerType.APACHE));

        client.ssl().listAll(new ListCertificatesRequest().domainName(domain))
                .forEach(c -> System.out.println(c.certificateId() + " " + c.status() + " " + c.subscriptionExpiresAt()));
    }
}
