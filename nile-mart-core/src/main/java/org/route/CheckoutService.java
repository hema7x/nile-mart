package org.route;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final ProductService productService;
    private final ShippingQuoteService shippingQuoteService;
    private final TaxCalculator taxCalculator;
    private final OrderRegistry orderRegistry;
    private final List<NotificationSender> notificationSenders;

    public synchronized Order placeOrder(ShoppingCart cart, Governorate governorate) {
        if (cart.isEmpty()) throw new StoreException("Cannot check out an empty cart.");
        var lines = cart.getLines();
        lines.forEach((sku, qty) -> {
            Product product = productService.findBySku(sku);
            if (product.getStockQuantity() < qty)
                throw new StoreException("Insufficient stock for " + product.getName() + ". Available: " + product.getStockQuantity());
        });
        BigDecimal subtotal = cart.subtotal();
        BigDecimal vat = taxCalculator.calculateVat(subtotal);
        BigDecimal shipping = shippingQuoteService.quote(governorate, subtotal);
        BigDecimal total = Money.round(subtotal.add(vat).add(shipping));
        String number = orderRegistry.nextOrderNumber();
        Order order = new Order(number, cart.getCustomer(), governorate, lines, subtotal, vat, shipping, total);
        productService.reduceStocks(lines);
        orderRegistry.save(order);
        String message = "Order " + number + " confirmed. Total " + Money.format(total) + " EGP";
        notificationSenders.forEach(sender -> sender.send(cart.getCustomer(), message));
        cart.clear();
        return order;
    }
}
