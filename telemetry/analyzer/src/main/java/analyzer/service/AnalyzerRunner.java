package analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyzerRunner implements CommandLineRunner {
    final HubEventProcessor hubEventProcessor;
    final SnapshotProcessor snapshotProcessor;

    @Override
    public void run(String... args) throws Exception {
        Thread hubEventThread = new Thread(hubEventProcessor);
        hubEventThread.setName("HubEventHandlerThread");
        hubEventThread.start();

        Thread shapshotEventThread = new Thread(snapshotProcessor);
        shapshotEventThread.setName("SnapshotEventHandlerThread");
        shapshotEventThread.start();
    }
}