package com.epti.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastResetTime = new ConcurrentHashMap<>();

    // Rate limit: 20 requests per minute per user
    private static final int RATE_LIMIT = 20;
    private static final long TIME_WINDOW_MS = 60 * 1000; // 1 minute

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        String clientIdentifier = getClientIdentifier(request);
        long currentTime = System.currentTimeMillis();

        // Reset counter if time window has passed
        lastResetTime.computeIfAbsent(clientIdentifier, k -> currentTime);

        if (currentTime - lastResetTime.get(clientIdentifier) > TIME_WINDOW_MS) {
            requestCounts.put(clientIdentifier, new AtomicInteger(0));
            lastResetTime.put(clientIdentifier, currentTime);
        }

        // Check rate limit
        AtomicInteger counter = requestCounts.computeIfAbsent(clientIdentifier, k -> new AtomicInteger(0));
        int currentCount = counter.incrementAndGet();

        if (currentCount > RATE_LIMIT) {
            log.warn("Rate limit exceeded for {}: {} requests in last minute", clientIdentifier, currentCount);

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                    "success": false,
                    "message": "Rate limit exceeded. Maximum 20 requests per minute allowed.",
                    "timestamp": "%s"
                }
                """.formatted(java.time.LocalDateTime.now()));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIdentifier(HttpServletRequest request) {
        // Try to get authenticated user first
        String username = request.getRemoteUser();
        if (username != null && !username.isEmpty()) {
            return "user:" + username;
        }

        // Fall back to IP address
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }

        return "ip:" + ipAddress;
    }
}
