package com.randevu.randevusistemibackend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response for API errors")
public class ApiErrorResponse {
    
    @Schema(description = "HTTP status code", example = "400")
    private int status;
    
    @Schema(description = "HTTP status reason phrase", example = "Bad Request")
    private String error;
    
    @Schema(description = "Application-specific error code", example = "USER_NOT_FOUND")
    private String code;
    
    @Schema(description = "Error message", example = "User with id 123 not found")
    private String message;
    
    @Schema(description = "Path that caused the error", example = "/api/users/123")
    private String path;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Schema(description = "Timestamp when the error occurred", example = "2025-04-10T14:30:15.123")
    private LocalDateTime timestamp;
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(description = "List of validation errors")
    private List<ValidationError> errors = new ArrayList<>();
    
    public void addValidationError(String field, String message) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(new ValidationError(field, message));
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Validation error details")
    public static class ValidationError {
        @Schema(description = "Field that failed validation", example = "email")
        private String field;
        
        @Schema(description = "Validation error message", example = "must be a well-formed email address")
        private String message;
    }
    
    public static ApiErrorResponse of(HttpStatus status, String message, String path) {
        return ApiErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}