package com.randevu.randevusistemibackend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a user is authenticated but lacks permission to access a resource.
 */
public class ForbiddenException extends ApplicationException {
    
    public ForbiddenException(String message) {
        super(message, "FORBIDDEN", HttpStatus.FORBIDDEN);
    }
    
    public ForbiddenException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.FORBIDDEN);
    }
}