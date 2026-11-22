package io.github.domainreseller.dna.orders;

import io.github.domainreseller.dna.CodedEnum;

/** The state of an order. */
public enum OrderStatus implements CodedEnum {
    /** Completed and active. */
    ACTIVE(1),
    /** Waiting. */
    PENDING(2),
    /** Cancelled. */
    CANCELED(3),
    /** Being processed. */
    IN_PROGRESS(4),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    OrderStatus(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
