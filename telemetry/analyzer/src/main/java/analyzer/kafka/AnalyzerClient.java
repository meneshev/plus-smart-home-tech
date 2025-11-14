package analyzer.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface AnalyzerClient {
    Consumer<Void, HubEventAvro> getHubConsumer();
//TODO разделить на два класса
    Consumer<Void, SensorsSnapshotAvro> getSnapshotConsumer();

    void stop();
}