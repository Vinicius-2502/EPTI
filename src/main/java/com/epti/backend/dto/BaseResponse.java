package com.epti.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    
    private T data;
    private String message;
    private boolean success;
    private LocalDateTime timestamp;
    private String path;
    
    public static <T> BaseResponse<T> success(T data, String message) {
        return BaseResponse.<T>builder()
                .data(data)
                .message(message)
                .success(true)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> BaseResponse<T> success(T data) {
        return success(data, "Operation completed successfully");
    }
    
    public static <T> BaseResponse<T> success(String message) {
        return success(null, message);
    }
    
    public static <T> BaseResponse<T> error(String message) {
        return BaseResponse.<T>builder()
                .data(null)
                .message(message)
                .success(false)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
