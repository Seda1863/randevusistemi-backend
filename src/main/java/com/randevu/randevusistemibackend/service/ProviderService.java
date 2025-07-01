package com.randevu.randevusistemibackend.service;

import com.randevu.randevusistemibackend.dto.ProviderDTO;
import com.randevu.randevusistemibackend.dto.ProviderFilterRequest;
import com.randevu.randevusistemibackend.model.Provider;
import com.randevu.randevusistemibackend.repository.ProviderRepository;
import com.randevu.randevusistemibackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * Service class for managing provider-related operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderService {

    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;

    /**
     * Search for providers based on filter criteria using database queries
     * 
     * @param filter The filter criteria for searching providers
     * @return A paginated list of providers matching the criteria
     */
    public Page<ProviderDTO> findProvidersByFilter(ProviderFilterRequest filter) {
        Specification<Provider> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getBusinessName() != null && !filter.getBusinessName().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("businessName"), "%" + filter.getBusinessName() + "%"));
            }

            if (filter.getAvailable() != null && filter.getAvailable()) {
                predicates.add(criteriaBuilder.equal(root.get("available"), true));
            }

            if (filter.getService() != null) {
                predicates.add(criteriaBuilder.isMember(filter.getService(), root.get("services")));
            }

            if (filter.getCity() != null && !filter.getCity().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.join("address").get("city"), filter.getCity()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Provider> providerPage = providerRepository.findAll(spec, PageRequest.of(filter.getPageNumber(), filter.getPageSize()));
        return providerPage.map(ProviderDTO::fromEntity);
    }
}
