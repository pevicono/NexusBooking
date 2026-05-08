package com.example.nexusbooking.mobile

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.example.nexusbooking.mobile.data.local.TokenDataStore
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
import com.example.nexusbooking.mobile.data.repository.AuthRepository
import com.example.nexusbooking.mobile.data.repository.HomeRepository
import com.example.nexusbooking.mobile.data.repository.UserRepository
import com.example.nexusbooking.mobile.ui.admin.AdminScreen
import com.example.nexusbooking.mobile.ui.admin.AdminViewModel
import com.example.nexusbooking.mobile.ui.home.HomeScreen
import com.example.nexusbooking.mobile.ui.home.HomeViewModel
import com.example.nexusbooking.mobile.ui.profile.ProfileScreen
import com.example.nexusbooking.mobile.ui.profile.ProfileViewModel
import com.example.nexusbooking.mobile.ui.theme.NexusBookingTheme
import org.junit.Rule
import org.junit.Test

class RoleUiComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun homeScreen_userRole_showsUserTabsAndNoBackofficeEntry() {
        val fakeApi = FakeApiService(
            currentUser = user(role = "USER", email = "user@example.com"),
            facilities = listOf(
                FacilityResponse(1, "Pista Central", "Desc", "SPORT", 20, "Barcelona", "ACTIVE")
            ),
            groups = listOf(
                GroupResponse(10, "Team Alpha", "Desc", 2, "owner@example.com", 2)
            )
        )

        composeRule.setContent {
            NexusBookingTheme {
                HomeScreen(
                    onOpenProfile = {},
                    onOpenAdmin = {},
                    viewModel = HomeViewModel(UserRepository(fakeApi), HomeRepository(fakeApi))
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Perfil").assertIsDisplayed()
        composeRule.onNodeWithText("Instal.lacions").assertIsDisplayed()
        composeRule.onNodeWithText("Reserves").assertIsDisplayed()
        composeRule.onNodeWithText("Grups").assertIsDisplayed()
        composeRule.onAllNodesWithText("Anar a Backoffice").assertCountEquals(0)
    }

    @Test
    fun homeScreen_adminRole_showsBackofficeEntryAndHidesUserTabs() {
        val fakeApi = FakeApiService(
            currentUser = user(role = "ADMIN", email = "admin@example.com")
        )

        composeRule.setContent {
            NexusBookingTheme {
                HomeScreen(
                    onOpenProfile = {},
                    onOpenAdmin = {},
                    viewModel = HomeViewModel(UserRepository(fakeApi), HomeRepository(fakeApi))
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Backoffice").assertIsDisplayed()
        composeRule.onNodeWithText("Anar a Backoffice").assertIsDisplayed()
        composeRule.onAllNodesWithText("Instal.lacions").assertCountEquals(0)
        composeRule.onAllNodesWithText("Crear reserva").assertCountEquals(0)
    }

    @Test
    fun adminScreen_adminRole_showsBackofficeActions() {
        val fakeApi = FakeApiService(
            adminUsers = listOf(
                user(id = 1, role = "ADMIN", email = "admin@example.com"),
                user(id = 2, role = "USER", email = "user@example.com")
            )
        )

        composeRule.setContent {
            NexusBookingTheme {
                AdminScreen(
                    onBack = {},
                    viewModel = AdminViewModel(AdminRepository(fakeApi))
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Backoffice").assertIsDisplayed()
        composeRule.onNodeWithText("Gestio d'usuaris").assertIsDisplayed()
        composeRule.onNodeWithText("Guardar estat usuari").assertIsDisplayed()
        composeRule.onNodeWithText("Facility ID (opcional)").assertIsDisplayed()
    }

    @Test
    fun profileScreen_authenticatedUser_canOpenEditActions() {
        val fakeApi = FakeApiService(
            currentUser = user(role = "USER", email = "profile@example.com")
        )
        val tokenStore = TokenDataStore(
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        )

        composeRule.setContent {
            NexusBookingTheme {
                ProfileScreen(
                    onLogout = {},
                    viewModel = ProfileViewModel(
                        UserRepository(fakeApi),
                        AuthRepository(fakeApi, tokenStore)
                    )
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Mi perfil").assertIsDisplayed()
        composeRule.onNodeWithText("profile@example.com").assertIsDisplayed()
        composeRule.onNodeWithText("Editar perfil").performClick()
        composeRule.onNodeWithText("Cambiar contraseña").assertIsDisplayed()
    }

    private fun user(id: Long = 2, role: String, email: String): UserResponse {
        return UserResponse(id = id, email = email, role = role, active = true)
    }

    private class FakeApiService(
        private val currentUser: UserResponse = UserResponse(2, "user@example.com", "USER", true),
        private val facilities: List<FacilityResponse> = emptyList(),
        private val bookings: List<BookingResponse> = emptyList(),
        private val groups: List<GroupResponse> = emptyList(),
        private val adminUsers: List<UserResponse> = emptyList()
    ) : ApiService {
        override suspend fun login(request: LoginRequest): AuthResponse = AuthResponse("token", "Bearer")

        override suspend fun register(request: RegisterRequest): UserResponse = currentUser

        override suspend fun logout() = Unit

        override suspend fun getCurrentUser(): UserResponse = currentUser

        override suspend fun updateEmail(request: UpdateEmailRequest): UserResponse =
            currentUser.copy(email = request.email)

        override suspend fun changePassword(request: ChangePasswordRequest) = Unit

        override suspend fun getFacilities(): List<FacilityResponse> = facilities

        override suspend fun createFacility(request: FacilityRequest): FacilityResponse =
            FacilityResponse(99, request.name, request.description, request.type, request.capacity, request.location, "ACTIVE")

        override suspend fun updateFacility(id: Long, request: FacilityRequest): FacilityResponse =
            FacilityResponse(id, request.name, request.description, request.type, request.capacity, request.location, "ACTIVE")

        override suspend fun deleteFacility(id: Long): MessageResponse = MessageResponse("deleted")

        override suspend fun getMyBookings(): List<BookingResponse> = bookings

        override suspend fun getAllBookings(): List<BookingResponse> = bookings

        override suspend fun createBooking(request: BookingRequest): BookingResponse =
            BookingResponse(1, request.facilityId, "Pista Central", request.groupId, "Team Alpha", request.startTime, request.endTime, "ACTIVE", request.notes)

        override suspend fun cancelBooking(id: Long): BookingResponse =
            bookings.firstOrNull() ?: BookingResponse(id, 1, "Pista Central", null, null, "2026-05-01T10:00:00", "2026-05-01T11:00:00", "CANCELLED", null)

        override suspend fun getGroups(): List<GroupResponse> = groups

        override suspend fun getMyGroups(): List<GroupResponse> = groups

        override suspend fun createGroup(request: GroupRequest): GroupResponse =
            GroupResponse(55, request.name, request.description, currentUser.id, currentUser.email, 1)

        override suspend fun joinGroup(id: Long): GroupResponse = groups.first()

        override suspend fun joinGroupByCode(code: String): GroupResponse = groups.first()

        override suspend fun leaveGroup(id: Long): MessageResponse = MessageResponse("left")

        override suspend fun getIncidents(): List<IncidentResponse> = emptyList()

        override suspend fun createIncident(request: IncidentRequest): IncidentResponse =
            IncidentResponse(1, request.facilityId, "Pista Central", currentUser.id, currentUser.email, request.title ?: "", request.description, "OPEN")

        override suspend fun updateIncidentStatus(id: Long, request: IncidentRequest): IncidentResponse =
            IncidentResponse(id, request.facilityId, "Pista Central", currentUser.id, currentUser.email, request.title ?: "", request.description, "IN_PROGRESS")

        override suspend fun getAdminUsers(): List<UserResponse> = adminUsers

        override suspend fun setUserActive(id: Long, active: Boolean): UserResponse =
            adminUsers.firstOrNull { it.id == id }?.copy(active = active) ?: currentUser.copy(id = id, active = active)
    }
}
