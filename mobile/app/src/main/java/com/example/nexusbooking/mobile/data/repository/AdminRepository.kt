package com.example.nexusbooking.mobile.data.repository

import com.example.nexusbooking.mobile.data.remote.ApiService
import com.example.nexusbooking.mobile.data.remote.dto.FacilityRequest
import com.example.nexusbooking.mobile.data.remote.dto.IncidentRequest
import com.example.nexusbooking.mobile.data.remote.dto.UserResponse
import com.example.nexusbooking.mobile.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun users(): Resource<List<UserResponse>> = runCatching {
        api.getAdminUsers()
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error(it.message ?: "Error carregant usuaris") }
    )

    suspend fun setUserActive(userId: Long, active: Boolean): Resource<UserResponse> = runCatching {
        api.setUserActive(userId, active)
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error(it.message ?: "Error actualitzant usuari") }
    )

    suspend fun createFacility(name: String, type: String, capacity: Int?, location: String, description: String): Resource<Unit> = runCatching {
        api.createFacility(
            FacilityRequest(
                name = name,
                description = description,
                type = type,
                capacity = capacity,
                location = location
            )
        )
    }.fold(
        onSuccess = { Resource.Success(Unit) },
        onFailure = { Resource.Error(it.message ?: "Error creant instal.lacio") }
    )

    suspend fun createIncident(facilityId: Long?, title: String, description: String): Resource<Unit> = runCatching {
        api.createIncident(IncidentRequest(facilityId = facilityId, title = title, description = description))
    }.fold(
        onSuccess = { Resource.Success(Unit) },
        onFailure = { Resource.Error(it.message ?: "Error creant incidencia") }
    )
}
