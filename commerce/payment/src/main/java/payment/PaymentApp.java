package payment;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"payment", "util"}) // для логирования через AOP
@EnableFeignClients(basePackages = {"feign.warehouse", "feign.shopping.store"})
public class PaymentApp {
}
