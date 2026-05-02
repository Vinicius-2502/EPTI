package com.epti.backend.exception;

import com.epti.backend.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        log.error("Resource not found: {}", ex.getMessage());
        
        BaseResponse<Object> response = BaseResponse.error(ex.getMessage());
        response.setPath(request.getDescription(false));
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BaseResponse<Object>> handleBadRequestException(
            BadRequestException ex, WebRequest request) {
        
        log.error("Bad request: {}", ex.getMessage());
        
        BaseResponse<Object> response = BaseResponse.error(ex.getMessage());
        response.setPath(request.getDescription(false));
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponse<Object>> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {
        
        log.error("Bad credentials: {}", ex.getMessage());
        
        BaseResponse<Object> response = BaseResponse.error("Invalid username or password");
        response.setPath(request.getDescription(false));
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        
        log.error("Access denied: {}", ex.getMessage());
        
        BaseResponse<Object> response = BaseResponse.error("You do not have permission to access this resource");
        response.setPath(request.getDescription(false));
        
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.error("Validation errors: {}", errors);
        
        BaseResponse<Object> response = BaseResponse.error("Validation failed");
        response.setData(errors);
        response.setPath(request.getDescription(false));
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        
        log.error("Unexpected error occurred", ex);
        
        BaseResponse<Object> response = BaseResponse.error("An unexpected error occurred. Please try again later.");
        response.setPath(request.getDescription(false));
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
