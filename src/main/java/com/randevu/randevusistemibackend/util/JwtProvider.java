package com.randevu.randevusistemibackend.util;

import com.randevu.randevusistemibackend.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.expiration}")
    private long expirationTime;
    
    @Value("${jwt.issuer:RandevuSistemiAPI}")
    private String issuer;
    
    private Key key;
    
    // Initialize key after properties are set
    private Key getSigningKey() {
        if (key == null) {
            key = Keys.hmacShaKeyFor(secretKey.getBytes());
        }
        return key;
    }

    /**
     * Generate a token from Authentication object (used by Spring Security)
     */
    public String generateToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);
        
        // Extract roles from authorities
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        
        // Determine UserType from roles
        UserType userType = getUserTypeFromRoles(roles);
        
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("roles", roles)
                .claim("userType", userType.name())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Generate a token for refresh token operations
     */
    public String generateRefreshToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (expirationTime * 2)); // Longer expiry for refresh tokens
        
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Legacy method to generate token with userId and userType
     */
    public String generateToken(Long userId, String username, UserType userType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("userType", userType.name())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Map roles string to UserType enum
     */
    private UserType getUserTypeFromRoles(String roles) {
        if (roles.contains(Role.ROLE_ADMIN.name())) {
            return UserType.ADMIN;
        } else if (roles.contains(Role.ROLE_PROVIDER.name())) {
            return UserType.PROVIDER;
        } else {
            return UserType.USER;
        }
    }
    
    /**
     * Extract username from JWT token
     */
    public String getUsernameFromToken(String token) {
        return getUsernameFromJWT(token);
    }
    
    /**
     * Extract UserType from JWT token
     */
    public UserType getUserTypeFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
                
        return UserType.valueOf(claims.get("userType", String.class));
    }

    /**
     * Extract username from JWT token (alternative method)
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }
    
    /**
     * Validate a refresh token and return the authentication
     */
    public Authentication validateRefreshToken(String refreshToken) {
        // Implement token validation and return Authentication
        // For now, returning null as placeholder
        return null; // This should be implemented based on your authentication system
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(getSigningKey()).build().parseSignedClaims(authToken);
            return true;
        } catch (Exception ex) {
            // Log exception details
            return false;
        }
    }
}