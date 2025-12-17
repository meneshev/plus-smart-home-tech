package warehouse.service;

import dto.cart.ShoppingCartDto;
import dto.warehouse.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import util.exception.*;
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

    @Override
    public AddressDto getAddress() {
        return addressRepository.findById(DEFAULT_WAREHOUSE_ID).stream()
                .findAny()
                .map(AddressMapper::convertToDto)
                .orElseThrow(() -> new NotFoundException("Address not found"));
    }

    @Override
    public void addNewProduct(NewProductInWarehouseRequest request) {
        UUID productId = UUID.fromString(request.getProductId());
        if (productSpecsRepository.existsByProductId(productId)) {
            throw new SpecifiedProductAlreadyInWarehouseException(String.format("Product with id %s already exists", productId));
        }
        productSpecsRepository.save(ProductSpecsMapper.toEntity(request));
    }

    @Override
    public void addProduct(AddProductInWarehouseRequest request) {
        UUID productId = UUID.fromString(request.getProductId());

        if (!productSpecsRepository.existsByProductId(productId)) {
            throw new NoSpecifiedProductInWarehouseException(String.format("Product with id %s not found", productId));
        }

        WarehouseProductId id = WarehouseProductId.builder()
                .warehouseId(DEFAULT_WAREHOUSE_ID)
                .productId(productId)
                .build();

        Optional<WarehouseProduct> product = warehouseProductRepository.findById(id);

        if (product.isPresent()) {
            product.get().setQuantity(request.getQuantity());
        } else {
            product = Optional.ofNullable(WarehouseProduct.builder()
                    .id(id)
                    .quantity(request.getQuantity())
                    .build());
        }

        warehouseProductRepository.save(product.get());
    }

    @Override
    public BookedProductsDto check(ShoppingCartDto request) throws NoSpecifiedProductInWarehouseException,
            ProductInShoppingCartLowQuantityInWarehouse {
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
            throw new NoSpecifiedProductInWarehouseException("Products from cart not found");
        }

        AtomicBoolean isFragile = new AtomicBoolean(false);
        request.getProducts().entrySet().forEach(product-> {
            UUID currentId = UUID.fromString(product.getKey());
            WarehouseProduct productFromWarhouse = productsInWarehouse.get(currentId);
            if (product.getValue() > productFromWarhouse.getQuantity()) {
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
            throw new ProductInShoppingCartLowQuantityInWarehouse(
                    String.format("Products from cart not enough, IDs: %s",  notEnoughProductIds));
        }

        bookedProductsDto.setFragile(isFragile.get());

        return bookedProductsDto;
    }

    /*
    Передать товары в доставку*. Метод должен обновить информацию о собранном заказе в базе данных склада:
    добавить в него идентификатор доставки, который вернул сервис доставки,
    присвоить идентификатор доставки во внутреннем хранилище собранных товаров заказа.
    Вызывается из сервиса доставки.
     */
    @Override
    public void shipToDelivery(ShippedToDeliveryRequest request) {
        //todo after delivery
    }

    /*
    Вернуть товар. Если товар возвращается на склад, нужно увеличить остаток.
    Соответственно, метод принимает список товаров с количеством и увеличивает доступный остаток.
     */
    @Override
    public void returnProducts(Map<String, Long> products) {
        // TODO вынести в отдельный private метод проверку на товар на складе
        Set<WarehouseProductId> currentProductIds = products.keySet().stream()
                .map(uuid -> WarehouseProductId.builder()
                        .warehouseId(DEFAULT_WAREHOUSE_ID)
                        .productId(UUID.fromString(uuid))
                        .build())
                .collect(Collectors.toSet());

        Map<UUID, WarehouseProduct> productsInWarehouse = warehouseProductRepository.findAllById(currentProductIds).stream()
                .collect(Collectors.toMap(
                        wp -> wp.getId().getProductId(),
                        wp -> wp
                ));

        if (productsInWarehouse.isEmpty() || productsInWarehouse.size() != currentProductIds.size()) {
            throw new NoSpecifiedProductInWarehouseException("Products from cart not found");
        }

        products.forEach((key, value) -> {
            WarehouseProductId id = WarehouseProductId.builder()
                    .warehouseId(DEFAULT_WAREHOUSE_ID)
                    .productId(UUID.fromString(key))
                    .build();

            Optional<WarehouseProduct> productDB = warehouseProductRepository.findById(id);

            productDB.get().setQuantity(productDB.get().getQuantity() + value);
            //TODO подумать насчет транзакций, посмотреть предыдущий вебинар
            warehouseProductRepository.save(productDB.get());
        });
    }

    /*
    Собрать товары для заказа. Метод получает список товаров и идентификатор заказа.
    По нему повторно проверяется наличие заказанных товаров в нужном количестве,
    уменьшается их доступный остаток и создаётся сущность «Забронированные для заказа товары» (OrderBooking).
     */
    @Override
    public BookedProductsDto assembly(AssemblyProductsForOrderRequest request) {
        return null;
    }
}
