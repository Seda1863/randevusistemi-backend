package com.randevu.randevusistemibackend.service;

import com.randevu.randevusistemibackend.dto.ProviderRegisterRequest;
import com.randevu.randevusistemibackend.dto.TokenRequest;
import com.randevu.randevusistemibackend.dto.RegisterRequest;
import com.randevu.randevusistemibackend.dto.TokenResponse;
import com.randevu.randevusistemibackend.exception.BadRequestException;
import com.randevu.randevusistemibackend.exception.UnauthorizedException;
import com.randevu.randevusistemibackend.model.Address;
import com.randevu.randevusistemibackend.model.Provider;
import com.randevu.randevusistemibackend.model.Role;
import com.randevu.randevusistemibackend.model.User;
import com.randevu.randevusistemibackend.repository.UserRepository;
import com.randevu.randevusistemibackend.util.JwtProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtTokenProvider;
    
    // Default token expiration time in seconds (24 hours)
    private static final Integer DEFAULT_TOKEN_EXPIRATION = 86400;
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        log.debug("Registering new user with username: {}", registerRequest.getUsername());
        
        // Validate username
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username is already taken!", "USERNAME_TAKEN");
        }

        // Validate email format
        if (!isValidEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Invalid email format: " + registerRequest.getEmail(), "INVALID_EMAIL_FORMAT");
        }

        // Validate email uniqueness
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already in use!", "EMAIL_TAKEN");
        }

        // Create user entity
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setPhone(registerRequest.getPhone());
        
        // Default role is USER, can be changed in the registration request if needed
        user.setRoles(Collections.singleton(Role.ROLE_USER));

        User savedUser = userRepository.save(user);
        log.info("Successfully registered user with ID: {}", savedUser.getId());
        return savedUser;
    }
    
    @Transactional
    public Provider registerProvider(ProviderRegisterRequest registerRequest) {
        log.debug("Registering new provider with username: {}", registerRequest.getUsername());
        
        // Validate username
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username is already taken!", "USERNAME_TAKEN");
        }

        // Validate email format
        if (!isValidEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Invalid email format: " + registerRequest.getEmail(), "INVALID_EMAIL_FORMAT");
        }

        // Validate email uniqueness
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already in use!", "EMAIL_TAKEN");
        }
        
        // Create address entity if address details are provided
        Address address = null;
        if (registerRequest.getStreetAddress() != null && registerRequest.getCity() != null && 
            registerRequest.getState() != null && registerRequest.getPostalCode() != null) {
            
            address = new Address();
            address.setStreetAddress(registerRequest.getStreetAddress());
            address.setCity(registerRequest.getCity());
            address.setState(registerRequest.getState());
            address.setPostalCode(registerRequest.getPostalCode());
            address.setCountry("Turkey"); // Default country
        }

        // Create provider entity
        Provider provider = new Provider();
        provider.setUsername(registerRequest.getUsername());
        provider.setEmail(registerRequest.getEmail());
        provider.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        provider.setFullName(registerRequest.getFullName());
        provider.setPhone(registerRequest.getPhone());
        
        // Add provider-specific properties
        provider.setBusinessName(registerRequest.getBusinessName());
        provider.setDescription(registerRequest.getDescription());
        provider.setAddress(address);
        
        if (registerRequest.getAverageAppointmentDurationMinutes() != null) {
            provider.setAverageAppointmentDurationMinutes(registerRequest.getAverageAppointmentDurationMinutes());
        }
        
        // Set provider roles
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);      // Provider is also a user
        roles.add(Role.ROLE_PROVIDER);  // Provider-specific role
        provider.setRoles(roles);

        Provider savedProvider = (Provider) userRepository.save(provider);
        log.info("Successfully registered provider with ID: {}", savedProvider.getId());
        return savedProvider;
    }

    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public TokenResponse getTokenResponse(TokenRequest tokenRequest) {
        log.debug("Processing token request with grant type: {}", tokenRequest.getGrant_type());
        
        // Handle different OAuth2 grant types
        switch (tokenRequest.getGrant_type().toLowerCase()) {
            case "password":
                return handlePasswordGrant(tokenRequest);
            case "refresh_token":
                return handleRefreshTokenGrant(tokenRequest);
            default:
                throw new BadRequestException(
                    "Unsupported grant type: " + tokenRequest.getGrant_type(), 
                    "INVALID_GRANT_TYPE");
        }
    }
    
    private TokenResponse handlePasswordGrant(TokenRequest tokenRequest) {
        if (tokenRequest.getUsername() == null || tokenRequest.getPassword() == null) {
            throw new BadRequestException(
                "Username and password required for password grant type", 
                "MISSING_CREDENTIALS");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        tokenRequest.getUsername(), 
                        tokenRequest.getPassword()
                    )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
            
            log.debug("Successfully generated tokens for user: {}", tokenRequest.getUsername());
            
            return new TokenResponse(
                    accessToken,
                    DEFAULT_TOKEN_EXPIRATION,
                    refreshToken,
                    tokenRequest.getScope() != null ? tokenRequest.getScope() : "read write"
            );
        } catch (BadCredentialsException e) {
            log.warn("Failed authentication attempt for user: {}", tokenRequest.getUsername());
            throw new UnauthorizedException("Invalid username or password", "INVALID_CREDENTIALS");
        }
    }
    
    private TokenResponse handleRefreshTokenGrant(TokenRequest tokenRequest) {
        if (tokenRequest.getRefresh_token() == null) {
            throw new BadRequestException(
                "Refresh token is required for refresh_token grant type", 
                "MISSING_REFRESH_TOKEN");
        }
        
        try {
            // Validate and process refresh token
            Authentication authentication = jwtTokenProvider.validateRefreshToken(tokenRequest.getRefresh_token());
            if (authentication == null) {
                throw new UnauthorizedException("Invalid or expired refresh token", "INVALID_REFRESH_TOKEN");
            }
            
            String accessToken = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
            
            log.debug("Successfully refreshed token");
            
            return new TokenResponse(
                    accessToken,
                    DEFAULT_TOKEN_EXPIRATION,
                    refreshToken,
                    tokenRequest.getScope() != null ? tokenRequest.getScope() : "read write"
            );
        } catch (Exception e) {
            log.warn("Failed to refresh token: {}", e.getMessage());
            throw new UnauthorizedException("Invalid or expired refresh token", "INVALID_REFRESH_TOKEN");
        }
    }
}
