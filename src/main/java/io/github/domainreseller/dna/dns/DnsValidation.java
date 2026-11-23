package io.github.domainreseller.dna.dns;

import io.github.domainreseller.dna.JsonBody;
import java.util.List;

/** Checks the rules the API enforces on zone records, so mistakes fail before anything is sent. */
final class DnsValidation {
    private DnsValidation() {
    }

    static void check(String name, RecordType type, int ttl, List<String> contents) {
        if (ttl < 1 || ttl > 86400) {
            throw new IllegalArgumentException("ttl must be between 1 and 86400 seconds.");
        }
        for (String content : contents) {
            if (content == null || content.isBlank()) {
                throw new IllegalArgumentException("contents contains an empty value.");
            }
        }
        if (RecordType.CNAME.equals(type) && (name == null || name.isBlank() || name.trim().equals("@"))) {
            throw new IllegalArgumentException("A CNAME record needs a name such as www; use ALIAS at the apex.");
        }
        if (RecordType.CAA.equals(type)) {
            for (String content : contents) {
                if (content.trim().split("\\s+").length < 3) {
                    throw new IllegalArgumentException(
                            "CAA content needs three parts, for example: 0 issue \"letsencrypt.org\". Got: " + content);
                }
            }
        }
    }

    /** The {@code {"zoneStruct": {...}}} body of the zone write endpoints. */
    static JsonBody zoneBody(String name, RecordType type, int ttl, List<String> contents) {
        return JsonBody.create().add("zoneStruct", JsonBody.create()
                .add("name", name)
                .add("ttl", ttl)
                .add("type", type)
                .add("contents", contents));
    }
}
