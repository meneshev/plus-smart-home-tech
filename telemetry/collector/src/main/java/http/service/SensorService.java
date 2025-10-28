package http.service;

import http.model.event.SensorEvent;

public interface SensorService {
    void sendToQueue(SensorEvent event);
}
