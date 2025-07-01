package com.randevu.randevusistemibackend.controller;

import com.randevu.randevusistemibackend.dto.TokenResponse;
import com.randevu.randevusistemibackend.dto.RegisterRequest;
import com.randevu.randevusistemibackend.dto.ProviderRegisterRequest;
import com.randevu.randevusistemibackend.dto.TokenRequest;
import com.randevu.randevusistemibackend.dto.ApiErrorResponse;
import com.randevu.randevusistemibackend.dto.MessageResponse;
import com.randevu.randevusistemibackend.exception.BadRequestException;
import com.randevu.randevusistemibackend.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication API endpoints")
@Slf4j
public class AuthController {

    private final AuthService authService;
    
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Get OAuth2 token", description = "Standard OAuth2 token endpoint supporting password and refresh_token grant types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token generated successfully",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", 
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed", 
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/oauth/token")
    public ResponseEntity<TokenResponse> getToken(@Valid @RequestBody TokenRequest tokenRequest) {
        log.debug("Processing token request with grant type: {}", tokenRequest.getGrant_type());
        
        if (tokenRequest.getGrant_type() == null) {
            throw new BadRequestException("Grant type is required", "MISSING_GRANT_TYPE");
        }
        
        return ResponseEntity.ok(authService.getTokenResponse(tokenRequest));
    }
    
    // For compatibility with standard OAuth2 form submission
    @Operation(summary = "Get OAuth2 token (form submission)", description = "Standard OAuth2 token endpoint for form submissions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token generated successfully", 
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", 
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed", 
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping(value = "/oauth/token", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<TokenResponse> getTokenFromForm(
            @RequestParam("grant_type") String grantType,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "refresh_token", required = false) String refreshToken,
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "client_secret", required = false) String clientSecret,
            @RequestParam(value = "scope", required = false) String scope) {
        
        log.debug("Processing form-based token request with grant type: {}", grantType);
        
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setGrant_type(grantType);
        tokenRequest.setUsername(username);
        tokenRequest.setPassword(password);
        tokenRequest.setRefresh_token(refreshToken);
        tokenRequest.setClient_id(clientId);
        tokenRequest.setClient_secret(clientSecret);
        tokenRequest.setScope(scope);
        
        return ResponseEntity.ok(authService.getTokenResponse(tokenRequest));
    }

    @Operation(summary = "Register new user", description = "Register a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user registration data", 
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        log.debug("Processing user registration request for email: {}", registerRequest.getEmail());
        
        authService.registerUser(registerRequest);
        return ResponseEntity.ok(
                new MessageResponse(
                        "User registered successfully: " + registerRequest.getEmail()
                )
        );
    }

    @Operation(summary = "Register new provider", description = "Register a new service provider in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Provider registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid provider registration data", 
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/register/provider")
    public ResponseEntity<?> registerProvider(@Valid @RequestBody ProviderRegisterRequest registerRequest) {
        log.debug("Processing provider registration request for email: {}", registerRequest.getEmail());
        
        authService.registerProvider(registerRequest);
        return ResponseEntity.ok(
                new MessageResponse(
                        "Provider registered successfully: " + registerRequest.getEmail()
                )
        );
    }
}
