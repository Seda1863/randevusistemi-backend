package com.randevu.randevusistemibackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provider entity that extends the User class.
 * Contains additional properties specific to service providers.
 */
@Entity
@DiscriminatorValue("provider")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Provider extends User {
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "business_name")
    private String businessName;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "provider_services", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "service_name")
    private Set<String> services = new HashSet<>();
    
    @Column(name = "average_appointment_duration_minutes")
    private Integer averageAppointmentDurationMinutes = 30;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;
    
    @ElementCollection
    @CollectionTable(name = "provider_business_hours", 
                    joinColumns = @JoinColumn(name = "provider_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "day_of_week")
    private Map<DayOfWeek, BusinessHours> businessHours = new HashMap<>();
    
    @Column(name = "is_available")
    private boolean isAvailable = true;
    
    /**
     * Add a service to this provider's offered services
     */
    public void addService(String service) {
        this.services.add(service);
    }
    
    /**
     * Remove a service from this provider's offered services
     */
    public void removeService(String service) {
        this.services.remove(service);
    }
    
    /**
     * Set business hours for a specific day of week
     */
    public void setHoursForDay(DayOfWeek day, LocalTime startTime, LocalTime endTime, boolean isClosed) {
        BusinessHours hours = new BusinessHours();
        hours.setStartTime(startTime);
        hours.setEndTime(endTime);
        hours.setClosed(isClosed);
        this.businessHours.put(day, hours);
    }
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessHours {
        private LocalTime startTime;
        private LocalTime endTime;
        private boolean isClosed;
    }
}