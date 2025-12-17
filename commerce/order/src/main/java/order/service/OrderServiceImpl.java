package order.service;

import dto.cart.ShoppingCartDto;
import dto.order.CreateNewOrderRequest;
import dto.order.OrderDto;
import dto.order.OrderState;
import dto.order.ProductReturnRequest;
import dto.store.ProductIdDto;
import feign.shopping.cart.ShoppingCartClient;
import feign.warehouse.WarehouseClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import order.dal.entity.Order;
import order.dal.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private OrderRepository orderRepository;
    private ShoppingCartClient shoppingCartClient;
    private WarehouseClient warehouseClient;

    @Override
    public List<OrderDto> getOrders(String username) {
        ShoppingCartDto cart = shoppingCartClient.getCart(username);
        //return
        orderRepository.findAllByShoppingCartId(UUID.fromString(cart.getShoppingCartId()));
        // get delivery
        // get payment
        // mapToDto
        //
        return List.of();
    }

    @Override
    public OrderDto createOrder(CreateNewOrderRequest request) {
        Order newOrder = Order.builder()
                .shoppingCartId(UUID.fromString(request.getShoppingCart().getShoppingCartId()))
                .products(request.getShoppingCart().getProducts().entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> UUID.fromString(entry.getKey()),
                                Map.Entry::getValue
                        )))
                .orderState(OrderState.NEW)
                .build();

        // create delivery
        // create payment
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
