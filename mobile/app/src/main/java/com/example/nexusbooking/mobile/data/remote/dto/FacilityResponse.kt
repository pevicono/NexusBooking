package com.example.nexusbooking.mobile.data.remote.dto

data class FacilityResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val type: String,
    val capacity: Int?,
    val location: String?,
    val status: String
)
