package analyzer.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface AnalyzerSnapshotClient {
    Consumer<Void, SensorsSnapshotAvro> getConsumer();

    void stop();
}