package io.github.domainreseller.dna.dnssec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A DS record to publish or remove. Copy the values from your DNS provider, or use {@link #parse(String)} with the
 * DS record text ({@code keytag algorithm digesttype digest}).
 *
 * @param keyTag     the key tag
 * @param algorithm  the algorithm
 * @param digestType the digest type
 * @param digest     the digest, in hexadecimal
 */
public record DsRecordData(int keyTag, DnssecAlgorithm algorithm, DsDigestType digestType, String digest) {
    /**
     * Reads a DS record in presentation format, for example
     * {@code 12345 13 2 49FD46E6C4B45C55D4AC69CBD3CD34AC1AFE51DE}. A leading {@code example.com. IN DS} is ignored and
     * a digest split by spaces is joined.
     *
     * @param text the DS record
     * @return the record
     * @throws IllegalArgumentException when the text is not a DS record
     */
    public static DsRecordData parse(String text) {
        List<String> parts = new ArrayList<>();
        for (String part : (text == null ? "" : text.trim()).split("\\s+")) {
            if (!part.isEmpty()) {
                parts.add(part);
            }
        }
        for (int i = 0; i < parts.size(); i++) {
            if (parts.get(i).equalsIgnoreCase("DS")) {
                parts = parts.subList(i + 1, parts.size());
                break;
            }
        }
        if (parts.size() < 4 || !digits(parts.get(0)) || !digits(parts.get(1)) || !digits(parts.get(2))) {
            throw new IllegalArgumentException("Expected a DS record such as '12345 13 2 49FD46E6...', got '" + text + "'.");
        }
        DnssecAlgorithm algorithm = byCode(DnssecAlgorithm.values(), Integer.parseInt(parts.get(1)), DnssecAlgorithm.UNKNOWN);
        DsDigestType digestType = byCode(DsDigestType.values(), Integer.parseInt(parts.get(2)), DsDigestType.UNKNOWN);
        return new DsRecordData(Integer.parseInt(parts.get(0)), algorithm, digestType,
                String.join("", parts.subList(3, parts.size())));
    }

    private static boolean digits(String text) {
        return !text.isEmpty() && text.length() <= 9 && text.chars().allMatch(Character::isDigit);
    }

    private static <E extends Enum<E> & io.github.domainreseller.dna.CodedEnum> E byCode(E[] values, int code, E unknown) {
        return Arrays.stream(values).filter(v -> v.code() == code).findFirst().orElse(unknown);
    }
}
