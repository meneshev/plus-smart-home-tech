package http.service;

import http.model.hub.HubEvent;

public interface HubService {
    void sendToQueue(HubEvent event);
}
