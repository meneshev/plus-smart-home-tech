package cart.dal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "cart", name = "ShoppingCart")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "shoppingcart_id")
    private UUID shoppingCartId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "isActive", nullable = false)
    private Boolean active;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "ShoppingCart_Products",
            schema = "cart",
            joinColumns = @JoinColumn(name = "ShoppingCart_id")
    )
    @MapKeyColumn(name = "Product_id")
    @Column(name = "quantity")
    private Map<UUID, Long> products = new HashMap<>();
}