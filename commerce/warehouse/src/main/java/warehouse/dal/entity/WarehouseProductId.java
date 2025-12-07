package warehouse.dal.entity;

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
    private Long warehouseId;
    private UUID productId;
}