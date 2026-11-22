import io.github.domainreseller.dna.ApiErrorKind;
import io.github.domainreseller.dna.DomainNameApiClient;
import io.github.domainreseller.dna.DomainNameApiException;

/** Shows how to react to the kinds of failure. */
public final class ErrorHandling {
    private ErrorHandling() {
    }

    /**
     * Runs the example.
     *
     * @param args the domain
     */
    public static void main(String[] args) {
        DomainNameApiClient client = DomainNameApiClient.fromEnvironment();
        String domain = args.length > 0 ? args[0] : "example.com";
        try {
            client.domains().renew(domain, 1);
        } catch (DomainNameApiException e) {
            if (e.kind() == ApiErrorKind.VALIDATION) {
                System.out.println(e.code() + ": " + e.getMessage());
                e.validationErrors().forEach(field -> System.out.println("  " + field));
            } else if (e.isOutcomeUnknown()) {
                // The renewal may have run. Check before sending it again.
                System.out.println("Unknown outcome; expiration is now " + client.domains().get(domain).expirationDate());
            } else if (e.kind() == ApiErrorKind.AUTHENTICATION) {
                System.out.println(e.code() + ": " + e.hint());
            } else {
                System.out.println(e);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Nothing was sent: " + e.getMessage());
        }
    }
}
