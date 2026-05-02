package com.epti.backend.model.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDENTE("Pendente"),
    PAGO("Pago");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public static PaymentStatus fromDisplayName(String displayName) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.getDisplayName().equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de pagamento não encontrado: " + displayName);
    }
}
