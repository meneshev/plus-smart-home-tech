package feign.shopping.store;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store") // TODO fallback
public interface ShoppingStoreClient extends ShoppingStoreOperations {
}