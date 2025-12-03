package dto;

import lombok.Data;

@Data
public class SetProductQuantityStateRequest {
    //Not null
    private String productId;
    // Not null
    private QuantityState quantityState;
}
