package com.epti.backend.repository;

import com.epti.backend.model.Product;
import com.epti.backend.model.enums.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByAvailableTrue();
    
    List<Product> findByAvailableTrueOrderByPriceAsc();
    
    List<Product> findByAvailableTrueOrderByPriceDesc();
    
    @Query("SELECT p FROM Product p WHERE p.available = true AND (:turma IS NULL OR p.allowedTurmas IS EMPTY OR :turma MEMBER OF p.allowedTurmas)")
    List<Product> findAvailableForTurma(@Param("turma") Turma turma);
    
    @Query("SELECT p FROM Product p WHERE p.available = true AND (:turma IS NULL OR p.allowedTurmas IS EMPTY OR :turma MEMBER OF p.allowedTurmas) ORDER BY p.price ASC")
    List<Product> findAvailableForTurmaOrderByPriceAsc(@Param("turma") Turma turma);
    
    @Query("SELECT p FROM Product p WHERE p.available = true AND (:turma IS NULL OR p.allowedTurmas IS EMPTY OR :turma MEMBER OF p.allowedTurmas) ORDER BY p.price DESC")
    List<Product> findAvailableForTurmaOrderByPriceDesc(@Param("turma") Turma turma);
    
    @Query("SELECT p FROM Product p WHERE p.available = true AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<Product> findAvailableByNameContaining(@Param("name") String name);
    
    @Query("SELECT p FROM Product p WHERE p.available = true AND (:turma IS NULL OR p.allowedTurmas IS EMPTY OR :turma MEMBER OF p.allowedTurmas) AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<Product> findAvailableForTurmaByNameContaining(@Param("turma") Turma turma, @Param("name") String name);
    
    boolean existsByName(String name);
    
    Optional<Product> findByName(String name);
}
