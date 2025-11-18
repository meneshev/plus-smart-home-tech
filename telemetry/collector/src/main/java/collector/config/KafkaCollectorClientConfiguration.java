package collector.config;

import collector.kafka.CollectorClient;
import config.KafkaProperties;
import kafka.SmartHomeTechAvroSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaCollectorClientConfiguration {
    private final KafkaProperties kafkaProps;

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
                props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProps.getBootstrapServers());
                props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProps.getKeySerializer());
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SmartHomeTechAvroSerializer.class.getName());
                props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProps.getProducer().getLingerMs());
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