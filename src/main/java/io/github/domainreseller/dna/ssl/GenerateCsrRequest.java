package io.github.domainreseller.dna.ssl;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.internal.Values;
import java.util.Arrays;
import java.util.List;

/**
 * {@code POST ssls/generate-csr}: generates a certificate signing request and a private key. Charges nothing. Keep
 * the private key: you need it to install the certificate.
 *
 * <pre>{@code
 * GeneratedCsr csr = client.ssl().generateCsr(new GenerateCsrRequest("example.com")
 *         .country("TR").state("Istanbul").locality("Istanbul")
 *         .organization("Example Ltd").organizationUnit("IT"));
 * }</pre>
 */
public final class GenerateCsrRequest extends BaseRequest<GeneratedCsr> {
    private final String domainName;
    private String country;
    private String state;
    private String locality;
    private String organization;
    private String organizationUnit;
    private List<String> alternativeNames;

    /**
     * Creates the request. Country, state, locality, organisation and organisational unit are required as well.
     *
     * @param domainName the common name
     */
    public GenerateCsrRequest(String domainName) {
        this.domainName = domainName;
    }

    /**
     * Returns the common name.
     *
     * @return the domain name
     */
    public String domainName() {
        return domainName;
    }

    /**
     * Returns the country.
     *
     * @return the two-letter country code
     */
    public String country() {
        return country;
    }

    /**
     * Sets the two-letter country code.
     *
     * @param country the country code
     * @return this request
     */
    public GenerateCsrRequest country(String country) {
        this.country = country;
        return this;
    }

    /**
     * Returns the state or province.
     *
     * @return the state
     */
    public String state() {
        return state;
    }

    /**
     * Sets the state or province.
     *
     * @param state the state
     * @return this request
     */
    public GenerateCsrRequest state(String state) {
        this.state = state;
        return this;
    }

    /**
     * Returns the city.
     *
     * @return the city
     */
    public String locality() {
        return locality;
    }

    /**
     * Sets the city.
     *
     * @param locality the city
     * @return this request
     */
    public GenerateCsrRequest locality(String locality) {
        this.locality = locality;
        return this;
    }

    /**
     * Returns the organisation.
     *
     * @return the organisation
     */
    public String organization() {
        return organization;
    }

    /**
     * Sets the organisation.
     *
     * @param organization the organisation
     * @return this request
     */
    public GenerateCsrRequest organization(String organization) {
        this.organization = organization;
        return this;
    }

    /**
     * Returns the organisational unit.
     *
     * @return the unit
     */
    public String organizationUnit() {
        return organizationUnit;
    }

    /**
     * Sets the organisational unit.
     *
     * @param organizationUnit the unit
     * @return this request
     */
    public GenerateCsrRequest organizationUnit(String organizationUnit) {
        this.organizationUnit = organizationUnit;
        return this;
    }

    /**
     * Returns the additional domain names.
     *
     * @return the names, or null
     */
    public List<String> alternativeNames() {
        return alternativeNames;
    }

    /**
     * Sets additional domain names for multi-domain certificates.
     *
     * @param alternativeNames the names
     * @return this request
     */
    public GenerateCsrRequest alternativeNames(String... alternativeNames) {
        this.alternativeNames = Values.list(Arrays.asList(alternativeNames));
        return this;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "ssls/generate-csr";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }

    @Override
    protected Object body() {
        return JsonBody.create()
                .add("domainName", domainName)
                .add("country", country)
                .add("state", state)
                .add("locality", locality)
                .add("organization", organization)
                .add("organizationUnit", organizationUnit)
                .add("alternativeNames", alternativeNames);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        require(country, "country");
        require(state, "state");
        require(locality, "locality");
        require(organization, "organization");
        require(organizationUnit, "organizationUnit");
    }
}
