package com.example.nexusbooking.mobile.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexusbooking.mobile.data.remote.dto.BookingRequest
import com.example.nexusbooking.mobile.data.remote.dto.BookingResponse
import com.example.nexusbooking.mobile.data.remote.dto.FacilityResponse
import com.example.nexusbooking.mobile.data.remote.dto.GroupResponse
import com.example.nexusbooking.mobile.data.remote.dto.UserResponse
import com.example.nexusbooking.mobile.data.repository.HomeRepository
import com.example.nexusbooking.mobile.data.repository.UserRepository
import com.example.nexusbooking.mobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val user: UserResponse? = null,
    val facilities: List<FacilityResponse> = emptyList(),
    val bookings: List<BookingResponse> = emptyList(),
    val groups: List<GroupResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    init {
        refreshAll()
    }

    fun refreshAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val user = when (val result = userRepository.getCurrentUser()) {
                is Resource.Success -> result.data
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                    return@launch
                }
                is Resource.Loading -> null
            }

            val facilities = when (val result = homeRepository.facilities()) {
                is Resource.Success -> result.data
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                    return@launch
                }
                is Resource.Loading -> emptyList()
            }

            val bookings = when (val result = homeRepository.myBookings()) {
                is Resource.Success -> result.data
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                    return@launch
                }
                is Resource.Loading -> emptyList()
            }

            val groups = when (val result = homeRepository.groups()) {
                is Resource.Success -> result.data
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                    return@launch
                }
                is Resource.Loading -> emptyList()
            }

            _state.value = _state.value.copy(
                user = user,
                facilities = facilities,
                bookings = bookings,
                groups = groups,
                isLoading = false
            )
        }
    }

    fun createBooking(facilityId: Long, groupId: Long, start: String, end: String, notes: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, success = null)
            when (val result = homeRepository.createBooking(BookingRequest(facilityId, groupId, start, end, notes))) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false, success = "Reserva creada")
                    refreshAll()
                }
                is Resource.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
            }
        }
    }

    fun createGroup(name: String, description: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, success = null)
            when (val result = homeRepository.createGroup(name, description)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false, success = "Grup creat")
                    refreshAll()
                }
                is Resource.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
            }
        }
    }

    fun joinGroup(groupId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, success = null)
            when (val result = homeRepository.joinGroup(groupId)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false, success = "Has entrat al grup")
                    refreshAll()
                }
                is Resource.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
            }
        }
    }

    fun joinGroupByCode(code: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, success = null)
            when (val result = homeRepository.joinGroupByCode(code)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false, success = "Has entrat al grup")
                    refreshAll()
                }
                is Resource.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
            }
        }
    }

    fun leaveGroup(groupId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, success = null)
            when (val result = homeRepository.leaveGroup(groupId)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false, success = "Has sortit del grup")
                    refreshAll()
                }
                is Resource.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(error = null, success = null)
    }
}
