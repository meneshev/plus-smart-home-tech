package delivery.dal.entity;

import dto.delivery.DeliveryState;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "delivery", name = "ShoppingDelivery")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delivery_id", columnDefinition = "uuid")
    private UUID deliveryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fromAddress_id")
    private DeliveryAddress fromAddress;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "toAddress_id")
    private DeliveryAddress toAddress;

    @Column(name = "order_id", columnDefinition = "uuid")
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_state")
    private DeliveryState state;

    @Column(name = "delivery_weight")
    private Double deliveryWeight;

    @Column(name = "delivery_volume")
    private Double deliveryVolume;

    private boolean fragile;

    @Column(name = "delivery_price")
    private Double deliveryPrice;
}
