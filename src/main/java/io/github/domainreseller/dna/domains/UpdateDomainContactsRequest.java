package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.contacts.DomainContact;
import io.github.domainreseller.dna.internal.Values;
import java.util.Collection;
import java.util.List;

/** {@code PUT domains/contacts/update}: replaces the contacts of a domain with details given inline. */
public final class UpdateDomainContactsRequest extends BaseRequest<Void> {
    private final String domainName;
    private final List<DomainContact> contacts;

    /**
     * Creates the request.
     *
     * @param domainName the domain
     * @param contacts   the contacts, one per role you want to change; {@link DomainContact#forAllRoles} fills all four
     */
    public UpdateDomainContactsRequest(String domainName, Collection<DomainContact> contacts) {
        this.domainName = domainName;
        this.contacts = Values.list(contacts);
    }

    /**
     * Returns the domain.
     *
     * @return the domain name
     */
    public String domainName() {
        return domainName;
    }

    /**
     * Returns the contacts.
     *
     * @return the contacts, one per role
     */
    public List<DomainContact> contacts() {
        return contacts;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.PUT;
    }

    @Override
    public String path() {
        return "domains/contacts/update";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.WRITE;
    }

    @Override
    protected Object body() {
        return JsonBody.create().add("domainName", domainName).add("contacts", contacts);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        require(contacts, "contacts");
        for (DomainContact contact : contacts) {
            require(contact, "contacts");
            contact.ensureComplete("contacts[" + contact.contactType() + "]");
        }
    }
}
