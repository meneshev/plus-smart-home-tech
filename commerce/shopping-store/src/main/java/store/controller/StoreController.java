package store.controller;

import dto.ProductCategory;
import dto.ProductDto;
import dto.ProductIdDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import store.service.StoreService;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category,
                                        @PageableDefault(
                                                size = 10, page = 0,
                                                sort = "productName", direction = Sort.Direction.ASC
                                        ) Pageable pageable) {
        return storeService.getProductsByCategory(category, pageable);
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody @Valid ProductDto product) {
        return storeService.updateProduct(product);
    }

    @PutMapping
    public ProductDto createProduct(@RequestBody @Valid ProductDto product) {
        return storeService.createProduct(product);
    }

    @PostMapping("/removeProductFromStore")
    public boolean removeProduct(@RequestBody ProductIdDto productIdDto) {
        return storeService.removeProduct(productIdDto.getProductId());
    }

    @PostMapping("/quantityState")
    public boolean updateProductQuantity(@RequestParam String quantityState, @RequestParam String productId) {
        return storeService.updateProductQt(quantityState, productId);
    }

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable String productId) {
        return storeService.getProductById(productId);
    }
}