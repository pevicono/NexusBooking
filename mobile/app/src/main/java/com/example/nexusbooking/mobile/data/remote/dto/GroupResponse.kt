package com.example.nexusbooking.mobile.data.remote.dto

data class GroupResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val ownerId: Long,
    val ownerEmail: String,
    val memberCount: Int,
    val members: List<GroupMemberResponse> = emptyList()
)

data class GroupMemberResponse(
    val userId: Long,
    val email: String,
    val role: String
)
