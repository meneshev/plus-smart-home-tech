package store.controller;

import dto.ProductCategory;
import dto.ProductDto;
import dto.SetProductQuantityStateRequest;
import entity.Product;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class StoreController {

    @GetMapping
    public Page<Product> getProducts(@RequestParam ProductCategory category,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "productName") String sortBy,
                                     @RequestParam(defaultValue = "ASC") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        //TODO
        return null;
    }

    @PutMapping
    public ProductDto updateProduct(@RequestBody @Valid ProductDto product) {
        return null;
    }

    @PostMapping
    public ProductDto createProduct(@RequestBody @Valid ProductDto product) {
        return null;
    }

    @PostMapping("/removeProductFromStore")
    public boolean removeProduct(@RequestBody String productId) {
        return false;
    }

    @PostMapping("/quantityState")
    public boolean updateProductQuantity(@RequestBody @Valid SetProductQuantityStateRequest qtRequest) {
        return false;
    }

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable String productId) {
        return null;
    }
}