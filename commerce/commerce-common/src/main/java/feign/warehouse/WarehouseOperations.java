package feign.warehouse;

import dto.cart.ShoppingCartDto;
import dto.warehouse.AddProductInWarehouseRequest;
import dto.warehouse.AddressDto;
import dto.warehouse.BookedProductsDto;
import dto.warehouse.NewProductInWarehouseRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface WarehouseOperations {

    @GetMapping("/address")
    AddressDto getAddress();

    @PutMapping
    void addNewProduct(@Valid @RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/add")
    void addProduct(@Valid @RequestBody AddProductInWarehouseRequest request);

    @PostMapping("/check")
    BookedProductsDto check(@Valid @RequestBody ShoppingCartDto request);
}
