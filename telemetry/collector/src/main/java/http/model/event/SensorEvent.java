package http.model.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "sensorType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClimateSensorEvent.class, name = "climateSensorEvent"),
        @JsonSubTypes.Type(value = LightSensorEvent.class, name = "lightSensorEvent"),
        @JsonSubTypes.Type(value = MotionSensorEvent.class, name = "motionSensorEvent"),
        @JsonSubTypes.Type(value = SwitchSensorEvent.class, name = "switchSensorEvent"),
        @JsonSubTypes.Type(value = TemperatureSensorEvent.class, name = "temperatureSensorEvent")
})
@Getter @Setter @ToString
public abstract class SensorEvent {
    private String id;
    private String hubId;
    private Instant timestamp = Instant.now();

    public abstract SensorEventType getType();
}