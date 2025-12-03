package dto;

import lombok.Data;

@Data
public class ProductDto {
    private String productId;

    //Not null
    private String productName;
    //Not null
    private String description;

    private String imageSrc;
    //Not null
    private QuantityState quantityState;
    //Not null
    private ProductState productState;

    private ProductCategory productCategory;
    //Not null
    private double price;
}
