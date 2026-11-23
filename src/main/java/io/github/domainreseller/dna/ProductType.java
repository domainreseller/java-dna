package io.github.domainreseller.dna;

/** Kind of product. */
public enum ProductType implements CodedEnum {
    /** A domain name. */
    DOMAIN(1),
    /** An SSL certificate. */
    SSL(2),
    /** A balance deposit. */
    DEPOSIT(3),
    /** A trustee (local presence) service for a domain. */
    DOMAIN_TRUSTEE(4),
    /** Web hosting. */
    HOSTING(5),
    /** A domain owner change. */
    OWNER_CHANGE(6),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    ProductType(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
