package com.example.nexusbooking.controller;

import com.example.nexusbooking.dto.GroupRequest;
import com.example.nexusbooking.dto.GroupResponse;
import com.example.nexusbooking.dto.MessageResponse;
import com.example.nexusbooking.model.User;
import com.example.nexusbooking.service.GroupService;
import com.example.nexusbooking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
@Tag(name = "Groups")
public class GroupController {

    private final GroupService groupService;
    private final UserService userService;

    public GroupController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "List groups")
    public List<GroupResponse> list() {
        return groupService.findAll().stream().map(GroupResponse::from).toList();
    }

    @GetMapping("/mine")
    @Operation(summary = "List current user's groups")
    public ResponseEntity<?> mine(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(groupService.findByMember(user).stream().map(GroupResponse::from).toList());
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Create a group")
    public ResponseEntity<?> create(@AuthenticationPrincipal UserDetails userDetails,
                                    @Valid @RequestBody GroupRequest request) {
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.status(HttpStatus.CREATED).body(GroupResponse.from(groupService.create(user, request)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @PostMapping("/{id}/join")
    @Operation(summary = "Join a group")
    public ResponseEntity<?> join(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(GroupResponse.from(groupService.join(user, id)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @PostMapping("/join-by-code")
    @Operation(summary = "Join a group by join code")
    public ResponseEntity<?> joinByCode(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestParam String code) {
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(GroupResponse.from(groupService.joinByCode(user, code)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @PostMapping("/{id}/leave")
    @Operation(summary = "Leave a group")
    public ResponseEntity<?> leave(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            groupService.leave(user, id);
            return ResponseEntity.ok(new MessageResponse("Left group successfully"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a group (owner only)")
    public ResponseEntity<?> delete(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            groupService.delete(user, id);
            return ResponseEntity.ok(new MessageResponse("Group deleted successfully"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit a group (owner only)")
    public ResponseEntity<?> update(@AuthenticationPrincipal UserDetails userDetails,
                                    @PathVariable Long id,
                                    @Valid @RequestBody GroupRequest request) {
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(GroupResponse.from(groupService.update(user, id, request)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }
}
