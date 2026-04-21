package com.example.nexusbooking.repository;

import com.example.nexusbooking.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByStatusOrderByCreatedAtDesc(Incident.Status status);
}
