package controller;

import model.event.SensorEvent;
import model.hub.HubEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/events")
public class EventsController {

    @PostMapping("/hubs")
    public void processHubEvent(HubEvent event) {

    }

    @PostMapping("/sensors")
    public void processSensorEvent(SensorEvent event) {

    }
}

