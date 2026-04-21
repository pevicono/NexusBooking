package com.example.nexusbooking.dto;

import com.example.nexusbooking.model.Incident;
import java.time.LocalDateTime;

public class IncidentResponse {
    private Long id;
    private Long facilityId;
    private String facilityName;
    private Long reportedById;
    private String reportedByEmail;
    private String title;
    private String description;
    private String status;
    private String statusLabel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static IncidentResponse from(Incident i) {
        IncidentResponse r = new IncidentResponse();
        r.setId(i.getId());
        if (i.getFacility() != null) {
            r.setFacilityId(i.getFacility().getId());
            r.setFacilityName(i.getFacility().getName());
        }
        r.setReportedById(i.getReportedBy().getId());
        r.setReportedByEmail(i.getReportedBy().getEmail());
        r.setTitle(i.getTitle());
        r.setDescription(i.getDescription());
        r.setStatus(i.getStatus().name());
        r.setStatusLabel(i.getStatus().getLabel());
        r.setCreatedAt(i.getCreatedAt());
        r.setUpdatedAt(i.getUpdatedAt());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public Long getReportedById() { return reportedById; }
    public void setReportedById(Long reportedById) { this.reportedById = reportedById; }

    public String getReportedByEmail() { return reportedByEmail; }
    public void setReportedByEmail(String reportedByEmail) { this.reportedByEmail = reportedByEmail; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStatusLabel() { return statusLabel; }
    public void setStatusLabel(String statusLabel) { this.statusLabel = statusLabel; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
