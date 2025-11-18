package aggregator.config;

import aggregator.kafka.AggregatorClient;
import config.KafkaProperties;
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
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaAggregatorClientConfiguration {
    private final KafkaProperties kafkaProps;

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
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProps.getBootstrapServers());
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProps.getKeyDeserializer());
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class.getName());
                props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProps.getConsumer().getGroups().getAggregator());
                props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProps.getConsumer().isAutoCommit());

                consumer = new KafkaConsumer<>(props);

                consumer.subscribe(List.of(kafkaProps.getTopics().getSensorEvents()));
            }

            private void initProducer() {
                Properties props = new Properties();
                props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProps.getBootstrapServers());
                props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProps.getKeySerializer());
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SmartHomeTechAvroSerializer.class.getName());
                props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProps.getProducer().getLingerMs());

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