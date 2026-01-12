package payment.controller;

import dto.order.OrderDto;
import dto.payment.PaymentDto;
import dto.store.UUIDBodyDto;
import feign.payment.PaymentOperations;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payment.service.PaymentService;
import util.logging.Loggable;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController implements PaymentOperations {
    private final PaymentService paymentService;

    @Loggable
    @PostMapping
    public PaymentDto createPayment(@RequestBody @Valid OrderDto order) {
        return paymentService.createPayment(order);
    }

    @Loggable
    @PostMapping("/totalCost")
    public Double getTotalCost(@RequestBody @Valid OrderDto order) {
        return paymentService.getTotalCost(order);
    }

    @Loggable
    @PostMapping("/refund")
    public void refund(@RequestBody @Valid UUIDBodyDto paymentId) {
        paymentService.refund(paymentId);
    }

    @Loggable
    @PostMapping("/productCost")
    public Double getProductCost(@RequestBody @Valid OrderDto order) {
        return paymentService.getProductCost(order);
    }

    @Loggable
    @PostMapping("/failed")
    public void failed(@RequestBody @Valid UUIDBodyDto paymentId) {
        paymentService.failed(paymentId);
    }
}
