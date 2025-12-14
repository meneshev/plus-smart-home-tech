package order.service;

import dto.order.CreateNewOrderRequest;
import dto.order.OrderDto;
import dto.order.ProductReturnRequest;
import dto.store.ProductIdDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Override
    public List<OrderDto> getOrders(String username) {
        return List.of();
    }

    @Override
    public OrderDto createOrder(CreateNewOrderRequest request) {
        return null;
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) {
        return null;
    }

    @Override
    public OrderDto payOrder(ProductIdDto productId) {
        return null;
    }

    @Override
    public OrderDto payOrderFailed(ProductIdDto productId) {
        return null;
    }

    @Override
    public OrderDto deliveryOrder(ProductIdDto productId) {
        return null;
    }

    @Override
    public OrderDto deliveryOrderFailed(ProductIdDto productId) {
        return null;
    }

    @Override
    public OrderDto completeOrder(ProductIdDto productId) {
        return null;
    }

    @Override
    public OrderDto calculateTotalOrder(ProductIdDto productId) {
        return null;
    }

    @Override
    public OrderDto calculateDeliveryOrder(ProductIdDto productId) {
        return null;
    }

    @Override
    public OrderDto assemblyOrder(ProductIdDto productId) {
        return null;
    }

    @Override
    public OrderDto assemblyOrderFailed(ProductIdDto productId) {
        return null;
    }
}
