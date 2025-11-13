package analyzer.dal.service;

import analyzer.dal.entity.Sensor;
import analyzer.dal.repository.ActionRepository;
import analyzer.dal.repository.ConditionRepository;
import analyzer.dal.repository.ScenarioRepository;
import analyzer.dal.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubDBService {
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;
    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;

    public boolean processHubEvent(HubEventAvro eventAvro) {
        try {
            switch (eventAvro.getPayload()) {
                case DeviceAddedEventAvro deviceAdded -> {
                    handleDeviceAdded(eventAvro);
                    return true;
                }
                case DeviceRemovedEventAvro deviceRemoved -> {
                    handleDeviceRemoved(eventAvro);
                    return true;
                }
                case ScenarioAddedEventAvro scenarioAdded -> {
                    handleScenarioAdded(eventAvro);
                    return true;
                }
                case ScenarioRemovedEventAvro scenarioRemoved -> {
                    handleScenarioRemoved(eventAvro);
                    return true;
                }
                default -> {
                    log.error("Unknown payload type: {}", eventAvro.getPayload());
                    throw new RuntimeException("Unknown payload type");
                }
            }
        } catch (Exception e) {
            log.error("Exception during process Hub Event: {}", e.getMessage());
            return false;
        }
    }

    private void handleDeviceAdded(HubEventAvro eventAvro) {
        DeviceAddedEventAvro addedEvent = (DeviceAddedEventAvro) eventAvro.getPayload();
        if (sensorRepository.existsByIdAndHubId(List.of(addedEvent.getId()), eventAvro.getHubId())) {
            throw new RuntimeException(String.format("Sensor: %s already exists", eventAvro));
        }

        Sensor newSensor = Sensor.builder()
                .id(addedEvent.getId())
                .hubId(eventAvro.getHubId())
                .build();

        sensorRepository.save(newSensor);
        log.info("New sensor: {} was saved to database", newSensor);
    }

    private void handleDeviceRemoved(HubEventAvro eventAvro) {
        DeviceRemovedEventAvro removedEvent = (DeviceRemovedEventAvro) eventAvro.getPayload();
        if (!sensorRepository.existsByIdAndHubId(List.of(removedEvent.getId()), eventAvro.getHubId())) {
            throw new RuntimeException(String.format("Sensor: %s is no exists", eventAvro));
        }
        scenarioRepository.findByHubId(eventAvro.getHubId()).stream()
                .filter(scenario -> scenario.getConditions().c)
    }

    private void handleScenarioAdded(HubEventAvro eventAvro) {

    }

    private void handleScenarioRemoved(HubEventAvro eventAvro) {

    }

}
