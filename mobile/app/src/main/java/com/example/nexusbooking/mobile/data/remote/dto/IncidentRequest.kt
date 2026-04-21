package com.example.nexusbooking.mobile.data.remote.dto

data class IncidentRequest(
    val facilityId: Long? = null,
    val title: String? = null,
    val description: String? = null,
    val status: String? = null
)
