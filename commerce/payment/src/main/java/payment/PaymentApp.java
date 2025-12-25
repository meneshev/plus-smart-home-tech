package payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"payment", "util"}) // для логирования через AOP
@EnableFeignClients(basePackages = {"feign.order", "feign.shopping.store", "feign.delivery"})
public class PaymentApp {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApp.class, args);
    }
}
