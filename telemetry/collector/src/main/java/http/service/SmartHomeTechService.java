package http.service;

import http.model.event.SensorEvent;
import http.model.hub.HubEvent;

public interface SmartHomeTechService {
    void sendToQueue(HubEvent event);

    void sendToQueue(SensorEvent event);
}
