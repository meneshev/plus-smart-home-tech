package warehouse.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import warehouse.dal.entity.WarehouseProduct;
import warehouse.dal.entity.WarehouseProductId;

import java.util.Optional;

public interface WarehouseProductRepository extends JpaRepository<WarehouseProduct, WarehouseProductId> {
    Optional<WarehouseProduct> findById(WarehouseProductId id);
}
