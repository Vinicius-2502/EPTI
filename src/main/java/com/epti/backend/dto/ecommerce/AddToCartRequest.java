package com.epti.backend.dto.ecommerce;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartRequest {
    
    @NotNull(message = "Product ID or Kit ID is required")
    private Long itemId;
    
    @NotNull(message = "Item type is required")
    private String itemType; // "PRODUCT" or "KIT"
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
