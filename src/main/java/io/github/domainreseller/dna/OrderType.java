package io.github.domainreseller.dna;

/** Kind of order (also used to ask for a product's price). */
public enum OrderType implements CodedEnum {
    /** New registration. */
    REGISTER(1),
    /** Renewal. */
    RENEW(2),
    /** Transfer from another registrar. */
    TRANSFER(3),
    /** Restore from redemption. */
    RESTORE(4),
    /** Transfer between sub-resellers. */
    SUBRESELLER_TRANSFER(5),
    /** Backorder. */
    BACKORDER(6),
    /** Transfer between resellers on the platform. */
    INTERNAL_TRANSFER(7),
    /** A charge. */
    CHARGE(8),
    /** Trustee service. */
    TRUSTEE(9),
    /** Leasing (hosting). */
    LEASING(10),
    /** Creation (hosting, SSL). */
    CREATE(11),
    /** Owner change. */
    OWNER_CHANGE(12),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    OrderType(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
