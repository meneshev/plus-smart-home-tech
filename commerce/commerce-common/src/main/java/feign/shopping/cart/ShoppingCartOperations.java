package feign.shopping.cart;

import dto.ChangeProductQuantityRequest;
import dto.ShoppingCartDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public interface ShoppingCartOperations {

    @PutMapping
    ShoppingCartDto addToCart(@RequestBody String username, @RequestBody Map<String, Long> products);

    @GetMapping
    ShoppingCartDto getCart(@RequestParam String username);

    @DeleteMapping
    void deleteCart(@RequestParam String username);

    @PostMapping("/remove")
    ShoppingCartDto removeProduct(@RequestParam String username, @RequestBody List<String> productIds);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeQuantity(@RequestParam String username, @RequestBody ChangeProductQuantityRequest request);
}
