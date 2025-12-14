package store.service;

import dto.store.ProductCategory;
import dto.store.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreService {
    Page<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable);

    ProductDto updateProduct(ProductDto productDto);

    ProductDto createProduct(ProductDto productDto);

    boolean removeProduct(String productId);

    boolean updateProductQt(String quantityState, String productId);

    ProductDto getProductById(String productId);
}