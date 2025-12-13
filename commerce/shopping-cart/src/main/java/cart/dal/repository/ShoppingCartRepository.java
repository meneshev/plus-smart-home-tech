package cart.dal.repository;

import cart.dal.entity.ShoppingCart;
import cart.dal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, UUID> {
    ShoppingCart getShoppingCartByUser(User user);
}
