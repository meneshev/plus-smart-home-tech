package warehouse.service;

import dto.cart.ShoppingCartDto;
import dto.order.OrderState;
import dto.warehouse.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import util.exception.*;
import warehouse.dal.entity.*;
import warehouse.dal.mapper.AddressMapper;
import warehouse.dal.mapper.ProductSpecsMapper;
import warehouse.dal.repository.*;

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
    private final BookingRepository bookingRepository;
    private final BookingProductRepository bookingProductRepository;

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
        Map<UUID, WarehouseProduct> productsInWarehouse = checkProductsExistsAndGet(request.getProducts());
        checkProductQuantity(request.getProducts(), productsInWarehouse);
        return getBookedProducts(request.getProducts(), productsInWarehouse);
    }

    /*
    Передать товары в доставку*. Метод должен обновить информацию о собранном заказе в базе данных склада:
    добавить в него идентификатор доставки, который вернул сервис доставки,
    присвоить идентификатор доставки во внутреннем хранилище собранных товаров заказа.
    Вызывается из сервиса доставки.
     */
    @Override
    public void shipToDelivery(ShippedToDeliveryRequest request) {
        List<Booking> bookings = bookingRepository.findAllByOrderId(UUID.fromString(request.getOrderId()));
        if (bookings.isEmpty()) {
            log.error("Bookings for order {} not exists", request.getOrderId());
            throw new ValidationException(String.format("Bookings for order %s not exists", request.getOrderId()));
        }

        Optional<Booking> currentBooking = bookings.stream()
                .filter(booking -> booking.getDeliveryId() == null)
                .findAny();

        if (currentBooking.isPresent()) {
            currentBooking.get().setDeliveryId(UUID.fromString(request.getDeliveryId()));
            bookingRepository.save(currentBooking.get());
        } else {
            log.error("Booking for order {} already shipped to delivery", request.getOrderId());
            throw new ValidationException(String.format("Booking for order %s already shipped to delivery", request.getOrderId()));
        }
    }

    /*
    Вернуть товар. Если товар возвращается на склад, нужно увеличить остаток.
    Соответственно, метод принимает список товаров с количеством и увеличивает доступный остаток.
     */
    @Override
    public void returnProducts(Map<String, Long> products) {
        checkProductsExistsAndGet(products);

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
        Map<UUID, WarehouseProduct> productsInWarehouse = checkProductsExistsAndGet(request.getProducts());
        checkProductQuantity(request.getProducts(), productsInWarehouse);

        productsInWarehouse.forEach((uuid, product) -> {
            Long qtFromOrder = request.getProducts().get(uuid);
            Long qtFromWh = product.getQuantity();

            product.setQuantity(qtFromWh - qtFromOrder);
            warehouseProductRepository.save(product);
        });

        List<Booking> bookings = bookingRepository.findAllByOrderId(UUID.fromString(request.getOrderId()));

        if (bookings.isEmpty()) {
            Booking newBooking = bookingRepository.save(Booking.builder()
                            .orderId(UUID.fromString(request.getOrderId()))
                            .build());

            request.getProducts().forEach((uuid, qt) -> {
                BookingProductsId id = BookingProductsId.builder()
                        .warehouseId(DEFAULT_WAREHOUSE_ID)
                        .productId(UUID.fromString(uuid))
                        .bookingId(newBooking.getBookingId())
                        .build();

                bookingProductRepository.save(BookingProducts.builder()
                                .id(id)
                                .quantity(qt)
                                .build()
                );
            });

            newBooking.setBookingState(OrderState.ASSEMBLED);
            bookingRepository.save(newBooking);
        } else {
            Optional<Booking> currentBooking =  bookings.stream()
                    .filter(booking -> booking.getBookingState() == null
                            || booking.getBookingState().equals(OrderState.ASSEMBLY_FAILED))
                    .findAny();

            if (currentBooking.isPresent()) {
                currentBooking.get().getProducts().clear();

                request.getProducts().forEach((uuid, qt) -> {
                            BookingProductsId id = BookingProductsId.builder()
                                    .warehouseId(DEFAULT_WAREHOUSE_ID)
                                    .productId(UUID.fromString(uuid))
                                    .bookingId(currentBooking.get().getBookingId())
                                    .build();

                            bookingProductRepository.save(BookingProducts.builder()
                                    .id(id)
                                    .quantity(qt)
                                    .build()
                            );
                });
                currentBooking.get().setBookingState(OrderState.ASSEMBLED);
            } else {
                log.error("Order {} already assembled", request.getOrderId());
                throw new ValidationException(String.format("Order %s already assembled", request.getOrderId()));
            }
        }
        return getBookedProducts(request.getProducts(), productsInWarehouse);
    }

    private Map<UUID, WarehouseProduct> checkProductsExistsAndGet(Map<String, Long> products) {
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

        return productsInWarehouse;
    }

    private void checkProductQuantity(Map<String, Long> products, Map<UUID, WarehouseProduct> productsInWarehouse) throws NotEnoughProductException {
        Set<UUID> notEnoughProductIds = new HashSet<>();
        products.forEach((uuid, qt) -> {
            if (productsInWarehouse.get(uuid).getQuantity() < qt) {
                notEnoughProductIds.add(UUID.fromString(uuid));
            }
        });

        if (!notEnoughProductIds.isEmpty()) {
            throw new NotEnoughProductException(
                    String.format("Products not enough, IDs: %s",  notEnoughProductIds));
        }
    }

    private BookedProductsDto getBookedProducts(Map<String, Long> products, Map<UUID, WarehouseProduct> productsInWarehouse) {
        BookedProductsDto bookedProductsDto = BookedProductsDto.builder()
                .deliveryVolume(0.0)
                .deliveryWeight(0.0)
                .build();

        AtomicBoolean isFragile = new AtomicBoolean(false);
        products.forEach((key, qt) -> {
            UUID currentId = UUID.fromString(key);
            WarehouseProduct productFromWarehouse = productsInWarehouse.get(currentId);

            if (productFromWarehouse.getProductSpecs().getIsFragile()) {
                isFragile.set(true);
            }
            bookedProductsDto.setDeliveryVolume(
                    bookedProductsDto.getDeliveryVolume() +
                            (productFromWarehouse.getProductSpecs().getVolume() * qt)
            );

            bookedProductsDto.setDeliveryWeight(
                    bookedProductsDto.getDeliveryWeight() +
                            (productFromWarehouse.getProductSpecs().getWeight() * qt)
            );
        });

        bookedProductsDto.setFragile(isFragile.get());
        return bookedProductsDto;
    }
}
