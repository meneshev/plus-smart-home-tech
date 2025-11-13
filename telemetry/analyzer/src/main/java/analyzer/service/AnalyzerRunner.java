package analyzer.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AnalyzerRunner implements CommandLineRunner {
    final HubEventProcessor hubEventProcessor;
    final SnapshotProcessor snapshotProcessor;

    @Override
    public void run(String... args) throws Exception {
        Thread hubEventThread = new Thread(hubEventProcessor);
        hubEventThread.setName("HubEventHandlerThread");
        hubEventThread.start();

        snapshotProcessor.start();
    }
}
