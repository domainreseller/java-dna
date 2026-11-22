package io.github.domainreseller.dna;

/**
 * The gateway's soft quota after a request. It never rejects requests: once {@link #used()} passes
 * {@link #limit()}, each request is held for up to 5 seconds until points decay (20% per minute).
 * Searches and info lookups cost 1 point, everything else 10.
 *
 * @param scope        the quota category: domain, contact, ssl, products, order, deposit or hosting; may be null
 * @param cost         points charged for this request
 * @param used         points in the bucket after this request
 * @param limit        the threshold where slowing down starts
 * @param delaySeconds seconds the gateway held this request
 * @param resetSeconds seconds until the bucket is empty again
 */
public record ApiQuota(String scope, double cost, double used, double limit, double delaySeconds, double resetSeconds) {
    /**
     * Returns whether the gateway slowed this request down.
     *
     * @return true when {@link #delaySeconds()} is positive
     */
    public boolean wasDelayed() {
        return delaySeconds > 0;
    }
}
