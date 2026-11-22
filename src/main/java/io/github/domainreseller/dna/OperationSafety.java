package io.github.domainreseller.dna;

/**
 * What an API operation can do to your account. Every request declares one, so you can tell a harmless lookup from
 * a call that spends money before you send it. The classes are the same as in the {@code dna} CLI.
 */
public enum OperationSafety {
    /** No side effects, even when the API uses POST or PUT (for example a domain search). */
    READ,

    /** A free change that can be undone (DNS records, locks, contacts, nameservers). */
    WRITE,

    /** Charges your balance: registrations, renewals, transfers, restores, SSL and hosting orders. */
    BILLABLE,

    /** Deletes something or is hard to undo: record deletion, DNSSEC removal, certificate revocation. */
    DESTRUCTIVE
}
