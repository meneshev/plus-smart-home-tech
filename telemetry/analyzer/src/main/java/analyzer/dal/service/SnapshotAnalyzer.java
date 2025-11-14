package analyzer.dal.service;

import analyzer.dal.entity.Condition;
import analyzer.dal.entity.Scenario;
import analyzer.dal.repository.ActionRepository;
import analyzer.dal.repository.ConditionRepository;
import analyzer.dal.repository.ScenarioRepository;
import analyzer.dal.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotAnalyzer {
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;
    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;

    public boolean processSnapshot(SensorsSnapshotAvro snapshot) {
        boolean isSuccess;
        try {
            List<Scenario> hubScenarios = scenarioRepository.findByHubId(snapshot.getHubId());
            if (!hubScenarios.isEmpty()) {
                snapshot.getSensorState().forEach((sensorId, sensorState) -> {
                    hubScenarios.forEach(
                            scenario -> {

                                Condition condition = scenario.getConditions().get(sensorId);

                                if (condition != null) {

                                }
                            }

                    );
                });
            }

        }

        );
    }

    private boolean checkCondition(String conditionType, SensorStateAvro sensorState) {
        switch (conditionType) {
            case "EQUALS" -> {
                return condition.getValue().equals(sensorState.getData())
            }
        }
    }
}
