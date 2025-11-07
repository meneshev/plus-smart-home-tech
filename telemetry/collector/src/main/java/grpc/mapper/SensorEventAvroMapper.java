package grpc.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SensorEventAvroMapper {

    public static SensorEventAvro toClimateSensorAvro(final SensorEventProto sensorEventProto) {

        ClimateSensorAvro climateSensorAvro = ClimateSensorAvro.newBuilder()
                .setTemperatureC(sensorEventProto.getClimateSensor().getTemperatureC())
                .setHumidity(sensorEventProto.getClimateSensor().getHumidity())
                .setCo2Level(sensorEventProto.getClimateSensor().getCo2Level())
                .build();

        return toSensorEventAvro(sensorEventProto, climateSensorAvro);
    }

    public static SensorEventAvro toLightSensorAvro(final SensorEventProto sensorEventProto) {

        LightSensorAvro lightSensorAvro = LightSensorAvro.newBuilder()
                .setLinkQuality(sensorEventProto.getLightSensor().getLinkQuality())
                .setLuminosity(sensorEventProto.getLightSensor().getLuminosity())
                .build();

        return toSensorEventAvro(sensorEventProto, lightSensorAvro);
    }

    public static SensorEventAvro toMotionSensorAvro(final SensorEventProto sensorEventProto) {

        MotionSensorAvro motionSensorAvro = MotionSensorAvro.newBuilder()
                .setLinkQuality(sensorEventProto.getMotionSensor().getLinkQuality())
                .setMotion(sensorEventProto.getMotionSensor().getMotion())
                .setVoltage(sensorEventProto.getMotionSensor().getVoltage())
                .build();

        return toSensorEventAvro(sensorEventProto, motionSensorAvro);
    }

    public static SensorEventAvro toSwitchSensorAvro(final SensorEventProto sensorEventProto) {

        SwitchSensorAvro switchSensorAvro = SwitchSensorAvro.newBuilder()
                .setState(sensorEventProto.getSwitchSensor().getState())
                .build();

        return toSensorEventAvro(sensorEventProto, switchSensorAvro);
    }

    public static SensorEventAvro toTemperatureSensorAvro(final SensorEventProto sensorEventProto) {

        TemperatureSensorAvro temperatureSensorAvro = TemperatureSensorAvro.newBuilder()
                .setTemperatureC(sensorEventProto.getTemperatureSensor().getTemperatureC())
                .setTemperatureF(sensorEventProto.getTemperatureSensor().getTemperatureF())
                .build();

        return toSensorEventAvro(sensorEventProto, temperatureSensorAvro);
    }

    private static SensorEventAvro toSensorEventAvro(final SensorEventProto sensorEventProto, Object payload) {
        return SensorEventAvro.newBuilder()
                .setId(sensorEventProto.getId())
                .setHubId(sensorEventProto.getHubId())
                .setTimestamp(Instant.ofEpochSecond(
                        sensorEventProto.getTimestamp().getSeconds(),
                        sensorEventProto.getTimestamp().getNanos()
                ))
                .setPayload(payload)
                .build();
    }
}
