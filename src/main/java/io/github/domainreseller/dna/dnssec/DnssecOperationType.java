package io.github.domainreseller.dna.dnssec;

import io.github.domainreseller.dna.CodedEnum;

/** Kind of DNSSEC operation. */
public enum DnssecOperationType implements CodedEnum {
    /** A DS record was added. */
    ADD(0),
    /** A DS record was removed. */
    REMOVE(1),
    /** All DS records were removed. */
    REMOVE_ALL(2),
    /** The state was synchronised with the registry. */
    SYNC(3),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    DnssecOperationType(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
