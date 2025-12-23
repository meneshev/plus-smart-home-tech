package delivery.dal.mapper;

import delivery.dal.entity.Delivery;
import dto.delivery.DeliveryDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeliveryMapper {

    public static DeliveryDto toDto(Delivery delivery) {
        return DeliveryDto.builder()
                .deliveryId(delivery.getDeliveryId().toString())
                .fromAddress(DeliveryAddressMapper.toDto(delivery.getFromAddress()))
                .toAddress(DeliveryAddressMapper.toDto(delivery.getToAddress()))
                .deliveryState(delivery.getState().toString())
                .orderId(delivery.getOrderId().toString())
                .build();
    }
}