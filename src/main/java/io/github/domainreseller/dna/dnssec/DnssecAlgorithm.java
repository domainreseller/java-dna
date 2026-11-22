package io.github.domainreseller.dna.dnssec;

import io.github.domainreseller.dna.CodedEnum;

/** IANA DNSSEC algorithm numbers. */
public enum DnssecAlgorithm implements CodedEnum {
    /** RSA/MD5 (deprecated). */
    RSA_MD5(1),
    /** DSA/SHA-1. */
    DSA(3),
    /** RSA/SHA-1. */
    RSA_SHA1(5),
    /** DSA-NSEC3-SHA1. */
    DSA_NSEC3_SHA1(6),
    /** RSASHA1-NSEC3-SHA1. */
    RSA_SHA1_NSEC3_SHA1(7),
    /** RSA/SHA-256. */
    RSA_SHA256(8),
    /** RSA/SHA-512. */
    RSA_SHA512(10),
    /** GOST R 34.10-2001. */
    ECC_GOST(12),
    /** ECDSA P-256 with SHA-256 (the most common choice). */
    ECDSA_P256_SHA256(13),
    /** ECDSA P-384 with SHA-384. */
    ECDSA_P384_SHA384(14),
    /** Ed25519. */
    ED25519(15),
    /** Ed448. */
    ED448(16),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    DnssecAlgorithm(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
