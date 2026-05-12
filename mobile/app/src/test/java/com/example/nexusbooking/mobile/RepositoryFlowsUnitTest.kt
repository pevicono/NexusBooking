package com.example.nexusbooking.mobile

import com.example.nexusbooking.mobile.data.remote.ApiService
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
import com.example.nexusbooking.mobile.data.repository.AdminRepository
import com.example.nexusbooking.mobile.data.repository.HomeRepository
import com.example.nexusbooking.mobile.data.repository.UserRepository
import com.example.nexusbooking.mobile.util.Resource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositoryFlowsUnitTest {

    @Test
    fun homeRepository_facilities_success_returnsResourceSuccess() = runBlocking {
        val api = FakeApiService().apply {
            facilitiesResult = listOf(
                FacilityResponse(1, "Pista Central", "Desc", "SPORT", 20, "Barcelona", "ACTIVE")
            )
        }

        val result = HomeRepository(api).facilities()

        assertTrue(result is Resource.Success)
        result as Resource.Success
        assertEquals(1, result.data.size)
        assertEquals("Pista Central", result.data.first().name)
    }

    @Test
    fun homeRepository_createGroup_failure_returnsResourceError() = runBlocking {
        val api = FakeApiService().apply {
            createGroupError = RuntimeException("join code generation failed")
        }

        val result = HomeRepository(api).createGroup("Team", "Desc")

        assertTrue(result is Resource.Error)
        result as Resource.Error
        assertTrue(result.message.contains("join code generation failed"))
    }

    @Test
    fun adminRepository_createFacility_success_returnsUnitSuccess() = runBlocking {
        val api = FakeApiService()

        val result = AdminRepository(api).createFacility(
            name = "Sala Nova",
            type = "MEETING_ROOM",
            capacity = 10,
            location = "Planta 2",
            description = "Sala per reunions"
        )

        assertTrue(result is Resource.Success)
    }

    @Test
    fun userRepository_updateEmail_success_returnsUpdatedUser() = runBlocking {
        val api = FakeApiService().apply {
            currentUserResult = UserResponse(5, "old@example.com", "USER", true)
        }

        val result = UserRepository(api).updateEmail("new@example.com")

        assertTrue(result is Resource.Success)
        result as Resource.Success
        assertEquals("new@example.com", result.data.email)
    }

    @Test
    fun userRepository_changePassword_failure_returnsResourceError() = runBlocking {
        val api = FakeApiService().apply {
            changePasswordError = RuntimeException("invalid current password")
        }

        val result = UserRepository(api).changePassword("oldPass", "newPass")

        assertTrue(result is Resource.Error)
        result as Resource.Error
        assertTrue(result.message.contains("invalid current password"))
    }

    private class FakeApiService : ApiService {
        var facilitiesResult: List<FacilityResponse> = emptyList()
        var createGroupError: Throwable? = null
        var currentUserResult: UserResponse = UserResponse(2, "user@example.com", "USER", true)
        var changePasswordError: Throwable? = null

        override suspend fun login(request: LoginRequest): AuthResponse = AuthResponse("token", "Bearer")

        override suspend fun register(request: RegisterRequest): UserResponse = currentUserResult

        override suspend fun logout() = Unit

        override suspend fun getCurrentUser(): UserResponse = currentUserResult

        override suspend fun updateEmail(request: UpdateEmailRequest): UserResponse =
            currentUserResult.copy(email = request.email)

        override suspend fun changePassword(request: ChangePasswordRequest) {
            changePasswordError?.let { throw it }
        }

        override suspend fun getFacilities(): List<FacilityResponse> = facilitiesResult

        override suspend fun createFacility(request: FacilityRequest): FacilityResponse =
            FacilityResponse(99, request.name, request.description, request.type, request.capacity, request.location, "ACTIVE")

        override suspend fun updateFacility(id: Long, request: FacilityRequest): FacilityResponse =
            FacilityResponse(id, request.name, request.description, request.type, request.capacity, request.location, "ACTIVE")

        override suspend fun deleteFacility(id: Long): MessageResponse = MessageResponse("deleted")

        override suspend fun getMyBookings(): List<BookingResponse> = emptyList()

        override suspend fun getAllBookings(): List<BookingResponse> = emptyList()

        override suspend fun createBooking(request: BookingRequest): BookingResponse =
            BookingResponse(1, request.facilityId, "Pista Central", request.groupId, "Team Alpha", request.startTime, request.endTime, "ACTIVE", request.notes)

        override suspend fun cancelBooking(id: Long): BookingResponse =
            BookingResponse(id, 1, "Pista Central", null, null, "2026-05-01T10:00:00", "2026-05-01T11:00:00", "CANCELLED", null)

        override suspend fun getGroups(): List<GroupResponse> = emptyList()

        override suspend fun getMyGroups(): List<GroupResponse> = emptyList()

        override suspend fun createGroup(request: GroupRequest): GroupResponse {
            createGroupError?.let { throw it }
            return GroupResponse(55, request.name, request.description, 2, "user@example.com", 1)
        }

        override suspend fun joinGroup(id: Long): GroupResponse = GroupResponse(55, "Team", "Desc", 2, "user@example.com", 2)

        override suspend fun joinGroupByCode(code: String): GroupResponse = GroupResponse(55, "Team", "Desc", 2, "user@example.com", 2)

        override suspend fun leaveGroup(id: Long): MessageResponse = MessageResponse("left")

        override suspend fun getIncidents(): List<IncidentResponse> = emptyList()

        override suspend fun createIncident(request: IncidentRequest): IncidentResponse =
            IncidentResponse(1, request.facilityId, "Pista Central", 2, "user@example.com", request.title ?: "", request.description, "OPEN")

        override suspend fun updateIncidentStatus(id: Long, request: IncidentRequest): IncidentResponse =
            IncidentResponse(id, request.facilityId, "Pista Central", 2, "user@example.com", request.title ?: "", request.description, "IN_PROGRESS")

        override suspend fun getAdminUsers(): List<UserResponse> = listOf(currentUserResult)

        override suspend fun setUserActive(id: Long, active: Boolean): UserResponse =
            currentUserResult.copy(id = id, active = active)
    }
}
