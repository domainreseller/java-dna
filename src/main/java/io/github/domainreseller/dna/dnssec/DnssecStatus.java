package io.github.domainreseller.dna.dnssec;

import io.github.domainreseller.dna.CodedEnum;

/** The DNSSEC state of a domain. */
public enum DnssecStatus implements CodedEnum {
    /** No DS record at the registry. */
    UNSIGNED(0),
    /** A DS record was sent; waiting for the registry. */
    PENDING_ENABLE(1),
    /** At least one DS record is active. */
    SIGNED(2),
    /** DS removal was sent; waiting for the registry. */
    PENDING_DISABLE(3),
    /** The last DNSSEC operation failed. */
    ERROR(4),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    DnssecStatus(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
