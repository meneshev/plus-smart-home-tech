package payment.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import payment.dal.entity.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findPaymentByOrderId(UUID orderId);

    Payment getPaymentByPaymentId(UUID paymentId);
}
