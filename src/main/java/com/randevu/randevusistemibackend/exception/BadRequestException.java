package com.randevu.randevusistemibackend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a client sends an invalid request.
 */
public class BadRequestException extends ApplicationException {
    
    public BadRequestException(String message) {
        super(message, "BAD_REQUEST", HttpStatus.BAD_REQUEST);
    }
    
    public BadRequestException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.BAD_REQUEST);
    }
}