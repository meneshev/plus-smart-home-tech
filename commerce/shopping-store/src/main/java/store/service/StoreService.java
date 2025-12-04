package store.service;

import dto.ProductCategory;
import dto.ProductDto;
import dto.SetProductQuantityStateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreService {
    Page<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable);

    ProductDto updateProduct(ProductDto productDto) throws Exception;

    ProductDto createProduct(ProductDto productDto);

    boolean removeProduct(String productId);

    boolean updateProductQt(SetProductQuantityStateRequest qtRequest);

    ProductDto getProductById(String productId);
}