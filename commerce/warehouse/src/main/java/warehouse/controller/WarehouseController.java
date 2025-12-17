package warehouse.controller;

import dto.cart.ShoppingCartDto;
import dto.warehouse.*;
import feign.warehouse.WarehouseOperations;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import util.logging.Loggable;
import warehouse.service.WarehouseService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseOperations {

    private final WarehouseService warehouseService;

    @Loggable
    @GetMapping("/address")
    public AddressDto getAddress() {
        return warehouseService.getAddress();
    }

    @Loggable
    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewProduct(@Valid @RequestBody NewProductInWarehouseRequest request) {
        warehouseService.addNewProduct(request);
    }

    @Loggable
    @PostMapping("/add")
    public void addProduct(@Valid @RequestBody AddProductInWarehouseRequest request) {
        warehouseService.addProduct(request);
    }

    @Loggable
    @PostMapping("/check")
    public BookedProductsDto check(@Valid @RequestBody ShoppingCartDto request) {
        return warehouseService.check(request);
    }

    @Loggable
    @PostMapping("/shipped")
    public void shipToDelivery(ShippedToDeliveryRequest request) {
        warehouseService.shipToDelivery(request);
    }

    @Loggable
    @PostMapping("/return")
    public void returnProducts(Map<String, Long> products) {
        warehouseService.returnProducts(products);
    }

    @Loggable
    @PostMapping("/assembly")
    public BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequest request) {
        return warehouseService.assembly(request);
    }
}
