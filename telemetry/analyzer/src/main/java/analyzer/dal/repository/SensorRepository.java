package analyzer.dal.repository;

import analyzer.dal.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, String> {
    boolean existsByIdAndHubId(Collection<String> ids, String hubId);
    Optional<Sensor> findByIdAndHubId(String id, String hubId);
}