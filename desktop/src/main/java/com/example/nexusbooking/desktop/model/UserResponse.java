package com.example.nexusbooking.desktop.model;

public class UserResponse {
    private Long id;
    private String email;
    private String role;
    private boolean active;

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }
}
