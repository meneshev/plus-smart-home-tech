package warehouse.service;

import dto.cart.ShoppingCartDto;
import dto.warehouse.*;

import java.util.Map;

public interface WarehouseService {
    AddressDto getAddress();

    void addNewProduct(NewProductInWarehouseRequest request);

    void addProduct(AddProductInWarehouseRequest request);

    BookedProductsDto check(ShoppingCartDto request);

    void shipToDelivery(ShippedToDeliveryRequest request);

    void returnProducts(Map<String, Long> products);

    BookedProductsDto assembly(AssemblyProductsForOrderRequest request);
}
