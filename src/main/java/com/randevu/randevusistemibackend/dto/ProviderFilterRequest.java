package com.randevu.randevusistemibackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for filtering providers based on various criteria
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Criteria for filtering providers")
public class ProviderFilterRequest {
    
    @Schema(description = "Filter by service offered by provider", example = "Dental Cleaning")
    private String service;
    
    @Schema(description = "Filter by city", example = "Istanbul")
    private String city;
    
    @Schema(description = "Filter by business name (partial match)", example = "Dental")
    private String businessName;
    
    @Schema(description = "Filter by provider name (partial match)", example = "Smith")
    private String providerName;
    
    @Schema(description = "Only include providers that are currently available", example = "true")
    private Boolean available;
    
    @Schema(description = "Maximum page size", example = "20", defaultValue = "10")
    private Integer pageSize = 10;
    
    @Schema(description = "Page number (0-based)", example = "0", defaultValue = "0")
    private Integer pageNumber = 0;
}