package com.epti.backend.controller;

import com.epti.backend.dto.BaseResponse;
import com.epti.backend.dto.ecommerce.MarkOrderPaidRequest;
import com.epti.backend.dto.ecommerce.OrderRequest;
import com.epti.backend.model.Order;
import com.epti.backend.model.User;
import com.epti.backend.model.enums.PaymentStatus;
import com.epti.backend.service.OrderService;
import com.epti.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @PostMapping("/create")
    @Operation(summary = "Create order from cart", description = "Create order from user's cart items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Empty cart or user has pending order"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Order>> createOrderFromCart() {
        var currentUser = userService.getCurrentUser();
        log.info("Creating order from cart for user: {}", currentUser.getUsername());
        
        Order order = orderService.createOrderFromCart(currentUser);
        
        BaseResponse<Order> response = BaseResponse.success(order, "Order created successfully");
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Get user orders", description = "Retrieve authenticated user's orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<List<Order>>> getUserOrders() {
        var currentUser = userService.getCurrentUser();
        log.info("Fetching orders for user: {}", currentUser.getUsername());
        
        List<Order> orders = orderService.getUserOrders(currentUser);
        
        BaseResponse<List<Order>> response = BaseResponse.success(orders, "User orders retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-orders/pending")
    @Operation(summary = "Get user pending order", description = "Retrieve user's pending order if exists")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Order>> getUserPendingOrder() {
        var currentUser = userService.getCurrentUser();
        
        if (!orderService.hasUserPendingOrder(currentUser)) {
            return ResponseEntity.ok(BaseResponse.success(null, "No pending order found"));
        }
        
        Order order = orderService.getUserPendingOrder(currentUser);
        
        BaseResponse<Order> response = BaseResponse.success(order, "Pending order retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/payment-proof")
    @Operation(summary = "Update payment proof", description = "Update payment proof for pending order")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Order>> updatePaymentProof(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderRequest request) {
        
        var currentUser = userService.getCurrentUser();
        log.info("Updating payment proof for order {} by user: {}", orderId, currentUser.getUsername());
        
        Order order = orderService.updateOrderPaymentProof(orderId, request.getPaymentProofUrl());
        
        BaseResponse<Order> response = BaseResponse.success(order, "Payment proof updated successfully");
        return ResponseEntity.ok(response);
    }

    // Admin endpoints
    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieve all orders (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<Order>>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(BaseResponse.success(orders, "All orders retrieved successfully"));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by payment status", description = "Retrieve orders by payment status (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<Order>>> getOrdersByStatus(
            @Parameter(description = "Payment status") @PathVariable PaymentStatus status) {
        
        List<Order> orders = orderService.getOrdersByPaymentStatus(status);
        
        BaseResponse<List<Order>> response = BaseResponse.success(orders, 
                "Orders with status " + status.getDisplayName() + " retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/mark-paid")
    @Operation(summary = "Mark order as paid", description = "Mark order as paid (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Order>> markOrderAsPaid(
            @PathVariable Long orderId,
            @Valid @RequestBody MarkOrderPaidRequest request) {
        
        log.info("Admin marking order {} as paid", orderId);
        
        Order order = orderService.markOrderAsPaid(orderId, request.getPaymentProofUrl());
        
        BaseResponse<Order> response = BaseResponse.success(order, "Order marked as paid successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/notes")
    @Operation(summary = "Add notes to order", description = "Add notes to order (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Order>> addNotesToOrder(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {
        
        String notes = request.get("notes");
        Order order = orderService.addNotesToOrder(orderId, notes);
        
        BaseResponse<Order> response = BaseResponse.success(order, "Notes added to order successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unpaid-users")
    @Operation(summary = "Get unpaid users", description = "Get list of users who haven't paid (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<User>>> getUnpaidUsers() {
        List<User> unpaidUsers = userService.getUnpaidUsersFromParticipatingTurmas();
        
        BaseResponse<List<User>> response = BaseResponse.success(unpaidUsers, "Unpaid users retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get order statistics", description = "Get order statistics (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Map<String, Object>>> getOrderStatistics() {
        long totalOrders = orderService.countOrdersByPaymentStatus(PaymentStatus.PAGO) + 
                           orderService.countOrdersByPaymentStatus(PaymentStatus.PENDENTE);
        long paidOrders = orderService.countOrdersByPaymentStatus(PaymentStatus.PAGO);
        long pendingOrders = orderService.countOrdersByPaymentStatus(PaymentStatus.PENDENTE);
        BigDecimal totalRevenue = orderService.getTotalRevenue();
        BigDecimal pendingRevenue = orderService.getPendingRevenue();
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalOrders", totalOrders);
        statistics.put("paidOrders", paidOrders);
        statistics.put("pendingOrders", pendingOrders);
        statistics.put("totalRevenue", totalRevenue);
        statistics.put("pendingRevenue", pendingRevenue);
        statistics.put("paymentRate", totalOrders > 0 ? (paidOrders * 100.0 / totalOrders) : 0);
        
        BaseResponse<Map<String, Object>> response = BaseResponse.success(statistics, "Order statistics retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieve order details by ID")
    @PreAuthorize("hasRole('ADMIN') or @userService.isOrderOwner(#orderId, authentication.name)")
    public ResponseEntity<BaseResponse<Order>> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        
        BaseResponse<Order> response = BaseResponse.success(order, "Order retrieved successfully");
        return ResponseEntity.ok(response);
    }
}
