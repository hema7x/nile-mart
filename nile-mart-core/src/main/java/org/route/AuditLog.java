package org.route;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditLog {
    @Before("execution(* org.route.CheckoutService.placeOrder(org.route.ShoppingCart, org.route.Governorate))")
    public void auditCheckout(org.aspectj.lang.JoinPoint joinPoint) {
        ShoppingCart cart = (ShoppingCart) joinPoint.getArgs()[0];
        Governorate governorate = (Governorate) joinPoint.getArgs()[1];
        System.out.println("Audit: checkout cart " + cart.getCartNumber() + " for " + governorate.getDisplayName());
    }
}
