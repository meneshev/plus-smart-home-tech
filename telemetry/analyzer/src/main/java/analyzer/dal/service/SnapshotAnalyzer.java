package analyzer.dal.service;

import analyzer.controller.HubRouterActionsSender;
import analyzer.dal.entity.Action;
import analyzer.dal.entity.Condition;
import analyzer.dal.entity.ConditionOperation;
import analyzer.dal.entity.Scenario;
import analyzer.dal.exception.NonConsistentDataException;
import analyzer.dal.repository.ScenarioRepository;
import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotAnalyzer {
    private final ScenarioRepository scenarioRepository;
    private final HubRouterActionsSender actionsSender;

    public boolean processSnapshot(SensorsSnapshotAvro snapshot) {
        boolean isSuccessfulProcessed = false;
        try {
            List<Scenario> hubScenarios = scenarioRepository.findByHubId(snapshot.getHubId());
            if (!hubScenarios.isEmpty()) {
                snapshot.getSensorState().forEach((sensorId, sensorState) -> {
                    hubScenarios.stream()
                            .filter(scenario -> (scenario.getConditions().get(sensorId) != null
                                    && scenario.getConditions().get(sensorId).getValue() != null)
                                        && scenario.getActions().get(sensorId) != null)
                            .forEach(
                            scenario -> {
                                Condition condition = scenario.getConditions().get(sensorId);

                                boolean isConditionPassed;
                                switch (sensorState.getData()) {
                                    case ClimateSensorAvro sensor -> isConditionPassed = processClimateSensor(sensor, condition);
                                    case TemperatureSensorAvro sensor -> isConditionPassed = processTemperatureSensor(sensor, condition);
                                    case LightSensorAvro sensor -> isConditionPassed = processLightSensor(sensor, condition);
                                    case MotionSensorAvro sensor -> isConditionPassed = processMotionSensor(sensor, condition);
                                    case SwitchSensorAvro sensor -> isConditionPassed = processSwitchSensor(sensor, condition);
                                    default -> throw new NonConsistentDataException(
                                            String.format("Unexpected sensor state type: %s",
                                                    sensorState.getData().getClass())
                                    );
                                }

                                if (isConditionPassed) {
                                    Action action = scenario.getActions().get(sensorId);
                                    DeviceActionRequest actionRequest;

                                    DeviceActionProto.Builder actionBuilder = DeviceActionProto.newBuilder()
                                            .setSensorId(sensorId)
                                            .setType(ActionTypeProto.valueOf(action.getType().name()));

                                    if (action.getValue() != null) {
                                        actionBuilder.setValue(action.getValue());
                                    }

                                    Instant now = Instant.now();

                                    actionRequest = DeviceActionRequest.newBuilder()
                                            .setHubId(snapshot.getHubId())
                                            .setScenarioName(scenario.getName())
                                            .setAction(actionBuilder.build())
                                            .setTimestamp(Timestamp.newBuilder()
                                                    .setSeconds(now.getEpochSecond())
                                                    .setNanos(now.getNano())
                                                    .build()
                                            )
                                            .build();

                                    actionsSender.sendAction(actionRequest);
                                }
                            }
                    );
                });
                isSuccessfulProcessed = true;
            }
        } catch (NonConsistentDataException e ) {
            log.error("Non consistent data during process Hub Event: {}", e.getMessage());
            return isSuccessfulProcessed = true;
        } catch (Exception e) {
            log.error("Error during snapshot processing: ", e);
            return isSuccessfulProcessed;
        }
        return isSuccessfulProcessed;
    }

    private boolean processClimateSensor(ClimateSensorAvro sensorState, Condition condition) {
        int valueToCheck;
        switch (condition.getType()) {
            case CO2LEVEL -> valueToCheck = sensorState.getCo2Level();
            case TEMPERATURE -> valueToCheck = sensorState.getTemperatureC();
            case HUMIDITY -> valueToCheck = sensorState.getHumidity();
            default -> throw new NonConsistentDataException(
                    String.format("Incorrect condition type: %s for sensor: %s",
                            condition.getType(), sensorState.getClass())
            );
        }
        return checkIntCondition(condition.getOperation(), condition.getValue(), valueToCheck);
    }

    private boolean processTemperatureSensor(TemperatureSensorAvro sensorState, Condition condition) {
        int valueToCheck;
        switch (condition.getType()) {
            case TEMPERATURE -> valueToCheck = sensorState.getTemperatureC();
            default -> throw new NonConsistentDataException(
                    String.format("Incorrect condition type: %s for sensor: %s",
                            condition.getType(), sensorState.getClass())
            );
        }
        return checkIntCondition(condition.getOperation(), condition.getValue(), valueToCheck);
    }

    private boolean processLightSensor(LightSensorAvro sensorState, Condition condition) {
        int valueToCheck;
        switch (condition.getType()) {
            case LUMINOSITY -> valueToCheck = sensorState.getLuminosity();
            default -> throw new NonConsistentDataException(
                    String.format("Incorrect condition type: %s for sensor: %s",
                            condition.getType(), sensorState.getClass())
            );
        }
        return checkIntCondition(condition.getOperation(), condition.getValue(), valueToCheck);
    }

    private boolean processMotionSensor(MotionSensorAvro sensorState, Condition condition) {
        switch (condition.getType()) {
            case MOTION -> {
                return checkBoolCondition(condition.getValue(), sensorState.getMotion());
            }
            default -> throw new NonConsistentDataException(
                    String.format("Incorrect condition type: %s for sensor: %s",
                            condition.getType(), sensorState.getClass())
            );
        }
    }

    private boolean processSwitchSensor(SwitchSensorAvro sensorState, Condition condition) {
        switch (condition.getType()) {
            case SWITCH -> {
                return checkBoolCondition(condition.getValue(), sensorState.getState());
            }
            default -> throw new NonConsistentDataException(
                    String.format("Incorrect condition type: %s for sensor: %s",
                            condition.getType(), sensorState.getClass())
            );
        }
    }

    private boolean checkIntCondition(ConditionOperation operation, int conditionValue, int currentValue) {
        switch (operation) {
            case EQUALS -> {
                return currentValue == conditionValue;
            }
            case GREATER_THAN -> {
                return currentValue > conditionValue;
            }
            case LOWER_THAN -> {
                return currentValue < conditionValue;
            }
            default -> throw new NonConsistentDataException(String.format("Unexpected condition operation: %s", operation));
        }
    }

    private boolean checkBoolCondition(int conditionValue, boolean currentValue) {
        boolean conditionBool;
        if (conditionValue == 0) {
            conditionBool = false;
        } else if (conditionValue == 1) {
            conditionBool = true;
        } else {
            throw new NonConsistentDataException(String.format("Can't convert %d to bool value", conditionValue));
        }
        return conditionBool == currentValue;
    }
}