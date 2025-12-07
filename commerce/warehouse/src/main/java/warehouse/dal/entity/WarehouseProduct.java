package warehouse.dal.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "storage")
public class WarehouseProduct {
    @EmbeddedId
    private WarehouseProductId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "Product_id")
    private ProductSpecs productSpecs;

    @Column(nullable = false)
    private Long quantity;
}
