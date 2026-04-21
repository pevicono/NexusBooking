package com.example.nexusbooking.mobile.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexusbooking.mobile.data.remote.dto.UserResponse
import com.example.nexusbooking.mobile.data.repository.AdminRepository
import com.example.nexusbooking.mobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUiState(
    val users: List<UserResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminUiState())
    val state: StateFlow<AdminUiState> = _state

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = adminRepository.users()) {
                is Resource.Success -> _state.value = _state.value.copy(users = result.data, isLoading = false)
                is Resource.Error -> _state.value = _state.value.copy(error = result.message, isLoading = false)
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
            }
        }
    }

    fun setUserActive(userId: Long, active: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, success = null)
            when (val result = adminRepository.setUserActive(userId, active)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false, success = "Usuari actualitzat")
                    refresh()
                }
                is Resource.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
            }
        }
    }

    fun createFacility(name: String, type: String, capacity: Int?, location: String, description: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, success = null)
            when (val result = adminRepository.createFacility(name, type, capacity, location, description)) {
                is Resource.Success -> _state.value = _state.value.copy(isLoading = false, success = "Instal.lacio creada")
                is Resource.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
            }
        }
    }

    fun createIncident(facilityId: Long?, title: String, description: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, success = null)
            when (val result = adminRepository.createIncident(facilityId, title, description)) {
                is Resource.Success -> _state.value = _state.value.copy(isLoading = false, success = "Incidencia creada")
                is Resource.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(error = null, success = null)
    }
}
