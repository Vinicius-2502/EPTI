package com.epti.backend.repository;

import com.epti.backend.model.Kit;
import com.epti.backend.model.enums.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KitRepository extends JpaRepository<Kit, Long> {
    
    List<Kit> findByAvailableTrue();
    
    List<Kit> findByAvailableTrueOrderByPriceAsc();
    
    List<Kit> findByAvailableTrueOrderByPriceDesc();
    
    @Query("SELECT k FROM Kit k WHERE k.available = true AND (:turma IS NULL OR k.allowedTurmas IS EMPTY OR :turma MEMBER OF k.allowedTurmas)")
    List<Kit> findAvailableForTurma(@Param("turma") Turma turma);
    
    @Query("SELECT k FROM Kit k WHERE k.available = true AND (:turma IS NULL OR k.allowedTurmas IS EMPTY OR :turma MEMBER OF k.allowedTurmas) ORDER BY k.price ASC")
    List<Kit> findAvailableForTurmaOrderByPriceAsc(@Param("turma") Turma turma);
    
    @Query("SELECT k FROM Kit k WHERE k.available = true AND (:turma IS NULL OR k.allowedTurmas IS EMPTY OR :turma MEMBER OF k.allowedTurmas) ORDER BY k.price DESC")
    List<Kit> findAvailableForTurmaOrderByPriceDesc(@Param("turma") Turma turma);
    
    @Query("SELECT k FROM Kit k WHERE k.available = true AND (LOWER(k.name) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(k.description) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<Kit> findAvailableByNameContaining(@Param("name") String name);
    
    @Query("SELECT k FROM Kit k WHERE k.available = true AND (:turma IS NULL OR k.allowedTurmas IS EMPTY OR :turma MEMBER OF k.allowedTurmas) AND (LOWER(k.name) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(k.description) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<Kit> findAvailableForTurmaByNameContaining(@Param("turma") Turma turma, @Param("name") String name);
    
    boolean existsByName(String name);
    
    Optional<Kit> findByName(String name);
    
    @Query("SELECT k FROM Kit k JOIN k.products p WHERE p.id = :productId")
    List<Kit> findKitsContainingProduct(@Param("productId") Long productId);
}
