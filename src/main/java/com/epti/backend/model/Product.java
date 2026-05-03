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
@Table(name = "products")
public class Product extends BaseEntity {

    @NotBlank(message = "Nome do produto é obrigatório")
    @Size(max = 200, message = "Nome do produto deve ter no máximo 200 caracteres")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url")
    @Size(max = 500, message = "URL da imagem deve ter no máximo 500 caracteres")
    private String imageUrl;

    @Column(name = "available", nullable = false)
    @Builder.Default
    private boolean available = true;

    @ManyToMany(mappedBy = "products")
    @Builder.Default
    private Set<Kit> kits = new HashSet<>();

    @ElementCollection
    @CollectionTable(
        name = "product_turmas",
        joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "turma")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<Turma> allowedTurmas = new HashSet<>();

    @Column(name = "kit_discount_percentage")
    @Builder.Default
    private BigDecimal kitDiscountPercentage = BigDecimal.ZERO;

    public boolean isAvailableForTurma(Turma turma) {
        return allowedTurmas.isEmpty() || allowedTurmas.contains(turma);
    }

    public BigDecimal getKitPrice() {
        if (kitDiscountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = price.multiply(kitDiscountPercentage).divide(BigDecimal.valueOf(100));
            return price.subtract(discount);
        }
        return price;
    }
}
