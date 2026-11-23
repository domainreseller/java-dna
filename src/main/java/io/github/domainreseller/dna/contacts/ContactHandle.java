package io.github.domainreseller.dna.contacts;

import java.util.ArrayList;
import java.util.List;

/**
 * A contact handle assigned to a role, used to register a domain or to change its contacts.
 *
 * @param contactType the role
 * @param handle      the contact handle, for example {@code DNA-12345}
 */
public record ContactHandle(ContactType contactType, String handle) {
    /**
     * Uses the same handle for all four roles.
     *
     * @param handle the contact handle
     * @return four assignments, one per role
     */
    public static List<ContactHandle> forAllRoles(String handle) {
        List<ContactHandle> handles = new ArrayList<>();
        for (ContactType role : ContactType.ALL) {
            handles.add(new ContactHandle(role, handle));
        }
        return handles;
    }
}
