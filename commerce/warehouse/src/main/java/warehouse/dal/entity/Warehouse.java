package warehouse.dal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "storage")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long warehouseId;

    @Column(nullable = false)
    private String warehouseName;

    @Column(name = "Warehouse_bedDt")
    private LocalDateTime warehouseBegDate;

    @Column(name = "Warehouse_endDt")
    private LocalDateTime warehouseEndDate;
}