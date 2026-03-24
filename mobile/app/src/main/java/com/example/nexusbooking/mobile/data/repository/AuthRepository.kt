package com.example.nexusbooking.mobile.data.repository

import com.example.nexusbooking.mobile.data.local.TokenDataStore
import com.example.nexusbooking.mobile.data.remote.ApiService
import com.example.nexusbooking.mobile.data.remote.dto.LoginRequest
import com.example.nexusbooking.mobile.data.remote.dto.RegisterRequest
import com.example.nexusbooking.mobile.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val tokenDataStore: TokenDataStore
) {
    suspend fun login(email: String, password: String): Resource<Unit> = runCatching {
        val response = api.login(LoginRequest(email, password))
        tokenDataStore.saveToken(response.token)
    }.fold(
        onSuccess = { Resource.Success(Unit) },
        onFailure = { Resource.Error(it.message ?: "Error desconocido") }
    )

    suspend fun register(email: String, password: String): Resource<Unit> = runCatching {
        api.register(RegisterRequest(email, password))
    }.fold(
        onSuccess = { Resource.Success(Unit) },
        onFailure = { Resource.Error(it.message ?: "Error desconocido") }
    )

    suspend fun logout(): Resource<Unit> = runCatching {
        api.logout()
        tokenDataStore.clearToken()
    }.fold(
        onSuccess = { Resource.Success(Unit) },
        onFailure = {
            tokenDataStore.clearToken()
            Resource.Success(Unit)
        }
    )
}
