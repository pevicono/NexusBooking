package com.example.nexusbooking.mobile.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexusbooking.mobile.data.remote.dto.UserResponse
import com.example.nexusbooking.mobile.data.repository.AuthRepository
import com.example.nexusbooking.mobile.data.repository.UserRepository
import com.example.nexusbooking.mobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: UserResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val isLoggedOut: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            _state.value = when (val result = userRepository.getCurrentUser()) {
                is Resource.Success -> _state.value.copy(user = result.data, isLoading = false)
                is Resource.Error -> _state.value.copy(error = result.message, isLoading = false)
                is Resource.Loading -> _state.value.copy(isLoading = true)
            }
        }
    }

    fun updateEmail(newEmail: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, successMessage = null)
            _state.value = when (val result = userRepository.updateEmail(newEmail)) {
                is Resource.Success -> _state.value.copy(user = result.data, isLoading = false, successMessage = "Email actualizado")
                is Resource.Error -> _state.value.copy(error = result.message, isLoading = false)
                is Resource.Loading -> _state.value.copy(isLoading = true)
            }
        }
    }

    fun changePassword(current: String, new: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, successMessage = null)
            _state.value = when (val result = userRepository.changePassword(current, new)) {
                is Resource.Success -> _state.value.copy(isLoading = false, successMessage = "Contraseña actualizada")
                is Resource.Error -> _state.value.copy(error = result.message, isLoading = false)
                is Resource.Loading -> _state.value.copy(isLoading = true)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.value = _state.value.copy(isLoggedOut = true)
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(error = null, successMessage = null)
    }
}
