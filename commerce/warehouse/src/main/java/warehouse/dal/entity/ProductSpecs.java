package warehouse.dal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenerationTime;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "storage", name = "ProductSpecs")
public class ProductSpecs {
    @Id
    @Column(name = "Product_id", columnDefinition = "uuid")
    private UUID productId;

    @Column(name = "ProductSpecs_isFragile")
    private Boolean isFragile;

    @Column(name = "ProductSpecs_Width", nullable = false)
    private Double width;

    @Column(name = "ProductSpecs_Height", nullable = false)
    private Double height;

    @Column(name = "ProductSpecs_Depth", nullable = false)
    private Double depth;

    @Column(name = "ProductSpecs_Weight", nullable = false)
    private Double weight;

    @Column(name = "ProductSpecs_Volume", insertable = false, updatable = false)
    private Double volume;
}