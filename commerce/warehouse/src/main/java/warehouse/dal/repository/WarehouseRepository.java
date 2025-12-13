package warehouse.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import warehouse.dal.entity.Warehouse;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}
