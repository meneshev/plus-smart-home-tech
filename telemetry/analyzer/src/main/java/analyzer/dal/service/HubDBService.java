package analyzer.dal.service;

import analyzer.dal.entity.Action;
import analyzer.dal.entity.Condition;
import analyzer.dal.entity.Scenario;
import analyzer.dal.entity.Sensor;
import analyzer.dal.repository.ActionRepository;
import analyzer.dal.repository.ConditionRepository;
import analyzer.dal.repository.ScenarioRepository;
import analyzer.dal.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubDBService {
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;
    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;

    public boolean processHubEvent(HubEventAvro eventAvro) {
        boolean isSuccess;
        try {
            switch (eventAvro.getPayload()) {
                case DeviceAddedEventAvro deviceAdded -> {
                    isSuccess = handleDeviceAdded(eventAvro);
                }
                case DeviceRemovedEventAvro deviceRemoved -> {
                    isSuccess = handleDeviceRemoved(eventAvro);
                }
                case ScenarioAddedEventAvro scenarioAdded -> {
                    isSuccess = handleScenarioAdded(eventAvro);
                }
                case ScenarioRemovedEventAvro scenarioRemoved -> {
                    isSuccess = handleScenarioRemoved(eventAvro);
                }
                default -> {
                    log.error("Unknown payload type: {}", eventAvro.getPayload());
                    throw new RuntimeException("Unknown payload type");
                }
            }
        } catch (Exception e) {
            log.error("Exception during process Hub Event: {}", e.getMessage());
            isSuccess = false;
        }
        return isSuccess;
    }

    private boolean handleDeviceAdded(HubEventAvro eventAvro) {
        DeviceAddedEventAvro addedEvent = (DeviceAddedEventAvro) eventAvro.getPayload();
        if (sensorRepository.existsByIdAndHubId(List.of(addedEvent.getId()), eventAvro.getHubId())) {
            throw new RuntimeException(String.format("Sensor: %s already exists", eventAvro));
        }

        Sensor newSensor = Sensor.builder()
                .id(addedEvent.getId())
                .hubId(eventAvro.getHubId())
                .build();

        newSensor = sensorRepository.save(newSensor);
        log.info("New sensor: {} was saved to database", newSensor);
        return true;
    }

    private boolean handleDeviceRemoved(HubEventAvro eventAvro) {
        DeviceRemovedEventAvro removedEvent = (DeviceRemovedEventAvro) eventAvro.getPayload();
        if (!sensorRepository.existsByIdAndHubId(List.of(removedEvent.getId()), eventAvro.getHubId())) {
            throw new RuntimeException(String.format("Sensor: %s is no exists", eventAvro));
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

        return true;
    }

    private boolean handleScenarioAdded(HubEventAvro eventAvro) {
        ScenarioAddedEventAvro addedEvent = (ScenarioAddedEventAvro) eventAvro.getPayload();
        if (scenarioRepository.findByHubIdAndName(eventAvro.getHubId(), addedEvent.getName()).isPresent()) {
            throw new RuntimeException(String.format("Scenario: %s is already exists", eventAvro));
        }

//        Map<String, Condition> newConditions = addedEvent.getConditions().stream()
//                .map(condition -> {
//                    Condition newCnd = Condition.builder()
//                            .type(condition.getType().name())
//                            .operation(condition.getOperation().name())
//                            .build();
//
//                    if (condition.getValue() != null) {
//                        newCnd.setValue((Integer) condition.getValue());
//                    }
//
//                    return conditionRepository.save(newCnd);
//                })
//                .collect(Collectors.toMap());

        Map<String, Condition> newConditions = addedEvent.getConditions().stream()
                .collect(
                        HashMap::new,
                        (map, conditionAvro) -> {
                            Condition newCnd = Condition.builder()
                                    .type(conditionAvro.getType().name())
                                    .operation(conditionAvro.getOperation().name())
                                    .build();

                            if (conditionAvro.getValue() != null) {
                                newCnd.setValue((Integer) conditionAvro.getValue());
                            }

                            newCnd = conditionRepository.save(newCnd);

                            map.put(conditionAvro.getSensorId(), newCnd);
                        },
                        Map::putAll
                );

//        List<Action> newActions = addedEvent.getActions().stream()
//                .map(action -> {
//                    Action newAct = Action.builder()
//                            .type(action.getType().name())
//                            .build();
//
//                    if (action.getValue() != null) {
//                        newAct.setValue(action.getValue());
//                    }
//
//                    return actionRepository.save(newAct);
//                })
//                .toList();

        Map<String, Action> newActions = addedEvent.getActions().stream()
                .collect(HashMap::new,
                        (map, actionAvro) -> {
                            Action newAct = Action.builder()
                                    .type(actionAvro.getType().name())
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

        return true;
    }

    private boolean handleScenarioRemoved(HubEventAvro eventAvro) {
        ScenarioRemovedEventAvro scenarioRemoved = (ScenarioRemovedEventAvro) eventAvro.getPayload();
        if (scenarioRepository.findByHubIdAndName(eventAvro.getHubId(), scenarioRemoved.getName()).isEmpty()) {
            throw new RuntimeException(String.format("Scenario: %s is not  exists", eventAvro));
        }

        scenarioRepository.findByHubIdAndName(eventAvro.getHubId(), scenarioRemoved.getName()).stream()
                        .forEach(scenario -> {
                            scenario.getConditions().clear();
                            scenario.getActions().clear();
                            scenarioRepository.delete(scenario);
                        });

        return true;
    }
}
