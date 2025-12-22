package payment.service;

import dto.order.OrderDto;
import dto.payment.PaymentDto;
import dto.store.UUIDBodyDto;
import jakarta.validation.Valid;

public interface PaymentService {
    PaymentDto createPayment(@Valid OrderDto order);

    Double getTotalCost(@Valid OrderDto order);

    void refund(@Valid UUIDBodyDto paymentId);

    Double getProductCost(@Valid OrderDto order);

    void failed(@Valid UUIDBodyDto paymentId);
}
