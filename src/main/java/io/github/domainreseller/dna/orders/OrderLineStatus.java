package io.github.domainreseller.dna.orders;

import io.github.domainreseller.dna.CodedEnum;

/** The state of an order line. */
public enum OrderLineStatus implements CodedEnum {
    /** Ordered. */
    ORDERED(1),
    /** Cancelled. */
    CANCELED(2),
    /** Being processed. */
    IN_PROGRESS(3),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    OrderLineStatus(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
