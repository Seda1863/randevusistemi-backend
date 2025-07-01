package com.randevu.randevusistemibackend.controller;

import com.randevu.randevusistemibackend.dto.AppointmentRequest;
import com.randevu.randevusistemibackend.dto.AppointmentResponse;
import com.randevu.randevusistemibackend.dto.ApiErrorResponse;
import com.randevu.randevusistemibackend.exception.BadRequestException;
import com.randevu.randevusistemibackend.model.Provider;
import com.randevu.randevusistemibackend.model.Role;
import com.randevu.randevusistemibackend.model.User;
import com.randevu.randevusistemibackend.repository.UserRepository;
import com.randevu.randevusistemibackend.service.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointments", description = "Appointment management endpoints")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;
    
    @Operation(summary = "Create a new appointment", description = "Schedule a new appointment with a provider")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointment created successfully",
                content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Not a user account or attempting to book with self",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Provider not found",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request, Principal principal) {
        
        User currentUser = getUserFromPrincipal(principal);
        
        // Ensure the user is not trying to book an appointment with themselves if they are also a provider
        if (currentUser.getId().equals(request.getProviderId())) {
            throw new BadRequestException("You cannot book an appointment with yourself", 
                                        "SELF_APPOINTMENT_NOT_ALLOWED");
        }
        
        AppointmentResponse response = appointmentService.createAppointment(request, currentUser);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get user appointments", description = "Get all appointments for the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppointmentResponse.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AppointmentResponse>> getUserAppointments(Principal principal) {
        User currentUser = getUserFromPrincipal(principal);
        List<AppointmentResponse> appointments = appointmentService.getUserAppointments(currentUser);
        
        return ResponseEntity.ok(appointments);
    }
    
    @Operation(summary = "Get provider appointments", description = "Get all appointments for the current provider")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppointmentResponse.class)))),
        @ApiResponse(responseCode = "403", description = "Not a provider account",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/provider")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<List<AppointmentResponse>> getProviderAppointments(Principal principal) {
        Provider provider = getProviderFromPrincipal(principal);
        List<AppointmentResponse> appointments = appointmentService.getProviderAppointments(provider);
        
        return ResponseEntity.ok(appointments);
    }
    
    @Operation(summary = "Get provider appointments by date range", 
              description = "Get provider appointments within a specific date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppointmentResponse.class)))),
        @ApiResponse(responseCode = "403", description = "Not a provider account",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/provider/range")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<List<AppointmentResponse>> getProviderAppointmentsByDateRange(
            Principal principal,
            @Parameter(description = "Start date (yyyy-MM-dd)", example = "2025-04-10")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", example = "2025-04-17")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Provider provider = getProviderFromPrincipal(principal);
        List<AppointmentResponse> appointments = 
            appointmentService.getProviderAppointmentsByDateRange(provider, startDate, endDate);
        
        return ResponseEntity.ok(appointments);
    }
    
    @Operation(summary = "Cancel an appointment", description = "Cancel an existing appointment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointment cancelled successfully",
                content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or permission denied",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Appointment not found",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @PathVariable("id") Long appointmentId, Principal principal) {
        
        User currentUser = getUserFromPrincipal(principal);
        AppointmentResponse response = appointmentService.cancelAppointment(appointmentId, currentUser);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Confirm an appointment", description = "Confirm a pending appointment (provider only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointment confirmed successfully",
                content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or permission denied",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Not a provider account",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Appointment not found",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<AppointmentResponse> confirmAppointment(
            @PathVariable("id") Long appointmentId, Principal principal) {
        
        Provider provider = getProviderFromPrincipal(principal);
        AppointmentResponse response = appointmentService.confirmAppointment(appointmentId, provider);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get the current authenticated user from the security principal
     */
    private User getUserFromPrincipal(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new BadRequestException("User not found", "USER_NOT_FOUND"));
    }
    
    /**
     * Get the current provider from the security principal
     */
    private Provider getProviderFromPrincipal(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .filter(user -> user instanceof Provider)
                .map(user -> (Provider) user)
                .orElseThrow(() -> new BadRequestException("Only provider accounts can access this endpoint", 
                                                         "NOT_A_PROVIDER"));
    }
}