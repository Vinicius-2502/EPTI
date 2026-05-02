package com.epti.backend.repository;

import com.epti.backend.model.CartItem;
import com.epti.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> findByUser(User user);
    
    List<CartItem> findByUserOrderByCreatedAtDesc(User user);
    
    @Query("SELECT ci FROM CartItem ci WHERE ci.user = :user AND (ci.product IS NOT NULL OR ci.kit IS NOT NULL)")
    List<CartItem> findValidCartItemsByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.user = :user")
    long countByUser(@Param("user") User user);
    
    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.user = :user")
    Long sumQuantityByUser(@Param("user") User user);
    
    @Query("SELECT ci FROM CartItem ci WHERE ci.user = :user AND ci.product.id = :productId")
    Optional<CartItem> findByUserAndProductId(@Param("user") User user, @Param("productId") Long productId);
    
    @Query("SELECT ci FROM CartItem ci WHERE ci.user = :user AND ci.kit.id = :kitId")
    Optional<CartItem> findByUserAndKitId(@Param("user") User user, @Param("kitId") Long kitId);
    
    void deleteByUser(User user);
    
    @Query("SELECT ci FROM CartItem ci WHERE ci.user = :user AND (ci.product IS NOT NULL OR ci.kit IS NOT NULL)")
    List<CartItem> findCartItemsForCheckout(@Param("user") User user);
}
