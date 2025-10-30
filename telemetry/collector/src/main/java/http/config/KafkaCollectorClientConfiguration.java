package http.config;

import http.kafka.CollectorClient;
import http.kafka.SmartHomeTechAvroSerializer;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.VoidSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Properties;

@Configuration
public class KafkaCollectorClientConfiguration {

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
                props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class.getName());
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SmartHomeTechAvroSerializer.class.getName());

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
