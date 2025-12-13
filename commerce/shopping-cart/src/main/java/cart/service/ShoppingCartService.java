package cart.service;

import dto.ChangeProductQuantityRequest;
import dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;

public interface ShoppingCartService {
    ShoppingCartDto addToCart(String username, Map<String, Long> products);

    ShoppingCartDto getCart(String username);

    void deactivateCart(String username);

    ShoppingCartDto removeProduct(String username, List<String> productIds);

    ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request);
}
