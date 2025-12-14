package dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    @NotBlank
    @UUID
    private String orderId;

    @UUID
    private String shoppingCartId;

    @NotNull
    private Map<String, Long> products;

    @UUID
    private String paymentId;

    @UUID
    private String deliveryId;

    //TODO valid enum?
    private String orderState;

    private Double deliveryWeight;

    private Double deliveryVolume;

    private Boolean fragile;

    private Double totalPrice;

    private Double deliveryPrice;

    private Double productPrice;
}