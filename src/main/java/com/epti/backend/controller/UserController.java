package com.epti.backend.controller;

import com.epti.backend.dto.BaseResponse;
import com.epti.backend.model.User;
import com.epti.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User management APIs")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all users (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<User>>> getAllUsers() {
        log.info("Fetching all users");
        
        List<User> users = userService.getAllUsers();
        
        BaseResponse<List<User>> response = BaseResponse.success(users, "Users retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user details by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<BaseResponse<User>> getUserById(@PathVariable Long id) {
        log.info("Fetching user with id: {}", id);
        
        User user = userService.getUserById(id);
        
        BaseResponse<User> response = BaseResponse.success(user, "User retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile", description = "Retrieve current user's profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved profile"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<BaseResponse<User>> getCurrentUserProfile() {
        User user = userService.getCurrentUser();
        
        BaseResponse<User> response = BaseResponse.success(user, "Profile retrieved successfully");
        return ResponseEntity.ok(response);
    }
}
