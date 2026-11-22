import io.github.domainreseller.dna.DomainNameApiClient;
import io.github.domainreseller.dna.accounts.ResellerAccount;
import io.github.domainreseller.dna.domains.DomainSearchResult;

/**
 * Checks the credentials, shows the balance, searches a domain and lists the first domains of the account. Changes
 * nothing: the client is read-only.
 *
 * <pre>
 * export DNA_RESELLER_ID=... DNA_API_KEY=... DNA_ENV=ote
 * java -cp domainnameapi-2.0.0.jar:jackson-*.jar:. QuickStart example.com
 * </pre>
 */
public final class QuickStart {
    private QuickStart() {
    }

    /**
     * Runs the example.
     *
     * @param args the domain to search, optional
     */
    public static void main(String[] args) {
        // The DNA_* variables, as the dna CLI reads them; read-only, so nothing can change by accident.
        DomainNameApiClient client = DomainNameApiClient.Builder.fromEnvironment().readOnly(true).build();

        ResellerAccount account = client.account().get();
        System.out.println(account.resellerName() + ": " + account.usdBalance() + " USD, " + account.tryBalance() + " TRY");

        String domain = args.length > 0 ? args[0] : "example.com";
        DomainSearchResult search = client.domains().search(domain);
        if (search.info() != null) {
            System.out.println(domain + ": " + search.info().status() + " " + search.info().price() + " " + search.info().currency());
        }

        client.domains().listAll().limit(10).forEach(d ->
                System.out.println(d.domainName() + " expires in " + d.remainingDays() + " days"));
    }
}
