package org.route;

import java.math.BigDecimal;

public enum Zone {
    GREATER_CAIRO("50.00", true), ALEX_DELTA("65.00", true), CANAL("70.00", true),
    UPPER_EGYPT("85.00", true), REMOTE("120.00", false);
    private final BigDecimal fee;
    private final boolean eligibleForFreeShipping;
    Zone(String fee, boolean eligible) { this.fee = new BigDecimal(fee); this.eligibleForFreeShipping = eligible; }
    public BigDecimal getFee() { return fee; }
    public boolean isEligibleForFreeShipping() { return eligibleForFreeShipping; }
}
