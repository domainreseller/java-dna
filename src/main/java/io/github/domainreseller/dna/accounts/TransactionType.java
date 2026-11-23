package io.github.domainreseller.dna.accounts;

import io.github.domainreseller.dna.CodedEnum;

/** What a balance transaction was for. */
public enum TransactionType implements CodedEnum {
    /** Domain registration. */
    REGISTER(1),
    /** Renewal. */
    RENEW(2),
    /** Transfer. */
    TRANSFER(3),
    /** Restore from redemption. */
    RESTORE(4),
    /** Balance deposit. */
    DEPOSIT(5),
    /** Backorder. */
    BACKORDER(6),
    /** Refund. */
    REFUND(7),
    /** Deduction. */
    DEDUCTION(8),
    /** Pre-registration. */
    PRE_REGISTER(9),
    /** Purchase (SSL, hosting and other products). */
    SHOPPING(10),
    /** Payment between resellers. */
    RESELLER_PAYMENT(11),
    /** Transfer between resellers on the platform. */
    INTERNAL_TRANSFER(12),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    TransactionType(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
