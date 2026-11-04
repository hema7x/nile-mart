package org.route;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class);
             var input = new Scanner(System.in)) {
            ProductService products = context.getBean(ProductService.class);
            CheckoutService checkout = context.getBean(CheckoutService.class);
            Map<Integer, ShoppingCart> carts = new LinkedHashMap<>();
            int nextCartNumber = 1;
            boolean running = true;
            while (running) {
                printMenu();
                String choice = input.nextLine().trim();
                try {
                    switch (choice) {
                        case "1" -> products.findAll().forEach(Main::printProduct);
                        case "2" -> {
                            System.out.print("Customer name: "); String name = input.nextLine().trim();
                            System.out.print("Phone: "); String phone = input.nextLine().trim();
                            System.out.print("Email (optional): "); String email = input.nextLine().trim();
                            ShoppingCart cart = context.getBean(ShoppingCart.class);
                            cart.configure(nextCartNumber, new Customer(name, phone, email.isBlank() ? null : email));
                            carts.put(nextCartNumber, cart);
                            System.out.println("Created cart " + nextCartNumber++ + " for " + name);
                        }
                        case "3" -> {
                            ShoppingCart cart = findCart(carts, readInt(input, "Cart number: "));
                            System.out.print("Product SKU: "); String sku = input.nextLine().trim();
                            int quantity = readInt(input, "Quantity: ");
                            cart.addItem(sku, quantity);
                            System.out.println("Added to cart " + cart.getCartNumber());
                        }
                        case "4" -> printCart(findCart(carts, readInt(input, "Cart number: ")), products);
                        case "5" -> {
                            ShoppingCart cart = findCart(carts, readInt(input, "Cart number: "));
                            System.out.print("Governorate: ");
                            Order order = checkout.placeOrder(cart, Governorate.fromName(input.nextLine()));
                            System.out.println("Order total: " + Money.format(order.total()) + " EGP");
                        }
                        case "0" -> running = false;
                        default -> throw new StoreException("Choose one of the listed options.");
                    }
                } catch (RuntimeException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println("========== NILEMART ==========");
        System.out.println("1. Show products\n2. Create a cart\n3. Add a product to a cart\n4. Show a cart\n5. Check out a cart\n0. Exit");
        System.out.print("Choice: ");
    }
    private static int readInt(Scanner input, String prompt) {
        System.out.print(prompt);
        try { return Integer.parseInt(input.nextLine().trim()); }
        catch (NumberFormatException ex) { throw new StoreException("Enter a valid number."); }
    }
    private static ShoppingCart findCart(Map<Integer, ShoppingCart> carts, int number) {
        ShoppingCart cart = carts.get(number);
        if (cart == null) throw new StoreException("Cart not found: " + number);
        return cart;
    }
    private static void printProduct(Product p) {
        System.out.println(p.getSku() + " | " + p.getName() + " | " + p.getCategory() + " | "
                + Money.format(p.getPrice()) + " EGP | Stock: " + p.getStockQuantity());
    }
    private static void printCart(ShoppingCart cart, ProductService products) {
        System.out.println("Cart " + cart.getCartNumber() + " - " + cart.getCustomer().fullName());
        if (cart.isEmpty()) System.out.println("(empty)");
        cart.getLines().forEach((sku, qty) -> {
            Product p = products.findBySku(sku);
            System.out.println(p.getName() + " x " + qty + " = " + Money.format(products.getPrice(sku, qty)) + " EGP");
        });
        System.out.println("Subtotal: " + Money.format(cart.subtotal()) + " EGP");
    }
}
