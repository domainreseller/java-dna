import io.github.domainreseller.dna.ApiResponse;
import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.DomainNameApiClient;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;
import io.github.domainreseller.dna.domains.DomainDetails;

/** Sends a request class of your own: everything the built-in requests get works for it too. */
public final class CustomRequest {
    private CustomRequest() {
    }

    /** A hand-written request; here the same endpoint as {@code client.domains().get(...)}. */
    static final class DomainInfoRequest extends BaseRequest<DomainDetails> {
        private final String domainName;

        DomainInfoRequest(String domainName) {
            this.domainName = domainName;
        }

        @Override
        public HttpMethod method() {
            return HttpMethod.GET;
        }

        @Override
        public String path() {
            return "domains/info";
        }

        @Override
        public OperationSafety safety() {
            return OperationSafety.READ;
        }

        @Override
        protected void addQueryParameters(QueryParameters query) {
            query.add("DomainName", domainName);
        }

        @Override
        protected void validate() {
            require(domainName, "domainName");
        }
    }

    /**
     * Runs the example.
     *
     * @param args the domain
     */
    public static void main(String[] args) {
        DomainNameApiClient client = DomainNameApiClient.fromEnvironment();
        ApiResponse<DomainDetails> response = client.sendWithResponse(new DomainInfoRequest(args.length > 0 ? args[0] : "example.com"));
        System.out.println(response.value().domainName() + " (correlation ID " + response.correlationId()
                + ", attempts " + response.attempts() + ", quota " + response.quota() + ")");
    }
}
