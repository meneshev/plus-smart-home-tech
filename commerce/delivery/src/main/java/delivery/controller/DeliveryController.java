package delivery.controller;

import delivery.service.DeliveryService;
import dto.delivery.DeliveryDto;
import dto.order.OrderDto;
import dto.store.UUIDBodyDto;
import feign.delivery.DeliveryOperations;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import util.logging.Loggable;

@Slf4j
@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryOperations {

    private final DeliveryService deliveryService;

    @Loggable
    @PutMapping
    public DeliveryDto createDelivery(@Valid @RequestBody DeliveryDto deliveryDto) {
        return deliveryService.create(deliveryDto);
    }

    @Loggable
    @GetMapping
    public DeliveryDto getDelivery(@Valid @RequestBody UUIDBodyDto deliveryId) {
        return deliveryService.getDelivery(deliveryId);
    }

    @Loggable
    @PostMapping("/successful")
    public void successfulDelivery(@Valid @RequestBody UUIDBodyDto deliveryId) {
        deliveryService.success(deliveryId);
    }

    @Loggable
    @PostMapping("/picked")
    public void pickedDelivery(@Valid @RequestBody UUIDBodyDto deliveryId) {
        deliveryService.picked(deliveryId);
    }

    @Loggable
    @PostMapping("/failed")
    public void failedDelivery(@Valid @RequestBody UUIDBodyDto deliveryId) {
        deliveryService.failed(deliveryId);
    }

    @Loggable
    @PostMapping("/cost")
    public Double createCost(@Valid @RequestBody OrderDto orderDto) {
        return deliveryService.getCost(orderDto);
    }
}
