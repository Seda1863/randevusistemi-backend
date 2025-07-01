package com.randevu.randevusistemibackend.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * Base exception class for all application specific exceptions.
 */
@Getter
public class ApplicationException extends RuntimeException {
    
    private final String errorCode;
    private final HttpStatus status;
    
    public ApplicationException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
    
    public ApplicationException(String message, String errorCode, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.status = status;
    }
}