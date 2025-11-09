package collector.config;

import collector.kafka.CollectorClient;
import collector.kafka.SmartHomeTechAvroSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaCollectorClientConfiguration {
    private final Environment env;

    @Scope("prototype")
    @Bean
    CollectorClient kafkaCollectorClient() {
        return new CollectorClient() {
            private Producer<Void, SpecificRecordBase> producer;

            @Override
            public Producer<Void, SpecificRecordBase> getProducer() {
                if (producer == null) {
                    initProducer();
                }
                return producer;
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
                if (producer != null) {
                    producer.close();
                }
            }
        };
    }
}