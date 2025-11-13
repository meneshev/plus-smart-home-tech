package analyzer.controller;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import static ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;

@Slf4j
@Service
public class HubRouterActionsSender {
    private final HubRouterControllerBlockingStub hubRouterClient;

    public HubRouterActionsSender(@GrpcClient("hub-router") HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    public void sendAction() {
        log.info("Sending action: {}", new Object());

        // обработать ошибки через exception
    }
}