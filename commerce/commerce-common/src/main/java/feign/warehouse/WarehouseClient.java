package feign.warehouse;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse") // TODO fallback
public interface WarehouseClient extends WarehouseOperations {
}
