package com.epti.backend.model;

import com.epti.backend.model.enums.Turma;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "email")
})
public class User extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "username")
    private String username;

    @NotBlank
    @Size(max = 100)
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "Email deve ser válido")
    @Column(name = "email")
    private String email;

    @NotBlank
    @Size(max = 120)
    @Column(name = "password")
    private String password;

    @NotBlank
    @Size(max = 100, message = "Nome completo é obrigatório")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotNull(message = "Turma é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(name = "turma", nullable = false)
    private Turma turma;

    @Column(name = "first_name")
    @Size(max = 50)
    private String firstName;

    @Column(name = "last_name")
    @Size(max = 50)
    private String lastName;

    @Column(name = "enabled")
    @Builder.Default
    private boolean enabled = true;

    @Column(name = "has_paid")
    @Builder.Default
    private boolean hasPaid = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CartItem> cartItems = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private Set<Order> orders = new HashSet<>();

    @PrePersist
    @PreUpdate
    public void extractNameFromFullName() {
        if (fullName != null && (firstName == null || lastName == null)) {
            String[] names = fullName.trim().split("\\s+", 2);
            this.firstName = names.length > 0 ? names[0] : "";
            this.lastName = names.length > 1 ? names[1] : "";
        }
    }

    public boolean isInstitutionalEmail() {
        return email != null && email.endsWith("@aluno.ce.gov.br");
    }

    public boolean isParticipatingTurma() {
        return Turma.isParticipatingTurma(turma);
    }
}
