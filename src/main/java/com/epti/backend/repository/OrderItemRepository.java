package com.epti.backend.repository;

import com.epti.backend.model.OrderItem;
import com.epti.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrder(Order order);
    
    List<OrderItem> findByOrderOrderByCreatedAtAsc(Order order);
    
    void deleteByOrder(Order order);
}
