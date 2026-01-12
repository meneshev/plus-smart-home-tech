package delivery.service;

import delivery.dal.entity.Delivery;
import delivery.dal.entity.DeliveryAddress;
import delivery.dal.mapper.DeliveryAddressMapper;
import delivery.dal.mapper.DeliveryMapper;
import delivery.dal.repository.DeliveryAddressRepository;
import delivery.dal.repository.DeliveryRepository;
import dto.delivery.DeliveryDto;
import dto.delivery.DeliveryState;
import dto.order.OrderDto;
import dto.store.UUIDBodyDto;
import dto.warehouse.ShippedToDeliveryRequest;
import feign.order.OrderClient;
import feign.warehouse.WarehouseClient;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final WarehouseClient warehouseClient;
    private final OrderClient orderClient;

    private final Double BASE_PRICE = 5.0;

    @Override
    public DeliveryDto create(DeliveryDto deliveryDto) {
        Optional<DeliveryAddress> from = deliveryAddressRepository.findAddress(
                deliveryDto.getFromAddress().getCountry(),
                deliveryDto.getFromAddress().getCity(),
                deliveryDto.getFromAddress().getStreet(),
                deliveryDto.getFromAddress().getHouse(),
                deliveryDto.getFromAddress().getFlat()
        );

        if (from.isEmpty()) {
            from = Optional.of(
                    deliveryAddressRepository.save(
                            DeliveryAddressMapper.toEntity(deliveryDto.getFromAddress())
                    )
            );
        }

        Optional<DeliveryAddress> to = deliveryAddressRepository.findAddress(
                deliveryDto.getToAddress().getCountry(),
                deliveryDto.getToAddress().getCity(),
                deliveryDto.getToAddress().getStreet(),
                deliveryDto.getToAddress().getHouse(),
                deliveryDto.getToAddress().getFlat()
        );

        if (to.isEmpty()) {
            to = Optional.of(
                    deliveryAddressRepository.save(
                            DeliveryAddressMapper.toEntity(deliveryDto.getToAddress())
                    )
            );
        }

        Delivery delivery = Delivery.builder()
                .fromAddress(from.get())
                .toAddress(to.get())
                .orderId(UUID.fromString(deliveryDto.getOrderId()))
                .state(DeliveryState.CREATED)
                .fragile(deliveryDto.isFragile())
                .deliveryVolume(deliveryDto.getDeliveryVolume())
                .deliveryWeight(deliveryDto.getDeliveryWeight())
                .build();



        deliveryRepository.save(delivery);

        log.info("Delivery:{} for order:{} was created", delivery.getDeliveryId(), delivery.getOrderId());

        return DeliveryMapper.toDto(delivery);
    }

    @Override
    public void success(UUIDBodyDto deliveryId) {
        checkDelivery(UUID.fromString(deliveryId.getId()));
        Delivery delivery = deliveryRepository.findById(UUID.fromString(deliveryId.getId())).get();
        delivery.setState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);

        orderClient.deliveryOrder(
                UUIDBodyDto.builder()
                        .id(delivery.getOrderId().toString())
                        .build()
        );

        log.info("Delivery has been successfully delivered");
    }

    @Override
    public void picked(UUIDBodyDto deliveryId) {
        checkDelivery(UUID.fromString(deliveryId.getId()));
        Delivery delivery = deliveryRepository.findById(UUID.fromString(deliveryId.getId())).get();

        warehouseClient.shipToDelivery(ShippedToDeliveryRequest.builder()
                .deliveryId(deliveryId.getId())
                .orderId(delivery.getOrderId().toString())
                .build());

        delivery.setState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);
        log.info("Delivery has been picked");
    }

    @Override
    public void failed(UUIDBodyDto deliveryId) {
        checkDelivery(UUID.fromString(deliveryId.getId()));
        Delivery delivery = deliveryRepository.findById(UUID.fromString(deliveryId.getId())).get();
        delivery.setState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);

        orderClient.deliveryOrderFailed(
                UUIDBodyDto.builder()
                        .id(delivery.getOrderId().toString())
                        .build()
        );

        log.info("Delivery has been failed");
    }

    @Override
    public Double getCost(OrderDto orderDto) {
        Double cost = BASE_PRICE;

        Delivery delivery = deliveryRepository.getReferenceById(UUID.fromString(orderDto.getDeliveryId()));

        // имитация ADDRESS_2
        if (delivery.getFromAddress().getFlat().equals("2")) {
            cost *= 2;
        }

        cost += BASE_PRICE;

        if (orderDto.getFragile()) {
            cost += cost * 0.2;
        }

        cost += orderDto.getDeliveryWeight() * 0.3;

        cost += orderDto.getDeliveryVolume() * 0.2;

        if (!(delivery.getFromAddress().getCountry().equals(delivery.getToAddress().getCountry())
                && delivery.getFromAddress().getCity().equals(delivery.getToAddress().getCity())
                && delivery.getFromAddress().getStreet().equals(delivery.getToAddress().getStreet()))) {
            cost += cost * 0.2;
        }
        cost = BigDecimal.valueOf(cost).setScale(2, RoundingMode.HALF_EVEN).doubleValue();

        delivery.setDeliveryPrice(cost);
        deliveryRepository.save(delivery);

        return cost;
    }

    @Override
    public DeliveryDto getDelivery(String deliveryId) {
        checkDelivery(UUID.fromString(deliveryId));
        Delivery delivery = deliveryRepository.findById(UUID.fromString(deliveryId)).get();
        return DeliveryMapper.toDto(delivery);
    }

    private void checkDelivery(UUID deliveryId) {
        if (!deliveryRepository.existsById(deliveryId)) {
            log.error("Delivery id {} does not exist", deliveryId);
            throw new NotFoundException(String.format("Delivery id %s not found", deliveryId));
        }
    }
}
