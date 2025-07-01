package com.randevu.randevusistemibackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for provider registration")
public class ProviderRegisterRequest {
    
    @NotBlank
    @Size(min = 3, max = 20)
    @Schema(description = "Username for the provider account", example = "drsmith")
    private String username;
    
    @NotBlank
    @Size(min = 6, max = 40)
    @Schema(description = "Password for the provider account")
    private String password;
    
    @NotBlank
    @Size(max = 50)
    @Email
    @Schema(description = "Email address for the provider account", example = "drsmith@example.com")
    private String email;
    
    @NotBlank
    @Schema(description = "Full name of the provider", example = "Dr. John Smith")
    private String fullName;
    
    @Schema(description = "Phone number of the provider", example = "+90 555 123 4567")
    private String phone;
    
    @Schema(description = "Business name of the provider", example = "Smith Medical Services")
    private String businessName;
    
    @Schema(description = "Description of services offered by the provider", example = "Specialized in family medicine with 15 years of experience.")
    private String description;
    
    @Schema(description = "Street address of the provider location", example = "123 Main St")
    private String streetAddress;
    
    @Schema(description = "City of the provider location", example = "Istanbul")
    private String city;
    
    @Schema(description = "State/province of the provider location", example = "Istanbul")
    private String state;
    
    @Schema(description = "Postal code of the provider location", example = "34000")
    private String postalCode;
    
    @Schema(description = "Average appointment duration in minutes", example = "30")
    private Integer averageAppointmentDurationMinutes = 30;
}
