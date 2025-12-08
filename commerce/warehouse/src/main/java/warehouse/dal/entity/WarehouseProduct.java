package warehouse.dal.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "storage", name = "Warehouse_Product")
public class WarehouseProduct {
    @EmbeddedId
    private WarehouseProductId id;

    @ManyToOne
    @JoinColumn(name = "Warehouse_id", insertable = false, updatable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    //@MapsId("productId")
    @JoinColumn(name = "Product_id", insertable = false, updatable = false)
    private ProductSpecs productSpecs;

    @Column(nullable = false)
    private Long quantity;
}
