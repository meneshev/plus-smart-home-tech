package analyzer.config;

import analyzer.kafka.AnalyzerHubClient;
import kafka.HubEventDeserializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.List;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@ConfigurationProperties("kafka")
public class KafkaAnalyzerHubClientConfig {
    private final Environment env;

    @Scope("prototype")
    @Bean
    AnalyzerHubClient kafkaAnalyzerClient() {
        return new AnalyzerHubClient() {
            private Consumer<Void, HubEventAvro> hubConsumer;

            @Override
            public Consumer<Void, HubEventAvro> getConsumer() {
                if (hubConsumer != null) {
                    initHubConsumer();
                }

                return hubConsumer;
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

            @Override
            public void stop() {
                if (hubConsumer != null) {
                    hubConsumer.close();
                }
            }
        };
    }
}