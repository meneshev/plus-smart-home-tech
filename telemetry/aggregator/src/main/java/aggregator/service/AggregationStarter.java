package aggregator.service;

import aggregator.kafka.AggregatorClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final Environment env;
    private final AggregatorClient client;
    //TODO snapshots client

    public void start() {
        try {
            while (true) {
                client.getConsumer().poll(Duration.ofSeconds(5));
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Error during process 'telemetry.sensors.v1'", e);
        } finally {
            try {
                // Перед тем, как закрыть продюсер и консьюмер, нужно убедится,
                // что все сообщения, лежащие в буффере, отправлены и
                // все оффсеты обработанных сообщений зафиксированы

                // здесь нужно вызвать метод продюсера для сброса данных в буффере
                // здесь нужно вызвать метод консьюмера для фиксиции смещений
            } finally {
                log.info("Closing aggregator consumer...");
                client.stop();
                log.info("Closing snapshots producer...");
                //TODO
            }
        }
    }
}
