package analyzer.controller;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;

import static ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;

@Slf4j
@Service
public class HubRouterActionsSender {
    private final HubRouterControllerBlockingStub hubRouterClient;

    public HubRouterActionsSender(@GrpcClient("hub-router") HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    public void sendAction(DeviceActionRequest actionRequest) {
        try {
            log.info("Sending action: {}", new Object());
            hubRouterClient.handleDeviceAction(actionRequest);
        } catch (Exception e) {

        }


        // обработать ошибки через exception
    }
}