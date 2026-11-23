package io.github.domainreseller.dna.hosting;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.OperationSafety;
import java.util.List;

/** {@code GET hostings/packages}: the hosting packages and their service plans. */
public final class ListHostingPackagesRequest extends BaseRequest<List<HostingPackage>> {
    /** Creates the request. */
    public ListHostingPackagesRequest() {
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }

    @Override
    public String path() {
        return "hostings/packages";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }
}
