package order.dal.mapper;

import dto.order.OrderDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import order.dal.entity.Order;

import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderMapper {
    public static OrderDto toDto(Order order) {
        return OrderDto.builder()
                .orderId(order.getOrderId().toString())
                .shoppingCartId(order.getShoppingCartId().toString())
                .products(order.getProducts().entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> entry.getKey().toString(),
                                Map.Entry::getValue
                        )))
                .paymentId(order.getPaymentId() != null ? order.getPaymentId().toString() : null)
                .deliveryId(order.getDeliveryId() != null ? order.getDeliveryId().toString() : null)
                .orderState(order.getOrderState().toString())
                .build();
    }
}
