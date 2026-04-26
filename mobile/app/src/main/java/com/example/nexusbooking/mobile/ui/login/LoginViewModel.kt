package com.example.nexusbooking.mobile.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexusbooking.mobile.data.repository.AuthRepository
import com.example.nexusbooking.mobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState

    private val _registerState = MutableStateFlow(LoginUiState())
    val registerState: StateFlow<LoginUiState> = _registerState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginUiState(isLoading = true)
            _loginState.value = when (val result = authRepository.login(email, password)) {
                is Resource.Success -> LoginUiState(success = true)
                is Resource.Error -> LoginUiState(error = result.message)
                is Resource.Loading -> LoginUiState(isLoading = true)
            }
        }
    }

    fun register(email: String, password: String) {
        val strengthWarning = validatePasswordStrength(password)
        if (email.isEmpty() || password.isEmpty()) {
            _registerState.value = LoginUiState(error = "Email and password are required")
            return
        }
        
        viewModelScope.launch {
            _registerState.value = LoginUiState(isLoading = true)
            _registerState.value = when (val result = authRepository.register(email, password)) {
                is Resource.Success -> {
                    if (strengthWarning != null) {
                        LoginUiState(success = true, error = "Note: $strengthWarning")
                    } else {
                        LoginUiState(success = true)
                    }
                }
                is Resource.Error -> LoginUiState(error = result.message)
                is Resource.Loading -> LoginUiState(isLoading = true)
            }
        }
    }

    fun clearLoginError() { _loginState.value = LoginUiState() }
    fun clearRegisterError() { _registerState.value = LoginUiState() }
    
    /**
        * Validate password strength
     * Returns warning message if password is weak, null if strong
     */
    private fun validatePasswordStrength(password: String): String? {
        if (password.length < 8) {
            return "Password should be at least 8 characters"
        }
        
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigits = password.any { it.isDigit() }
        val hasSpecialChars = password.any { !it.isLetterOrDigit() }
        
        val strength = listOf(hasUppercase, hasLowercase, hasDigits, hasSpecialChars).count { it }
        
        if (strength < 2) {
            return "Password should include uppercase, lowercase, and numbers"
        }
        
        return null
    }
}
