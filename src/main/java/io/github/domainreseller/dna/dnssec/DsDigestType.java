package io.github.domainreseller.dna.dnssec;

import io.github.domainreseller.dna.CodedEnum;

/** IANA DS digest types. */
public enum DsDigestType implements CodedEnum {
    /** SHA-1. */
    SHA1(1),
    /** SHA-256 (the most common choice). */
    SHA256(2),
    /** GOST R 34.11-94. */
    GOST(3),
    /** SHA-384. */
    SHA384(4),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    DsDigestType(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
