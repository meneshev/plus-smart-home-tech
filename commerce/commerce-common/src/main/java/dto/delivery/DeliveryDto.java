package dto.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import dto.warehouse.AddressDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    private AddressDto fromAddress;

    @NotNull
    private AddressDto toAddress;

    @UUID
    @NotBlank
    private String orderId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String deliveryState;

    private Double deliveryWeight;

    private Double deliveryVolume;

    private boolean fragile;

    private Double deliveryPrice;
}
