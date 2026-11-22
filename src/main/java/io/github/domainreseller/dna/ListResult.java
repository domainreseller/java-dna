package io.github.domainreseller.dna;

import io.github.domainreseller.dna.internal.Values;
import java.util.List;

/**
 * A list the API returns in one piece, wrapped as {@code {"items": [...]}}.
 *
 * @param items the items; never null
 * @param <T>   the type of the items
 */
public record ListResult<T>(List<T> items) {
    /**
     * Creates a list.
     *
     * @param items the items; null becomes an empty list
     */
    public ListResult {
        items = Values.list(items);
    }
}
