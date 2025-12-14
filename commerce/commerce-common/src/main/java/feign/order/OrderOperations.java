package feign.order;

import dto.order.CreateNewOrderRequest;
import dto.order.OrderDto;
import dto.order.ProductReturnRequest;
import dto.store.ProductIdDto;
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
    OrderDto payOrder(@RequestBody @Valid ProductIdDto productId);

    @PostMapping("/payment/failed")
    OrderDto payOrderFailed(@RequestBody @Valid ProductIdDto productId);

    @PostMapping("/delivery")
    OrderDto deliveryOrder(@RequestBody @Valid ProductIdDto productId);

    @PostMapping("/delivery/failed")
    OrderDto deliveryOrderFailed(@RequestBody @Valid ProductIdDto productId);

    @PostMapping("/completed")
    OrderDto completedOrder(@RequestBody @Valid ProductIdDto productId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotalOrder(@RequestBody @Valid ProductIdDto productId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryOrder(@RequestBody @Valid ProductIdDto productId);

    @PostMapping("/assembly")
    OrderDto assemblyOrder(@RequestBody @Valid ProductIdDto productId);

    @PostMapping("/assembly/failed")
    OrderDto assemblyOrderFailed(@RequestBody @Valid ProductIdDto productId);
}
