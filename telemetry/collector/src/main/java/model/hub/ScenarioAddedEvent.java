package model.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {
    private Instant timestamp;
    private String name;
    private List<ScenarioCondition> conditions = new ArrayList<>();
    private List<DeviceAction> actions = new ArrayList<>();

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
