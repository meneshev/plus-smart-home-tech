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
import util.exception.ValidationException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final OrderClient orderClient;
    private final ShoppingStoreClient shoppingStoreClient;
    private final DeliveryClient deliveryClient;
    private final PaymentRepository paymentRepository;
    private final double VAT = 0.1;

    //TODO доделать payment, затем order

    @Override
    public PaymentDto createPayment(OrderDto order) {
        if (paymentRepository.getPaymentByOrderId(UUID.fromString(order.getOrderId()))) {
            log.info("Payment already exists for order {}", order.getOrderId());
            throw new ValidationException("Payment already exists for order " + order.getOrderId());
            // TODO либо просто пересчитываем стоимость?
        }

        Payment newPayment = Payment.builder()
                .orderId(UUID.fromString(order.getOrderId()))
                .productPrice(getProductCost(order))
                .deliveryPrice(deliveryClient.createCost(order))
                .totalPrice(getTotalCost(order))
                .state(PaymentState.PENDING)
                .build();

        paymentRepository.save(newPayment);

        log.info("Payment created for order {}", order.getOrderId());

        return PaymentMapper.toDto(newPayment);
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
    }

    @Override
    public Double getProductCost(OrderDto order) {
        return getProductTotal(order.getProducts());
    }

    @Override
    public void failed(UUIDBodyDto paymentId) {
        checkPayment(UUID.fromString(paymentId.getId()));
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

    private void checkOrder(UUID orderId) {
    }
}
