package org.route;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class ShippingQuoteService {
    private final ShippingCalculator zoneCalculator;
    private final ShippingCalculator freeCalculator;
    private final BigDecimal threshold;
    public ShippingQuoteService(ShippingCalculator zoneCalculator,
                                @Qualifier("freeShipping") ShippingCalculator freeCalculator,
                                @Value("${store.free-shipping-threshold}") BigDecimal threshold) {
        this.zoneCalculator = zoneCalculator; this.freeCalculator = freeCalculator; this.threshold = threshold;
    }
    public BigDecimal quote(Governorate governorate, BigDecimal subtotal) {
        if (governorate.getZone().isEligibleForFreeShipping() && subtotal.compareTo(threshold) >= 0)
            return Money.round(freeCalculator.calculate(governorate));
        return Money.round(zoneCalculator.calculate(governorate));
    }
}
