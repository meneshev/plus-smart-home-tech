package store.service;

import dto.ProductCategory;
import dto.ProductDto;
import dto.SetProductQuantityStateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import util.logging.Loggable;

public interface StoreService {
    Page<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable);

    ProductDto updateProduct(ProductDto productDto);

    ProductDto createProduct(ProductDto productDto);

    boolean removeProduct(String productId);

    boolean updateProductQt(String quantityState, String productId);

    ProductDto getProductById(String productId);
}