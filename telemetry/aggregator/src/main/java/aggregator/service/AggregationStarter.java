package aggregator.service;

import aggregator.kafka.AggregatorClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final Environment env;
    private final AggregatorClient client;

    private static final Duration CONSUMER_TIMEOUT = Duration.ofMillis(500);
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private static final Map<String, SensorsSnapshotAvro> hubSnapshots = new HashMap<>();

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(client.getConsumer()::wakeup));
        try {
            while (true) {
                List<SensorsSnapshotAvro> sensorSnapshots = processRecords(client.getConsumer().poll(CONSUMER_TIMEOUT));
                if (!sensorSnapshots.isEmpty()) {
                    sensorSnapshots.forEach(snapshot -> {
                        log.info("Sending snapshot: {}", snapshot);
                        client.getProducer().send(
                                new ProducerRecord<>(env.getProperty("kafka.topics.events-snapshots"), null, snapshot));
                    });
                }
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Error during process 'telemetry.sensors.v1'", e);
        } finally {
            try {
                client.getProducer().flush();
                client.getConsumer().commitSync(currentOffsets);
            } finally {
                log.info("Closing aggregator consumer and producer...");
                client.stop();
            }
        }
    }

    private List<SensorsSnapshotAvro> processRecords(ConsumerRecords<Void, SensorEventAvro> records) {
        int recordsConsumed = 0;
        List<SensorsSnapshotAvro> snapshots = new ArrayList<>();

        for (ConsumerRecord<Void, SensorEventAvro> record : records) {
            log.info("Processing record - topic:[{}] partition:[{}] offset:[{}] value: {}",
                    record.topic(), record.partition(), record.offset(), record.value());

            updateState(record.value())
                    .ifPresent(snapshots::add);


            commitOffsets(record, recordsConsumed, (KafkaConsumer<Void, SensorEventAvro>) client.getConsumer());
            recordsConsumed++;
        }
        client.getConsumer().commitAsync();
        return snapshots;
    }

    private Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        if (!hubSnapshots.containsKey(event.getHubId())) {
            Map<String, SensorStateAvro> sensorState = new HashMap<>();
            sensorState.put(
                    event.getId(),
                    SensorStateAvro.newBuilder()
                            .setTimestamp(event.getTimestamp())
                            .setData(event.getPayload())
                            .build()
            );

            SensorsSnapshotAvro snapshot = SensorsSnapshotAvro.newBuilder()
                    .setHubId(event.getHubId())
                    .setTimestamp(event.getTimestamp())
                    .setSensorState(sensorState)
                    .build();

            hubSnapshots.put(event.getHubId(), snapshot);

            return Optional.of(snapshot);
        }

        if (!hubSnapshots.get(event.getHubId()).getSensorState().containsKey(event.getId())) {
            hubSnapshots.get(event.getHubId()).getSensorState().put(
                    event.getId(),
                    SensorStateAvro.newBuilder()
                        .setTimestamp(event.getTimestamp())
                        .setData(event.getPayload())
                        .build()
            );

            return Optional.of(hubSnapshots.get(event.getHubId()));
        }

        SensorStateAvro oldSensorState= hubSnapshots.get(event.getHubId())
                .getSensorState()
                .get(event.getId());

        if (event.getTimestamp().isBefore(oldSensorState.getTimestamp())
                || Objects.equals(event.getPayload(), oldSensorState.getData())) {
            return Optional.empty();
        } else {
            hubSnapshots.get(event.getHubId()).getSensorState().put(
                    event.getId(),
                    SensorStateAvro.newBuilder()
                            .setTimestamp(event.getTimestamp())
                            .setData(event.getPayload())
                            .build()
            );
            return Optional.of(hubSnapshots.get(event.getHubId()));
        }
    }

    private void commitOffsets(ConsumerRecord<Void, SensorEventAvro> record,
                               int count,
                               KafkaConsumer<Void, SensorEventAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 5 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.error("Error during offset commit: {}", offsets, exception);
                }
            });
        }
    }
}