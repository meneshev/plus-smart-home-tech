package order.service;

import dto.order.CreateNewOrderRequest;
import dto.order.OrderDto;
import dto.order.ProductReturnRequest;
import dto.store.ProductIdDto;
import jakarta.validation.Valid;

import java.util.List;

public interface OrderService {
    List<OrderDto> getOrders(String username);

    OrderDto createOrder(@Valid CreateNewOrderRequest request);

    OrderDto returnOrder(@Valid ProductReturnRequest request);

    OrderDto payOrder(@Valid ProductIdDto productId);

    OrderDto payOrderFailed(@Valid ProductIdDto productId);

    OrderDto deliveryOrder(@Valid ProductIdDto productId);

    OrderDto deliveryOrderFailed(@Valid ProductIdDto productId);

    OrderDto completeOrder(@Valid ProductIdDto productId);

    OrderDto calculateTotalOrder(@Valid ProductIdDto productId);

    OrderDto calculateDeliveryOrder(@Valid ProductIdDto productId);

    OrderDto assemblyOrder(@Valid ProductIdDto productId);

    OrderDto assemblyOrderFailed(@Valid ProductIdDto productId);
}
