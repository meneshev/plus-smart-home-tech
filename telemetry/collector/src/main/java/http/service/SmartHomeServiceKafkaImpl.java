package http.service;

import http.kafka.CollectorClient;
import http.mapper.HubEventAvroMapper;
import http.mapper.SensorEventAvroMapper;
import http.model.event.SensorEvent;
import http.model.hub.HubEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartHomeServiceKafkaImpl implements SmartHomeTechService {
    private final Environment env;
    private final CollectorClient collectorClient;

    @Override
    public void sendToQueue(HubEvent event) {
        collectorClient.getProducer().send(
                new ProducerRecord<>(env.getProperty("kafka.topics.hub-events"), null, eventToAvro(event))
        );
        log.info("Sent to queue with event {}", eventToAvro(event));
    }

    @Override
    public void sendToQueue(SensorEvent event) {
        collectorClient.getProducer().send(
                new ProducerRecord<>(env.getProperty("kafka.topics.sensor-events"), null, eventToAvro(event))
        );
        log.info("Sent to queue with event {}", eventToAvro(event));
    }

    private HubEventAvro eventToAvro(HubEvent event) {
        switch (event.getType()) {
            case DEVICE_ADDED -> {
                return HubEventAvroMapper.toDeviceAddedEventAvro(event);
            }
            case DEVICE_REMOVED -> {
                return HubEventAvroMapper.toDeviceRemovedEventAvro(event);
            }
            case SCENARIO_ADDED ->  {
                return HubEventAvroMapper.toScenarioAddedEventAvro(event);
            }
            case SCENARIO_REMOVED ->  {
                return HubEventAvroMapper.toScenarioRemovedEventAvro(event);
            }
            default -> throw new RuntimeException("Unknown event type");
        }
    }

    private SensorEventAvro eventToAvro(SensorEvent event) {
        switch (event.getType()) {
            case MOTION_SENSOR_EVENT -> {
                return SensorEventAvroMapper.toMotionSensorAvro(event);
            }
            case TEMPERATURE_SENSOR_EVENT -> {
                return SensorEventAvroMapper.toTemperatureSensorAvro(event);
            }
            case LIGHT_SENSOR_EVENT -> {
                return SensorEventAvroMapper.toLightSensorAvro(event);
            }
            case CLIMATE_SENSOR_EVENT ->  {
                return SensorEventAvroMapper.toClimateSensorAvro(event);
            }
            case SWITCH_SENSOR_EVENT ->   {
                return SensorEventAvroMapper.toSwitchSensorAvro(event);
            }
            default -> throw new RuntimeException("Unknown event type");
        }
    }
}
