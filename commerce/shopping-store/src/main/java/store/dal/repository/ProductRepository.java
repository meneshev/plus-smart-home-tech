package store.dal.repository;

import dto.store.ProductCategory;
import dto.store.QuantityState;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import store.dal.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findProductsByProductCategory(ProductCategory productCategory, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Product SET quantityState = :quantityState WHERE productId = :productId")
    void setQuantityStateByProductId(QuantityState quantityState, UUID productId);

    @Modifying
    @Transactional
    @Query("UPDATE Product SET productState = \"DEACTIVATE\" WHERE productId = :productId")
    void removeProduct(UUID productId);
}
