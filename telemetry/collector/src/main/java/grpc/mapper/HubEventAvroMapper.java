package grpc.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HubEventAvroMapper {

    public static HubEventAvro toDeviceAddedEventAvro(HubEventProto hubEventProto) {
        DeviceAddedEventAvro deviceAddedEventAvro = DeviceAddedEventAvro.newBuilder()
                .setId(hubEventProto.getDeviceAdded().getId())
                .setType(DeviceTypeAvro.valueOf(hubEventProto.getDeviceAdded().getType().name()))
                .build();

        return toHubEventAvro(hubEventProto, deviceAddedEventAvro);
    }

    public static HubEventAvro toDeviceRemovedEventAvro(HubEventProto hubEventProto) {

        DeviceRemovedEventAvro deviceRemovedEventAvro = DeviceRemovedEventAvro.newBuilder()
                .setId(hubEventProto.getDeviceRemoved().getId())
                .build();

        return toHubEventAvro(hubEventProto, deviceRemovedEventAvro);
    }

    public static HubEventAvro toScenarioAddedEventAvro(HubEventProto hubEventProto) {

        ScenarioAddedEventAvro scenarioAddedEventAvro = ScenarioAddedEventAvro.newBuilder()
                .setName(hubEventProto.getScenarioAdded().getName())
                .setActions(
                        hubEventProto.getScenarioAdded().getActionList().stream()
                                .map(deviceAction -> DeviceActionAvro.newBuilder()
                                        .setSensorId(deviceAction.getSensorId())
                                        .setType(ActionTypeAvro.valueOf(deviceAction.getType().name()))
                                        .setValue(deviceAction.getValue())
                                        .build())
                                .toList()
                )
                .setConditions(
                        hubEventProto.getScenarioAdded().getConditionList().stream()
                                .map(condition -> ScenarioConditionAvro.newBuilder()
                                        .setSensorId(condition.getSensorId())
                                        .setType(CondiionTypeAvro.valueOf(condition.getType().name()))
                                        .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                                        .setValue(
                                            switch (condition.getValueCase()) {
                                                case BOOL_VALUE -> condition.getBoolValue();
                                                case INT_VALUE -> condition.getIntValue();
                                                default -> throw new RuntimeException("Unknown scenario value");
                                            }
                                        )
                                        .build()
                                )
                                .toList()
                )
                .build();

        return toHubEventAvro(hubEventProto, scenarioAddedEventAvro);
    }

    public static HubEventAvro toScenarioRemovedEventAvro(HubEventProto hubEventProto) {

        ScenarioRemovedEventAvro scenarioRemovedEventAvro = ScenarioRemovedEventAvro.newBuilder()
                .setName(hubEventProto.getScenarioRemoved().getName())
                .build();

        return toHubEventAvro(hubEventProto, scenarioRemovedEventAvro);
    }

    private static HubEventAvro toHubEventAvro(HubEventProto hubEventProto, Object payload) {
        return HubEventAvro.newBuilder()
                .setHubId(hubEventProto.getHubId())
                .setTimestamp(Instant.ofEpochSecond(
                        hubEventProto.getTimestamp().getSeconds(),
                        hubEventProto.getTimestamp().getNanos()
                ))
                .setPayload(payload)
                .build();
    }
}
