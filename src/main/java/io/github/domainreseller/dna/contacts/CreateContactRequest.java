package io.github.domainreseller.dna.contacts;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;

/** {@code POST contacts}: creates a reusable contact handle. */
public final class CreateContactRequest extends BaseRequest<CreateContactResult> {
    private final ContactInfo contact;

    /**
     * Creates the request.
     *
     * @param contact the contact details; every required field must be set
     */
    public CreateContactRequest(ContactInfo contact) {
        this.contact = contact;
    }

    /**
     * Returns the contact details.
     *
     * @return the contact
     */
    public ContactInfo contact() {
        return contact;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "contacts";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.WRITE;
    }

    @Override
    protected Object body() {
        return JsonBody.create().add("contact", contact);
    }

    @Override
    protected void validate() {
        require(contact, "contact");
        contact.ensureComplete("contact");
    }
}
