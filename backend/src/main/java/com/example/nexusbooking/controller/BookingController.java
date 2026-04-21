package com.example.nexusbooking.controller;

import com.example.nexusbooking.dto.BookingRequest;
import com.example.nexusbooking.dto.BookingResponse;
import com.example.nexusbooking.dto.MessageResponse;
import com.example.nexusbooking.model.User;
import com.example.nexusbooking.service.BookingService;
import com.example.nexusbooking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    public BookingController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @GetMapping("/mine")
    @Operation(summary = "List current user's bookings")
    public ResponseEntity<?> mine(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(bookingService.findByUser(user).stream().map(BookingResponse::from).toList());
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all bookings (admin)")
    public List<BookingResponse> listAll() {
        return bookingService.findAll().stream().map(BookingResponse::from).toList();
    }

    @PostMapping
    @Operation(summary = "Create booking")
    public ResponseEntity<?> create(@AuthenticationPrincipal UserDetails userDetails,
                                    @Valid @RequestBody BookingRequest request) {
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.status(HttpStatus.CREATED).body(BookingResponse.from(bookingService.create(user, request)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel booking")
    public ResponseEntity<?> cancel(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(BookingResponse.from(bookingService.cancel(user, id)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit booking")
    public ResponseEntity<?> update(@AuthenticationPrincipal UserDetails userDetails,
                                    @PathVariable Long id,
                                    @Valid @RequestBody BookingRequest request) {
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(BookingResponse.from(bookingService.update(user, id, request)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }
}
