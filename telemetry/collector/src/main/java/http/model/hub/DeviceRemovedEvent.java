package http.model.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceRemovedEvent extends HubEvent {
    private String id;
    private Instant timestamp = Instant.now();

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
