package org.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.*;

@Component
@Scope("prototype")
public class ShoppingCart {
    private final ProductService productService;
    private final int maxQuantity;
    private int cartNumber;
    private Customer customer;
    private final Map<String, Integer> lines = new LinkedHashMap<>();

    public ShoppingCart(ProductService productService,
                        @Value("${store.max-quantity-per-product}") int maxQuantity) {
        this.productService = productService; this.maxQuantity = maxQuantity;
    }
    public void configure(int cartNumber, Customer customer) { this.cartNumber = cartNumber; this.customer = customer; }
    public int getCartNumber() { return cartNumber; }
    public Customer getCustomer() { return customer; }
    public Map<String, Integer> getLines() { return Collections.unmodifiableMap(new LinkedHashMap<>(lines)); }
    public void addItem(String sku, int qty) {
        if (qty < 1) throw new StoreException("Quantity must be at least 1.");
        Product product = productService.findBySku(sku);
        if (product.getStockQuantity() == 0) throw new StoreException(product.getName() + " is out of stock.");
        int updated = lines.getOrDefault(sku, 0) + qty;
        if (updated > maxQuantity) throw new StoreException("Maximum quantity per product is " + maxQuantity + ".");
        if (updated > product.getStockQuantity()) throw new StoreException("Only " + product.getStockQuantity() + " available for " + product.getName() + ".");
        lines.put(sku, updated);
    }
    public BigDecimal subtotal() {
        return Money.round(lines.entrySet().stream()
                .map(e -> productService.getPrice(e.getKey(), e.getValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
    public boolean isEmpty() { return lines.isEmpty(); }
    public void clear() { lines.clear(); }
}
