package model.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class HubEvent {
    private String hubId;

    public abstract HubEventType getType();
}
