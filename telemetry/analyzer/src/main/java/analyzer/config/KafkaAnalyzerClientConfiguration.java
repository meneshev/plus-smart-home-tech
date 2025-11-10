package analyzer.config;

import analyzer.kafka.AnalyzerClient;
import kafka.HubEventDeserializer;
import kafka.SensorSnapshotDeserializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaAnalyzerClientConfiguration {
    private final Environment env;

    @Scope("prototype")
    @Bean
    AnalyzerClient kafkaAnalyzerClient() {
        return new AnalyzerClient() {
            private Consumer<Void, HubEventAvro> hubConsumer;
            private Consumer<Void, SensorsSnapshotAvro> snapshotConsumer;

            @Override
            public Consumer<Void, HubEventAvro> getHubConsumer() {
                if (hubConsumer != null) {
                    initHubConsumer();
                }

                return hubConsumer;
            }

            @Override
            public Consumer<Void, SensorsSnapshotAvro> getSnapshotConsumer() {
                if (snapshotConsumer != null) {
                    initSnapshotConsumer();
                }

                return snapshotConsumer;
            }

            private void initHubConsumer() {
                Properties props = new Properties();
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("spring.kafka.properties.bootstrap.servers"));
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, env.getProperty("spring.kafka.properties.key.deserializer"));
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class.getName());
                props.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("kafka.groups.analyzer-hub"));
                props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, env.getProperty("spring.kafka.properties.consumer.auto-commit"));

                hubConsumer = new KafkaConsumer<>(props);

                hubConsumer.subscribe(List.of(env.getProperty("kafka.topics.hub-events")));
            }

            private void initSnapshotConsumer() {
                Properties props = new Properties();
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("spring.kafka.properties.bootstrap.servers"));
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, env.getProperty("spring.kafka.properties.key.deserializer"));
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorSnapshotDeserializer.class.getName());
                props.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("kafka.groups.analyzer-snapshot"));
                props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, env.getProperty("spring.kafka.properties.consumer.auto-commit"));

                snapshotConsumer = new KafkaConsumer<>(props);

                snapshotConsumer.subscribe(List.of(env.getProperty("kafka.topics.events-snapshots")));
            }

            @Override
            public void stop() {
                if (hubConsumer != null) {
                    hubConsumer.close();
                }

                if (snapshotConsumer != null) {
                    snapshotConsumer.close();
                }
            }
        };
    }
}