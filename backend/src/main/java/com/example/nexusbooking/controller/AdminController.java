package com.example.nexusbooking.controller;

import com.example.nexusbooking.dto.AdminBookingRequest;
import com.example.nexusbooking.dto.AdminGroupRequest;
import com.example.nexusbooking.dto.BookingResponse;
import com.example.nexusbooking.dto.ChangeRoleRequest;
import com.example.nexusbooking.dto.CreateUserRequest;
import com.example.nexusbooking.dto.GroupRequest;
import com.example.nexusbooking.dto.GroupResponse;
import com.example.nexusbooking.dto.IncidentResponse;
import com.example.nexusbooking.dto.MessageResponse;
import com.example.nexusbooking.dto.UserResponse;
import com.example.nexusbooking.model.User;
import com.example.nexusbooking.service.BookingService;
import com.example.nexusbooking.service.GroupService;
import com.example.nexusbooking.service.IncidentService;
import com.example.nexusbooking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin")
public class AdminController {

    private final UserService userService;
    private final BookingService bookingService;
    private final GroupService groupService;
    private final IncidentService incidentService;

    public AdminController(UserService userService,
                           BookingService bookingService,
                           GroupService groupService,
                           IncidentService incidentService) {
        this.userService = userService;
        this.bookingService = bookingService;
        this.groupService = groupService;
        this.incidentService = incidentService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Admin dashboard metrics")
    public Map<String, Object> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("users", userService.findAll().size());
        data.put("bookings", bookingService.findAll().size());
        data.put("groups", groupService.findAll().size());
        data.put("incidents", incidentService.findAll().size());
        return data;
    }

    @GetMapping("/users")
    @Operation(summary = "List users")
    public List<UserResponse> users() {
        return userService.findAll().stream().map(UserResponse::from).toList();
    }

    @PutMapping("/users/{id}/active")
    @Operation(summary = "Enable/disable user")
    public ResponseEntity<?> setUserActive(@PathVariable Long id, @RequestParam boolean active) {
        try {
            User user = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(UserResponse.from(userService.setActive(user, active)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @PostMapping("/users")
    @Operation(summary = "Create user with role (admin)")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUserWithRole(request.getEmail(), request.getPassword(), request.getRole());
            return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @PutMapping("/users/{id}/role")
    @Operation(summary = "Change user role (admin)")
    public ResponseEntity<?> changeUserRole(@PathVariable Long id, @Valid @RequestBody ChangeRoleRequest request) {
        try {
            User user = userService.changeRole(id, request.getRole());
            return ResponseEntity.ok(UserResponse.from(user));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user (admin)")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @GetMapping("/bookings")
    @Operation(summary = "List bookings")
    public List<BookingResponse> bookings() {
        return bookingService.findAll().stream().map(BookingResponse::from).toList();
    }

    @PostMapping("/bookings")
    @Operation(summary = "Create booking for any user (admin)")
    public ResponseEntity<?> createBooking(@Valid @RequestBody AdminBookingRequest request) {
        try {
            User user = userService.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BookingResponse.from(bookingService.createForUser(
                            user, request.getFacilityId(),
                            request.getGroupId(),
                            request.getStartTime(), request.getEndTime(),
                            request.getNotes())));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @PutMapping("/bookings/{id}")
    @Operation(summary = "Edit any booking (admin)")
    public ResponseEntity<?> updateBooking(@PathVariable Long id, @Valid @RequestBody AdminBookingRequest request) {
        try {
            return ResponseEntity.ok(BookingResponse.from(bookingService.adminUpdate(
                    id,
                    request.getUserId(),
                    request.getFacilityId(),
                    request.getGroupId(),
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getNotes())));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @PostMapping("/bookings/{id}/cancel")
    @Operation(summary = "Cancel any booking (admin)")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            bookingService.adminCancel(id);
            return ResponseEntity.ok(new MessageResponse("Booking cancelled"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @GetMapping("/groups")
    @Operation(summary = "List groups")
    public List<GroupResponse> groups() {
        return groupService.findAll().stream().map(GroupResponse::from).toList();
    }

    @PostMapping("/groups")
    @Operation(summary = "Create group for a non-admin owner (admin)")
    public ResponseEntity<?> createGroup(@Valid @RequestBody AdminGroupRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(GroupResponse.from(groupService.adminCreate(
                            request.getName(),
                            request.getDescription(),
                            request.getOwnerId())));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @PutMapping("/groups/{id}")
    @Operation(summary = "Edit any group (admin)")
    public ResponseEntity<?> updateGroup(@PathVariable Long id, @Valid @RequestBody GroupRequest request) {
        try {
            return ResponseEntity.ok(GroupResponse.from(groupService.adminUpdate(id, request)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @DeleteMapping("/groups/{id}")
    @Operation(summary = "Delete any group (admin)")
    public ResponseEntity<?> deleteGroup(@PathVariable Long id) {
        try {
            groupService.adminDelete(id);
            return ResponseEntity.ok(new MessageResponse("Group deleted successfully"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @GetMapping("/incidents")
    @Operation(summary = "List incidents")
    public List<IncidentResponse> incidents() {
        return incidentService.findAll().stream().map(IncidentResponse::from).toList();
    }
}
