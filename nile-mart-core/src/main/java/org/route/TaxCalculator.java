package org.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class TaxCalculator {
    private final BigDecimal vatRate;
    public TaxCalculator(@Value("${store.vat-rate}") BigDecimal vatRate) { this.vatRate = vatRate; }
    public BigDecimal calculateVat(BigDecimal subtotal) { return Money.round(subtotal.multiply(vatRate)); }
}
