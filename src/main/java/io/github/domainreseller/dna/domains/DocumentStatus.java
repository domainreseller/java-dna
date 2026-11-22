package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.CodedEnum;

/** The review state of a registry document. */
public enum DocumentStatus implements CodedEnum {
    /** Expected but not sent yet. */
    WAITING(1),
    /** Sent, not reviewed yet. */
    PENDING(2),
    /** Approved. */
    APPROVED(3),
    /** Rejected. */
    REJECTED(4),
    /** Expired. */
    EXPIRED(5),
    /** The application or owner change was cancelled. */
    CANCELLED(6),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    DocumentStatus(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
