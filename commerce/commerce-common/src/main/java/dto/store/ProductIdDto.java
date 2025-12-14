package dto.store;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductIdDto {
    @NotBlank
    @UUID
    private String productId;
}
