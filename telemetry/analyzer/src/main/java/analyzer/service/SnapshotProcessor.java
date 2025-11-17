package analyzer.service;

import analyzer.dal.service.SnapshotAnalyzer;
import analyzer.kafka.AnalyzerSnapshotClient;
import config.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {
    private final KafkaProperties props;
    private final AnalyzerSnapshotClient client;
    private final SnapshotAnalyzer snapshotAnalyzer;

    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(client.getConsumer()::wakeup));
        try {
            while (true) {
                log.info("Getting snapshots...");
                processRecords(client.getConsumer().poll(props.getConsumer().getTimeoutMs()));
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Error during process topic {}", props.getTopics().getEventsSnapshots(), e);
        } finally {
            try {
                client.getConsumer().commitSync(currentOffsets);
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
                client.getConsumer().commitSync();
            }
        }
    }
}