package feign.delivery;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "delivery", path = "/api/v1/delivery") // TODO fallback
public interface DeliveryClient extends DeliveryOperations {
}
