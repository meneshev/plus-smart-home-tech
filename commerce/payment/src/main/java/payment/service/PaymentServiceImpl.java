package payment.service;

import dto.order.OrderDto;
import dto.payment.PaymentDto;
import dto.store.UUIDBodyDto;
import feign.order.OrderClient;
import feign.shopping.store.ShoppingStoreClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final OrderClient orderClient;
    private final ShoppingStoreClient shoppingStoreClient;

    //TODO доделать payment, затем order

    @Override
    public PaymentDto createPayment(OrderDto order) {
        return null;
    }

    @Override
    public Double getTotalCost(OrderDto order) {
        return 0.0;
    }

    @Override
    public void refund(UUIDBodyDto paymentId) {

    }

    @Override
    public Double getProductCost(OrderDto order) {
        return 0.0;
    }

    @Override
    public void failed(UUIDBodyDto paymentId) {

    }
}
