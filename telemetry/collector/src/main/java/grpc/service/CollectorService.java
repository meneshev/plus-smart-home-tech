package grpc.service;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface CollectorService {
    void sendToQueue(HubEventProto event);

    void sendToQueue(SensorEventProto event);
}