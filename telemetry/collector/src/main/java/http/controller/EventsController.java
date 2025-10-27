package http.controller;

import lombok.extern.slf4j.Slf4j;
import http.model.event.SensorEvent;
import http.model.hub.HubEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/events")
public class EventsController {

    @PostMapping("/hubs")
    public void processHubEvent(HubEvent event) {
        log.info("HubEvent received: " + event.toString());
    }

    @PostMapping("/sensors")
    public void processSensorEvent(SensorEvent event) {
        log.info("SensorEvent received: " + event.toString());
    }
}

