package delivery.dal.repository;

import delivery.dal.entity.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
    @Query("SELECT d from DeliveryAddress d " +
            "where 1=1 " +
            "and d.country = :country " +
            "and d.city = :city " +
            "and d.street = :street " +
            "and d.house = :house " +
            "and d.flat = :flat")
    Optional<DeliveryAddress> findAddress(String country, String city, String street, String house, String flat);
}
