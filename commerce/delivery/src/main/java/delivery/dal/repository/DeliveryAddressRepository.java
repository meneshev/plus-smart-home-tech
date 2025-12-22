package delivery.dal.repository;

import delivery.dal.entity.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
    @Query("SELECT DeliveryAddress from DeliveryAddress " +
            "where 1=1 " +
            "and DeliveryAddress.country = country " +
            "and DeliveryAddress.city = city " +
            "and DeliveryAddress.street = street " +
            "and DeliveryAddress.house = house " +
            "and DeliveryAddress.flat = flat")
    Optional<DeliveryAddress> findAddress(String country, String city, String street, String house, String flat);
}
