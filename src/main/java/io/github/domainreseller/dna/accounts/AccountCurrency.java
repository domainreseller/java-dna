package io.github.domainreseller.dna.accounts;

import io.github.domainreseller.dna.CodedEnum;

/** The currency a reseller account is billed in. */
public enum AccountCurrency implements CodedEnum {
    /** Turkish lira (TRY). */
    TRY(1),
    /** US dollar (USD). */
    USD(2),
    /** Euro (the API's {@code EURO}). */
    EURO(3),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    AccountCurrency(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
