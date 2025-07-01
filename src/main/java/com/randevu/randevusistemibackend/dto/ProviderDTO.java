package com.randevu.randevusistemibackend.dto;

import com.randevu.randevusistemibackend.model.Provider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO for Provider information
 * Controls what provider data is exposed in API responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Provider information")
public class ProviderDTO {
    
    @Schema(description = "Provider ID", example = "42")
    private Long id;
    
    @Schema(description = "Username", example = "drsmith")
    private String username;
    
    @Schema(description = "Full name", example = "Dr. John Smith")
    private String fullName;
    
    @Schema(description = "Email address", example = "drsmith@example.com")
    private String email;
    
    @Schema(description = "Phone number", example = "+90 555 123 4567")
    private String phone;
    
    @Schema(description = "Business name", example = "Smith Medical Services")
    private String businessName;
    
    @Schema(description = "Description of the provider", example = "Specialized in family medicine with 15 years of experience")
    private String description;
    
    @Schema(description = "Services offered by the provider")
    private Set<String> services;
    
    @Schema(description = "Average appointment duration in minutes", example = "30")
    private Integer averageAppointmentDurationMinutes;
    
    @Schema(description = "Whether the provider is currently available for appointments", example = "true")
    private boolean available;
    
    @Schema(description = "City where the provider is located", example = "Istanbul")
    private String city;
    
    /**
     * Convert Provider entity to ProviderDTO
     */
    public static ProviderDTO fromEntity(Provider provider) {
        ProviderDTO dto = new ProviderDTO();
        dto.setId(provider.getId());
        dto.setUsername(provider.getUsername());
        dto.setFullName(provider.getFullName());
        dto.setEmail(provider.getEmail());
        dto.setPhone(provider.getPhone());
        dto.setBusinessName(provider.getBusinessName());
        dto.setDescription(provider.getDescription());
        dto.setServices(provider.getServices());
        dto.setAverageAppointmentDurationMinutes(provider.getAverageAppointmentDurationMinutes());
        dto.setAvailable(provider.isAvailable());
        
        // Add city information if address is available
        if (provider.getAddress() != null) {
            dto.setCity(provider.getAddress().getCity());
        }
        
        return dto;
    }
}
