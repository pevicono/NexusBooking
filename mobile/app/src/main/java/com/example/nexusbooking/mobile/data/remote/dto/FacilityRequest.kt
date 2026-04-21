package com.example.nexusbooking.mobile.data.remote.dto

data class FacilityRequest(
    val name: String,
    val description: String?,
    val type: String,
    val capacity: Int?,
    val location: String?,
    val status: String? = null
)
