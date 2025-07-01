package com.randevu.randevusistemibackend.repository;

import com.randevu.randevusistemibackend.model.Appointment;
import com.randevu.randevusistemibackend.model.Provider;
import com.randevu.randevusistemibackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findByUser(User user);
    
    List<Appointment> findByProvider(Provider provider);
    
    List<Appointment> findByUserOrderByStartTimeDesc(User user);
    
    List<Appointment> findByProviderOrderByStartTimeDesc(Provider provider);
    
    List<Appointment> findByProviderAndStartTimeBetween(Provider provider, LocalDateTime start, LocalDateTime end);
    
    List<Appointment> findByUserAndStartTimeBetween(User user, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT a FROM Appointment a WHERE a.provider = :provider AND " +
           "((a.startTime <= :endTime AND a.endTime >= :startTime) OR " +
           "(a.startTime >= :startTime AND a.startTime < :endTime)) AND " +
           "a.status NOT IN ('CANCELLED')")
    List<Appointment> findOverlappingAppointments(@Param("provider") Provider provider, 
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a WHERE " +
           "a.provider = :provider AND " +
           "((a.startTime <= :endTime AND a.endTime > :startTime) OR " +
           "(a.startTime >= :startTime AND a.startTime < :endTime)) AND " +
           "a.status NOT IN ('CANCELLED')")
    boolean hasOverlappingAppointments(@Param("provider") Provider provider, 
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);
}