package com.epti.backend.controller;

import com.epti.backend.dto.BaseResponse;
import com.epti.backend.dto.auth.LoginRequest;
import com.epti.backend.dto.auth.LoginResponse;
import com.epti.backend.dto.auth.RegisterRequest;
import com.epti.backend.model.User;
import com.epti.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticate user with username and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<BaseResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        
        LoginResponse loginResponse = userService.login(loginRequest);
        
        BaseResponse<LoginResponse> response = BaseResponse.success(loginResponse, "Login successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Register a new user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "User already exists or validation failed")
    })
    public ResponseEntity<BaseResponse<User>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registration attempt for user: {}", registerRequest.getUsername());
        
        User user = userService.register(registerRequest);
        
        BaseResponse<User> response = BaseResponse.success(user, "User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get details of the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user details"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<BaseResponse<User>> getCurrentUser() {
        User user = userService.getCurrentUser();
        
        BaseResponse<User> response = BaseResponse.success(user, "User details retrieved successfully");
        return ResponseEntity.ok(response);
    }
}
