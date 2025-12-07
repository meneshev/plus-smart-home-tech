package warehouse.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import warehouse.dal.entity.ProductSpecs;

import java.util.UUID;

public interface ProductSpecsRepository extends JpaRepository<ProductSpecs, Long> {
    boolean findByProductId(UUID productId);
}
