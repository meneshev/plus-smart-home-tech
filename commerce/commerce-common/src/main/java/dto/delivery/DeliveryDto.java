package dto.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private AddressDto fromAddress;

    @NotBlank
    private AddressDto toAddress;

    @UUID
    @NotBlank
    private String orderId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String deliveryState;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double deliveryWeight;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double deliveryVolume;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean fragile;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double deliveryPrice;
}
