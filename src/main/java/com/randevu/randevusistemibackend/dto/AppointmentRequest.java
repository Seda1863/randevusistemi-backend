package com.randevu.randevusistemibackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for creating a new appointment")
public class AppointmentRequest {
    
    @NotNull
    @Schema(description = "Provider ID", example = "1", required = true)
    private Long providerId;
    
    @NotNull
    @Future(message = "Appointment time must be in the future")
    @Schema(description = "Proposed start time for the appointment (ISO format)", 
           example = "2025-05-01T14:00:00", required = true)
    private LocalDateTime startTime;
    
    @Positive(message = "Duration must be positive")
    @Schema(description = "Duration of appointment in minutes (defaults to provider's recommended duration)", 
           example = "30")
    private Integer durationMinutes;
    
    @Schema(description = "Service requested", example = "Dental Cleaning")
    private String serviceName;
    
    @Schema(description = "Notes for the provider about the appointment", 
           example = "First time visit, having tooth pain")
    private String notes;
}