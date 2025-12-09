package warehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"warehouse", "util"}) // для логирования через AOP
public class WarehouseApp {
    public static void main(String[] args) {
        SpringApplication.run(WarehouseApp.class, args);
    }
}