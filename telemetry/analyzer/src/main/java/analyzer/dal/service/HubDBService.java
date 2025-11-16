package analyzer.dal.service;

import analyzer.dal.entity.*;
import analyzer.dal.exception.NonConsistentDataException;
import analyzer.dal.repository.ActionRepository;
import analyzer.dal.repository.ConditionRepository;
import analyzer.dal.repository.ScenarioRepository;
import analyzer.dal.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HubDBService {
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;
    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;

    public boolean processHubEvent(HubEventAvro eventAvro) {
        boolean isRecordProcessed = false;
        try {
            switch (eventAvro.getPayload()) {
                case DeviceAddedEventAvro deviceAdded -> handleDeviceAdded(eventAvro);
                case DeviceRemovedEventAvro deviceRemoved -> handleDeviceRemoved(eventAvro);
                case ScenarioAddedEventAvro scenarioAdded -> handleScenarioAdded(eventAvro);
                case ScenarioRemovedEventAvro scenarioRemoved -> handleScenarioRemoved(eventAvro);
                default -> {
                    log.error("Unknown payload type: {}", eventAvro.getPayload());
                    throw new RuntimeException("Unknown payload type");
                }
            }
        } catch (NonConsistentDataException e) {
            log.error("Non consistent data during process Hub Event: {}", e.getMessage());
            return isRecordProcessed = true;
        } catch (Exception e) {
            log.error("Exception during process Hub Event: {}", e.getMessage());
            return isRecordProcessed;
        }
        return isRecordProcessed = true;
    }

    private void handleDeviceAdded(HubEventAvro eventAvro) {
        DeviceAddedEventAvro addedEvent = (DeviceAddedEventAvro) eventAvro.getPayload();
        if (sensorRepository.existsByIdAndHubId(addedEvent.getId(), eventAvro.getHubId())) {
            throw new NonConsistentDataException(String.format("Sensor: %s already exists", eventAvro));
        }

        Sensor newSensor = Sensor.builder()
                .id(addedEvent.getId())
                .hubId(eventAvro.getHubId())
                .build();

        newSensor = sensorRepository.save(newSensor);
        log.info("New sensor: {} was saved to database", newSensor);
    }

    private void handleDeviceRemoved(HubEventAvro eventAvro) {
        DeviceRemovedEventAvro removedEvent = (DeviceRemovedEventAvro) eventAvro.getPayload();
        if (!sensorRepository.existsByIdAndHubId(removedEvent.getId(), eventAvro.getHubId())) {
            throw new NonConsistentDataException(String.format("Sensor: %s is no exists", eventAvro));
        }
        scenarioRepository.findByHubId(eventAvro.getHubId()).stream()
                .filter(scenario -> scenario.getConditions().containsKey(removedEvent.getId())
                        || scenario.getActions().containsKey(removedEvent.getId()))
                .forEach(scenario -> {
                    scenario.getConditions().remove(removedEvent.getId());
                    scenario.getActions().remove(removedEvent.getId());
                    scenarioRepository.delete(scenario);
                });

        Sensor sensorToDelete = Sensor.builder()
                .id(removedEvent.getId())
                .hubId(eventAvro.getHubId())
                .build();
        sensorRepository.delete(sensorToDelete);
        log.info("Sensor: {} was deleted", sensorToDelete);
    }

    private void handleScenarioAdded(HubEventAvro eventAvro) {
        ScenarioAddedEventAvro addedEvent = (ScenarioAddedEventAvro) eventAvro.getPayload();
        if (scenarioRepository.findByHubIdAndName(eventAvro.getHubId(), addedEvent.getName()).isPresent()) {
            throw new NonConsistentDataException(String.format("Scenario: %s is already exists", eventAvro));
        }

        Map<String, Condition> newConditions = addedEvent.getConditions().stream()
                .collect(
                        HashMap::new,
                        (map, conditionAvro) -> {
                            Condition newCnd = Condition.builder()
                                    .type(conditionAvro.getType())
                                    .operation(ConditionOperation.valueOf(conditionAvro.getOperation().name()))
                                    .build();

                            if (conditionAvro.getValue() != null) {
                                Integer value = switch (newCnd.getType()) {
                                    case MOTION, SWITCH -> conditionAvro.getValue().equals(true) ? 1 : 0;
                                    default -> (Integer) conditionAvro.getValue();
                                };
                                newCnd.setValue(value);
                            }

                            newCnd = conditionRepository.save(newCnd);

                            map.put(conditionAvro.getSensorId(), newCnd);
                        },
                        Map::putAll
                );

        Map<String, Action> newActions = addedEvent.getActions().stream()
                .collect(HashMap::new,
                        (map, actionAvro) -> {
                            Action newAct = Action.builder()
                                    .type(actionAvro.getType())
                                    .build();

                            if (actionAvro.getValue() != null) {
                                newAct.setValue(actionAvro.getValue());
                            }

                            newAct = actionRepository.save(newAct);

                            map.put(actionAvro.getSensorId(), newAct);
                        },
                        Map::putAll
                );

        Scenario newScenario = Scenario.builder()
                .hubId(eventAvro.getHubId())
                .name(addedEvent.getName())
                .conditions(newConditions)
                .actions(newActions)
                .build();

        newScenario = scenarioRepository.save(newScenario);
        log.info("New scenario: {} was saved to database", newScenario);
    }

    private void handleScenarioRemoved(HubEventAvro eventAvro) {
        ScenarioRemovedEventAvro scenarioRemoved = (ScenarioRemovedEventAvro) eventAvro.getPayload();
        if (scenarioRepository.findByHubIdAndName(eventAvro.getHubId(), scenarioRemoved.getName()).isEmpty()) {
            throw new NonConsistentDataException(String.format("Scenario: %s is not  exists", eventAvro));
        }

        scenarioRepository.findByHubIdAndName(eventAvro.getHubId(), scenarioRemoved.getName()).stream()
                        .forEach(scenario -> {
                            scenario.getConditions().clear();
                            scenario.getActions().clear();
                            scenarioRepository.delete(scenario);
                        });
    }
}