package warehouse.dal.mapper;

import dto.warehouse.NewProductInWarehouseRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import warehouse.dal.entity.ProductSpecs;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductSpecsMapper {

    public static ProductSpecs toEntity(NewProductInWarehouseRequest request) {
        return ProductSpecs.builder()
                .productId(UUID.fromString(request.getProductId()))
                .width(request.getDimension().getWidth())
                .height(request.getDimension().getHeight())
                .depth(request.getDimension().getDepth())
                .isFragile(request.getFragile() != null ? request.getFragile() : null)
                .weight(request.getWeight())
                .build();
    }
}
