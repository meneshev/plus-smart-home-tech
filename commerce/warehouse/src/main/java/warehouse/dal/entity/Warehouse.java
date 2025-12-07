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
    @Column(name = "Warehouse_id")
    private Long warehouseId;

    @Column(name = "Warehouse_Name", nullable = false)
    private String warehouseName;

    @Column(name = "Warehouse_begdt")
    private LocalDateTime warehouseBegDate;

    @Column(name = "Warehouse_enddt")
    private LocalDateTime warehouseEndDate;
}