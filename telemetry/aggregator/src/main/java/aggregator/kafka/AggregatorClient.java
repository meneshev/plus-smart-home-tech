package aggregator.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface AggregatorClient {
    Consumer<Void, SensorEventAvro> getConsumer();

    //TODO getProducer()

    void stop();
}