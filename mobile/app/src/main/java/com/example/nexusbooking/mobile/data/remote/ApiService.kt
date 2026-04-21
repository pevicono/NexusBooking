package com.example.nexusbooking.mobile.data.remote

import com.example.nexusbooking.mobile.data.remote.dto.AuthResponse
import com.example.nexusbooking.mobile.data.remote.dto.BookingRequest
import com.example.nexusbooking.mobile.data.remote.dto.BookingResponse
import com.example.nexusbooking.mobile.data.remote.dto.ChangePasswordRequest
import com.example.nexusbooking.mobile.data.remote.dto.FacilityRequest
import com.example.nexusbooking.mobile.data.remote.dto.FacilityResponse
import com.example.nexusbooking.mobile.data.remote.dto.GroupRequest
import com.example.nexusbooking.mobile.data.remote.dto.GroupResponse
import com.example.nexusbooking.mobile.data.remote.dto.IncidentRequest
import com.example.nexusbooking.mobile.data.remote.dto.IncidentResponse
import com.example.nexusbooking.mobile.data.remote.dto.LoginRequest
import com.example.nexusbooking.mobile.data.remote.dto.MessageResponse
import com.example.nexusbooking.mobile.data.remote.dto.RegisterRequest
import com.example.nexusbooking.mobile.data.remote.dto.UpdateEmailRequest
import com.example.nexusbooking.mobile.data.remote.dto.UserResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): UserResponse

    @POST("api/auth/logout")
    suspend fun logout()

    @GET("api/users/me")
    suspend fun getCurrentUser(): UserResponse

    @PUT("api/users/me")
    suspend fun updateEmail(@Body request: UpdateEmailRequest): UserResponse

    @PUT("api/users/me/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest)

    @GET("api/facilities")
    suspend fun getFacilities(): List<FacilityResponse>

    @POST("api/facilities")
    suspend fun createFacility(@Body request: FacilityRequest): FacilityResponse

    @PUT("api/facilities/{id}")
    suspend fun updateFacility(@Path("id") id: Long, @Body request: FacilityRequest): FacilityResponse

    @DELETE("api/facilities/{id}")
    suspend fun deleteFacility(@Path("id") id: Long): MessageResponse

    @GET("api/bookings/mine")
    suspend fun getMyBookings(): List<BookingResponse>

    @GET("api/bookings")
    suspend fun getAllBookings(): List<BookingResponse>

    @POST("api/bookings")
    suspend fun createBooking(@Body request: BookingRequest): BookingResponse

    @POST("api/bookings/{id}/cancel")
    suspend fun cancelBooking(@Path("id") id: Long): BookingResponse

    @GET("api/groups")
    suspend fun getGroups(): List<GroupResponse>

    @GET("api/groups/mine")
    suspend fun getMyGroups(): List<GroupResponse>

    @POST("api/groups")
    suspend fun createGroup(@Body request: GroupRequest): GroupResponse

    @POST("api/groups/{id}/join")
    suspend fun joinGroup(@Path("id") id: Long): GroupResponse

    @POST("api/groups/join-by-code")
    suspend fun joinGroupByCode(@Query("code") code: String): GroupResponse

    @POST("api/groups/{id}/leave")
    suspend fun leaveGroup(@Path("id") id: Long): MessageResponse

    @GET("api/incidents")
    suspend fun getIncidents(): List<IncidentResponse>

    @POST("api/incidents")
    suspend fun createIncident(@Body request: IncidentRequest): IncidentResponse

    @PUT("api/incidents/{id}/status")
    suspend fun updateIncidentStatus(@Path("id") id: Long, @Body request: IncidentRequest): IncidentResponse

    @GET("api/admin/users")
    suspend fun getAdminUsers(): List<UserResponse>

    @PUT("api/admin/users/{id}/active")
    suspend fun setUserActive(@Path("id") id: Long, @Query("active") active: Boolean): UserResponse
}
