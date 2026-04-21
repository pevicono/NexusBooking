package com.example.nexusbooking.dto;

import com.example.nexusbooking.model.Booking;
import java.time.LocalDateTime;

public class BookingResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private Long facilityId;
    private String facilityName;
    private Long groupId;
    private String groupName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String notes;
    private LocalDateTime createdAt;

    public static BookingResponse from(Booking b) {
        BookingResponse r = new BookingResponse();
        r.setId(b.getId());
        r.setUserId(b.getUser().getId());
        r.setUserEmail(b.getUser().getEmail());
        r.setFacilityId(b.getFacility().getId());
        r.setFacilityName(b.getFacility().getName());
        if (b.getGroup() != null) {
            r.setGroupId(b.getGroup().getId());
            r.setGroupName(b.getGroup().getName());
        }
        r.setStartTime(b.getStartTime());
        r.setEndTime(b.getEndTime());
        r.setStatus(b.getStatus().name());
        r.setNotes(b.getNotes());
        r.setCreatedAt(b.getCreatedAt());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
