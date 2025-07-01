package com.randevu.randevusistemibackend.controller;

import com.randevu.randevusistemibackend.dto.ApiErrorResponse;
import com.randevu.randevusistemibackend.dto.UserUpdateRequest;
import com.randevu.randevusistemibackend.exception.ResourceNotFoundException;
import com.randevu.randevusistemibackend.model.User;
import com.randevu.randevusistemibackend.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "User management endpoints")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;

    @Operation(summary = "Get current user profile", description = "Retrieves the profile of the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Principal principal) {
        log.debug("Fetching user profile for username: {}", principal.getName());
        
        User user = userRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", principal.getName()));
        
        // TODO: Create DTOs to control what data is exposed
        return ResponseEntity.ok(user);
    }
    
    @Operation(summary = "Update user profile", description = "Updates the profile of the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid update data",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(
            @Valid @RequestBody UserUpdateRequest updateRequest, 
            Principal principal) {
        log.debug("Updating profile for user: {}", principal.getName());
        
        User user = userRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", principal.getName()));
        
        // Update user fields
        if (updateRequest.getFullName() != null) {
            user.setFullName(updateRequest.getFullName());
        }
        if (updateRequest.getPhone() != null) {
            user.setPhone(updateRequest.getPhone());
        }
        
        // Save the updated user
        User updatedUser = userRepository.save(user);
        log.info("User profile updated successfully for ID: {}", updatedUser.getId());
        
        return ResponseEntity.ok(updatedUser);
    }
}