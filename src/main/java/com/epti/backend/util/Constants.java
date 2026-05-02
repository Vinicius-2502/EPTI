package com.epti.backend.util;

public final class Constants {
    
    public static final String DEFAULT_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";
    
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String JWT_HEADER_STRING = "Authorization";
    
    public static final String CORS_ALLOWED_ORIGINS = "http://localhost:4200,http://localhost:3000";
    
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int PASSWORD_MAX_LENGTH = 120;
    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int USERNAME_MAX_LENGTH = 50;
    
    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
