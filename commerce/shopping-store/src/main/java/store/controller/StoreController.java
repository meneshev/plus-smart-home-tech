package store.controller;

import dto.store.ProductCategory;
import dto.store.ProductDto;
import dto.store.UUIDBodyDto;
import feign.shopping.store.ShoppingStoreOperations;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import store.service.StoreService;
import util.logging.Loggable;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class StoreController implements ShoppingStoreOperations {

    private final StoreService storeService;

    @Loggable
    @GetMapping
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category,
                                        @PageableDefault(
                                                size = 10, page = 0,
                                                sort = "productName", direction = Sort.Direction.ASC
                                        ) Pageable pageable) {
        return storeService.getProductsByCategory(category, pageable);
    }

    @Loggable
    @PostMapping
    public ProductDto updateProduct(@RequestBody @Valid ProductDto product) {
        return storeService.updateProduct(product);
    }

    @Loggable
    @PutMapping
    public ProductDto createProduct(@RequestBody @Valid ProductDto product) {
        return storeService.createProduct(product);
    }

    @Loggable
    @PostMapping("/removeProductFromStore")
    public boolean removeProduct(@RequestBody UUIDBodyDto productIdDto) {
        return storeService.removeProduct(productIdDto.getId());
    }

    @Loggable
    @PostMapping("/quantityState")
    public boolean updateProductQuantity(@RequestParam String quantityState, @RequestParam String productId) {
        return storeService.updateProductQt(quantityState, productId);
    }

    @Loggable
    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable String productId) {
        return storeService.getProductById(productId);
    }
}