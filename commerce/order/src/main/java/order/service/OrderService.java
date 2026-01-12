package order.service;

import dto.order.CreateNewOrderRequest;
import dto.order.OrderDto;
import dto.order.ProductReturnRequest;
import dto.store.UUIDBodyDto;
import jakarta.validation.Valid;

import java.util.List;

public interface OrderService {
    List<OrderDto> getOrders(String username);

    OrderDto createOrder(@Valid CreateNewOrderRequest request);

    OrderDto returnOrder(@Valid ProductReturnRequest request);

    OrderDto payOrder(@Valid UUIDBodyDto productId);

    OrderDto payOrderFailed(@Valid UUIDBodyDto productId);

    OrderDto deliveryOrder(@Valid UUIDBodyDto productId);

    OrderDto deliveryOrderFailed(@Valid UUIDBodyDto productId);

    OrderDto completeOrder(@Valid UUIDBodyDto productId);

    OrderDto calculateTotalOrder(@Valid UUIDBodyDto productId);

    OrderDto calculateDeliveryOrder(@Valid UUIDBodyDto productId);

    OrderDto assemblyOrder(@Valid UUIDBodyDto productId);

    OrderDto assemblyOrderFailed(@Valid UUIDBodyDto productId);
}
