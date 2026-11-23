package io.github.domainreseller.dna;

/**
 * An enum whose values the API sends as numbers, such as {@code DomainStatus} or {@link OrderType}.
 * Reading accepts the number (also as text) and the constant's name; writing uses the number.
 * Every such enum has an {@code UNKNOWN} constant for numbers this SDK version does not know yet.
 */
public interface CodedEnum {
    /**
     * Returns the number the API uses for this value.
     *
     * @return the API's number, or -1 for {@code UNKNOWN}
     */
    int code();
}
