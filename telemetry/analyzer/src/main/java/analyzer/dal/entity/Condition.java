package analyzer.dal.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

@Getter @Setter @Builder
@Entity
@Table(name = "conditions")
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ConditionTypeAvro type;

    @Enumerated(EnumType.STRING)
    private ConditionOperation operation;

    private Integer value;
}