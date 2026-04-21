package com.example.nexusbooking.dto;

import jakarta.validation.constraints.NotBlank;

public class ChangeRoleRequest {
    @NotBlank
    private String role;

    public ChangeRoleRequest() {}

    public ChangeRoleRequest(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
