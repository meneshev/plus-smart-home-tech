package http.service;

import http.model.event.SensorEvent;
import org.springframework.stereotype.Service;

@Service
public class SensorServiceKafkaImpl implements SensorService {
    @Override
    public void sendToQueue(SensorEvent event) {

    }
}
