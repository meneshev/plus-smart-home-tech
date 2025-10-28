package http.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private Instant timestamp = Instant.now();
    @NotBlank
    private String name;
    @NotNull
    private List<ScenarioCondition> conditions = new ArrayList<>();
    @NotNull
    private List<DeviceAction> actions = new ArrayList<>();

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
