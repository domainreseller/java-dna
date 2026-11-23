package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.CodedEnum;

/** The lifecycle state of a domain. */
public enum DomainStatus implements CodedEnum {
    /** Registered and working. */
    ACTIVE(0),
    /** The registration is being processed. */
    WAITING_FOR_REGISTRATION(1),
    /** Registry documents must be uploaded. */
    WAITING_FOR_DOCUMENT(2),
    /** An internal transfer from another reseller is pending. */
    WAITING_FOR_INCOMING_TRANSFER(3),
    /** Transferred to another registrar. */
    TRANSFERRED_OUT(4),
    /** The transfer was rejected. */
    TRANSFER_REJECTED(5),
    /** About to be deleted. */
    PENDING_DELETE(7),
    /** Deleted. */
    DELETED(8),
    /** The transfer confirmation e-mail was sent. */
    CONFIRMATION_EMAIL_SEND(9),
    /** An outgoing transfer is pending. */
    WAITING_FOR_OUTGOING_TRANSFER(11),
    /** On hold. */
    PENDING_HOLD(12),
    /** A change is being processed. */
    MODIFICATION_PENDING(15),
    /** Subject to a legal process. */
    IN_CASE(18),
    /** A restore from redemption is pending. */
    PENDING_RESTORE(19),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    DomainStatus(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
