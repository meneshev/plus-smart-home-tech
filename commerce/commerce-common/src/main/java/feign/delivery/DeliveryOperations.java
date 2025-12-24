package feign.delivery;

import dto.delivery.DeliveryDto;
import dto.order.OrderDto;
import dto.store.UUIDBodyDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    @GetMapping
    DeliveryDto getDelivery(@Valid @RequestBody UUIDBodyDto deliveryId);
}