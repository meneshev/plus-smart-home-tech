package store.dal.mapper;

import dto.store.ProductCategory;
import dto.store.ProductDto;
import dto.store.ProductState;
import dto.store.QuantityState;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import store.dal.entity.Product;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductMapper {

    public static ProductDto convertToDto(Product product) {
        return ProductDto.builder()
                .productId(product.getProductId().toString())
                .productName(product.getProductName())
                .description(product.getDescription())
                .imageSrc(product.getImageSrc() != null ? product.getImageSrc() : null)
                .quantityState(String.valueOf(product.getQuantityState()))
                .productState(String.valueOf(product.getProductState()))
                .productCategory(product.getProductCategory() != null ? String.valueOf(product.getProductCategory()) : null)
                .price(product.getPrice())
                .build();
    }

    public static Product toEntity(ProductDto productDto) {
        return Product.builder()
                .productId(productDto.getProductId() != null ? UUID.fromString(productDto.getProductId()) : null)
                .productName(productDto.getProductName())
                .description(productDto.getDescription())
                .imageSrc(productDto.getImageSrc() != null ? productDto.getImageSrc() : null)
                .quantityState(QuantityState.valueOf(productDto.getQuantityState()))
                .productState(ProductState.valueOf(productDto.getProductState()))
                .productCategory(productDto.getProductCategory() != null ? ProductCategory.valueOf(productDto.getProductCategory()) : null)
                .price(productDto.getPrice())
                .build();
    }

}
