package com.randevu.randevusistemibackend.service;

import com.randevu.randevusistemibackend.dto.AppointmentRequest;
import com.randevu.randevusistemibackend.dto.AppointmentResponse;
import com.randevu.randevusistemibackend.exception.BadRequestException;
import com.randevu.randevusistemibackend.exception.ResourceNotFoundException;
import com.randevu.randevusistemibackend.model.Appointment;
import com.randevu.randevusistemibackend.model.Provider;
import com.randevu.randevusistemibackend.model.User;
import com.randevu.randevusistemibackend.repository.AppointmentRepository;
import com.randevu.randevusistemibackend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    /**
     * Create a new appointment based on the requested information
     */
    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request, User currentUser) {
        log.debug("Creating appointment for user {} with provider {}", 
                 currentUser.getUsername(), request.getProviderId());
        
        // Find the provider
        Provider provider = userRepository.findById(request.getProviderId())
                .filter(user -> user instanceof Provider)
                .map(user -> (Provider) user)
                .orElseThrow(() -> new ResourceNotFoundException("Provider", "id", request.getProviderId()));
        
        // Validate that the provider is available
        if (!provider.isAvailable()) {
            throw new BadRequestException("This provider is not currently accepting appointments", 
                                        "PROVIDER_UNAVAILABLE");
        }
        
        // Determine appointment duration (use provided duration or provider's recommended duration)
        Integer durationMinutes = request.getDurationMinutes();
        if (durationMinutes == null || durationMinutes <= 0) {
            durationMinutes = provider.getAverageAppointmentDurationMinutes();
            if (durationMinutes == null || durationMinutes <= 0) {
                durationMinutes = 30; // Default fallback if no duration is specified anywhere
            }
        }
        
        // Calculate end time based on start time and duration
        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);
        
        // Check for overlapping appointments
        if (appointmentRepository.hasOverlappingAppointments(provider, startTime, endTime)) {
            throw new BadRequestException("The requested time overlaps with an existing appointment", 
                                        "APPOINTMENT_OVERLAP");
        }
        
        // Create and save the appointment
        Appointment appointment = new Appointment();
        appointment.setUser(currentUser);
        appointment.setProvider(provider);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setServiceName(request.getServiceName());
        appointment.setNotes(request.getNotes());
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Created appointment with ID {} for user {}", savedAppointment.getId(), currentUser.getUsername());

        // Send email notification to the provider
        emailService.sendNewAppointmentNotificationToProvider(savedAppointment);
        // Send email notification to the user
        emailService.sendAppointmentConfirmationToUser(savedAppointment);

        return convertToResponse(savedAppointment);
    }
    
    /**
     * Cancel an appointment
     */
    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId, User currentUser) {
        log.debug("Cancelling appointment {} for user {}", appointmentId, currentUser.getUsername());
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));
        
        // Verify the user owns this appointment or is the provider
        if (!appointment.getUser().getId().equals(currentUser.getId()) && 
            !(currentUser instanceof Provider && appointment.getProvider().getId().equals(currentUser.getId()))) {
            throw new BadRequestException("You do not have permission to cancel this appointment", 
                                        "PERMISSION_DENIED");
        }
        
        // Cannot cancel already completed appointments
        if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a completed appointment", 
                                        "APPOINTMENT_COMPLETED");
        }
        
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        
        log.info("Cancelled appointment with ID {}", appointmentId);
        return convertToResponse(updatedAppointment);
    }
    
    /**
     * Confirm an appointment (provider only)
     */
    @Transactional
    public AppointmentResponse confirmAppointment(Long appointmentId, Provider provider) {
        log.debug("Confirming appointment {} by provider {}", appointmentId, provider.getUsername());
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));
        
        // Verify the provider owns this appointment
        if (!appointment.getProvider().getId().equals(provider.getId())) {
            throw new BadRequestException("You do not have permission to confirm this appointment", 
                                        "PERMISSION_DENIED");
        }
        
        // Cannot confirm cancelled appointments
        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            throw new BadRequestException("Cannot confirm a cancelled appointment", 
                                        "APPOINTMENT_CANCELLED");
        }
        
        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        
        // Send email confirmation to the user with ICS calendar attachment
        emailService.sendAppointmentConfirmedToUser(updatedAppointment);
        
        log.info("Confirmed appointment with ID {}", appointmentId);
        return convertToResponse(updatedAppointment);
    }
    
    /**
     * Get appointments for the current user
     */
    public List<AppointmentResponse> getUserAppointments(User user) {
        log.debug("Getting appointments for user {}", user.getUsername());
        
        List<Appointment> appointments = appointmentRepository.findByUserOrderByStartTimeDesc(user);
        return appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get appointments for a provider
     */
    public List<AppointmentResponse> getProviderAppointments(Provider provider) {
        log.debug("Getting appointments for provider {}", provider.getUsername());
        
        List<Appointment> appointments = appointmentRepository.findByProviderOrderByStartTimeDesc(provider);
        return appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get provider appointments for a specific date range
     */
    public List<AppointmentResponse> getProviderAppointmentsByDateRange(
            Provider provider, LocalDate startDate, LocalDate endDate) {
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        List<Appointment> appointments = appointmentRepository.findByProviderAndStartTimeBetween(
                provider, startDateTime, endDateTime);
                
        return appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Appointment entity to AppointmentResponse DTO
     */
    private AppointmentResponse convertToResponse(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        
        response.setId(appointment.getId());
        
        // Set user info
        User user = appointment.getUser();
        AppointmentResponse.UserInfoDTO userInfo = new AppointmentResponse.UserInfoDTO(
                user.getId(), 
                user.getUsername(), 
                user.getFullName(), 
                user.getEmail(), 
                user.getPhone());
        response.setUser(userInfo);
        
        // Set provider info
        Provider provider = appointment.getProvider();
        AppointmentResponse.ProviderInfoDTO providerInfo = new AppointmentResponse.ProviderInfoDTO(
                provider.getId(), 
                provider.getUsername(), 
                provider.getFullName(),
                provider.getBusinessName(),
                provider.getDescription(),
                provider.getEmail(), 
                provider.getPhone());
        response.setProvider(providerInfo);
        
        // Set other appointment details
        response.setStartTime(appointment.getStartTime());
        response.setEndTime(appointment.getEndTime());
        response.setDurationMinutes(appointment.getDurationMinutes());
        response.setServiceName(appointment.getServiceName());
        response.setNotes(appointment.getNotes());
        response.setStatus(appointment.getStatus());
        response.setCreatedAt(appointment.getCreatedAt());
        response.setUpdatedAt(appointment.getUpdatedAt());
        
        return response;
    }
}