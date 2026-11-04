package org.route;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;
    private final PricingService pricingService;

    public List<Product> findAll() { return repository.findAll(); }

    public Product findBySku(String sku) {
        return repository.findBySku(sku)
                .filter(Product::isActive)
                .orElseThrow(() -> new StoreException("Product not found: " + sku));
    }

    public BigDecimal getPrice(String sku, int qty) {
        return Money.round(pricingService.getPrice(findBySku(sku), qty));
    }

    public synchronized void reduceStock(String sku, int qty) {
        Product product = findBySku(sku);
        if (qty < 1 || product.getStockQuantity() < qty)
            throw new StoreException("Insufficient stock for " + sku + ". Available: " + product.getStockQuantity());
        product.setStockQuantity(product.getStockQuantity() - qty);
        repository.save(product);
    }

    public synchronized void reduceStocks(java.util.Map<String, Integer> quantities) {
        java.util.Map<String, Product> products = new java.util.LinkedHashMap<>();
        quantities.forEach((sku, qty) -> {
            Product product = findBySku(sku);
            if (qty == null || qty < 1 || product.getStockQuantity() < qty)
                throw new StoreException("Insufficient stock for " + sku + ". Available: " + product.getStockQuantity());
            products.put(sku, product);
        });
        quantities.forEach((sku, qty) -> {
            Product product = products.get(sku);
            product.setStockQuantity(product.getStockQuantity() - qty);
            repository.save(product);
        });
    }
}
