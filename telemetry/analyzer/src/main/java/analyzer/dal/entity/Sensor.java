package analyzer.dal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@Table(name = "sensors")
public class Sensor {
    @Column(nullable = false)
    private String id;
    @Column(name = "hub_id", nullable = false)
    private String hubId;
}