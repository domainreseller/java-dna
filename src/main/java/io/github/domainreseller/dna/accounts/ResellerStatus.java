package io.github.domainreseller.dna.accounts;

import io.github.domainreseller.dna.CodedEnum;

/** The state of a reseller account. */
public enum ResellerStatus implements CodedEnum {
    /** Active. */
    ACTIVE(0),
    /** Waiting for approval. */
    WAITING_FOR_APPROVAL(1),
    /** Suspended. */
    SUSPEND(2),
    /** Waiting for the sign-up wizard to be completed. */
    WAITING_FOR_COMPLETION_WIZARD(3),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    ResellerStatus(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
