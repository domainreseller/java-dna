package io.github.domainreseller.dna;

import io.github.domainreseller.dna.internal.Values;
import java.util.List;

/**
 * One page of a list.
 *
 * @param items      the items on this page; never null
 * @param totalCount the number of items across all pages
 * @param <T>        the type of the items
 */
public record Page<T>(List<T> items, long totalCount) {
    /**
     * Creates a page.
     *
     * @param items      the items on this page; null becomes an empty list
     * @param totalCount the number of items across all pages
     */
    public Page {
        items = Values.list(items);
    }
}
