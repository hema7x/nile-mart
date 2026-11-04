package org.route;

import java.math.BigDecimal;
import java.math.RoundingMode;

final class Money {
    private Money() { }
    static BigDecimal round(BigDecimal amount) { return amount.setScale(2, RoundingMode.HALF_UP); }
    static String format(BigDecimal amount) { return round(amount).toPlainString(); }
}
