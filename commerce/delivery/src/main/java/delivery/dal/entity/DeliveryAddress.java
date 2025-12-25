package delivery.dal.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "delivery", name = "DeliveryAddress")
public class DeliveryAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DeliveryAddress_id")
    private Long addressId;

    @Column(name = "DeliveryAddress_Country", nullable = false)
    private String country;

    @Column(name = "DeliveryAddress_City", nullable = false)
    private String city;

    @Column(name = "DeliveryAddress_Street", nullable = false)
    private String street;

    @Column(name = "DeliveryAddress_House", nullable = false)
    private String house;

    @Column(name = "DeliveryAddress_Flat")
    private String flat;
}