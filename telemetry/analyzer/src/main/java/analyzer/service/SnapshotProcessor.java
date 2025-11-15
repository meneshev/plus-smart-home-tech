package analyzer.service;

import analyzer.dal.service.SnapshotAnalyzer;
import analyzer.kafka.AnalyzerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {
    private final Environment env;
    private final AnalyzerClient client;
    private final SnapshotAnalyzer snapshotAnalyzer;

    private static final Duration CONSUMER_TIMEOUT = Duration.ofMillis(500);
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(client.getHubConsumer()::wakeup));
        try {
            while (true) {
                log.info("Getting snapshots...");
                processRecords(client.getSnapshotConsumer().poll(CONSUMER_TIMEOUT));
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Error during process 'telemetry.snapshots.v1'", e);
        } finally {
            try {
                client.getHubConsumer().commitSync(currentOffsets);
            } finally {
                log.info("Closing analyzer hub consumer...");
                client.stop();
            }
        }
    }

    private void processRecords(ConsumerRecords<Void, SensorsSnapshotAvro> snapshots) {
        for (ConsumerRecord<Void, SensorsSnapshotAvro> snapshot : snapshots) {
            log.info("Processing record - topic:[{}] partition:[{}] offset:[{}] value: {}",
                    snapshot.topic(), snapshot.partition(), snapshot.offset(), snapshot.value());
            if (snapshotAnalyzer.processSnapshot(snapshot.value())) {
                client.getSnapshotConsumer().commitSync();
            }
        }
        // коммит только после успешной отправки действия (или сохранения)

    }
}
