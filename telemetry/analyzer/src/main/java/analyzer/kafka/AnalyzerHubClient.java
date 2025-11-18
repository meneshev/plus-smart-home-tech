package analyzer.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface AnalyzerHubClient {
    Consumer<Void, HubEventAvro> getConsumer();

    void stop();
}