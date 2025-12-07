package warehouse.controller;

import dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import warehouse.service.WarehouseService;

@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping("/address")
    public AddressDto getAddress() {
        return warehouseService.getAddress();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewProduct(@Valid @RequestBody NewProductInWarehouseRequest request) {
        warehouseService.addNewProduct(request);
    }

    @PostMapping("/add")
    public void addProduct(@Valid @RequestBody AddProductInWarehouseRequest request) {
        warehouseService.addProduct(request);
    }

    @PostMapping("/check")
    public BookedProductsDto check(@Valid @RequestBody ShoppingCartDto request) {
        return warehouseService.check(request);
    }
}
