package cart.controller;

import cart.service.ShoppingCartService;
import dto.ChangeProductQuantityRequest;
import dto.ShoppingCartDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class CartController {

    private final ShoppingCartService cartService;

    @PutMapping
    public ShoppingCartDto addToCart(@RequestParam String username,
                                    @RequestBody Map<String, Long> products) {
        return cartService.addToCart(username, products);
    }

    @GetMapping
    public ShoppingCartDto getCart(@RequestParam String username) {
        return cartService.getCart(username);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteCart(@RequestParam String username) {
        cartService.deactivateCart(username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeProduct(@RequestParam String username,
                                         @RequestBody List<String> productIds) {
        return cartService.removeProduct(username, productIds);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeQuantity(@RequestParam String username,
                                          @RequestBody ChangeProductQuantityRequest request) {
        return cartService.changeQuantity(username, request);
    }
}
