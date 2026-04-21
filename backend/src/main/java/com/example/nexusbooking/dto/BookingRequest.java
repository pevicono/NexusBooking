package com.example.nexusbooking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class BookingRequest {

    @NotNull
    private Long facilityId;

    @NotNull
    private Long groupId;

    @NotNull
    @Future
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    private String notes;

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
