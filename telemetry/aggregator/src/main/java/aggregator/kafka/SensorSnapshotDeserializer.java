package aggregator.kafka;

import org.apache.avro.Schema;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class SensorSnapshotDeserializer extends BaseAvroDeserializer<SensorsSnapshotAvro> {
    public SensorSnapshotDeserializer(Schema schema) {
        super(SensorsSnapshotAvro.getClassSchema());
    }
}