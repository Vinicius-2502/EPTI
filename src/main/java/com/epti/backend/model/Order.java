package com.epti.backend.model;

import com.epti.backend.model.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @NotNull(message = "Usuário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;

    @NotNull(message = "Status do pagamento é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "pix_key")
    private String pixKey;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "payment_proof_url")
    private String paymentProofUrl;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    public void generateOrderNumber() {
        if (orderNumber == null) {
            orderNumber = "ORD-" + System.currentTimeMillis() + "-" + getId();
        }
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        calculateTotal();
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
        calculateTotal();
    }

    public void calculateTotal() {
        totalAmount = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isPaid() {
        return paymentStatus == PaymentStatus.PAGO;
    }

    public boolean isPending() {
        return paymentStatus == PaymentStatus.PENDENTE;
    }

    public void markAsPaid() {
        this.paymentStatus = PaymentStatus.PAGO;
        this.paymentDate = LocalDateTime.now();
    }
}
