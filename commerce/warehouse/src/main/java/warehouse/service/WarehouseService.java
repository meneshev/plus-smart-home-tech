package warehouse.service;

import dto.*;

public interface WarehouseService {
    AddressDto getAddress();

    void addNewProduct(NewProductInWarehouseRequest request);

    void addProduct(AddProductInWarehouseRequest request);

    BookedProductsDto check(ShoppingCartDto request);
}
