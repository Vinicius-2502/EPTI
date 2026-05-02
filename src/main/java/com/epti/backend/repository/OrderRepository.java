package com.epti.backend.repository;

import com.epti.backend.model.Order;
import com.epti.backend.model.User;
import com.epti.backend.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByUser(User user);
    
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    
    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);
    
    List<Order> findByPaymentStatusOrderByCreatedAtDesc(PaymentStatus paymentStatus);
    
    @Query("SELECT o FROM Order o WHERE o.paymentStatus = :status AND o.user.hasPaid = false")
    List<Order> findPendingOrdersFromUnpaidUsers(@Param("status") PaymentStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.paymentStatus = :status")
    Optional<Order> findByUserAndPaymentStatus(@Param("user") User user, @Param("status") PaymentStatus status);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.paymentStatus = :status")
    long countByPaymentStatus(@Param("status") PaymentStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.orderNumber = :orderNumber")
    Optional<Order> findByOrderNumber(@Param("orderNumber") String orderNumber);
    
    boolean existsByOrderNumber(String orderNumber);
    
    @Query("SELECT o FROM Order o WHERE o.user.hasPaid = false")
    List<Order> findOrdersFromUnpaidUsers();
    
    @Query("SELECT DISTINCT o.user FROM Order o WHERE o.paymentStatus = :status")
    List<User> findUsersWithPaymentStatus(@Param("status") PaymentStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.user.hasPaid = false ORDER BY o.createdAt ASC")
    List<Order> findOldestPendingOrders();
}
