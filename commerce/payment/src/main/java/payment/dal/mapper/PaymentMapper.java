package payment.dal.mapper;

import dto.payment.PaymentDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import payment.dal.entity.Payment;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentMapper {

    public static PaymentDto toDto(Payment payment) {
        return PaymentDto.builder()
                .paymentId(payment.getPaymentId().toString())
                .deliveryTotal(payment.getDeliveryPrice())
                .feeTotal(payment.getFeePrice())
                .totalPayment(payment.getTotalPrice())
                .build();
    }
}
