package feign.shopping.store;

import dto.ProductCategory;
import dto.ProductDto;
import dto.ProductIdDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

public interface ShoppingStoreOperations {

    @GetMapping
    Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable);

    @PostMapping
    ProductDto updateProduct(@RequestBody @Valid ProductDto product);

    @PutMapping
    ProductDto createProduct(@RequestBody @Valid ProductDto product);

    @PostMapping("/removeProductFromStore")
    boolean removeProduct(@RequestBody ProductIdDto productIdDto);

    @PostMapping("/quantityState")
    boolean updateProductQuantity(@RequestParam String quantityState, @RequestParam String productId);

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable String productId);
}
