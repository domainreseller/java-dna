package io.github.domainreseller.dna.accounts;

import io.github.domainreseller.dna.CodedEnum;

/** Whether a transaction added money to the balance or took it. */
public enum ChargeType implements CodedEnum {
    /** Credit: money added. */
    ADD(0),
    /** Debit: money taken. */
    SUBTRACT(1),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    ChargeType(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
