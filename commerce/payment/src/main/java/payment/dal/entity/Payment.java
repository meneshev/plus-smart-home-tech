package payment.dal.entity;

import dto.payment.PaymentState;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "pay")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentId;

    @Column(columnDefinition = "uuid")
    private UUID orderId;

    private Double totalPrice;

    private Double deliveryPrice;

    private Double productPrice;

    private Double feePrice;

    @Enumerated(EnumType.STRING)
    private PaymentState state;
}
