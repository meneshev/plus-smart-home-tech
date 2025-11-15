package analyzer.service;

import analyzer.dal.service.HubDBService;
import analyzer.kafka.AnalyzerHubClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final Environment env;
    private final AnalyzerHubClient client;
    private final HubDBService hubDBService;

    private static final Duration CONSUMER_TIMEOUT = Duration.ofMillis(500);
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(client.getConsumer()::wakeup));
        try {
            while (true) {
                log.info("Getting hub events...");
                processRecords(client.getConsumer().poll(CONSUMER_TIMEOUT));
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Error during process topic {}", env.getProperty("kafka.topics.hub-events"), e);
        } finally {
            try {
                client.getConsumer().commitSync(currentOffsets);
            } finally {
                log.info("Closing analyzer hub consumer...");
                client.stop();
            }
        }
    }

    private void processRecords(ConsumerRecords<Void, HubEventAvro> events) {
        for (ConsumerRecord<Void, HubEventAvro> event : events) {
            log.info("Processing record - topic:[{}] partition:[{}] offset:[{}] value: {}",
                    event.topic(), event.partition(), event.offset(), event.value());

            if (hubDBService.processHubEvent(event.value())) {
                currentOffsets.put(
                        new TopicPartition(event.topic(), event.partition()),
                        new OffsetAndMetadata(event.offset() + 1)
                );
                client.getConsumer().commitSync(currentOffsets);
            }
            client.getConsumer().commitAsync();
        }
    }
}