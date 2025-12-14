package store.dal.entity;

import jakarta.persistence.*;
import lombok.*;
import dto.store.ProductCategory;
import dto.store.ProductState;
import dto.store.QuantityState;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "store")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String description;

    private String imageSrc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductState productState;

    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    @Column(nullable = false)
    private double price;
}