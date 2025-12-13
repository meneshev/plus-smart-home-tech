package warehouse.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import warehouse.dal.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
