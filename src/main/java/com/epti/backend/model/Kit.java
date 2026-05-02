package com.epti.backend.model;

import com.epti.backend.model.enums.Turma;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "kits")
public class Kit extends BaseEntity {

    @NotBlank(message = "Nome do kit é obrigatório")
    @Size(max = 200, message = "Nome do kit deve ter no máximo 200 caracteres")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Preço do kit é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço do kit deve ser maior que zero")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url")
    @Size(max = 500, message = "URL da imagem deve ter no máximo 500 caracteres")
    private String imageUrl;

    @Column(name = "available", nullable = false)
    @Builder.Default
    private boolean available = true;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "kit_products",
        joinColumns = @JoinColumn(name = "kit_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @Builder.Default
    private Set<Product> products = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "kit_turmas",
        joinColumns = @JoinColumn(name = "kit_id"),
        inverseJoinColumns = @JoinColumn(name = "turma_id")
    )
    @Builder.Default
    private Set<Turma> allowedTurmas = new HashSet<>();

    public boolean isAvailableForTurma(Turma turma) {
        return allowedTurmas.isEmpty() || allowedTurmas.contains(turma);
    }

    public BigDecimal calculateIndividualPrice() {
        return products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getSavings() {
        BigDecimal individualTotal = calculateIndividualPrice();
        return individualTotal.subtract(price);
    }

    public BigDecimal getSavingsPercentage() {
        BigDecimal individualTotal = calculateIndividualPrice();
        if (individualTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getSavings().multiply(BigDecimal.valueOf(100))
                .divide(individualTotal, 2, BigDecimal.ROUND_HALF_UP);
    }
}
