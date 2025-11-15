package analyzer.dal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@Entity
@Table(name = "sensors")
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {
    @Id
    private String id;
    @Column(name = "hub_id", nullable = false)
    private String hubId;
}