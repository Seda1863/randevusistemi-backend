package com.randevu.randevusistemibackend.controller;

import com.randevu.randevusistemibackend.dto.ApiErrorResponse;
import com.randevu.randevusistemibackend.dto.MessageResponse;
import com.randevu.randevusistemibackend.dto.ProviderDTO;
import com.randevu.randevusistemibackend.dto.ProviderFilterRequest;
import com.randevu.randevusistemibackend.dto.ProviderUpdateRequest;
import com.randevu.randevusistemibackend.model.Address;
import com.randevu.randevusistemibackend.model.Provider;
import com.randevu.randevusistemibackend.repository.UserRepository;
import com.randevu.randevusistemibackend.exception.ResourceNotFoundException;
import com.randevu.randevusistemibackend.exception.ForbiddenException;
import com.randevu.randevusistemibackend.service.ProviderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/provider")
@Tag(name = "Provider", description = "Provider management endpoints")
@RequiredArgsConstructor
@Slf4j
public class ProviderController {

    private final UserRepository userRepository;
    private final ProviderService providerService;

    @Operation(summary = "Search providers with filters", description = "Search for providers using various filter criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    @PostMapping("/search")
    public ResponseEntity<Page<ProviderDTO>> searchProviders(
            @Valid ProviderFilterRequest filter) {
        log.debug("Searching providers with filter: {}", filter);
        
        Page<ProviderDTO> providers = providerService.findProvidersByFilter(filter);
        return ResponseEntity.ok(providers);
    }

    @Operation(summary = "Get current provider profile", description = "Retrieves the profile of the currently authenticated provider")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Provider profile retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Not a provider account",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/profile")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<?> getProviderProfile(Principal principal) {
        log.debug("Fetching provider profile for username: {}", principal.getName());
        
        // Find the provider by username
        Provider provider = userRepository.findByUsername(principal.getName())
            .filter(user -> user instanceof Provider)
            .map(user -> (Provider) user)
            .orElseThrow(() -> new ResourceNotFoundException("Provider", "username", principal.getName()));
        
        // TODO: Create DTOs to control what data is exposed
        return ResponseEntity.ok(provider);
    }
    
    @Operation(summary = "Get provider by ID", description = "Retrieves a provider profile by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Provider profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Provider not found",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getProviderById(@PathVariable Long id) {
        log.debug("Fetching provider with ID: {}", id);
        
        Provider provider = userRepository.findById(id)
            .filter(user -> user instanceof Provider)
            .map(user -> (Provider) user)
            .orElseThrow(() -> new ResourceNotFoundException("Provider", "id", id));
        
        // TODO: Create DTOs to control what data is exposed
        return ResponseEntity.ok(provider);
    }
    
    @Operation(summary = "Update provider profile", description = "Updates the profile of the currently authenticated provider")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Provider profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid update data",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Not a provider account",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/profile")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<?> updateProviderProfile(
            @Valid @RequestBody ProviderUpdateRequest updateRequest, 
            Principal principal) {
        log.debug("Updating profile for provider: {}", principal.getName());
        
        // Find the provider by username
        Provider provider = userRepository.findByUsername(principal.getName())
            .filter(user -> user instanceof Provider)
            .map(user -> (Provider) user)
            .orElseThrow(() -> new ResourceNotFoundException("Provider", "username", principal.getName()));
        
        // Update provider fields
        if (updateRequest.getFullName() != null) {
            provider.setFullName(updateRequest.getFullName());
        }
        if (updateRequest.getPhone() != null) {
            provider.setPhone(updateRequest.getPhone());
        }
        if (updateRequest.getBusinessName() != null) {
            provider.setBusinessName(updateRequest.getBusinessName());
        }
        if (updateRequest.getDescription() != null) {
            provider.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getAverageAppointmentDurationMinutes() != null) {
            provider.setAverageAppointmentDurationMinutes(updateRequest.getAverageAppointmentDurationMinutes());
        }
        
        // Update address if any address fields are provided
        if (updateRequest.getStreetAddress() != null || 
            updateRequest.getCity() != null || 
            updateRequest.getState() != null || 
            updateRequest.getPostalCode() != null) {
            
            Address address = provider.getAddress();
            if (address == null) {
                address = new Address();
            }
            
            if (updateRequest.getStreetAddress() != null) {
                address.setStreetAddress(updateRequest.getStreetAddress());
            }
            if (updateRequest.getCity() != null) {
                address.setCity(updateRequest.getCity());
            }
            if (updateRequest.getState() != null) {
                address.setState(updateRequest.getState());
            }
            if (updateRequest.getPostalCode() != null) {
                address.setPostalCode(updateRequest.getPostalCode());
            }
            
            provider.setAddress(address);
        }
        
        // Save the updated provider
        Provider updatedProvider = (Provider) userRepository.save(provider);
        log.info("Provider profile updated successfully for ID: {}", updatedProvider.getId());
        
        return ResponseEntity.ok(updatedProvider);
    }
    
    @Operation(summary = "Set provider availability", description = "Updates the availability status of the provider")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Availability updated successfully"),
        @ApiResponse(responseCode = "403", description = "Not a provider account",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/availability")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<?> setAvailability(@RequestParam boolean available, Authentication authentication) {
        log.debug("Setting provider availability to {} for username: {}", available, authentication.getName());
        
        Provider provider = userRepository.findByUsername(authentication.getName())
            .filter(user -> user instanceof Provider)
            .map(user -> (Provider) user)
            .orElseThrow(() -> new ForbiddenException("Only provider accounts can access this endpoint"));
        
        provider.setAvailable(available);
        userRepository.save(provider);
        
        String message = available 
            ? "Provider is now accepting appointments" 
            : "Provider is now not accepting appointments";
            
        return ResponseEntity.ok(new MessageResponse(message));
    }
    
    @Operation(summary = "Add a service", description = "Add a new service to the provider's offered services")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service added successfully"),
        @ApiResponse(responseCode = "403", description = "Not a provider account",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/services")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<?> addService(@RequestParam String serviceName, Authentication authentication) {
        log.debug("Adding service '{}' for provider: {}", serviceName, authentication.getName());
        
        Provider provider = userRepository.findByUsername(authentication.getName())
            .filter(user -> user instanceof Provider)
            .map(user -> (Provider) user)
            .orElseThrow(() -> new ForbiddenException("Only provider accounts can access this endpoint"));
        
        provider.addService(serviceName);
        userRepository.save(provider);
        
        return ResponseEntity.ok(new MessageResponse("Service added successfully: " + serviceName));
    }
    
    @Operation(summary = "Remove a service", description = "Remove a service from the provider's offered services")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service removed successfully"),
        @ApiResponse(responseCode = "403", description = "Not a provider account",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/services")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<?> removeService(@RequestParam String serviceName, Authentication authentication) {
        log.debug("Removing service '{}' for provider: {}", serviceName, authentication.getName());
        
        Provider provider = userRepository.findByUsername(authentication.getName())
            .filter(user -> user instanceof Provider)
            .map(user -> (Provider) user)
            .orElseThrow(() -> new ForbiddenException("Only provider accounts can access this endpoint"));
        
        provider.removeService(serviceName);
        userRepository.save(provider);
        
        return ResponseEntity.ok(new MessageResponse("Service removed successfully: " + serviceName));
    }
    
    // Additional endpoints for managing provider-specific functionality can be added here
}