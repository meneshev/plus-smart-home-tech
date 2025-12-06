package store.dal.repository;

import dto.ProductCategory;
import dto.QuantityState;
import store.dal.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findProductsByProductCategory(ProductCategory productCategory, Pageable pageable);
    //Product updateProductQuantityById(UUID productId, QuantityState quantityState);
}
