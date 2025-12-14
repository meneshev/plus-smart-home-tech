package dto.warehouse;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippedToDeliveryRequest {
    @UUID
    @NotBlank
    private String orderId;

    @UUID
    @NotBlank
    private String deliveryId;
}
