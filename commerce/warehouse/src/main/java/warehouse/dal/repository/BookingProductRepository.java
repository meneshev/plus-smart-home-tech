package warehouse.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import warehouse.dal.entity.BookingProducts;
import warehouse.dal.entity.BookingProductsId;

public interface BookingProductRepository extends JpaRepository<BookingProducts, BookingProductsId> {
}
