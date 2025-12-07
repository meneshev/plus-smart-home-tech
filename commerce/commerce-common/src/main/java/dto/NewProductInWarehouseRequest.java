package dto;

import jakarta.validation.constraints.DecimalMin;
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
public class NewProductInWarehouseRequest {

    @NotBlank
    @UUID
    private String productId;

    private Boolean fragile;

    @NotNull
    private DimensionDto dimension;

    @NotBlank
    @DecimalMin(value = "1")
    private Double weight;
}