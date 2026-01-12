package warehouse.dal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class BookingProductsId implements Serializable {
    @Column(name = "Booking_id")
    private Long bookingId;
    @Column(name = "Warehouse_id")
    private Long warehouseId;
    @Column(name = "Product_id", columnDefinition = "uuid")
    private UUID productId;
}
