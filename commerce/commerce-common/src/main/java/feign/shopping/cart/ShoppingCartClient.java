package feign.shopping.cart;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart") // TODO fallback
public interface ShoppingCartClient extends ShoppingCartOperations {
}
