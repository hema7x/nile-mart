package org.route;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class OrderRegistry {
    private final List<Order> orders = new ArrayList<>();
    public synchronized String nextOrderNumber() {
        return "NM-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-" + String.format(Locale.ROOT, "%05d", orders.size() + 1);
    }
    public synchronized void save(Order order) { orders.add(order); }
    public synchronized List<Order> findAll() { return List.copyOf(orders); }
    @PreDestroy
    public synchronized void printEndOfDay() {
        BigDecimal revenue = orders.stream().map(Order::total).reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("End of day: " + orders.size() + " orders, revenue " + Money.format(revenue) + " EGP");
    }
}
