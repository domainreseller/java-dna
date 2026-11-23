package io.github.domainreseller.dna.contacts;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.internal.Values;

/** {@code GET contacts/{handleCode}}: a contact handle and the state of its verification. */
public final class GetContactRequest extends BaseRequest<ContactDetails> {
    private final String handle;

    /**
     * Creates the request.
     *
     * @param handle the contact handle, for example {@code DNA-12345}
     */
    public GetContactRequest(String handle) {
        this.handle = handle;
    }

    /**
     * Returns the contact handle.
     *
     * @return the handle
     */
    public String handle() {
        return handle;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }

    @Override
    public String path() {
        return "contacts/" + Values.pathSegment(handle);
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }

    @Override
    protected void validate() {
        require(handle, "handle");
    }
}
