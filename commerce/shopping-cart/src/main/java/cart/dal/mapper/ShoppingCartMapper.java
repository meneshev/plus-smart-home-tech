package cart.dal.mapper;

import cart.dal.entity.ShoppingCart;
import dto.cart.ShoppingCartDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShoppingCartMapper {
    public static ShoppingCartDto toDto(ShoppingCart cart) {
        return ShoppingCartDto.builder()
                .shoppingCartId(cart.getShoppingCartId().toString())
                .products(cart.getProducts().entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> entry.getKey().toString(),
                                Map.Entry::getValue
                        )))
                .build();
    }
}
