package order.controller;

import dto.order.CreateNewOrderRequest;
import dto.order.OrderDto;
import dto.order.ProductReturnRequest;
import dto.store.UUIDBodyDto;
import feign.order.OrderOperations;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import order.service.OrderService;
import org.springframework.web.bind.annotation.*;
import util.logging.Loggable;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements OrderOperations {
    private final OrderService orderService;

    @GetMapping
    @Loggable
    public List<OrderDto> getOrders(@RequestParam String username) {
        return orderService.getOrders(username);
    }

    @PutMapping
    @Loggable
    public OrderDto createOrder(@RequestBody @Valid CreateNewOrderRequest request) {
        return orderService.createOrder(request);
    }

    @PostMapping("/return")
    @Loggable
    public OrderDto returnOrder(@RequestBody @Valid ProductReturnRequest request) {
        return orderService.returnOrder(request);
    }

    @PostMapping("/payment")
    @Loggable
    public OrderDto payOrder(@RequestBody @Valid UUIDBodyDto productId) {
        return orderService.payOrder(productId);
    }

    @PostMapping("/payment/failed")
    @Loggable
    public OrderDto payOrderFailed(@RequestBody @Valid UUIDBodyDto productId) {
        return orderService.payOrderFailed(productId);
    }

    @PostMapping("/delivery")
    @Loggable
    public OrderDto deliveryOrder(@RequestBody @Valid UUIDBodyDto productId) {
        return orderService.deliveryOrder(productId);
    }

    @PostMapping("/delivery/failed")
    @Loggable
    public OrderDto deliveryOrderFailed(@RequestBody @Valid UUIDBodyDto productId) {
        return orderService.deliveryOrderFailed(productId);
    }

    @PostMapping("/completed")
    @Loggable
    public OrderDto completedOrder(@RequestBody @Valid UUIDBodyDto productId) {
        return orderService.completeOrder(productId);
    }

    @PostMapping("/calculate/total")
    @Loggable
    public OrderDto calculateTotalOrder(@RequestBody @Valid UUIDBodyDto productId) {
        return orderService.calculateTotalOrder(productId);
    }

    @PostMapping("/calculate/delivery")
    @Loggable
    public OrderDto calculateDeliveryOrder(@RequestBody @Valid UUIDBodyDto productId) {
        return orderService.calculateDeliveryOrder(productId);
    }

    @PostMapping("/assembly")
    @Loggable
    public OrderDto assemblyOrder(@RequestBody @Valid UUIDBodyDto productId) {
        return orderService.assemblyOrder(productId);
    }

    @PostMapping("/assembly/failed")
    @Loggable
    public OrderDto assemblyOrderFailed(@RequestBody @Valid UUIDBodyDto productId) {
        return orderService.assemblyOrderFailed(productId);
    }
}
