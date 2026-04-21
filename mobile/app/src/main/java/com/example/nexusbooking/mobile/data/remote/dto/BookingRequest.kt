package com.example.nexusbooking.mobile.data.remote.dto

data class BookingRequest(
    val facilityId: Long,
    val groupId: Long? = null,
    val startTime: String,
    val endTime: String,
    val notes: String? = null
)
