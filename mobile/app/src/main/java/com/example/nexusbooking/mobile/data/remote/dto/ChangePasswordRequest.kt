package com.example.nexusbooking.mobile.data.remote.dto

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)
