package com.example.nexusbooking.mobile.data.remote.dto

data class IncidentResponse(
    val id: Long,
    val facilityId: Long?,
    val facilityName: String?,
    val reportedById: Long,
    val reportedByEmail: String,
    val title: String,
    val description: String?,
    val status: String
)
