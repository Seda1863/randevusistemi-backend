package com.randevu.randevusistemibackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "OAuth2 token request")
public class TokenRequest {

    @NotBlank
    @Schema(description = "OAuth2 grant type", example = "password", required = true)
    private String grant_type;
    
    @Schema(description = "Username for password grant type", example = "user123")
    private String username;
    
    @Schema(description = "Password for password grant type", example = "password123")
    private String password;
    
    @Schema(description = "Refresh token for refresh_token grant type", example = "eyJhbGciOiJIUzI1...")
    private String refresh_token;
    
    @Schema(description = "Client ID for authentication", example = "appointment-client")
    private String client_id;
    
    @Schema(description = "Client secret for authentication")
    private String client_secret;
    
    @Schema(description = "Scope of access being requested", example = "read write")
    private String scope;
}
