package com.example.nexusbooking.dto;

import com.example.nexusbooking.model.Group;
import com.example.nexusbooking.model.GroupMember;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class GroupResponse {
    private Long id;
    private String joinCode;
    private String name;
    private String description;
    private Long ownerId;
    private String ownerEmail;
    private int memberCount;
    private LocalDateTime createdAt;
    private List<GroupMemberResponse> members;

    public static GroupResponse from(Group g) {
        GroupResponse r = new GroupResponse();
        r.setId(g.getId());
        r.setJoinCode(g.getJoinCode());
        r.setName(g.getName());
        r.setDescription(g.getDescription());
        r.setOwnerId(g.getOwner().getId());
        r.setOwnerEmail(g.getOwner().getEmail());
        r.setMemberCount(g.getMembers().size());
        r.setCreatedAt(g.getCreatedAt());
        r.setMembers(g.getMembers().stream()
                .map(GroupMemberResponse::from)
                .collect(Collectors.toList()));
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getJoinCode() { return joinCode; }
    public void setJoinCode(String joinCode) { this.joinCode = joinCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<GroupMemberResponse> getMembers() { return members; }
    public void setMembers(List<GroupMemberResponse> members) { this.members = members; }

    public static class GroupMemberResponse {
        private Long userId;
        private String email;
        private String role;

        public static GroupMemberResponse from(GroupMember m) {
            GroupMemberResponse r = new GroupMemberResponse();
            r.setUserId(m.getUser().getId());
            r.setEmail(m.getUser().getEmail());
            r.setRole(m.getRole().name());
            return r;
        }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
