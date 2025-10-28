package http.service;


import http.model.hub.HubEvent;
import org.springframework.stereotype.Service;

@Service
public class HubServiceKafkaImpl implements HubService {
    @Override
    public void sendToQueue(HubEvent event) {

    }
}
