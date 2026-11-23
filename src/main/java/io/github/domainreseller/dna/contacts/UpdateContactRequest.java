package io.github.domainreseller.dna.contacts;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.internal.Values;

/**
 * {@code PUT contacts/{handleCode}}: updates a contact handle. The domains that use it are updated too; the result
 * says what happened to each of them.
 */
public final class UpdateContactRequest extends BaseRequest<HandleUpdateResult> {
    private final String handle;
    private final ContactInfo contact;

    /**
     * Creates the request.
     *
     * @param handle  the contact handle
     * @param contact the new details; every required field must be set
     */
    public UpdateContactRequest(String handle, ContactInfo contact) {
        this.handle = handle;
        this.contact = contact;
    }

    /**
     * Returns the contact handle.
     *
     * @return the handle
     */
    public String handle() {
        return handle;
    }

    /**
     * Returns the new details.
     *
     * @return the contact
     */
    public ContactInfo contact() {
        return contact;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.PUT;
    }

    @Override
    public String path() {
        return "contacts/" + Values.pathSegment(handle);
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
        require(handle, "handle");
        require(contact, "contact");
        contact.ensureComplete("contact");
    }
}
