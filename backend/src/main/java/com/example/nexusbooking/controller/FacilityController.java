package com.example.nexusbooking.controller;

import com.example.nexusbooking.dto.FacilityRequest;
import com.example.nexusbooking.dto.FacilityResponse;
import com.example.nexusbooking.dto.MessageResponse;
import com.example.nexusbooking.service.FacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@Tag(name = "Facilities")
public class FacilityController {

    private final FacilityService facilityService;

    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @GetMapping
    @Operation(summary = "List all facilities")
    public List<FacilityResponse> list() {
        return facilityService.findAll().stream().map(FacilityResponse::from).toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a facility (admin)")
    public ResponseEntity<?> create(@Valid @RequestBody FacilityRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(FacilityResponse.from(facilityService.create(request)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a facility (admin)")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody FacilityRequest request) {
        try {
            return ResponseEntity.ok(FacilityResponse.from(facilityService.update(id, request)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a facility (admin)")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            facilityService.delete(id);
            return ResponseEntity.ok(new MessageResponse("Facility deleted successfully"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }
}
