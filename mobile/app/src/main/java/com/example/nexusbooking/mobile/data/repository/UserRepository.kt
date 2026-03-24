package com.example.nexusbooking.mobile.data.repository

import com.example.nexusbooking.mobile.data.remote.ApiService
import com.example.nexusbooking.mobile.data.remote.dto.ChangePasswordRequest
import com.example.nexusbooking.mobile.data.remote.dto.UpdateEmailRequest
import com.example.nexusbooking.mobile.data.remote.dto.UserResponse
import com.example.nexusbooking.mobile.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getCurrentUser(): Resource<UserResponse> = runCatching {
        api.getCurrentUser()
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error(it.message ?: "Error desconocido") }
    )

    suspend fun updateEmail(newEmail: String): Resource<UserResponse> = runCatching {
        api.updateEmail(UpdateEmailRequest(newEmail))
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error(it.message ?: "Error desconocido") }
    )

    suspend fun changePassword(current: String, new: String): Resource<Unit> = runCatching {
        api.changePassword(ChangePasswordRequest(current, new))
    }.fold(
        onSuccess = { Resource.Success(Unit) },
        onFailure = { Resource.Error(it.message ?: "Error desconocido") }
    )
}
