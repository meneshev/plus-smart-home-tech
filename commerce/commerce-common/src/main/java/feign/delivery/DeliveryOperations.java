package feign.delivery;

import dto.delivery.DeliveryDto;
import dto.order.OrderDto;
import dto.store.UUIDBodyDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

public interface DeliveryOperations {

    @PutMapping
    DeliveryDto createDelivery(@Valid @RequestBody DeliveryDto deliveryDto);

    @PostMapping("/successful")
    void successfulDelivery(@Valid @RequestBody UUIDBodyDto deliveryId);

    @PostMapping("/picked")
    void pickedDelivery(@Valid @RequestBody UUIDBodyDto deliveryId);

    @PostMapping("/failed")
    void failedDelivery(@Valid @RequestBody UUIDBodyDto deliveryId);

    @PostMapping("/cost")
    Double createCost(@Valid @RequestBody OrderDto orderDto);

    @GetMapping("/{deliveryId}")
    DeliveryDto getDelivery(@PathVariable String deliveryId);
}