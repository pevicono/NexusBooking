package com.example.nexusbooking.mobile.data.repository

import com.example.nexusbooking.mobile.data.remote.ApiService
import com.example.nexusbooking.mobile.data.remote.dto.BookingRequest
import com.example.nexusbooking.mobile.data.remote.dto.BookingResponse
import com.example.nexusbooking.mobile.data.remote.dto.FacilityResponse
import com.example.nexusbooking.mobile.data.remote.dto.GroupRequest
import com.example.nexusbooking.mobile.data.remote.dto.GroupResponse
import com.example.nexusbooking.mobile.util.Resource
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
        onFailure = { Resource.Error(it.message ?: "Error creant reserva") }
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
}
