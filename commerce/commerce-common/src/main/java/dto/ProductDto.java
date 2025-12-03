package dto;

import jakarta.validation.constraints.DecimalMin;
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
public class ProductDto {

    private String productId;

    @NotBlank
    private String productName;

    @NotBlank
    private String description;

    private String imageSrc;

    @NotBlank
    @ValidEnum(
            enumClass = QuantityState.class,
            values = { "ENDED", "FEW", "ENOUGH", "MANY" },
            message = "Недопустимое значение. Допустимые: {accepted}"
    )
    private String quantityState;

    @NotBlank
    @ValidEnum(
            enumClass = ProductState.class,
            values = { "ACTIVE", "DEACTIVATE" },
            message = "Недопустимое значение. Допустимые: {accepted}"
    )
    private String productState;

    @NotBlank
    @ValidEnum(
            enumClass = ProductCategory.class,
            values = { "LIGHTING", "CONTROL", "SENSORS" },
            message = "Недопустимое значение. Допустимые: {accepted}"
    )
    private String productCategory;

    @NotBlank
    @DecimalMin(value = "1", message = "Минимально допустимая цена товара: 1")
    private double price;
}