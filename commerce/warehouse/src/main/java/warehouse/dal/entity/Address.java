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
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "Address_Country", nullable = false)
    private String country;

    @Column(name = "Address_City", nullable = false)
    private String city;

    @Column(name = "Address_Street", nullable = false)
    private String street;

    @Column(name = "Address_House", nullable = false)
    private String house;

    @Column(name = "Address_Flat")
    private String flat;
}