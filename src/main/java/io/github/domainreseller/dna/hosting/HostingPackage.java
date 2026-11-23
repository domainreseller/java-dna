package io.github.domainreseller.dna.hosting;

import io.github.domainreseller.dna.internal.Values;
import java.util.List;

/**
 * A hosting package and the service plans you can order it with.
 *
 * @param name                the package name
 * @param requiredMinLeasing  the shortest lease, in months
 * @param permittedMaxLeasing the longest lease, in months
 * @param servicePlans        the service plans of the package; never null
 */
public record HostingPackage(String name, int requiredMinLeasing, int permittedMaxLeasing, List<Plan> servicePlans) {
    /**
     * Creates a package.
     *
     * @param name                the package name
     * @param requiredMinLeasing  the shortest lease, in months
     * @param permittedMaxLeasing the longest lease, in months
     * @param servicePlans        the service plans; null becomes an empty list
     */
    public HostingPackage {
        servicePlans = Values.list(servicePlans);
    }

    /**
     * A service plan of a hosting package.
     *
     * @param servicePlan     the plan name, used when ordering
     * @param operatingSystem the operating system
     * @param panelType       the control panel
     */
    public record Plan(String servicePlan, String operatingSystem, String panelType) {
    }
}
