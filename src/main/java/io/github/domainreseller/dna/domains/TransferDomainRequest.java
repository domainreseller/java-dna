package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;
import io.github.domainreseller.dna.contacts.ContactInfo;
import io.github.domainreseller.dna.internal.Values;
import java.util.Collection;
import java.util.List;

/**
 * {@code POST domains/transfer}: transfers a domain in from another registrar. <b>Charges your balance.</b>
 * Check it first with {@link CheckTransferRequest}.
 */
public final class TransferDomainRequest extends BaseRequest<DomainOperationResult> {
    private final String domainName;
    private final String authCode;
    private int period = 1;
    private List<ContactInfo> contacts;

    /**
     * Creates the request for one year.
     *
     * @param domainName the domain to transfer
     * @param authCode   the transfer (EPP) code from the current registrar; some registries do not use one
     */
    public TransferDomainRequest(String domainName, String authCode) {
        this.domainName = domainName;
        this.authCode = authCode;
    }

    /**
     * Returns the domain to transfer.
     *
     * @return the domain name
     */
    public String domainName() {
        return domainName;
    }

    /**
     * Returns the transfer code.
     *
     * @return the auth code, or null
     */
    public String authCode() {
        return authCode;
    }

    /**
     * Returns the years added by the transfer.
     *
     * @return the period in years
     */
    public int period() {
        return period;
    }

    /**
     * Sets the years added by the transfer. 1 by default.
     *
     * @param period the period in years
     * @return this request
     */
    public TransferDomainRequest period(int period) {
        this.period = period;
        return this;
    }

    /**
     * Returns the contacts to set after the transfer.
     *
     * @return the contacts, or null
     */
    public List<ContactInfo> contacts() {
        return contacts;
    }

    /**
     * Sets contacts to use after the transfer. Usually left unset: the registrar takes the current ones over.
     *
     * @param contacts the contacts, or null
     * @return this request
     */
    public TransferDomainRequest contacts(Collection<? extends ContactInfo> contacts) {
        this.contacts = contacts == null ? null : Values.list(contacts);
        return this;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "domains/transfer";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.BILLABLE;
    }

    @Override
    protected Object body() {
        return JsonBody.create()
                .add("domainName", domainName)
                .add("authCode", authCode)
                .add("period", period)
                .add("contacts", contacts);
    }

    @Override
    protected void validate() {
        require(domainName, "domainName");
        if (period < 1) {
            throw new IllegalArgumentException("period must be at least 1 year.");
        }
    }
}
