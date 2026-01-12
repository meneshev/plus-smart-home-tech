package dto.store;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import util.validation.ValidEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetProductQuantityStateRequest {
    @NotBlank
    private String productId;

    @NotBlank
    @ValidEnum(
            enumClass = QuantityState.class,
            values = { "ENDED", "FEW", "ENOUGH", "MANY" },
            message = "Недопустимое значение. Допустимые: {accepted}"
    )
    private String quantityState;
}