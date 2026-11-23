package io.github.domainreseller.dna.dnssec;

import io.github.domainreseller.dna.CodedEnum;

/** The state of a DS record. */
public enum DsRecordStatus implements CodedEnum {
    /** Waiting for the registry. */
    PENDING(0),
    /** Published. */
    ACTIVE(1),
    /** Being removed. */
    REMOVING(2),
    /** Removed. */
    REMOVED(3),
    /** The registry refused it. */
    FAILED(4),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    DsRecordStatus(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
