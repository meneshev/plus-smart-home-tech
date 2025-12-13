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
public class WarehouseProductId implements Serializable {
    @Column(name = "Warehouse_id")
    private Long warehouseId;
    @Column(name = "Product_id")
    private UUID productId;
}