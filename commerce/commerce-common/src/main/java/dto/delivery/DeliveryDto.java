package dto.delivery;

import dto.warehouse.AddressDto;
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
public class DeliveryDto {
    @UUID
    private String deliveryId;

    @NotBlank
    private AddressDto fromAddress;

    @NotBlank
    private AddressDto toAddress;

    @UUID
    @NotBlank
    private String orderId;

    @NotBlank
    //TODO valid enum?
    private String deliveryState;
}
