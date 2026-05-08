package com.example.nexusbooking.mobile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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

class ExampleInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun login_withEmptyFields_showsValidationMessage() {
        composeRule.onNodeWithText("Entrar").performClick()
        composeRule.onNodeWithText("Completa todos los campos").assertIsDisplayed()
    }

    @Test
    fun register_withShortPassword_showsValidationMessage() {
        composeRule.onNodeWithText("Registrarse").performClick()

        composeRule.onNodeWithText("Email").performTextInput("mobile@example.com")
        composeRule.onNodeWithText("Contraseña").performTextInput("123")
        composeRule.onNodeWithText("Confirmar contraseña").performTextInput("123")
        composeRule.onNodeWithText("Crear cuenta").performClick()

        composeRule.onNodeWithText("La contraseña debe tener al menos 6 caracteres").assertIsDisplayed()
    }

    @Test
    fun register_withMismatchedPassword_showsValidationMessage() {
        composeRule.onNodeWithText("Registrarse").performClick()

        composeRule.onNodeWithText("Email").performTextInput("mobile@example.com")
        composeRule.onNodeWithText("Contraseña").performTextInput("Password123!")
        composeRule.onNodeWithText("Confirmar contraseña").performTextInput("Mismatch123!")
        composeRule.onNodeWithText("Crear cuenta").performClick()

        composeRule.onNodeWithText("Las contraseñas no coinciden").assertIsDisplayed()
    }

    @Test
    fun authTabs_switchBetweenLoginAndRegister() {
        composeRule.onNodeWithText("Registrarse").performClick()
        composeRule.onNodeWithText("Crear cuenta").assertIsDisplayed()

        composeRule.onNodeWithText("Iniciar sesión").performClick()
        composeRule.onNodeWithText("Entrar").assertIsDisplayed()
    }
}