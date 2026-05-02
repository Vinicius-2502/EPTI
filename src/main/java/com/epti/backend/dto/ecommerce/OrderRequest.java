package com.epti.backend.dto.ecommerce;

import lombok.Data;

@Data
public class OrderRequest {
    
    private String notes;
    
    private String paymentProofUrl;
}
