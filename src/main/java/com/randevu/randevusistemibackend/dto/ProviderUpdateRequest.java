package com.randevu.randevusistemibackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating provider profile information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for updating provider information")
public class ProviderUpdateRequest {
    
    @Schema(description = "Full name of the provider", example = "Dr. John Smith")
    private String fullName;
    
    @Schema(description = "Phone number of the provider", example = "+90 555 123 4567")
    private String phone;
    
    @Schema(description = "Business name of the provider", example = "Smith Medical Services")
    private String businessName;
    
    @Schema(description = "Description of services offered by the provider", example = "Specialized in family medicine with 15 years of experience.")
    private String description;
    
    @Schema(description = "Average appointment duration in minutes", example = "30")
    private Integer averageAppointmentDurationMinutes;
    
    // Address fields
    @Schema(description = "Street address of the provider location", example = "123 Main St")
    private String streetAddress;
    
    @Schema(description = "City of the provider location", example = "Istanbul")
    private String city;
    
    @Schema(description = "State/province of the provider location", example = "Istanbul")
    private String state;
    
    @Schema(description = "Postal code of the provider location", example = "34000")
    private String postalCode;
}