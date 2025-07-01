package com.randevu.randevusistemibackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for user authentication")
public class LoginRequest {
    @NotBlank
    @Schema(description = "Username for authentication", example = "user123")
    private String username;
    
    @NotBlank
    @Schema(description = "Password for authentication", example = "password123")
    private String password;
    
    @Schema(description = "OAuth2 grant type, defaults to 'password'", example = "password")
    private String grant_type = "password";
    
    @Schema(description = "Client ID for OAuth2 authentication", example = "appointment-client")
    private String client_id;
    
    @Schema(description = "Client secret for OAuth2 authentication")
    private String client_secret;
    
    @Schema(description = "Scope of access being requested", example = "read write")
    private String scope;
    
    @Schema(description = "Refresh token for refresh_token grant type")
    private String refresh_token;
}
