package payment.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import payment.dal.entity.Payment;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    boolean getPaymentByOrderId(UUID orderId);
}
