package io.github.domainreseller.dna.accounts;

import io.github.domainreseller.dna.CodedEnum;

/** The outcome of a balance transaction. */
public enum TransactionStatus implements CodedEnum {
    /** Failed. */
    FAILED(0),
    /** Completed. */
    SUCCESS(1),
    /** Cancelled. */
    CANCELED(2),
    /** Still being processed. */
    PROCESSING(3),
    /** Refunded. */
    REFUNDED(4),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    TransactionStatus(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
