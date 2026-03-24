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
        viewModelScope.launch {
            _registerState.value = LoginUiState(isLoading = true)
            _registerState.value = when (val result = authRepository.register(email, password)) {
                is Resource.Success -> LoginUiState(success = true)
                is Resource.Error -> LoginUiState(error = result.message)
                is Resource.Loading -> LoginUiState(isLoading = true)
            }
        }
    }

    fun clearLoginError() { _loginState.value = LoginUiState() }
    fun clearRegisterError() { _registerState.value = LoginUiState() }
}
