package com.randevu.randevusistemibackend.config;

import com.randevu.randevusistemibackend.model.Address;
import com.randevu.randevusistemibackend.model.Provider;
import com.randevu.randevusistemibackend.model.Role;
import com.randevu.randevusistemibackend.model.User;
import com.randevu.randevusistemibackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

/**
 * Database initializer to create demo data on application startup.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Initialize database with demo data when the application starts.
     * Only active in development or test profiles, not in production.
     */
    @Bean
    @Profile({"dev", "test", "default"})  // Do not run in production
    @Transactional
    public CommandLineRunner initDatabase() {
        return args -> {
            log.info("Initializing database with demo data...");

            // Skip if data already exists
            if (userRepository.count() > 0) {
                log.info("Database already contains users. Skipping initialization.");
                return;
            }

            // Create a regular user
            User demoUser = new User();
            demoUser.setUsername("yigituser");
            demoUser.setPassword(passwordEncoder.encode("yigit123"));
            demoUser.setEmail("agaogluyigit0@gmail.com");
            demoUser.setFullName("Yiğit Ağaoglu");
            demoUser.setPhone("+90 555 123 4567");

            Set<Role> userRoles = new HashSet<>();
            userRoles.add(Role.ROLE_USER);
            demoUser.setRoles(userRoles);

            User savedUser = userRepository.save(demoUser);
            log.info("Created demo user: {}", savedUser.getUsername());

            User demoUser1 = new User();
            demoUser1.setUsername("berkayaydemir");
            demoUser1.setPassword(passwordEncoder.encode("berkay123"));
            demoUser1.setEmail("berkayaydmr@hotmail.com");
            demoUser1.setFullName("Berkay Aydemir");
            demoUser1.setPhone("+90 538 229 7801");

            Set<Role> userRoles1 = new HashSet<>();
            userRoles1.add(Role.ROLE_USER);
            demoUser1.setRoles(userRoles1);

            User savedUser1 = userRepository.save(demoUser1);
            log.info("Created demo user: {}", savedUser1.getUsername());

            // Create a provider
            Provider demoProvider = new Provider();
            demoProvider.setUsername("sedadoktor");
            demoProvider.setPassword(passwordEncoder.encode("seda123"));
            demoProvider.setEmail("seda.sarmasik@darussafaka.net");
            demoProvider.setFullName("Dr. Demo Provider");
            demoProvider.setPhone("+90 555 987 6543");
            demoProvider.setBusinessName("Demo Clinic");
            demoProvider.setDescription("This is a demo healthcare provider for testing purposes.");
            demoProvider.setAverageAppointmentDurationMinutes(30);
            demoProvider.setAvailable(true);

            // Add services
            demoProvider.addService("General Consultation");
            demoProvider.addService("Follow-up Visit");
            demoProvider.addService("Routine Check-up");

            // Create an address
            Address address = new Address();
            address.setStreetAddress("123 Demo Street");
            address.setCity("Istanbul");
            address.setState("Marmara");
            address.setPostalCode("34000");
            address.setCountry("Turkey");

            demoProvider.setAddress(address);

            // Set provider roles
            Set<Role> providerRoles = new HashSet<>();
            providerRoles.add(Role.ROLE_PROVIDER);
            demoProvider.setRoles(providerRoles);

            Provider savedProvider = (Provider) userRepository.save(demoProvider);
            log.info("Created demo provider: {}", savedProvider.getUsername());

            log.info("Database initialization completed successfully.");
        };
    }
}