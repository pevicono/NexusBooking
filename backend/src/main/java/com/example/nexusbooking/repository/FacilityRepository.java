package com.example.nexusbooking.repository;

import com.example.nexusbooking.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
    List<Facility> findByStatus(Facility.Status status);
}
