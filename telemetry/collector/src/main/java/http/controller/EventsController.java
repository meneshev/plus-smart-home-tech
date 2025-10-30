package http.controller;

import http.service.SmartHomeTechService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import http.model.event.SensorEvent;
import http.model.hub.HubEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventsController {
    private final SmartHomeTechService smartHomeTechService;

    @PostMapping("/hubs")
    public ResponseEntity<Void> processHubEvent(@RequestBody @Valid HubEvent event) {
        log.info("HubEvent received: " + event.toString());
        smartHomeTechService.sendToQueue(event);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sensors")
    public ResponseEntity<Void> processSensorEvent(@RequestBody @Valid SensorEvent event) {
        log.info("SensorEvent received: " + event.toString());
        smartHomeTechService.sendToQueue(event);
        return ResponseEntity.ok().build();
    }
}

