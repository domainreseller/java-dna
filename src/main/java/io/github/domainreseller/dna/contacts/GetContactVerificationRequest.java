package io.github.domainreseller.dna.contacts;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.QueryParameters;

/** {@code GET contacts/verification/check}: the e-mail and phone verification state of a contact. */
public final class GetContactVerificationRequest extends BaseRequest<ContactVerificationResult> {
    private final String handle;

    /**
     * Creates the request.
     *
     * @param handle the contact handle
     */
    public GetContactVerificationRequest(String handle) {
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
        return "contacts/verification/check";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }

    @Override
    protected void addQueryParameters(QueryParameters query) {
        query.add("HandleCode", handle);
    }

    @Override
    protected void validate() {
        require(handle, "handle");
    }
}
