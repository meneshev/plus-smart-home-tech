package aggregator.kafka;

import org.apache.avro.Schema;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public class SensorEventDeserializer extends BaseAvroDeserializer<SensorEventAvro> {
    public SensorEventDeserializer(Schema schema) {
        super(SensorEventAvro.getClassSchema());
    }
}