package aggregator.config;

import aggregator.kafka.AggregatorClient;
import kafka.SensorEventDeserializer;
import kafka.SmartHomeTechAvroSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

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
            private Producer<Void, SensorsSnapshotAvro> producer;

            @Override
            public Consumer<Void, SensorEventAvro> getConsumer() {
                if (consumer == null) {
                    initConsumer();
                }
                return consumer;
            }

            @Override
            public Producer<Void, SensorsSnapshotAvro> getProducer() {
                if (producer == null) {
                    initProducer();
                }
                return producer;
            }

            private void initConsumer() {
                Properties props = new Properties();
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("spring.kafka.properties.bootstrap.servers"));
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, env.getProperty("spring.kafka.properties.key.deserializer"));
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class.getName());
                props.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("kafka.groups.aggregator"));
                props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, env.getProperty("spring.kafka.properties.consumer.auto-commit"));

                consumer = new KafkaConsumer<>(props);

                consumer.subscribe(List.of(env.getProperty("kafka.topics.sensor-events")));
            }

            private void initProducer() {
                Properties props = new Properties();
                props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("spring.kafka.properties.bootstrap.servers"));
                props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, env.getProperty("spring.kafka.properties.key.serializer"));
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SmartHomeTechAvroSerializer.class.getName());
                props.put(ProducerConfig.LINGER_MS_CONFIG, env.getProperty("spring.kafka.properties.producer.linger-ms"));

                producer = new KafkaProducer<>(props);
            }

            @Override
            public void stop() {
                if (consumer != null) {
                    consumer.close();
                }

                if (producer != null) {
                    producer.close();
                }
            }
        };
    }
}