package com.randevu.randevusistemibackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for successful authentication")
public class TokenResponse {
    
    @Schema(description = "OAuth2 access token", example = "eyJhbGciOiJSUzI1...")
    private String access_token;
    
    @Schema(description = "OAuth2 token type", example = "Bearer")
    private String token_type = "Bearer";
    
    @Schema(description = "Access token expiration time in seconds", example = "3600")
    private Integer expires_in;
    
    @Schema(description = "Refresh token for obtaining a new access token", example = "eyJhbGciOiJIUzI1...")
    private String refresh_token;
    
    @Schema(description = "Granted scopes for this token", example = "read write")
    private String scope;
    
    // Constructor that accepts just a token (for backward compatibility)
    public TokenResponse(String token) {
        this.access_token = token;
        this.token_type = "Bearer";
        this.expires_in = 86400; // 24 hours in seconds (default)
    }
    
    // Full OAuth2 response constructor
    public TokenResponse(String accessToken, Integer expiresIn, String refreshToken, String scope) {
        this.access_token = accessToken;
        this.token_type = "Bearer";
        this.expires_in = expiresIn;
        this.refresh_token = refreshToken;
        this.scope = scope;
    }
}
