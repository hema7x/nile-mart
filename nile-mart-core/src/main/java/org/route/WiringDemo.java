package org.route;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.math.BigDecimal;

public class WiringDemo {
    public void showQuotes() {
        BigDecimal subtotal = new BigDecimal("4350.00");
        print("Factory", ObjectFactory.createShippingQuoteService(), subtotal);
        try (var xml = new ClassPathXmlApplicationContext("beans.xml")) {
            print("XML", xml.getBean(ShippingQuoteService.class), subtotal);
        }
        try (var annotations = new AnnotationConfigApplicationContext(AppConfig.class)) {
            print("Annotations", annotations.getBean(ShippingQuoteService.class), subtotal);
        }
    }
    private void print(String source, ShippingQuoteService service, BigDecimal subtotal) {
        System.out.println(source + " Giza shipping quote: " + Money.format(service.quote(Governorate.GIZA, subtotal)) + " EGP");
    }
}
