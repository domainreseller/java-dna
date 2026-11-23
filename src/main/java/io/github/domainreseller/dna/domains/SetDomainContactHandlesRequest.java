package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.contacts.ContactHandle;
import io.github.domainreseller.dna.internal.Values;
import java.util.Collection;
import java.util.List;

/** {@code PUT domains/contacts/update-with-handle}: points the contact roles of a domain at existing contact handles. */
public final class SetDomainContactHandlesRequest extends BaseRequest<Void> {
    private final String domainName;
    private final List<ContactHandle> handles;

    /**
     * Creates the request.
     *
     * @param domainName the domain
     * @param handles    the handles, one per role you want to change; {@link ContactHandle#forAllRoles} sets all four
     */
    public SetDomainContactHandlesRequest(String domainName, Collection<ContactHandle> handles) {
        this.domainName = domainName;
        this.handles = Values.list(handles);
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
     * Returns the handles.
     *
     * @return the handles, one per role
     */
    public List<ContactHandle> handles() {
        return handles;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.PUT;
    }

    @Override
    public String path() {
        return "domains/contacts/update-with-handle";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.WRITE;
    }

    @Override
    protected Object body() {
        return JsonBody.create().add("domainName", domainName).add("handleItems", handles);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        require(handles, "handles");
        for (ContactHandle handle : handles) {
            if (handle == null || handle.handle() == null || handle.handle().isBlank()) {
                throw new IllegalArgumentException("Every entry in handles needs a handle.");
            }
        }
    }
}
