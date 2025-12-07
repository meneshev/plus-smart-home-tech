package warehouse.service;

import dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import util.exception.NotFoundException;
import util.exception.ValidationException;
import util.logging.Loggable;
import warehouse.dal.entity.WarehouseProduct;
import warehouse.dal.entity.WarehouseProductId;
import warehouse.dal.mapper.AddressMapper;
import warehouse.dal.mapper.ProductSpecsMapper;
import warehouse.dal.repository.AddressRepository;
import warehouse.dal.repository.ProductSpecsRepository;
import warehouse.dal.repository.WarehouseProductRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final static Long DEFAULT_WAREHOUSE_ID = 1L;
    private final WarehouseProductRepository warehouseProductRepository;
    private final AddressRepository addressRepository;
    private final ProductSpecsRepository productSpecsRepository;

    @Loggable
    @Override
    public AddressDto getAddress() {
        return addressRepository.findById(DEFAULT_WAREHOUSE_ID).stream()
                .findAny()
                .map(AddressMapper::convertToDto)
                .orElseThrow(() -> new NotFoundException("Address not found"));
    }

    @Loggable
    @Override
    public void addNewProduct(NewProductInWarehouseRequest request) {
        UUID productId = UUID.fromString(request.getProductId());
        if (productSpecsRepository.findByProductId(productId)) {
            throw new ValidationException(String.format("Product with id %s already exists", productId));
        }
        productSpecsRepository.save(ProductSpecsMapper.toEntity(request));
    }

    @Loggable
    @Override
    public void addProduct(AddProductInWarehouseRequest request) {
        UUID productId = UUID.fromString(request.getProductId());
        WarehouseProductId id = WarehouseProductId.builder()
                .warehouseId(DEFAULT_WAREHOUSE_ID)
                .productId(productId)
                .build();

        Optional<WarehouseProduct> product = warehouseProductRepository.findById(id);
        if (product.isEmpty()) {
            throw new NotFoundException(String.format("Product with id %s not found", productId));
        }
        product.get().setQuantity(request.getQuantity());
        warehouseProductRepository.save(product.get());
    }

    @Loggable
    @Override
    public BookedProductsDto check(ShoppingCartDto request) {
        Set<UUID> notEnoughProductIds = new HashSet<>();
        Set<WarehouseProductId> currentProductIds = request.getProducts().keySet().stream()
                .map(uuid -> WarehouseProductId.builder()
                        .warehouseId(DEFAULT_WAREHOUSE_ID)
                        .productId(UUID.fromString(uuid))
                        .build())
                .collect(Collectors.toSet());

        BookedProductsDto bookedProductsDto = BookedProductsDto.builder()
                .deliveryVolume(0.0)
                .deliveryWeight(0.0)
                .build();

        Map<UUID, WarehouseProduct> productsInWarehouse = warehouseProductRepository.findAllById(currentProductIds).stream()
                .collect(Collectors.toMap(
                        wp -> wp.getId().getProductId(),
                        wp -> wp
                ));

        if (productsInWarehouse.isEmpty() || productsInWarehouse.size() != currentProductIds.size()) {
            throw new ValidationException("Products from cart not found");
        }

        AtomicBoolean isFragile = new AtomicBoolean(false);
        request.getProducts().entrySet().forEach(product-> {
            UUID currentId = UUID.fromString(product.getKey());
            WarehouseProduct productFromWarhouse = productsInWarehouse.get(currentId);
            if (product.getValue() < productFromWarhouse.getQuantity()) {
                notEnoughProductIds.add(currentId);
            } else {
                if (productFromWarhouse.getProductSpecs().getIsFragile()) {
                    isFragile.set(true);
                }
                bookedProductsDto.setDeliveryVolume(
                        bookedProductsDto.getDeliveryVolume() +
                                (productFromWarhouse.getProductSpecs().getVolume() * productFromWarhouse.getQuantity())
                );

                bookedProductsDto.setDeliveryWeight(
                        bookedProductsDto.getDeliveryWeight() +
                                (productFromWarhouse.getProductSpecs().getWeight() * productFromWarhouse.getQuantity())
                );
            }
        });

        if (!notEnoughProductIds.isEmpty()) {
            throw new ValidationException(String.format("Products from cart not enough, IDs: %s",  notEnoughProductIds));
        }

        bookedProductsDto.setFragile(isFragile.get());

        return bookedProductsDto;
    }
}
