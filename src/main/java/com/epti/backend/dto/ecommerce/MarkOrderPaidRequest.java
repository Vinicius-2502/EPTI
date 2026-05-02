package com.epti.backend.dto.ecommerce;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MarkOrderPaidRequest {
    
    @NotBlank(message = "Payment proof URL is required")
    private String paymentProofUrl;
}
