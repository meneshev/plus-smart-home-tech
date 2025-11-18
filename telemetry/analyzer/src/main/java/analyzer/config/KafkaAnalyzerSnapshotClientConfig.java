package analyzer.config;

import analyzer.kafka.AnalyzerSnapshotClient;
import config.KafkaProperties;
import kafka.SensorSnapshotDeserializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaAnalyzerSnapshotClientConfig {
    private final KafkaProperties kafkaProps;

    @Scope("prototype")
    @Bean
    AnalyzerSnapshotClient kafkaAnalyzerSnapshotClient() {
        return new AnalyzerSnapshotClient() {
            private Consumer<Void, SensorsSnapshotAvro> consumer;

            @Override
            public Consumer<Void, SensorsSnapshotAvro> getConsumer() {
                if (consumer == null) {
                    initSnapshotConsumer();
                }

                return consumer;
            }

            private void initSnapshotConsumer() {
                Properties props = new Properties();
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProps.getBootstrapServers());
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProps.getKeyDeserializer());
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorSnapshotDeserializer.class.getName());
                props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProps.getConsumer().getGroups().getAnalyzerSnapshot());
                props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProps.getConsumer().isAutoCommit());

                consumer = new KafkaConsumer<>(props);

                consumer.subscribe(List.of(kafkaProps.getTopics().getEventsSnapshots()));
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