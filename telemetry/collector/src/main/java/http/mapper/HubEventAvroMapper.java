package http.mapper;

import http.model.hub.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.kafka.telemetry.event.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HubEventAvroMapper {

    public static HubEventAvro toDeviceAddedEventAvro(HubEvent hubEvent) {
        DeviceAddedEvent deviceAddedEvent = (DeviceAddedEvent) hubEvent;

        DeviceAddedEventAvro deviceAddedEventAvro = DeviceAddedEventAvro.newBuilder()
                .setId(deviceAddedEvent.getId())
                .setType(DeviceTypeAvro.valueOf(deviceAddedEvent.getDeviceType().name()))
                .build();

        return toHubEventAvro(hubEvent, deviceAddedEventAvro);
    }

    public static HubEventAvro toDeviceRemovedEventAvro(HubEvent hubEvent) {
        DeviceRemovedEvent deviceRemovedEvent = (DeviceRemovedEvent) hubEvent;

        DeviceRemovedEventAvro deviceRemovedEventAvro = DeviceRemovedEventAvro.newBuilder()
                .setId(deviceRemovedEvent.getId())
                .build();

        return toHubEventAvro(hubEvent, deviceRemovedEventAvro);
    }

    public static HubEventAvro toScenarioAddedEventAvro(HubEvent hubEvent) {
        ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) hubEvent;

        ScenarioAddedEventAvro scenarioAddedEventAvro = ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName())
                .setActions(
                        scenarioAddedEvent.getActions().stream()
                                .map(deviceAction -> DeviceActionAvro.newBuilder()
                                        .setSensorId(deviceAction.getSensorId())
                                        .setType(ActionTypeAvro.valueOf(deviceAction.getType().name()))
                                        .setValue(deviceAction.getValue())
                                        .build())
                                .toList()
                )
                .setConditions(
                        scenarioAddedEvent.getConditions().stream()
                                .map(condition -> ScenarioConditionAvro.newBuilder()
                                        .setSensorId(condition.getSensorId())
                                        .setType(CondiionTypeAvro.valueOf(condition.getType().name()))
                                        .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                                        .setValue(condition.getValue())
                                        .build()
                                )
                                .toList()
                )
                .build();

        return toHubEventAvro(hubEvent, scenarioAddedEventAvro);
    }

    public static HubEventAvro toScenarioRemovedEventAvro(HubEvent hubEvent) {
        ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) hubEvent;

        ScenarioRemovedEventAvro scenarioRemovedEventAvro = ScenarioRemovedEventAvro.newBuilder()
                .setName(scenarioRemovedEvent.getName())
                .build();

        return toHubEventAvro(hubEvent, scenarioRemovedEventAvro);
    }

    private static HubEventAvro toHubEventAvro(HubEvent hubEvent, Object payload) {
        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(payload)
                .build();
    }
}
