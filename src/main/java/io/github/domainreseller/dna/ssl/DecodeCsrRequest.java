package io.github.domainreseller.dna.ssl;

import io.github.domainreseller.dna.BaseRequest;
import io.github.domainreseller.dna.HttpMethod;
import io.github.domainreseller.dna.JsonBody;
import io.github.domainreseller.dna.OperationSafety;

/** {@code POST ssls/check-csr}: decodes a CSR and returns its subject. */
public final class DecodeCsrRequest extends BaseRequest<CsrDetails> {
    private final String csr;

    /**
     * Creates the request.
     *
     * @param csr the CSR in PEM format
     */
    public DecodeCsrRequest(String csr) {
        this.csr = csr;
    }

    /**
     * Returns the CSR.
     *
     * @return the CSR in PEM format
     */
    public String csr() {
        return csr;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return "ssls/check-csr";
    }

    @Override
    public OperationSafety safety() {
        return OperationSafety.READ;
    }

    @Override
    protected Object body() {
        return JsonBody.create().add("csr", csr);
    }

    @Override
    protected void validate() {
        require(csr, "csr");
    }
}
