package cart.controller;

import cart.service.ShoppingCartService;
import dto.ChangeProductQuantityRequest;
import dto.ShoppingCartDto;
import feign.shopping.cart.ShoppingCartOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import util.logging.Loggable;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class CartController implements ShoppingCartOperations {

    private final ShoppingCartService cartService;

    @Loggable
    @PutMapping
    public ShoppingCartDto addToCart(@RequestParam String username,
                                    @RequestBody Map<String, Long> products) {
        return cartService.addToCart(username, products);
    }

    @Loggable
    @GetMapping
    public ShoppingCartDto getCart(@RequestParam String username) {
        return cartService.getCart(username);
    }

    @Loggable
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteCart(@RequestParam String username) {
        cartService.deactivateCart(username);
    }

    @Loggable
    @PostMapping("/remove")
    public ShoppingCartDto removeProduct(@RequestParam String username,
                                         @RequestBody List<String> productIds) {
        return cartService.removeProduct(username, productIds);
    }

    @Loggable
    @PostMapping("/change-quantity")
    public ShoppingCartDto changeQuantity(@RequestParam String username,
                                          @RequestBody ChangeProductQuantityRequest request) {
        return cartService.changeQuantity(username, request);
    }
}
