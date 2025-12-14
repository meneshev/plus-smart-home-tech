package feign.order;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "order", path = "/api/v1/order") // TODO fallback
public interface OrderClient extends OrderOperations {
}
