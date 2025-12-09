package cart.service;

import cart.dal.entity.ShoppingCart;
import cart.dal.entity.User;
import cart.dal.mapper.ShoppingCartMapper;
import cart.dal.repository.ShoppingCartRepository;
import cart.dal.repository.UserRepository;
import dto.ChangeProductQuantityRequest;
import dto.ShoppingCartDto;
import feign.warehouse.WarehouseClient;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import util.exception.NotFoundException;
import util.logging.Loggable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto addToCart(String username, Map<String, Long> products) {
        checkUsername(username);
        if (checkActive(username)) {
            ShoppingCart cart = shoppingCartRepository.getShoppingCartByUser(userRepository.findByUsername(username));
            if (cart.getProducts() == null) {
                cart.setProducts(new HashMap<>());
            }
            cart.getProducts().putAll(products.entrySet().stream().collect(Collectors.toMap(
                    entry -> UUID.fromString(entry.getKey()),
                    Map.Entry::getValue
            )));

            warehouseClient.check(ShoppingCartMapper.toDto(cart));
            shoppingCartRepository.save(cart);
            return ShoppingCartMapper.toDto(cart);
        }
        return null;
    }

    @Override
    public ShoppingCartDto getCart(String username) {
        checkUsername(username);
        User user = userRepository.findByUsername(username);
        return ShoppingCartMapper.toDto(shoppingCartRepository.getShoppingCartByUser(user));
    }

    @Override
    public void deactivateCart(String username) {
        checkUsername(username);
        if (!checkActive(username)) {
            return;
        }
        ShoppingCart cart = shoppingCartRepository.getShoppingCartByUser(userRepository.findByUsername(username));
        cart.setActive(false);
        shoppingCartRepository.save(cart);
    }

    @Override
    public ShoppingCartDto removeProduct(String username, List<String> productIds) {
        checkUsername(username);
        if (!checkActive(username)) {
            log.error("Cart username {} is not active", username);
            throw new ValidationException("Cannot remove product from deactivated shopping cart");
        }

        ShoppingCart cart = shoppingCartRepository.getShoppingCartByUser(userRepository.findByUsername(username));
        List<UUID> productUUIDs = productIds.stream().map(UUID::fromString).toList();

        if (cart.getProducts().keySet().containsAll(productUUIDs)) {
            productUUIDs.forEach(productId -> cart.getProducts().remove(productId));
            shoppingCartRepository.save(cart);
        } else {
            log.info("Uncosistent product id list: {}", productUUIDs);
            throw new ValidationException("Uncosistent product id list");
        }

        return ShoppingCartMapper.toDto(cart);
    }

    @Loggable
    @Override
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        log.info("Received request to change quantity {} for user {}", request, username);
        checkUsername(username);

        ShoppingCart cart = shoppingCartRepository.getShoppingCartByUser(userRepository.findByUsername(username));
        if (cart.getProducts() == null) {
            cart.setProducts(new HashMap<>());
        }

        if (!cart.getProducts().containsKey(UUID.fromString(request.getProductId()))) {
            throw new NotFoundException("Product not found");
        }

        cart.getProducts().put(
                UUID.fromString(request.getProductId()),
                request.getNewQuantity()
        );
        warehouseClient.check(ShoppingCartMapper.toDto(cart));
        shoppingCartRepository.save(cart);
        return ShoppingCartMapper.toDto(cart);
    }

    private void checkUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = userRepository.save(User.builder().username(username).build());
            shoppingCartRepository.save(ShoppingCart.builder()
                    .active(true)
                    .user(user)
                    .build());
        }
    }

    private boolean checkActive(String username) {
        if (shoppingCartRepository.getShoppingCartByUser(userRepository.findByUsername(username)) == null) {
            throw new NotFoundException(String.format("ShoppingCart not found for username %s", username));
        }

        return shoppingCartRepository.getShoppingCartByUser(userRepository.findByUsername(username)).getActive();
    }

}
