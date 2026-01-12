package feign.order;

import dto.order.CreateNewOrderRequest;
import dto.order.OrderDto;
import dto.order.ProductReturnRequest;
import dto.store.UUIDBodyDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface OrderOperations {
    @GetMapping
    List<OrderDto> getOrders(@RequestParam String username);

    @PutMapping
    OrderDto createOrder(@RequestBody @Valid CreateNewOrderRequest request);

    @PostMapping("/return")
    OrderDto returnOrder(@RequestBody @Valid ProductReturnRequest request);

    @PostMapping("/payment")
    OrderDto payOrder(@RequestBody @Valid UUIDBodyDto productId);

    @PostMapping("/payment/failed")
    OrderDto payOrderFailed(@RequestBody @Valid UUIDBodyDto productId);

    @PostMapping("/delivery")
    OrderDto deliveryOrder(@RequestBody @Valid UUIDBodyDto productId);

    @PostMapping("/delivery/failed")
    OrderDto deliveryOrderFailed(@RequestBody @Valid UUIDBodyDto productId);

    @PostMapping("/completed")
    OrderDto completedOrder(@RequestBody @Valid UUIDBodyDto productId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotalOrder(@RequestBody @Valid UUIDBodyDto productId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryOrder(@RequestBody @Valid UUIDBodyDto productId);

    @PostMapping("/assembly")
    OrderDto assemblyOrder(@RequestBody @Valid UUIDBodyDto productId);

    @PostMapping("/assembly/failed")
    OrderDto assemblyOrderFailed(@RequestBody @Valid UUIDBodyDto productId);
}
