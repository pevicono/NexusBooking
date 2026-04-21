package com.example.nexusbooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class IncidentRequest {

    private Long facilityId;

    @NotBlank
    @Size(max = 200)
    private String title;

    private String description;

    private String status;

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
