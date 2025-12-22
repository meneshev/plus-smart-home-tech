package feign.payment;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "payment", path = "/api/v1/payment") // TODO fallback
public interface PaymentClient extends PaymentOperations {
}
