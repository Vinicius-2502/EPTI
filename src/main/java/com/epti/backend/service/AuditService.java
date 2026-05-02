package com.epti.backend.service;

import com.epti.backend.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    public void logAction(String action, User user, String details) {
        log.info("AUDIT: {} | User: {} | Details: {} | Timestamp: {}", 
                action, 
                user != null ? user.getUsername() : "ANONYMOUS", 
                details, 
                LocalDateTime.now());
    }

    public void logLoginAttempt(String username, boolean success, String ipAddress) {
        if (success) {
            log.info("AUDIT: LOGIN_SUCCESS | User: {} | IP: {} | Timestamp: {}", 
                    username, ipAddress, LocalDateTime.now());
        } else {
            log.warn("AUDIT: LOGIN_FAILED | User: {} | IP: {} | Timestamp: {}", 
                    username, ipAddress, LocalDateTime.now());
        }
    }

    public void logCartAction(String action, User user, String details) {
        logAction("CART_" + action, user, details);
    }

    public void logOrderAction(String action, User user, String details) {
        logAction("ORDER_" + action, user, details);
    }

    public void logPaymentAction(String action, User user, String details) {
        logAction("PAYMENT_" + action, user, details);
    }

    public void logAdminAction(String action, User user, String details) {
        logAction("ADMIN_" + action, user, details);
    }

    public void logSecurityEvent(String event, String details, String ipAddress) {
        log.warn("AUDIT: SECURITY_EVENT | Event: {} | Details: {} | IP: {} | Timestamp: {}", 
                event, details, ipAddress, LocalDateTime.now());
    }

    public void logSystemEvent(String event, String details) {
        log.info("AUDIT: SYSTEM_EVENT | Event: {} | Details: {} | Timestamp: {}", 
                event, details, LocalDateTime.now());
    }
}
