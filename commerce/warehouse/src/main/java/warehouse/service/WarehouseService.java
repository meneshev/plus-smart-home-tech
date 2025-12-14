package warehouse.service;

import dto.cart.ShoppingCartDto;
import dto.warehouse.AddProductInWarehouseRequest;
import dto.warehouse.AddressDto;
import dto.warehouse.BookedProductsDto;
import dto.warehouse.NewProductInWarehouseRequest;

public interface WarehouseService {
    AddressDto getAddress();

    void addNewProduct(NewProductInWarehouseRequest request);

    void addProduct(AddProductInWarehouseRequest request);

    BookedProductsDto check(ShoppingCartDto request);
}
