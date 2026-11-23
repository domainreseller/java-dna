package io.github.domainreseller.dna.contacts;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;

/** {@code POST contacts/verification/request}: sends the contact verification notice again. */
public final class ResendContactVerificationRequest extends BaseRequest<Void> {
    private final String handle;

    /**
     * Creates the request.
     *
     * @param handle the contact handle
     */
    public ResendContactVerificationRequest(String handle) {
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
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "contacts/verification/request";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.WRITE;
    }

    @Override
    protected Object body() {
        return JsonBody.create().add("handleCode", handle);
    }

    @Override
    protected void validate() {
        require(handle, "handle");
    }
}
