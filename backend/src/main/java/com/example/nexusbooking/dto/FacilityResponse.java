package com.example.nexusbooking.dto;

import com.example.nexusbooking.model.Facility;
import java.time.LocalDateTime;

public class FacilityResponse {
    private Long id;
    private String name;
    private String description;
    private String type;
    private Integer capacity;
    private String location;
    private String status;
    private LocalDateTime createdAt;

    public static FacilityResponse from(Facility f) {
        FacilityResponse r = new FacilityResponse();
        r.setId(f.getId());
        r.setName(f.getName());
        r.setDescription(f.getDescription());
        r.setType(f.getType());
        r.setCapacity(f.getCapacity());
        r.setLocation(f.getLocation());
        r.setStatus(f.getStatus().name());
        r.setCreatedAt(f.getCreatedAt());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
