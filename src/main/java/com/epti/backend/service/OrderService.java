package com.epti.backend.service;

import com.epti.backend.exception.BadRequestException;
import com.epti.backend.exception.ResourceNotFoundException;
import com.epti.backend.model.*;
import com.epti.backend.model.enums.PaymentStatus;
import com.epti.backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserService userService;
    private final AuditService auditService;

    private static final String PIX_KEY = "epti-evento-pix@example.com";

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByPaymentStatus(PaymentStatus status) {
        log.info("Fetching orders with payment status: {}", status);
        return orderRepository.findByPaymentStatusOrderByCreatedAtDesc(status);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersFromUnpaidUsers() {
        log.info("Fetching orders from unpaid users");
        return orderRepository.findOrdersFromUnpaidUsers();
    }

    @Transactional(readOnly = true)
    public List<Order> getOldestPendingOrders() {
        log.info("Fetching oldest pending orders");
        return orderRepository.findOldestPendingOrders();
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        log.info("Fetching order with id: {}", id);
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    @Transactional(readOnly = true)
    public Order getOrderByOrderNumber(String orderNumber) {
        log.info("Fetching order with order number: {}", orderNumber);
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
    }

    @Transactional(readOnly = true)
    public List<Order> getUserOrders(User user) {
        log.info("Fetching orders for user: {}", user.getUsername());
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Order createOrderFromCart(User user) {
        log.info("Creating order from cart for user: {}", user.getUsername());
        
        // Check if user already has a pending order
        userService.getCurrentUser();
        
        List<CartItem> cartItems = cartService.getCartItemsForCheckout(user);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cannot create order from empty cart");
        }
        
        // Create order
        Order order = Order.builder()
                .user(user)
                .paymentStatus(PaymentStatus.PENDENTE)
                .pixKey(PIX_KEY)
                .build();
        
        // Add items to order
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .kit(cartItem.getKit())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .totalPrice(cartItem.getTotalPrice())
                    .build();
            
            order.addItem(orderItem);
        }
        
        Order savedOrder = orderRepository.save(order);
        
        // Clear cart after order creation
        cartService.clearCart(user);
        
        auditService.logAction("ORDER_CREATED", user, "Created order " + savedOrder.getOrderNumber() + " with total " + savedOrder.getTotalAmount());
        
        log.info("Order {} created successfully for user: {}", savedOrder.getOrderNumber(), user.getUsername());
        return savedOrder;
    }

    public Order markOrderAsPaid(Long orderId, String paymentProofUrl) {
        log.info("Marking order {} as paid", orderId);
        
        Order order = getOrderById(orderId);
        
        if (order.isPaid()) {
            throw new BadRequestException("Order is already paid");
        }
        
        order.markAsPaid();
        order.setPaymentProofUrl(paymentProofUrl);
        
        Order savedOrder = orderRepository.save(order);
        
        // Update user payment status
        User user = order.getUser();
        user.setHasPaid(true);
        userService.updateUser(user);
        
        auditService.logAction("ORDER_PAID", user, "Order " + order.getOrderNumber() + " marked as paid");
        
        log.info("Order {} marked as paid for user: {}", order.getOrderNumber(), user.getUsername());
        return savedOrder;
    }

    public Order updateOrderPaymentProof(Long orderId, String paymentProofUrl) {
        log.info("Updating payment proof for order: {}", orderId);
        
        Order order = getOrderById(orderId);
        
        if (order.isPaid()) {
            throw new BadRequestException("Cannot update payment proof for paid order");
        }
        
        order.setPaymentProofUrl(paymentProofUrl);
        Order savedOrder = orderRepository.save(order);
        
        auditService.logAction("PAYMENT_PROOF_UPDATED", order.getUser(), "Updated payment proof for order " + order.getOrderNumber());
        
        return savedOrder;
    }

    public Order addNotesToOrder(Long orderId, String notes) {
        log.info("Adding notes to order: {}", orderId);
        
        Order order = getOrderById(orderId);
        order.setNotes(notes);
        Order savedOrder = orderRepository.save(order);
        
        auditService.logAction("ORDER_NOTES_ADDED", order.getUser(), "Added notes to order " + order.getOrderNumber());
        
        return savedOrder;
    }

    @Transactional(readOnly = true)
    public long countOrdersByPaymentStatus(PaymentStatus status) {
        return orderRepository.countByPaymentStatus(status);
    }

    @Transactional(readOnly = true)
    public List<User> getUsersWithPaymentStatus(PaymentStatus status) {
        return orderRepository.findUsersWithPaymentStatus(status);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        List<Order> paidOrders = orderRepository.findByPaymentStatus(PaymentStatus.PAGO);
        return paidOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public BigDecimal getPendingRevenue() {
        List<Order> pendingOrders = orderRepository.findByPaymentStatus(PaymentStatus.PENDENTE);
        return pendingOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public boolean hasUserPendingOrder(User user) {
        return orderRepository.findByUserAndPaymentStatus(user, PaymentStatus.PENDENTE).isPresent();
    }

    @Transactional(readOnly = true)
    public Order getUserPendingOrder(User user) {
        return orderRepository.findByUserAndPaymentStatus(user, PaymentStatus.PENDENTE)
                .orElseThrow(() -> new ResourceNotFoundException("No pending order found for user"));
    }
}
