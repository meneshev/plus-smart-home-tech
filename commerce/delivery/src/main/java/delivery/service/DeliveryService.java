package delivery.service;

import dto.delivery.DeliveryDto;
import dto.order.OrderDto;
import dto.store.UUIDBodyDto;
import jakarta.validation.Valid;

public interface DeliveryService {
    DeliveryDto create(@Valid DeliveryDto deliveryDto);

    void success(@Valid UUIDBodyDto deliveryId);

    void picked(@Valid UUIDBodyDto deliveryId);

    void failed(@Valid UUIDBodyDto deliveryId);

    Double getCost(@Valid OrderDto orderDto);

    DeliveryDto getDelivery(@Valid UUIDBodyDto deliveryId);
}
