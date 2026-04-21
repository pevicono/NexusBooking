package com.example.nexusbooking.service;

import com.example.nexusbooking.dto.FacilityRequest;
import com.example.nexusbooking.model.Facility;
import com.example.nexusbooking.repository.FacilityRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public FacilityService(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    public List<Facility> findAll() {
        return facilityRepository.findAll();
    }

    public Facility findById(Long id) {
        return facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));
    }

    public Facility create(FacilityRequest request) {
        Facility facility = new Facility();
        applyRequest(facility, request);
        return facilityRepository.save(facility);
    }

    public Facility update(Long id, FacilityRequest request) {
        Facility facility = findById(id);
        applyRequest(facility, request);
        return facilityRepository.save(facility);
    }

    public void delete(Long id) {
        Facility facility = findById(id);
        facilityRepository.delete(facility);
    }

    private void applyRequest(Facility facility, FacilityRequest request) {
        facility.setName(request.getName());
        facility.setDescription(request.getDescription());
        facility.setType(request.getType());
        facility.setCapacity(request.getCapacity());
        facility.setLocation(request.getLocation());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            facility.setStatus(Facility.Status.valueOf(request.getStatus().toUpperCase()));
        }
    }
}
