package http.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import http.model.event.SensorEvent;
import http.model.hub.HubEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/events")
public class EventsController {
    private final ObjectMapper mapper;

    public EventsController(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @PostMapping("/hubs")
    public void processHubEvent(@RequestBody @Valid HubEvent event) throws JsonProcessingException {
        log.info("HubEvent received: " + event.toString());
        System.out.println(mapper.writeValueAsString(event));
    }

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> processSensorEvent(@RequestBody @Valid SensorEvent event) throws JsonProcessingException {
        log.info("SensorEvent received: " + event.toString());
        System.out.println("JSON: " + mapper.writeValueAsString(event));
        return ResponseEntity.ok().build();
    }
}

