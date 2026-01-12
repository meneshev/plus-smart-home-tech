package warehouse.dal.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "storage", name = "Booking_Products")
public class BookingProducts {
    @EmbeddedId
    private BookingProductsId id;

    @Column(nullable = false)
    private Long quantity;

    @ManyToOne
    @JoinColumn(name = "Booking_id", updatable = false, insertable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Warehouse_id", updatable = false, insertable = false)
    private Warehouse warehouse;
}
