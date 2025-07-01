package com.randevu.randevusistemibackend.dto;

import com.randevu.randevusistemibackend.model.Appointment.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Details of an appointment")
public class AppointmentResponse {
    
    @Schema(description = "Unique appointment ID", example = "1")
    private Long id;
    
    @Schema(description = "User who booked the appointment")
    private UserInfoDTO user;
    
    @Schema(description = "Provider with whom the appointment is booked")
    private ProviderInfoDTO provider;
    
    @Schema(description = "Appointment start time", example = "2025-05-01T14:00:00")
    private LocalDateTime startTime;
    
    @Schema(description = "Appointment end time", example = "2025-05-01T14:30:00")
    private LocalDateTime endTime;
    
    @Schema(description = "Duration of appointment in minutes", example = "30")
    private long durationMinutes;
    
    @Schema(description = "Service requested", example = "Dental Cleaning")
    private String serviceName;
    
    @Schema(description = "Notes for the appointment", example = "First time visit")
    private String notes;
    
    @Schema(description = "Current status of the appointment", example = "CONFIRMED")
    private AppointmentStatus status;
    
    @Schema(description = "When the appointment record was created", example = "2025-04-29T10:15:30")
    private LocalDateTime createdAt;
    
    @Schema(description = "When the appointment record was last updated", example = "2025-04-30T11:20:45")
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoDTO {
        @Schema(description = "User ID", example = "42")
        private Long id;
        
        @Schema(description = "Username", example = "johndoe")
        private String username;
        
        @Schema(description = "Full name", example = "John Doe")
        private String fullName;
        
        @Schema(description = "Email address", example = "john@example.com")
        private String email;
        
        @Schema(description = "Phone number", example = "+90 555 123 4567")
        private String phone;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderInfoDTO {
        @Schema(description = "Provider ID", example = "5")
        private Long id;
        
        @Schema(description = "Username", example = "drsmith")
        private String username;
        
        @Schema(description = "Full name", example = "Dr. Jane Smith")
        private String fullName;
        
        @Schema(description = "Business name", example = "Smith Dental Clinic")
        private String businessName;
        
        @Schema(description = "Provider description", example = "Specialized in family dentistry with 15 years of experience")
        private String description;
        
        @Schema(description = "Email address", example = "drsmith@example.com")
        private String email;
        
        @Schema(description = "Phone number", example = "+90 555 987 6543")
        private String phone;
    }
}