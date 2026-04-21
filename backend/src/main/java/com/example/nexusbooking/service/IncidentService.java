package com.example.nexusbooking.service;

import com.example.nexusbooking.dto.IncidentRequest;
import com.example.nexusbooking.model.Facility;
import com.example.nexusbooking.model.Incident;
import com.example.nexusbooking.model.User;
import com.example.nexusbooking.repository.FacilityRepository;
import com.example.nexusbooking.repository.IncidentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final FacilityRepository facilityRepository;

    public IncidentService(IncidentRepository incidentRepository, FacilityRepository facilityRepository) {
        this.incidentRepository = incidentRepository;
        this.facilityRepository = facilityRepository;
    }

    public List<Incident> findAll() {
        return incidentRepository.findAll();
    }

    public Incident findById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));
    }

    public Incident create(User reportedBy, IncidentRequest request) {
        Incident incident = new Incident();
        incident.setReportedBy(reportedBy);
        incident.setTitle(request.getTitle());
        incident.setDescription(request.getDescription());

        if (request.getFacilityId() != null) {
            Facility facility = facilityRepository.findById(request.getFacilityId())
                    .orElseThrow(() -> new RuntimeException("Facility not found"));
            incident.setFacility(facility);
        }

        return incidentRepository.save(incident);
    }

    public Incident updateStatus(Long incidentId, String status) {
        Incident incident = findById(incidentId);
        incident.setStatus(Incident.Status.valueOf(status.toUpperCase()));
        return incidentRepository.save(incident);
    }

    public void deleteIncident(Long incidentId) {
        Incident incident = findById(incidentId);
        incidentRepository.delete(incident);
    }
}
