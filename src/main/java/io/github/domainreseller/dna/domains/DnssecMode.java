package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.CodedEnum;

/** What to do with DNSSEC when the nameservers of a signed domain change. */
public enum DnssecMode implements CodedEnum {
    /** Refuse the nameserver change while DS records exist. The safe default. */
    BLOCK(0),
    /** Keep the DS records; you confirm the new DNS provider serves a matching DNSKEY. */
    KEEP_DNSSEC(1),
    /** Remove all DS records at the registry first, then change the nameservers. */
    DISABLE_DNSSEC(2),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    DnssecMode(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
