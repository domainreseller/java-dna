package io.github.domainreseller.dna.domains;

import io.github.domainreseller.dna.CodedEnum;

/** Kind of registry document (mostly needed for .tr domains). */
public enum DocumentType implements CodedEnum {
    /** Identity card. */
    IDENTITY_CARD(1),
    /** Passport. */
    PASSPORT(2),
    /** Tax certificate. */
    TAX_CERTIFICATE(3),
    /** Certificate of activity. */
    ACTIVITY_CERTIFICATE(4),
    /** Signature circular. */
    SIGNATURE_CIRCULAR(5),
    /** Letter of authorisation. */
    AUTHORIZATION_LETTER(6),
    /** Petition. */
    PETITION(7),
    /** Trade registry gazette. */
    TRADE_REGISTRY_GAZETTE(8),
    /** Trademark certificate. */
    TRADEMARK_CERTIFICATE(9),
    /** Education certificate (.edu.tr). */
    EDUCATION_CERTIFICATE(10),
    /** Organisation statute (.org.tr). */
    ORGANIZATION_STATUTE(11),
    /** Government approval (.gov.tr, .bank, .mil.tr). */
    GOVERNMENT_APPROVAL(12),
    /** Proof of domain ownership. */
    DOMAIN_OWNERSHIP_PROOF(13),
    /** Power of attorney. */
    POWER_OF_ATTORNEY(14),
    /** Bar association certificate (.av.tr). */
    BAR_ASSOCIATION_CERTIFICATE(15),
    /** Medical diploma or licence (.dr.tr). */
    MEDICAL_DIPLOMA_OR_LICENSE(16),
    /** Official letter of a municipality (.bel.tr). */
    MUNICIPAL_OFFICIAL_LETTER(17),
    /** Licence of an education institution (.k12.tr, .edu.tr). */
    EDUCATION_INSTITUTION_LICENSE(18),
    /** Chamber of commerce certificate. */
    CHAMBER_OF_COMMERCE_CERTIFICATE(19),
    /** Trademark transfer agreement. */
    TRADEMARK_TRANSFER_AGREEMENT(20),
    /** KEP authorisation (.kep.tr). */
    KEP_AUTHORIZATION(21),
    /** Any other document. */
    OTHER(99),
    /** A value this SDK version does not know. */
    UNKNOWN(-1);

    private final int code;

    DocumentType(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
