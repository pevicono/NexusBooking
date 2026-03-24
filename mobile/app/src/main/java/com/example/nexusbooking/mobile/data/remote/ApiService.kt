package com.example.nexusbooking.mobile.data.remote

import com.example.nexusbooking.mobile.data.remote.dto.AuthResponse
import com.example.nexusbooking.mobile.data.remote.dto.ChangePasswordRequest
import com.example.nexusbooking.mobile.data.remote.dto.LoginRequest
import com.example.nexusbooking.mobile.data.remote.dto.RegisterRequest
import com.example.nexusbooking.mobile.data.remote.dto.UpdateEmailRequest
import com.example.nexusbooking.mobile.data.remote.dto.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

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
}
