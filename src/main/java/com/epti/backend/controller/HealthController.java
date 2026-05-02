package com.epti.backend.controller;

import com.epti.backend.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/public")
@Tag(name = "Public", description = "Public APIs")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the API is running")
    public ResponseEntity<BaseResponse<Map<String, Object>>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("application", "EPTI Backend API");
        healthInfo.put("version", "1.0.0");
        
        BaseResponse<Map<String, Object>> response = BaseResponse.success(healthInfo, "API is healthy");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    @Operation(summary = "API info", description = "Get API information")
    public ResponseEntity<BaseResponse<Map<String, Object>>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "EPTI Backend API");
        info.put("description", "Backend API for EPTI project with Angular integration");
        info.put("version", "1.0.0");
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("springBootVersion", "3.3.0");
        
        BaseResponse<Map<String, Object>> response = BaseResponse.success(info, "API information retrieved successfully");
        return ResponseEntity.ok(response);
    }
}
