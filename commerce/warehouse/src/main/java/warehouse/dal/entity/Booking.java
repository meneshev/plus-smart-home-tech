package warehouse.dal.entity;

import dto.order.OrderState;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "storage", name = "Booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Booking_id")
    private Long bookingId;

    @Column(name = "Order_id", columnDefinition = "uuid")
    private UUID orderId;

    @Column(name = "Delivery_id", columnDefinition = "uuid")
    private UUID deliveryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "Booking_state")
    private OrderState bookingState;

    @OneToMany(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<BookingProducts> products;
}