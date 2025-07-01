package com.randevu.randevusistemibackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating user profile information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for updating user information")
public class UserUpdateRequest {
    
    @Schema(description = "Full name of the user", example = "John Doe")
    private String fullName;
    
    @Schema(description = "Phone number of the user", example = "+90 555 123 4567")
    private String phone;
}