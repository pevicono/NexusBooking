package com.example.nexusbooking.mobile.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexusbooking.mobile.data.local.TokenDataStore
import com.example.nexusbooking.mobile.data.repository.UserRepository
import com.example.nexusbooking.mobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashUiState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean? = null,
    val error: String? = null
)

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SplashUiState())
    val state: StateFlow<SplashUiState> = _state

    init {
        validateSession()
    }

    private fun validateSession() {
        viewModelScope.launch {
            try {
                val token = tokenDataStore.token.first()

                if (token != null) {
                    when (val result = userRepository.getCurrentUser()) {
                        is Resource.Success -> {
                            _state.value = SplashUiState(isLoading = false, isLoggedIn = true)
                        }
                        is Resource.Error -> {
                            tokenDataStore.clearToken()
                            _state.value = SplashUiState(isLoading = false, isLoggedIn = false)
                        }
                        is Resource.Loading -> {}
                    }
                } else {
                    _state.value = SplashUiState(isLoading = false, isLoggedIn = false)
                }
            } catch (e: Exception) {
                _state.value = SplashUiState(isLoading = false, isLoggedIn = false, error = e.message)
            }
        }
    }
}
