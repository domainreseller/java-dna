package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.contacts.ContactType;
import io.github.domainreseller.dna.contacts.DomainContact;
import io.github.domainreseller.dna.internal.Values;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * {@code POST domains/register-with-contacts}: registers a domain with contact details given inline.
 * <b>Charges your balance.</b> Use {@link RegisterDomainWithHandlesRequest} to reuse existing contact handles.
 *
 * <pre>{@code
 * RegisterDomainRequest request = new RegisterDomainRequest("example.com")
 *         .period(1)
 *         .nameServers("ns1.example.net", "ns2.example.net")
 *         .contacts(DomainContact.forAllRoles(contact));
 * DomainOperationResult result = client.domains().register(request);
 * }</pre>
 */
public final class RegisterDomainRequest extends BaseRequest<DomainOperationResult> {
    private final String domainName;
    private int period = 1;
    private List<String> nameServers = List.of();
    private List<DomainContact> contacts = List.of();
    private Map<String, String> tldAttributes;
    private boolean useTrusteeContact;

    /**
     * Creates the request.
     *
     * @param domainName the domain to register
     */
    public RegisterDomainRequest(String domainName) {
        this.domainName = domainName;
    }

    /**
     * Returns the domain to register.
     *
     * @return the domain name
     */
    public String domainName() {
        return domainName;
    }

    /**
     * Returns the registration period.
     *
     * @return the period in years
     */
    public int period() {
        return period;
    }

    /**
     * Sets the registration period. 1 year by default.
     *
     * @param period the period in years
     * @return this request
     */
    public RegisterDomainRequest period(int period) {
        this.period = period;
        return this;
    }

    /**
     * Returns the nameservers.
     *
     * @return the nameservers
     */
    public List<String> nameServers() {
        return nameServers;
    }

    /**
     * Sets the nameservers. Most registries need at least two.
     *
     * @param nameServers the nameservers
     * @return this request
     */
    public RegisterDomainRequest nameServers(String... nameServers) {
        return nameServers(Arrays.asList(nameServers));
    }

    /**
     * Sets the nameservers. Most registries need at least two.
     *
     * @param nameServers the nameservers
     * @return this request
     */
    public RegisterDomainRequest nameServers(Collection<String> nameServers) {
        this.nameServers = Values.list(nameServers);
        return this;
    }

    /**
     * Returns the contacts.
     *
     * @return the contacts, one per role
     */
    public List<DomainContact> contacts() {
        return contacts;
    }

    /**
     * Sets the contacts, one per role. All four roles (Registrant, Admin, Tech, Billing) are needed unless
     * {@link #useTrusteeContact(boolean)} is set; {@link DomainContact#forAllRoles} fills all four from one contact.
     *
     * @param contacts the contacts
     * @return this request
     */
    public RegisterDomainRequest contacts(Collection<DomainContact> contacts) {
        this.contacts = Values.list(contacts);
        return this;
    }

    /**
     * Returns the registry-specific attributes.
     *
     * @return the attributes, or null
     */
    public Map<String, String> tldAttributes() {
        return tldAttributes;
    }

    /**
     * Sets registry-specific attributes, such as the ones .tr domains need. The TLD list names each TLD's
     * attributes ({@code Tld.attributes()}).
     *
     * @param tldAttributes the attributes, or null
     * @return this request
     */
    public RegisterDomainRequest tldAttributes(Map<String, String> tldAttributes) {
        this.tldAttributes = tldAttributes == null ? null : Values.map(tldAttributes);
        return this;
    }

    /**
     * Returns whether the registry's trustee service is used.
     *
     * @return whether a trustee contact is used
     */
    public boolean useTrusteeContact() {
        return useTrusteeContact;
    }

    /**
     * Uses the registry's trustee (local presence) service.
     *
     * @param useTrusteeContact whether to use it
     * @return this request
     */
    public RegisterDomainRequest useTrusteeContact(boolean useTrusteeContact) {
        this.useTrusteeContact = useTrusteeContact;
        return this;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "domains/register-with-contacts";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.BILLABLE;
    }

    @Override
    protected Object body() {
        return JsonBody.create()
                .add("domainName", domainName)
                .add("period", period)
                .add("nameServers", nameServers)
                .add("contacts", contacts)
                .add("tldAttributes", tldAttributes)
                .add("useTrusteeContact", useTrusteeContact);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        if (period < 1) {
            throw new IllegalArgumentException("period must be at least 1 year.");
        }
        require(nameServers, "nameServers");
        require(contacts, "contacts");
        for (DomainContact contact : contacts) {
            require(contact, "contacts");
            contact.ensureComplete("contacts[" + contact.contactType() + "]");
        }
        if (!useTrusteeContact) {
            List<String> missing = new ArrayList<>();
            for (ContactType role : ContactType.ALL) {
                if (contacts.stream().noneMatch(c -> role.equals(c.contactType()))) {
                    missing.add(role.value());
                }
            }
            if (!missing.isEmpty()) {
                throw new IllegalArgumentException("contacts is missing the roles " + String.join(", ", missing)
                        + ". Give all four roles (DomainContact.forAllRoles helps) or set useTrusteeContact.");
            }
        }
    }
}
