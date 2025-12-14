package order.dal.entity;

import dto.order.OrderState;
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
@Table(schema = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;

    // TODO?
    private UUID shoppingCartId;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "Order_Products",
            schema = "orders",
            joinColumns = @JoinColumn(name = "order_id")
    )
    @MapKeyColumn(name = "Product_id")
    @Column(name = "quantity")
    private Map<UUID, Long> products = new HashMap<>();

    // TODO?
    private UUID paymentId;

    // TODO?
    private UUID deliveryId;

    @Enumerated(EnumType.STRING)
    //@Column(nullable = false)
    private OrderState orderState;
}
