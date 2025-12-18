package warehouse.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import warehouse.dal.entity.BookingProducts;
import warehouse.dal.entity.BookingProductsId;

import java.util.List;

public interface BookingProductRepository extends JpaRepository<BookingProducts, BookingProductsId> {
    List<BookingProducts> findAllByBookingId(Long bookingId);
}
