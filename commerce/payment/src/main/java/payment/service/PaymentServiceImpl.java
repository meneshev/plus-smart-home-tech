package payment.service;

import dto.order.OrderDto;
import dto.payment.PaymentDto;
import dto.payment.PaymentState;
import dto.store.UUIDBodyDto;
import feign.delivery.DeliveryClient;
import feign.order.OrderClient;
import feign.shopping.store.ShoppingStoreClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import payment.dal.entity.Payment;
import payment.dal.mapper.PaymentMapper;
import payment.dal.repository.PaymentRepository;
import util.exception.NotFoundException;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final OrderClient orderClient;
    private final ShoppingStoreClient shoppingStoreClient;
    private final DeliveryClient deliveryClient;
    private final PaymentRepository paymentRepository;
    private final double VAT = 0.1;

    @Override
    public PaymentDto createPayment(OrderDto order) {
        Optional<Payment> payment = paymentRepository.findPaymentByOrderId(UUID.fromString(order.getOrderId()));
        if (payment.isPresent()) {
            log.info("Payment already exists for order {}, updating payment...", order.getOrderId());
            payment.get().setOrderId(UUID.fromString(order.getOrderId()));
            payment.get().setProductPrice(getProductCost(order));
            payment.get().setDeliveryPrice(deliveryClient.createCost(order));
            payment.get().setTotalPrice(getTotalCost(order));
            payment.get().setState(PaymentState.PENDING);

        } else {
            log.info("Creating payment for order {}", order.getOrderId());
            payment = Optional.ofNullable(Payment.builder()
                    .orderId(UUID.fromString(order.getOrderId()))
                    .productPrice(getProductCost(order))
                    .deliveryPrice(deliveryClient.createCost(order))
                    .totalPrice(getTotalCost(order))
                    .state(PaymentState.PENDING)
                    .build());
        }

        paymentRepository.save(payment.get());
        return PaymentMapper.toDto(payment.get());
    }

    @Override
    public Double getTotalCost(OrderDto order) {
        Double total = getProductTotal(order.getProducts());

        // НДС
        total += total * VAT;

        // Доставка
        Double deliveryCost = deliveryClient.createCost(order);
        total += deliveryCost;

        return total;
    }

    @Override
    public void refund(UUIDBodyDto paymentId) {
        checkPayment(UUID.fromString(paymentId.getId()));
        Payment payment = paymentRepository.getPaymentByPaymentId(UUID.fromString(paymentId.getId()));
        payment.setState(PaymentState.SUCCESS);
        paymentRepository.save(payment);
        log.info("Payment refund for order {}", payment.getOrderId());
    }

    @Override
    public Double getProductCost(OrderDto order) {
        return getProductTotal(order.getProducts());
    }

    @Override
    public void failed(UUIDBodyDto paymentId) {
        checkPayment(UUID.fromString(paymentId.getId()));
        Payment payment = paymentRepository.getPaymentByPaymentId(UUID.fromString(paymentId.getId()));
        payment.setState(PaymentState.FAILED);
        paymentRepository.save(payment);
        log.info("Payment failed for order {}", payment.getOrderId());
    }

    // стоимость товаров
    private double getProductTotal(Map<String, Long> products) {
        return products.entrySet().stream()
                .mapToDouble(entry -> {
                    Double productPrice = shoppingStoreClient.getProduct(entry.getKey()).getPrice();
                    return productPrice * entry.getValue();
                })
                .sum();
    }

    private void checkPayment(UUID paymentId) {
        if (!paymentRepository.existsById(paymentId)) {
            log.info("Payment not found with id {}", paymentId);
            throw new NotFoundException("Payment not found with id " + paymentId);
        }
    }
}
