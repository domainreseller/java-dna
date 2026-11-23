package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.DomainNameApiClient;
import io.github.domainreseller.dna.Page;
import io.github.domainreseller.dna.contacts.ContactHandle;
import io.github.domainreseller.dna.contacts.ContactInfo;
import io.github.domainreseller.dna.contacts.DomainContact;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Search, register, renew, transfer and manage domains. Reach it through {@link DomainNameApiClient#domains()}.
 * URL forwarding, child nameservers and registry documents have their own clients: {@link #forwarding()},
 * {@link #childNameServers()} and {@link #documents()}.
 *
 * <p>Methods that charge your balance say so. They are never sent twice: after a network error the outcome is
 * unknown ({@link io.github.domainreseller.dna.DomainNameApiException#isOutcomeUnknown()}), so check the domain before
 * trying again.
 */
public final class DomainsClient {
    private final DomainNameApiClient api;
    private final DomainForwardingClient forwarding;
    private final ChildNameServersClient childNameServers;
    private final DomainDocumentsClient documents;

    /**
     * Creates the client. Use {@link DomainNameApiClient#domains()} instead.
     *
     * @param api the client that sends the requests
     */
    public DomainsClient(DomainNameApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
        this.forwarding = new DomainForwardingClient(api);
        this.childNameServers = new ChildNameServersClient(api);
        this.documents = new DomainDocumentsClient(api);
    }

    /**
     * URL forwarding of domains.
     *
     * @return the forwarding client
     */
    public DomainForwardingClient forwarding() {
        return forwarding;
    }

    /**
     * Child nameservers (glue records) such as ns1.example.com.
     *
     * @return the child nameservers client
     */
    public ChildNameServersClient childNameServers() {
        return childNameServers;
    }

    /**
     * Registry documents (mostly for .tr domains).
     *
     * @return the documents client
     */
    public DomainDocumentsClient documents() {
        return documents;
    }

    // ------------------------------------------------------------------------------------------------ search

    /**
     * Checks whether a domain can be registered and returns its price in the account's currency.
     *
     * @param domainName the domain, for example {@code example.com}
     * @return availability and price
     */
    public DomainSearchResult search(String domainName) {
        return search(domainName, null);
    }

    /**
     * Checks whether a domain can be registered and returns its price.
     *
     * @param domainName the domain
     * @param currency   the price currency, for example {@code USD} or {@code TRY}; null for the account default
     * @return availability and price
     */
    public DomainSearchResult search(String domainName, String currency) {
        return api.send(new SearchDomainRequest(domainName, currency));
    }

    /**
     * Checks several domains in one call.
     *
     * @param domainNames the domains, for example {@code example.com} and {@code example.net}
     * @return one result per domain
     */
    public BulkSearchResult bulkSearch(Collection<String> domainNames) {
        return bulkSearch(domainNames, null);
    }

    /**
     * Checks several domains in one call.
     *
     * @param domainNames the domains
     * @param currency    the price currency; null for the account default
     * @return one result per domain
     */
    public BulkSearchResult bulkSearch(Collection<String> domainNames, String currency) {
        return api.send(new BulkSearchDomainsRequest(domainNames, currency));
    }

    // ------------------------------------------------------------------------------------- list and details

    /**
     * Returns the first page of the domains in your account.
     *
     * @return the page
     */
    public Page<Domain> list() {
        return list(new ListDomainsRequest());
    }

    /**
     * Returns one page of the domains in your account.
     *
     * @param request filters and paging
     * @return the page
     */
    public Page<Domain> list(ListDomainsRequest request) {
        return api.send(request);
    }

    /**
     * Returns every domain in your account, fetching pages as the stream is consumed.
     *
     * @return the domains
     */
    public Stream<Domain> listAll() {
        return listAll(new ListDomainsRequest());
    }

    /**
     * Returns every domain that matches the filters, fetching pages as the stream is consumed.
     *
     * @param request the filters
     * @return the domains
     */
    public Stream<Domain> listAll(ListDomainsRequest request) {
        return api.listAll(request);
    }

    /**
     * Returns the full details of a domain.
     *
     * @param domainName the domain
     * @return the details
     */
    public DomainDetails get(String domainName) {
        return api.send(new GetDomainRequest(domainName));
    }

    // ------------------------------------------------------------------ register, renew, transfer, restore

    /**
     * Registers a domain with contact details given inline. <b>Charges your balance.</b>
     *
     * @param request the registration
     * @return the result; check {@link DomainOperationResult#success()}
     */
    public DomainOperationResult register(RegisterDomainRequest request) {
        return api.send(request);
    }

    /**
     * Registers a domain for one year and uses one contact for all four roles. <b>Charges your balance.</b>
     *
     * @param domainName  the domain to register
     * @param contact     the contact used as registrant, admin, tech and billing contact
     * @param nameServers the nameservers; most registries need at least two
     * @return the result; check {@link DomainOperationResult#success()}
     */
    public DomainOperationResult register(String domainName, ContactInfo contact, Collection<String> nameServers) {
        return register(domainName, contact, nameServers, 1);
    }

    /**
     * Registers a domain and uses one contact for all four roles. <b>Charges your balance.</b>
     *
     * @param domainName  the domain to register
     * @param contact     the contact used as registrant, admin, tech and billing contact
     * @param nameServers the nameservers; most registries need at least two
     * @param years       the registration period in years
     * @return the result; check {@link DomainOperationResult#success()}
     */
    public DomainOperationResult register(String domainName, ContactInfo contact, Collection<String> nameServers, int years) {
        Objects.requireNonNull(contact, "contact");
        return api.send(new RegisterDomainRequest(domainName)
                .period(years)
                .nameServers(nameServers)
                .contacts(DomainContact.forAllRoles(contact)));
    }

    /**
     * Registers a domain using existing contact handles. <b>Charges your balance.</b>
     *
     * @param request the registration
     * @return the result; check {@link DomainOperationResult#success()}
     */
    public DomainOperationResult registerWithHandles(RegisterDomainWithHandlesRequest request) {
        return api.send(request);
    }

    /**
     * Registers a domain and uses one contact handle for all four roles. <b>Charges your balance.</b>
     *
     * @param domainName  the domain to register
     * @param handle      the contact handle, for example {@code DNA-12345}
     * @param nameServers the nameservers; most registries need at least two
     * @param years       the registration period in years
     * @return the result; check {@link DomainOperationResult#success()}
     */
    public DomainOperationResult registerWithHandle(String domainName, String handle, Collection<String> nameServers, int years) {
        return api.send(new RegisterDomainWithHandlesRequest(domainName)
                .period(years)
                .nameServers(nameServers)
                .handles(ContactHandle.forAllRoles(handle)));
    }

    /**
     * Renews a domain for one year. <b>Charges your balance.</b>
     *
     * @param domainName the domain
     * @return the result; check {@link DomainOperationResult#success()}
     */
    public DomainOperationResult renew(String domainName) {
        return renew(domainName, 1);
    }

    /**
     * Renews a domain. <b>Charges your balance.</b>
     *
     * @param domainName the domain
     * @param years      the renewal period in years
     * @return the result; check {@link DomainOperationResult#success()}
     */
    public DomainOperationResult renew(String domainName, int years) {
        return api.send(new RenewDomainRequest(domainName, years));
    }

    /**
     * Checks whether a domain can be renewed for one year, and its premium price if any. Charges nothing.
     *
     * @param domainName the domain
     * @return the check
     */
    public RenewalCheck checkRenewal(String domainName) {
        return checkRenewal(domainName, 1);
    }

    /**
     * Checks whether a domain can be renewed, and its premium price if any. Charges nothing.
     *
     * @param domainName the domain
     * @param years      the renewal period in years to check
     * @return the check
     */
    public RenewalCheck checkRenewal(String domainName, int years) {
        return api.send(new CheckRenewalRequest(domainName, years));
    }

    /**
     * Transfers a domain in from another registrar, adding one year. <b>Charges your balance.</b>
     *
     * @param domainName the domain
     * @param authCode   the transfer (EPP) code from the current registrar
     * @return the result; check {@link DomainOperationResult#success()}
     */
    public DomainOperationResult transfer(String domainName, String authCode) {
        return transfer(domainName, authCode, 1);
    }

    /**
     * Transfers a domain in from another registrar. <b>Charges your balance.</b>
     *
     * @param domainName the domain
     * @param authCode   the transfer (EPP) code from the current registrar
     * @param years      years added by the transfer
     * @return the result; check {@link DomainOperationResult#success()}
     */
    public DomainOperationResult transfer(String domainName, String authCode, int years) {
        return api.send(new TransferDomainRequest(domainName, authCode).period(years));
    }

    /**
     * Checks whether a domain can be transferred in with an auth code. Charges nothing.
     *
     * @param domainName the domain
     * @param authCode   the transfer (EPP) code
     * @return the check
     */
    public TransferCheck checkTransfer(String domainName, String authCode) {
        return api.send(new CheckTransferRequest(domainName, authCode));
    }

    /**
     * Returns the state of a pending transfer.
     *
     * @param domainName the domain
     * @return the transfer state
     */
    public TransferStatus getTransferStatus(String domainName) {
        return api.send(new GetTransferStatusRequest(domainName));
    }

    /**
     * Restores a deleted domain from redemption. <b>Charges your balance.</b>
     *
     * @param domainName the domain
     */
    public void restore(String domainName) {
        api.send(new RestoreDomainRequest(domainName));
    }

    // --------------------------------------------------------------------------------------------- settings

    /**
     * Turns the transfer lock on.
     *
     * @param domainName the domain
     */
    public void lock(String domainName) {
        api.send(new LockDomainRequest(domainName));
    }

    /**
     * Turns the transfer lock off, which is needed before transferring out.
     *
     * @param domainName the domain
     */
    public void unlock(String domainName) {
        api.send(new UnlockDomainRequest(domainName));
    }

    /**
     * Turns WHOIS privacy protection on or off.
     *
     * @param domainName the domain
     * @param enabled    true to hide the contacts in WHOIS
     */
    public void setPrivacyProtection(String domainName, boolean enabled) {
        api.send(new SetPrivacyProtectionRequest(domainName, enabled));
    }

    /**
     * Sets a new transfer (EPP) code and returns it.
     *
     * @param domainName the domain
     * @return the new auth code
     */
    public DomainAuthCode updateAuthCode(String domainName) {
        return api.send(new UpdateAuthCodeRequest(domainName));
    }

    /**
     * Replaces the nameservers of a domain. On a DNSSEC-signed domain the API refuses the change; use
     * {@link #setNameServers(String, Collection, DnssecMode)} to say what happens to the DS records.
     *
     * @param domainName  the domain
     * @param nameServers the complete list of nameservers
     */
    public void setNameServers(String domainName, Collection<String> nameServers) {
        setNameServers(domainName, nameServers, null);
    }

    /**
     * Replaces the nameservers of a domain.
     *
     * @param domainName  the domain
     * @param nameServers the complete list of nameservers
     * @param dnssecMode  what to do with DNSSEC on a signed domain; null refuses the change while DS records exist
     */
    public void setNameServers(String domainName, Collection<String> nameServers, DnssecMode dnssecMode) {
        api.send(new SetNameServersRequest(domainName, nameServers).dnssecMode(dnssecMode));
    }

    // --------------------------------------------------------------------------------------------- contacts

    /**
     * Replaces the contacts of a domain with details given inline.
     *
     * @param domainName the domain
     * @param contacts   the contacts, one per role; {@link DomainContact#forAllRoles} fills all four
     */
    public void updateContacts(String domainName, Collection<DomainContact> contacts) {
        api.send(new UpdateDomainContactsRequest(domainName, contacts));
    }

    /**
     * Points the contact roles of a domain at existing contact handles.
     *
     * @param domainName the domain
     * @param handles    the handles, one per role; {@link ContactHandle#forAllRoles} uses one handle for all four
     */
    public void setContactHandles(String domainName, Collection<ContactHandle> handles) {
        api.send(new SetDomainContactHandlesRequest(domainName, handles));
    }
}
