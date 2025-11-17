package analyzer.config;

import analyzer.kafka.AnalyzerHubClient;
import config.KafkaProperties;
import kafka.HubEventDeserializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.List;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaAnalyzerHubClientConfig {
    private final KafkaProperties kafkaProps;

    @Scope("prototype")
    @Bean
    AnalyzerHubClient kafkaAnalyzerHubClient() {
        return new AnalyzerHubClient() {
            private Consumer<Void, HubEventAvro> hubConsumer;

            @Override
            public Consumer<Void, HubEventAvro> getConsumer() {
                if (hubConsumer == null) {
                    initHubConsumer();
                }

                return hubConsumer;
            }

            private void initHubConsumer() {
                Properties props = new Properties();
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProps.getBootstrapServers());
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProps.getKeyDeserializer());
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class.getName());
                props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProps.getConsumer().getGroups().getAnalyzerHub());
                props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProps.getConsumer().isAutoCommit());

                hubConsumer = new KafkaConsumer<>(props);

                hubConsumer.subscribe(List.of(kafkaProps.getTopics().getHubEvents()));
            }

            @Override
            public void stop() {
                if (hubConsumer != null) {
                    hubConsumer.close();
                }
            }
        };
    }
}