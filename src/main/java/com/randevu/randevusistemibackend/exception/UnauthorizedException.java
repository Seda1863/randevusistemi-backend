package com.randevu.randevusistemibackend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication fails or is not provided.
 */
public class UnauthorizedException extends ApplicationException {
    
    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }
    
    public UnauthorizedException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.UNAUTHORIZED);
    }
}