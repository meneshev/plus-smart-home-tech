package order.service;

import dto.cart.ShoppingCartDto;
import dto.delivery.DeliveryDto;
import dto.order.CreateNewOrderRequest;
import dto.order.OrderDto;
import dto.order.OrderState;
import dto.order.ProductReturnRequest;
import dto.payment.PaymentDto;
import dto.store.UUIDBodyDto;
import dto.warehouse.AddressDto;
import dto.warehouse.AssemblyProductsForOrderRequest;
import dto.warehouse.BookedProductsDto;
import feign.delivery.DeliveryClient;
import feign.payment.PaymentClient;
import feign.shopping.cart.ShoppingCartClient;
import feign.warehouse.WarehouseClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import order.dal.entity.Order;
import order.dal.mapper.OrderMapper;
import order.dal.repository.OrderRepository;
import org.springframework.stereotype.Service;
import util.exception.NoOrderFoundException;
import util.exception.NotEnoughProductException;
import util.exception.NotFoundException;

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
    private DeliveryClient deliveryClient;
    private PaymentClient paymentClient;

    @Override
    public List<OrderDto> getOrders(String username) {
        ShoppingCartDto cart = shoppingCartClient.getCart(username);
        if (cart == null) {
            log.error("Cart not found for username {}", username);
            throw new NotFoundException("Cart not found for username " + username);
        }

        List<Order> orders = orderRepository.findAllByShoppingCartId(UUID.fromString(cart.getShoppingCartId()));
        if (orders.isEmpty()) {
            log.info("No orders found for username {}", username);
            throw new NoOrderFoundException("No orders found for username " + username);
        }

        return orders.stream()
                .map(dbOrder -> {
                    OrderDto orderDto = OrderMapper.toDto(dbOrder);
                    DeliveryDto deliveryData = deliveryClient.getDelivery(UUIDBodyDto.builder()
                                    .id(orderDto.getDeliveryId())
                                    .build());

                    if (deliveryData == null) {
                        log.error("Delivery data not found for order {}", orderDto.getOrderId());
                    } else {
                        orderDto.setDeliveryWeight(deliveryData.getDeliveryWeight());
                        orderDto.setDeliveryPrice(deliveryData.getDeliveryVolume());
                        orderDto.setFragile(deliveryData.isFragile());
                        orderDto.setDeliveryPrice(deliveryData.getDeliveryPrice());
                    }

                    orderDto.setTotalPrice(paymentClient.getTotalCost(orderDto));
                    orderDto.setProductPrice(paymentClient.getProductCost(orderDto));
                    return orderDto;
                })
                .toList();
    }

    @Override
    public OrderDto createOrder(CreateNewOrderRequest request) {
        BookedProductsDto deliveryInfo = warehouseClient.check(request.getShoppingCart());
        if (deliveryInfo == null) {
            log.error("Failed to create order: products not enough in warehouse. Cart:{}",
                    request.getShoppingCart().getShoppingCartId());
            throw new NotEnoughProductException(String.format("Failed to create order: products not enough in warehouse. Cart:%s",
                    request.getShoppingCart().getShoppingCartId()));
        }

        Order newOrder = Order.builder()
                .shoppingCartId(UUID.fromString(request.getShoppingCart().getShoppingCartId()))
                .products(request.getShoppingCart().getProducts().entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> UUID.fromString(entry.getKey()),
                                Map.Entry::getValue
                        )))
                .orderState(OrderState.NEW)
                .build();

        orderRepository.save(newOrder);
        log.info("New order:{} for cart:{} has been created", newOrder.getOrderId(), request.getShoppingCart().getShoppingCartId());

        AddressDto whAddress = warehouseClient.getAddress();
        if (whAddress == null) {
            log.error("Warehouse address not found");
            throw new RuntimeException("Warehouse address not found");
        }
        DeliveryDto delivery = deliveryClient.createDelivery(DeliveryDto.builder()
                        .orderId(newOrder.getOrderId().toString())
                        .fromAddress(whAddress)
                        .toAddress(request.getDeliveryAddress())
                        .build()
        );

        if (delivery == null) {
            log.error("Delivery for order:{}  wasn't created",  newOrder.getOrderId());
            throw new RuntimeException("Delivery wasn't created");
        }

        OrderDto orderDto = OrderMapper.toDto(newOrder);
        orderDto.setDeliveryId(delivery.getDeliveryId());
        orderDto.setDeliveryWeight(deliveryInfo.getDeliveryWeight());
        orderDto.setDeliveryVolume(deliveryInfo.getDeliveryVolume());
        orderDto.setFragile(deliveryInfo.getFragile());

        PaymentDto payment = paymentClient.createPayment(orderDto);
        if (payment == null) {
            log.error("Payment for order:{} wasn't created",  orderDto.getOrderId());
            throw new RuntimeException("Payment wasn't created");
        }
        orderDto.setPaymentId(payment.getPaymentId());
        orderDto.setTotalPrice(payment.getTotalPayment());
        orderDto.setDeliveryPrice(payment.getDeliveryTotal());
        orderDto.setProductPrice(paymentClient.getProductCost(orderDto));

        return orderDto;
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) {
        checkOrder(request.getOrderId());
        warehouseClient.returnProducts(request.getProducts());
        Order order = orderRepository.getReferenceById(UUID.fromString(request.getOrderId()));
        order.setOrderState(OrderState.PRODUCT_RETURNED);
        orderRepository.save(order);
        log.info("Order:{} has been returned", order.getOrderId());
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto payOrder(UUIDBodyDto orderId) {
        checkOrder(orderId.getId());
        Order order = orderRepository.getReferenceById(UUID.fromString(orderId.getId()));
        order.setOrderState(OrderState.PAID);
        orderRepository.save(order);
        log.info("Order:{} has been payed", order.getOrderId());
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto payOrderFailed(UUIDBodyDto orderId) {
        checkOrder(orderId.getId());
        Order order = orderRepository.getReferenceById(UUID.fromString(orderId.getId()));
        order.setOrderState(OrderState.PAYMENT_FAILED);
        orderRepository.save(order);
        log.info("Order:{} payment has been failed", order.getOrderId());
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto deliveryOrder(UUIDBodyDto orderId) {
        checkOrder(orderId.getId());
        Order order = orderRepository.getReferenceById(UUID.fromString(orderId.getId()));
        order.setOrderState(OrderState.DELIVERED);
        orderRepository.save(order);
        log.info("Order:{} delivery has been successful", order.getOrderId());
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto deliveryOrderFailed(UUIDBodyDto orderId) {
        checkOrder(orderId.getId());
        Order order = orderRepository.getReferenceById(UUID.fromString(orderId.getId()));
        order.setOrderState(OrderState.DELIVERY_FAILED);
        orderRepository.save(order);
        log.info("Order:{} delivery has been failed", order.getOrderId());
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto completeOrder(UUIDBodyDto orderId) {
        checkOrder(orderId.getId());
        Order order = orderRepository.getReferenceById(UUID.fromString(orderId.getId()));
        order.setOrderState(OrderState.COMPLETED);
        orderRepository.save(order);
        log.info("Order:{} has been completed", order.getOrderId());
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto calculateTotalOrder(UUIDBodyDto orderId) {
        checkOrder(orderId.getId());
        OrderDto orderDto = OrderMapper.toDto(orderRepository.getReferenceById(UUID.fromString(orderId.getId())));
        orderDto.setTotalPrice(paymentClient.getTotalCost(orderDto));

        return orderDto;
    }

    @Override
    public OrderDto calculateDeliveryOrder(UUIDBodyDto orderId) {
        checkOrder(orderId.getId());
        OrderDto orderDto = OrderMapper.toDto(orderRepository.getReferenceById(UUID.fromString(orderId.getId())));
        orderDto.setDeliveryPrice(deliveryClient.createCost(orderDto));

        return orderDto;
    }

    @Override
    public OrderDto assemblyOrder(UUIDBodyDto orderId) {
        checkOrder(orderId.getId());
        Order order = orderRepository.getReferenceById(UUID.fromString(orderId.getId()));

        warehouseClient.assemblyProducts(AssemblyProductsForOrderRequest.builder()
                        .orderId(orderId.getId())
                        .products(order.getProducts().entrySet().stream()
                                .collect(Collectors.toMap(
                                        Object::toString,
                                        Map.Entry::getValue
                                ))
                        )
                        .build());

        order.setOrderState(OrderState.ASSEMBLED);
        orderRepository.save(order);

        log.info("Order:{} has been assembled", order.getOrderId());
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto assemblyOrderFailed(UUIDBodyDto orderId) {
        checkOrder(orderId.getId());
        Order order = orderRepository.getReferenceById(UUID.fromString(orderId.getId()));
        order.setOrderState(OrderState.ASSEMBLY_FAILED);
        orderRepository.save(order);
        log.info("Order:{} assembly has been failed", order.getOrderId());
        return OrderMapper.toDto(order);
    }

    private void checkOrder(String orderId) {
        orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new NoOrderFoundException(String.format("OrderId:%s not found", orderId)));
    }
}
