package feign.warehouse;

import dto.cart.ShoppingCartDto;
import dto.warehouse.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface WarehouseOperations {

    @GetMapping("/address")
    AddressDto getAddress();

    @PutMapping
    void addNewProduct(@Valid @RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/add")
    void addProduct(@Valid @RequestBody AddProductInWarehouseRequest request);

    @PostMapping("/check")
    BookedProductsDto check(@Valid @RequestBody ShoppingCartDto request);

    @PostMapping("/shipped")
    void shipToDelivery(@Valid @RequestBody ShippedToDeliveryRequest request);

    @PostMapping("/return")
    void returnProducts(@RequestBody Map<String, Long> products);

    @PostMapping("/assembly")
    BookedProductsDto assemblyProducts(@Valid @RequestBody AssemblyProductsForOrderRequest request);
}