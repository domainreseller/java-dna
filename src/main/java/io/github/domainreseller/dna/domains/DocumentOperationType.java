package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.CodedEnum;

/** The operation a registry document was requested for. */
public enum DocumentOperationType implements CodedEnum {
    /** A new registration. */
    CREATE(1),
    /** A registrant change. */
    OWNER_CHANGE(2),
    /** A deletion. */
    DELETE(3),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    DocumentOperationType(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
