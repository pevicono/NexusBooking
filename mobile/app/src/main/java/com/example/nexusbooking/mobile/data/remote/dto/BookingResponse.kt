package com.example.nexusbooking.mobile.data.remote.dto

data class BookingResponse(
    val id: Long,
    val facilityId: Long,
    val facilityName: String,
    val groupId: Long?,
    val groupName: String?,
    val startTime: String,
    val endTime: String,
    val status: String,
    val notes: String?
)
