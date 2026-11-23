package io.github.domainreseller.dna;

/** The HTTP methods the API uses. */
public enum HttpMethod {
    /** Reads a resource. */
    GET,

    /** Creates something or runs an action. Searches and checks also use POST; see {@link OperationSafety}. */
    POST,

    /** Replaces or updates a resource. */
    PUT,

    /** Deletes a resource. */
    DELETE
}
