package feign.payment;

import dto.order.OrderDto;
import dto.payment.PaymentDto;
import dto.store.UUIDBodyDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface PaymentOperations {
    @PostMapping
    PaymentDto createPayment(@RequestBody @Valid OrderDto order);

    @PostMapping("/totalCost")
    Double getTotalCost(@RequestBody @Valid OrderDto order);

    @PostMapping("/refund")
    void refund(@RequestBody @Valid UUIDBodyDto paymentId);

    @PostMapping("/productCost")
    Double getProductCost(@RequestBody @Valid OrderDto order);

    @PostMapping("/failed")
    void failed(@RequestBody @Valid UUIDBodyDto paymentId);
}
