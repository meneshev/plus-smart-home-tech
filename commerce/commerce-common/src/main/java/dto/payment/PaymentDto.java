package dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    @UUID
    private String paymentId;

    private Double totalPayment;

    private Double deliveryTotal;

    private Double feeTotal;
}
