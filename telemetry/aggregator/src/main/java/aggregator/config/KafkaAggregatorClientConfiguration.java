package aggregator.config;

import aggregator.kafka.AggregatorClient;
import aggregator.kafka.SensorEventDeserializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.List;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaAggregatorClientConfiguration {
    private final Environment env;

    @Scope("prototype")
    @Bean
    AggregatorClient kafkaAggregatorClient() {
        return new AggregatorClient() {
            private Consumer<Void, SensorEventAvro> consumer;

            @Override
            public Consumer<Void, SensorEventAvro> getConsumer() {
                if (consumer == null) {
                    initConsumer();
                }
                return consumer;
            }

            private void initConsumer() {
                Properties props = new Properties();
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("spring.kafka.properties.bootstrap.servers"));
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, env.getProperty("spring.kafka.properties.key.serializer"));
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class.getName());
                props.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("kafka.groups.aggregator"));

                consumer = new KafkaConsumer<>(props);

                consumer.subscribe(List.of(env.getProperty("kafka.topics.sensor-events")));
            }

            @Override
            public void stop() {
                if (consumer != null) {
                    consumer.close();
                }
            }
        };
    }
}