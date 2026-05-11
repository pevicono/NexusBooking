package com.example.nexusbooking.mobile.data.repository

import android.util.Log
import com.example.nexusbooking.mobile.data.remote.ApiService
import com.example.nexusbooking.mobile.data.remote.dto.BookingRequest
import com.example.nexusbooking.mobile.data.remote.dto.BookingResponse
import com.example.nexusbooking.mobile.data.remote.dto.FacilityRequest
import com.example.nexusbooking.mobile.data.remote.dto.FacilityResponse
import com.example.nexusbooking.mobile.data.remote.dto.GroupRequest
import com.example.nexusbooking.mobile.data.remote.dto.GroupResponse
import com.example.nexusbooking.mobile.data.remote.dto.IncidentRequest
import com.example.nexusbooking.mobile.data.remote.dto.IncidentResponse
import com.example.nexusbooking.mobile.data.remote.dto.UserResponse
import com.example.nexusbooking.mobile.util.Resource
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun facilities(): Resource<List<FacilityResponse>> = runCatching {
        api.getFacilities()
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error(it.message ?: "Error carregant instal.lacions") }
    )

    suspend fun myBookings(): Resource<List<BookingResponse>> = runCatching {
        api.getMyBookings()
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error(it.message ?: "Error carregant reserves") }
    )

    suspend fun createBooking(request: BookingRequest): Resource<BookingResponse> = runCatching {
        api.createBooking(request)
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { throwable ->
            val errorMessage = when (throwable) {
                is HttpException -> {
                    val errorBody = throwable.response()?.errorBody()?.string() ?: ""
                    val message = "HTTP ${throwable.code()}: $errorBody"
                    Log.e("HomeRepository", message)
                    message
                }
                else -> {
                    Log.e("HomeRepository", "Error creating booking", throwable)
                    throwable.message ?: "Error creant reserva"
                }
            }
            Resource.Error(errorMessage)
        }
    )

    suspend fun cancelBooking(bookingId: Long): Resource<BookingResponse> = runCatching {
        api.cancelBooking(bookingId)
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error(it.message ?: "Error cancel.lant reserva") }
    )

    suspend fun groups(): Resource<List<GroupResponse>> = runCatching {
        api.getMyGroups()
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error(it.message ?: "Error carregant grups") }
    )

    suspend fun createGroup(name: String, description: String): Resource<GroupResponse> = runCatching {
        api.createGroup(GroupRequest(name, description))
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error(it.message ?: "Error creant grup") }
    )

    suspend fun joinGroup(groupId: Long): Resource<GroupResponse> = runCatching {
        api.joinGroup(groupId)
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error(it.message ?: "Error afegint-se al grup") }
    )

    suspend fun joinGroupByCode(code: String): Resource<GroupResponse> = runCatching {
        api.joinGroupByCode(code)
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error(it.message ?: "Error afegint-se al grup") }
    )

    suspend fun leaveGroup(groupId: Long): Resource<Unit> = runCatching {
        api.leaveGroup(groupId)
    }.fold(
        onSuccess = { Resource.Success(Unit) },
        onFailure = { Resource.Error(it.message ?: "Error sortint del grup") }
    )

    suspend fun getAllUsers(): Resource<List<UserResponse>> = runCatching {
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

    suspend fun createFacility(name: String, type: String, capacity: Int): Resource<FacilityResponse> = runCatching {
        api.createFacility(FacilityRequest(name, "", type, capacity, ""))
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error(it.message ?: "Error creant instal·lació") }
    )

    suspend fun createIncident(title: String, description: String): Resource<IncidentResponse> = runCatching {
        api.createIncident(IncidentRequest(title = title, description = description))
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error(it.message ?: "Error creant incidència") }
    )
}
